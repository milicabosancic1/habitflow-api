# Model podataka — HabitFlow

Isti konceptualni model važi za Room (Android) i PostgreSQL (backend).
Nazivi polja dati su u camelCase (Kotlin/Java); u PostgreSQL koristiti snake_case.

---

## Enumeracije

- **HabitType**: `BUILD` (izgradnja navike) | `QUIT` (eliminacija navike)
- **TrackingType**: `SIMPLE` (jednim tapom) | `QUANTITY` (količina uz jedinicu, npr. ml) | `NUMERIC` (broj)
- **FrequencyType**: `DAILY` | `SPECIFIC_DAYS` (određeni dani u nedelji) | `TIMES_PER_WEEK`
- **EntryStatus**: `DONE` | `MISSED` | `PARTIAL`
- **SyncStatus** (samo Android/Room): `SYNCED` | `PENDING` | `PENDING_DELETE`
- **RecommendationType**: `OPTIMAL_TIME` | `MAKE_EASIER` | `STREAK_WARNING` | `NEW_HABIT` | `WEEKLY_INSIGHT`
- **AchievementType**: `FIRST_HABIT` | `STREAK_7` | `STREAK_30` | `PERFECT_WEEK` | `COMEBACK` | ...

---

## User

| Polje | Tip | Opis |
|-------|-----|------|
| id | UUID/String | Primarni ključ (generisan na klijentu, da radi offline) |
| email | String (unique) | Email, može biti null za lokalni offline profil |
| passwordHash | String? | Samo na serveru (BCrypt). Null za naloge kreirane preko Google Sign-In. |
| googleId | String? (unique) | Google "sub" claim — postoji ako je nalog povezan sa Google nalogom |
| displayName | String | Prikazno ime |
| identityStatement | String | „Želim da postanem osoba koja…" |
| createdAt | Long (epoch ms) | Vreme kreiranja |

## Habit

| Polje | Tip | Opis |
|-------|-----|------|
| id | UUID/String | PK (generisan na klijentu) |
| userId | String (FK → User) | Vlasnik |
| name | String | Naziv navike |
| category | String | npr. Zdravlje, Učenje, Sport… |
| type | HabitType | BUILD ili QUIT |
| frequencyType | FrequencyType | Kako se ponavlja |
| daysOfWeek | String? | Za SPECIFIC_DAYS, npr. "MO,WE,FR" |
| targetCount | Int | Ciljna vrednost (npr. broj puta / minuta) |
| trackingType | TrackingType | SIMPLE / QUANTITY / NUMERIC (podrazumevano SIMPLE) |
| unit | String? | npr. "ml", "koraka" — samo za QUANTITY |
| incrementAmount | Int? | Korak uvećanja — samo za QUANTITY |
| reminderTime | String? | HH:mm, lokalni podsetnik |
| cueText | String? | „Nakon što __" (habit stacking okidač) |
| stackedAfterHabitId | String? (FK → Habit) | Navika na koju se nadovezuje |
| isArchived | Boolean | Arhivirana |
| createdAt | Long | Vreme kreiranja |
| updatedAt | Long | Za sinhronizaciju (last-write-wins) |
| syncStatus | SyncStatus | Samo Android |

## HabitEntry

| Polje | Tip | Opis |
|-------|-----|------|
| id | UUID/String | PK |
| habitId | String (FK → Habit) | Navika |
| date | String (YYYY-MM-DD) | Datum izvršenja |
| status | EntryStatus | DONE / MISSED / PARTIAL |
| value | Int | Ostvarena vrednost (npr. 15 min) |
| updatedAt | Long | Za sinhronizaciju |
| syncStatus | SyncStatus | Samo Android |

Jedinstvenost: (habitId, date) — jedan zapis po navici po danu.

## Achievement

| Polje | Tip | Opis |
|-------|-----|------|
| id | UUID/String | PK |
| userId | String (FK) | Vlasnik |
| type | AchievementType | Vrsta bedža |
| unlockedAt | Long | Kada je otključan |

## Recommendation

| Polje | Tip | Opis |
|-------|-----|------|
| id | UUID/String | PK |
| userId | String (FK) | Vlasnik |
| habitId | String? (FK) | Vezano za naviku (opciono) |
| type | RecommendationType | Vrsta preporuke |
| message | String | Tekst prikazan korisniku |
| createdAt | Long | Kada je generisana |
| isDismissed | Boolean | Da li ju je korisnik odbacio |

---

## Relacije

- User 1—N Habit
- Habit 1—N HabitEntry
- User 1—N Achievement
- User 1—N Recommendation (opciono → Habit)
- Habit 0..1—N Habit (samoreferenca za habit stacking)

## Napomena o ID-jevima

ID-jevi se generišu na klijentu (UUID) da bi offline kreiranje radilo bez servera.
Server prihvata klijentske ID-jeve. Time se izbegava problem mapiranja
privremenih i serverskih ID-jeva pri sinhronizaciji.
