package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms_table")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    @Id
    private String id = UUID.randomUUID().toString();
    @Column(nullable = false)
    @JsonProperty(value = "room_name")
    private String roomName;
    @Column
    @CreatedDate
    @JsonProperty(value = "created_at")
    private Date createdAt;
    @Column(nullable = false)
    @JsonProperty(value = "users_ids")
    private String[] usersIds;
}
