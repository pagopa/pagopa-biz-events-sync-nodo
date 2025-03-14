package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RptSoggettiId implements Serializable {

  private Long rptId;

  private String tipoSoggetto;
}
