package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.MoveDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovePlayerDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovementDto;
import md.faf223.mafiaplatformgatewayservice.responses.MovementsResponse;
import md.faf223.mafiaplatformgatewayservice.responses.PlayerMovementsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MovementServiceCommunication extends BaseCommunication {

    public MovementServiceCommunication(@Value("${TOWN_SERVICE_HOST}") String baseUrl,
                                        @Value("${TOWN_SERVICE_PORT}") String port) {
        super(baseUrl, port, "MovementCommunication");
    }

    public List<MovementDto> getAllMovements(String lobbyId) {
        MovementsResponse response = makeGetRequest(
                String.format("/movements/%s", lobbyId),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getMovements();
    }

    public PlayerMovementsResponse getMovementByPlayer(String lobbyId, String playerId) {
        return makeGetRequest(
                String.format("/movements/%s/%s", lobbyId, playerId),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public MoveDto movePlayer(MovePlayerDto request) {
        return makePostRequest(
                "/movements/move",
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}