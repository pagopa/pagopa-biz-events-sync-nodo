package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class SyncReportRecord {

  @JsonProperty("biz_event_id")
  private String bizEventId;

  @JsonProperty("time_slot")
  private SyncReportTimeSlot timeSlot;

  @JsonProperty("iuv")
  private String iuv;

  @JsonProperty("domain_id")
  private String domainId;

  @JsonProperty("payment_token")
  private String paymentToken;

  @JsonProperty("model_version")
  private PaymentModelVersion modelVersion;

  @JsonProperty("sync_status")
  private String syncStatus;

  @JsonProperty("biz_event")
  private BizEvent event;
}
