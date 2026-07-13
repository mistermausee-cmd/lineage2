package l2s.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.QuestState;

public class DailyQuestsManager
{
	private static final Logger _log = LoggerFactory.getLogger(DailyQuestsManager.class);
	private static List<Integer> _disabledQuests = new ArrayList<Integer>();

	public static void EngageSystem()
	{
		switch(Rnd.get(1, 3))
		
		{
			case 1:
				_disabledQuests.add(470);
				break;
			case 2:
				_disabledQuests.add(474);
				break;
		}

		switch(Rnd.get(1, 2))
		
		{
			case 1:
				_disabledQuests.add(488);
				break;
			case 2:
				_disabledQuests.add(489);
				break;
		}
		_log.info("Daily Quests Disable Managed: Loaded " + _disabledQuests.size() + " quests in total (4).");
	}

	
	public static void checkAndRemoveDisabledQuests(Player player)
	{
		if(player == null)
			return;

		for(int qId : _disabledQuests)
		{
			QuestState qs = player.getQuestState(qId);
		    if(qs == null)
		    	continue;
		    if(qs.getQuest().checkMaxLevelCondition(player))
		    	continue;
		    qs.abortQuest();
		}
	}

	public static boolean isQuestDisabled(int questId)
	{
		if(_disabledQuests.contains(questId))
			return true;
		return false;
	}
}