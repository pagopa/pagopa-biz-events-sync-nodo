package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BizEventsRepository extends CosmosRepository<BizEvent, String> {

  @Query(
      """
SELECT
  c.debtorPosition.noticeNumber != null ? c.debtorPosition.noticeNumber : c.debtorPosition.iuv as iuv,
  c.paymentInfo.paymentToken as paymentToken,
  c.creditor.idPA as domainId,
  StringToNumber(c.debtorPosition.modelType) = 1 ? "OLD" : "NEW" as version
FROM c
WHERE c.paymentInfo.paymentDateTime >= @minDate
  AND c.paymentInfo.paymentDateTime < @maxDate
""")
  Set<Map<String, Object>> readBizEventsInTimeSlot(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);

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
