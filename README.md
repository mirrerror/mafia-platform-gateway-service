# Mafia Platform Gateway Service


## API Endpoints

### All request and response bodies are in **JSON** format.


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

**Error Response

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

### 503 Internal Server Error
```json
{
  "error": {
    "code": "DATABASE_UNAVAILABLE",
    "message": "Database service is currently unavailable. Please try again later."
  }
}
```
