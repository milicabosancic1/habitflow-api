-- Android je uveo color, weeklyTarget i replacementText na Habit (lokalno) pre nego
-- sto ih je backend znao da cuva/vrati - sync round-trip bi ih tiho izbrisao (server
-- ih nikad ne bi upamtio, pa bi ih sledecim pull-om prepisao sa null). Dodajemo ih
-- da server postane potpuni izvor istine za sva polja koja Android sinhronizuje.

ALTER TABLE habits ADD COLUMN color VARCHAR(10);
ALTER TABLE habits ADD COLUMN weekly_target INTEGER;
ALTER TABLE habits ADD COLUMN replacement_text VARCHAR(500);
