package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _124_MeetingTheElroki extends Quest
{
	//NPC
	public final int Marquez = 32113;
	public final int Mushika = 32114;
	public final int Asamah = 32115;
	public final int Karakawei = 32117;
	public final int Mantarasa = 32118;
	//item
	public final int Mushika_egg = 8778;

	private static final int EXP_REWARD = 1109665;	private static final int SP_REWARD = 266; 	public _124_MeetingTheElroki()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Marquez);
		addTalkId(Mushika);
		addTalkId(Asamah);
		addTalkId(Karakawei);
		addTalkId(Mantarasa);
		addLevelCheck("marquez_q0124_02.htm", 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if(event.equals("marquez_q0124_03.htm"))
		if(event.equals("marquez_q0124_04.htm") && cond == 0)
		{
			st.setCond(1);
		}
		if(event.equals("marquez_q0124_06.htm") && cond == 1)
		{
			st.setCond(2);
		}
		if(event.equals("mushika_q0124_03.htm") && cond == 2)
		{
			st.setCond(3);
		}
		if(event.equals("asama_q0124_06.htm") && cond == 3)
		{
			st.setCond(4);
		}
		if(event.equals("shaman_caracawe_q0124_03.htm") && cond == 4)
			st.set("id", "1");
		if(event.equals("shaman_caracawe_q0124_05.htm") && cond == 4)
		{
			st.setCond(5);
		}
		if(event.equals("egg_of_mantarasa_q0124_02.htm") && cond == 5)
		{
			st.giveItems(Mushika_egg, 1);
			st.setCond(6);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Marquez)
		{
			if(cond == 0)
				htmltext = "marquez_q0124_01.htm";
			else if(cond == 1)
				htmltext = "marquez_q0124_04.htm";
			else if(cond == 2)
				htmltext = "marquez_q0124_07.htm";
		}
		else if(npcId == Mushika && cond == 2)
			htmltext = "mushika_q0124_01.htm";
		else if(npcId == Asamah)
		{
			if(cond == 3)
				htmltext = "asama_q0124_03.htm";
			else if(cond == 6)
			{
				htmltext = "asama_q0124_08.htm";
				st.takeItems(Mushika_egg, 1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(ADENA_ID, 236510);
				st.finishQuest();
			}
		}

		else if(npcId == Karakawei)
		{
			if(cond == 4)
			{
				htmltext = "shaman_caracawe_q0124_01.htm";

				if(st.getInt("id") == 1)
					htmltext = "shaman_caracawe_q0124_03.htm";
				else if(cond == 5)
					htmltext = "shaman_caracawe_q0124_07.htm";
			}
		}
		else if(npcId == Mantarasa && cond == 5)
			htmltext = "egg_of_mantarasa_q0124_01.htm";
		return htmltext;
	}
}