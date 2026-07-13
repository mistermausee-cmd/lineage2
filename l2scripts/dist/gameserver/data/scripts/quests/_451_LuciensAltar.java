package quests;


import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _451_LuciensAltar extends Quest
{

	private static int DAICHIR = 30537;

	private static int REPLENISHED_BEAD = 14877;
	private static int DISCHARGED_BEAD = 14878;

	private static int ALTAR_1 = 32706;
	private static int ALTAR_2 = 32707;
	private static int ALTAR_3 = 32708;
	private static int ALTAR_4 = 32709;
	private static int ALTAR_5 = 32710;

	private static int[] ALTARS = new int[]{
			ALTAR_1,
			ALTAR_2,
			ALTAR_3,
			ALTAR_4,
			ALTAR_5
	};

	private static final int EXP_REWARD = 13773960;	private static final int SP_REWARD = 3305; 	public _451_LuciensAltar()
	{
		super(PARTY_NONE, DAILY);

		addStartNpc(DAICHIR);
		addTalkId(ALTARS);
		addLevelCheck("30537-00.htm", 80);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("30537-03.htm"))
		{
			st.setCond(1);
			st.giveItems(REPLENISHED_BEAD, 5);
			st.set("Altar1", 0);
			st.set("Altar2", 0);
			st.set("Altar3", 0);
			st.set("Altar4", 0);
			st.set("Altar5", 0);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();

		if(npcId == DAICHIR)
		{
			if(cond == 0)
				htmltext = "30537-01.htm";
			else if(cond == 1)
				htmltext = "30537-04.htm";
			else if(cond == 2)
			{
				htmltext = "30537-05.htm";
				st.giveItems(ADENA_ID, 742800);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.takeItems(DISCHARGED_BEAD, -1);
				st.finishQuest();
				st.getPlayer().setVar(getName(), String.valueOf(System.currentTimeMillis()), -1);
				st.set("Altar1", 0);
				st.set("Altar2", 0);
				st.set("Altar3", 0);
				st.set("Altar4", 0);
				st.set("Altar5", 0);
			}
		}
		else if(cond == 1 && ArrayUtils.contains(ALTARS, npcId))
			if(npcId == ALTAR_1 && st.getInt("Altar1") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar1", 1);
			}
			else if(npcId == ALTAR_2 && st.getInt("Altar2") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar2", 1);
			}
			else if(npcId == ALTAR_3 && st.getInt("Altar3") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar3", 1);
			}
			else if(npcId == ALTAR_4 && st.getInt("Altar4") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar4", 1);
			}
			else if(npcId == ALTAR_5 && st.getInt("Altar5") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar5", 1);
			}
			else
				htmltext = "findother.htm";
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == DAICHIR)
			htmltext = "30537-06.htm";
		return htmltext;
	}

	private void onAltarCheck(QuestState st)
	{
		st.takeItems(REPLENISHED_BEAD, 1);
		st.giveItems(DISCHARGED_BEAD, 1);
		if(st.getQuestItemsCount(DISCHARGED_BEAD) >= 5)
		{
			st.setCond(2);
			return;
		}
		st.playSound(SOUND_ITEMGET);
	}
}