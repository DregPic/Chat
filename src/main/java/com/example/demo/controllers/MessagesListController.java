package com.example.demo.controllers;

import com.example.demo.models.ChatRoomDto;
import com.example.demo.models.MySQLMessageDto;
import com.example.demo.services.ChatRoomService;
import com.example.demo.services.MySQLMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin()
@RequestMapping("messages")
public class MessagesListController {
    @Autowired
    private MySQLMessageService mySQLMessageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping
    public List<MySQLMessageDto> getMessagesList(@RequestParam String chatRoom) {
        return mySQLMessageService
                .findMessagesByReceiverAndSender(chatRoom);
    }

    @PostMapping(value = "/activate")
    public List<MySQLMessageDto> setMessagesAsRead(@RequestParam String chatRoom) {
        var messages = mySQLMessageService
                .findMessagesByReceiverAndSender(chatRoom);
        var unReadMessages = messages.stream()
                .filter(message -> !message.isRead())
                .toList();
        var updatedList = unReadMessages.stream().peek(message -> {
                message.setRead(true);
                mySQLMessageService.save(message);
        }).toList();
        return updatedList;
    }

    @PostMapping(value = "/chatroom")
    public ChatRoomDto createChatRoom(@RequestBody ChatRoomDto room){
        return chatRoomService.save(room);
    }
}
