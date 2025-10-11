# Mafia Platform Gateway Service

## API Endpoints

### All request and response bodies are in **JSON** format.


---

## User Management Service

#### POST /api/auth/register
Creates a new user account.

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "identification": "string",
  "deviceInfo": "object",
  "location": "string"
}
```

**Success Response (201):**
```json
{
  "data": {
    "id": 1,
    "username": "string" 
  }
}
```

**Error Responses:**
- **409 Conflict**
  ```json
  {
    "error": {
      "code": "USER_ALREADY_EXISTS",
      "message": "Username or email already exists"
    }
  }
  ```
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Password must be at least 8 characters long"
    }
  }
  ```

#### POST /api/auth/login
Authenticates user and returns JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "deviceInfo": "object"
}
```

**Success Response (200):**
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "string"
  }
}
```

**Error Responses:**
- **401 Unauthorized**
  ```json
  {
    "error": {
      "code": "INVALID_CREDENTIALS",
      "message": "Invalid username or password"
    }
  }
  ```

#### GET /api/users/profile/{id}
Retrieves user profile information.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "currency": {
      "diamonds": 50,
      "coins": 250
    }
  }
}
```

**Error Responses:**
- **401 Unauthorized**
  ```json
  {
    "error": {
      "code": "INVALID_TOKEN",
      "message": "Invalid or expired token"
    }
  }
  ```
- **403 Forbidden**
  ```json
  {
    "error": {
      "code": "FORBIDDEN",
      "message": "Not authorized to access this profile"
    }
  }
  ```
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "USER_NOT_FOUND",
      "message": "User not found"
    }
  }
  ```

#### PUT /api/users/currency/{id}
Adds, subtracts or sets a user's currency balance. Internal endpoint for service-to-service calls.

**Request Body:**
```json
{
  "currency": "diamonds",
  "amount": 1,
  "operation": "add"
}
```

**Success Response (200):**
```json
{
  "data": {
    "id": 1,
    "newBalance": 11,
    "transactionId": 1,
    "currency": "diamonds"
  }
}
```

**Error Responses:**
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "INSUFFICIENT_FUNDS",
      "message": "You do not have enough {currency_type} balance"
    }
  }
  ```
  ```json
  {
    "error": {
      "code": "INVALID_AMOUNT",
      "message": "Balance cannot be set to a negative value"
    }
  }
  ```
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "USER_NOT_FOUND",
      "message": "User not found"
    }
  }
  ```

---

## Game Service

#### POST /api/game/lobby
Creates a new game lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "hostId": 1,
  "lobbyName": "string",
  "maxPlayers": 1
}
```

**Success Response (201):**
```json
{
  "data": {
    "gameId": 1,
    "lobbyId": 1,
    "hostId": 1,
    "status": "waiting_for_players",
    "joinCode": 1
  }
}
```

**Error Responses:**
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "INVALID_PLAYER_COUNT",
      "message": "Max players must be between 5 and 30"
    }
  }
  ```

