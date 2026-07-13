package ai;

import l2s.gameserver.utils.Functions;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

//By Evil_dnk
public class Trapcoridor extends DefaultAI
{
	private boolean activeted = false;

	public Trapcoridor(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getImmobilized().start();
		actor.getFlags().getDamageBlocked().start();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (!actor.getAroundCharacters(160, 150).isEmpty() && !activeted)
		{
			for (Creature obj : actor.getAroundCharacters(160, 150))
			{
				if (obj.isPlayer())
				{
					timerS();
					activeted = true;
				}
			}
		}
		return true;
	}

	private void timerS()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			getActor().doDie(null);
		}, 7000);

		ThreadPoolManager.getInstance().schedule(() ->
		{   Functions.npcSay(getActor(), "0");
			getActor().doCast(SkillHolder.getInstance().getSkillEntry(5422, 9), getActor(), true);
		}, 6000);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			Functions.npcSay(getActor(), "1");
		}, 4500);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			Functions.npcSay(getActor(), "2");
		}, 3000);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			Functions.npcSay(getActor(), "3");
		}, 1500);
	}
}