package md.faf223.mafiaplatformgatewayservice.services;

import md.faf223.mafiaplatformgatewayservice.dtos.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GameServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(GameServiceClient.class);
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final GameServiceCacheService cacheService;
    private final String gameServiceUrl;

    public GameServiceClient(
            @Qualifier("downstreamServicesRestTemplate") RestTemplate restTemplate,
            JwtService jwtService,
            GameServiceCacheService cacheService,
            @Value("${services.game.host}") String host,
            @Value("${services.game.port}") String port) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
        this.cacheService = cacheService;
        this.gameServiceUrl = "http://" + host + ":" + port;
    }

    // ================== LOBBY ENDPOINTS ==================

    public LobbyCreateResponseDto createLobby(LobbyCreateDto createDto, String token) {
        String url = gameServiceUrl + "/lobby/";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<LobbyCreateDto> entity = new HttpEntity<>(createDto, headers);
        
        try {
            logger.info("Creating lobby at URL: {}", url);
            logger.info("Request body: {}", createDto);
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                url, 
                entity, 
                String.class
            );
            logger.info("Received response status: {}", rawResponse.getStatusCode());
            logger.info("Received raw response body: {}", rawResponse.getBody());
            
            // Now try to parse it
            ResponseEntity<LobbyCreateResponseDto> response = restTemplate.postForEntity(
                url, 
                entity, 
                LobbyCreateResponseDto.class
            );
            logger.info("Parsed response body: {}", response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error creating lobby: {}", ex.getMessage());
            throw ex;
        }
    }

    public LobbyJoinResponseDto joinLobby(Long lobbyId, String token) {
        String url = gameServiceUrl + "/lobby/" + lobbyId + "/join";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Long userId = null;
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            userId = authData.getUserId();
            headers.set("X-User-Id", String.valueOf(userId));
            headers.set("X-Username", authData.getUsername());
        }
        
        // Create request body with userId
        LobbyJoinDto requestBody = new LobbyJoinDto(userId);
        HttpEntity<LobbyJoinDto> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<LobbyJoinResponseDto> response = restTemplate.postForEntity(
                url, 
                entity, 
                LobbyJoinResponseDto.class
            );
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error joining lobby: {}", ex.getMessage());
            throw ex;
        }
    }

    public GameStartResponseDto startGame(Long lobbyId, String token) {
        String url = gameServiceUrl + "/lobby/" + lobbyId + "/start";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Long hostId = null;
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            hostId = authData.getUserId();
            headers.set("X-User-Id", String.valueOf(hostId));
            headers.set("X-Username", authData.getUsername());
        }
        
        // Create request body with hostId
        GameStartDto requestBody = new GameStartDto(hostId);
        HttpEntity<GameStartDto> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<GameStartResponseDto> response = restTemplate.postForEntity(
                url, 
                entity, 
                GameStartResponseDto.class
            );
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error starting game: {}", ex.getMessage());
            throw ex;
        }
    }

    // ================== GAME ENDPOINTS ==================

    public GameStateResponseDto getGameState(Long gameId, String token) {
        // Try to get from cache first
        GameStateResponseDto cached = cacheService.getCachedGameState(gameId);
        if (cached != null) {
            logger.info("Cache hit for game state: {}", gameId);
            return cached;
        }
        
        String url = gameServiceUrl + "/game/" + gameId + "/state";
        
        HttpHeaders headers = new HttpHeaders();
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<GameStateResponseDto> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                entity,
                GameStateResponseDto.class
            );
            
            // Cache the result
            GameStateResponseDto result = response.getBody();
            cacheService.cacheGameState(gameId, result);
            
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error getting game state: {}", ex.getMessage());
            throw ex;
        }
    }

    public PlayersStatusResponseDto getPlayersStatus(Long gameId, String token) {
        // Try to get from cache first
        PlayersStatusResponseDto cached = cacheService.getCachedPlayersStatus(gameId);
        if (cached != null) {
            logger.info("Cache hit for players status: {}", gameId);
            return cached;
        }
        
        String url = gameServiceUrl + "/game/" + gameId + "/players/status";
        
        HttpHeaders headers = new HttpHeaders();
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<PlayersStatusResponseDto> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                entity,
                PlayersStatusResponseDto.class
            );
            
            // Cache the result
            PlayersStatusResponseDto result = response.getBody();
            cacheService.cachePlayersStatus(gameId, result);
            
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error getting players status: {}", ex.getMessage());
            throw ex;
        }
    }

    public PlayerStatusUpdateResponseDto updatePlayerStatus(
            Long gameId, 
            Long playerId, 
            PlayerStatusUpdateDto updateDto, 
            String token
    ) {
        String url = gameServiceUrl + "/game/" + gameId + "/players/" + playerId + "/status";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<PlayerStatusUpdateDto> entity = new HttpEntity<>(updateDto, headers);
        
        try {
            ResponseEntity<PlayerStatusUpdateResponseDto> response = restTemplate.exchange(
                url, 
                HttpMethod.PUT,
                entity,
                PlayerStatusUpdateResponseDto.class
            );
            
            // Invalidate related caches since player status changed
            cacheService.evictPlayersStatus(gameId);
            cacheService.evictGameState(gameId);
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error updating player status: {}", ex.getMessage());
            throw ex;
        }
    }

    public GameEventsResponseDto getGameEvents(Long gameId, String token) {
        // Try to get from cache first
        GameEventsResponseDto cached = cacheService.getCachedGameEvents(gameId);
        if (cached != null) {
            logger.info("Cache hit for game events: {}", gameId);
            return cached;
        }
        
        String url = gameServiceUrl + "/game/" + gameId + "/events";
        
        HttpHeaders headers = new HttpHeaders();
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<GameEventsResponseDto> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                entity,
                GameEventsResponseDto.class
            );
            
            // Cache the result
            GameEventsResponseDto result = response.getBody();
            cacheService.cacheGameEvents(gameId, result);
            
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error getting game events: {}", ex.getMessage());
            throw ex;
        }
    }

    public PlayersRolesResponseDto getPlayersRoles(Long gameId, String token) {
        // Try to get from cache first
        PlayersRolesResponseDto cached = cacheService.getCachedPlayersRoles(gameId);
        if (cached != null) {
            logger.info("Cache hit for players roles: {}", gameId);
            return cached;
        }
        
        String url = gameServiceUrl + "/game/" + gameId + "/players-roles";
        
        HttpHeaders headers = new HttpHeaders();
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<PlayersRolesResponseDto> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                entity,
                PlayersRolesResponseDto.class
            );
            
            // Cache the result
            PlayersRolesResponseDto result = response.getBody();
            cacheService.cachePlayersRoles(gameId, result);
            
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error getting players roles: {}", ex.getMessage());
            throw ex;
        }
    }

    public VoteResponseDto submitVote(Long gameId, VoteDto voteDto, String token) {
        String url = gameServiceUrl + "/game/" + gameId + "/voting";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<VoteDto> entity = new HttpEntity<>(voteDto, headers);
        
        try {
            ResponseEntity<VoteResponseDto> response = restTemplate.postForEntity(
                url, 
                entity, 
                VoteResponseDto.class
            );
            
            // Invalidate game state cache since voting affects game state
            cacheService.evictGameState(gameId);
            cacheService.evictGameEvents(gameId);
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error submitting vote: {}", ex.getMessage());
            throw ex;
        }
    }

    public EliminationResponseDto processElimination(Long gameId, EliminationDto eliminationDto, String token) {
        String url = gameServiceUrl + "/game/" + gameId + "/voting/elimination";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (token != null) {
            GameServiceCacheService.UserAuthData authData = getCachedOrExtractUserData(token);
            headers.set("X-User-Id", String.valueOf(authData.getUserId()));
            headers.set("X-Username", authData.getUsername());
        }
        
        HttpEntity<EliminationDto> entity = new HttpEntity<>(eliminationDto, headers);
        
        try {
            ResponseEntity<EliminationResponseDto> response = restTemplate.postForEntity(
                url, 
                entity, 
                EliminationResponseDto.class
            );
            
            // Invalidate all game caches since elimination changes entire game state
            cacheService.evictAllGameCache(gameId);
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error processing elimination: {}", ex.getMessage());
            throw ex;
        }
    }

    // ================== HELPER METHODS ==================

    /**
     * Get user authentication data from cache or extract from token
     * This reduces JWT parsing overhead by caching user data
     */
    private GameServiceCacheService.UserAuthData getCachedOrExtractUserData(String token) {
        if (token == null) {
            return new GameServiceCacheService.UserAuthData(null, null);
        }
        
        // Try to get from cache first
        GameServiceCacheService.UserAuthData cached = cacheService.getCachedUserAuthData(token);
        if (cached != null) {
            logger.debug("Using cached user auth data");
            return cached;
        }
        
        // Cache miss - extract from token
        String username = extractUsernameFromToken(token);
        Long userId = extractUserIdFromToken(token);
        
        // Cache for future use
        cacheService.cacheUserAuthData(token, userId, username);
        
        return new GameServiceCacheService.UserAuthData(userId, username);
    }

    private String extractUsernameFromToken(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
    
    private Long extractUserIdFromToken(String token) {
        try {
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            logger.error("Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }
}
