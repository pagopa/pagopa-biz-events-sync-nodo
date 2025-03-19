package it.gov.pagopa.bizevents.sync.nodo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.bizevents.sync.nodo.model.sync.SyncReport;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventSynchronizerService;
import it.gov.pagopa.bizevents.sync.nodo.util.CommonUtility;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
@RequestMapping("/manual")
public class SyncController {

  private final BizEventSynchronizerService bizEventSynchronizerService;

  public SyncController(BizEventSynchronizerService bizEventSynchronizerService) {

    this.bizEventSynchronizerService = bizEventSynchronizerService;
  }

  @Operation()
  @GetMapping(value = "/sync")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<SyncReport> manuallySynchronize(
      @NotNull
          @RequestParam(name = "dateFrom")
          @Schema(example = "2025-01-01T12:00:00", description = "Lower limit date")
          LocalDateTime dateFrom,
      @NotNull
          @RequestParam(name = "dateTo")
          @Schema(example = "2025-01-01T21:00:00", description = "Upper limit date")
          LocalDateTime dateTo,
      @RequestParam(name = "showBizEvents", defaultValue = "true")
          @Schema(example = "true", description = "Show generated biz events data in final report")
          boolean showBizEvents) {

    log.info("Invoking BizEvent-to-Nodo synchronization via HTTP manual trigger!");
    long start = Calendar.getInstance().getTimeInMillis();
    SyncReport report =
        bizEventSynchronizerService.executeSynchronization(dateFrom, dateTo, showBizEvents);
    log.info(
        "Invoked BizEvent-to-Nodo synchronization via HTTP manual trigger completed in [{}] ms!",
        CommonUtility.getTimelapse(start));
    return ResponseEntity.status(HttpStatus.OK).body(report);
  }
}
