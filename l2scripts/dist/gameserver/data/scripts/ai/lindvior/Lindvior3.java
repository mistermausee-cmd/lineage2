package ai.lindvior;

import l2s.gameserver.Announcements;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

import spawns.TersiManager;

/**
 * @author iqman
 * @reworked by Bonux
**/
public class Lindvior3 extends LindviorAI
{
	public Lindvior3(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		int progress = getRaidProgress();
		if(progress == 3)
			getActor().setCurrentHp(getActor().getMaxHp() * 0.6, false);
		else if(progress == 5)
			getActor().setCurrentHp(getActor().getMaxHp() * 0.2, false);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 40.0D)
		{
			if(setRaidProgress(4))
				return;
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		Announcements.announceToAll(new ExShowScreenMessage(NpcString.HONORABLE_WARRIORS_HAVE_DRIVEN_OFF_LINDVIOR_THE_EVIL_WIND_DRAGON, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true));
		TersiManager.spawnTersi();
		super.onEvtDead(killer);
	}
}