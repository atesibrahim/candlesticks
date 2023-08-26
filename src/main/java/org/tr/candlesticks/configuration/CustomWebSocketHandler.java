package org.tr.candlesticks.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.tr.candlesticks.constant.Constants;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final BlockingQueue<TextMessage> messageQueue = new LinkedBlockingQueue<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        messageQueue.add(message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        subscribeToInstruments(session);
        subscribeToQuotes(session);
    }

    private void subscribeToInstruments(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(Constants.TYPE_SUBSCRIBE_INSTRUMENTS));
    }

    private void subscribeToQuotes(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(Constants.TYPE_SUBSCRIBE_QUOTES));
    }

    public TextMessage receiveMessage() {
        try {
            return messageQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
