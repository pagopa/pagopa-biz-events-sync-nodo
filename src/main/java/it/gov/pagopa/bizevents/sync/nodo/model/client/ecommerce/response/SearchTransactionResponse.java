package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import java.util.List;
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
public class SearchTransactionResponse {

  private List<TransactionResponse> transactions;

  private PageInfo pageInfo;
}
