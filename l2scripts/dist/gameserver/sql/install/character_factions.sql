CREATE TABLE IF NOT EXISTS `character_factions` (
	`char_id` INT NOT NULL,
	`type` TINYINT NOT NULL,
	`progress` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_id`,`type`)
) ENGINE=MyISAM;
