package com.ecommerce.product_service.stock.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * @param reference caller's idempotency key, e.g. the order number. Repeating a request with the same
 *                  reference returns the existing reservation and does not take stock again.
 */
public record StockReservationRequest(
		@NotBlank(message = "Reference is required") @Size(max = 64) String reference,
		@NotEmpty(message = "At least one item is required") @Size(max = 100) List<@NotNull @Valid StockItemRequest> items) {
}
