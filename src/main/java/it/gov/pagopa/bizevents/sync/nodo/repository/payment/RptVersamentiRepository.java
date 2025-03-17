package it.gov.pagopa.bizevents.sync.nodo.repository.payment;

import it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.RptVersamenti;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RptVersamentiRepository extends JpaRepository<RptVersamenti, Long> {

  @Query(
      """
      SELECT rptv
      FROM RptVersamenti rptv
      WHERE rptv.fkRpt = :rptId
      ORDER BY rptv.progressivo ASC
      """)
  List<RptVersamenti> readByRptId(@Param("rptId") Long rptId);
}
