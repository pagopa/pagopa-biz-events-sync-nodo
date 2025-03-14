package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "RT_XML")
public class RtXml {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "CCP")
  private String ccp;

  @Column(name = "IDENT_DOMINIO")
  private String identDominio;

  @Column(name = "IUV")
  private String iuv;

  @Column(name = "TIPO_FIRMA")
  private String tipoFirma;

  @Lob
  @Column(name = "XML_CONTENT")
  private String xmlContent;

  @Column(name = "INSERTED_TIMESTAMP")
  private Timestamp insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private Timestamp updatedTimestamp;

  @OneToOne
  @JoinColumn(name = "FK_RT", referencedColumnName = "ID")
  private Rt rt;
}
