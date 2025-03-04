package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Info {
	private String type;
	private String blurredNumber;
	private String holder;
	private String expireMonth;
	private String expireYear;
	private String brand;
	private String issuerAbi;
	private String issuerName;
	private String label;
}
