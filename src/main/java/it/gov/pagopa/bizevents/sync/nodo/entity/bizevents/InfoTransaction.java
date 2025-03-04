package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
}
