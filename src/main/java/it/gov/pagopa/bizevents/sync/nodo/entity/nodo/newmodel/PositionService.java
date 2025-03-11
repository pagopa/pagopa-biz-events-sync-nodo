package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_SERVICE")
public class PositionService {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PA_FISCAL_CODE")
  private String paFiscalCode;

  @Column(name = "NOTICE_ID")
  private String noticeId;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "COMPANY_NAME")
  private String companyName;

  @Column(name = "OFFICE_NAME")
  private String officeName;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @OneToOne
  @JoinColumn(name = "DEBTOR_ID", referencedColumnName = "ID")
  private PositionSubject debtor;
}
