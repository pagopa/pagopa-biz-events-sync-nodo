package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Details {
	private String blurredNumber;
	private String holder;
	private String circuit; 
}
