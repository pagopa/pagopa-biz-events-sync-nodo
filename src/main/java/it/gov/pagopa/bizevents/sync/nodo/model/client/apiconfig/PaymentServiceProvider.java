package it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PaymentServiceProvider implements Serializable {

  @JsonProperty(value = "psp_code")
  private String pspCode;

  @JsonProperty(value = "enabled")
  private Boolean enabled;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "business_name")
  private String businessName;

  @JsonProperty(value = "abi")
  private String abi;

  @JsonProperty(value = "bic")
  private String bic;

  @JsonProperty(value = "my_bank_code")
  private String myBankCode;

  @JsonProperty(value = "digital_stamp")
  private Boolean digitalStamp;

  @JsonProperty(value = "agid_psp")
  private Boolean agidPsp;

  @JsonProperty(value = "tax_code")
  private String taxCode;

  @JsonProperty(value = "vat_number")
  private String vatNumber;
}
