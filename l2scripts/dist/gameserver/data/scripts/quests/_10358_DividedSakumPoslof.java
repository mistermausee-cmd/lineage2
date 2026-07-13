package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import quests._10337_SakumImpact;

//By Evil_dnk dev.fairytale-world.ru
public class _10358_DividedSakumPoslof extends Quest
{

	private static final int guild = 31795;
	private static final int lef = 33510;

	private static final int vilan = 20402;
	private static final int zombi = 20458;
	private static final int poslov = 27452;

	private static final String vilan_item = "vilan";
	private static final String zombi_item = "zombi";
	private int killedposlov;

	private static final int EXP_REWARD = 750000;	private static final int SP_REWARD = 180; 	public _10358_DividedSakumPoslof()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(lef);
		addTalkId(guild);
		addTalkId(lef);
		addKillId(poslov);
		addKillNpcWithLog(1, vilan_item, 23, vilan);
		addKillNpcWithLog(1, zombi_item, 20, zombi);

		addLevelCheck(NO_QUEST_DIALOG, 33/*, 40*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10337);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "1-3.htm";
			st.takeAllItems(17585);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		if(event.equalsIgnoreCase("1-3.htm"))
		{
			htmltext = "1-3.htm";
			st.setCond(2);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == lef)
		{
			if(cond == 0)
				htmltext = "0-1.htm";
			else if(cond == 1)
				htmltext = "0-4.htm";
			else if(cond == 2)
			{
				htmltext = "0-5.htm";
				st.giveItems(17585, 1, false);
				st.setCond(3);
			} 
			else if(cond == 3)
				return htmltext;
			else if(cond == 4)
				return htmltext;
			else
				return htmltext;
		} 
		else if(npcId == guild)
		{
			if(cond == 0 || cond == 1 || cond == 2 || cond == 3)
				return htmltext;
			else if(cond == 4)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == guild)
			htmltext = "1-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);

		if(doneKill)
		{
			st.unset(vilan_item);
			st.unset(zombi_item);
			st.setCond(2);
		}

		int npcId = npc.getNpcId();

		if(npcId == poslov && (st.getCond() == 3))
		{
			++killedposlov;
			if(killedposlov >= 1)
			{
				st.setCond(4);
				killedposlov = 0;
			}
		}
		return null;
	}	
}