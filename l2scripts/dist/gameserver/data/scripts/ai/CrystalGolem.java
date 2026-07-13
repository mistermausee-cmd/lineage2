package ai;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.*;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Location;


//By Evil_dnk

public class CrystalGolem extends DefaultAI
{
    private int countfear = 0;

	public CrystalGolem(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(getActor().getFollowTarget() != null)
		{
			getActor().setRunning();
			addTaskMove(Location.findPointToStay(getActor().getLoc(), 100, 100, getActor().getGeoIndex()), true);
			countfear++;
		}

		if(countfear > 8)
		{
			escape();
			countfear = 0;
		}
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}

	private void escape()
	{
		getActor().setFollowTarget(null);
		getActor().setBusy(false);
		getActor().setTitleNpcString(null);
		//setTitleNpcString(NpcString.GIVEN_TO_S1_, player.getName());
		//getActor().setNameNpcString(NpcString.CRYSTALLINE_GOLEM);
		getActor().setTitle("");
		getActor().setNameNpcString(NpcString.RETREAT);
		getActor().broadcastCharInfoImpl(NpcInfoType.TITLE, NpcInfoType.TITLE_NPCSTRINGID, NpcInfoType.NAME, NpcInfoType.NAME_NPCSTRINGID);
		teleportHome();
	}
}
