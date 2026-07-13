DROP TABLE IF EXISTS `raidboss_points`;
ALTER TABLE characters ADD COLUMN `raid_points` INT NOT NULL DEFAULT '0' AFTER `fame`;