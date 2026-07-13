package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10372_PurgatoryVolvere extends Quest
{
	private static final int andreig = 31292;
	private static final int gerkenshtein = 33648;
	private static final int Essence = 34766;

	private static final int Bloody = 23185;

	private static final int chance = 10;

	private static final int EXP_REWARD = 23009000;	private static final int SP_REWARD = 5522; 	public _10372_PurgatoryVolvere()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(gerkenshtein);
		addTalkId(gerkenshtein);
		addTalkId(andreig);
		addKillId(Bloody);
		addQuestItem(Essence);

		addLevelCheck(NO_QUEST_DIALOG, 76/*, 81*/);
		addClassIdCheck(NO_QUEST_DIALOG, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10371);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-4.htm";
		} 
		else if(event.equalsIgnoreCase("firec"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9552, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		} 
		else if(event.equalsIgnoreCase("waterc"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9553, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		} 
		else if(event.equalsIgnoreCase("windc"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9555, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		} 
		else if(event.equalsIgnoreCase("earth"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9554, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		} 
		else if(event.equalsIgnoreCase("darkc"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9556, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		} else if(event.equalsIgnoreCase("holyc"))
		{
			htmltext = "1-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(9557, 1);
			st.takeAllItems(Essence);
			st.finishQuest();
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == gerkenshtein)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-5.htm";
			else if(cond == 2) 
			{
				htmltext = "0-6.htm";
				st.setCond(3);
				st.giveItems(34767, 1, false);
				st.takeAllItems(Essence);
			} 
			else if(cond == 3)
				htmltext = "0-3.htm";
		} 
		else if(npcId == andreig)
		{
			if(cond == 3)
			{
				htmltext = "1-1.htm";
				st.takeAllItems(34767);
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == gerkenshtein)
			htmltext = "0-c.htm";
		else if(npcId == andreig)
			htmltext = "1-4.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(st.getCond() == 1 && npcId == Bloody && st.getQuestItemsCount(Essence) < 10)
		{
			st.rollAndGive(Essence, 1, chance);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(Essence) >= 10)
		{
			st.setCond(2);
		}
		return null;
	}		
}