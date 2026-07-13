package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _150_ExtremeChallengePrimalMotherResurrected extends Quest
{
	private static final int LIMIER = 33293;
	
	private static final int ISXINA_EXTRIM = 29196;

	private static final int SIGN_OF_SHILEN = 17589;

	public _150_ExtremeChallengePrimalMotherResurrected()
	{
		super(COMMAND_CHANNEL, ONETIME);

		addStartNpc(LIMIER);
		addTalkId(LIMIER);
		addKillId(ISXINA_EXTRIM);
		addQuestItem(SIGN_OF_SHILEN);
		addLevelCheck("33293-level.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33293-5.htm"))
		{
			st.setCond(1);			
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == LIMIER)
		{
			if(cond == 0)
				return "33293.htm";
			if(cond == 1)
				return "33293-7.htm";
			if(cond == 2)
			{
				st.takeItems(SIGN_OF_SHILEN, -1);
				st.giveItems(34883, 1); // isxina soul bottle GOD: harmony //maybe trade for blessed?
				st.finishQuest();			
				return "33293-9.htm";
			}	
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == LIMIER)
			htmltext = "33293-comp.htm";
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && st.getQuestItemsCount(SIGN_OF_SHILEN) == 0)
		{
			st.giveItems(SIGN_OF_SHILEN,1);
			st.setCond(2);
		}	
		return null;	
			
	}
}
