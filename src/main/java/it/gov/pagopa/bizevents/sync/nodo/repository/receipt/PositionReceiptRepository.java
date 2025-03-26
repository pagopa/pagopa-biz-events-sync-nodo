package it.gov.pagopa.bizevents.sync.nodo.repository.receipt;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionReceiptRepository extends JpaRepository<PositionReceipt, Long> {

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo(
          pr.noticeId AS iuv,
          pr.paymentToken AS paymentToken,
          pr.paFiscalCode AS domainId,
          pr.insertedTimestamp AS insertedTimestamp,
          it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion.NEW AS version
      )
      FROM PositionReceipt pr
      WHERE pr.paymentDateTime >= :minDate
        AND pr.paymentDateTime < :maxDate
      """)
  Set<ReceiptEventInfo> readReceiptsInTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT COUNT(pr)
      FROM PositionReceipt pr
      WHERE pr.paymentDateTime >= :minDate
        AND pr.paymentDateTime < :maxDate
      """)
  long countByTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);
}
