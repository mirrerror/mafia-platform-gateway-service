package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrivateChatResponse {
    private String lobbyId;
    private String channelName;
    private long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
}