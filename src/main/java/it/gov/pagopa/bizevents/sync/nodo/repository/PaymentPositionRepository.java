package it.gov.pagopa.bizevents.sync.nodo.repository;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionPayment;
import java.time.LocalDateTime;
import java.util.Set;
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
  Set<PositionPayment> readByPaymentToken(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("paymentToken") String paymentToken);
}
