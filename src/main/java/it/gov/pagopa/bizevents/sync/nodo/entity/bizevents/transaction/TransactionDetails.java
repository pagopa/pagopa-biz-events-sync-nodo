package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.subject.User;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.wallet.WalletItem;
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
public class TransactionDetails {

  private String origin;

  private User user;

  private Transaction transaction;

  private WalletItem wallet;

  private InfoTransaction info;
}
