package it.gov.pagopa.bizevents.sync.nodo.repository.receipt;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rt;
import it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RtRepository extends JpaRepository<Rt, Long> {

  @Query(
      """
      SELECT DISTINCT new it.gov.pagopa.bizevents.sync.nodo.model.bizevent.ReceiptEventInfo(
          rt.iuv AS iuv,
          rt.ccp AS paymentToken,
          rt.identDominio AS domainId,
          rt.insertedTimestamp AS insertedTimestamp,
          it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion.OLD AS version
      )
      FROM Rt rt
      JOIN Rpt rpt
        ON rpt.identDominio = rt.identDominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp
      WHERE rt.esito = 'ESEGUITO'
        AND rt.dataRicevuta >= :minDate
        AND rt.dataRicevuta < :maxDate
        AND (rpt.flagSeconda = 'N' OR rpt.flagSeconda IS NULL OR rpt.flagSeconda = 'Y')
        AND rt.generataDa = 'PSP'
      """)
  List<ReceiptEventInfo> readReceiptsInTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT rt
      FROM Rt rt
      WHERE rt.identDominio >= :domainId
        AND rt.iuv = :iuv
        AND rt.ccp = :ccp
      """)
  Optional<Rt> readByUniqueIdentifier(String domainId, String iuv, String ccp);

  @Query(
      """
      SELECT COUNT(rt)
      FROM Rt rt
      JOIN Rpt rpt
        ON rpt.identDominio = rt.identDominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp
      WHERE rt.esito = 'ESEGUITO'
        AND rt.dataRicevuta >= :minDate
        AND rt.dataRicevuta < :maxDate
        AND (rpt.flagSeconda = 'N' OR rpt.flagSeconda IS NULL)
        AND rt.generataDa = 'PSP'
      """)
  long countFirstRPTsByTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);

  @Query(
      """
      SELECT COUNT(rt)
      FROM Rt rt
      JOIN Rpt rpt
        ON rpt.identDominio = rt.identDominio AND rpt.iuv = rt.iuv AND rpt.ccp = rt.ccp
      WHERE rt.esito = 'ESEGUITO'
        AND rt.dataRicevuta >= :minDate
        AND rt.dataRicevuta < :maxDate
        AND rpt.flagSeconda = 'Y'
        AND rt.generataDa = 'PSP'
      """)
  long countRetriedRPTsByTimeSlot(
      @Param("minDate") LocalDateTime minDate, @Param("maxDate") LocalDateTime maxDate);
}
