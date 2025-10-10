package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDto {
    private String id;
    private String lobbyId;
    private String content;
    private LocalDateTime timestamp;
}