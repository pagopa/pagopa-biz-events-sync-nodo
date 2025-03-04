package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Payer {
	private String fullName;
	private String entityUniqueIdentifierType;
	private String entityUniqueIdentifierValue;
	private String streetName;
	private String civicNumber;
	private String postalCode;
	private String city;
	private String stateProvinceRegion;
	private String country;
	@JsonProperty(value="eMail")
	private String eMail;
}
