package it.gov.pagopa.bizevents.sync.nodo.model;

import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NodoReceiptInfo {

    long receiptId;
    String paymentToken;
    NodoReceiptInfoVersion version;
}
