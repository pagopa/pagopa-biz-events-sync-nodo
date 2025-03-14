package it.gov.pagopa.bizevents.sync.nodo.repository.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionTransferRepository extends JpaRepository<PositionTransfer, Long> {

  @Query(
      """
      SELECT pt
      FROM PositionTransfer pt
      WHERE pt.fkPositionPayment = :positionPaymentId
        AND pt.insertedTimestamp >= :minDate
      ORDER BY pt.transferIdentifier ASC
      """)
  List<PositionTransfer> readByPositionPayment(
      @Param("positionPaymentId") Long positionPaymentId, @Param("minDate") LocalDateTime minDate);
}
