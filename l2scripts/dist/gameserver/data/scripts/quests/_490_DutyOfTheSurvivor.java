package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _490_DutyOfTheSurvivor extends Quest
{
	//npc
	public static final int VOLODOS = 30137;
	
	//mobs
	public static final int[] mobs = {23162, 23163, 23164, 23165, 23166, 23167, 23168, 23169, 23170, 23171, 23172, 23173};
	private static int Zhelch = 34059;
	private static int Blood = 34060;

	private static final int EXP_REWARD = 259683840;	private static final int SP_REWARD = 311580;	
	private static final int EXP_REWARD2 = 513967680;
	private static final int SP_REWARD2 = 623160;
	private static final int EXP_REWARD3 = 770951520;
	private static final int SP_REWARD3 = 934740;
	private static final int EXP_REWARD4 = 1038735360;
	private static final int SP_REWARD4 = 1246320;	public _490_DutyOfTheSurvivor()
	{
		super(PARTY_ONE, DAILY);
		addStartNpc(VOLODOS);
		addTalkId(VOLODOS);
		addKillId(mobs);
		addLevelCheck("30137-lvl.htm", 85, 89);
		addQuestItem(Zhelch);
		addQuestItem(Blood);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("30137-6.htm"))
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
		if(npcId == VOLODOS)
		{
			if(cond == 0)
				return "30137.htm";
			if(cond == 1)
				return "30137-7.htm";
			if(cond == 2 && st.getPlayer().getLevel() >= 85 && st.getQuestItemsCount(Zhelch) >= 100 && st.getQuestItemsCount(Blood) >= 100)
			{
				st.giveItems(57, 1363716);
				st.takeItems(Zhelch, -1);
				st.takeItems(Blood, -1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
				return "30137-9.htm";
			}
			if(cond == 2 && st.getPlayer().getLevel() >= 85 && st.getQuestItemsCount(Zhelch) >= 200 && st.getQuestItemsCount(Blood) >= 200)
			{
				st.giveItems(57, 2727432);
				st.takeItems(Zhelch, -1);
				st.takeItems(Blood, -1);
				st.addExpAndSp(EXP_REWARD2, SP_REWARD2);
				st.finishQuest();
				return "30137-9.htm";
			}
			if(cond == 2 && st.getPlayer().getLevel() >= 85 && st.getQuestItemsCount(Zhelch) >= 300 && st.getQuestItemsCount(Blood) >= 300)
			{
				st.giveItems(57, 4091148);
				st.takeItems(Zhelch, -1);
				st.takeItems(Blood, -1);
				st.addExpAndSp(EXP_REWARD3, SP_REWARD3);
				st.finishQuest();
				return "30137-9.htm";
			}
			if(cond == 2 && st.getPlayer().getLevel() >= 85 && st.getQuestItemsCount(Zhelch) >= 400 && st.getQuestItemsCount(Blood) >= 400)
			{
				st.giveItems(57, 5454864);
				st.takeItems(Zhelch, -1);
				st.takeItems(Blood, -1);
				st.addExpAndSp(EXP_REWARD4, SP_REWARD4);
				st.finishQuest();
				return "30137-9.htm";
			}
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == VOLODOS)
			htmltext = "30137-comp.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
			
		if(ArrayUtils.contains(mobs, npc.getNpcId()))
			if(Rnd.chance(30))
			{
				if(Rnd.chance(50))
				{
					if(st.getQuestItemsCount(Zhelch) < 400)
						st.giveItems(Zhelch, 1);
				}
				else
					if(st.getQuestItemsCount(Blood) < 400)
						st.giveItems(Blood, 1);
				if(st.getQuestItemsCount(Zhelch) >= 100 && st.getQuestItemsCount(Blood) >= 100)
					st.setCond(2);
			}
		return null;
	}	
}