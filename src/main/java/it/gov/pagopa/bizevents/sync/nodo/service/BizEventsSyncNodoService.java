package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.model.NodoReceiptInfo;

import java.util.List;

public interface BizEventsSyncNodoService {

    long checkBizEventsDiffAtDate(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDateReceipt);

    List<NodoReceiptInfo>  retrieveNotElaboratedNodoReceipts(String bizEventMinDate, String bizEventMaxDate, String nodoMinDate, String nodoMaxDateReceipt);
}
