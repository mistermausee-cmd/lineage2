package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.ChatUtils;

/**
 * @author Bonux
 * PTS AI: ai_si_illusion_people8
**/
public class Rotina extends DefaultAI
{
	private static NpcString FSTRID_SAY = NpcString.YOULL_EARN_TONS_OF_ITEMS_USING_THE_TRAINING_GROUNDS;
	private static int TID_SAY = 83004;
	private static int TIME_SAY = 10000;
	private static int WEAPONID = 15304;

	private static int BOSS_NPC_ID = 33280;

	private NpcInstance _master = null;

	public Rotina(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().setRHandId(WEAPONID);
		getActor().broadcastCharInfoImpl(NpcInfoType.EQUIPPED);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		for(NpcInstance npc : actor.getAroundNpc(4000, 400))
		{
			if(npc.getNpcId() == BOSS_NPC_ID)
			{
				if(_master == null || actor.getDistance(npc) < actor.getDistance(_master))
					_master = npc;
			}
		}
		if(_master != null)
		{
			actor.setWalking();
			actor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _master, 0);
			addTimer(TID_SAY, TIME_SAY + (Rnd.get(5) * 1000));
		}
		return false;
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == TID_SAY)
		{
			ChatUtils.say(getActor(), FSTRID_SAY);
			addTimer(TID_SAY, TIME_SAY + (Rnd.get(5) * 1000));
		}
		super.onEvtTimer(timerId, arg1, arg2);;
	}
}