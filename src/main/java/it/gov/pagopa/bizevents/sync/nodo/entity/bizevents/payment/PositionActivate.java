package it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionActivate {

  private Long id;

  private String paFiscalCode;

  private String noticeNumber;

  private String creditorReference;

  private String idPsp;

  private String idempotencyKey;

  private String paymentToken;

  private String paymentTokenValidFrom;

  private String paymentTokenValidTo;

  private String dueDate;

  private String amount;

  private String insertedTimestamp;

  private String updatedTimestamp;

  private String insertedBy;

  private String updatedBy;

  private String paymentNote;

  private String paymentMethod;

  private String touchpoint;

  private String suggestedIdBundle;

  private String suggestedIdCiBundle;

  private String suggestedUserFee;

  private String suggestedPaFee;
}