package ai.beleth;

import l2s.gameserver.ai.Ranger;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class LeonelaH extends Ranger
{
	private long _lastSay;

	public LeonelaH(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return true;

		if(!actor.isInCombat() && System.currentTimeMillis() - _lastSay > 5000)
		{
			Functions.npcSay(actor, NpcString.I_DONT_FEEL_ANY_EVIL_FROM_THEM_I_WILL_TAKE_OF_THEM_SO_PLEASE_GO_AND_ATTACK_BELETHS_INCARNATION, ChatType.NPC_ALL, 800);
			_lastSay = System.currentTimeMillis();
		}
		return super.thinkActive();
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}

}