package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.transaction.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AuthRequest {

  private String authOutcome;

  private String guid;

  private String correlationId;

  private String error;

  @JsonProperty(value = "auth_code")
  private String authCode;
}
