package quests;

import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10354_ResurrectedOwnerofHall extends Quest
{
	// NPC's
	private static final int LYDIA = 32892;
	private static final int OCTAVIS = 29212;

	// Item's
	private static final int BOTTLE_OF_OCTAVIS_SOUL = 34884;

	private static final int EXP_REWARD = 897850000;	private static final int SP_REWARD = 215484; 	public _10354_ResurrectedOwnerofHall()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(LYDIA);
		addTalkId(LYDIA);
		addKillId(OCTAVIS);
		addKillNpcWithLog(1, "OCTAVIS", 1, OCTAVIS);
		addLevelCheck("orbis_typia_q10354_02.htm", 95);
		addQuestCompletedCheck("orbis_typia_q10354_04.htm", 10351);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("orbis_typia_q10354_07.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("orbis_typia_q10354_10.htm"))
		{
			Player player = st.getPlayer();
			htmltext = HtmCache.getInstance().getHtml("quests/_10354_ResurrectedOwnerofHall/orbis_typia_q10354_10.htm", player);
			htmltext = htmltext.replace("<?name?>", player.getName());

			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 23655000, true);
			st.giveItems(BOTTLE_OF_OCTAVIS_SOUL, 1);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == LYDIA)
		{
			if(cond == 0)
				htmltext = "orbis_typia_q10354_01.htm";
			else if(cond == 1)
				htmltext = "orbis_typia_q10354_08.htm";
			else if(cond == 2)
				htmltext = "orbis_typia_q10354_09.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == LYDIA)
			htmltext = "orbis_typia_q10354_03.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == OCTAVIS)
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		return null;
	}
}