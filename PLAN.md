# Coding Dojo — Implementation Plan

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 4.0.3, Java 25, WebSocket (STOMP) |
| **Frontend** | React + Vite, TypeScript, TailwindCSS, shadcn/ui |
| **Database** | PostgreSQL, Spring Data JPA, Liquibase migrations |
| **Testing** | JUnit 5 + Mockito (unit), SpringBootTest + Testcontainers (integration), Playwright (E2E) |

## Architecture

```
┌─────────────────────────────────────┐
│         React SPA (Vite)            │
│  TypeScript + Tailwind + shadcn/ui  │
│  STOMP client (/ws) for real-time   │
└──────────┬──────────────────────────┘
           │ REST + WebSocket
┌──────────▼──────────────────────────┐
│       Spring Boot Server            │
│                                     │
│  ┌─────────┐  ┌──────────────────┐  │
│  │ REST API│  │ WebSocket (STOMP)│  │
│  │ /api/*  │  │ /ws → /topic/*   │  │
│  └────┬────┘  └───────┬──────────┘  │
│       │               │             │
│  ┌────▼───────────────▼──────────┐  │
│  │       Game Engine Core        │  │
│  │  GameLoop · GameManager       │  │
│  │  GameSession · EngineRegistry │  │
│  └────────────┬──────────────────┘  │
│               │                     │
│  ┌────────────▼──────────────────┐  │
│  │   Game Implementations        │  │
│  │   🐍 SnakeEngine (first)      │  │
│  │   🎮 Future games (pluggable) │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌────────────────┐ ┌────────────┐  │
│  │ Room & Player  │ │ Stats &    │  │
│  │ Management     │ │ Leaderboard│  │
│  └───────┬────────┘ └────────────┘  │
└──────────┼──────────────────────────┘
           │
┌──────────▼──────────────────────────┐
│         PostgreSQL                   │
│  Liquibase-managed schema            │
└──────────────────────────────────────┘
```

## User Flow

1. **Create room** → Anyone visits `/`, creates a room, picks game type (snake), gets shareable link
2. **Join** → Friends open `/rooms/{code}`, enter name, receive auth token
3. **Connect client** → Player clones language-specific client repo, pastes token into config
4. **Game runs** → Server runs tick-based game loop; clients poll REST for state & submit moves; viewers watch via WebSocket on the web page
5. **Late join** → New players can register during a running game. They appear on the game field and leaderboard only after the server receives their first API request (state poll or move). Until then they are registered but inactive — this prevents ghost entries from players who registered but never connected a client.
6. **Game ends** → After N rounds, leaderboard + funny stats displayed

## Epics & Tasks

### Epic 1: Domain Models & Core Configuration `P0`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-jcv` | **1.1 Player model** — UUID id, name, token, roomId. Lombok `@Data`/`@Builder`. | `model` | 2.2, 1b.3 |
| `codingdojo-gci` | **1.2 Room model** — UUID id, code (short), name, hostPlayerId, status enum (WAITING/RUNNING/FINISHED), gameType, settings map, playerIds list | `model` | 1.3, 2.1, 1b.3 |
| `codingdojo-3a7` | **1.3 GameState base model** — Abstract/sealed base: roomId, roundNumber, tickNumber, status, playerStates map. Concrete games extend. | `model` | 1.4, 1.5, 3.1, 4.1, 7.1 |
| `codingdojo-z75` | **1.4 Round model** — roundNumber, mapConfig ref, duration, speed, scores map | `model` | — |
| `codingdojo-jer` | **1.5 MoveRequest / MoveResponse DTOs** | `model.dto` | — |
| `codingdojo-5rb` | **1.6 GameConfig properties class** — `@ConfigurationProperties(prefix = "dojo.game")`, tick rate, max players, round count. Update `application.yaml`. | `config` | — |
| `codingdojo-pn2` | **1.7 Jackson JSON configuration** — JavaTimeModule, camelCase naming convention | `config` | — |

### Epic 1b: PostgreSQL Persistence Layer `P0`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-67e` | **1b.1 Enable JPA + PostgreSQL + Flyway + Testcontainers in pom.xml** — Uncomment JPA, add pg driver, Flyway, Testcontainers deps | `pom.xml` | 1b.2 |
| `codingdojo-6hh` | **1b.2 Datasource & Flyway config in application.yaml** — spring.datasource, spring.jpa, spring.flyway. Test profile uses Testcontainers. | `config` | 1b.3, 1b.4 |
| `codingdojo-m2m` | **1b.3 JPA entities + repositories for Player & Room** — `@Entity` annotations, Spring Data repos, V1 Flyway migration SQL | `model` | 2.1, 2.2 |
| `codingdojo-cei` | **1b.4 Testcontainers base configuration** — `@TestConfiguration` with `@ServiceConnection` for PostgresContainer. `AbstractIntegrationTest` base class. | `test` | 8.1 |

