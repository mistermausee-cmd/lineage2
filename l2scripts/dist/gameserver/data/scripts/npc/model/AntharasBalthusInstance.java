package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.PositionUtils;

//By Evil_dnk

public class AntharasBalthusInstance extends ReflectionBossInstance
{

	public AntharasBalthusInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isDeathImmune()
	{
		return true;
	}
}