package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  private String userFiscalCode;

  private String notificationEmail;

  private String surname;

  private String name;

  private String username;
}
