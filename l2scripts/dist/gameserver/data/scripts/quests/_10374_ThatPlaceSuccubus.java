package quests;

import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassLevel;

/** 
* @author coldy
 * TODO: offlike EN HTMLs
*/
public class _10374_ThatPlaceSuccubus extends Quest
{
	private static final int NPC_ANDREI = 31292;
	private static final int NPC_AGNES = 31588;
	private static final int NPC_ZENYA = 32140;

	private static final int MirageFighter = 23186;
	private static final int WarriorMirage = 23187;
	private static final int ShooterMirage = 23188;
	private static final int ShamanMirage = 23189;
	private static final int MartyrMirage = 23190;
	
	public static final String Mirage_Fighter = "MirageFighter";
	public static final String Warrior_Mirage = "WarriorMirage";
	public static final String Shooter_Mirage = "ShooterMirage";
	public static final String Shaman_Mirage = "ShamanMirage";
	public static final String Martyr_Mirage = "MartyrMirage";

	private static final int EXP_REWARD = 23747100;	private static final int SP_REWARD = 5699; 	public _10374_ThatPlaceSuccubus() 
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(NPC_ANDREI);
		addTalkId(NPC_AGNES, NPC_ZENYA);
		
		addKillNpcWithLog(3, Mirage_Fighter, 15, MirageFighter);
		addKillNpcWithLog(3, Warrior_Mirage, 10, WarriorMirage);
		addKillNpcWithLog(3, Shooter_Mirage, 5, ShooterMirage);
		addKillNpcWithLog(3, Shaman_Mirage, 5, ShamanMirage);
		addKillNpcWithLog(3, Martyr_Mirage, 5, MartyrMirage);

		addLevelCheck("31292-05.htm", 80);
		addClassLevelCheck("31292-04.htm", false, ClassLevel.THIRD);
		addClassLevelCheck("31292-04.htm", true, ClassLevel.SECOND); // Ertheia
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		if(event.equalsIgnoreCase("31292-07.htm")) 
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("31588-02.htm")) 
		{
			st.setCond(2);
		}
		if(event.equalsIgnoreCase("32140-02.htm")) 
		{
			st.setCond(3);
		}
		return event;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == NPC_ANDREI)
		{
			if(cond == 0)
				htmltext = "31292-01.htm";
			else if(cond == 1)
				htmltext = "31292-08.htm";
		}
		else if(npcId == NPC_AGNES)
		{
			if(cond == 1)
				htmltext = "31588-01.htm";
			else if(cond == 2)
				htmltext = "31588-03.htm";
		}
		else if(npcId == NPC_ZENYA)
		{
			if(cond == 2)
				htmltext = "32140-01.htm";
			else if(cond == 3)
				htmltext = "32140-03.htm";
			else if(cond == 4)
			{
				htmltext = "32140-04.htm";
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(57, 500560);
				st.finishQuest();
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NPC_ANDREI)
			htmltext = "31292-06.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{

		if(st.getCond() != 3)
			return null;

		if(updateKill(npc, st))
		{
			st.unset(Mirage_Fighter);
			st.unset(Warrior_Mirage);
			st.unset(Shooter_Mirage);
			st.unset(Shaman_Mirage);
			st.unset(Martyr_Mirage);
			st.setCond(4);
		}
		return null;
	}
}
