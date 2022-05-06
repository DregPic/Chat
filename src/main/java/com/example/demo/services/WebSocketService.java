package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.time.LocalTime;

import static com.example.demo.handlers.ServerWebSocketHandler.sessions;

@Component
@EnableScheduling
@Slf4j
public class WebSocketService {

    @Scheduled(fixedRate = 10000)
    void sendPeriodicMessages() throws IOException {
        if (sessions != null) {
            sessions.forEach((key, value) -> {
                if (value.isOpen()) {
                    String broadcast = "server periodic message " + LocalTime.now();
                    log.info("Server sends: {}", broadcast);
                    try {
                        value.sendMessage(new TextMessage(broadcast));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }
}
