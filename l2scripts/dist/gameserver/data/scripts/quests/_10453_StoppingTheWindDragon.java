package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Bonux
**/
public class _10453_StoppingTheWindDragon extends Quest
{
	// NPC's
	private static final int JENNA = 33872;

	// Monster's
	private static final int LINDVIOR = 29240;

	// Item's
	private static final int LINDVIOR_SLAYERS_HELMET = 37497;

	private static final int EXP_REWARD = 2147483500;	private static final int SP_REWARD = 515396; 	public _10453_StoppingTheWindDragon()
	{
		super(COMMAND_CHANNEL, ONETIME);

		addStartNpc(JENNA);
		addTalkId(JENNA);

		addKillId(LINDVIOR);

		addLevelCheck("adens_wizard_jenna_q10453_0.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("adens_wizard_jenna_q10453_2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("adens_wizard_jenna_q10453_5.htm"))
		{
			st.giveItems(LINDVIOR_SLAYERS_HELMET, 1, true);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == JENNA)
		{
			if(cond == 0)
				htmltext = "adens_wizard_jenna_q10453_1.htm";
			else if(cond == 1)
				htmltext = "adens_wizard_jenna_q10453_3.htm";
			else if(cond == 2)
				htmltext = "adens_wizard_jenna_q10453_4.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == JENNA)
			htmltext = "adens_wizard_jenna_q10453_6.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1)
		{
			if(npcId == LINDVIOR)
				st.setCond(2);
		}

		return null;
	}
}