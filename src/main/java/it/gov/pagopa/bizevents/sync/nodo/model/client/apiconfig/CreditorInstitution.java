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
public class CreditorInstitution implements Serializable {

  @JsonProperty(value = "creditor_institution_code")
  private String creditorInstitutionCode;

  @JsonProperty(value = "enabled")
  private Boolean enabled;

  @JsonProperty(value = "business_name")
  private String businessName;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "address")
  private CreditorInstitutionAddress address;

  @JsonProperty(value = "psp_payment")
  private Boolean pspPayment;

  @JsonProperty(value = "reporting_ftp")
  private Boolean reportingFtp;

  @JsonProperty(value = "reporting_zip")
  private Boolean reportingZip;
}
