package quests;

import l2s.gameserver.listener.actor.player.OnFishingListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _10407_LetsGoFishing extends Quest
{
	private static final int BATIDA = 31989;
	private static final int HILDE = 31578;
	private static final int KLAUS = 31579;
	private static final int LANOSCO = 31570;
	private static final int LINEUS = 31577;
	private static final int LITUL = 31575;
	private static final int OFULLE = 31572;
	private static final int PAMFUS = 31568;
	private static final int WILLI = 31574;

	private static final long EXP_REWARD = 2469600;
	private static final long SP_REWARD = 2963;

	private final OnFishingListener FISHING_LISTENER = new OnFishingListenerImpl();

	public _10407_LetsGoFishing()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(PAMFUS, LITUL, BATIDA, HILDE, KLAUS, LANOSCO, LINEUS, OFULLE, WILLI);
		addTalkId(PAMFUS, LITUL, BATIDA, HILDE, KLAUS, LANOSCO, LINEUS, OFULLE, WILLI);
		addQuestItemWithLog(2, 540712, 5, 46736);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("fisher_batidae_q10407_04.htm") || event.equalsIgnoreCase("fisher_hilgendorf_q10407_04.htm")
				|| event.equalsIgnoreCase("fisher_klaw_q10407_04.htm") || event.equalsIgnoreCase("fisher_lanosco_q10407_04.htm")
					|| event.equalsIgnoreCase("fisher_linneaus_q10407_04.htm") || event.equalsIgnoreCase("fisher_litulon_q10407_04.htm")
						|| event.equalsIgnoreCase("fisher_ofulle_q10407_04.htm") || event.equalsIgnoreCase("fisher_pamfus_q10407_04.htm")
							|| event.equalsIgnoreCase("fisher_willeri_q10407_04.htm")
			)
		{
			st.setCond(1);
			if(!st.haveQuestItem(47580))
				st.giveItems(47580, 1, false);
			if(!st.haveQuestItem(46737))
				st.giveItems(46737, 100, false);
		}
		if (event.equalsIgnoreCase("fisher_batidae_q10407_08.htm") || event.equalsIgnoreCase("fisher_hilgendorf_q10407_08.htm")
				|| event.equalsIgnoreCase("fisher_klaw_q10407_08.htm") || event.equalsIgnoreCase("fisher_lanosco_q10407_08.htm")
				|| event.equalsIgnoreCase("fisher_linneaus_q10407_08.htm") || event.equalsIgnoreCase("fisher_litulon_q10407_08.htm")
				|| event.equalsIgnoreCase("fisher_ofulle_q10407_08.htm") || event.equalsIgnoreCase("fisher_pamfus_q10407_08.htm")
				|| event.equalsIgnoreCase("fisher_willeri_q10407_08.htm")
				)
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("give_rod") )
		{
			if(npc.getNpcId() == BATIDA)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_batidae_q10407_15.htm";
				else
				{
					htmltext = "fisher_batidae_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == HILDE)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_hilgendorf_q10407_15.htm";
				else
				{
					htmltext = "fisher_hilgendorf_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == KLAUS)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_klaw_q10407_15.htm";
				else
				{
					htmltext = "fisher_klaw_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == LANOSCO)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_lanosco_q10407_15.htm";
				else
				{
					htmltext = "fisher_lanosco_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == LINEUS)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_linneaus_q10407_15.htm";
				else
				{
					htmltext = "fisher_linneaus_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == LITUL)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_litulon_q10407_15.htm";
				else
				{
					htmltext = "fisher_litulon_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == OFULLE)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_ofulle_q10407_15.htm";
				else
				{
					htmltext = "fisher_ofulle_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == PAMFUS)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_pamfus_q10407_15.htm";
				else
				{
					htmltext = "fisher_pamfus_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
			if(npc.getNpcId() == WILLI)
			{
				if (st.haveQuestItem(47580))
					htmltext = "fisher_willeri_q10407_15.htm";
				else
				{
					htmltext = "fisher_willeri_q10407_14.htm";
					st.giveItems(47580, 1, false);
				}
			}
		}
		else if (event.equalsIgnoreCase("give_bait") )
		{
			if(npc.getNpcId() == BATIDA)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_batidae_q10407_16.htm";
			}
			if(npc.getNpcId() == HILDE)
			{
				st.giveItems(46737, 100, false);
				htmltext = "fisher_hilgendorf_q10407_16.htm";
			}
			else if(npc.getNpcId() == KLAUS)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_klaw_q10407_16.htm";
			}
			else if(npc.getNpcId() == LANOSCO)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_lanosco_q10407_16.htm";
			}
			else if(npc.getNpcId() == LINEUS)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_linneaus_q10407_16.htm";
			}
			else if(npc.getNpcId() == LITUL)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_litulon_q10407_16.htm";
			}
			else if(npc.getNpcId() == OFULLE)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_ofulle_q10407_16.htm";
			}
			else if(npc.getNpcId() == PAMFUS)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_pamfus_q10407_16.htm";
			}
			else if(npc.getNpcId() == WILLI)
			{
				st.giveItems(46737, 10, false);
				htmltext = "fisher_willeri_q10407_16.htm";
			}
		}
		else if (event.equalsIgnoreCase("fisher_batidae_q10407_18.htm") || event.equalsIgnoreCase("fisher_hilgendorf_q10407_18.htm")
					|| event.equalsIgnoreCase("fisher_klaw_q10407_18.htm") || event.equalsIgnoreCase("fisher_lanosco_q10407_18.htm")
						|| event.equalsIgnoreCase("fisher_linneaus_q10407_18.htm") || event.equalsIgnoreCase("fisher_litulon_q10407_18.htm")
							|| event.equalsIgnoreCase("fisher_ofulle_q10407_18.htm") || event.equalsIgnoreCase("fisher_pamfus_q10407_18.htm")
								|| event.equalsIgnoreCase("fisher_willeri_q10407_18.htm")
				)
		{
			st.giveItems(47547, 60);
			st.giveItems(46739, 1);
			st.giveItems(38154, 60);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
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
		switch (npcId)
		{
			case BATIDA:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_batidae_q10407_02.htm";
					else
						htmltext = "fisher_batidae_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_batidae_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_batidae_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_batidae_q10407_17.htm";
				break;
			case HILDE:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_hilgendorf_q10407_02.htm";
					else
						htmltext = "fisher_hilgendorf_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_hilgendorf_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_hilgendorf_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_hilgendorf_q10407_17.htm";
				break;
			case KLAUS:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_klaw_q10407_02.htm";
					else
						htmltext = "fisher_klaw_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_klaw_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_klaw_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_klaw_q10407_17.htm";
				break;
			case LANOSCO:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_lanosco_q10407_02.htm";
					else
						htmltext = "fisher_lanosco_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_lanosco_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_lanosco_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_lanosco_q10407_17.htm";
				break;
			case LINEUS:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_linneaus_q10407_02.htm";
					else
						htmltext = "fisher_linneaus_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_linneaus_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_linneaus_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_linneaus_q10407_17.htm";
				break;
			case LITUL:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_litulon_q10407_02.htm";
					else
						htmltext = "fisher_litulon_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_litulon_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_litulon_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_litulon_q10407_17.htm";
				break;
			case OFULLE:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_ofulle_q10407_02.htm";
					else
						htmltext = "fisher_ofulle_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_ofulle_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_ofulle_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_ofulle_q10407_17.htm";
				break;
			case PAMFUS:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_pamfus_q10407_02.htm";
					else
						htmltext = "fisher_pamfus_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_pamfus_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_pamfus_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_pamfus_q10407_17.htm";
				break;
			case WILLI:
				if (cond == 0)
				{
					if(st.getPlayer().getLevel() < 85)
						htmltext = "fisher_willeri_q10407_02.htm";
					else
						htmltext = "fisher_willeri_q10407_01.htm";
				}
				else if (cond == 1)
					htmltext = "fisher_willeri_q10407_05.htm";
				else if (cond == 2)
					htmltext = "fisher_willeri_q10407_09.htm";
				else if (cond == 3)
					htmltext = "fisher_willeri_q10407_17.htm";
				break;
		}
		return htmltext;
	}

	private class OnFishingListenerImpl implements OnFishingListener
	{
		@Override
		public void onFishing(Player player, boolean var)
		{
			QuestState questState = player.getQuestState(10407);
			if(questState != null && questState.getCond() == 2)
				if(questState.getQuestItemsCount(46736) >= 5)
					questState.setCond(3);
		}
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(FISHING_LISTENER);
	}

}
