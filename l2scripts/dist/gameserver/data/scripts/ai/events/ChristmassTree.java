package ai.events;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;

public class ChristmassTree extends DefaultAI
{
	public ChristmassTree(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return true;

		int skillId = 2139;
		for(Player player : World.getAroundPlayers(actor, 200, 200))
			if(player != null && !player.isInPeaceZone() && !player.getAbnormalList().contains(skillId))
				actor.doCast(SkillHolder.getInstance().getSkillEntry(skillId, 1), player, true);
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}