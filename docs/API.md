# CodingDojo Game API Reference

Bot developers use this REST API to interact with the CodingDojo game server.

## Base URL

```
http://localhost:8080/api
```

## Authentication

All requests to game endpoints require an `Authorization` header with the player's auth token:

```
Authorization: <token>
```

Tokens are issued when a player joins a room.

---

## Room Management

### Create Room

**POST** `/room`

Create a new game room.

**Request Body:**
```json
{
  "playerName": "Alice"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "code": "ABC-DEF",
  "status": "waiting",
  "players": [
    {
      "id": "player-uuid",
      "name": "Alice",
      "token": "auth-token-here"
    }
  ],
  "createdAt": 1710800000000
}
```

**Errors:**
- `400 Bad Request` - Invalid input
- `500 Internal Server Error` - Server error

---

### Join Room

**POST** `/room/{roomCode}/join`

Join an existing game room.

**Path Parameters:**
- `roomCode` (string, required) - Room code (e.g., "ABC-DEF")

**Request Body:**
```json
{
  "playerName": "Bob"
}
```

**Response (200 OK):**
```json
{
  "id": "player-uuid",
  "name": "Bob",
  "token": "auth-token-here"
}
```

**Errors:**
- `404 Not Found` - Room not found
- `409 Conflict` - Room is full or game in progress
- `400 Bad Request` - Invalid input

---

### Get Room Details

**GET** `/room/{roomCode}`

Get current room state and all players.

**Path Parameters:**
- `roomCode` (string, required) - Room code

**Response (200 OK):**
```json
{
  "id": "room-uuid",
  "code": "ABC-DEF",
  "status": "waiting",
  "players": [
    {
      "id": "player-uuid-1",
      "name": "Alice",
      "joinedAt": 1710800000000
    },
    {
      "id": "player-uuid-2",
      "name": "Bob",
      "joinedAt": 1710800001000
    }
  ],
  "createdAt": 1710800000000
}
```

**Errors:**
- `404 Not Found` - Room not found

---

## Game Play

### Get Game State

**GET** `/game/{roomCode}/state`

Get the current game state snapshot.

**Path Parameters:**
- `roomCode` (string, required) - Room code

**Headers:**
- `Authorization` (string, required) - Player auth token

**Response (200 OK):**
```json
{
  "state": {
    "roomId": "room-uuid",
    "type": "SnakeGameState",
    "score": 150,
    "snakeBody": [
      {"x": 10, "y": 10},
      {"x": 10, "y": 11},
      {"x": 10, "y": 12}
    ],
    "direction": "RIGHT",
    "food": {"x": 15, "y": 15},
    "timestamp": 1710800005000
  }
}
```

**Errors:**
- `404 Not Found` - Room not found
- `409 Conflict` - Game not started
- `401 Unauthorized` - Invalid token

---

### Submit Move

**POST** `/game/{roomCode}/move`

Submit a move (direction) for the current game.

**Path Parameters:**
- `roomCode` (string, required) - Room code

**Headers:**
- `Authorization` (string, required) - Player auth token

**Request Body:**
```json
{
  "payload": "UP"
}
```

Valid directions: `UP`, `DOWN`, `LEFT`, `RIGHT`

**Response (202 Accepted):**
```
No content
```

Move is queued and will be processed in the next game tick.

**Errors:**
- `400 Bad Request` - Invalid direction
- `404 Not Found` - Room not found
- `409 Conflict` - Game not started
- `401 Unauthorized` - Invalid token
- `429 Too Many Requests` - Rate limit exceeded

---

## Admin Operations

These endpoints are for game controllers (room admins).

### Start Game

**POST** `/admin/game/{roomCode}/start`

Start a game in a room (transition from waiting to in_game).

**Path Parameters:**
- `roomCode` (string, required) - Room code

**Query Parameters:**
- `gameType` (string, optional, default: "snake") - Game type to play
- `mapConfig` (string, optional, default: "snake-20x20") - Map configuration

**Response (200 OK):**
```json
{
  "state": {
    "roomId": "room-uuid",
    "type": "SnakeGameState",
    "score": 0,
    "snakeBody": [{"x": 10, "y": 10}],
    "direction": "RIGHT",
    "food": {"x": 15, "y": 15},
    "timestamp": 1710800010000
  }
}
```

