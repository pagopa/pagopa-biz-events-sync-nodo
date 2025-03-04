package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.enumeration.WalletType;
import lombok.*;

import java.util.List;

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
	private Info info;
	private AuthRequest authRequest;
}
