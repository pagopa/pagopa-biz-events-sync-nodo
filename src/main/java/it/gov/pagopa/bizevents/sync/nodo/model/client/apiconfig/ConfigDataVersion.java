package it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDataVersion {

  private String cacheVersion;

  private String version;
}
