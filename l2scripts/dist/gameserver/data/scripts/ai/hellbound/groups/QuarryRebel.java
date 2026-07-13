package ai.hellbound.groups;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
 */
public class QuarryRebel extends Fighter
{
	// Monster ID's
	private static final int FIRE_SLAVE_BRIDGET = 19503;
	private static final int FLOX_GOLEM = 19506;
	private static final int EDAN = 19509;

	private static final int DISCIPLINED_DEATHMOZ = 19504;
	private static final int MAGICAL_DEATHMOZ = 19505;
	private static final int DISCIPLINED_FLOXIS = 19507;
	private static final int MAGICAL_FLOXIS = 19508;
	private static final int DISCIPLINED_BELIKA = 19510;
	private static final int MAGICAL_BELIKA = 19511;

	private static final int DISCIPLINED_TANYA = 19513;
	private static final int MAGICAL_SCARLETT = 19514;

	private static final int BERSERK_TANYA = 23379;
	private static final int BERSERK_SCARLETT = 23380;

	// Orher
	private static final int NEXT_SPAWN_TIMER_ID = 100001;
	private static final double GROUP_4_SPAWN_CHANCE = 25; // TODO: Check chance.

	private boolean _lastMagicAttack = false;

	public QuarryRebel(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);

		if(damage > 0 && skill != null && skill.isMagic() && skill.isOffensive())
			_lastMagicAttack = true;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		addTimer(NEXT_SPAWN_TIMER_ID, killer, 500); // TODO: Check delay.
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == NEXT_SPAWN_TIMER_ID)
		{
			Location loc = getActor().getLoc();
			Creature killer = (Creature) arg1;
			switch(getActor().getNpcId())
			{
				case FIRE_SLAVE_BRIDGET:
					spawnNextMob(_lastMagicAttack ? MAGICAL_DEATHMOZ : DISCIPLINED_DEATHMOZ, killer, loc);
					getActor().doDecay();
					break;
				case FLOX_GOLEM:
					spawnNextMob(_lastMagicAttack ? MAGICAL_FLOXIS : DISCIPLINED_FLOXIS, killer, loc);
					getActor().doDecay();
					break;
				case EDAN:
					spawnNextMob(_lastMagicAttack ? MAGICAL_BELIKA : DISCIPLINED_BELIKA, killer, loc);
					getActor().doDecay();
					break;
				case DISCIPLINED_DEATHMOZ:
				case DISCIPLINED_FLOXIS:
				case DISCIPLINED_BELIKA:
					spawnNextMob(DISCIPLINED_TANYA, killer, loc);
					getActor().doDecay();
					break;
				case MAGICAL_DEATHMOZ:
				case MAGICAL_FLOXIS:
				case MAGICAL_BELIKA:
					spawnNextMob(MAGICAL_SCARLETT, killer, loc);
					getActor().doDecay();
					break;
				case DISCIPLINED_TANYA:
					if(Rnd.chance(GROUP_4_SPAWN_CHANCE))
					{
						spawnNextMob(BERSERK_TANYA, killer, loc);
						getActor().doDecay();
					}
					break;
				case MAGICAL_SCARLETT:
					if(Rnd.chance(GROUP_4_SPAWN_CHANCE))
					{
						spawnNextMob(BERSERK_SCARLETT, killer, loc);
						getActor().doDecay();
					}
					break;
			}
		}
	}

	private static void spawnNextMob(int npcId, Creature killer, Location loc)
	{
		NpcInstance npc = NpcUtils.spawnSingle(npcId, loc);
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
	}
}
