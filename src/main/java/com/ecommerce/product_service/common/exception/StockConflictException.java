package com.ecommerce.product_service.common.exception;

/**
 * Stock cannot be reserved: not enough units, product inactive, or the reservation was already released (409).
 */
public class StockConflictException extends RuntimeException {

	public StockConflictException(String message) {
		super(message);
	}
}
