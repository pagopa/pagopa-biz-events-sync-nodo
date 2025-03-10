package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BizEventsRepository extends CosmosRepository<BizEvent, String> {

  @Query(
      "SELECT * "
          + "FROM e "
          + "WHERE e.paymentInfo.paymentDateTime >= @minDate "
          + "  AND e.paymentInfo.paymentDateTime < @maxDate")
  List<BizEvent> getBizEventsFromPaymentDateTime(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);

  @Query(
      "SELECT e.paymentInfo.paymentToken "
          + "FROM e "
          + "WHERE e.paymentInfo.paymentDateTime >= @minDate "
          + "  AND e.paymentInfo.paymentDateTime < @maxDate")
  List<String> getBizEventsPaymentTokenFromPaymentDateTime(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);

  @Query(
      "SELECT COUNT(1) "
          + "FROM e "
          + "WHERE e.paymentInfo.paymentDateTime >= @minDate "
          + "  AND e.paymentInfo.paymentDateTime < @maxDate")
  long countBizEventsFromPaymentDateTime(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);
}