### Epic 2: Room & Player Management `P0`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-13v` | **2.1 RoomService** — CRUD via RoomRepository. Generate short-code + link. | `room` | 2.2, 2.3, 3.4 |
| `codingdojo-enj` | **2.2 PlayerService** — Register player → token. Validate token. JPA-backed. | `room` | 2.3, 2.4, 2.5 |
| `codingdojo-5q8` | **2.3 RoomController** — `POST /api/rooms`, `GET /api/rooms/{code}`, `POST /api/rooms/{code}/join` | `api` | 2.6, 5.2 |
| `codingdojo-a09` | **2.4 TokenAuthFilter** — `OncePerRequestFilter`, reads Bearer token on `/api/game/**` | `api.auth` | 5.1 |
| `codingdojo-ft3` | **2.5 Unit tests for RoomService / PlayerService** — JUnit 5 + Mockito (mock repos) + integration tests (Testcontainers) | `test` | — |
| `codingdojo-pk3` | **2.6 WebMvcTest for RoomController** — Slice test with MockMvc | `test` | — |

### Epic 3: Game Engine Core `P0`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-dk7` | **3.1 GameEngine interface** — `initState()`, `tick()`, `isRoundOver()` contract | `engine` | 3.2, 3.6, 4.3 |
| `codingdojo-jir` | **3.2 GameSession class** — Holds live GameState, collects moves thread-safely | `engine` | 3.3 |
| `codingdojo-4g2` | **3.3 GameLoop service** — ScheduledExecutorService tick loop, broadcasts state | `engine` | 3.4, 3.7, 5.4, 7.2 |
| `codingdojo-ca2` | **3.4 GameManager service** — Full lifecycle orchestrator: start → N rounds → finalize | `engine` | 3.7, 5.1, 5.2 |
| `codingdojo-qra` | **3.5 MapConfig model + YAML loader** — Load map files from `classpath:maps/` | `engine.map` | 4.4 |
| `codingdojo-89p` | **3.6 GameEngineRegistry** — Collects GameEngine beans by name (strategy pattern) | `engine` | — |
| `codingdojo-3u0` | **3.7 Unit tests for GameLoop and GameManager** — Mock GameEngine, test tick/round/game-over | `test` | — |

### Epic 4: Snake Game Implementation `P1`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-6dg` | **4.1 SnakeGameState** — Extends GameState: grid, snake positions, food, direction, alive/dead | `game.snake` | 4.3 |
| `codingdojo-7w8` | **4.2 SnakeMove enum** — UP, DOWN, LEFT, RIGHT, NONE | `game.snake` | 4.3 |
| `codingdojo-b4m` | **4.3 SnakeEngine** — Implements GameEngine. Move, collide, eat, grow, score. Bean named "snake". | `game.snake` | 4.5 |
| `codingdojo-3xn` | **4.4 Snake map configs** — YAML files: `classic.yaml` (20×20), `obstacles.yaml` (30×30 with walls) | `resources/maps/snake/` | — |
| `codingdojo-dcu` | **4.5 Unit tests for SnakeEngine** — Collisions, food, growth, scoring, edge cases | `test` | — |

### Epic 5: Game REST API + WebSocket `P1`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-yab` | **5.0 WebSocket STOMP configuration** — `spring-boot-starter-websocket`, STOMP config, `/ws` endpoint, `/topic/room/{code}` | `config` | 5.4 |
| `codingdojo-407` | **5.1 GameController** — `GET /api/game/{roomCode}/state`, `POST /api/game/{roomCode}/move` (token-protected) | `api` | 5.5, 8.1, 8.4, 8.5 |
| `codingdojo-a6t` | **5.2 AdminController** — `POST /api/rooms/{code}/start`, `POST /api/rooms/{code}/stop` (host only) | `api` | — |
| `codingdojo-zjw` | **5.3 Global error handling** — `@RestControllerAdvice` for GameNotFound, InvalidMove, Unauthorized → JSON | `api.error` | — |
| `codingdojo-xve` | **5.4 GameBroadcastService** — Publishes GameState to `/topic/room/{code}` via SimpMessagingTemplate each tick | `api` | 6.5 |
| `codingdojo-eha` | **5.5 WebMvcTest for GameController + AdminController** | `test` | — |

### Epic 6: React Front-End `P1`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-0je` | **6.1 Scaffold React + Vite + TS + Tailwind + shadcn/ui** — `frontend/` dir, Vite proxy to :8080 | `frontend/` | 6.2, 6.7 |
| `codingdojo-jig` | **6.2 React Router + layout + API client** — Routes: `/`, `/rooms/:code`, `/rooms/:code/play`, `/rooms/:code/results` | `frontend/src/` | 6.3, 6.4, 6.5, 6.6 |
| `codingdojo-viq` | **6.3 Home page** — Create room form, open rooms list. shadcn Card/Button/Input/Select. | `frontend/src/pages/` | — |
| `codingdojo-eue` | **6.4 Lobby page** — Room info, live player list (WebSocket), join form, copy token, Start button | `frontend/src/pages/` | 8.3 |
| `codingdojo-0ba` | **6.5 Game viewer page** — Canvas rendering Snake grid real-time via STOMP subscription | `frontend/src/pages/` | 8.3 |
| `codingdojo-qmy` | **6.6 Results page** — Leaderboard table, podium, funny stat cards | `frontend/src/pages/` | — |
| `codingdojo-79z` | **6.7 Serve React build from Spring Boot + CORS** — Production build serving, SPA catch-all forward | `config` | — |

