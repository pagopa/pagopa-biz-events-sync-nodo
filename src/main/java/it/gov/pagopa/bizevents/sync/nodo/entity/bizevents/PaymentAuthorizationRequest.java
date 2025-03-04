package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import lombok.*;

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
	private Details details;
}
