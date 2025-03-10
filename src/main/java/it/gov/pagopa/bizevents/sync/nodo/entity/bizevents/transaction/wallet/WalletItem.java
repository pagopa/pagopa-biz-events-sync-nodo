package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.wallet;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.enumeration.WalletType;
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
public class WalletItem {

  private String idWallet;

  private WalletType walletType;

  private List<String> enableableFunctions;

  private boolean pagoPa;

  private String onboardingChannel;

  private boolean favourite;

  private String createDate;

  private WalletItemInfo info;

  private AuthRequest authRequest;
}
