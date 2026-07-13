package ai.sevensingsraid;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

/**
 * @author Bonux
**/
public class SealRemnant extends Fighter
{
	private static final int TELEPORT_CHANCE = 5;	// Шанс телепорта монстра при атаке
	private static final int TELEPORT_MIN_HP_PERCENT = 30;	// Минимальное количество HP для телепорта монстра при атаке

	private boolean _inTeleportating = false;

	public SealRemnant(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(!Rnd.chance(TELEPORT_CHANCE))
			return;

		final NpcInstance actor = getActor();

		int hpPercent = (int) (actor.getCurrentHp() / (actor.getMaxHp() / 100.));
		if(hpPercent > TELEPORT_MIN_HP_PERCENT)
			return;

		Spawner spawn = actor.getSpawn();
		if(spawn == null)
			return;

		if(_inTeleportating)
			return;

		_inTeleportating = true;

		actor.broadcastPacket(new MagicSkillUse(actor, actor, 14286, 1, 1000, 0));
		ThreadPoolManager.getInstance().schedule(() ->
		{
			_inTeleportating = false;
			actor.setSpawnRange(spawn.getRandomSpawnRange());
			actor.setSpawnedLoc(actor.getSpawnRange().getRandomLoc(actor.getGeoIndex()));
			actor.teleToLocation(actor.getSpawnedLoc());
		}, 1000L);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return false;
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		//
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}