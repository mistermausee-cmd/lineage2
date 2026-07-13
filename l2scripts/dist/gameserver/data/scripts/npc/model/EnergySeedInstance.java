package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class EnergySeedInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public EnergySeedInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{}

	@Override
	public void showChatWindow(Player player, String filename, boolean firstTalk, Object... replace)
	{}

	@Override
	public void onBypassFeedback(Player player, String command)
	{}

	@Override
	public double getRewardRate(Player player)
	{
		return Config.RATE_DROP_ENERGY_SEED;
	}
}