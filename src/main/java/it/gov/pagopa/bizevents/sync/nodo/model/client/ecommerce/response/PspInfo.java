package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import java.io.Serializable;
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
public class PspInfo implements Serializable {

  private String pspId;

  private String businessName;

  private String idChannel;
}
