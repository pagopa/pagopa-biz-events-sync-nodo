package it.gov.pagopa.bizevents.sync.nodo.controller;

import io.swagger.v3.oas.annotations.Operation;
import it.gov.pagopa.bizevents.sync.nodo.model.AppInfo;
import it.gov.pagopa.bizevents.sync.nodo.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
public class BaseController {

  private final BaseService baseService;

  public BaseController(BaseService baseService) {

    this.baseService = baseService;
  }

  @Operation(
      summary = "health check",
      description = "Return OK if application is started",
      tags = {"Home"})
  @GetMapping(value = "/info")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<AppInfo> healthCheck() {

    return ResponseEntity.status(HttpStatus.OK).body(baseService.health());
  }
}
