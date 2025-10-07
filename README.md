# Mafia Platform Gateway Service

## API Endpoints

### All request and response bodies are in **JSON** format.

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

#### Message Destinations:

**Send Global Message:**
- Destination: `/api/app/chat/global/{lobbyId}/send-message`
- Payload:
  ```json
  {
    "senderId": 1,
    "senderName": "John",
    "content": "Hello!"
  }
  ```

**Send Private Message:**
- Destination: `/api/app/chat/private/{lobbyId}/{channelName}/send-message`
- Payload:
  ```json
  {
    "senderId": 1,
    "senderName": "John",
    "content": "Secret message"
  }
  ```

#### Subscription Topics:

**Subscribe to Global Chat:**
- Topic: `/api/topic/chat/global/{lobbyId}`

**Subscribe to Private Chat:**
- Topic: `/api/topic/chat/private/{lobbyId}/{channelName}`

**Subscribe to Announcements:**
- Topic: `/api/topic/chat/announcement/{lobbyId}`

#### WebSocket Error Responses:
- **401 Unauthorized**: Invalid or missing JWT token
- **403 Forbidden**: User not authorized for this channel/lobby
- **404 Not Found**: Lobby or channel does not exist
- **400 Bad Request**: Validation error in message content (same validation rules as REST endpoints)

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