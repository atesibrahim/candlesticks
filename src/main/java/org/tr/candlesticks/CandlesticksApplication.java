package org.tr.candlesticks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.socket.client.WebSocketClient;
import org.tr.candlesticks.configuration.CustomWebSocketHandler;
import org.tr.candlesticks.service.StreamService;
import org.tr.candlesticks.stream.Streams;

@SpringBootApplication
public class CandlesticksApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CandlesticksApplication.class, args);

        WebSocketClient webSocketClient = context.getBean(WebSocketClient.class);
        StreamService streamService = context.getBean(StreamService.class);
        CustomWebSocketHandler webSocketHandler = context.getBean(CustomWebSocketHandler.class);

        Streams streams = new Streams(webSocketClient, streamService, webSocketHandler);

        streams.connectToPartnerWebSocket();
    }
}
