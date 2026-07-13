package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _10383_FergasonsOffer extends Quest
{
	// NPC'S
	private static final int SIZRAK = 33669;
	private static final int AKU = 33671;
	private static final int FERGASON = 33681;

	// Monster's
	private static final int[] MONSTERS = { 23213, 23214, 23215, 23216, 23217, 23218, 23219 };

	// Item's
	private static final int UNSTABLE_PETRA = 34958;

	private static final int EXP_REWARD = 951127800;	private static final int SP_REWARD = 228270; 	public _10383_FergasonsOffer()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK, AKU, FERGASON);
		addKillId(MONSTERS);
		addQuestItem(UNSTABLE_PETRA);
		addLevelCheck("sofa_sizraku_q10383_04.htm", 97);
		addQuestCompletedCheck("sofa_sizraku_q10383_07.htm", 10381);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_sizraku_q10383_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("maestro_ferguson_q10383_04.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("sofa_aku_q10383_03.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 3256740, true);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == SIZRAK)
		{
			if(cond == 0)
				htmltext = "sofa_sizraku_q10383_01.htm";
			else if(cond > 0)
				htmltext = "sofa_sizraku_q10383_06.htm";
		}
		else if(npcId == FERGASON)
		{
			if(cond == 1)
				htmltext = "maestro_ferguson_q10383_01.htm";
			else if(cond == 2)
				htmltext = "maestro_ferguson_q10383_05.htm";
		}
		else if(npcId == AKU)
		{
			if(cond == 2)
				htmltext = "sofa_aku_q10383_01.htm";
			else if(cond == 3)
				htmltext = "sofa_aku_q10383_02.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == SIZRAK)
			htmltext = "sofa_sizraku_q10383_05.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 2)
			{
				st.giveItems(UNSTABLE_PETRA, 1);
				if(st.getQuestItemsCount(UNSTABLE_PETRA) >= 20)
				{
					st.setCond(3);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}