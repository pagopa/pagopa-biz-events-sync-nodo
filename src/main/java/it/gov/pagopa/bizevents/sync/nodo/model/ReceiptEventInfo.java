package it.gov.pagopa.bizevents.sync.nodo.model;

import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReceiptEventInfo {

  private String iuv;

  private String paymentToken;

  private NodoReceiptInfoVersion version;

  private String domainId;

  private LocalDateTime insertedTimestamp;

  public ReceiptEventInfo(
      String iuv,
      String paymentToken,
      String domainId,
      LocalDateTime insertedTimestamp,
      NodoReceiptInfoVersion version) {

    this.iuv = iuv;
    this.paymentToken = paymentToken;
    this.domainId = domainId;
    this.insertedTimestamp = insertedTimestamp;
    this.version = version;
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
