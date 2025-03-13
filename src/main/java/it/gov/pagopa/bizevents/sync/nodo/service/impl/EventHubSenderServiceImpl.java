package it.gov.pagopa.bizevents.sync.nodo.service.impl;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.service.EventHubSenderService;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventHubSenderServiceImpl implements EventHubSenderService {

  private final EventHubProducerClient eventHubProducerClient;

  public EventHubSenderServiceImpl(
      @Value("${azure.event-hub.connection-string}") String eventHubConnectionString,
      @Value("${azure.event-hub.producer.name}") String eventHubTopicName) {

    this.eventHubProducerClient =
        new EventHubClientBuilder()
            .connectionString(eventHubConnectionString, eventHubTopicName)
            .buildProducerClient();
  }

  @PreDestroy
  private void preDestroy() {
    this.eventHubProducerClient.close();
  }

  public void sendBizEventsToEventHub(List<BizEvent> bizEvents) {

    List<EventData> evhEvents =
        bizEvents.stream()
            .map(bizEvent -> new EventData(Constants.GSON_PARSER.toJson(bizEvent)))
            .toList();
    evhEvents.forEach(
        eventData ->
            eventData
                .getProperties()
                .put(
                    Constants.REGEN_SERVICE_IDENTIFIER_KEY,
                    Constants.REGEN_SERVICE_IDENTIFIER_VALUE));

    //
    EventDataBatch evhEventBatch = this.eventHubProducerClient.createBatch();

    int batchMaxSize = evhEventBatch.getMaxSizeInBytes();
    log.debug("Defining batches with maximum dimension of [" + batchMaxSize + "] bytes.");

    for (EventData evhEvent : evhEvents) {

      // Try to add the event from the array to the batch
      if (!evhEventBatch.tryAdd(evhEvent)) {

        // If the batch is full, send it and then create a new batch
        this.eventHubProducerClient.send(evhEventBatch);
        evhEventBatch = this.eventHubProducerClient.createBatch();

        // Try to add that event that couldn't fit before.
        if (!evhEventBatch.tryAdd(evhEvent)) {
          log.error("Event is too large for an empty batch. Max size: [" + batchMaxSize + "].");
          // TODO throw custom exception
        }
      }
    }
    // send the last batch of remaining events
    if (evhEventBatch.getCount() > 0) {
      this.eventHubProducerClient.send(evhEventBatch);
    }
  }
}
