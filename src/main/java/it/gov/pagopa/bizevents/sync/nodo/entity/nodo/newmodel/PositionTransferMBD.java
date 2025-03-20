package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
  @Column(name = "XML_CONTENT")
  private byte[] xmlContent;

  @OneToOne
  @JoinColumn(name = "FK_POSITION_TRANSFER", referencedColumnName = "ID")
  private PositionTransfer positionTransfer;
}
