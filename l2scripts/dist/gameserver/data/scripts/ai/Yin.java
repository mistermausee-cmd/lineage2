package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class Yin extends Fighter
{
	// Monster's
	private static final int SPICULA_ELITE_SOLDIER = 23262;	// Элитный Воитель Спикула

	// Other
	private static final int SPICULA_SPAWN_CHANCE = 20;
	private static final int SPICULA_SPAWN_COUNT = 5;
	private static final int SPICULA_DESPAWN_TIME = 180000; // В милисекундах
	private static final int SPICULA_SPAWN_DELAY = 90; // В секундах

	private int _lastSpawnTime = 0;

	public Yin(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getDeathImmunity().start();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(((System.currentTimeMillis() / 1000) - _lastSpawnTime) < SPICULA_SPAWN_DELAY)
			return;

		if(!Rnd.chance(SPICULA_SPAWN_CHANCE))
			return;

		Player player = attacker.getPlayer();
		if(player != null)
			player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_SUMMONED_ELITE_SOLDIERS_THROUGH_THE_CLONE_GENERATOR, 3000, ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));

		for(int i = 1; i <= SPICULA_SPAWN_COUNT; i++)
		{
			NpcInstance npc = NpcUtils.spawnSingle(SPICULA_ELITE_SOLDIER, Location.findPointToStay(getActor(), 100, 150), SPICULA_DESPAWN_TIME);
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 1000);
		}
		_lastSpawnTime = (int) (System.currentTimeMillis() / 1000);
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