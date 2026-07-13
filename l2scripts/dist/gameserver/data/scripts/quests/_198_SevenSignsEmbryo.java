package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

public class _198_SevenSignsEmbryo extends Quest
{
	// NPCs
	private static int Wood = 32593;
	private static int Franz = 32597;
	private static int Jaina = 32582;
	private static int ShilensEvilThoughtsCapt = 27346;

	// ITEMS
	private static int PieceOfDoubt = 14355;
	private static int DawnsBracelet = 15312;
	private static int AncientAdena = 5575;

	private static final int izId = 113;

	Location setcloc = new Location(-23734, -9184, -5384, 0);

	private static final int EXP_REWARD = 67500000;	private static final int SP_REWARD = 16200; 	public _198_SevenSignsEmbryo()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Wood);
		addTalkId(Wood, Franz, Jaina);
		addKillId(ShilensEvilThoughtsCapt);
		addQuestItem(PieceOfDoubt);
		addLevelCheck("wood_q198_0.htm", 79);
		addQuestCompletedCheck("wood_q198_0.htm", 197);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("wood_q198_2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("wood_q198_3.htm"))
		{
			enterInstance(st, izId);
			if(st.get("embryo") != null)
				st.unset("embryo");
		}
		else if(event.equalsIgnoreCase("franz_q198_3.htm"))
		{
			NpcInstance embryo = player.getReflection().addSpawnWithoutRespawn(ShilensEvilThoughtsCapt, setcloc, 0);
			st.set("embryo", 1);
			Functions.npcSay(npc, player.getName() + "! You should kill this monster! I'll try to help!");
			Functions.npcSay(embryo, "This is not yours.");
			embryo.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 500);
		}
		else if(event.equalsIgnoreCase("wood_q198_8.htm"))
			enterInstance(st, izId);
		else if(event.equalsIgnoreCase("franz_q198_5.htm"))
		{
			Functions.npcSay(npc, "We will be with you always...");
			st.takeItems(PieceOfDoubt, -1);
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("jaina_q198_2.htm"))
			player.getReflection().collapse();
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == Wood)
		{
			if(cond == 0)
				htmltext = "wood_q198_1.htm";
			else if(cond == 1 || cond == 2)
				htmltext = "wood_q198_2a.htm";
			else if(cond == 3)
			{
				if(player.isBaseClassActive())
				{
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.giveItems(DawnsBracelet, 1);
					st.giveItems(57, 1500000);
					st.finishQuest();
					htmltext = "wood_q198_4.htm";
				}
				else
					htmltext = "subclass_forbidden.htm";
			}
		}
		else if(npcId == Franz)
		{
			if(cond == 1)
			{
				if(st.get("embryo") == null || Integer.parseInt(st.get("embryo")) != 1)
					htmltext = "franz_q198_1.htm";
				else
					htmltext = "franz_q198_3a.htm";
			}
			else if(cond == 2)
				htmltext = "franz_q198_4.htm";
			else
				htmltext = "franz_q198_6.htm";
		}
		else if(npcId == Jaina)
			htmltext = "jaina_q198_1.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		if(player == null)
			return null;

		if(npcId == ShilensEvilThoughtsCapt && cond == 1)
		{
			Functions.npcSay(npc, player.getName() + ", I'm leaving now. But we shall meet again!");
			st.set("embryo", 2);
			st.setCond(2);
			st.giveItems(PieceOfDoubt, 1);
			player.startScenePlayer(SceneMovie.SSQ_EMBRYO);
		}
		return null;
	}
}