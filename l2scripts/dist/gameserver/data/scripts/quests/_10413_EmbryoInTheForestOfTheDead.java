package quests;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

//By Evil_dnk

public class _10413_EmbryoInTheForestOfTheDead extends Quest
{
	public static final String A_LIST = "A_LIST";
	
	private static final int Hatuba = 33849;
	private static final int[] Mobs = new int[] {21549, 21547, 21548, 21550, 21551, 21552, 21553, 21554, 21555, 21556,
			21557, 21558, 21559, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567, 21568, 21569, 21570,
			21571, 21572, 21573, 21574, 21575, 21576, 21577, 21578, 21579, 21580, 21581, 21582, 21583, 21584,
			21585, 21586, 21587, 21588, 21589, 21590, 21591, 21592, 21593, 21594, 21595, 21596, 21597, 21599, 18119};

	private static final int Forest_The_Dead_Scout_Embryo = 27509;

	private static final int EXP_REWARD = 161046201;
	private static final int SP_REWARD = 4072; 

	public _10413_EmbryoInTheForestOfTheDead()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Hatuba);
		addTalkId(Hatuba);
		addKillNpcWithLog(1, A_LIST, 300, Forest_The_Dead_Scout_Embryo);
		addKillId(Mobs);
		addLevelCheck("chaser_ahtuba_q10413_02.htm)", 65, 70);
		addQuestCompletedCheck("chaser_ahtuba_q10413_02.htm", 10412);
		addRaceCheck("chaser_ahtuba_q10413_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addClassTypeCheck("chaser_ahtuba_q10413_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("chaser_ahtuba_q10413_05.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("chaser_ahtuba_q10413_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case Hatuba:
				if (cond == 0)
					htmltext = "chaser_ahtuba_q10413_01.htm";
				else if (cond == 1)
					htmltext = "chaser_ahtuba_q10413_06.htm";
				else if (cond == 2)
					htmltext = "chaser_ahtuba_q10413_07.htm";
				break;
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		if(ArrayUtils.contains(Mobs,npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(Forest_The_Dead_Scout_Embryo, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			Functions.npcSay(scout, NpcString.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}	
}