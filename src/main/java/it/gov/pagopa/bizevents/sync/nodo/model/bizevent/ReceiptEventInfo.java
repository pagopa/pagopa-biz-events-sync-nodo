package it.gov.pagopa.bizevents.sync.nodo.model.bizevent;

import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.PaymentModelVersion;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptEventInfo {

  private String iuv;

  private String paymentToken;

  private PaymentModelVersion version;

  private String domainId;

  private LocalDateTime insertedTimestamp;

  private LocalDateTime lowerBoundTimeSlot;

  private LocalDateTime upperBoundTimeSlot;

  public ReceiptEventInfo(
      String iuv,
      String paymentToken,
      String domainId,
      LocalDateTime insertedTimestamp,
      PaymentModelVersion version) {

    this.iuv = iuv;
    this.paymentToken = paymentToken;
    this.domainId = domainId;
    this.insertedTimestamp = insertedTimestamp;
    this.version = version;
  }

  public ReceiptEventInfo(Map<String, Object> rawResults) {

    this.iuv = (String) rawResults.getOrDefault("iuv", "N/A");
    this.paymentToken = (String) rawResults.getOrDefault("paymentToken", "N/A");
    this.domainId = (String) rawResults.getOrDefault("domainId", "N/A");
    this.version =
        PaymentModelVersion.valueOf(
            (String) rawResults.getOrDefault("version", PaymentModelVersion.OLD));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReceiptEventInfo that = (ReceiptEventInfo) o;
    return Objects.equals(iuv, that.iuv)
        && Objects.equals(paymentToken, that.paymentToken)
        && Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iuv, paymentToken, version);
  }
}