#### POST /api/game/lobby/{lobbyId}/join
Join an existing game lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "id": 1
}
```

**Success Response (200):**
```json
{
  "data": {
    "lobbyId": 1,
    "currentPlayers": 2,
    "maxPlayers": 5
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```
- **409 Conflict**
  ```json
  {
    "error": {
      "code": "LOBBY_FULL",
      "message": "Lobby has reached maximum capacity"
    }
  }
  ```

#### POST /api/game/lobby/{lobbyId}/start
Start the game in the lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "hostId": 1
}
```

**Success Response (200):**
```json
{
  "data": {
    "gameId": 1,
    "status": "started",
    "players": "array"
  }
}
```

**Error Responses:**
- **403 Forbidden**
  ```json
  {
    "error": {
      "code": "NOT_HOST",
      "message": "Only the host can start the game"
    }
  }
  ```
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "INSUFFICIENT_PLAYERS",
      "message": "At least 5 players required to start the game"
    }
  }
  ```

#### GET /api/game/{gameId}/state
Get current game state. Internal endpoint for service-to-service calls.

**Success Response (200):**
```json
{
  "data": {
    "gameId": 1,
    "phase": "day|night|voting|ended",
    "dayNumber": "number",
    "playersAlive": "array",
    "totalPlayers": 10
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "GAME_NOT_FOUND",
      "message": "Game does not exist"
    }
  }
  ```
- **403 Forbidden**
  ```json
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "You are not a player in this game"
    }
  }
  ```

#### GET /api/game/{gameId}/players/status
Get status of each player (alive/not alive). Internal endpoint for service-to-service calls.

**Success Response (200):**
```json
{
  "data": {
    "players": [
      {
        "playerId": 1,
        "username": "string",
        "status": "alive"
      },
      {
        "playerId": 2,
        "username": "string",
        "status": "eliminated"
      }
    ]
  }
}
```

#### PUT /api/game/{gameId}/players/{playerId}/status
Update player status (used by Roleplay Service when players are killed/affected). Internal endpoint for service-to-service calls.

**Request Body:**
```json
{
  "status": "eliminated|alive|protected",
  "cause": "killed_by_mafia|voted_out|protected_by_doctor",
  "dayNumber": 2
}
```

**Success Response (200):**
```json
{
  "data": {
    "id": 1,
    "previousStatus": "alive",
    "newStatus": "eliminated",
    "cause": "killed_by_mafia",
    "dayNumber": 2
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "GAME_NOT_FOUND",
      "message": "Game does not exist"
    }
  }
  ```
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "PLAYER_NOT_FOUND",
      "message": "Player does not exist in this game"
    }
  }
  ```
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "INVALID_STATUS_TRANSITION",
      "message": "Cannot change status from eliminated to alive"
    }
  }
  ```
- **409 Conflict**
  ```json
  {
    "error": {
      "code": "PLAYER_ALREADY_ELIMINATED",
      "message": "Player is already eliminated"
    }
  }
  ```

#### GET /api/game/{gameId}/events
Get game events. Internal endpoint for service-to-service calls.

**Success Response (200):**
```json
{
  "data": {
    "events": [
      {
        "id": 1,
        "type": "elimination",
        "message": "Player X was eliminated"
      }
    ]
  }
}
```

#### GET /api/game/{gameId}/players-roles
Get players and their roles and careers. Internal endpoint for service-to-service calls.

**Success Response (200):**
```json
{
  "data": {
    "players": [
      {
        "playerId": 1,
        "username": "Alice",
        "role": "mafia",
        "career": "banker"
      }
    ]
  }
}
```

#### POST /api/game/{gameId}/voting
Submit voting results.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "targetPlayerId": 5
}
```

**Success Response (200):**
```json
{
  "data": {
    "voteSubmitted": true,
    "targetPlayerId": 5
  }
}
```

**Error Responses:**
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "VOTING_NOT_ACTIVE",
      "message": "Voting phase is not currently active"
    }
  }
  ```
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "PLAYER_NOT_FOUND",
      "message": "Target player does not exist"
    }
  }
  ```
- **409 Conflict**
  ```json
  {
    "error": {
      "code": "ALREADY_VOTED",
      "message": "You have already cast your vote"
    }
  }
  ```

#### POST /api/game/{gameId}/voting/elimination
Receive voted-out player to Game Service. Internal endpoint for service-to-service calls.

**Request Body:**
```json
{
  "gameId": 1,
  "dayNumber": 2,
  "votedOutPlayerId": 13
}
```

**Success Response (200):**
```json
{
  "data": {
    "gameId": 1,
    "dayNumber": 2,
    "votedOutPlayerId": 13
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "GAME_NOT_FOUND",
      "message": "Game does not exist"
    }
  }
  ```
- **409 Conflict**
  ```json
  {
    "error": {
      "code": "ALREADY_NOTIFIED",
      "message": "Elimination has already been sent for this day"
    }
  }
  ```

### WebSocket Events

The Game Service broadcasts real-time events to all connected players using WebSocket connections.

