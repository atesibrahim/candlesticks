package org.tr.candlesticks.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.tr.candlesticks.configuration.CustomWebSocketHandler;
import org.tr.candlesticks.model.Quote;
import org.tr.candlesticks.service.StreamService;
import org.tr.candlesticks.constant.Constants;
import org.tr.candlesticks.model.Instrument;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
public class Streams {

    private final Logger logger = LoggerFactory.getLogger(Streams.class);
    private final WebSocketClient webSocketClient;
    private final StreamService streamService;
    private final CustomWebSocketHandler webSocketHandler;
    private volatile boolean keepRunning = true;
    private static final String WEBSOCKET_INSTRUMENTS_URL = "ws://localhost:8032/instruments";
    private static final String WEBSOCKET_QUOTES_URL = "ws://localhost:8032/quotes";

    public void connectToPartnerWebSocket() {
        logger.info("Connecting to partner websocket");
        WebSocketSession session = null;

        try {
            session = webSocketClient.execute(webSocketHandler, WEBSOCKET_INSTRUMENTS_URL).get();
            session = webSocketClient.execute(webSocketHandler, WEBSOCKET_QUOTES_URL).get();

            subscribeToInstruments(session);
            subscribeToQuotes(session);

            while (keepRunning) {
                TextMessage receivedMessage = webSocketHandler.receiveMessage();

                if (receivedMessage != null) {
                    String payload = receivedMessage.getPayload();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(payload);

                    String type = jsonNode.has(Constants.TYPE) ? jsonNode.get(Constants.TYPE).asText() : "";
                    if (Constants.QUOTE.equals(type)) {
                        Quote quote = objectMapper.treeToValue(jsonNode.get(Constants.DATA), Quote.class);
                        quote.setTimestamp(Instant.now());
                        streamService.processQuoteUpdate(quote);
                    } else if (Constants.ADD.equals(type)) {
                        Instrument instrument = objectMapper.treeToValue(jsonNode.get(Constants.DATA), Instrument.class);
                        streamService.updateInstrument(instrument);
                    } else if (Constants.DELETE.equals(type)) {
                        Instrument instrument = objectMapper.treeToValue(jsonNode.get(Constants.DATA), Instrument.class);
                        streamService.deleteInstrument(instrument);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while connecting to partner websocket", e);
            throw new RuntimeException(e);
        } finally {
            if (session != null && session.isOpen()) {
                logger.warn("Partner websocket session closed unexpectedly");
                connectToPartnerWebSocket();
            }
        }
    }

    public void stop() {
        keepRunning = false;
    }

    private void subscribeToInstruments(WebSocketSession session) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put(Constants.TYPE, Constants.SUBSCRIBE_INSTRUMENTS);

        String subscriptionRequest = objectMapper.writeValueAsString(requestJson);
        session.sendMessage(new TextMessage(subscriptionRequest));
        session.sendMessage(new TextMessage(subscriptionRequest));
    }

    private void subscribeToQuotes(WebSocketSession session) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put(Constants.TYPE, Constants.SUBSCRIBE_QUOTES);

        String subscriptionRequest = objectMapper.writeValueAsString(requestJson);
        session.sendMessage(new TextMessage(subscriptionRequest));
    }
}
