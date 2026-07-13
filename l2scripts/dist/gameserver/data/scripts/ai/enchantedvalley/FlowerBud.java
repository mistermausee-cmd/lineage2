package ai.enchantedvalley;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.PositionUtils;

/**
 * @author Bonux
**/
public class FlowerBud extends Fighter
{
	// Monster's
	private static final int ELEGANT_NYMPH_ROSE = 23582;	// Нимфа Роза - Элегантная
	private static final int ELEGANT_NYMPH_LILY = 23583;	// Нимфа Лилия - Элегантная
	private static final int ELEGANT_NYMPH_TULIP = 23584;	// Нимфа Тюльпан - Элегантная
	private static final int ELEGANT_NYMPH_COSMOS = 23585;	// Нимфа Астра - Элегантная

	//Other
	private static final int[] MONSTER_LIST = { ELEGANT_NYMPH_ROSE, ELEGANT_NYMPH_LILY, ELEGANT_NYMPH_TULIP, ELEGANT_NYMPH_COSMOS };

	public FlowerBud(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		NpcInstance npc = NpcUtils.spawnSingle(Rnd.get(MONSTER_LIST), getActor().getLoc(), getActor().getReflection());
		npc.setHeading(PositionUtils.calculateHeadingFrom(npc, killer));
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker == null || actor.isDead())
			return;

		notifyFriends(attacker, skill, damage);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		//
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		//
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
