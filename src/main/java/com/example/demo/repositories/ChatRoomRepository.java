package com.example.demo.repositories;

import com.example.demo.models.ChatRoomDto;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomRepository extends CrudRepository<ChatRoomDto, String> {
}
