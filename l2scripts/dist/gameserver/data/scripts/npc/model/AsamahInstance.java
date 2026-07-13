package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

import quests._111_ElrokianHuntersProof;

/**
 * @author VISTALL
 * @date 10:35/24.06.2011
 */
public class AsamahInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int ElrokianTrap = 8763;
	private static final int TrapStone = 8764;

	public AsamahInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("buyTrap"))
		{
			String htmltext = null;
			QuestState ElrokianHuntersProof = player.getQuestState(111);

			if(player.getLevel() >= 75 && ElrokianHuntersProof != null && ElrokianHuntersProof.isCompleted() && ItemFunctions.getItemCount(player, 57) > 1000000)
			{
				if(ItemFunctions.getItemCount(player, ElrokianTrap) > 0)
					htmltext = getNpcId() + "-alreadyhave.htm";
				else
				{
					ItemFunctions.deleteItem(player, 57, 1000000);
					ItemFunctions.addItem(player, ElrokianTrap, 1);
					htmltext = getNpcId() + "-given.htm";
				}

			}
			else
				htmltext = getNpcId() + "-cant.htm";

			showChatWindow(player, "default/" + htmltext, false);
		}
		else if(command.equals("buyStones"))
		{
			String htmltext = null;
			QuestState ElrokianHuntersProof = player.getQuestState(111);

			if(player.getLevel() >= 75 && ElrokianHuntersProof != null && ElrokianHuntersProof.isCompleted() && ItemFunctions.getItemCount(player, 57) > 1000000)
			{
				ItemFunctions.deleteItem(player, 57, 1000000);
				ItemFunctions.addItem(player, TrapStone, 100);
				htmltext = getNpcId() + "-given.htm";
			}
			else
				htmltext = getNpcId() + "-cant.htm";

			showChatWindow(player, "default/" + htmltext, false);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
