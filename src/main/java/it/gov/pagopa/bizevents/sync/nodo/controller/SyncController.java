package it.gov.pagopa.bizevents.sync.nodo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.bizevents.sync.nodo.service.BizEventSynchronizerService;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

  @Operation()
  @GetMapping(value = "/sync")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Map<String, Object>> manuallySynchronize(
      @NotNull
          @RequestParam(name = "dateFrom")
          @Schema(example = "2025-01-01T12:00:00", description = "Lower limit date")
          LocalDateTime dateFrom,
      @NotNull
          @RequestParam(name = "dateTo")
          @Schema(example = "2025-01-01T21:00:00", description = "Upper limit date")
          LocalDateTime dateTo) {

    List<String> analyzedBizEvents =
        bizEventSynchronizerService.executeSynchronization(dateFrom, dateTo);
    Map<String, Object> response =
        Map.of(
            "time_slot", String.format("[%s - %s]", dateFrom, dateTo), "events", analyzedBizEvents);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
