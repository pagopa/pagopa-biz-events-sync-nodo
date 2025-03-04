package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionPsp {
	private String idChannel;
	private String businessName;
	private String serviceName;
}
