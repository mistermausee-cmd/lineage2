package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;

//By Evil_dnk

public class SuperionHeliosGatekeeperInstance extends NpcInstance
{
	public SuperionHeliosGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("enter_superion"))
		{
			QuestState qs = player.getQuestState(10857);
			if(qs != null && qs.isCompleted())
				player.teleToLocation(78040, 181112, -10126, player.getReflection());  //TODO CHECK
			else if(player.getLevel() > 99)
				player.teleToLocation(79895, 152614, 2304, player.getReflection());
			else
				return;
		}
		else if(command.equalsIgnoreCase("enter_heillos"))
		{
			showChatWindow(player, "default/34222-1.htm", false); //TODO EPIC HELLIOS
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(player.getLevel() > 101)
		{
			if(player.getRace() == Race.ERTHEIA)
				showChatWindow(player, "default/34222-3.htm", firstTalk);
			else
				showChatWindow(player, "default/34222.htm", firstTalk);
		}
		else
			showChatWindow(player, "default/34222-4.htm", firstTalk);
	}
}