package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel.PositionReceipt;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodoReceiptOldModelRepository extends JpaRepository<RT, Long> {

    @Query("WITH vecchio AS (  " +
            "SELECT DISTINCT r.CCP, r.IUV  " +
            "FROM NODO_ONLINE.RT t  " +
            "JOIN NODO_ONLINE.RPT r   " +
            "ON r.ident_dominio = t.ident_dominio   " +
            "AND r.iuv = t.iuv   " +
            "AND r.ccp = t.ccp  " +
            "WHERE t.ESITO = 'ESEGUITO'  " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@minDate, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.INSERTED_TIMESTAMP >= TO_DATE(@minDate, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.INSERTED_TIMESTAMP < TO_DATE(@minDateInsertion, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND (r.FLAG_SECONDA = 'N' OR r.FLAG_SECONDA IS NULL)  " +
            "AND t.GENERATA_DA = 'PSP'  " +
            "UNION  " +
            "SELECT DISTINCT r.CCP, r.IUV  " +
            "FROM NODO_ONLINE.RT t  " +
            "JOIN NODO_ONLINE.RPT r   " +
            "ON r.ident_dominio = t.ident_dominio   " +
            "AND r.iuv = t.iuv   " +
            "AND r.ccp = t.ccp  " +
            "WHERE t.ESITO = 'ESEGUITO'  " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@maxDate, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.INSERTED_TIMESTAMP >= TO_DATE(@maxDate, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND t.INSERTED_TIMESTAMP < TO_DATE(@minDateInsertion, 'yyyy-mm-dd hh24:mi:ss')  " +
            "AND r.FLAG_SECONDA = 'Y'  " +
            "AND t.GENERATA_DA = 'PSP'  " +
            ")  " +
            "SELECT CCP, IUV FROM vecchio")
    List<PositionReceipt> getReceiptFromReceiptDate(
            @Param("minDate") String minDate,
            @Param("maxDateReceipt") String maxDateReceipt,
            @Param("maxDateInsertion") String maxDateInsertion
    );

    @Query("with vecchio as ( " +
            "select 'Con eccedenza' as tipo, COUNT(*) as numero " +
            "from NODO_ONLINE.RT t " +
            "join NODO_ONLINE.RPT r on r.ident_dominio=t.ident_dominio and r.iuv=t.iuv and r.ccp=t.ccp " +
            "where t.ESITO='ESEGUITO' " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.INSERTED_TIMESTAMP >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.INSERTED_TIMESTAMP < TO_DATE(@minDateInsertion,'yyyy-mm-dd hh24:mi:ss') " +
            "AND (r.FLAG_SECONDA = 'N' OR r.FLAG_SECONDA IS NULL) " +
            "AND t.GENERATA_DA = 'PSP' " +
            "UNION " +
            "select 'Da togliere' as tipo, COUNT(*) as numero " +
            "from NODO_ONLINE.RT t " +
            "join NODO_ONLINE.RPT r on r.ident_dominio=t.ident_dominio and r.iuv=t.iuv and r.ccp=t.ccp " +
            "where t.ESITO='ESEGUITO' " +
            "AND t.DATA_RICEVUTA >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.DATA_RICEVUTA < TO_DATE(@maxDateReceipt,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.INSERTED_TIMESTAMP >= TO_DATE(@maxDate,'yyyy-mm-dd hh24:mi:ss') " +
            "AND t.INSERTED_TIMESTAMP < TO_DATE(@minDateInsertion,'yyyy-mm-dd hh24:mi:ss') " +
            "AND r.FLAG_SECONDA = 'Y' " +
            "AND t.GENERATA_DA = 'PSP') " +
            "select a.numero - b.numero " +
            "from vecchio a, vecchio b " +
            "where a.tipo = 'Con eccedenza' " +
            "and b.tipo = 'Da togliere'")
    long countReceiptFromReceiptDate(
            @Param("minDate") String minDate,
            @Param("maxDateReceipt") String maxDateReceipt,
            @Param("maxDateInsertion") String maxDateInsertion
    );
}