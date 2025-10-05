package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;
import java.util.Map;

@Data
public class LobbyDto {
    private String id;
    private Map<String, PrivateChannelResponseDto> privateChannels;
}