package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "stTipoIdentificativoUnivoco")
@XmlEnum
public enum StTipoIdentificativoUnivoco {
  G,
  A,
  B;

  public static StTipoIdentificativoUnivoco fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }
}
