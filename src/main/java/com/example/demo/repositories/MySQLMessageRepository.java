package com.example.demo.repositories;

import com.example.demo.models.MySQLMessageDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import static java.lang.String.format;

public interface MySQLMessageRepository extends CrudRepository<MySQLMessageDto, String> {

    @Query(value = "SELECT m " +
            "FROM MySQLMessageDto m " +
            "WHERE m.receiver = ?1 AND m.sender = ?2 " +
            "OR m.receiver = ?2 AND m.sender = ?1")
    List<MySQLMessageDto> findByReceiverAndSender(String receiver, String sender);

    List<MySQLMessageDto> findByChatRoomId(String chatRoomId);
}
