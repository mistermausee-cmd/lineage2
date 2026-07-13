package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;

import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 13:28/06.03.2011
 */
public class NurkaInstance extends SiegeGuardInstance
{
	private static final long serialVersionUID = 1L;

	public static final SkillEntry SKILL = SkillHolder.getInstance().getSkillEntry(5456, 1);

	public NurkaInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(attacker.getLevel() > (getLevel() + 8) && !attacker.getAbnormalList().contains(SKILL.getTemplate()))
		{
			doCast(SKILL, attacker, false);
			return;
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	public void onDeath(Creature killer)
	{
		SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
		if(siegeEvent == null)
			return;

		siegeEvent.processStep(getMostDamagedClan());

		super.onDeath(killer);

		deleteMe();
	}

	public Clan getMostDamagedClan()
	{
		Player temp = null;

		Map<Player, Integer> damageMap = new HashMap<Player, Integer>();

		for(AggroList.HateInfo info : getAggroList().getPlayableMap().values())
		{
			Playable killer = (Playable)info.attacker;
			int damage = info.damage;
			if(killer.isServitor())
				temp = killer.getPlayer();
			else if(killer.isPlayer())
				temp = (Player) killer;

			if(temp == null || temp.getClan() == null || temp.getClan().getHasHideout() != 0)
				continue;

			if(!damageMap.containsKey(temp))
				damageMap.put(temp, damage);
			else
			{
				int dmg = damageMap.get(temp) + damage;
				damageMap.put(temp, dmg);
			}
		}

		int mostDamage = 0;
		Player player = null;

		for(Map.Entry<Player, Integer> entry : damageMap.entrySet())
		{
			int damage = entry.getValue();
			Player t = entry.getKey();
			if(damage > mostDamage)
			{
				mostDamage = damage;
				player = t;
			}
		}

		return player == null ? null : player.getClan();
	}

	@Override
	public boolean isEffectImmune(Creature effector)
	{
		return true;
	}
}
