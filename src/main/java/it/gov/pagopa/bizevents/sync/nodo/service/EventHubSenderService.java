package it.gov.pagopa.bizevents.sync.nodo.service;

import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import java.util.List;

public interface EventHubSenderService {

  void sendBizEventsToEventHub(List<BizEvent> bizEvents);
}
