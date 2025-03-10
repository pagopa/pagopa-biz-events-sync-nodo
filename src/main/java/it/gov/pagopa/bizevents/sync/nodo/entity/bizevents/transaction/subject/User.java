package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.subject;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.enumeration.UserType;
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
public class User {

  private String fullName;

  private UserType type;

  private String fiscalCode;

  private String notificationEmail;

  private String userId;

  private String userStatus;

  private String userStatusDescription;
}
