package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _492_TombRaiders extends Quest
{
	//npc
	public static final int ZENIA = 32140;
	
	//mobs
	private static final int[] Mobs = {23193, 23194, 23195, 23196};
	
	//q items
	public static final int ANCIENT_REL = 34769;

	private static final int EXP_REWARD = 25000000;//Unknown!!!!!	private static final int SP_REWARD = 8997; //Unknown!!!!!	public _492_TombRaiders()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(ZENIA);
		addKillId(Mobs);
		addQuestItem(ANCIENT_REL);
		addLevelCheck("32140-lvl.htm", 80);
		addClassLevelCheck("32140-class.htm", false, ClassLevel.THIRD);
		addClassLevelCheck("32140-class.htm", true, ClassLevel.SECOND); // Ertheia
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32140-5.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ZENIA)
		{
			if(cond == 0)
				return "32140.htm";
			if(cond == 1)
				return "32140-6.htm";
			if(cond == 2)
			{
				st.addExpAndSp(EXP_REWARD, SP_REWARD);//Unknown!!!!!
				st.takeItems(ANCIENT_REL, -1);
				st.finishQuest();	
				return "32140-7.htm";	
			}
		}		
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == ZENIA)
			htmltext = "32140-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		if(ArrayUtils.contains(Mobs, npc.getNpcId()) && Rnd.chance(25))
		{
			st.giveItems(ANCIENT_REL, 1);
		}	
		if(st.getQuestItemsCount(ANCIENT_REL) >= 50)
			st.setCond(2);
			
		return null;
	}	
}