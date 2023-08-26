package org.tr.candlesticks.stream;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.tr.candlesticks.service.StreamService;
import org.tr.candlesticks.configuration.CustomWebSocketHandler;
import org.tr.candlesticks.model.Instrument;
import org.tr.candlesticks.model.Quote;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StreamsTest {
    @Mock
    private WebSocketClient webSocketClient;

    @Mock
    private CompletableFuture<WebSocketSession> completableFuture;

    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private CustomWebSocketHandler webSocketHandler;

    @Mock
    private StreamService streamService;

    @InjectMocks
    private Streams streams;

    @Test
    @Order(1)
    public void testConnectToPartnerWebSocket_Success() throws Exception {
        when(webSocketClient.execute(any(), anyString())).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(webSocketSession);
        when(webSocketHandler.receiveMessage()).thenReturn(new TextMessage("{\"type\": \"QUOTE\", \"data\": { \"price\": 819.7368, \"isin\": \"VJN163T4E2R0\" }}"));

        Thread thread = new Thread(() -> streams.connectToPartnerWebSocket());
        thread.start();

        Thread.sleep(5000);
        streams.stop();

        // Verify interactions
        verify(webSocketClient, atLeastOnce()).execute(eq(webSocketHandler), anyString());
        verify(webSocketHandler, atLeastOnce()).receiveMessage();
        verify(streamService, atLeastOnce()).processQuoteUpdate(any(Quote.class));
    }

    @Test
    @Order(3)
    public void testConnectToPartnerWebSocket_SuccessInstrumentDelete() throws Exception {
        when(webSocketClient.execute(any(), anyString())).thenReturn(completableFuture);
        when(completableFuture.get()).thenReturn(webSocketSession);
        when(webSocketHandler.receiveMessage()).thenReturn(new TextMessage("{\"type\": \"DELETE\", \"data\": { \"description\": \"dolorum putent explicari molestiae quidam\", \"isin\": \"MBH132512243\" }}"));

        Thread thread = new Thread(() -> streams.connectToPartnerWebSocket());
        thread.start();

        Thread.sleep(5000);
        streams.stop();
        thread.join();

        // Verify interactions
        verify(webSocketClient, atLeastOnce()).execute(any(), anyString());
        verify(webSocketHandler, atLeastOnce()).receiveMessage();
        verify(streamService, atLeastOnce()).deleteInstrument(any(Instrument.class));
    }

    @Test
    @Order(4)
    public void testConnectToPartnerWebSocket_Exception() {
        when(webSocketClient.execute(any(), anyString())).thenThrow(new RuntimeException("WebSocket error"));

        try {
            streams.connectToPartnerWebSocket();
        } catch (Exception ignored) {
        }

        // Verify interactions
        verify(webSocketClient, times(1)).execute(eq(webSocketHandler), anyString());
    }
}

