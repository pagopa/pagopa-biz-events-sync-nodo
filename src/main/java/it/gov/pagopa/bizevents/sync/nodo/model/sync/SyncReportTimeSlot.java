package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncReportTimeSlot {

  private LocalDateTime from;

  private LocalDateTime to;
}
