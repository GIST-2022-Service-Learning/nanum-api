package com.nanum.market.dto.chat;

import com.nanum.market.model.chat.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDetailDto {
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private String receiverName;
    private List<ChatMessage> messages;
    private Long chatRoomId;

}
