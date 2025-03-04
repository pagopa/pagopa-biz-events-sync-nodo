package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodoReceiptNewModelRepository extends JpaRepository<PositionReceipt, Long> {

    @Query("SELECT  " +
            "FROM  NODO_ONLINE.POSITION_RECEIPT pr " +
            "WHERE pr.INSERTED_TIMESTAMP >= TO_DATE(@minDate) " +
            "AND pr.INSERTED_TIMESTAMP < TO_DATE(@maxDateInsertion) " +
            "AND pr.PAYMENT_DATE_TIME >= TO_DATE(@minDate) " +
            "AND pr.PAYMENT_DATE_TIME < TO_DATE(@maxDateReceipt)")
    List<PositionReceipt> getPositionReceiptFromReceiptDate(
            @Param("minDate") String minDate,
            @Param("maxDateReceipt") String maxDateReceipt,
            @Param("maxDateInsertion") String maxDateInsertion
    );

    @Query("SELECT COUNT(1) FROM NODO_ONLINE.POSITION_RECEIPT pr " +
            "WHERE pr.INSERTED_TIMESTAMP >= TO_DATE(@minDate) " +
            "AND pr.INSERTED_TIMESTAMP < TO_DATE(@maxDateInsertion) " +
            "AND pr.PAYMENT_DATE_TIME >= TO_DATE(@minDate) " +
            "AND pr.PAYMENT_DATE_TIME < TO_DATE(@maxDateReceipt)")
    long countPositionReceiptFromReceiptDate(
            @Param("minDate") String minDate,
            @Param("maxDateReceipt") String maxDateReceipt,
            @Param("maxDateInsertion") String maxDateInsertion
    );
}
