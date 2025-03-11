package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.ReceiptEventInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BizEventsRepository extends CosmosRepository<BizEvent, String> {

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.ReceiptEventInfo(
          e.debtorPosition.noticeNumber AS iuv,
          e.paymentInfo.paymentToken AS paymentToken,
          e.creditor.idPA AS domainId,
          e.timestamp AS insertedTimestamp,
          it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion.OLD AS version
      )
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
        AND e.debtorPosition.modelType == '1'
      """)
  Set<ReceiptEventInfo> readBizEventsOfOldModelInTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT new it.gov.pagopa.bizevents.sync.nodo.model.ReceiptEventInfo(
          e.debtorPosition.noticeNumber AS iuv,
          e.paymentInfo.paymentToken AS paymentToken,
          e.creditor.idPA AS domainId,
          e.timestamp AS insertedTimestamp,
          it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion.OLD AS version
      )
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
        AND e.debtorPosition.modelType == '2'
      """)
  Set<ReceiptEventInfo> readBizEventsOfNewModelInTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT VALUE COUNT(e)
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
      """)
  List<Long> countBizEventsInTimeSlot(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);
}
