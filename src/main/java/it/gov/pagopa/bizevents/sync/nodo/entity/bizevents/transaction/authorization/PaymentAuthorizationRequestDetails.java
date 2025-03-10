package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.authorization;

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
public class PaymentAuthorizationRequestDetails {

  private String blurredNumber;

  private String holder;

  private String circuit;
}
