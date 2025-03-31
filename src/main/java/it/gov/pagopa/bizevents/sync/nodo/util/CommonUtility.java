package it.gov.pagopa.bizevents.sync.nodo.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtility {

  /**
   * @param value value to deNullify.
   * @return return empty string if value is null
   */
  public static String deNull(String value) {
    return Optional.ofNullable(value).orElse("");
  }

  /**
   * @param value value to deNullify.
   * @return return empty string if value is null
   */
  public static String deNull(Object value) {
    return Optional.ofNullable(value).orElse("").toString();
  }

  /**
   * @param value value to deNullify.
   * @return return false if value is null
   */
  public static Boolean deNull(Boolean value) {
    return Optional.ofNullable(value).orElse(false);
  }

  /**
   * Split the time duration in between bounds in several slots of fixed size.
   *
   * @param lowerBound the lower bound of the time duration
   * @param upperBound the lower bound of the time duration
   * @param slotSizeInMinutes the fixed size of the time slots
   * @return the list of newly created time slots
   */
  public static List<LocalDateTime> splitInSlots(
      LocalDateTime lowerBound, LocalDateTime upperBound, int slotSizeInMinutes) {

    List<LocalDateTime> slots = new ArrayList<>();
    if (lowerBound.isBefore(upperBound)) {
      LocalDateTime currentSlot = LocalDateTime.from(lowerBound);
      slots.add(currentSlot);
      while (currentSlot.isBefore(upperBound)) {
        currentSlot = currentSlot.plusMinutes(slotSizeInMinutes);
        slots.add(currentSlot);
      }
    } else {
      slots.add(lowerBound);
      slots.add(upperBound);
    }
    return slots;
  }

  public static String formatDate(LocalDateTime date, DateTimeFormatter formatter) {
    String formattedDate = null;
    if (date != null && formatter != null) {
      formattedDate = date.format(formatter);
    }
    return formattedDate;
  }

  public static String toPlainString(Long value) {
    return value != null ? value.toString() : null;
  }

  public static String toPlainString(BigDecimal value) {
    return value != null ? value.toPlainString() : null;
  }

  public static String toPlainString(Double value) {
    return value != null ? value.toString() : null;
  }

  public static long getTimelapse(long startTime) {
    return Calendar.getInstance().getTimeInMillis() - startTime;
  }

  public static String convertBlob(byte[] blobContent) {
    String convertedBlob = null;
    if (blobContent.length > 0) {
      convertedBlob = new String(blobContent, StandardCharsets.UTF_8);
    }
    return convertedBlob;
  }
}
