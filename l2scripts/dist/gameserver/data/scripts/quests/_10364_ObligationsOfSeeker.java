package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


//By Evil_dnk dev.fairytale-world.ru

public class _10364_ObligationsOfSeeker extends Quest
{
	private static final int celin = 33451;
	private static final int avian = 22994;
	private static final int warper = 22996;
	private static final int dep = 33453;
	private static final int papper = 17578;
	private static final int walter = 33452;

	private static final int EXP_REWARD = 114000;
	private static final int SP_REWARD = 14;

	public _10364_ObligationsOfSeeker() {
		super(PARTY_NONE, ONETIME);
		addStartNpc(celin);
		addTalkId(celin);
		addTalkId(dep);
		addTalkId(walter);
		addKillId(warper, avian);
		addQuestItem(papper);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 14/*, 25*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10363);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-3.htm";
			//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			//536341	u,С помощью Устройства Телепорта Эсагира, помеченного красным цветом, переместитесь в 3-ю Зону Исследования.\0
		}

		if(event.equalsIgnoreCase("papper"))
		{
			st.setCond(2);
			htmltext = "0-3.htm";
			//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			//536442	u,С помощью Устройства Телепорта Эсагира, помеченного красным цветом, переместитесь в 4-ю Зону Исследования.\0
		}

		if(event.equalsIgnoreCase("qet_rev"))
		{
			st.takeAllItems(papper);
			htmltext = "2-4.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(37, 1, false);
			st.finishQuest();
		}
		return htmltext;
	}

	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;


		if(npcId == celin)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-4.htm";
			else
				htmltext = "0-nc.htm";
		}
		else if(npcId == walter)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
			else if(cond == 2)
				htmltext = "1-5.htm";
			else if(cond == 3)
				htmltext = "1-6.htm";
			else
				htmltext = "1-5.htm";
		}
		else if(npcId == dep)
		{
			if(cond == 0)
				htmltext = "2-nc.htm";
			else if(cond == 1)
				return htmltext;
			else if(cond == 2)
				return htmltext;
			else if(cond == 3)
				htmltext = "2-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == celin)
			htmltext = "0-c.htm";
		else if(npcId == walter)
			htmltext = "1-c.htm";
		else if(npcId == dep)
			htmltext = "2-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(st.getCond() == 2 && st.getQuestItemsCount(papper) < 5 && npcId == warper || npcId == avian)
			st.rollAndGive(papper, 1, 35);

		if(st.getQuestItemsCount(papper) >= 5) 
		{
			st.setCond(3);
		}
		return null;
	}
}