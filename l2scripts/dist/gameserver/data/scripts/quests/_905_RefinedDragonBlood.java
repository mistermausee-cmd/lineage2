package quests;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 *         Daily
 */
public class _905_RefinedDragonBlood extends Quest
{
	private static final int[] SeparatedSoul = {32864, 32865, 32866, 32867, 32868, 32869, 32870};
	private static final int[] AntharasDragonsBlue = {22852, 22853, 22844, 22845};
	private static final int[] AntharasDragonsRed = {22848, 22849, 22850, 22851};

	private static final int UnrefinedRedDragonBlood = 21913;
	private static final int UnrefinedBlueDragonBlood = 21914;

	public _905_RefinedDragonBlood()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(SeparatedSoul);
		addKillId(AntharasDragonsBlue);
		addKillId(AntharasDragonsRed);
		addQuestItem(UnrefinedRedDragonBlood, UnrefinedBlueDragonBlood);
		addLevelCheck("sepsoul_q905_00.htm", 83);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sepsoul_q905_05.htm"))
		{
			st.setCond(1);
		}
		else if(event.startsWith("sepsoul_q905_08.htm"))
		{
			st.takeAllItems(AntharasDragonsBlue);
			st.takeAllItems(AntharasDragonsRed);

			StringTokenizer tokenizer = new StringTokenizer(event);
			tokenizer.nextToken();
			switch(Integer.parseInt(tokenizer.nextToken()))
			{
				case 1:
					st.giveItems(21903, 1);
					break;
				case 2:
					st.giveItems(21904, 1);
					break;
				default:
					break;
			}
			htmltext = "sepsoul_q905_08.htm";
			st.finishQuest();
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(ArrayUtils.contains(SeparatedSoul, npc.getNpcId()))
		{
			if(cond == 0)
				htmltext = "sepsoul_q905_01.htm";
			if(cond == 1)
				htmltext = "sepsoul_q905_06.htm";
			else if(cond == 2)
				htmltext = "sepsoul_q905_07.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(ArrayUtils.contains(SeparatedSoul, npc.getNpcId()))
			htmltext = "sepsoul_q905_00a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(ArrayUtils.contains(AntharasDragonsBlue, npc.getNpcId()))
			{
				if(st.getQuestItemsCount(UnrefinedBlueDragonBlood) < 10 && Rnd.chance(70))
					st.giveItems(UnrefinedBlueDragonBlood, 1);
			}
			else if(ArrayUtils.contains(AntharasDragonsRed, npc.getNpcId()))
			{
				if(st.getQuestItemsCount(UnrefinedRedDragonBlood) < 10 && Rnd.chance(70))
					st.giveItems(UnrefinedRedDragonBlood, 1);
			}
			if(st.getQuestItemsCount(UnrefinedBlueDragonBlood) >= 10 && st.getQuestItemsCount(UnrefinedRedDragonBlood) >= 10)
				st.setCond(2);
		}
		return null;
	}
}