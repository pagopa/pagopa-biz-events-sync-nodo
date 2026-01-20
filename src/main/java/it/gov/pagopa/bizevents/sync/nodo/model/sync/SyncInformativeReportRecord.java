package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "time_slot",
  "count"
})
@Schema(description = "The records describing the informative detail of a single step.")
public class SyncInformativeReportRecord {

  @JsonProperty("time_slot")
  @Schema(description = "The fine-grained timeslot within which informative operation of this BizEvent was executed.")
  private SyncReportTimeSlot timeSlot;

  @JsonProperty("count")
  @Schema(example = "15", description = "The number of related receipt analyzed by informative operation.")
  private long count;
}
