# HabitFlow API — Backend

REST API za mobilnu aplikaciju za sticanje navika (*Atomic Habits* tema).
Diplomski rad — softversko inženjerstvo (OAS).

## Tehnologije
Java 17 · Spring Boot 3.3 · Spring Web · Spring Data JPA · PostgreSQL ·
Spring Security + JWT · Flyway · Maven

---

## Pokretanje

### Preduslovi
- Java 17+ 
- Maven 
- PostgreSQL (ILI koristi ugrađeni H2 profil — vidi dole)

### Opcija A — Brzi start BEZ PostgreSQL-a (H2 in-memory)
Najlakši način da odmah vidiš da sve radi. Baza je u memoriji (briše se pri gašenju).

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev-h2
```
API je na `http://localhost:8080`. H2 konzola: `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:habitflow`, user: `sa`, bez lozinke).

### Opcija B — Sa PostgreSQL-om (pravi razvoj)
1. Napravi bazu:
   ```sql
   CREATE DATABASE habitflow;
   ```
2. Postavi kredencijale (kopiraj `.env.example` u `.env` ili postavi env varijable):
   `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.
   (Opciono: `ANTHROPIC_API_KEY` za `/api/ai/weekly-insight` — bez njega taj
   endpoint samo vraća `502`, ostatak API-ja radi normalno.)
3. Pokreni:
   ```bash
   mvn spring-boot:run
   ```
   Flyway će automatski napraviti tabele (migracija `V1__init.sql`).

### Kroz IDE
Otvori folder u IntelliJ IDEA ili VS Code (sa Java + Spring ekstenzijama).
Pokreni `HabitFlowApiApplication`. Za H2 profil, dodaj u run konfiguraciju:
`--spring.profiles.active=dev-h2`.

> Ako `mvn` nije instaliran globalno, najlakše je otvoriti projekat u IDE-u koji
> ima ugrađen Maven. Alternativno instaliraj Maven ili koristi Maven wrapper
> (`./mvnw`) ako ga IDE generiše.

---

## Testiranje API-ja
Koristi `requests.http` (VS Code REST Client ili IntelliJ HTTP Client) — sadrži
gotove pozive za registraciju, prijavu, kreiranje navike, itd.

Ili kroz Swagger UI (živa dokumentacija, generisana iz koda):
`http://localhost:8080/swagger-ui.html` — klikni "Authorize" i unesi
`Bearer <TOKEN>` da testiraš zaštićene rute direktno iz browsera.

---

## Struktura
```
controller/  REST endpointi
service/     poslovna logika (uklj. SyncService — last-write-wins)
repository/  Spring Data JPA
entity/      JPA entiteti
dto/         DTO objekti
mapper/      entitet <-> DTO
security/    JWT, filter, rate limiting
config/      OpenAPI/Swagger, HTTP klijent za AI provajdera
exception/   globalno rukovanje greškama
resources/db/migration/  Flyway SQL migracije
```

## Glavni endpointi
Detaljno u `docs/api-spec.md` (pun spisak i uživo u Swagger UI-ju, vidi gore).
- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/google`,
  `POST /api/auth/refresh`, `POST /api/auth/logout`
- `GET/POST /api/habits`, `PUT/DELETE /api/habits/{id}`
- `GET/POST /api/entries`
- `POST /api/sync` — batch sinhronizacija (offline-first)
- `GET /api/recommendations`, `POST /api/recommendations/{id}/dismiss`
- `GET/POST /api/achievements` — bedževi (unlock je idempotentan po tipu)
- `POST /api/ai/weekly-insight` — opcioni personalizovani nedeljni uvid preko
  Anthropic Claude-a (`docs/backend-ai-weekly-insight.md`); zahteva
  `ANTHROPIC_API_KEY`, inače vraća `502` i Android tiho koristi fallback tekst
