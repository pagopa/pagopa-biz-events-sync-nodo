package it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "stTipoIdentificativoUnivocoPersFG")
@XmlEnum
public enum StTipoIdentificativoUnivocoPersFG {
  F,
  G;

  public static StTipoIdentificativoUnivocoPersFG fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }
}
