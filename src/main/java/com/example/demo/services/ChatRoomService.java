package com.example.demo.services;

import com.example.demo.models.ChatRoomDto;
import com.example.demo.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    public ChatRoomDto save(ChatRoomDto room){
        return chatRoomRepository.save(room);
    }

    public Optional<ChatRoomDto> findById(String id){
        return chatRoomRepository.findById(id);
    }
}
