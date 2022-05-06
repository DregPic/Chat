package com.example.demo.listeners;

import com.example.demo.models.MySQLMessageDto;
import com.example.demo.models.RedisMessageDto;
import com.example.demo.services.MySQLMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import static com.example.demo.CommandConstant.ID_HEADER_KEY;
import static com.example.demo.handlers.ServerWebSocketHandler.sessions;

import java.util.Map;

@Component
@NoArgsConstructor
@Slf4j
public class RedisMessageListener implements MessageListener {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {
        var messageValues = objectMapper.readValue(message.toString(), RedisMessageDto.class);
        var receiverId =  messageValues.getReceiver();
        if (sessions.containsKey(receiverId)) {
            sessions.get(receiverId)
                    .sendMessage(new TextMessage(message.toString()));
            log.info("Consumed event {}", message);
        } else {
            log.info("Consumed event {}", message);
        }
    }
}
