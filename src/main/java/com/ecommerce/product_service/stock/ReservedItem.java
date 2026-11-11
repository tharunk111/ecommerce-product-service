package com.ecommerce.product_service.stock;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReservedItem {

	@Column(nullable = false)
	private UUID productId;

	@Column(nullable = false)
	private Integer quantity;
}