#### WS /api/game/lobby/{lobbyId}/events
Real-time lobby events before game starts.

**Authentication:** JWT token required via query parameter

**Events Broadcasted:**

**Player Joined Lobby:**
```json
{
  "type": "player_joined_lobby",
  "data": {
    "lobbyId": 1,
    "id": 1,
    "username": "string",
    "currentPlayers": 4,
    "maxPlayers": 10
  }
}
```

**Player Left Lobby:**
```json
{
  "type": "player_left_lobby",
  "data": {
    "lobbyId": 1,
    "id": 1,
    "username": "string",
    "currentPlayers": 3
  }
}
```

**Game Starting:**
```json
{
  "type": "game_starting",
  "data": {
    "lobbyId": 1,
    "gameId": 1,
    "countdown": 5,
    "message": "Game starting in 5 seconds..."
  }
}
```

**Error Responses:**
- **4001 - Invalid Token**
  ```json
  {
    "error": {
      "code": "INVALID_TOKEN",
      "message": "JWT token is invalid or expired"
    }
  }
  ```
- **4003 - Access Denied**
  ```json
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "Player is not part of this lobby"
    }
  }
  ```
- **4004 - Lobby Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```
- **4009 - Connection Limit Exceeded**
  ```json
  {
    "error": {
      "code": "CONNECTION_LIMIT_EXCEEDED",
      "message": "Too many connections from this player"
    }
  }
  ```

#### WS /api/game/{gameId}/events
Real-time game events during active gameplay.

**Authentication:** JWT token required via query parameter

**Events Broadcasted:**

**Phase Change:**
```json
{
  "type": "phase_change",
  "data": {
    "gameId": 1,
    "newPhase": "night|day|voting",
    "dayNumber": 2,
    "duration": 300,
    "message": "Night phase has begun."
  }
}
```

**New Day Started:**
```json
{
  "type": "new_day",
  "data": {
    "gameId": 1,
    "dayNumber": 2,
    "phase": "day",
    "message": "Day 2 has begun."
  }
}
```

**Player Elimination:**
```json
{
  "type": "player_elimination",
  "data": {
    "gameId": 1,
    "id": 2,
    "cause": "voted_out|killed_by_mafia",
    "dayNumber": 2,
    "remainingPlayers": 7
  }
}
```

**Game Announcement:**
```json
{
  "type": "game_announcement",
  "data": {
    "gameId": 1,
    "message": "A player was attacked last night but survived!",
    "category": "night_result|system|voting"
  }
}
```

**Role and Career Assignment:**
```json
{
  "type": "role_assignment",
  "data": {
    "gameId": 1,
    "id": 2,
    "role": "mafia|doctor|investigator|villager",
    "career": "teacher|hunter|banker|other"
  }
}
```

**Voting Phase Started:**
```json
{
  "type": "voting_started",
  "data": {
    "gameId": 1,
    "dayNumber": 2
  }
}
```

**Game Ended:**
```json
{
  "type": "game_ended",
  "data": {
    "gameId": 1,
    "winner": "mafia|villagers",
    "winCondition": "mafia_majority|all_mafia_eliminated",
    "survivingPlayers": [
      {
        "id": 1,
        "username": "string",
        "role": "mafia"
      }
    ],
    "totalDays": 3
  }
}
```

**Connection Established:**
```json
{
  "type": "connection_established",
  "data": {
    "gameId": 1,
    "id": 2,
    "message": "Successfully connected to game events"
  }
}
```

**Error Responses:**
- **4001 - Invalid Token**
  ```json
  {
    "error": {
      "code": "INVALID_TOKEN",
      "message": "JWT token is invalid or expired"
    }
  }
  ```
- **4003 - Access Denied**
  ```json
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "Player is not part of this game"
    }
  }
  ```
- **4004 - Game Not Found**
  ```json
  {
    "error": {
      "code": "GAME_NOT_FOUND",
      "message": "Game does not exist"
    }
  }
  ```
- **4010 - Game Not Started**
  ```json
  {
    "error": {
      "code": "GAME_NOT_STARTED",
      "message": "Cannot connect to events before game has started"
    }
  }
  ```
- **4011 - Player Eliminated**
  ```json
  {
    "error": {
      "code": "PLAYER_ELIMINATED",
      "message": "Eliminated players cannot receive game events"
    }
  }
  ```

---

## Character Service

#### GET /api/character/assets/slots
Get list of all available asset slots.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "slots": [
      "HAIR",
      "SHIRT",
      "PANTS",
      "SHOES",
      "ACCESSORY"
    ]
  }
}
```

