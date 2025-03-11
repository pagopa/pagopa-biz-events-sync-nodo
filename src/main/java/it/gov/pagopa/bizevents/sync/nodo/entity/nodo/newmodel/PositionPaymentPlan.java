package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_PAYMENT_PLAN")
public class PositionPaymentPlan {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "NOTICE_ID")
  private String noticeId;

  @Column(name = "CREDITOR_REFERENCE_ID")
  private String creditorReferenceId;

  @Column(name = "DUE_DATE")
  private LocalDateTime dueDate;

  @Column(name = "RETENTION_DATE")
  private LocalDateTime retentionDate;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "FLAG_FINAL_PAYMENT")
  private String flagFinalPayment;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "METADATA")
  private String metadata;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @OneToOne
  @JoinColumn(name = "FK_POSITION_SERVICE", referencedColumnName = "ID")
  private PositionService positionService;
}
