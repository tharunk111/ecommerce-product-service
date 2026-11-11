package com.ecommerce.product_service.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record StockItemRequest(
		@NotNull(message = "Product id is required") UUID productId,
		@NotNull(message = "Quantity is required") @Positive(message = "Quantity must be positive") Integer quantity) {
}
