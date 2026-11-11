package com.ecommerce.product_service.stock;

import com.ecommerce.product_service.common.exception.ResourceNotFoundException;
import com.ecommerce.product_service.common.exception.StockConflictException;
import com.ecommerce.product_service.product.Product;
import com.ecommerce.product_service.product.ProductRepository;
import com.ecommerce.product_service.stock.dto.StockItemRequest;
import com.ecommerce.product_service.stock.dto.StockReservationRequest;
import com.ecommerce.product_service.stock.dto.StockReservationResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

	private final ProductRepository productRepository;
	private final StockReservationRepository reservationRepository;

	public StockService(ProductRepository productRepository, StockReservationRepository reservationRepository) {
		this.productRepository = productRepository;
		this.reservationRepository = reservationRepository;
	}

	/**
	 * Takes the requested quantities out of stock, all or nothing: if any line fails,
	 * the transaction rolls back and no product's stock changes.
	 */
	@Transactional
	public StockReservationResponse reserve(StockReservationRequest request) {
		Optional<StockReservation> existing = reservationRepository.findByReference(request.reference());
		if (existing.isPresent()) {
			StockReservation reservation = existing.get();
			if (reservation.isReleased()) {
				throw new StockConflictException("Reservation " + request.reference() + " was already released");
			}
			// Retry of a reservation that already succeeded
			return StockReservationResponse.from(reservation);
		}

		StockReservation reservation = new StockReservation();
		reservation.setReference(request.reference());

		// Same product twice becomes one line; sorted so concurrent reservations lock rows in the same order
		Map<UUID, Integer> quantities = new TreeMap<>();
		for (StockItemRequest item : request.items()) {
			quantities.merge(item.productId(), item.quantity(), Integer::sum);
		}

		Instant now = Instant.now();
		quantities.forEach((productId, quantity) -> {
			if (productRepository.decrementStock(productId, quantity, now) == 0) {
				throw reserveFailure(productId, quantity);
			}
			reservation.getItems().add(new ReservedItem(productId, quantity));
		});

		return StockReservationResponse.from(reservationRepository.save(reservation));
	}

	/**
	 * Puts the reserved stock back. Safe to call repeatedly, and for a reference that was never reserved.
	 */
	@Transactional
	public void release(String reference) {
		Optional<StockReservation> existing = reservationRepository.findByReferenceForUpdate(reference);
		Instant now = Instant.now();

		if (existing.isEmpty()) {
			// Remember the release so a reserve arriving late for this reference is refused
			StockReservation tombstone = new StockReservation();
			tombstone.setReference(reference);
			tombstone.setReleasedAt(now);
			reservationRepository.save(tombstone);
			return;
		}

		StockReservation reservation = existing.get();
		if (reservation.isReleased()) {
			return;
		}
		for (ReservedItem item : reservation.getItems()) {
			// A product deleted since the reservation has nothing to give stock back to
			productRepository.incrementStock(item.getProductId(), item.getQuantity(), now);
		}
		reservation.setReleasedAt(now);
	}

	@Transactional(readOnly = true)
	public StockReservationResponse getReservation(String reference) {
		return reservationRepository.findByReference(reference)
				.map(StockReservationResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("Stock reservation not found: " + reference));
	}

	// The conditional update matched no row; find out why so the caller gets a useful error
	private RuntimeException reserveFailure(UUID productId, int quantity) {
		Optional<Product> product = productRepository.findById(productId);
		if (product.isEmpty()) {
			return new ResourceNotFoundException("Product not found with id: " + productId);
		}
		if (!product.get().isActive()) {
			return new StockConflictException("Product " + productId + " is not available");
		}
		return new StockConflictException("Not enough stock for product " + productId
				+ ": requested " + quantity + ", available " + product.get().getStockQuantity());
	}
}
