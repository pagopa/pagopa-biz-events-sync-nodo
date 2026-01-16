package it.gov.pagopa.bizevents.sync.nodo.repository.historic.receipt;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricPositionReceiptRepository extends JpaRepository<PositionReceipt, Long> {

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo(
          pr.noticeId AS iuv,
          pr.paymentToken AS paymentToken,
          pr.paFiscalCode AS domainId,
          pr.insertedTimestamp AS insertedTimestamp,
          "NEW" AS version
      )
      FROM PositionReceipt pr
      WHERE (pr.insertedTimestamp >= :minDate AND pr.insertedTimestamp < :maxDate)
          AND (pr.paymentDateTime >= :minDateTime AND pr.paymentDateTime < :maxDateTime)
      """)
  Set<ReceiptEventInfo> readReceiptsInTimeSlot(
            @Param("minDate") LocalDateTime minDate,
            @Param("maxDate") LocalDateTime maxDate,
            @Param("minDateTime") LocalDateTime minDateTime,
            @Param("maxDateTime") LocalDateTime maxDateTime);

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo(
          pr.noticeId AS iuv,
          pr.paymentToken AS paymentToken,
          pr.paFiscalCode AS domainId,
          pr.insertedTimestamp AS insertedTimestamp,
          "NEW" AS version
      )
      FROM PositionReceipt pr
      WHERE pr.paymentDateTime >= :minDate
        AND pr.paymentDateTime < :maxDate
        AND pr.paFiscalCode = :domainId
        AND pr.noticeId = :noticeNumber
      """)
  Set<ReceiptEventInfo> readReceiptsByDomainAndNoticeNumberInTimeSlot(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("domainId") String domainId,
      @Param("noticeNumber") String noticeNumber);



  @Query(
      """
      SELECT COUNT(pr)
      FROM PositionReceipt pr
      WHERE (pr.insertedTimestamp >= :minDate AND pr.insertedTimestamp < :maxDate)
        AND (pr.paymentDateTime >= :minDateTime AND pr.paymentDateTime < :maxDateTime)
      """)
  long countByTimeSlot(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("minDateTime") LocalDateTime minDateTime,
      @Param("maxDateTime") LocalDateTime maxDateTime);
}
