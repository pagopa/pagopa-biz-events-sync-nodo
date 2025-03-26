package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ctIdentificativoUnivoco",
    propOrder = {"tipoIdentificativoUnivoco", "codiceIdentificativoUnivoco"})
public class CtIdentificativoUnivoco {

  @XmlSchemaType(name = "string")
  protected StTipoIdentificativoUnivoco tipoIdentificativoUnivoco;

  protected String codiceIdentificativoUnivoco;
}