**Errors:**
- `400 Bad Request` - Invalid game type
- `404 Not Found` - Room not found
- `409 Conflict` - Game already started

---

### Stop Game

**POST** `/admin/game/{roomCode}/stop`

Stop a running game and close the room.

**Path Parameters:**
- `roomCode` (string, required) - Room code

**Response (204 No Content):**
```
No content
```

**Errors:**
- `404 Not Found` - Room not found
- `409 Conflict` - Game not running

---

## WebSocket (STOMP)

Real-time game state updates are available via WebSocket.

**Endpoint:** `ws://localhost:8080/ws`

### Subscribe to Game State

```javascript
stompClient.subscribe('/topic/game/{roomId}', (msg) => {
  const update = JSON.parse(msg.body);
  console.log('Game state updated:', update);
});
```

Messages contain:
- `type` - Message type (GAME_STATE, GAME_STARTED, GAME_ENDED)
- `state` - Current GameState
- `timestamp` - Update timestamp

### Subscribe to Room Events

```javascript
stompClient.subscribe('/topic/room/{roomId}', (msg) => {
  const event = JSON.parse(msg.body);
  console.log('Room event:', event);
});
```

Events:
- `PLAYER_JOINED` - New player joined room
- `PLAYER_LEFT` - Player left room

---

## Error Responses

All errors return a JSON response:

```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable error message"
}
```

### Common Error Codes

| Code | Status | Meaning |
|------|--------|---------|
| ROOM_NOT_FOUND | 404 | Room doesn't exist |
| GAME_NOT_STARTED | 409 | Game hasn't started yet |
| INVALID_TOKEN | 401 | Auth token missing or invalid |
| INVALID_GAME_TYPE | 400 | Game type not supported |
| INVALID_REQUEST | 400 | Malformed request |
| INTERNAL_ERROR | 500 | Server error |

---

## Examples

### Example 1: Create Room and Start Game

```bash
# Create room as player "Alice"
curl -X POST http://localhost:8080/api/room \
  -H "Content-Type: application/json" \
  -d '{"playerName": "Alice"}'

# Response:
# {
#   "id": "room-123",
#   "code": "ABC-DEF",
#   "status": "waiting",
#   "players": [...],
#   "createdAt": 1710800000000
# }

# Start the game
curl -X POST "http://localhost:8080/api/admin/game/ABC-DEF/start"
```

### Example 2: Join Room and Play

```bash
# Join room
curl -X POST http://localhost:8080/api/room/ABC-DEF/join \
  -H "Content-Type: application/json" \
  -d '{"playerName": "Bob"}'

# Response: {"id": "player-456", "name": "Bob", "token": "xyz123"}

# Get game state
curl -X GET http://localhost:8080/api/game/ABC-DEF/state \
  -H "Authorization: xyz123"

# Submit a move
curl -X POST http://localhost:8080/api/game/ABC-DEF/move \
  -H "Authorization: xyz123" \
  -H "Content-Type: application/json" \
  -d '{"payload": "UP"}'
```

---

## Rate Limiting

Move submissions are rate-limited to prevent cheating:
- Max 1 move per game tick
- Game tick: 200ms (configurable)

Exceeding the limit returns `429 Too Many Requests`.

---

## Game State Structure (Snake)

For Snake games, the state includes:

```json
{
  "roomId": "room-uuid",
  "type": "SnakeGameState",
  "score": 150,
  "snakeBody": [
    {"x": 10, "y": 10},
    {"x": 10, "y": 11},
    {"x": 11, "y": 11}
  ],
  "direction": "RIGHT",
  "food": {"x": 15, "y": 15},
  "timestamp": 1710800005000
}
```

- `snakeBody` - Array of body segments (head is first)
- `direction` - Current snake direction (UP, DOWN, LEFT, RIGHT)
- `food` - Current food location
- `score` - Points earned

---

## Glossary

- **Room Code** - Human-readable identifier (e.g., "ABC-DEF")
- **Player Token** - Authentication token for game moves
- **Game State** - Snapshot of current game (positions, score, etc.)
- **Game Tick** - Server update cycle (200ms for Snake)
- **STOMP** - Streaming Text Oriented Messaging Protocol (WebSocket)

---

## Support

For issues or questions, see the GitHub repository: https://github.com/ntvf/codingdojo
