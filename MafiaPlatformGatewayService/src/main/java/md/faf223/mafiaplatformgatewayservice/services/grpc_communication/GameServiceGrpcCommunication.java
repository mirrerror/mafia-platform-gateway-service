package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.pad.lab.grpc.GameServiceGrpc;
import com.pad.lab.grpc.GameServiceOuterClass;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.game.*;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import md.faf223.mafiaplatformgatewayservice.services.GameServiceCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameServiceGrpcCommunication extends BaseGrpcCommunication {

    private final GameServiceCacheService cacheService;

    public GameServiceGrpcCommunication(
            DiscoveryServiceClient discoveryServiceClient,
            @Autowired(required = false) GameServiceCacheService cacheService) {
        super("game-service", discoveryServiceClient);
        this.cacheService = cacheService;
    }

    /**
     * Create a new game lobby
     */
    public LobbyCreateResponseDto createLobby(LobbyCreateDto createDto) {
        GameServiceOuterClass.LobbyResponse grpcResponse = executeGrpcCall(channel -> {
            GameServiceGrpc.GameServiceBlockingStub stub = GameServiceGrpc.newBlockingStub(channel);

            GameServiceOuterClass.CreateLobbyRequest request = GameServiceOuterClass.CreateLobbyRequest.newBuilder()
                    .setHostId(createDto.getHostId())
                    .setLobbyName(createDto.getLobbyName())
                    .setMaxPlayers(createDto.getMaxPlayers())
                    .build();

            return stub.createLobby(request);
        });

        // Check for error
        if (!grpcResponse.getSuccess()) {
            throw new MicroserviceException(400,
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                            grpcResponse.getErrorCode(), grpcResponse.getErrorMessage()));
        }

        // Convert to DTO
        LobbyCreateResponseDto.LobbyData data = new LobbyCreateResponseDto.LobbyData(
                grpcResponse.getGameId(),
                grpcResponse.getLobbyId(),
                grpcResponse.getHostId(),
                grpcResponse.getStatus(),
                grpcResponse.getJoinCode()
        );
        return new LobbyCreateResponseDto(data);
    }

    /**
     * Join an existing lobby
     */
    public LobbyJoinResponseDto joinLobby(Long lobbyId, Long userId, String username) {
        GameServiceOuterClass.JoinResponse grpcResponse = executeGrpcCall(channel -> {
            GameServiceGrpc.GameServiceBlockingStub stub = GameServiceGrpc.newBlockingStub(channel);

            GameServiceOuterClass.JoinLobbyRequest request = GameServiceOuterClass.JoinLobbyRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setUserId(userId)
                    .setUsername(username != null ? username : "")
                    .build();

            return stub.joinLobby(request);
        });

        // Check for error
        if (!grpcResponse.getSuccess()) {
            throw new MicroserviceException(400,
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                            grpcResponse.getErrorCode(), grpcResponse.getErrorMessage()));
        }

        // Convert to DTO
        LobbyJoinResponseDto.LobbyJoinData data = new LobbyJoinResponseDto.LobbyJoinData(
                grpcResponse.getLobbyId(),
                grpcResponse.getCurrentPlayers(),
                grpcResponse.getMaxPlayers()
        );
        return new LobbyJoinResponseDto(data);
    }

    /**
     * Start the game from a lobby
     */
    public GameStartResponseDto startGame(Long lobbyId, Long hostId) {
        GameServiceOuterClass.StartGameResponse grpcResponse = executeGrpcCall(channel -> {
            GameServiceGrpc.GameServiceBlockingStub stub = GameServiceGrpc.newBlockingStub(channel);

            GameServiceOuterClass.StartGameRequest request = GameServiceOuterClass.StartGameRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setHostId(hostId)
                    .build();

            return stub.startGame(request);
        });

        // Check for error
        if (!grpcResponse.getSuccess()) {
            throw new MicroserviceException(400,
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                            grpcResponse.getErrorCode(), grpcResponse.getErrorMessage()));
        }

        // Convert players list
        List<GameStartResponseDto.PlayerInfo> players = grpcResponse.getPlayersList().stream()
                .map(p -> new GameStartResponseDto.PlayerInfo(p.getPlayerId(), p.getUsername()))
                .collect(Collectors.toList());

        // Convert to DTO
        GameStartResponseDto.GameStartData data = new GameStartResponseDto.GameStartData(
                grpcResponse.getGameId(),
                grpcResponse.getStatus(),
                players
        );
        return new GameStartResponseDto(data);
    }

    /**
     * Get the current game state
     */
    public GameStateResponseDto getGameState(Long gameId) {
        // Try to get from cache first
        if (cacheService != null) {
            GameStateResponseDto cached = cacheService.getCachedGameState(gameId);
            if (cached != null) {
                log.info("Cache hit for game state: {}", gameId);
                return cached;
            }
        }

        GameServiceOuterClass.GameStateResponse grpcResponse = executeGrpcCall(channel -> {
            GameServiceGrpc.GameServiceBlockingStub stub = GameServiceGrpc.newBlockingStub(channel);

            GameServiceOuterClass.GameIdRequest request = GameServiceOuterClass.GameIdRequest.newBuilder()
                    .setGameId(gameId)
                    .build();

            return stub.getGameState(request);
        });

        // Check for error
        if (!grpcResponse.getSuccess()) {
            throw new MicroserviceException(404,
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                            grpcResponse.getErrorCode(), grpcResponse.getErrorMessage()));
        }

        // Convert to DTO
        GameStateResponseDto.GameStateData data = new GameStateResponseDto.GameStateData(
                grpcResponse.getGameId(),
                grpcResponse.getPhase(),
                grpcResponse.getDayNumber(),
                grpcResponse.getPlayersAliveList(),
                grpcResponse.getTotalPlayers()
        );
        GameStateResponseDto result = new GameStateResponseDto(data);

        // Cache the result
        if (cacheService != null) {
            cacheService.cacheGameState(gameId, result);
        }

        return result;
    }

    /**
     * Get players status in a game
     */
    public PlayersStatusResponseDto getPlayersStatus(Long gameId) {
        // Try to get from cache first
        if (cacheService != null) {
            PlayersStatusResponseDto cached = cacheService.getCachedPlayersStatus(gameId);
            if (cached != null) {
                log.info("Cache hit for players status: {}", gameId);
                return cached;
            }
        }

        GameServiceOuterClass.PlayersStatusResponse grpcResponse = executeGrpcCall(channel -> {
            GameServiceGrpc.GameServiceBlockingStub stub = GameServiceGrpc.newBlockingStub(channel);

            GameServiceOuterClass.GameIdRequest request = GameServiceOuterClass.GameIdRequest.newBuilder()
                    .setGameId(gameId)
                    .build();

            return stub.getPlayersStatus(request);
        });

        // Check for error
        if (!grpcResponse.getSuccess()) {
            throw new MicroserviceException(404,
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                            grpcResponse.getErrorCode(), grpcResponse.getErrorMessage()));
        }

        // Convert players list
        List<PlayersStatusResponseDto.PlayerStatus> players = grpcResponse.getPlayersList().stream()
                .map(p -> new PlayersStatusResponseDto.PlayerStatus(
                        p.getPlayerId(),
                        p.getUsername(),
                        p.getStatus()))
                .collect(Collectors.toList());

        // Convert to DTO
        PlayersStatusResponseDto.PlayersStatusData data = new PlayersStatusResponseDto.PlayersStatusData(players);
        PlayersStatusResponseDto result = new PlayersStatusResponseDto(data);

        // Cache the result
        if (cacheService != null) {
            cacheService.cachePlayersStatus(gameId, result);
        }

        return result;
    }
}
