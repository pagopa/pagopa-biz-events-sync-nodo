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
    name = "ctAllegatoRicevuta",
    propOrder = {"tipoAllegatoRicevuta", "testoAllegato"})
public class CtAllegatoRicevuta {

  @XmlSchemaType(name = "string")
  protected StTipoAllegatoRicevuta tipoAllegatoRicevuta;

  protected byte[] testoAllegato;
}