#### GET /api/character/{playerId}/items
Get list of items for a player.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "items": [
      {
        "id": 1,
        "quantity": 1
      }
    ]
  }
}
```

#### POST /api/character/{playerId}/items
Add item to player's inventory.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "itemId": 1,
  "quantity": 1
}
```

**Success Response (201):**
```json
{
  "data": {
    "itemId": 1,
    "totalQuantity": 2
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "ITEM_NOT_FOUND",
      "message": "Item does not exist"
    }
  }
  ```

#### DELETE /api/character/{playerId}/items/{itemId}
Drop/delete item from inventory.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "itemId": 1,
    "removed": true
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "ITEM_NOT_FOUND",
      "message": "Item not found in inventory"
    }
  }
  ```

#### POST /api/character/{playerId}/items/{itemId}/use
Use an item.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "itemId": 1
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "ITEM_NOT_FOUND",
      "message": "Item not found in inventory"
    }
  }
  ```

#### POST /api/character/{playerId}/assets
Add character asset.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "slot": "hair",
  "assetId": 1
}
```

**Success Response (201):**
```json
{
  "data": {
    "slot": "hair",
    "assetId": 1,
    "equipped": true
  }
}
```

#### GET /api/character/{playerId}/appearance
Get character appearance - list of all assets.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "assets": {
      "hair": 1,
      "shirt": 1,
      "pants": 1,
      "accessories": [
        1,
        2
      ]
    }
  }
}
```

#### PUT /api/character/{playerId}/assets
Update character asset.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "slot": "shirt",
  "assetId": 1
}
```

**Success Response (200):**
```json
{
  "data": {
    "slot": "shirt",
    "previousAssetId": 1,
    "newAssetId": 1
  }
}
```

---

## Town Service

#### GET /api/town/locations
Retrieve all available locations.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "locations": [
      {
        "id": 1,
        "name": "School",
        "description": "Education center"
      }
    ]
  }
}
```

#### GET /api/town/locations/{locationId}
Get details of a specific location.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "id": 1,
    "name": "School",
    "description": "Education center"
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOCATION_NOT_FOUND",
      "message": "Location does not exist"
    }
  }
  ```

#### GET /api/town/movements/{lobbyId}
Get movement of all players.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "movements": [
      {
        "playerId": 1,
        "locationId": 1,
        "timestamp": "2023-10-01T12:00:00Z"
      }
    ]
  }
}
```

#### POST /api/town/move
Movement depending on location and day.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "lobbyId": 1,
  "playerId": 1,
  "locationId": 1
}
```

**Success Response (200):**
```json
{
  "data": {
    "playerId": 1,
    "fromLocationId": 1,
    "toLocationId": 1,
    "timestamp": "2023-10-01T12:00:00Z"
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOCATION_NOT_FOUND",
      "message": "Location does not exist"
    }
  }
  ```

#### GET /api/town/movements/{lobbyId}/{playerId}
Get all movements of a specific player.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "playerId": 1,
    "movements": [
      {
        "locationId": 1,
        "locationName": "School",
        "timestamp": "2023-10-01T12:00:00Z"
      }
    ]
  }
}
```

---

## Communication Service

