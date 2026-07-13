package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.components.NpcString;

//By Evil_dnk dev.fairytale-world.ru
public class _10334_WindmillHilStatusReport extends Quest
{
	private static final int batis = 30332;
	private static final int shnain = 33508;
	private static final int sword = 2499;
	private static final int atuba = 190;
	private static final int dagg = 225;

	private static final int EXP_REWARD = 200000;	private static final int SP_REWARD = 48; 	public _10334_WindmillHilStatusReport()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(shnain);
		addTalkId(shnain);
		addTalkId(batis);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 22/*, 40*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10333);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "1-3.htm";
			player.sendPacket(new ExShowScreenMessage(NpcString.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
			if(player.isMageClass())
			{
				st.giveItems(atuba, 1, false);
			} 
			else if(player.getClassId().getId() == 7 || player.getClassId().getId() == 35 || player.getClassId().getId() == 7 || player.getClassId().getId() == 125 || player.getClassId().getId() == 126)
			{
				st.giveItems(dagg, 1, false);
			} 
			else
				st.giveItems(sword, 1, false);

		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == shnain)
		{
			if(cond == 0)
				htmltext = "0-1.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";
		} 
		else if(npcId == batis)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == batis)
			htmltext = "1-c.htm";
		return htmltext;
	}
}