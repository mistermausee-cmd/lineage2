package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10359_SakumsTrace extends Quest
{

	private static final int guild = 31795;
	private static final int fred = 33179;
	private static final int reins = 30288;  //Human Warrior
	private static final int raimon = 30289;  //Human Mag
	private static final int tobias = 30297;  //   Dark Elf
	private static final int Drikus = 30505;  //  Orc
	private static final int mendius = 30504;  //  Dwarf
	private static final int gershfin = 32196;  // Kamael
	private static final int elinia = 30155;  //  Elf mag
	private static final int ershandel = 30158;  // Elf warrior

	private static final int frag = 17586;

	private static final int[] huntl = {20067, 20070, 20072};
	private static final int[] hunth = {23097, 23098, 23026, 20192};

	private static final int EXP_REWARD = 1800000;	private static final int SP_REWARD = 216; 	public _10359_SakumsTrace() 
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(guild);
		addTalkId(fred);
		addTalkId(guild);
		addTalkId(elinia);
		addTalkId(raimon);
		addTalkId(reins);						
		addTalkId(ershandel);					
		addTalkId(gershfin);	
		addTalkId(tobias);
		addTalkId(Drikus);
		addTalkId(mendius);		

		addKillId(huntl);
		addKillId(hunth);
		addQuestItem(frag);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 34/*, 40*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("qet_rev"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
			if(st.getPlayer().getRace() == Race.HUMAN)
			{
				if(st.getPlayer().isMageClass())
					htmltext = "2-3re.htm";
				else
					htmltext = "2-3r.htm";
			} 
			else if(st.getPlayer().getRace() == Race.ELF)
			{
				if(st.getPlayer().isMageClass())
					htmltext = "2-3e.htm";
				else
					htmltext = "2-3ew.htm";
			} 
			else if(st.getPlayer().getRace() == Race.DARKELF)
				htmltext = "2-3t.htm";
			else if(st.getPlayer().getRace() == Race.ORC)
				htmltext = "2-3d.htm";
			else if(st.getPlayer().getRace() == Race.DWARF)
				htmltext = "2-3m.htm";
			else if(st.getPlayer().getRace() == Race.KAMAEL)
				htmltext = "2-3g.htm";
		}


		if(event.equalsIgnoreCase("1-3.htm"))
			st.setCond(2);

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == guild)
		{
			if(cond == 0)
				htmltext = "0-1.htm";
			else if(cond == 1 || cond == 2 || cond == 3)
				htmltext = "0-4.htm";
		} 
		else if(npcId == fred)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
			else if(cond == 2)
				htmltext = "1-4.htm";
			else if(cond == 3)
			{
				if(st.getPlayer().getRace() == Race.HUMAN)
				{
					if(st.getPlayer().isMageClass())
					{
						htmltext = "1-5re.htm";
						st.setCond(4);
					} 
					else 
					{
						htmltext = "1-5r.htm";
						st.setCond(5);
					}
				} 
				else if(st.getPlayer().getRace() == Race.ELF)
				{
					if(st.getPlayer().isMageClass()) 
					{
						htmltext = "1-5e.htm";
						st.setCond(11);
						
					} 
					else 
					{
						htmltext = "1-5ew.htm";
						st.setCond(10);
					}
				} 
				else if(st.getPlayer().getRace() == Race.DARKELF)
				{
					htmltext = "1-5t.htm";
					st.setCond(6);

				}
				else if(st.getPlayer().getRace() == Race.ORC)
				{
					htmltext = "1-5d.htm";
					st.setCond(7);

				} 
				else if(st.getPlayer().getRace() == Race.DWARF)
				{
					htmltext = "1-5m.htm";
					st.setCond(8);
				} 
				else if(st.getPlayer().getRace() == Race.KAMAEL)
				{
					htmltext = "1-5g.htm";
					st.setCond(9);
				}
			}

		} 
		else if(npcId == raimon && st.getPlayer().getRace() == Race.HUMAN && st.getPlayer().isMageClass())
		{
			if(cond == 4)
				htmltext = "2-1re.htm";
		} 
		else if(npcId == reins && st.getPlayer().getRace() == Race.HUMAN && !st.getPlayer().isMageClass())
		{
			if(cond == 5)
				htmltext = "2-1r.htm";
		} 
		else if(npcId == tobias && st.getPlayer().getRace() == Race.DARKELF) 
		{
			if(cond == 6)
				htmltext = "2-1t.htm";
		} 
		else if(npcId == Drikus && st.getPlayer().getRace() == Race.ORC)
		{
			if(cond == 7)
				htmltext = "2-1d.htm";
		} 
		else if(npcId == gershfin && st.getPlayer().getRace() == Race.KAMAEL)
		{
			if(cond == 9)
				htmltext = "2-1g.htm";
		} 
		else if(npcId == elinia && st.getPlayer().getRace() == Race.ELF && !st.getPlayer().isMageClass())
		{
			if(cond == 10)
				htmltext = "2-1e.htm";
		} 
		else if(npcId == ershandel && st.getPlayer().getRace() == Race.ELF && st.getPlayer().isMageClass())
		{
			if(cond == 11)
				htmltext = "2-1ew.htm";
		} 
		else if(npcId == mendius && st.getPlayer().getRace() == Race.DWARF)
		{
			if(cond == 8)
				htmltext = "2-1m.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == raimon && st.getPlayer().getRace() == Race.HUMAN && st.getPlayer().isMageClass())
			htmltext = "2re-c.htm";
		else if(npcId == reins && st.getPlayer().getRace() == Race.HUMAN && !st.getPlayer().isMageClass())
			htmltext = "2r-c.htm";
		else if(npcId == tobias && st.getPlayer().getRace() == Race.DARKELF)
			htmltext = "2t-c.htm";
		else if(npcId == Drikus && st.getPlayer().getRace() == Race.ORC)
			htmltext = "2d-c.htm";
		else if(npcId == gershfin && st.getPlayer().getRace() == Race.KAMAEL)
			htmltext = "2g-c.htm";
		else if(npcId == elinia && st.getPlayer().getRace() == Race.ELF && !st.getPlayer().isMageClass())
			htmltext = "2ew-c.htm";
		else if(npcId == ershandel && st.getPlayer().getRace() == Race.ELF && st.getPlayer().isMageClass())
			htmltext = "2e-c.htm";
		else if(npcId == mendius && st.getPlayer().getRace() == Race.DWARF)
			htmltext = "2m-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		if(cond == 2 && st.getQuestItemsCount(frag) < 20)
		{
			if(ArrayUtils.contains(huntl, npcId))
				st.rollAndGive(frag, 1, 15);

			else if(ArrayUtils.contains(hunth, npcId))
				st.rollAndGive(frag, 1, 35);
		}
		if(st.getQuestItemsCount(frag) >= 20)
			st.setCond(3);
		return null;
	}	
}