package it.gov.pagopa.bizevents.sync.nodo.repository;

import com.azure.spring.data.cosmos.repository.Query;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RT;
import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface NodoReceiptOldModelRepository extends JpaRepository<RT, Long> {

  @Query(
      "SELECT DISTINCT new NodoReceiptInfo(rpt.CCP, rpt.IUV, NodoReceiptInfoVersion.OLD) "
          + "FROM NODO_ONLINE.RT rt "
          + "JOIN NODO_ONLINE.RPT rpt "
          + "  ON rpt.ident_dominio = rt.ident_dominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp "
          + "WHERE rt.ESITO = 'ESEGUITO' "
          + "  AND rt.DATA_RICEVUTA >= TO_DATE(@minDate, 'yyyy-mm-dd hh24:mi:ss') "
          + "  AND rt.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss') "
          + "  AND (rpt.FLAG_SECONDA = 'N' OR rpt.FLAG_SECONDA IS NULL or rpt.FLAG_SECONDA = 'Y') "
          + "  AND rt.GENERATA_DA = 'PSP' "
          + "  AND pr.PAYMENT_TOKEN NOT IN @paymentTokenList ")
  List<NodoReceiptInfo> getReceiptFromReceiptDateAndNotInPaymentTokenList(
      @Param("minDate") String minDate,
      @Param("maxDate") String maxDate,
      @Param("paymentTokenList") List<String> paymentTokenList);

  @Query(
      "WITH vecchio AS "
          + "( "
          + "  SELECT 'Con eccedenza' AS tipo, COUNT(*) AS numero "
          + "  FROM NODO_ONLINE.RT rt "
          + "  JOIN NODO_ONLINE.RPT rpt "
          + "    ON rpt.ident_dominio = rt.ident_dominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp "
          + "  WHERE rt.ESITO = 'ESEGUITO' "
          + "    AND rt.DATA_RICEVUTA >= TO_DATE(@maxDate, 'yyyy-mm-dd hh24:mi:ss') "
          + "    AND rt.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss') "
          + "    AND (rpt.FLAG_SECONDA = 'N' OR rpt.FLAG_SECONDA IS NULL) "
          + "    AND rt.GENERATA_DA = 'PSP' "
          + "  UNION "
          + "  SELECT 'Da togliere' AS tipo, COUNT(*) AS numero "
          + "  FROM NODO_ONLINE.RT rt "
          + "  JOIN NODO_ONLINE.RPT rpt "
          + "    ON rpt.ident_dominio = rt.ident_dominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp "
          + "  WHERE rt.ESITO = 'ESEGUITO' "
          + "    AND rt.DATA_RICEVUTA >= TO_DATE(@maxDate, 'yyyy-mm-dd hh24:mi:ss') "
          + "    AND rt.DATA_RICEVUTA < TO_DATE(@maxDateReceipt, 'yyyy-mm-dd hh24:mi:ss') "
          + "    AND rpt.FLAG_SECONDA = 'Y' "
          + "    AND rt.GENERATA_DA = 'PSP' "
          + ") "
          + "SELECT a.numero - b.numero "
          + "FROM vecchio a, vecchio b "
          + "WHERE a.tipo = 'Con eccedenza' "
          + "  AND b.tipo = 'Da togliere'")
  long countReceiptFromReceiptDate(
      @Param("minDate") String minDate, @Param("maxDate") String maxDate);
}
