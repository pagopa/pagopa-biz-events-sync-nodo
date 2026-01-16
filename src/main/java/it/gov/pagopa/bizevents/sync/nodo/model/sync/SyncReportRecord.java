package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
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
  "biz_event_id",
  "time_slot",
  "iuv",
  "domain_id",
  "payment_token",
  "model_version",
  "sync_status",
  "biz_event"
})
@Schema(description = "The records describing the synchronization status of a single BizEvent.")
public class SyncReportRecord {

  @JsonProperty("biz_event_id")
  @Schema(example = "resync-000ac324-1c00-469f-8b00-950000fb1432", description = "The identifier of generated BizEvent. It contains a special prefix that defines whether the sync process was made.")
  private String bizEventId;

  @JsonProperty("time_slot")
  @Schema(description = "The fine-grained timeslot within which synchronization operation of this BizEvent was executed.")
  private SyncReportTimeSlot timeSlot;

  @JsonProperty("iuv")
  @Schema(example = "01102000021300930", description = "The IUV code of related receipt.")
  private String iuv;

  @JsonProperty("domain_id")
  @Schema(example = "77777777777", description = "The identifier of the creditor institution.")
  private String domainId;

  @JsonProperty("payment_token")
  @Schema(example = "000ff000ffffffff00000fff00000000", description = "The payment token of related trnasaction.")
  private String paymentToken;

  @JsonProperty("model_version")
  @Schema(example = "NEW", description = "The model of the payment related to the receipt. 'NEW' means payment position, 'OLD' means RPT")
  private PaymentModelVersion modelVersion;

  @JsonProperty("sync_status")
  @Schema(example = "GENERATED", description = "The status of re-generation of the BizEvent. This does not mean that the BizEvent has been correctly stored in the DB, but rather that it has been sent to EventHub for ingestion process.")
  private SyncSingleRecordStatus syncStatus;

  @JsonProperty("biz_event")
  @Schema(description = "The data related to the BizEvent generated.")
  private BizEvent event;
}
