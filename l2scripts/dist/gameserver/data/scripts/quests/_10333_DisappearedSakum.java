package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Quest "Disappeared Sakum"
 *
 * @author 
 */
public class _10333_DisappearedSakum extends Quest
{
	private static final int BATHIS = 30332;
	private static final int VENT = 33176;
	private static final int SCHUNAIN = 33508;

	private static final int LANGK_LIZARDMAN = 20030;
	private static final int VUKU_ORC_FIGHTER = 20017;
	private static final int POISONOUS_SPIDER = 23094;
	private static final int VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_PREDATOR = 20050;

	private static final int SUSPICIOUS_MARK = 17583;

	private static final int EXP_REWARD = 180000;
	private static final int SP_REWARD = 43;

	public _10333_DisappearedSakum()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(BATHIS);
		addTalkId(VENT, SCHUNAIN);
		addKillNpcWithLog(2, "LANGK_LIZARDMAN", 7, LANGK_LIZARDMAN);
		addKillNpcWithLog(2, "VUKU_ORC_FIGHTER", 5, VUKU_ORC_FIGHTER);
		addKillId(POISONOUS_SPIDER, VENOMOUS_SPIDER, ARACHNID_PREDATOR);
		addQuestItem(SUSPICIOUS_MARK);
		addRaceCheck("0-nc.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("0-nc.htm", 18/*, 40*/);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		int cond = qs.getCond();
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "bathis_q10333_5.htm";
			qs.setCond(1);
		}
		else if(cond == 1 && event.equalsIgnoreCase("vent_accept"))
		{
			htmltext = "vent_q10333_3.htm";
			qs.setCond(2);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "schunain_q10333_3.htm";
			qs.addExpAndSp(EXP_REWARD, SP_REWARD);
			qs.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = qs.getCond();
		switch(npcId)
		{
			case BATHIS:
				if(cond == 0)
					htmltext = "bathis_q10333_1.htm";
				else
					htmltext = "bathis_q10333_taken.htm";
				break;
			case VENT:
				if(cond == 1)
					htmltext = "vent_q10333_1.htm";
				break;
			case SCHUNAIN:
				if(cond == 3)
					htmltext = "schunain_q10333_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs.getCond() != 2)
			return "";

		switch(npcId)
		{
			case POISONOUS_SPIDER:
			case VENOMOUS_SPIDER:
			case ARACHNID_PREDATOR:
				if(!qs.haveQuestItem(SUSPICIOUS_MARK, 5))
					qs.rollAndGive(SUSPICIOUS_MARK, 1, 100);
				break;
		}

		if(updateKill(npc, qs) && qs.haveQuestItem(SUSPICIOUS_MARK, 5))
		{
			qs.unset("LANGK_LIZARDMAN");
			qs.unset("VUKU_ORC_FIGHTER");
			qs.setCond(3);
		}
		return "";
	}
}
