-- Sprecava duplirane bedzeve istog tipa za istog korisnika (npr. ako vise uredjaja
-- nezavisno prijavi isto otkljucavanje dok su offline) i dodaje indeks koji je
-- V1 propustio da doda za achievements (ostale tabele ga vec imaju).

ALTER TABLE achievements ADD CONSTRAINT uq_achievement_user_type UNIQUE (user_id, type);
CREATE INDEX idx_achievements_user ON achievements(user_id);
