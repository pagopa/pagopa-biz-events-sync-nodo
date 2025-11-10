package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "stTipoAllegatoRicevuta")
@XmlEnum
public enum StTipoAllegatoRicevuta {
  ES,
  BD;

  public static StTipoAllegatoRicevuta fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }
}
