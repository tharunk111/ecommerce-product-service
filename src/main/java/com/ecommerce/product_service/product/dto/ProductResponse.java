package com.ecommerce.product_service.product.dto;

import com.ecommerce.product_service.product.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
		UUID id,
		String name,
		String description,
		String sku,
		BigDecimal price,
		Integer stockQuantity,
		String category,
		boolean active,
		Instant createdAt,
		Instant updatedAt) {

	public static ProductResponse from(Product product) {
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getSku(),
				product.getPrice(),
				product.getStockQuantity(),
				product.getCategory(),
				product.isActive(),
				product.getCreatedAt(),
				product.getUpdatedAt());
	}
}
