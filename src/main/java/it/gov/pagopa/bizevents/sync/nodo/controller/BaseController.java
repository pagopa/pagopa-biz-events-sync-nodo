package it.gov.pagopa.bizevents.sync.nodo.controller;

import io.swagger.v3.oas.annotations.Operation;
import it.gov.pagopa.bizevents.sync.nodo.model.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
public class BaseController {

  @Value("${info.application.name}")
  private String name;

  @Value("${info.application.version}")
  private String version;

  @Value("${info.properties.environment}")
  private String environment;

  @Operation(
      summary = "health check",
      description = "Return OK if application is started",
      tags = {"Home"})
  @GetMapping(value = "/info")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<AppInfo> healthCheck() {

    AppInfo info = AppInfo.builder().name(name).version(version).environment(environment).build();
    return ResponseEntity.status(HttpStatus.OK).body(info);
  }
}
