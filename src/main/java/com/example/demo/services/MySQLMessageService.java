package com.example.demo.services;

import com.example.demo.models.MySQLMessageDto;
import com.example.demo.repositories.MySQLMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySQLMessageService {

    @Autowired
    MySQLMessageRepository mySQLMessageRepository;

    public void save(MySQLMessageDto message) {
        mySQLMessageRepository.save(message);
    }

    public List<MySQLMessageDto> findMessagesByReceiverAndSender(String chatRoom) {
        return mySQLMessageRepository.findByChatRoomId(chatRoom);
    }
}
