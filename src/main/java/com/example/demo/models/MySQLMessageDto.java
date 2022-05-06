package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "messages_table")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySQLMessageDto {
    @Id
    @JsonIgnore
    private String id = UUID.randomUUID().toString();
    @Column(nullable = false)
    @CreatedDate
    private Date messageTime;
    @Column
    private String message;
    @Column
    private String sender;
    @Column
    private String receiver;
    @Column(nullable = false)
    private boolean isRead;
    @Column(nullable = false)
    private String chatRoomId;

}
