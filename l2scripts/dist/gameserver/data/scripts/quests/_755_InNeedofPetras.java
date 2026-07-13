package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
**/
public class _755_InNeedofPetras extends Quest
{
	// NPC's
	private static final int AKU = 33671;

	// Monster's
	private static final int[] MONSTERS = { 23213, 23214, 23227, 23228, 23229, 23230, 23215, 23216, 23217, 23218, 23231, 23232, 23233, 23234, 23237, 23219 };

	// Item's
	private static final int AKUS_SUPPLY_BOX = 35550;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	private static final int PETRA = 34959;

	// Other
	private static final double PETRA_DROP_CHANCE = 75.0;

	private static final int EXP_REWARD = 570676680;	private static final int SP_REWARD = 136962; 	public _755_InNeedofPetras()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(AKU);
		addTalkId(AKU);
		addKillId(MONSTERS);
		addQuestItem(PETRA);
		addLevelCheck("sofa_aku_q0755_05.htm", 97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_aku_q0755_04.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == AKU)
		{
			if(cond == 0)
				htmltext = "sofa_aku_q0755_01.htm";
			else if(cond == 1)
				htmltext = "sofa_aku_q0755_07.htm";
			else if(cond == 2)
			{
				st.takeItems(PETRA, -1L);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(AKUS_SUPPLY_BOX, 1);
				st.giveItems(ENERGY_OF_DESTRUCTION, 1);
				st.finishQuest();
				htmltext = "sofa_aku_q0755_08.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == AKU)
			htmltext = "sofa_aku_q0755_06.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 1)
			{
				if(Rnd.chance(PETRA_DROP_CHANCE))
				{
					st.giveItems(PETRA, 1);
					if(st.getQuestItemsCount(PETRA) >= 50)
						st.setCond(2);
					else
						st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}