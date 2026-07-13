package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;


public class FishermanInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public FishermanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "fisherman/";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("FishingSkillList"))
			showFishingSkillList(player);
		else
			super.onBypassFeedback(player, command);
	}
}