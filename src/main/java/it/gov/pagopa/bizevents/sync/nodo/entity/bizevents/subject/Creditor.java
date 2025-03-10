package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creditor {

  private String idPA;

  private String idBrokerPA;

  private String idStation;

  private String companyName;

  private String officeName;
}
