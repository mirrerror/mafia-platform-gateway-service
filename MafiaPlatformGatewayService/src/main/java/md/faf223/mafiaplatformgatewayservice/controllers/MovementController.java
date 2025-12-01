package md.faf223.mafiaplatformgatewayservice.controllers;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.MoveDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovePlayerDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovementDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.MovementsResponse;
import md.faf223.mafiaplatformgatewayservice.responses.PlayerMovementsResponse;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.MovementServiceGrpcCommunication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/town/movements")
public class MovementController {

    private final MovementServiceGrpcCommunication communication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @GetMapping("/{lobbyId}")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<MovementsResponse> getAllMovements(@PathVariable String lobbyId) {  // Changed to public
        List<MovementDto> movements = communication.getAllMovements(lobbyId);
        return new ApiResponse<>(new MovementsResponse(movements));
    }

    @GetMapping("/{lobbyId}/{playerId}")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<PlayerMovementsResponse> getMovementByPlayer(@PathVariable String lobbyId, @PathVariable String playerId) {  // Changed to public
        PlayerMovementsResponse movements = communication.getMovementByPlayer(lobbyId, playerId);
        return new ApiResponse<>(movements);
    }

    @PostMapping("/move")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<MoveDto> movePlayer(@RequestBody MovePlayerDto movementDto) {  // Changed to public
        MoveDto movement = communication.movePlayer(movementDto);
        return new ApiResponse<>(movement);
    }
}