package it.gov.pagopa.bizevents.sync.nodo.util;

import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final DateTimeFormatter BIZ_EVENT_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  public static final String ECOMMERCE_HELPDESK_TRANSACTION_SEARCH_ID_TYPE = "PAYMENT_TOKEN";

  public static final String HEADER_REQUEST_ID = "X-Request-Id";

  public static final String HEADER_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
}
