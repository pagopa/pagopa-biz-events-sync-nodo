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
public class PaymentAuthorizationRequest {

  private String authOutcome;

  private String requestId;

  private String correlationId;

  private String authCode;

  private String paymentMethodType;

  private PaymentAuthorizationRequestDetails details;
}