#### GET /api/chat/lobby/{lobbyId}
Get lobby details including private channels.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "id": "test",
    "privateChannels": {
      "detectives": {
        "name": "detectives",
        "members": {
          "2": true,
          "3": true
        }
      },
      "mafia": {
        "name": "mafia",
        "members": {
          "0": true,
          "1": true
        }
      }
    }
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### POST /api/chat/lobby/create
Create a new lobby with private channels.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "lobbyId": "lobby-123",
  "privateChannels": [
    {
      "channelName": "mafia",
      "memberIds": [1, 2]
    }
  ]
}
```

**Success Response (200):**
```json
{
  "data": {
    "id": "lobby-123",
    "privateChannels": {
      "mafia": {
        "name": "mafia",
        "members": {
          "1": true,
          "2": true
        }
      }
    }
  }
}
```

**Error Responses:**
- **400 Bad Request**
  ```json
  {
    "error": {
      "code": "LOBBY_EXISTS",
      "message": "Lobby already exists"
    }
  }
  ```

#### DELETE /api/chat/lobby/{lobbyId}
Delete a lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "message": "Lobby deleted successfully"
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### POST /api/chat/global/{lobbyId}/send-message
Send a message to global chat.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "senderId": 1,
  "senderName": "John",
  "content": "Hello everyone!"
}
```

**Validation Rules:**
- `senderId`: Required, must be ≥ 0
- `senderName`: Required, 2–50 characters
- `content`: Required, not empty, max 200 characters

**Success Response (200):**
```json
{
  "data": {
    "lobbyId": "lobby-123",
    "senderId": 1,
    "senderName": "John",
    "content": "Hello everyone!",
    "timestamp": "2023-10-01T12:00:00Z"
  }
}
```

**Error Responses:**
- **400 Bad Request - Validation Error**
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Content must not exceed 200 characters"
    }
  }
  ```
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Sender name must be between 2 and 50 characters"
    }
  }
  ```
- **400 Bad Request - Chat Disabled**
  ```json
  {
    "error": {
      "code": "CHAT_DISABLED",
      "message": "Global chat is currently disabled for this lobby"
    }
  }
  ```
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### GET /api/chat/global/{lobbyId}/history
Get global chat history.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": [
    {
      "lobbyId": "lobby-123",
      "senderId": 1,
      "senderName": "John",
      "content": "Hello everyone!",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  ]
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### POST /api/chat/global/{lobbyId}/toggle
Toggle global chat enabled/disabled status.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "lobbyId": "lobby-123",
    "isGlobalChatEnabled": true
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### GET /api/chat/global/{lobbyId}/status
Get global chat status.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": {
    "lobbyId": "lobby-123",
    "isGlobalChatEnabled": true
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### POST /api/chat/private/{lobbyId}/{channelName}/send-message
Send a message to private channel.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "senderId": 1,
  "senderName": "John",
  "content": "Secret message"
}
```

**Validation Rules:**
- `senderId`: Required, must be ≥ 0
- `senderName`: Required, 2–50 characters
- `content`: Required, not empty, max 200 characters

**Success Response (200):**
```json
{
  "data": {
    "lobbyId": "lobby-123",
    "channelName": "mafia",
    "senderId": 1,
    "senderName": "John",
    "content": "Secret message",
    "timestamp": "2023-10-01T12:00:00Z"
  }
}
```

**Error Responses:**
- **400 Bad Request - Validation Error**
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Sender name must be between 2 and 50 characters"
    }
  }
  ```
  ```json
  {
    "error": {
      "code": "VALIDATION_ERROR",
      "message": "Content must not exceed 200 characters"
    }
  }
  ```
- **403 Forbidden**
  ```json
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "You do not have access to this private channel"
    }
  }
  ```
