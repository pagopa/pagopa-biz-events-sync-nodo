package it.gov.pagopa.bizevents.sync.nodo.model.client.apiconfig;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The cached configuration data, retrieved from APIConfig Cache, that can be used instead of
 * directly invoking APIConfig backend services. This DTO contains just a subset of all
 * configuration because only some information are required for this application.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDataV1 implements Serializable {

  private String cacheVersion;

  private String version;

  private Map<String, CreditorInstitution> creditorInstitutions;

  private Map<String, PaymentServiceProvider> psps;
}
