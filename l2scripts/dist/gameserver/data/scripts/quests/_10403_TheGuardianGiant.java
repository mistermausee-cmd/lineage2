package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

public class _10403_TheGuardianGiant extends Quest
{
	public static final String A_LIST = "A_LIST";
	
    //НПЦ
    private static final int Novain_Geographer = 33866;
    //Мобы
    private static final int [] MOBS = new int [] {20650, 20648, 20647, 20649};
    private static final int Guardian_Giant_Akum = 27504;
    //Квест Итем
    private static final int Guardian_Giants_Nucleus_Fragment = 36713;
    //Награда
    private static final int Enchant_Armor_B = 948;
    

	private static final int EXP_REWARD = 9579090;	private static final int SP_REWARD = 1578; 	public _10403_TheGuardianGiant()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Novain_Geographer);
		addTalkId(Novain_Geographer);

		addKillNpcWithLog(2, A_LIST, 1, Guardian_Giant_Akum);
		addKillId(MOBS);
		addQuestItem(Guardian_Giants_Nucleus_Fragment);

		addQuestCompletedCheck("no_level.htm", 10402);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("no_level.htm", 58/*, 61*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "4.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();	
			return "7.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == Novain_Geographer)
		{
			if(cond == 0)
				return "1.htm";
			else if(cond == 1 || cond == 2)
				return "5.htm";	
			else if(cond == 3)
				return "6.htm";				
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1 && qs.getCond() != 2)
			return null;
		
		Player player = qs.getPlayer();	
		NpcInstance scout = null;
		if(qs.getCond() == 1)
		{
			if(ArrayUtils.contains(MOBS, npc.getNpcId()))
			{
				qs.giveItems(Guardian_Giants_Nucleus_Fragment, 1);
				qs.playSound(SOUND_ITEMGET);
				if(qs.getQuestItemsCount(Guardian_Giants_Nucleus_Fragment) >= 90)
				{
					qs.setCond(2);
					scout = qs.addSpawn(Guardian_Giant_Akum, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
					scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);		
				}
			}
		}	
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(3);
			if(scout != null)
				Functions.npcSay(scout, NpcString.YOUWITH_THE_POWER_OF_THE_GODSCEASE_YOUR_MASQUERADING_AS_OUR_MASTERS_OR_ELSE);
		}
		return null;
	}
}