package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_TRANSFER")
public class PositionTransfer {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "NOTICE_ID")
  private String noticeId;

  @Column(name = "CREDITOR_REFERENCE_ID")
  private String creditorReferenceId;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "PA_FISCAL_CODE_SECONDARY")
  private String paFiscalCodeSecondary;

  @Column(name = "IBAN")
  private String iban;

  @Column(name = "AMOUNT")
  private Double amount;

  @Column(name = "REMITTANCE_INFORMATION")
  private String remittanceInformation;

  @Column(name = "TRANSFER_CATEGORY")
  private String transferCategory;

  @Column(name = "TRANSFER_IDENTIFIER")
  private String transferIdentifier;

  @Column(name = "VALID", length = 1)
  private String valid;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "METADATA", length = 3000)
  private String metadata;

  @Column(name = "REQ_TIPO_BOLLO", length = 2)
  private String reqTipoBollo;

  @Column(name = "REQ_HASH_DOCUMENTO", length = 72)
  private String reqHashDocumento;

  @Column(name = "REQ_PROVINCIA_RESIDENZA", length = 2)
  private String reqProvinciaResidenza;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Column(name = "FK_POSITION_PAYMENT")
  private Long fkPositionPayment;

  @Column(name = "FK_PAYMENT_PLAN")
  private Long fkPaymentPlan;

  @OneToOne(mappedBy = "positionTransfer", fetch = FetchType.EAGER)
  private PositionTransferMBD positionTransferMBD;
}
