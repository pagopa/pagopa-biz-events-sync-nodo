package it.gov.pagopa.bizevents.sync.nodo.model.client.ecommerce.response;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class TransactionInfo implements Serializable {

  private LocalDateTime creationDate;

  private String status;

  private String statusDetails;

  private String eventStatus;

  private Long amount;

  private Long fee;

  private Long grandTotal;

  private String rrn;

  private String authorizationCode;

  private String authorizationOperationId;

  private String refundOperationId;

  private String paymentMethodName;

  private String brand;

  private String authorizationRequestId;

  private String paymentGateway;

  private String correlationId;

  private String gatewayAuthorizationStatus;

  private String gatewayErrorCode;
}
