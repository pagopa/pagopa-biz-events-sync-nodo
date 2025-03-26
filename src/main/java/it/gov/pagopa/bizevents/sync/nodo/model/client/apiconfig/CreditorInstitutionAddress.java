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
public class CreditorInstitutionAddress implements Serializable {

  @JsonProperty(value = "location")
  private String location;

  @JsonProperty(value = "city")
  private String city;

  @JsonProperty(value = "zip_code")
  private String zipCode;

  @JsonProperty(value = "country_code")
  private String countryCode;

  @JsonProperty(value = "tax_domicile")
  private String taxDomicile;
}
