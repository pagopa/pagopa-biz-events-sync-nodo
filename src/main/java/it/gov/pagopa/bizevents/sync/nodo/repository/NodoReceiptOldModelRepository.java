package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RT;
import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodoReceiptOldModelRepository extends JpaRepository<RT, Long> {

    @Query("SELECT DISTINCT new NodoReceiptInfo(r.CCP, r.IUV, NodoReceiptInfoVersion.OLD) " +
            "FROM NODO_ONLINE.RT t  " +
            "JOIN NODO_ONLINE.RPT r   " +
            "ON r.ident_dominio = t.ident_dominio   " +
            "AND r.iuv = t.iuv   " +
            "AND r.ccp = t.ccp  " +
            "WHERE t.ESITO = 'ESEGUITO'  " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@minDate, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND (r.FLAG_SECONDA = 'N' OR r.FLAG_SECONDA IS NULL or r.FLAG_SECONDA = 'Y')  " +
            "AND t.GENERATA_DA = 'PSP'  " +
            "AND pr.PAYMENT_TOKEN NOT IN @paymentTokenList ")
    List<NodoReceiptInfo> getReceiptFromReceiptDateAndNotInPaymentTokenList(
            @Param("minDate") String minDate,
            @Param("maxDate") String maxDate,
            @Param("paymentTokenList") List<String> paymentTokenList
    );

    @Query("with vecchio as ( " +
            "select 'Con eccedenza' as tipo, COUNT(*) as numero " +
            "from NODO_ONLINE.RT t " +
            "join NODO_ONLINE.RPT r on r.ident_dominio=t.ident_dominio and r.iuv=t.iuv and r.ccp=t.ccp " +
            "where t.ESITO='ESEGUITO' " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt,'yyyy-mm-dd hh24:mi:ss') " +
            "AND (r.FLAG_SECONDA = 'N' OR r.FLAG_SECONDA IS NULL) " +
            "AND t.GENERATA_DA = 'PSP' " +
            "UNION " +
            "select 'Da togliere' as tipo, COUNT(*) as numero " +
            "from NODO_ONLINE.RT t " +
            "join NODO_ONLINE.RPT r on r.ident_dominio=t.ident_dominio and r.iuv=t.iuv and r.ccp=t.ccp " +
            "where t.ESITO='ESEGUITO' " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt,'yyyy-mm-dd hh24:mi:ss') " +
            "AND r.FLAG_SECONDA = 'Y' " +
            "AND t.GENERATA_DA = 'PSP') " +
            "select a.numero - b.numero " +
            "from vecchio a, vecchio b " +
            "where a.tipo = 'Con eccedenza' " +
            "and b.tipo = 'Da togliere'")
    long countReceiptFromReceiptDate(
            @Param("minDate") String minDate,
            @Param("maxDate") String maxDate
    );
}