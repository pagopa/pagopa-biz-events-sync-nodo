package it.gov.pagopa.bizevents.sync.nodo.util;

import com.google.gson.Gson;
import it.gov.pagopa.bizevents.sync.nodo.model.nodo.oldmodel.rt.CtRicevutaTelematica;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String APICONFIG_CACHE_REQUIRED_KEYS = "psps,creditorInstitutions";

  public static final DateTimeFormatter BIZ_EVENT_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
  public static final DateTimeFormatter BIZ_EVENT_EXTENDED_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

  public static final DateTimeFormatter BIZ_EVENT_REDUCED_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String ECOMMERCE_HELPDESK_TRANSACTION_SEARCH_ID_TYPE = "PAYMENT_TOKEN";

  public static final Gson GSON_PARSER = new Gson();

  public static final String HEADER_REQUEST_ID = "X-Request-Id";

  public static final String HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";

  public static final String REGEN_SERVICE_IDENTIFIER_KEY = "serviceIdentifier";

  public static final JAXBContext RT_JAXB_CONTEXT;

  static {
    try {
      RT_JAXB_CONTEXT = JAXBContext.newInstance(CtRicevutaTelematica.class);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
