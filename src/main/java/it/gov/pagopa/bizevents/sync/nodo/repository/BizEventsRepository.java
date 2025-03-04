package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BizEventsRepository extends CosmosRepository<BizEvent, String> {

    @Query("SELECT c.paymentInfo.paymentToken, c.debtorPosition.noticeNumber FROM c " +
            "WHERE c.paymentInfo.paymentDateTime >= @minDate " +
            "AND c.paymentInfo.paymentDateTime < @maxDate")
    List<BizEvent> getBizEventsFromPaymentDateTime(@Param("minDate") String minDate, @Param("maxDate") String maxDate);


    @Query("SELECT COUNT(1) FROM c " +
            "WHERE c.paymentInfo.paymentDateTime >= @minDate " +
            "AND c.paymentInfo.paymentDateTime < @maxDate")
    long countBizEventsFromPaymentDateTime(@Param("minDate") String minDate, @Param("maxDate") String maxDate);

}
