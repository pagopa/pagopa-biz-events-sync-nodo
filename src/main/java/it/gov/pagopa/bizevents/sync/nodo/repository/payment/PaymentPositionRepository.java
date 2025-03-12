package it.gov.pagopa.bizevents.sync.nodo.repository.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentPositionRepository extends JpaRepository<PositionPayment, Long> {

  @Query(
      """
      SELECT pp
      FROM PositionPayment pp
      WHERE pp.insertedTimestamp >= :minDate
        AND pp.insertedTimestamp < :maxDate
        AND pp.paymentToken = :paymentToken
      """)
  Optional<PositionPayment> readByPaymentTokenInTimeSlot(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("paymentToken") String paymentToken);

  @Query(
      """
      SELECT COUNT(pp)
      FROM PositionPayment pp
      WHERE pp.transactionId >= :transactionId
      """)
  Long countPositionPaymentsByTransactionId(@Param("transactionId") String transactionId);
}
