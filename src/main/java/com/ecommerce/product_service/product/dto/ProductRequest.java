package com.ecommerce.product_service.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
		@NotBlank(message = "Name is required") String name,
		String description,
		@NotBlank(message = "SKU is required") String sku,
		@NotNull(message = "Price is required") @DecimalMin(value = "0.0", message = "Price must not be negative") BigDecimal price,
		@NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQuantity,
		String category,
		Boolean active) {
}
