package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

import java.util.List;

@Data
public class LobbyCreationDto {
    private String lobbyId;
    private List<PrivateChannelDto> privateChannels;
}