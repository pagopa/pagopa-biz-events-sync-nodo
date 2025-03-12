package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

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
public class PaymentInfo {

  private String subject;

  private String iuv;

  private String rptId;

  private String idTransaction;

  private String paymentToken;

  private String creditorInstitution;

  private Long amount;

  private String paFiscalCode;
}
