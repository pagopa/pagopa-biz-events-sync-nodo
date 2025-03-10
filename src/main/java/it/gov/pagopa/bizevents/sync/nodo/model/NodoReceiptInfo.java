package it.gov.pagopa.bizevents.sync.nodo.model;

import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class NodoReceiptInfo {

  private String iuv;

  private String paymentToken;

  private NodoReceiptInfoVersion version;
}
