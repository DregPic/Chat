package com.example.demo.handlers;

import com.example.demo.models.MySQLMessageDto;
import com.example.demo.models.RedisMessageDto;
import com.example.demo.models.SocketMessageDto;
import com.example.demo.services.ChatRoomService;
import com.example.demo.services.MySQLMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

import static com.example.demo.CommonConstant.*;

@Component
@Slf4j
public class ServerWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private RedisTemplate template;
    @Autowired
    private ChatRoomService chatRoomService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ChannelTopic topic;
    private Object UserID;
    public static Map<String, WebSocketSession> sessions = new HashMap<>();
    private Gson gson = new Gson();

    @Autowired
    private MySQLMessageService mySQLMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (containValidHeaders(session)) {
            UserID = getUserId(session);
            sessions.put(UserID.toString(), session);
            log.info("New connection accepted, id {}", session.getId());
        } else {
            session.close(new CloseStatus(1015)); /// send error if user have no "userId" header
        }
    }

    @Override
    @SneakyThrows
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        var request = message.getPayload();
        var senderId = Objects.requireNonNull(session.getHandshakeHeaders()
                        .get(ID_HEADER_KEY))
                .get(0);
        var requestValues = objectMapper.readValue(request, SocketMessageDto.class);
        String chatRoom = Objects.requireNonNull(session.getHandshakeHeaders()
                        .get(ROOM_ID_HEADER_KEY))
                .get(0);
        var chatUsers = chatRoomService
                .findById(chatRoom)
                .get().getUsersIds();
        var filteredUsers = Arrays.stream(chatUsers)
                .filter(e -> !e.equals(senderId)).toList();

        filteredUsers.forEach(user -> {
            MySQLMessageDto messageDto;
            var userCurrentRoom = "";
            if (sessions.containsKey(user)) {
                userCurrentRoom = sessions.get(user)
                        .getHandshakeHeaders()
                        .get(ROOM_ID_HEADER_KEY).get(0);
            }
            if (!userCurrentRoom.isEmpty() && chatRoom.equals(userCurrentRoom)) {
                messageDto = setMessage(chatRoom,
                        senderId, ///SAVE message in MySql DB
                        user,
                        requestValues.getMessage(),
                        true);
            } else {
                messageDto = setMessage(chatRoom,
                        senderId, ///SAVE message in MySql DB (if receiver offline)
                        user,
                        requestValues.getMessage(),
                        false);
            }
            mySQLMessageService.save(messageDto);

            template.convertAndSend(topic.getTopic(),
                    setRedisMessage(user,
                            requestValues)); //Send message to Redis
            String response = String.format("response from server to '%s'", session);
            log.info("\nServer received: {}\nServer sends: {}", request, response);
            try {
                session.sendMessage(new TextMessage(response)); /// Send response Confirm message
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {
        log.info("User disconnected, session id {}", session.getId());
        sessions.remove(session);
    }

    private Object getUserId(WebSocketSession session) {
        return Objects.requireNonNull(session
                        .getHandshakeHeaders()
                        .get(ID_HEADER_KEY))
                .get(0);
    }

    private MySQLMessageDto setMessage(String chatRoomId,
                                       String sender,
                                       String receiver,
                                       String message,
                                       boolean isRead) {
        return MySQLMessageDto.builder()
                .message(message)
                .chatRoomId(chatRoomId)
                .receiver(receiver)
                .sender(sender)
                .isRead(isRead)
                .build();
    }

    private String setRedisMessage(String user, SocketMessageDto requestValues) {
        var redisMessage = new RedisMessageDto();
        redisMessage.setReceiver(user);
        redisMessage.setMessage(requestValues.getMessage());
        return gson.toJson(redisMessage);
    }

    private boolean containValidHeaders(WebSocketSession session) {
        var handshakeHeaders = session.getHandshakeHeaders();
        return handshakeHeaders.containsKey(ID_HEADER_KEY)
                && handshakeHeaders.containsKey(ROOM_ID_HEADER_KEY);
    }
}

