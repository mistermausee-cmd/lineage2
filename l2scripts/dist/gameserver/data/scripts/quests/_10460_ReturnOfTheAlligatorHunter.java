package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author Iqman
 */
public class _10460_ReturnOfTheAlligatorHunter extends Quest
{
	//npc
	private static final int ELLON = 33860;
	//mob
	private static final int[] MOBS1 = { 20135 };
	private static final int[] MOBS2 = { 20804, 20805, 20806 };
	private static final int[] MOBS3 = { 20807, 20808 };
	//q items
	private static final int ALLIGATOR_SKIN = 36710;
	private static final int BLUE_ALLIGATOR_SKIN = 36711;
	private static final int PRECIOUS_ALLIGATOR_SKIN = 36712;
	//rewards
			
	private static final int EXP_REWARD = 4150144;	private static final int SP_REWARD = 670; 	public _10460_ReturnOfTheAlligatorHunter()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(ELLON);
		addTalkId(ELLON);
		addKillId(MOBS1);
		addKillId(MOBS2);
		addKillId(MOBS3);
		addQuestItem(ALLIGATOR_SKIN);
		addQuestItem(BLUE_ALLIGATOR_SKIN);
		addQuestItem(PRECIOUS_ALLIGATOR_SKIN);
		addRaceCheck("no_level.htm"/*, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL*/);
		addLevelCheck("no_level.htm", 40, 45);
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
			st.takeItems(ALLIGATOR_SKIN, 30L);
			st.takeItems(BLUE_ALLIGATOR_SKIN, 20L);
			st.takeItems(PRECIOUS_ALLIGATOR_SKIN, 10L);		
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		
		if(npcId == ELLON)
		{
			if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 2)
				htmltext = "4.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() != 1)
			return null;
		if(ArrayUtils.contains(MOBS1, npcId) && st.getQuestItemsCount(ALLIGATOR_SKIN) < 30L)
		{
			st.giveItems(ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(ArrayUtils.contains(MOBS2, npcId) && st.getQuestItemsCount(BLUE_ALLIGATOR_SKIN) < 20L)
		{
			st.giveItems(BLUE_ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(ArrayUtils.contains(MOBS3, npcId) && st.getQuestItemsCount(PRECIOUS_ALLIGATOR_SKIN) < 10L)
		{
			st.giveItems(PRECIOUS_ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(ALLIGATOR_SKIN) >= 30L && st.getQuestItemsCount(BLUE_ALLIGATOR_SKIN) >= 20L && st.getQuestItemsCount(PRECIOUS_ALLIGATOR_SKIN) >= 10L)
		{
			st.setCond(2);
		}
		return null;
	}
}