- **404 Not Found - Lobby**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```
- **404 Not Found - Channel**
  ```json
  {
    "error": {
      "code": "CHANNEL_NOT_FOUND",
      "message": "Private channel does not exist"
    }
  }
  ```

#### GET /api/chat/private/{lobbyId}/{channelName}/history
Get private channel chat history.

**Headers:**
- `Authorization: Bearer <token>`

**Query Parameters:**
- `userId` (long) - User ID requesting the history

**Success Response (200):**
```json
{
  "data": [
    {
      "lobbyId": "lobby-123",
      "channelName": "mafia",
      "senderId": 1,
      "senderName": "John",
      "content": "Secret message",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  ]
}
```

**Error Responses:**
- **403 Forbidden**
  ```json
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "You do not have access to this private channel's history"
    }
  }
  ```
- **404 Not Found - Lobby**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```
- **404 Not Found - Channel**
  ```json
  {
    "error": {
      "code": "CHANNEL_NOT_FOUND",
      "message": "Channel does not exist"
    }
  }
  ```

#### GET /api/chat/private/{lobbyId}/channels
Get list of private channels in a lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": [
    "mafia",
    "doctors"
  ]
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### POST /api/chat/announcement/{lobbyId}
Send an announcement to lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "Game starts in 5 minutes!"
}
```

**Validation Rules:**
- `content`: Required, max 200 characters

**Success Response (200):**
```json
{
  "data": {
    "id": "announcement-123",
    "lobbyId": "lobby-123",
    "content": "Game starts in 5 minutes!",
    "timestamp": "2023-10-01T12:00:00Z"
  }
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### GET /api/chat/announcement/{lobbyId}/history
Get announcement history for a lobby.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": [
    {
      "id": "announcement-123",
      "lobbyId": "lobby-123",
      "content": "Game starts in 5 minutes!",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  ]
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

---

## WebSocket Endpoints

Connect to WebSocket at: `/api/ws`

**Connection:**
- Use SockJS client
- Send `Authorization: Bearer <token>` header on CONNECT frame

---

## Message Destinations (Client → Server)

### Send Global Message
- **Destination:** `/api/app/chat/global/{lobbyId}/send-message`
- **Description:** Sends a message to the global chat in the specified lobby
- **Payload:**
  ```json
  {
    "senderId": 1,
    "senderName": "John",
    "content": "Hello!"
  }
  ```

### Send Private Message
- **Destination:** `/api/app/chat/private/{lobbyId}/{channelName}/send-message`
- **Description:** Sends a message to the specified private channel in the lobby
- **Payload:**
  ```json
  {
    "senderId": 1,
    "senderName": "John",
    "content": "Secret message"
  }
  ```

### Join Global Chat
- **Destination:** `/api/app/chat/global/{lobbyId}/join`
- **Description:** Adds the user to the global chat in the specified lobby
- **Payload:** `userId` (long)
  ```json
  1
  ```

### Leave Global Chat
- **Destination:** `/api/app/chat/global/{lobbyId}/leave`
- **Description:** Removes the user from the global chat in the specified lobby
- **Payload:** `userId` (long)
  ```json
  1
  ```

### Join Private Channel
- **Destination:** `/api/app/chat/private/{lobbyId}/{channelName}/join`
- **Description:** Adds the user to the specified private channel
- **Payload:** `userId` (long)
  ```json
  1
  ```

### Leave Private Channel
- **Destination:** `/api/app/chat/private/{lobbyId}/{channelName}/leave`
- **Description:** Removes the user from the specified private channel
- **Payload:** `userId` (long)
  ```json
  1
  ```

---

## Subscription Topics (Server → Client)

### Subscribe to Global Chat
- **Topic:** `/api/topic/chat/global/{lobbyId}`
- **Description:** Receives messages from the global chat
- **Message Format:**
  ```json
  {
    "data": {
      "lobbyId": "lobby-123",
      "senderId": 1,
      "senderName": "John",
      "content": "Hello everyone!",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  }
  ```

### Subscribe to Private Chat
- **Topic:** `/api/topic/chat/private/{lobbyId}/{channelName}`
- **Description:** Receives messages from the specified private channel
- **Message Format:**
  ```json
  {
    "data": {
      "channelName": "mafia",
      "lobbyId": "lobby-123",
      "senderId": 1,
      "senderName": "John",
      "content": "Secret message",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  }
  ```

### Subscribe to Announcements
- **Topic:** `/api/topic/chat/announcement/{lobbyId}`
- **Description:** Receives system announcements for the lobby
- **Message Format:**
  ```json
  {
    "data": {
      "id": "0e3d9373-038e-4d03-a5ea-0cd1c4d648db",
      "lobbyId": "lobby-123",
      "content": "Night has fallen. Discuss your suspicions!",
      "timestamp": "2023-10-01T12:00:00Z"
    }
  }
  ```

### Subscribe to Global Chat Status
- **Topic:** `/api/topic/chat/status/{lobbyId}`
- **Description:** Receives updates when global chat is enabled/disabled
- **Message Format:**
  ```json
  {
    "data": {
      "lobbyId": "lobby-123",
      "isGlobalChatEnabled": true
    }
  }
  ```

---

## WebSocket Error Responses

- **401 Unauthorized**: Invalid or missing JWT token
- **403 Forbidden**: User not authorized for this channel/lobby
- **404 Not Found**: Lobby or channel does not exist
- **400 Bad Request**: Validation error in message content

### Validation Rules for Messages

#### ChatMessage Payload
- `senderId`: Required, must be ≥ 0
- `senderName`: Required, 2–50 characters
- `content`: Required, not empty, max 200 characters


---

## Rumours Service

#### POST /api/rumours/{lobbyId}/purchase
Purchase a rumour about another player.

**Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "rumourType": "activity",
  "senderId": 1,
  "targetId": 2
}
```

**Available rumour types:** `activity`, `appearance`

**Validation Rules:**
- `rumourType`: Required, must be one of: "activity", "appearance"
- `senderId`: Required, must be ≥ 0
- `targetId`: Required, must be ≥ 0

**Success Response (200):**
```json
{
  "data": {
    "id": 1,
    "lobbyId": "lobby-123",
    "type": "activity",
    "ownerId": 1,
    "targetId": 2,
    "text": "Text",
    "createdAt": "2023-10-01T12:00:00Z"
  }
}
```

**Error Responses:**
- **400 Bad Request - Insufficient Funds**
  ```json
  {
    "error": {
      "code": "INSUFFICIENT_FUNDS",
      "message": "Not enough currency to purchase rumour"
    }
  }
  ```
- **404 Not Found - Bad Rumour Type**
  ```json
  {
    "error": {
      "code": "BAD_RUMOURS_TYPE",
      "message": "Rumours type not found"
    }
  }
  ```
- **404 Not Found - Lobby**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

#### GET /api/rumours/{lobbyId}/user/{ownerId}
Get all rumours owned by a specific user.

**Headers:**
- `Authorization: Bearer <token>`

**Success Response (200):**
```json
{
  "data": [
    {
      "id": 1,
      "lobbyId": "lobby-123",
      "type": "activity",
      "ownerId": 1,
      "targetId": 2,
      "text": "Text",
      "createdAt": "2023-10-01T12:00:00Z"
    }
  ]
}
```

**Error Responses:**
- **404 Not Found**
  ```json
  {
    "error": {
      "code": "LOBBY_NOT_FOUND",
      "message": "Lobby does not exist"
    }
  }
  ```

---

## Common Error Codes

All services may return these common error responses:

### 500 Internal Server Error
```json
{
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "An unexpected error occurred"
  }
}
```

### 503 Service Unavailable
```json
{
  "error": {
    "code": "SERVICE_UNAVAILABLE",
    "message": "Service is temporarily unavailable"
  }
}
```

### 503 Concurrency Limit
```json
{
  "error": {
    "code": "CONCURRENCY_LIMIT_REACHED",
    "message": "The service is temporarily overloaded. Please try again later."
  }
}
```

### 503 Database Unavailable
```json
{
  "error": {
    "code": "DATABASE_UNAVAILABLE",
    "message": "Database service is currently unavailable. Please try again later."
  }
}
```

### 408 Request Timeout
```json
{
  "error": {
    "code": "REQUEST_TIMEOUT",
    "message": "The request took too long to process."
  }
}
```
