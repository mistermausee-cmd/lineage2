package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Quest "Sakum's Impact"
 *
 * @author Darvin
 */
public class _10337_SakumImpact extends Quest
{
	private static final int GUILDSMAN = 31795;
	private static final int SILVAN = 33178;
	private static final int LEF = 33510;

	private static final int BONE_WARRIOR = 23022;
	private static final int RUIN_IMP = 20506;
	private static final int RUIN_IMP_ELDER = 20507;
	private static final int BATTY = 27458; // Monster: Bat
	private static final int SCAVENGER_BAT = 20411;
	private static final int RUIN_BAT = 23023;

	private static final String WARRIOR = "WARRIOR";
	private static final String IMP = "IMP";
	private static final String BAT = "BAT";

	private static final int EXP_REWARD = 1000000;	private static final int SP_REWARD = 156; 	public _10337_SakumImpact()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GUILDSMAN);
		addTalkId(SILVAN);
		addKillNpcWithLog(2, WARRIOR, 15, BONE_WARRIOR);
		addKillNpcWithLog(2, IMP, 20, RUIN_IMP, RUIN_IMP_ELDER);
		addKillNpcWithLog(2, BAT, 25, BATTY, RUIN_BAT, SCAVENGER_BAT);
		addTalkId(LEF);
		addRaceCheck("Only characters under level 28 to 40 can accept this quest", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("Only characters under level 28 to 40 can accept this quest", 28/*, 40*/);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;

		int cond = qs.getCond();
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "guildsman_q10337_3.htm";
			qs.setCond(1);
		}
		else if(cond == 1 && event.equalsIgnoreCase("silvan_accept"))
		{
			htmltext = "silvan_q10337_3.htm";
			qs.setCond(2);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "lef_q10337_2.htm";
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

		switch (npcId)
		{
			case GUILDSMAN:
				if(cond == 0)
					htmltext = "guildsman_q10337_1.htm";
				break;
			case SILVAN:
				if(cond == 1)
					htmltext = "silvan_q10337_1.htm";
				break;
			case LEF:
				if(cond == 3)
					htmltext = "lef_q10337_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return "";

		if(updateKill(npc, qs))
		{
			qs.unset(WARRIOR);
			qs.unset(IMP);
			qs.unset(BAT);
			qs.setCond(3);
		}

		return "";
	}
}
