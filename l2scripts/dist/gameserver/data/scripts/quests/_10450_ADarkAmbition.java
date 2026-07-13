package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10450_ADarkAmbition extends Quest
{
	//npc
	private static final int MATHIAS = 31340;
	private static final int BARHAM = 33839;
	//quest_items
	//rewards
	private static final int SOI = 37019;
	private static final int SOULSHOT = 34609;
	private static final int SPIRITSHOT = 34616;
	private static final int ELIXIR_LIFE = 30357;
	private static final int ELIXIR_MIND = 30358;
	
	private static final int EXP_REWARD = 15436575;	private static final int SP_REWARD = 3704; 	public _10450_ADarkAmbition()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS);
		addTalkId(BARHAM);
		
		addLevelCheck("no_level.htm", 99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
		}
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(SOI, 1);
			if(st.getPlayer().isMageClass())
			{
				st.giveItems(SPIRITSHOT, 10000L);
				st.giveItems(ELIXIR_MIND, 50L);
			}
			else
			{
				st.giveItems(SOULSHOT, 10000L);
				st.giveItems(ELIXIR_LIFE, 50L);
			}		
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npcId == MATHIAS)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == BARHAM)
		{
			if(cond == 1)
				htmltext = "1-1.htm";		
		}
		return htmltext;
	}
}