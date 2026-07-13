package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _10288_SecretMission extends Quest
{
	// NPC's
	private static final int _dominic = 31350;
	private static final int _aquilani = 32780;
	private static final int _greymore = 32757;
	// Items
	private static final int _letter = 15529;

	private static final int EXP_REWARD = 417788;	private static final int SP_REWARD = 100; 	public _10288_SecretMission()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(_dominic);
		addStartNpc(_aquilani);
		addTalkId(_dominic);
		addTalkId(_greymore);
		addTalkId(_aquilani);
		addFirstTalkId(_aquilani);
		addLevelCheck("31350-00.htm", 82);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int npcId = npc.getNpcId();
		if(npcId == _dominic)
		{
			if(event.equalsIgnoreCase("31350-05.htm"))
			{
				st.setCond(1);
				st.giveItems(_letter, 1);
			}
		}
		else if(npcId == _greymore && event.equalsIgnoreCase("32757-03.htm"))
		{
			st.takeItems(_letter, -1);
			st.giveItems(57, 106583);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(npcId == _aquilani)
		{
			if(st.isStarted())
			{
				if(event.equalsIgnoreCase("32780-05.htm"))
				{
					st.setCond(2);
				}
			}
			else if(st.isCompleted() && event.equalsIgnoreCase("teleport"))
			{
				st.getPlayer().teleToLocation(118833, -80589, -2688);
				return null;
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == _dominic)
		{
			if(cond == 0)
				htmltext = "31350-01.htm";
			else if(cond == 1)
				htmltext = "31350-06.htm";
			else if(cond == 2)
				htmltext = "31350-07.htm";
		}
		else if(npcId == _aquilani)
		{
			if(cond == 1)
				htmltext = "32780-03.htm";
			else if(cond == 2)
				htmltext = "32780-06.htm";
		}
		else if(npcId == _greymore && cond == 2)
			htmltext = "32757-01.htm";

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == _dominic)
			htmltext = "31350-08.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getId());
		if(st == null)
		{
			newQuestState(player);
			st = player.getQuestState(getId());
		}
		if(npc.getNpcId() == _aquilani)
		{
			if(st.isCompleted())
				return "32780-01.htm";
			else
				return "32780-00.htm";
		}
		return null;
	}
}