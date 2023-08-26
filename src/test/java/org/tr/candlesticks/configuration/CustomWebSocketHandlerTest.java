package org.tr.candlesticks.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CustomWebSocketHandlerTest {
    @InjectMocks
    private CustomWebSocketHandler webSocketHandler;

    @Test
    public void testHandleTextMessage() throws Exception {
        //Given
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("Test message");

        //When
        webSocketHandler.handleTextMessage(session, message);

        //Then
        TextMessage receivedMessage = webSocketHandler.receiveMessage();
        assertEquals(message, receivedMessage);
    }

    @Test
    public void testAfterConnectionEstablished() throws Exception {
        //Given
        WebSocketSession session = mock(WebSocketSession.class);

        //When
        webSocketHandler.afterConnectionEstablished(session);

        //Then
        verify(session, times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    public void testReceiveMessage() throws Exception {
        //Given
        BlockingQueue<TextMessage> messageQueue = new LinkedBlockingQueue<>();
        TextMessage testMessage = new TextMessage("Test message");
        messageQueue.add(testMessage);

        ReflectionTestUtils.setField(webSocketHandler, "messageQueue", messageQueue);

        TextMessage receivedMessage = webSocketHandler.receiveMessage();

        assertEquals(testMessage, receivedMessage);
    }

    @Test
    public void testReceiveMessageInterruptedException() throws Exception {
        BlockingQueue<TextMessage> messageQueue = mock(BlockingQueue.class);
        when(messageQueue.take()).thenThrow(new InterruptedException());

        ReflectionTestUtils.setField(webSocketHandler, "messageQueue", messageQueue);
        TextMessage receivedMessage = webSocketHandler.receiveMessage();

        assertNull(receivedMessage);
        assertTrue(Thread.currentThread().isInterrupted());
    }
}
