package it.gov.pagopa.bizevents.sync.nodo.repository.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionTransfer;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionTransferRepository extends JpaRepository<PositionTransfer, Long> {

  @Query(
      """
      SELECT pt
      FROM PositionTransfer pt
      WHERE pt.fkPositionPayment = :positionPaymentId
      """)
  Set<PositionTransfer> readByPositionPayment(@Param("positionPaymentId") String positionPaymentId);
}
