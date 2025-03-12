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
public class PspInfo {

  private String pspId;

  private String businessName;

  private String idChannel;
}
