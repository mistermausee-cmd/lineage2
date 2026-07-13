package quests;

import java.util.function.Function;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.Functions;

import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk
public class _10758_TheOathoftheWind extends Quest
{
	// NPC's
	private static final int FIO = 33963;
	private NpcInstance clonw = null;

	// Monster's
	private static final int CLONE = 27522;

	private static final int EXP_REWARD = 561645;	private static final int SP_REWARD = 134; 	public _10758_TheOathoftheWind()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(FIO);
		addTalkId(FIO);
		addKillId(CLONE);
		addQuestCompletedCheck("33963-0.htm", 10757);
		addLevelCheck("33963-0.htm", 20);
		addRaceCheck("33963-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33963-3.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("summon"))
		{
			clonw = st.addSpawn(CLONE, st.getPlayer().getX() + 100, st.getPlayer().getY() + 100, st.getPlayer().getZ(), 0, 0, 180000);
			clonw.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case FIO:
				if(cond == 0)
					htmltext = "33963-1.htm";
				else if (cond == 1)
					htmltext = "33963-3.htm";
				else if (cond == 2)
					htmltext = "33963-4.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == CLONE)
		{
			if(cond == 1)
			{
				Functions.npcSay(clonw, NpcString.I_AM_LOYAL_TO_YO_MASTER_OF_THE_WINDS_AND_LOYAL_I_SHALL_REMAIN_IF_MY_VERY_SOUL_BETRAYS_ME);
				st.setCond(2);
			}
		}
		return null;
	}
}