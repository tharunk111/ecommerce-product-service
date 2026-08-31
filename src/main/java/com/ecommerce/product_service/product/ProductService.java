package com.ecommerce.product_service.product;

import com.ecommerce.product_service.common.exception.DuplicateResourceException;
import com.ecommerce.product_service.common.exception.ResourceNotFoundException;
import com.ecommerce.product_service.product.dto.ProductRequest;
import com.ecommerce.product_service.product.dto.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional
	public ProductResponse createProduct(ProductRequest request) {
		if (productRepository.existsBySkuIgnoreCase(request.sku())) {
			throw new DuplicateResourceException("A product with SKU '" + request.sku() + "' already exists");
		}

		Product product = new Product();
		applyRequest(product, request);

		return ProductResponse.from(productRepository.save(product));
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
				.map(ProductResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductResponse getProductById(UUID id) {
		return ProductResponse.from(findProductOrThrow(id));
	}

	@Transactional
	public ProductResponse updateProduct(UUID id, ProductRequest request) {
		Product product = findProductOrThrow(id);

		if (productRepository.existsBySkuIgnoreCaseAndIdNot(request.sku(), id)) {
			throw new DuplicateResourceException("A product with SKU '" + request.sku() + "' already exists");
		}

		applyRequest(product, request);

		return ProductResponse.from(productRepository.saveAndFlush(product));
	}

	@Transactional
	public void deleteProduct(UUID id) {
		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found with id: " + id);
		}
		productRepository.deleteById(id);
	}

	private Product findProductOrThrow(UUID id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}

	private void applyRequest(Product product, ProductRequest request) {
		product.setName(request.name().trim());
		product.setDescription(blankToNull(request.description()));
		product.setSku(request.sku().trim());
		product.setPrice(request.price());
		product.setStockQuantity(request.stockQuantity());
		product.setCategory(blankToNull(request.category()));
		product.setActive(request.active() == null || request.active());
	}

	private String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
