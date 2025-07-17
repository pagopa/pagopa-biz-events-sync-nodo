package it.gov.pagopa.bizevents.sync.nodo.repository.historic.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptSoggetti;
import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptSoggettiId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricRptSoggettiRepository extends JpaRepository<RptSoggetti, RptSoggettiId> {

  @Query(
      """
      SELECT rs
      FROM RptSoggetti rs
      WHERE rs.id.rptId = :rptId
      """)
  List<RptSoggetti> readByRptId(@Param("rptId") Long rptId);
}
