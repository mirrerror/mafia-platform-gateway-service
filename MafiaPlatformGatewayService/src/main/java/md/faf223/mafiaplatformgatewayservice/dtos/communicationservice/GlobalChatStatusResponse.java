package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

@Data
public class GlobalChatStatusResponse {
    private String lobbyId;
    private boolean isGlobalChatEnabled;
}