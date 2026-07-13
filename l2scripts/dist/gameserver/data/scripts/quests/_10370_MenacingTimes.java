package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _10370_MenacingTimes extends Quest
{

	private static final int Orven = 30857; // Human
	private static final int Winoin = 30856; // Elf
	private static final int Otlin = 30862; // D elf
	private static final int Ladana = 30865; // Orc
	private static final int Ferris = 30847; // Dwarf
	private static final int Brom = 32221; // Kamael

	private static final int andreig = 31292;
	private static final int gerkenshtein = 33648;

	private static final int[] mobs = {21647, 21649, 21650};

	private static final int chance = 25;
	private static final int Ashes = 34765;

	private static final int EXP_REWARD = 22451400;	private static final int SP_REWARD = 5388; 	public _10370_MenacingTimes()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Orven);
		addStartNpc(Winoin);
		addStartNpc(Otlin);
		addStartNpc(Ladana);
		addStartNpc(Ferris);
		addStartNpc(Brom);
		addTalkId(gerkenshtein);
		addTalkId(Orven);
		addTalkId(Winoin);
		addTalkId(Otlin);
		addTalkId(Ladana);
		addTalkId(Ferris);
		addTalkId(Brom);
		addTalkId(andreig);
		addKillId(mobs);
		addQuestItem(Ashes);

		addLevelCheck(Orven, "highpriest_orven_q10370_03.htm", 76/*, 81*/);
		addClassLevelCheck(Orven, "highpriest_orven_q10370_04.htm", false, ClassLevel.THIRD);
		addClassIdCheck(Orven, "highpriest_orven_q10370_04.htm", 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);

		addLevelCheck(Winoin, NO_QUEST_DIALOG, 76/*, 81*/);
		addClassLevelCheck(Winoin, NO_QUEST_DIALOG, false, ClassLevel.THIRD);
		addClassIdCheck(Winoin, NO_QUEST_DIALOG, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);

		addLevelCheck(Otlin, "grandmaster_oltlin_q10370_03.htm", 76/*, 81*/);
		addClassLevelCheck(Otlin, "grandmaster_oltlin_q10370_04.htm", false, ClassLevel.THIRD);
		addClassIdCheck(Otlin, "grandmaster_oltlin_q10370_04.htm", 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);

		addLevelCheck(Ladana, "high_prefect_ladanza_q10370_03.htm", 76/*, 81*/);
		addClassLevelCheck(Ladana, "high_prefect_ladanza_q10370_04.htm", false, ClassLevel.THIRD);
		addClassIdCheck(Ladana, "high_prefect_ladanza_q10370_04.htm", 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);

		addLevelCheck(Ferris, NO_QUEST_DIALOG, 76/*, 81*/);
		addClassLevelCheck(Ferris, NO_QUEST_DIALOG, false, ClassLevel.THIRD);
		addClassIdCheck(Ferris, NO_QUEST_DIALOG, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);

		addLevelCheck(Brom, NO_QUEST_DIALOG, 76/*, 81*/);
		addClassLevelCheck(Brom, NO_QUEST_DIALOG, false, ClassLevel.THIRD);
		addClassIdCheck(Brom, NO_QUEST_DIALOG, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);
	}

	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case Orven:
				return player.getRace() == Race.HUMAN;
			case Winoin:
				return player.getRace() == Race.ELF;
			case Otlin:
				return player.getRace() == Race.DARKELF;
			case Ladana:
				return player.getRace() == Race.ORC;
			case Ferris:
				return player.getRace() == Race.DWARF;
			case Brom:
				return player.getRace() == Race.KAMAEL;
		}
		return true;
	}

	@Override
	public boolean checkTalkNpc(NpcInstance npc, QuestState st)
	{
		return checkStartNpc(npc, st.getPlayer());
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac") || event.equalsIgnoreCase("grandmaster_oltlin_q10370_07.htm") || event.equalsIgnoreCase("high_prefect_ladanza_q10370_07.htm") || event.equalsIgnoreCase("highpriest_orven_q10370_07.htm"))
		{
			st.setCond(1);
		} 
		else if(event.equalsIgnoreCase("captain_andrei_q10370_03.htm"))
		{
			st.setCond(2);
		} 
		else if(event.equalsIgnoreCase("gerkenstein_q10370_02.htm"))
		{
			st.setCond(3);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		int classid = player.getClassId().getId();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == Orven)
		{
			if(cond == 0)
				htmltext = "highpriest_orven_q10370_01.htm";
			else if(cond == 1)
				htmltext = "highpriest_orven_q10370_08.htm";
		}
		else if(npcId == Winoin)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";

		}
		else if(npcId == Otlin)
		{
			if(cond == 0)
				htmltext = "grandmaster_oltlin_q10370_01.htm";
			else if(cond == 1)
				htmltext = "grandmaster_oltlin_q10370_08.htm";
		}
		else if(npcId == Ladana)
		{
			if(cond == 0)
				htmltext = "high_prefect_ladanza_q10370_01.htm";
			else if(cond == 1)
				htmltext = "high_prefect_ladanza_q10370_08.htm";
		}
		else if(npcId == Ferris)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";

		}
		else if(npcId == Brom)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";

		}
		else if(npcId == andreig)
		{
			if(cond == 1)
				htmltext = "captain_andrei_q10370_01.htm";
			else if(cond == 2)
				htmltext = "captain_andrei_q10370_04.htm";
		} 
		else if(npcId == gerkenshtein)
		{
			if(cond == 2)
				htmltext = "gerkenstein_q10370_01.htm";
			else if(cond == 3)
				htmltext = "gerkenstein_q10370_04.htm";
			else if(cond == 4)
			{
				htmltext = "gerkenstein_q10370_05.htm";
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(57, 479620);
				st.takeAllItems(Ashes);
				st.finishQuest();
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == Orven)
			htmltext = "highpriest_orven_q10370_05.htm";
		else if(npcId == Winoin)
			htmltext = "0-c.htm";
		else if(npcId == Otlin)
			htmltext = "grandmaster_oltlin_q10370_05.htm";
		else if(npcId == Ladana)
			htmltext = "high_prefect_ladanza_q10370_05.htm";
		else if(npcId == Ferris)
			htmltext = "0-c.htm";
		else if(npcId == Brom)
			htmltext = "0-c.htm";
		else if(npcId == andreig)
			htmltext = "1-c.htm";
		else if(npcId == gerkenshtein)
			htmltext = "gerkenstein_q10370_03.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(st.getCond() == 3 && ArrayUtils.contains(mobs, npcId) && st.getQuestItemsCount(Ashes) < 30)
		{
			st.rollAndGive(Ashes, 1, chance);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(Ashes) >= 30)
		{
			st.setCond(4);
		}
		return null;
	}
}