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
import org.springframework.web.bind.annotation.*;

@RestController()
@Slf4j
@RequestMapping("/manual")
public class SyncController {

  private final BizEventSynchronizerService bizEventSynchronizerService;

  public SyncController(BizEventSynchronizerService bizEventSynchronizerService) {

    this.bizEventSynchronizerService = bizEventSynchronizerService;
  }

  @Operation(
      summary = "Manually synchronize BizEvents by time slot",
      description = "Execute a synchronization, manually starting the operation",
      tags = {"Manual"})
  @GetMapping(value = "/synchronize")
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
      @RequestParam(name = "timeSlotSize", defaultValue = "-1")
          @Schema(
              example = "10",
              description =
                  "Override default time slot size in minutes. The values must be greater than 1")
          int overriddenTimeSlotSize,
      @RequestParam(name = "showBizEvents", defaultValue = "true")
          @Schema(example = "true", description = "Show generated biz events data in final report")
          boolean showBizEvents) {

    log.info("Invoking BizEvent-to-Nodo synchronization via HTTP manual trigger!");
    long start = Calendar.getInstance().getTimeInMillis();
    SyncReport report =
        bizEventSynchronizerService.executeSynchronization(
            dateFrom, dateTo, overriddenTimeSlotSize, showBizEvents);
    log.info(
        "Invoked BizEvent-to-Nodo synchronization via HTTP manual trigger completed in [{}] ms!",
        CommonUtility.getTimelapse(start));
    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @Operation(
      summary = "Manually synchronize single BizEvents",
      description = "Execute a synchronization for single event, manually starting the operation",
      tags = {"Manual"})
  @GetMapping(value = "/synchronize/single")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<SyncReport> manuallySynchronizeSingleEvent(
      @NotNull
          @RequestParam(name = "dateFrom")
          @Schema(example = "2025-01-01T12:00:00", description = "Lower limit date")
          LocalDateTime dateFrom,
      @NotNull
          @RequestParam(name = "dateTo")
          @Schema(example = "2025-01-01T21:00:00", description = "Upper limit date")
          LocalDateTime dateTo,
      @RequestParam(name = "domainId")
          @Schema(example = "77777777777", description = "Creditor institution identifier")
          String domainId,
      @RequestParam(name = "noticeNumber")
          @Schema(example = "300000000000000000", description = "Notice number")
          String noticeNumber) {

    log.info("Invoking BizEvent-to-Nodo single BizEvent synchronization!");
    long start = Calendar.getInstance().getTimeInMillis();
    SyncReport report =
        bizEventSynchronizerService.executeSynchronizationForSingleReceipt(
            dateFrom, dateTo, domainId, noticeNumber);
    log.info(
        "Invoked BizEvent-to-Nodo single BizEvent synchronization completed in [{}] ms!",
        CommonUtility.getTimelapse(start));
    return ResponseEntity.status(HttpStatus.OK).body(report);
  }
}
