package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
    name = "ctIdentificativoUnivocoPersonaFG",
    propOrder = {"tipoIdentificativoUnivoco", "codiceIdentificativoUnivoco"})
public class CtIdentificativoUnivocoPersonaFG {

  @XmlSchemaType(name = "string")
  protected StTipoIdentificativoUnivocoPersFG tipoIdentificativoUnivoco;

  @XmlElement(required = true)
  protected String codiceIdentificativoUnivoco;
}
