package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import java.util.List;

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
public class Transfer {
	@Builder.Default
	private String idTransfer = "0";
	private String fiscalCodePA;
	private String companyName;
	private String amount;
	private String transferCategory;
	private String remittanceInformation;
	@JsonProperty(value="IBAN")
	private String iban;
	@JsonProperty(value="MBD")
	private MBD mbd;
	private List<MapEntry> metadata;
}
