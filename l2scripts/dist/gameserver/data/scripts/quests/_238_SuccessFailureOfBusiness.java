package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _238_SuccessFailureOfBusiness extends Quest
{

	private static final int Helvetica = 32641;

	private static final int BrazierOfPurity = 18806;
	private static final int EvilSpirit = 22658;
	private static final int GuardianSpirit = 22659;

	private static final int VicinityOfTheFieldOfSilenceResearchCenter = 14865;
	private static final int BrokenPieveOfMagicForce = 14867;
	private static final int GuardianSpiritFragment = 14868;

	private static final int EXP_REWARD = 21843270;	private static final int SP_REWARD = 5242; 	public _238_SuccessFailureOfBusiness()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Helvetica);
		addKillId(BrazierOfPurity, EvilSpirit, GuardianSpirit);
		addQuestItem(BrokenPieveOfMagicForce, GuardianSpiritFragment);
		addLevelCheck("32641-00.htm", 82);
		addQuestCompletedCheck("32641-00.htm", 237);
		addItemHaveCheck("32641-10.htm", VicinityOfTheFieldOfSilenceResearchCenter, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32641-03.htm"))
		{
			st.setCond(1);
		}
		if(event.equalsIgnoreCase("32641-06.htm"))
		{
			st.takeAllItems(BrokenPieveOfMagicForce);
			st.setCond(3);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Helvetica)
			if(cond == 0)
				htmltext = "32641-01.htm";
			else if(cond == 1)
				htmltext = "32641-04.htm";
			else if(cond == 2)
				htmltext = "32641-05.htm";
			else if(cond == 3)
				htmltext = "32641-07.htm";
			else if(cond == 4)
			{
				st.takeAllItems(VicinityOfTheFieldOfSilenceResearchCenter);
				st.takeAllItems(GuardianSpiritFragment);
				st.giveItems(ADENA_ID, 1799640);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
				htmltext = "32641-08.htm";
			}

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Helvetica)
			htmltext = "32641-09.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1 && npc.getNpcId() == BrazierOfPurity)
		{
			st.giveItems(BrokenPieveOfMagicForce, 1);
			if(st.getQuestItemsCount(BrokenPieveOfMagicForce) >= 10)
				st.setCond(2);
		}
		else if(cond == 3 && (npc.getNpcId() == EvilSpirit || npc.getNpcId() == GuardianSpirit))
		{
			st.giveItems(GuardianSpiritFragment, 1);
			if(st.getQuestItemsCount(GuardianSpiritFragment) >= 20)
				st.setCond(4);
		}
		return null;
	}
}