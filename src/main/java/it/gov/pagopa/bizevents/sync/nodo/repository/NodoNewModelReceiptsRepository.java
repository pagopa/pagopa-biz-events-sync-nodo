package it.gov.pagopa.bizevents.sync.nodo.repository;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodoNewModelReceiptsRepository extends JpaRepository<PositionReceipt, Long> {

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo(
          pr.noticeId AS iuv,
          pr.paymentToken AS paymentToken,
          it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion.NEW AS version
      )
      FROM PositionReceipt pr
      WHERE pr.paymentDateTime >= :minDate
        AND pr.paymentDateTime < :maxDate
        AND pr.paymentToken NOT IN (:paymentTokenList)
      """)
  List<NodoReceiptInfo> readExcludedPaymentTokensInTimeSlot(
      @Param("minDate") LocalDateTime minDate,
      @Param("maxDate") LocalDateTime maxDate,
      @Param("paymentTokenList") List<String> paymentTokenList);

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
