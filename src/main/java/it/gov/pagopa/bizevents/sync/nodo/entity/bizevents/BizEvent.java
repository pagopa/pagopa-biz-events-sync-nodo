package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.enumeration.StatusType;
import lombok.*;

import java.util.List;

@Container(containerName = "${azure.cosmos.biz-events-container-name}", autoCreateContainer = false, ru="1000")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BizEvent {
	private String id;
	private String version;
	private String idPaymentManager;
	private String complete;
	private String receiptId;
	private List<String> missingInfo;
	private DebtorPosition debtorPosition;
	private Creditor creditor;
	private Psp psp;
	private Debtor debtor;
	private Payer payer;
	private PaymentInfo paymentInfo;
	private List<Transfer> transferList;
	private TransactionDetails transactionDetails;
	
	// internal management fields
	private StatusType eventStatus;
	private Integer eventRetryEnrichmentCount;
}
