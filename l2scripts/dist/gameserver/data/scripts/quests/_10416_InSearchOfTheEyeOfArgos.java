package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
//By SanyaDC
public class _10416_InSearchOfTheEyeOfArgos extends Quest
{
    private static final int Janitt = 33851;
    private static final int Eye = 31683;
    //Награда
	private static final int[] MOBS = new int[]{21294,21295,21296,21297,21299,23311,21304,23312};
    public static final String A_LIST = "a_list";

	private static final int EXP_REWARD = 178732196;	private static final int SP_REWARD = 261; 	public _10416_InSearchOfTheEyeOfArgos()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Janitt);
		addTalkId(Janitt, Eye);
		addLevelCheck("no_level.htm", 70, 75);
		addRaceCheck("no_level.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addKillId(MOBS);
		addKillNpcWithLog(1, 541611, A_LIST, 200, MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			return "4.htm";
		}
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();		
			return "2-2.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == Janitt)
		{
			if(st.getCond() == 0)
				return "1.htm";
			else if(st.getCond() == 1)
				return "5.htm";
		}		
		else if(npc.getNpcId() == Eye)
		{
			if(st.getCond() == 2)
				return "2-1.htm";
		}	
		return NO_QUEST_DIALOG;
	}
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{	
		if(qs.getCond() == 1)
		{
			if(updateKill(npc, qs))
			{
				qs.unset(A_LIST);				
				qs.setCond(2);			
			}
		}
		return null;		
	}
}