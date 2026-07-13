package quests;

import instances.GiantBook;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ReflectionUtils;

//By Evil_dnk

public class _10542_SearchingForNewPower extends Quest
{
	private static final int SHENON = 32974;
	private static final int TABLE = 33126;
	private static final int ASSASIN = 23121;
	private static final int TAIREN = 33004;
	private static final int book = 17575;
	private int killedassasin = 0;
	private static final int INSTANCE_ID = 182;

	private final static int SPIRITSHOT = 2509;
	private final static int SOULSHOT = 1835;

	public static final String A_LIST = "A_LIST";

	private static final int EXP_REWARD = 3200;
	private static final int SP_REWARD = 8;

	public _10542_SearchingForNewPower()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SHENON);
		addTalkId(SHENON);
		addFirstTalkId(TABLE);
		addQuestItem(book);
		addSkillUseId(ASSASIN);
		addFirstTalkId(TAIREN);
		addKillId(ASSASIN);
		addAttackId(ASSASIN);
		addKillNpcWithLog(4, 1023121, A_LIST, 2, ASSASIN);

		addRaceCheck("si_illusion_shannon_q10542_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_shannon_q10542_02.htm", 1/*, 20*/);
		addQuestCompletedCheck("si_illusion_shannon_q10542_02.htm", 10541);
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		GiantBook gb = (GiantBook) st.getPlayer().getActiveReflection();
		if(gb == null || npc.getReflection() != gb)
			return null;

		Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.ENOUGH_OF_THIS_COME_AT_ME);
		if(npc.getNpcId() == ASSASIN)
		{
			if(gb.getTairen() != null)
			{
				gb.Attack(npc);
				if(killedassasin >= 2)
				{
					st.setCond(5);
					st.cancelQuestTimer("attak");
					killedassasin = 0;
				}
				else
				{
					killedassasin++;
				}
			}
		}
		return null;
	}

	private boolean enterInstance(Player player, QuestState st)
	{
		Reflection reflection = player.getActiveReflection();
		if(reflection != null)
		{
			if(player.canReenterInstance(INSTANCE_ID))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(INSTANCE_ID))
		{
			ReflectionUtils.enterReflection(player, new GiantBook(player), INSTANCE_ID);
			st.setCond(2);
			st.unset(A_LIST);
		}
		else
			return false;
		return true;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("si_illusion_shannon_q10542_04"))
		{
			st.setCond(1);
			if(!st.getPlayer().isMageClass() || st.getPlayer().getRace() == Race.ORC)
			{
				htmltext = "si_illusion_shannon_q10542_04.htm";
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				st.giveItems(SOULSHOT, 100);
			}
			else
			{
				htmltext = "si_illusion_shannon_q10542_05.htm";
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				st.giveItems(SPIRITSHOT, 100);
			}
		}
		else if(event.equalsIgnoreCase("si_illusion_guard1_q10542_02.htm"))
		{
		   st.setCond(3);
		}
		else if(event.equalsIgnoreCase("enter_instance"))
		{
			if(!enterInstance(st.getPlayer(), st))
				return "You cannot enter this instance";
			st.playSound(SOUND_MIDDLE);

			GiantBook gb = (GiantBook) player.getActiveReflection();
			if(gb != null && gb.getTairen() != null)
				gb.getTairen().setRunning();
			return null;
		}
		else if(event.equalsIgnoreCase("si_illusion_shannon_q10542_08.htm"))
		{
			st.takeItems(book, -1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			if(!st.getPlayer().isMageClass() || st.getPlayer().getRace() == Race.ORC)
				st.giveItems(SOULSHOT, 1000);
			else
				st.giveItems(SPIRITSHOT, 1000);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("attak"))
		{
			htmltext = "";
			st.startQuestTimer("attak", 5000);
			GiantBook gb = (GiantBook) player.getActiveReflection();
			if(gb != null && gb.getTairen() != null)
			{
				gb.getTairen().moveToLocation(st.getPlayer().getLoc(), Rnd.get(0, 100), true);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.LOOKS_LIKE_ONLY_SKILL_BASED_ATTACKS_DAMAGE_THEM);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.YOUR_NORMAL_ATTACKS_ARENT_WORKING);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.USE_YOUR_SKILL_ATTACKS_AGAINST_THEM);
			}
		}
		else if(event.equalsIgnoreCase("spawnas"))
		{
			GiantBook gb = (GiantBook) player.getActiveReflection();
			if(gb != null)
				gb.stage2(player);
			return null;
		}
		else if(event.equalsIgnoreCase("exit"))
		{
			st.getPlayer().teleToLocation(-111544, 255752, -1469, ReflectionManager.MAIN);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == SHENON)
		{
			if (cond == 0)
				htmltext = "si_illusion_shannon_q10542_01.htm";
			else if(cond == 1)
				htmltext = "si_illusion_shannon_q10542_06.htm";
			else if(cond > 1 && cond < 5)
				htmltext = "si_illusion_shannon_q10542_06a.htm";
			else if(cond == 5)
				htmltext = "si_illusion_shannon_q10542_07.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		String htmltext = NO_QUEST_DIALOG;
		QuestState st = player.getQuestState(getId());
		int npcId = npc.getNpcId();

		if(npcId == TABLE)
		{
			if(st == null)
				return htmltext;

			if(st.getCond() != 3)
				return htmltext;

			GiantBook gb = (GiantBook) player.getActiveReflection();

			if(gb != null && npc.getObjectId() == gb.getbookdesk() && !gb.getTaken())
			{
				gb.setTaken();
				player.sendPacket(new ExShowScreenMessage(NpcString.WATCH_OUT_YOU_ARE_BEING_ATTACKED, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				htmltext = "book_success_q10327_01.htm";
				st.takeAllItems(book);
				st.giveItems(book, 1, false);
				st.setCond(4);
				st.startQuestTimer("attak", 5000);
				st.startQuestTimer("spawnas", 50);
			}
			else
				htmltext = "book_fail_q10327_01.htm";
		}
		if(npcId == TAIREN)
		{
			if(st == null || st.getCond() == 0)
				return "";
			else if(st.getCond() == 2)
			{
				htmltext = "si_illusion_guard1_q10542_01.htm";
				//player.sendPacket(new ExShowScreenMessage(NpcString.WATCH_OUT_YOU_ARE_BEING_ATTACKED, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				//532731	u,Найдите стол с книгой "Война Богов и Гигантов". \0
				st.showTutorialClientHTML("QT_004_skill_01");
			}
			else if(st.getCond() == 3)
				htmltext = "si_illusion_guard1_q10542_02.htm";
			else if(st.getCond() == 4)
				htmltext = "si_illusion_guard1_q10542_03.htm";
			else if(st.getCond() == 5)
				htmltext = "si_illusion_guard1_q10542_04.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{

		if (qs.getCond() == 4)
		{
			if (updateKill(npc, qs))
			{
				qs.unset(A_LIST);
				qs.setCond(5);
				//qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.WATCH_OUT_YOU_ARE_BEING_ATTACKED, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				//1803330	u,Возвращайтесь к Шенон после разговора с Тойроном\0
				qs.cancelQuestTimer("attak");
			}
		}
		return null;
	}
}
