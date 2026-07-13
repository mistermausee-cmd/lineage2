package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:27/15.04.2012
 */
public class UndergroundColiseumTowerInstance extends NpcInstance
{
	public UndergroundColiseumTowerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		setHasChatWindow(false);
		getFlags().getUndying().stop();
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);

		UndergroundColiseumBattleEvent battleEvent = getEvent(UndergroundColiseumBattleEvent.class);
		if(battleEvent == null)
			return;

		battleEvent.cancelResurrects(getTeam());
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return attacker != null && attacker.getTeam() != TeamType.NONE && getTeam() != attacker.getTeam();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return isAttackable(attacker);
	}
}
