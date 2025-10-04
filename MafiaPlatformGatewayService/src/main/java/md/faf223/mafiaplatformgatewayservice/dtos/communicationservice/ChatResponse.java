package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private String lobbyId;
    private long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
}