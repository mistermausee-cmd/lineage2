package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.PositionUtils;

//By Evil_dnk

public class BaylorrbInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;
	private static long damagetotal = 0;


	public BaylorrbInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(getAbnormalList().contains(5225))
		{
			switch (PositionUtils.getDirectionTo(this, attacker))
			{
				case BEHIND:
					if (Rnd.chance(25))
						damage = 0;
					break;
				case SIDE:
					if (Rnd.chance(65))
						damage = 0;
					break;
				case FRONT:
					if (Rnd.chance(90))
						damage = 0;
					break;
			}
		}
		if (damage > 0)
			damagetotal++;

		if(damagetotal > Rnd.get(100, 200))
		{
			getAbnormalList().stop(5225);
			damagetotal = 0;
			//Functions.npcSay(this, NpcString.HOW_DARE_YOU, ChatType.NPC_ALL, 800);
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);

	}

	public boolean isFearImmune() {
		return true;
	}

	public boolean isParalyzeImmune() {
		return true;
	}

	public boolean isLethalImmune() {
		return true;
	}

	public boolean hasRandomWalk() {
		return false;
	}

	public boolean canChampion() {
		return false;
	}
}