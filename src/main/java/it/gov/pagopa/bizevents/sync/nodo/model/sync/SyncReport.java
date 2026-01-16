package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

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
@JsonPropertyOrder({
  "status",
  "execution_time_slot",
  "sent_to_event_hub",
  "total_records"
})
@JsonInclude(Include.NON_NULL)
@Schema(description = "A report that defines the status of the synchronization operation for each BizEvent manipulated and in its entirety.")
public class SyncReport {

  @JsonProperty("status")
  @Schema(example = "GENERATED", description = "The outcome of the synchronization process.")
  private SyncOutcome status;

  @JsonProperty("execution_time_slot")
  @Schema(description = "The timeslot within which synchronization operation was executed.")
  private SyncReportTimeSlot executionTimeSlot;

  @JsonProperty("sent_to_event_hub")
  @Schema(example = "true", description = "The flag that determines whether events have been sent to the EventHub infrastructure component to be ingested by the BizEvents processor. This does NOT define whether the BizEvent has been stored in the DB.")
  private boolean sentToEventHub;

  @JsonProperty("total_records")
  @Schema(example = "7", description = "The total number of BizEvents regenerated through the executed process.")
  private int totalRecords;

  @Schema(description = "The list of individual records describing the synchronization status of a single BizEvent.")
  private List<SyncReportRecord> records;
}
