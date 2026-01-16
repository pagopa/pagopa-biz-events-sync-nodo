package it.gov.pagopa.bizevents.sync.nodo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bizevents.sync.nodo.repository.BizEventsRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.historic.receipt.HistoricPositionReceiptRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.historic.receipt.HistoricRtRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.primary.receipt.PositionReceiptRepository;
import it.gov.pagopa.bizevents.sync.nodo.repository.primary.receipt.RtRepository;
import it.gov.pagopa.bizevents.sync.nodo.service.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = BizEventsSyncNodoApplication.class)
@AutoConfigureMockMvc
class OpenApiGenerationTest {

  @Autowired ObjectMapper objectMapper;

  @Autowired private MockMvc mvc;

  @MockBean private BizEventSynchronizerService bizEventSynchronizerService;

  @MockBean private BizEventsRepository bizEventsRepository;

  @MockBean private BizEventsReaderService bizEventsReaderService;

  @MockBean private PaymentPositionReaderService paymentPositionReaderService;

  @MockBean private EcommerceHelpdeskReaderService ecommerceHelpdeskReaderService;

  @MockBean private EventHubSenderService eventHubSenderService;

  @MockBean private PositionReceiptRepository positionReceiptRepository;

  @MockBean private RtRepository rtRepository;

  @MockBean private HistoricPositionReceiptRepository historicPositionReceiptRepository;

  @MockBean private HistoricRtRepository historicRtRepository;

  @Test
  void swaggerSpringPlugin() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andDo(
            (result) -> {
              assertNotNull(result);
              assertNotNull(result.getResponse());
              final String content = result.getResponse().getContentAsString();
              assertFalse(content.isBlank());
              assertFalse(content.contains("${"), "Generated swagger contains placeholders");
              Object swagger =
                  objectMapper.readValue(result.getResponse().getContentAsString(), Object.class);
              String formatted =
                  objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
              Path basePath = Paths.get("openapi/");
              Files.createDirectories(basePath);
              Files.write(basePath.resolve("openapi.json"), formatted.getBytes());
            });
  }
}
