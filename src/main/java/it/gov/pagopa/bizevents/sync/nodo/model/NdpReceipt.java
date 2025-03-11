package it.gov.pagopa.bizevents.sync.nodo.model;

import it.gov.pagopa.bizevents.sync.nodo.model.enumeration.NodoReceiptInfoVersion;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NdpReceipt {

  private String recordId;

  private String iuv;

  private String paymentToken;

  private String domainId;

  private LocalDateTime insertedTimestamp;

  private NodoReceiptInfoVersion version;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NdpReceipt that = (NdpReceipt) o;
    return Objects.equals(iuv, that.iuv) && Objects.equals(paymentToken, that.paymentToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iuv, paymentToken);
  }
}
