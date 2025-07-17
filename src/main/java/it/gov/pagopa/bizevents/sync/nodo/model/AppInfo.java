package it.gov.pagopa.bizevents.sync.nodo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppInfo {

  private String name;

  private String version;

  private String environment;

  @JsonProperty("primary_db")
  private String primaryDb;

  @JsonProperty("historic_db")
  private String historicDb;
}
