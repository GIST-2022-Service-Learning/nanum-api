package com.nanum.market.repository.chat;

import com.nanum.market.model.User;
import com.nanum.market.model.chat.ChatRoom;
import com.nanum.market.model.chat.ChatRoomJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomJoinRepository extends JpaRepository<ChatRoomJoin,Long> {
    List<ChatRoomJoin> findByUser(User user);
    List<ChatRoomJoin> findByChatRoom(ChatRoom chatRoom);
}
