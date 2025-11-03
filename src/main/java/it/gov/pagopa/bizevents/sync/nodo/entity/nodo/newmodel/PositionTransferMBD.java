package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.*;

import java.sql.Blob;
import java.sql.Types;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name = "POSITION_TRANSFER_MBD")
public class PositionTransferMBD {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "TIPO_BOLLO")
  private String tipoBollo;

  @Column(name = "TIPO_ALLEGATO_RICEVUTA")
  private String tipoAllegatoRicevuta;

  @Column(name = "IUBD", length = 255)
  private String iubd;

  @Column(name = "IMPORTO")
  private Long importo;

  @Column(name = "ORA_ACQUISTO")
  private LocalDateTime oraAcquisto;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Lob
  @Basic(fetch = LAZY)
  @JdbcTypeCode(Types.BINARY)
  @Column(name = "XML_CONTENT")
  private Blob xmlContent;

  @OneToOne
  @JoinColumn(name = "FK_POSITION_TRANSFER", referencedColumnName = "ID")
  private PositionTransfer positionTransfer;
}
