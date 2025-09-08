package it.gov.pagopa.bizevents.sync.nodo.repository.historic.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.Rpt;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricRptRepository extends JpaRepository<Rpt, Long> {

  @Query(
      """
      SELECT rpt
      FROM Rpt rpt
      WHERE (rpt.insertedTimestamp >= :minDate AND rpt.insertedTimestamp < :maxDate)
        AND rpt.identDominio = :domainId
        AND rpt.iuv = :iuv
        AND rpt.ccp = :ccp
      """)
  Optional<Rpt> readByUniqueIdentifier(
      @Param("minDate") LocalDate minDate,
      @Param("maxDate") LocalDate maxDate,
      @Param("domainId") String domainId,
      @Param("iuv") String iuv,
      @Param("ccp") String ccp);
}
