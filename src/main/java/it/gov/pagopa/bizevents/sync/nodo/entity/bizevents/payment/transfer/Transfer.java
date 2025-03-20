package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.MapEntry;
import java.util.List;
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

  private String idTransfer;

  private String fiscalCodePA;

  private String companyName;

  private String amount;

  private String transferCategory;

  private String remittanceInformation;

  @JsonProperty(value = "IBAN")
  private String iban;

  @JsonProperty(value = "MBDAttachment")
  private String mbdAttachment;

  private List<MapEntry> metadata;

  @JsonProperty(value = "IUR")
  private String iur;
}
