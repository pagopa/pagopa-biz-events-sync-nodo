package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.subject.TransactionPsp;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

  private String idTransaction;

  private String transactionId;

  private long grandTotal;

  private long amount;

  private long fee;

  private String transactionStatus;

  private String accountingStatus;

  private String rrn;

  private String authorizationCode;

  private String creationDate;

  private String numAut;

  private String accountCode;

  private TransactionPsp psp;

  private String origin;
}
