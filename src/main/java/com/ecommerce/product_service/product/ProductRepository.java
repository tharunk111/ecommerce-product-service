package com.ecommerce.product_service.product;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

	boolean existsBySkuIgnoreCase(String sku);

	boolean existsBySkuIgnoreCaseAndIdNot(String sku, UUID id);

	/**
	 * Check and decrement in one statement, so two concurrent orders can't both take the last unit.
	 * Returns 0 when the product is missing, inactive, or short on stock.
	 * Bulk updates skip @PreUpdate, hence updatedAt is set here.
	 */
	@Modifying
	@Query("""
			update Product p
			set p.stockQuantity = p.stockQuantity - :quantity, p.updatedAt = :now
			where p.id = :id and p.active = true and p.stockQuantity >= :quantity""")
	int decrementStock(UUID id, int quantity, Instant now);

	@Modifying
	@Query("update Product p set p.stockQuantity = p.stockQuantity + :quantity, p.updatedAt = :now where p.id = :id")
	int incrementStock(UUID id, int quantity, Instant now);
}
