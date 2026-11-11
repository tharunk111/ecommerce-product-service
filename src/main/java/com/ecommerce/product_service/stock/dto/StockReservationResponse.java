package com.ecommerce.product_service.stock.dto;

import com.ecommerce.product_service.stock.StockReservation;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockReservationResponse(
		String reference,
		List<Item> items,
		Instant createdAt,
		Instant releasedAt) {

	public record Item(UUID productId, Integer quantity) {
	}

	public static StockReservationResponse from(StockReservation reservation) {
		return new StockReservationResponse(
				reservation.getReference(),
				reservation.getItems().stream().map(i -> new Item(i.getProductId(), i.getQuantity())).toList(),
				reservation.getCreatedAt(),
				reservation.getReleasedAt());
	}
}
