package quests;

import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

/**
 * @author Iqman
 */
public class _10390_KekropusLetter extends Quest
{
	// Квестовые персонажи
	private static final int MENDIO = 30504;
	private static final int RAYMOND = 30289;
	private static final int ELLIASIN = 30155;
	private static final int ESRANDEL = 30158;
	private static final int GERSHWIN = 32196;
	private static final int DRIKUS = 30505;
	private static final int RAINS = 30288;
	private static final int TOBIAS = 30297;
	private static final int BATHIS = 30332;
	private static final int GOSTA = 30916;
	private static final int ELLI = 33858;

	private static final int LETTER = 36706;
	private static final int HEINE_TELE_SCROLL = 37112;
	private static final int ALLIGATOR_TELE_SCROLL = 37025;

	private static final int EXP_REWARD = 370440;
	private static final int SP_REWARD = 88;

	private static final OnPlayerEnterListener PLAYER_ENTER_LISTENER = new PlayerEnterListener();
	private static final OnLevelChangeListener LEVEL_UP_LISTENER = new ChangeLevelListener();

	private static class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(10390);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 45)
			{
				if (!questState.isCompleted())
				{
					questState.abortQuest();
				}
			}
		}
	}

	private static class ChangeLevelListener implements OnLevelChangeListener
	{
		@Override
		public void onLevelChange(Player player, int was, int set)
		{
			QuestState questState = player.getQuestState(10390);
			if(questState == null)
				return;

			if(player.isBaseClassActive() && player.getLevel() > 45)
			{
				if (!questState.isCompleted())
				{
					questState.abortQuest();
				}
			}
		}
	}


	public _10390_KekropusLetter()
	{
		super(PARTY_NONE, ONETIME, false);
		addStartNpc(MENDIO, RAYMOND, ELLIASIN, ESRANDEL, GERSHWIN, DRIKUS, RAINS, TOBIAS);
		addTalkId(MENDIO, RAYMOND, ELLIASIN, ESRANDEL, GERSHWIN, DRIKUS, RAINS, TOBIAS, BATHIS, GOSTA, ELLI);
		addRaceCheck(NO_QUEST_DIALOG, Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck(NO_QUEST_DIALOG, 40, 45);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		Player player = qs.getPlayer();
		if(event.equals("accept") && !qs.isCompleted())
		{
			qs.setCond(1);
			switch(player.getRace())
			{
				case HUMAN:
					if(player.isMageClass())
					{
						return "bishop_raimund_q10390_03.htm";
					}
					else
					{
						return "master_rains_q10390_03.htm";
					}
				case ELF:
					if(player.isMageClass())
					{
						return "eso_q10390_03.htm";
					}
					else
					{
						return "elliasin_q10390_03.htm";
					}
				case DARKELF:
					return "master_tobias_q10390_03.htm";
				case ORC:
					return "high_prefect_drikus_q10390_03.htm";
				case DWARF:
					return "head_blacksmith_mendio_q10390_03.htm";
				case KAMAEL:
					return "grandmaster_gershuin_q10390_03.htm";
			}
		}
		if(event.equals("mendio"))
		{
			return "head_blacksmith_mendio_q10390_02.htm";
		}
		if(event.equals("raimund"))
		{
			return "bishop_raimund_q10390_02.htm";
		}
		if(event.equals("elliasin"))
		{
			return "elliasin_q10390_02.htm";
		}
		if(event.equals("eso"))
		{
			return "eso_q10390_02.htm";
		}
		if(event.equals("gershuin"))
		{
			return "grandmaster_gershuin_q10390_02.htm";
		}
		if(event.equals("drikus"))
		{
			return "high_prefect_drikus_q10390_02.htm";
		}
		if(event.equals("rains"))
		{
			return "master_rains_q10390_02.htm";
		}
		if(event.equals("tobias"))
		{
			return "master_tobias_q10390_02.htm";
		}
		if(event.equals("letter"))
		{
			if(qs.getQuestItemsCount(LETTER) == 0)
			{
				qs.giveItems(LETTER, 1);
			}
			return "captain_bathis_q10390_09.htm";
		}
		if(event.equals("open_letter"))
		{
			qs.setCond(2);
			if(qs.getQuestItemsCount(LETTER) > 0)
			{
				return "kekropus_letter.htm";
			}
			else
			{
				return "you got the letter already!";
			}
		}
		if(event.equals("gosta"))
		{
			qs.setCond(3);
			qs.takeItems(LETTER, 1);
			qs.giveItems(HEINE_TELE_SCROLL, 1);
			return "captain_bathis_q10390_11.htm";
		}
		if(event.equals("captain_gosta_q10390_03"))
		{
			qs.setCond(4);
			qs.giveItems(ALLIGATOR_TELE_SCROLL, 1);
			return "captain_gosta_q10390_03.htm";
		}
		if(event.equals("barons_personal_escort_eli_q10390_02"))
		{
			qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_46, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			qs.addExpAndSp(EXP_REWARD, SP_REWARD);;
			qs.finishQuest();	
			return "barons_personal_escort_eli_q10390_02.htm";
		}				
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;
		
		if(npc.getNpcId() == MENDIO)
		{
			if(player.getRace() == Race.DWARF) //гномы
			{
				if(st.getCond() == 0)
				{
					htmltext = "head_blacksmith_mendio_q10390_01.htm";
				}
				else
				{
					htmltext = "head_blacksmith_mendio_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "head_blacksmith_mendio_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == RAYMOND)
		{
			if(player.getRace() == Race.HUMAN && player.isMageClass()) //люди маги
			{
				if(st.getCond() == 0)
				{
					htmltext = "bishop_raimund_q10390_01.htm";
				}
				else
				{
					htmltext = "bishop_raimund_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "bishop_raimund_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == ELLIASIN)
		{
			if(player.getRace() == Race.ELF && !player.isMageClass()) // эльфы воины
			{
				if(st.getCond() == 0)
				{
					htmltext = "elliasin_q10390_01.htm";
				}
				else
				{
					htmltext = "elliasin_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "elliasin_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == ESRANDEL)
		{
			if(player.getRace() == Race.ELF && player.isMageClass())  // эльфы маги
			{
				if(st.getCond() == 0)
				{
					htmltext = "eso_q10390_01.htm";
				}
				else
				{
					htmltext = "eso_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "eso_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == GERSHWIN)
		{
			if(player.getRace() == Race.KAMAEL)  // камаэль
			{
				if(st.getCond() == 0)
				{
					htmltext = "grandmaster_gershuin_q10390_01.htm";
				}
				else
				{
					htmltext = "grandmaster_gershuin_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "eso_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == DRIKUS)
		{
			if(player.getRace() == Race.ORC)  // орки
			{
				if(st.getCond() == 0)
				{
					htmltext = "high_prefect_drikus_q10390_01.htm";
				}
				else
				{
					htmltext = "high_prefect_drikus_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "eso_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == RAINS)
		{
			if(player.getRace() == Race.HUMAN && !player.isMageClass())  // люди воины
			{
				if(st.getCond() == 0)
				{
					htmltext = "master_rains_q10390_01.htm";
				}
				else
				{
					htmltext = "master_rains_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "eso_q10390_04.htm";
			}
		}
		if(npc.getNpcId() == TOBIAS)
		{
			if(player.getRace() == Race.DARKELF)  // т.эльф
			{
				if(st.getCond() == 0)
				{
					htmltext = "master_tobias_q10390_01.htm";
				}
				else
				{
					htmltext = "master_tobias_q10390_04.htm";
				}
			}
			else
			{
				htmltext = "eso_q10390_04.htm";
			}
		}

		if(npc.getNpcId() == BATHIS)
		{
			if(st.getCond() == 1)
			{
				if(player.getRace() == Race.DWARF) //гномы
				{
					htmltext = "captain_bathis_q10390_01.htm";
				}
				if(player.getRace() == Race.HUMAN && player.isMageClass()) //люди маги
				{
					htmltext = "captain_bathis_q10390_02.htm";
				}
				if(player.getRace() == Race.ELF && !player.isMageClass()) // эльфы воины
				{
					htmltext = "captain_bathis_q10390_03.htm";
				}
				if(player.getRace() == Race.ELF && player.isMageClass())
				{
					htmltext = "captain_bathis_q10390_04.htm";
				}
				if(player.getRace() == Race.KAMAEL)  // камаэль
				{
					htmltext = "captain_bathis_q10390_05.htm";
				}
				if(player.getRace() == Race.ORC)  // орки
				{
					htmltext = "captain_bathis_q10390_06.htm";
				}
				if(player.getRace() == Race.HUMAN && !player.isMageClass())  // люди воины
				{
					htmltext = "captain_bathis_q10390_07.htm";
				}
				if(player.getRace() == Race.DARKELF)  // т.эльф
				{
					htmltext = "captain_bathis_q10390_08.htm";
				}
			}
			if(st.getCond() == 2)
			{
				htmltext = "captain_bathis_q10390_10.htm";
			}
			if(st.getCond() > 3)
			{
				htmltext = "captain_bathis_q10390_11.htm";
			}
		}
		if(npc.getNpcId() == GOSTA)
		{
			if(st.getCond() == 3)
			{
				return "captain_gosta_q10390_01.htm";
			}
		}
		if(npc.getNpcId() == ELLI)
		{
			if(st.getCond() == 4)
			{
				return "barons_personal_escort_eli_q10390_01.htm";
			}
		}		
		return htmltext;
	}

	@Override
	public void onInit()
	{
		super.onInit();
		CharListenerList.addGlobal(PLAYER_ENTER_LISTENER);
		CharListenerList.addGlobal(LEVEL_UP_LISTENER);
	}
}