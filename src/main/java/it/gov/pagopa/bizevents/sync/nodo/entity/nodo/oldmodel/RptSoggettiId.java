package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RptSoggettiId implements Serializable {

  @Column(name = "RPT_ID")
  private Long rptId;

  @Column(name = "TIPO_SOGGETTO")
  private String tipoSoggetto;
}
