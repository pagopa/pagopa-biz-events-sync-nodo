package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NodoReceiptNewModelRepository extends JpaRepository<PositionReceipt, Long> {

  @Query(
      "SELECT new NodoReceiptInfo(pr.PAYMENT_TOKEN, pr.NOTICE_ID, NodoReceiptInfoVersion.NEW) "
          + "FROM POSITION_RECEIPT pr "
          + "WHERE pr.PAYMENT_DATE_TIME >= TO_DATE(@minDate) "
          + "  AND pr.PAYMENT_DATE_TIME < TO_DATE(@maxDate)"
          + "  AND pr.PAYMENT_TOKEN NOT IN @paymentTokenList")
  List<NodoReceiptInfo> getPositionReceiptFromReceiptDateAndNotInPaymentTokenList(
      @Param("minDate") String minDate,
      @Param("maxDate") String maxDate,
      @Param("paymentTokenList") List<String> paymentTokenList);

  @Query(
      "SELECT COUNT(1) "
          + "FROM NODO_ONLINE.POSITION_RECEIPT pr "
          + "WHERE pr.PAYMENT_DATE_TIME >= TO_DATE(@minDate) "
          + "  pr.PAYMENT_DATE_TIME < TO_DATE(@maxDate)")
  long countPositionReceiptFromReceiptDate(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);
}
