package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "stTipoIdentificativoUnivocoPersG")
@XmlEnum
public enum StTipoIdentificativoUnivocoPersG {
  G;

  public static StTipoIdentificativoUnivocoPersG fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }
}
