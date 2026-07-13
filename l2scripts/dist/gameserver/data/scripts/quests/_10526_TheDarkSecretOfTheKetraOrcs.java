package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class _10526_TheDarkSecretOfTheKetraOrcs extends Quest
{
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";

	//Квестовые персонажи
	private static final int LUKONES = 33852;

	//Монстры
	private static final int[] MOBS = new int[]{21324, 21325, 21326, 21327, 21328, 21329, 21330, 21331, 21332, 21333,
			21334, 21335, 21336, 21337, 21338, 21339, 21340, 21341, 21342, 21343, 21344, 21345, 21346, 21347, 21348, 21349};
	private static final int ZAPAS_STREL = 27511;
	private static final int ZAPAS_MAG = 27512 ;

	private static final int EXP_REWARD = 492760460;
	private static final int SP_REWARD = 5519;

	public _10526_TheDarkSecretOfTheKetraOrcs()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(LUKONES);
		addTalkId(LUKONES);
		addKillId(MOBS);
		addKillNpcWithLog(1, 1027511, A_LIST, 100, ZAPAS_STREL);
		addKillNpcWithLog(1, 1027512, B_LIST, 100, ZAPAS_MAG);
		addRaceCheck("rugoness_q10526_02a.htm", Race.ERTHEIA);
		addLevelCheck("rugoness_q10526_02.htm", 76, 80);
		addClassTypeCheck("rugoness_q10526_02.htm", ClassType.MYSTIC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("rugoness_q10526_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("rugoness_q10526_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		switch (npcId)
		{
			case LUKONES:
				if(cond == 0)
					htmltext = "rugoness_q10526_01.htm";
				else if (cond == 1)
					htmltext = "rugoness_q10526_06.htm";
				else if (cond == 2)
					htmltext = "rugoness_q10526_07.htm";
				break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() != 1)
		{
			if (ArrayUtils.contains(MOBS, npc.getNpcId()))
			{
				if (Rnd.chance(50))
				{
					NpcInstance scout = qs.addSpawn(ZAPAS_STREL, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
					scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
				}
				else
				{
					NpcInstance scout = qs.addSpawn(ZAPAS_MAG, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
					scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
				}
			}

			else if (updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.unset(B_LIST);
				qs.setCond(2);
			}
		}
		return null;
	}
}
