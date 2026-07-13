package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _491_InNominePatris extends Quest
{
	//Шанс дропа
	private static final int chance = 50;
	//Квест итем
	private static final int Fragment = 34768;
	//Монстры
	private static final int[] mobstohunt = {23181, 23182, 23183, 23184};
	//НПСы
	private static final int sirik = 33649;

	public _491_InNominePatris()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(sirik);
		addTalkId(sirik);
		addKillId(mobstohunt);
		addQuestItem(Fragment);

		addLevelCheck("0-nc.htm", 76/*, 81*/);
		addClassIdCheck("0-nc.htm", 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);
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
		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "0-7.htm";
			st.takeAllItems(Fragment);
			st.finishQuest();
			if(Rnd.chance(50))
				st.addExpAndSp(19000000, 2132);
			else
				st.addExpAndSp(14000000, 1517);

		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		Player player = st.getPlayer();
		int classid = player.getClassId().getId();

		if(npcId == sirik)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-5.htm";
			else if(cond == 2)
				htmltext = "0-6.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == sirik)
			htmltext = "0-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(st.getCond() == 1 && ArrayUtils.contains(mobstohunt, npcId))
		{
			if(st.rollAndGive(Fragment, 1, 1, 50, chance))
				st.setCond(2);
		}
		return null;
	}
}