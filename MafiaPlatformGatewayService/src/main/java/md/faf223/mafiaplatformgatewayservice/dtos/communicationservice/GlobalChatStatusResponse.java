package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GlobalChatStatusResponse {

    @JsonProperty("lobbyId")
    private String lobbyId;

    @JsonProperty("isGlobalChatEnabled")
    private boolean isGlobalChatEnabled;

}