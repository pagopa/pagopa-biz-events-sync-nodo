package it.gov.pagopa.bizevents.sync.nodo.util;

import java.time.LocalDateTime;
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
   * @param slotSizeInHours the fixed size of the time slots
   * @return the list of newly created time slots
   */
  public static List<LocalDateTime> splitInSlots(
      LocalDateTime lowerBound, LocalDateTime upperBound, int slotSizeInHours) {

    List<LocalDateTime> slots = new ArrayList<>();
    if (lowerBound.isBefore(upperBound)) {
      LocalDateTime currentSlot = LocalDateTime.from(lowerBound);
      slots.add(currentSlot);
      while (!currentSlot.isEqual(upperBound)) {
        currentSlot = currentSlot.plusHours(slotSizeInHours);
        slots.add(currentSlot);
      }
    }
    return slots;
  }

  public static long getTimelapse(long startTime) {
    return Calendar.getInstance().getTimeInMillis() - startTime;
  }
}
