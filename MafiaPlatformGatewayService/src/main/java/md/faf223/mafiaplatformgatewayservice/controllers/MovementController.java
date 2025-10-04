package md.faf223.mafiaplatformgatewayservice.controllers;


import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.MoveDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovePlayerDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovementDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.MovementsResponse;
import md.faf223.mafiaplatformgatewayservice.responses.PlayerMovementsResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.MovementCommunication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/town/movements")
public class MovementController {

    private final MovementCommunication communication;

    @GetMapping("/{lobbyId}")
    private ApiResponse<MovementsResponse> getAllMovements(@PathVariable String lobbyId) {
        List<MovementDto> movements = communication.getAllMovements(lobbyId);
        return new ApiResponse<>(new MovementsResponse(movements));
    }

    @GetMapping("/{lobbyId}/{playerId}")
    private ApiResponse<PlayerMovementsResponse> getMovementByPlayer(@PathVariable String lobbyId, @PathVariable String playerId) {
        PlayerMovementsResponse movements = communication.getMovementByPlayer(lobbyId, playerId);
        return new ApiResponse<>(movements);
    }

    @PostMapping("/move")
    private ApiResponse<MoveDto> movePlayer(@RequestBody MovePlayerDto movementDto) {
        MoveDto movement = communication.movePlayer(movementDto);
        return new ApiResponse<>(movement);
    }
}
