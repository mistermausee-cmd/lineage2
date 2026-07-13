package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

public class MentorGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public MentorGuideInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("changediplom"))
		{
			if(ItemFunctions.deleteItem(player, 33800, 1)) 
			{
				ItemFunctions.addItem(player, 33805, 40); 
				showChatWindow(player, 0, false);
				return;
			}
			else
			{
				showChatWindow(player, "mentoring/menthelper-no.htm", false);
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
	    return "mentoring/";
	}
	  
	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(val == 0)
			return "menthelper.htm";
		return "menthelper-" + val + ".htm";
	}
}