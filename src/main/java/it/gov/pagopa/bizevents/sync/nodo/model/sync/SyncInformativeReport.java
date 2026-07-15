package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "status",
  "execution_time_slot",
  "total_count"
})
@JsonInclude(Include.NON_NULL)
@Schema(description = "A report that defines the status of the synchronization operation for each BizEvent manipulated and in its entirety.")
public class SyncInformativeReport {

  @JsonProperty("status")
  @Schema(example = "TO_GENERATE", description = "The outcome of the informative process.")
  private SyncOutcome status;

  @JsonProperty("execution_time_slot")
  @Schema(description = "The timeslot within which informative operation was executed.")
  private SyncReportTimeSlot executionTimeSlot;

  @JsonProperty("total_count")
  @Schema(example = "7", description = "The total number of BizEvents analyzed by the informative process.")
  private long totalCount;

  @Schema(description = "The list of individual records describing the informative status on a single step.")
  private List<SyncInformativeReportRecord> records;
}
