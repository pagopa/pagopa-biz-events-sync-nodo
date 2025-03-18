package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
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
