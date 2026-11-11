package com.ecommerce.product_service.stock;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stock taken out for one caller-supplied reference (order-service uses the order number).
 *
 * <p>The reference makes reserve and release idempotent, so callers can safely retry after a timeout.
 * A release that arrives before (or without) a reservation is stored as an already-released row, so a
 * late or retried reserve for the same reference is refused instead of taking stock nobody will return.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_reservations")
public class StockReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 64)
	private String reference;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "stock_reservation_items", joinColumns = @JoinColumn(name = "reservation_id"))
	private List<ReservedItem> items = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	private Instant releasedAt;

	public boolean isReleased() {
		return releasedAt != null;
	}

	@PrePersist
	void prePersist() {
		createdAt = Instant.now();
	}
}
