-- Tip pracenja navike (SIMPLE/QUANTITY/NUMERIC), potreban za sinhronizaciju izmedju uredjaja/naloga.

ALTER TABLE habits ADD COLUMN tracking_type VARCHAR(20) NOT NULL DEFAULT 'SIMPLE';
ALTER TABLE habits ADD COLUMN unit VARCHAR(50);
ALTER TABLE habits ADD COLUMN increment_amount INTEGER;
