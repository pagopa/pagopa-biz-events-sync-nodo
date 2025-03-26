package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import java.io.Serializable;
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
public class PaymentInfo implements Serializable {

  private String origin;

  private String idTransaction;

  private List<PaymentDetailInfo> details;
}
