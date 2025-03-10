package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.enumeration.StatusType;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.DebtorPosition;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.PaymentInfo;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Debtor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.subject.Payer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment.transfer.Transfer;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Creditor;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.subject.Psp;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.TransactionDetails;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Container(
    containerName = "${azure.cosmos.biz-events-container-name}",
    autoCreateContainer = false,
    ru = "1000")
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
