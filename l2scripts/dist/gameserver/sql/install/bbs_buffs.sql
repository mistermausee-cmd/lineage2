DROP TABLE IF EXISTS `bbs_buffs`;
CREATE TABLE `bbs_buffs` (
	`id` int(11) NOT NULL auto_increment,
	`char_id` int(11) NOT NULL DEFAULT '0',
	`name` varchar(256) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`skills` varchar(256) NOT NULL DEFAULT '',
	PRIMARY KEY (`id`, `char_id`,`name`)
) ENGINE=MyISAM;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'For Guardian;Стражу', '11523,11529,11530,11532,11517,11518,11519,11520,11521,11522,11565,11566,11567');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'For Berserker;Берсерку', '11524,11529,11530,11532,11517,11518,11519,11520,11521,11522,11565,11566,11567');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'For Magic;Магу', '11525,11529,11530,11532,11517,11518,11519,11520,11521,11522,11565,11566,11567');
