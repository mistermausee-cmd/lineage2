package quests;


import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _462_StuffedAncientHeroes extends Quest
{
	private final int[] Bosses = {25760, 25761, 25762, 25763, 25764, 25766, 25767, 25768, 25769, 25770};

	public _462_StuffedAncientHeroes()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(32892); // Tipia
		
		addLevelCheck("32892-lvl.htm", 95);
		addQuestCompletedCheck("32892-lvl.htm", 10317); // to replace for witch quest
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32892-6.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("reward"))
		{
			st.giveItems(30386, 3);
			st.unset("1bk");
			st.unset("2bk");
			st.set("1bk", "0");
			st.set("2bk", "0");
			st.finishQuest();
			return "32892-11.htm";		
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 32892)
		{
			if(cond == 0)
			{
				String htmltext = HtmCache.getInstance().getHtml("quests/_462_StuffedAncientHeroes/32892.htm", st.getPlayer());	
				htmltext.replace("%name%", player.getName());	
				return htmltext;
			}
			if(cond == 1)
				return "32892-7.htm";
			if(cond == 3)
				return "32892-8.htm";
			if(cond == 2)
				return "reward";
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == 32892)
			htmltext = "32892-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || cond != 3)
			return null;
		if(npc != null && ArrayUtils.contains(Bosses, npc.getNpcId()))
		{
			if(st.getInt("1bk") == 1)
			{
				st.set("2bk", "1");
				st.setCond(2);
			}
			else
			{
				st.set("1bk", "1");
				st.setCond(3);
			}	
		}
		return null;
	}	
}