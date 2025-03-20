package it.gov.pagopa.bizevents.sync.nodo.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bizevents.sync.nodo.entity.bizevents.BizEvent;
import it.gov.pagopa.bizevents.sync.nodo.exception.BizEventSyncException;
import it.gov.pagopa.bizevents.sync.nodo.util.Constants;
import jakarta.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventHubSenderService {

  private final EventHubProducerClient eventHubProducerClient;

  public EventHubSenderService(
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

    if (!bizEvents.isEmpty()) {

      try {

        //
        ObjectMapper mapper = new ObjectMapper();
        List<String> rawJsonBizEvents = new LinkedList<>();
        for (BizEvent event : bizEvents) {
          rawJsonBizEvents.add(mapper.writeValueAsString(event));
        }

        List<EventData> evhEvents = rawJsonBizEvents.stream().map(EventData::new).toList();
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
              throw new IllegalArgumentException(
                  "Event is too large for an empty batch. Max size: [" + batchMaxSize + "].");
            }
          }
        }
        // send the last batch of remaining events
        if (evhEventBatch.getCount() > 0) {
          this.eventHubProducerClient.send(evhEventBatch);
        }
      } catch (Exception e) {
        String ids = bizEvents.stream().map(BizEvent::getId).collect(Collectors.joining(","));
        String msg =
            String.format(
                "An error occurred while trying to send BizEvents with id [%s] to EventHub.", ids);
        throw new BizEventSyncException(msg, e);
      }
    }
  }
}
