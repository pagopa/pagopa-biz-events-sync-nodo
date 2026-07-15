package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "The timeslot within which synchronization operation was executed.")
public class SyncReportTimeSlot {

  @Schema(description = "The timeslot start (as date-time) from which synchronization operation was executed.")
  private LocalDateTime from;

  @Schema(description = "The timeslot end (as date-time) until which synchronization operation was executed.")
  private LocalDateTime to;
}
