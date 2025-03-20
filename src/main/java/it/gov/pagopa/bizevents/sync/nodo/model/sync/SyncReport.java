package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
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
  "execution_time_slot",
  "sent_to_event_hub",
  "error_during_computation",
  "total_records"
})
@JsonInclude(Include.NON_NULL)
public class SyncReport {

  @JsonProperty("execution_time_slot")
  private SyncReportTimeSlot executionTimeSlot;

  @JsonProperty("sent_to_event_hub")
  private boolean sentToEventHub;

  @JsonProperty("error_during_computation")
  private boolean errorDuringComputation;

  @JsonProperty("total_records")
  private int totalRecords;

  private List<SyncReportRecord> records;
}
