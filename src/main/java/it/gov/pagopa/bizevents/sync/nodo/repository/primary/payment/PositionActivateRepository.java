package it.gov.pagopa.bizevents.sync.nodo.repository.primary.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.PositionActivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PositionActivateRepository extends JpaRepository<PositionActivate, Long> {

  @Query(
      """
      SELECT pa
      FROM PositionActivate pa
      WHERE (pa.insertedTimestamp >= :minDate AND pa.insertedTimestamp < :maxDate)
        AND pa.paymentToken = :paymentToken
      """)
  Optional<PositionActivate> readByPaymentTokenInTimeSlot(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("paymentToken") String paymentToken);

}