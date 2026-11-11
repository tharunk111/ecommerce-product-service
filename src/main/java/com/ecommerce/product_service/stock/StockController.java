package com.ecommerce.product_service.stock;

import com.ecommerce.product_service.stock.dto.StockReservationRequest;
import com.ecommerce.product_service.stock.dto.StockReservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reserving and releasing stock for orders. Both operations are idempotent per reference.
 */
@RestController
@RequestMapping("/api/v1/products/stock/reservations")
public class StockController {

	private final StockService stockService;

	public StockController(StockService stockService) {
		this.stockService = stockService;
	}

	@PostMapping
	public StockReservationResponse reserve(@Valid @RequestBody StockReservationRequest request) {
		return stockService.reserve(request);
	}

	@GetMapping("/{reference}")
	public StockReservationResponse getReservation(@PathVariable String reference) {
		return stockService.getReservation(reference);
	}

	@PostMapping("/{reference}/release")
	public ResponseEntity<Void> release(@PathVariable String reference) {
		stockService.release(reference);
		return ResponseEntity.noContent().build();
	}
}
