# AGENTS.md

## Project Overview

Coding Dojo game server — a multiplayer platform where players code game bots in any language. Spring Boot 4.0.3 on **Java 25** (backend), React + Vite + TypeScript (frontend), PostgreSQL (persistence).

See **[PLAN.md](PLAN.md)** for the full implementation roadmap and task tracker.

## Build & Run

```sh
# Backend (Spring Boot)
./mvnw spring-boot:run          # run the app (port 8080)
./mvnw package                  # build JAR (target/server-0.0.1-SNAPSHOT.jar)
./mvnw test                     # run tests (needs Docker for Testcontainers)

# Frontend (React)
cd frontend && npm install      # first time
cd frontend && npm run dev      # Vite dev server (proxies to :8080)
cd frontend && npm run build    # production build
```

Always use the Maven wrapper (`./mvnw`), not a system `mvn`.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 4.0.3, Java 25, WebSocket (STOMP) |
| **Frontend** | React + Vite, TypeScript, TailwindCSS, shadcn/ui (in `frontend/`) |
| **Database** | PostgreSQL, Spring Data JPA, Flyway migrations |
| **Testing** | JUnit 5 + Mockito (unit), SpringBootTest + Testcontainers (integration), Playwright (E2E) |

## Package Structure

Base package: `me._on.codingdojo.server` (maps to groupId `me.9on.codingdojo`; the `_on` prefix is required because Java identifiers cannot start with a digit).

- Place all new classes under `me._on.codingdojo.server` or sub-packages so Spring component scanning picks them up.
- Source root: `src/main/java/me/_on/codingdojo/server/`
- Test root: `src/test/java/me/_on/codingdojo/server/`

### Sub-packages

| Package | Purpose |
|---------|---------|
| `model` | Domain models, JPA entities, enums |
| `model.dto` | Request/response DTOs |
| `config` | Spring `@Configuration` classes, `@ConfigurationProperties` |
| `room` | Room and player services (business logic) |
| `engine` | Game engine core — loop, session, manager, registry |
| `engine.map` | Map configuration and loader |
| `game.snake` | Snake game implementation |
| `api` | REST controllers |
| `api.auth` | Token auth filter |
| `api.error` | Global error handling |
| `stats` | Leaderboard, stats collection, funny stats |
| `web` | WebSocket broadcast, CORS, SPA serving |

## Key Dependencies & Conventions

| Dependency | Notes |
|---|---|
| `spring-boot-starter-webmvc` | Web layer (REST controllers, MVC) |
| `spring-boot-starter-websocket` | STOMP over WebSocket for real-time game state |
| `spring-boot-starter-data-jpa` | PostgreSQL persistence via Spring Data |
| Flyway | Database migrations (`src/main/resources/db/migration/`) |
| Lombok | Use `@Data`, `@Builder`, etc. Annotation processor is configured in `pom.xml`. |
| JUnit 5 + Mockito | Unit tests (no Spring context) |
| Testcontainers (PostgreSQL) | Integration tests with real DB |
| Playwright | E2E tests for React frontend |

## Testing

- **Unit tests**: JUnit 5 + Mockito, no Spring context. For services with mocked repositories.
- **Slice tests**: `@WebMvcTest(MyController.class)` for focused controller tests with MockMvc.
- **Integration tests**: `@SpringBootTest` + Testcontainers PostgreSQL. Extend `AbstractIntegrationTest`.
- **E2E tests**: Playwright in `frontend/e2e/` — tests full user flows against running server.

## Frontend (`frontend/`)

- React + Vite + TypeScript + TailwindCSS + shadcn/ui
- Vite dev server proxies `/api/*` and `/ws` to Spring Boot on port 8080
- STOMP client (`@stomp/stompjs`) for WebSocket subscriptions
- Production build served as static assets from Spring Boot

## Configuration

- `src/main/resources/application.yaml` — YAML format (not `.properties`).
- Add new config properties in YAML style to this file.
- Game settings under `dojo.game.*` prefix.


## Landing the Plane (Session Completion)

**When ending a work session**, you MUST complete ALL steps below. Work is NOT complete until `git push` succeeds.

**MANDATORY WORKFLOW:**

1. **File issues for remaining work** - Create issues for anything that needs follow-up
2. **Run quality gates** (if code changed) - Tests, linters, builds
3. **Update issue status** - Close finished work, update in-progress items
4. **PUSH TO REMOTE** - This is MANDATORY:
   ```bash
   git pull --rebase
   bd sync
   git push
   git status  # MUST show "up to date with origin"
   ```
5. **Clean up** - Clear stashes, prune remote branches
6. **Verify** - All changes committed AND pushed
7. **Hand off** - Provide context for next session

**CRITICAL RULES:**
- Work is NOT complete until `git push` succeeds
- NEVER stop before pushing - that leaves work stranded locally
- NEVER say "ready to push when you are" - YOU must push
- If push fails, resolve and retry until it succeeds
