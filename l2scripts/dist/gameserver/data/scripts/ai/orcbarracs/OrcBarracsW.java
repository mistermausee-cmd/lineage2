package ai.orcbarracs;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

/**
 * AI для ищущих помощи при HP < 50%
 *
 * @author Diamond
 */
public class OrcBarracsW extends Fighter
{
	private long _lastSearch = 0;
	private boolean isSearching = false;
	private HardReference<? extends Creature> _attackerRef = HardReferences.emptyRef();
	static final String[] flood = { "I'll be back", "You are stronger than expected" };
	static final String[] flood2 = { "Help me!", "Alarm! We are under attack!" };

	public OrcBarracsW(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(final Creature attacker, Skill skill, int damage)
	{
		final NpcInstance actor = getActor();
		if(attacker != null && !actor.getFaction().isNone() && actor.getCurrentHpPercents() < 50 && _lastSearch < System.currentTimeMillis() - 15000)
		{
			_lastSearch = System.currentTimeMillis();
			_attackerRef = attacker.getRef();

			if(findHelp())
				return;
			Functions.npcSay(actor, "Anyone, help me!");
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	private boolean findHelp()
	{
		isSearching = false;
		final NpcInstance actor = getActor();
		Creature attacker = _attackerRef.get();
		if(attacker == null)
			return false;

		for(final NpcInstance npc : actor.getAroundNpc(1000, 150))
			if(!actor.isDead() && npc.isInFaction(actor) && !npc.isInCombat())
			{
				clearTasks();
				isSearching = true;
				addTaskMove(npc.getLoc(), true);
				Functions.npcSay(actor, flood[Rnd.get(flood.length)]);
				return true;
			}
		return false;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_lastSearch = 0;
		_attackerRef = HardReferences.emptyRef();
		isSearching = false;
		if (Rnd.chance(50))
		{
			NpcUtils.spawnSingle(23422, getActor().getLoc(), getActor().getReflection());
			killer.sendPacket(new ExShowScreenMessage(NpcString.A_POWERFUL_MONSTER_HAS_COME_TO_FACE_YOU, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
		}
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtArrived()
	{
		NpcInstance actor = getActor();
		if(isSearching)
		{
			Creature attacker = _attackerRef.get();
			if(attacker != null)
			{
				Functions.npcSay(actor, flood2[Rnd.get(flood2.length)]);
				notifyFriends(attacker, null, 100);
			}
			isSearching = false;
			notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
		}
		else
			super.onEvtArrived();
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if(!isSearching)
			super.onEvtAggression(target, aggro);
	}
}