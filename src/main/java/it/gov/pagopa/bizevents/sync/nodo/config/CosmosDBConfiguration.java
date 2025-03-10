package it.gov.pagopa.bizevents.sync.nodo.config;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.core.mapping.EnableCosmosAuditing;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
@EnableCosmosRepositories("it.gov.pagopa.bizevents.sync.nodo.repository")
@EnableCosmosAuditing
@ConditionalOnExpression("'${info.properties.environment}'!='test'")
@Slf4j
public class CosmosDBConfiguration extends AbstractCosmosConfiguration {

  @Value("${azure.cosmos.uri}")
  private String uri;

  @Value("${azure.cosmos.key}")
  private String key;

  @Value("${azure.cosmos.database}")
  private String dbName;

  @Value("${azure.cosmos.populate-query-metrics}")
  private boolean queryMetricsEnabled;

  @Value("${azure.cosmos.read.region}")
  private String readRegion;

  @Bean
  CosmosClientBuilder getCosmosClientBuilder() {

    AzureKeyCredential azureKeyCredential = new AzureKeyCredential(key);
    DirectConnectionConfig directConnectionConfig = new DirectConnectionConfig();
    return new CosmosClientBuilder()
        .endpoint(uri)
        .credential(azureKeyCredential)
        .preferredRegions(List.of(readRegion))
        .directMode(directConnectionConfig);
  }

  @Override
  public CosmosConfig cosmosConfig() {
    return CosmosConfig.builder()
        .enableQueryMetrics(queryMetricsEnabled)
        .responseDiagnosticsProcessor(new ResponseDiagnosticsProcessorImplementation())
        .build();
  }

  @Override
  protected String getDatabaseName() {
    return dbName;
  }

  private static class ResponseDiagnosticsProcessorImplementation
      implements ResponseDiagnosticsProcessor {

    @Override
    public void processResponseDiagnostics(@Nullable ResponseDiagnostics responseDiagnostics) {
      log.info("Response Diagnostics {}", responseDiagnostics);
    }
  }
}
