package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.request;

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
public class SearchTransactionRequest {

  private String type;

  private String paymentToken;
}
