package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
// TODO В описании квеста нужно открыть дверь ключем, ключ реализовал, но в окне квеста явного этапа открытия двери нет
public class _10447_TimingisEverything extends Quest
{
	//npc
	private static final int BURINU = 33840;

	//mobs
	private static final int[] MOBS = {
			23314,
			23315,
			23316,
			23317,
			23318,
			23319,
			23320,
			23321,
			23322,
			23323,
			23324,
			23325,
			23326,
			23327,
			23328,
			23329
	};
	//q_items
	private static final int KEY = 36665;

	private static final int EXP_REWARD = 2147483647;	private static final int SP_REWARD = 515396; 	public _10447_TimingisEverything()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(BURINU);
		addTalkId(BURINU);
		addKillId(MOBS);
		addQuestItem(KEY);

		addLevelCheck("no_level.htm", 99);
		addQuestCompletedCheck("no_level.htm", 10445);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == BURINU)
		{
			if(cond == 0)
			{
				htmltext = "1.htm";
			}
			else if(cond == 1)
			{
				htmltext = "3.htm";
			}
			else if(cond == 2)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
				htmltext = "endquest.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		if(Rnd.chance(1))
		{
			qs.playSound(SOUND_MIDDLE);
			qs.giveItems(KEY, 1);
		}
		return null;
	}
}