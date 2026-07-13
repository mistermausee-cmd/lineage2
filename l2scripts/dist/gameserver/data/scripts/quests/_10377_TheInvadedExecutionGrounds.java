package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author GodWorld & Bonux
 * @TODO: Пересмотреть с оффом.
**/
public class _10377_TheInvadedExecutionGrounds extends Quest
{
	// NPC's
	private static final int SYLVAIN = 30070;	// Сильвиан - Верховный Жрец
	private static final int HARLAN = 30074;	// Харлан - Страж
	private static final int RODERIK = 30631;	// Родерик - Смотритель Крепости Гильотина
	private static final int ENDRIGO = 30632;	// Эндриго - Смотритель Крепости Гильотина
	private static final int TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH = 33717;	// Надгробие Покойного Гильотина
	private static final int TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER = 33718;	// Надгробие Главного Смотрителя Хоупана
	private static final int TOMBSTONE_OF_CROOK_THE_MAD = 33719;	// Надгробие Обезумевшего Крука

	// Monster's
	//private static final int HOUPON_THE_WARDEN_OVERSEER = 25886;	// Главный Смотритель Хоупан - Рейдовый Босс
	//private static final int CROOK_THE_MAD = 25887;	// Обезумевший Крук - Рейдовый Босс
	//private static final int EXECUTION_GROUNDS_WATCHMAN_GUILLOTINE = 25888;	// Смотритель Земли Казненных Гильотин - Рейдовый Босс

	// Item's
	private static final int SOE_GUILLOTINE_FORTRESS = 35292;
	private static final int HARLANS_ORDERS = 34972;
	private static final int ENDRIGOS_REPORT = 34973;

	private static final int EXP_REWARD = 756106110;	private static final int SP_REWARD = 181465; 	public _10377_TheInvadedExecutionGrounds()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(SYLVAIN);
		addFirstTalkId(TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH, TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER, TOMBSTONE_OF_CROOK_THE_MAD);
		addTalkId(SYLVAIN, HARLAN, RODERIK, ENDRIGO);
		//addKillId(HOUPON_THE_WARDEN_OVERSEER, CROOK_THE_MAD, EXECUTION_GROUNDS_WATCHMAN_GUILLOTINE);
		addQuestItem(HARLANS_ORDERS, ENDRIGOS_REPORT);
		addLevelCheck("sylvain_q10377_02.htm", 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sylvain_q10377_06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("hitsran_q10377_03.htm"))
		{
			st.setCond(2);
			st.giveItems(HARLANS_ORDERS, 1);
		}
		else if(event.equalsIgnoreCase("warden_roderik_q10377_02.htm"))
			st.takeItems(HARLANS_ORDERS, -1L);
		else if(event.equalsIgnoreCase("warden_roderik_q10377_03.htm"))
		{
			st.setCond(3);
			st.giveItems(ENDRIGOS_REPORT, 1);
		}
		else if(event.equalsIgnoreCase("warden_endrigo_q10377_02.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 2970560, true);
			st.giveItems(SOE_GUILLOTINE_FORTRESS, 2);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getId());
		if(st != null)
		{
			int cond = st.getCond();
			switch(npc.getNpcId())
			{
				case TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER:
					if(cond == 3)
						st.setCond(4);
					break;
				case TOMBSTONE_OF_CROOK_THE_MAD:
					if(cond == 4)
						st.setCond(5);
					break;
				case TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH:
					if(cond == 5)
						st.setCond(6);
					break;
			}
		}
		return null;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == SYLVAIN)
		{
			if(cond == 0)
				htmltext = "sylvain_q10377_01.htm";
			else if(cond > 0)
				htmltext = "sylvain_q10377_07.htm";
		}
		else if(npcId == HARLAN)
		{
			if(cond == 1)
				htmltext = "hitsran_q10377_01.htm";
			else if(cond == 2)
				htmltext = "hitsran_q10377_04.htm";
		}
		else if(npcId == RODERIK)
		{
			if(cond == 2)
				htmltext = "warden_roderik_q10377_01.htm";
			else if(cond == 3)
				htmltext = "warden_roderik_q10377_04.htm";
			else if(cond == 6)
				htmltext = "warden_roderik_q10377_05.htm";
		}
		else if(npcId == ENDRIGO)
		{
			if(cond == 6)
			{
				st.takeItems(ENDRIGOS_REPORT, -1);
				htmltext = "warden_endrigo_q10377_01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == SYLVAIN)
			htmltext = "sylvain_q10377_03.htm";
		return htmltext;
	}

	/*@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond > 2)
		{
			switch(npc.getNpcId())
			{
				case HOUPON_THE_WARDEN_OVERSEER:
					if(cond != 3)
						break;
					st.setCond(4);
					break;
				case CROOK_THE_MAD:
					if(cond != 4)
						break;
					st.setCond(5);
					break;
				case EXECUTION_GROUNDS_WATCHMAN_GUILLOTINE:
					if(cond != 5)
						break;
					st.setCond(6);
					break;
			}
		}
		return null;
	}*/
}