package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Quest "Divided Sakum, Kanilov"
 *
 * @author Darvin
 */
public class _10336_DividedSakumKanilov extends Quest
{
	private static final int ZENATH = 33509;
	private static final int GUILDSMAN = 31795;

	private static final int KANILOV = 27451;

	private static final int SAKUM_SKETCH_A = 17584;
	private static final int SCROLL_ENCHANT_WEAPON_D = 22006;

	private static final int EXP_REWARD = 500000;	private static final int SP_REWARD = 120; 	public _10336_DividedSakumKanilov()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ZENATH);
		addTalkId(GUILDSMAN);
		addKillId(KANILOV);
		addLevelCheck("Only characters under level 27 to 40 witch completed quest Request to Find Sakum can accept this quest (Not for Ertheia race)", 27/*, 40*/);
		addRaceCheck("Only characters under level 27 to 40 witch completed quest Request to Find Sakum can accept this quest (Not for Ertheia race)", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addQuestCompletedCheck("Only characters under level 27 to 40 witch completed quest Request to Find Sakum can accept this quest (Not for Ertheia race)", 10335);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		int cond = qs.getCond();
		// Displays call dialog when player reaches 27 level
		// TODO: В окне, которое появляется при нажатии кнопки "Да"/"Нет" окно не закрывается
		if(event.equalsIgnoreCase("zenath_call"))
		{
			htmltext = "zenath_call.htm";
			qs.onTutorialClientEvent(0);
		}
		else if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "zenath_q10336_3.htm";
			qs.setCond(1);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "guildsman_q10336_3.htm";
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
			case ZENATH:
				if(cond == 0)
					htmltext = "zenath_q10336_1.htm";
				else if(cond == 2)
				{
					htmltext = "zenath_q10336_4.htm";
					qs.setCond(3);
					qs.takeAllItems(SAKUM_SKETCH_A);
					qs.giveItems(SAKUM_SKETCH_A, 1);
				}
				else
					htmltext = "zenath_q10336_taken.htm";
				break;
			case GUILDSMAN:
				if(cond == 3)
					htmltext = "guildsman_q10336_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1 && npc.getNpcId() == KANILOV)
			qs.setCond(2);
		return "";
	}
}
