package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

import java.util.ArrayList;

//By Evil_dnk

public class _10776_TheWrathoftheGiants extends Quest
{
	// NPC's
	private static final int BELKATI = 30485;
	private static final int DEVICE = 32366;
	private static final int NARSID = 33992;
	private static ArrayList<NpcInstance> fighters = new ArrayList<NpcInstance>();
	
	// Mobs
	private static final int ENRAGEDNARSID = 27534;
	
	private static final int REGENERATIONCORE = 39716;
	private static final int ENCHANTARMOR = 23420;

	private static final int EXP_REWARD = 10046941;	private static final int SP_REWARD = 1161; 	public _10776_TheWrathoftheGiants()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(BELKATI);
		addTalkId(DEVICE);
		addTalkId(NARSID);
		addKillId(ENRAGEDNARSID);
		addQuestCompletedCheck("30485-0.htm", 10775);
		addLevelCheck("30485-0.htm", 48);
		addRaceCheck("30485-0.htm", Race.ERTHEIA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30485-3.htm"))
		{
			st.setCond(1);
			st.giveItems(REGENERATIONCORE, 1, false);
		}
		else if(event.equalsIgnoreCase("30485-7.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("startdevice"))
		{
			npc.setTitle(st.getPlayer().getName());
			npc.broadcastCharInfoImpl(NpcInfoType.TITLE);
			st.setCond(2);
			st.addSpawn(NARSID, 16422, 113281, -9064, 80000);
			st.startQuestTimer("despawnnarsid", 120000, npc);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				npc.setTitle(null);
				npc.broadcastCharInfoImpl(NpcInfoType.TITLE);
			}, 80000L);
			return null;
		}
		else if(event.equalsIgnoreCase("despawnnarsid"))
		{
		 if (st.getCond() == 2 || st.getCond() == 3)
				st.setCond(1);
			return null;
		}
		else if(event.equalsIgnoreCase("beginfight"))
		{
			if(fighters != null)
				fighters.clear();
			fighters.add(st.addSpawn(27535, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(27535, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(ENRAGEDNARSID, 16422, 113281, -9064, 0, 0, 180000));
			for (NpcInstance fighter : fighters)
			{
				if (fighter.getNpcId() == ENRAGEDNARSID)
				{
					Functions.npcSay(fighter, NpcString.CURSED_ERTHEIA_I_WILL_KILL_YOU_ALL);
				}
				fighter.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			npc.deleteMe();
			st.setCond(3);
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
			case BELKATI:
				if(cond == 0)
					htmltext = "30485-1.htm";
				else if (cond == 1 || cond == 2 || cond == 3)
					htmltext = "30485-4.htm";
				else if (cond == 4)
					htmltext = "30485-5.htm";
			break;

			case NARSID:
			 if (cond == 2)
				htmltext = "33992-1.htm";
			break;

			case DEVICE:
				if(cond == 1)
				{
					if(npc.getTitle().isEmpty())
						htmltext = "32366-1.htm";
					else
						htmltext = "32366-2.htm";
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 3)
			st.setCond(4);
		return null;
	}
}