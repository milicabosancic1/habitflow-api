# REST API specifikacija — HabitFlow

Bazna putanja: `/api`
Format: JSON. Autentikacija: JWT u `Authorization: Bearer <token>` (osim auth ruta).
Vremenske oznake: epoch milisekunde (Long). Datumi: `YYYY-MM-DD`.

---

## Autentikacija

### POST /api/auth/register
Telo: `{ "email", "password", "displayName", "identityStatement" }`
Odgovor 201: `{ "userId", "token" }`

### POST /api/auth/login
Telo: `{ "email", "password" }`
Odgovor 200: `{ "userId", "token", "displayName" }`
Greška 401 ako kredencijali nisu ispravni.

---

## Navike

### GET /api/habits
Vraća sve (nearhivirane) navike korisnika.
Odgovor 200: `[ HabitDto, ... ]`

### POST /api/habits
Kreira naviku. Telo: `HabitDto` (bez server-side polja).
Odgovor 201: `HabitDto`

### PUT /api/habits/{id}
Ažurira naviku. Telo: `HabitDto`. Odgovor 200: `HabitDto`.

### DELETE /api/habits/{id}
Arhivira ili briše naviku. Odgovor 204.

---

## Zapisi (entries)

### GET /api/entries?from=YYYY-MM-DD&to=YYYY-MM-DD
Vraća zapise u periodu. Odgovor 200: `[ HabitEntryDto, ... ]`

### POST /api/entries
Beleži izvršenje (upsert po habitId+date).
Telo: `HabitEntryDto`. Odgovor 200/201: `HabitEntryDto`.

---

## Preporuke

### GET /api/recommendations
Vraća aktuelne (neodbačene) preporuke. Odgovor 200: `[ RecommendationDto ]`

### POST /api/recommendations/{id}/dismiss
Označava preporuku kao odbačenu. Odgovor 204.

---

## Sinhronizacija (srce offline-first sistema)

### POST /api/sync
Batch push + pull u jednom pozivu.

**Zahtev:**
```json
{
  "since": 1737000000000,
  "habits":  [ HabitDto sa updatedAt ],
  "entries": [ HabitEntryDto sa updatedAt ]
}
```
- `since` = timestamp poslednje uspešne sinhronizacije klijenta.
- `habits`/`entries` = lokalne izmene (syncStatus PENDING) koje klijent gura na server.

**Obrada na serveru:**
1. Za svaki dolazni zapis: ako ne postoji → ubaci; ako postoji → primeni
   **last-write-wins** (zadrži zapis sa većim `updatedAt`).
2. Prikupi sve zapise korisnika sa `updatedAt > since`.

**Odgovor:**
```json
{
  "serverTime": 1737000005000,
  "habits":  [ HabitDto novije od 'since' ],
  "entries": [ HabitEntryDto novije od 'since' ]
}
```
Klijent primenjuje vraćene promene u Room i pomera svoj `since` na `serverTime`.

---

## DTO oblici

### HabitDto
```json
{
  "id": "uuid",
  "name": "Čitanje",
  "category": "Učenje",
  "type": "BUILD",
  "frequencyType": "DAILY",
  "daysOfWeek": null,
  "targetCount": 1,
  "reminderTime": "20:00",
  "cueText": "Nakon večere",
  "stackedAfterHabitId": null,
  "isArchived": false,
  "createdAt": 1737000000000,
  "updatedAt": 1737000000000
}
```

### HabitEntryDto
```json
{
  "id": "uuid",
  "habitId": "uuid",
  "date": "2026-07-24",
  "status": "DONE",
  "value": 1,
  "updatedAt": 1737000000000
}
```

### RecommendationDto
```json
{
  "id": "uuid",
  "habitId": "uuid",
  "type": "STREAK_WARNING",
  "message": "Juče si propustila čitanje — ne propusti dva puta zaredom!",
  "createdAt": 1737000000000,
  "isDismissed": false
}
```

---

## Greške (jedinstveni format)
```json
{ "timestamp": 1737000000000, "status": 400, "error": "Bad Request", "message": "..." }
```
