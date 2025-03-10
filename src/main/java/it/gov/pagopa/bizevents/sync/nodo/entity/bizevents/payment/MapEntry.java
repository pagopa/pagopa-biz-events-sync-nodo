package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapEntry implements Serializable {

  private String key;

  private String value;
}
