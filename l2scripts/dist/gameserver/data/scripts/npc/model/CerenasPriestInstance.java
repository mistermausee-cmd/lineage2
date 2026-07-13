package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import quests._10369_NoblesseSoulTesting;

/**
 * @author Bonux
 */
public final class CerenasPriestInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CerenasPriestInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("enter_to_eva"))
		{
			QuestState qs = player.getQuestState(10369);
			if(qs == null || !qs.isStarted())
				showChatWindow(player, "default/" + getNpcId() + "-no_enter.htm", false);
			else
				qs.getQuest().onEvent("enter_instance", qs, this);
		}
		else
			super.onBypassFeedback(player, command);
	}
}