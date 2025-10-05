package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

@Data
public class ChatMessage {
    private long senderId;
    private String senderName;
    private String content;
}