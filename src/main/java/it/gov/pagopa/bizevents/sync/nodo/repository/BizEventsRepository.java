package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BizEventsRepository extends CosmosRepository<BizEvent, String> {

  @Query(
      """
      SELECT e
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
      """)
  List<BizEvent> getBizEventsFromPaymentDateTime(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT e.paymentInfo.paymentToken
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
      """)
  List<String> getBizEventsPaymentTokenFromPaymentDateTime(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT VALUE COUNT(e)
      FROM e
      WHERE e.paymentInfo.paymentDateTime >= @minDate
        AND e.paymentInfo.paymentDateTime < @maxDate
      """)
  List<Long> countBizEventsWithPaymentDateTimeInTimeSlot(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);
}
