package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoTransaction {

  private String brand;

  private String brandLogo;

  private String clientId;

  private String paymentMethodName;

  private String type;

  private String maskedEmail;
}
