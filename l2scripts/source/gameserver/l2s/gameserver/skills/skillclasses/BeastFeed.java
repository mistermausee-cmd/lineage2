package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.FeedableBeastInstance;
import l2s.gameserver.templates.StatsSet;

public class BeastFeed extends Skill
{
	public BeastFeed(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		if(!(target instanceof FeedableBeastInstance))
			return;

		final Player player = activeChar.getPlayer();
		final FeedableBeastInstance beast = (FeedableBeastInstance) target;
		ThreadPoolManager.getInstance().execute(() -> beast.onSkillUse(player, getId()));
	}
}