package md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Rumour {
    private long id;
    private String lobbyId;
    private String type;
    private long ownerId;
    private long targetId;
    private String text;
    private LocalDateTime createdAt;
}