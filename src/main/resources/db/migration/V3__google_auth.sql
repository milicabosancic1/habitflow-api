-- Podrska za Google Sign-In: password_hash je vec nullable (V1), samo dodajemo google_id.

ALTER TABLE users ADD COLUMN google_id VARCHAR(255) UNIQUE;