### Epic 7: Leaderboard & Funny Stats `P2`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-y24` | **7.1 GameResult model + entity** — JPA entity, Liquibase changelog, fields: score, rounds survived, kills, food eaten, cause of death | `model` | 7.2 |
| `codingdojo-ssc` | **7.2 StatsCollector service** — Accumulates stats from game events, persists via GameResultRepository | `stats` | 7.3, 7.4 |
| `codingdojo-ux4` | **7.3 FunnyStatsGenerator** — Humorous superlatives: "Most Suicidal 🐍", "Wall Magnet", "Circle of Life Award" | `stats` | 7.4, 7.5 |
| `codingdojo-whk` | **7.4 StatsController** — `GET /api/rooms/{code}/results` (leaderboard + funny stats JSON) | `api` | 8.2 |
| `codingdojo-5bv` | **7.5 Unit tests for stats** | `test` | — |

### Epic 8: Integration Tests, E2E & Polish `P3`

| ID | Task | Package | Blocks |
|----|------|---------|--------|
| `codingdojo-dq3` | **8.1 TestClient utility** — RestClient-based, runs against SpringBootTest + Testcontainers | `test` | 8.2 |
| `codingdojo-2y1` | **8.2 Integration test: full game lifecycle** — Create room → bots join → play N rounds → verify results | `test` | — |
| `codingdojo-ri3` | **8.3 Playwright E2E tests** — Frontend flows: create room, join, copy token, game viewer, results | `frontend/e2e/` | — |
| `codingdojo-49z` | **8.4 Rate-limiting / anti-cheat** — One move per tick per player, reject duplicates (429) | `api` | — |
| `codingdojo-7xx` | **8.5 API documentation** — springdoc-openapi + Swagger UI at `/swagger-ui.html` | `config` | — |

## Critical Path

```
Phase 1 — Foundation (P0, can parallelize)
  ├── 1.1 Player model ──┐
  ├── 1.2 Room model ─────┤
  ├── 1.6 GameConfig ─────┤ (no deps, start immediately)
  ├── 1.7 Jackson config ─┤
  ├── 1b.1 Enable JPA ────┤
  │                       │
  │   1.2 → 1.3 GameState base → 1.4 Round, 1.5 DTOs
  │   1b.1 → 1b.2 Datasource → 1b.3 JPA entities → 1b.4 Testcontainers
  │
Phase 2 — Room/Player + Engine (P0)
  │   1b.3 → 2.1 RoomService → 2.2 PlayerService → 2.3 RoomController → 2.4 TokenAuth
  │   1.3 → 3.1 GameEngine → 3.2 Session → 3.3 Loop → 3.4 Manager
  │         3.5 MapConfig (parallel)
  │         3.6 EngineRegistry (parallel)
  │
Phase 3 — Snake + API + Frontend scaffold (P1, parallel tracks)
  │   Track A: 4.1–4.5 Snake implementation
  │   Track B: 5.0–5.5 REST API + WebSocket
  │   Track C: 6.1–6.4 React scaffold + pages
  │
Phase 4 — Viewer + Stats (P2)
  │   6.5 Game viewer (needs 5.4 broadcast)
  │   7.1–7.5 Leaderboard + funny stats
  │   6.6 Results page, 6.7 CORS/serving
  │
Phase 5 — Polish (P3)
      8.1–8.5 Integration tests, Playwright E2E, rate-limiting, Swagger

  ════════════════════════════════════════
  ✅ Playable via REST after Phase 3
  ✅ Watchable in browser after Phase 4
  ✅ Production-ready after Phase 5
```

## Ready to Start Now (no blockers)

These tasks have zero dependencies and can begin immediately:

- `codingdojo-jcv` — 1.1 Player model
- `codingdojo-gci` — 1.2 Room model
- `codingdojo-5rb` — 1.6 GameConfig properties class
- `codingdojo-pn2` — 1.7 Jackson JSON configuration
- `codingdojo-67e` — 1b.1 Enable JPA + PostgreSQL + Liquibase + Testcontainers
- `codingdojo-qra` — 3.5 MapConfig model + YAML loader
- `codingdojo-7w8` — 4.2 SnakeMove enum
- `codingdojo-yab` — 5.0 WebSocket STOMP configuration
- `codingdojo-zjw` — 5.3 Global error handling
- `codingdojo-0je` — 6.1 Scaffold React + Vite + TS + Tailwind + shadcn/ui

