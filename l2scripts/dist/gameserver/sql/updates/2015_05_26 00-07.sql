ALTER TABLE character_shortcuts CHANGE `level` `level` INT;
ALTER TABLE character_skills CHANGE `skill_level` `skill_level` INT UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE character_skills_save CHANGE `skill_level` `skill_level` INT UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE character_summons_save CHANGE `skill_level` `skill_level` INT UNSIGNED NOT NULL;