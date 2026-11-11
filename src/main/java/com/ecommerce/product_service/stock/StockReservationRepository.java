package com.ecommerce.product_service.stock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

	Optional<StockReservation> findByReference(String reference);

	// Row lock so two concurrent releases can't both put the stock back
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select r from StockReservation r where r.reference = :reference")
	Optional<StockReservation> findByReferenceForUpdate(String reference);
}
