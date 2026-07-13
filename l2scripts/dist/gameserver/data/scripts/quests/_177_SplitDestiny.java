package quests;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author GodWorld & Bonux
**/
public class _177_SplitDestiny extends Quest
{
	// NPC'S
	private static final int HADEL = 33344;
	private static final int ISHUMA = 32615;

	// Monster's
	private static final int QUEST_MONSTER_VAMPIRIC_BERISE = 27530;	// Вампир Берис - Квестовый Монстр

	// Item's
	private static final int PETRIFIED_GIANTS_HAND = 17718;
	private static final int PETRIFIED_GIANTS_FOOT = 17719;
	private static final int PETRIFIED_GIANTS_HAND_PIECE = 17720;
	private static final int PETRIFIED_GIANTS_FOOT_PIECE = 17721;

	// Other's
	private static final int[] FOOT_DROP_MONSTERS = { 22257, 22258, 22259, 22260 };

	private static final int EXP_REWARD = 175739575;	private static final int SP_REWARD = 42177; 	public _177_SplitDestiny()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(HADEL);
		addTalkId(HADEL, ISHUMA);
		addKillId(QUEST_MONSTER_VAMPIRIC_BERISE);
		addKillId(FOOT_DROP_MONSTERS);
		addQuestItem(PETRIFIED_GIANTS_HAND, PETRIFIED_GIANTS_FOOT, PETRIFIED_GIANTS_HAND_PIECE, PETRIFIED_GIANTS_FOOT_PIECE);
		addLevelCheck("", 80);
		addRaceCheck("", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addClassLevelCheck("", false, ClassLevel.THIRD);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();

		String htmltext = event;
		if(event.equalsIgnoreCase("33344-14.htm"))
		{
			st.setCond(1);
			st.set("accepted_sub_id", st.getPlayer().getActiveClassId());
		}
		else if(event.equalsIgnoreCase("33344-19.htm"))
		{
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("33344-22.htm"))
		{
			st.setCond(7);
		}
		else if(event.equalsIgnoreCase("32615-03.htm"))
		{
			st.takeItems(PETRIFIED_GIANTS_HAND_PIECE, -1);
			st.takeItems(PETRIFIED_GIANTS_FOOT_PIECE, -1);
			st.setCond(8);
		}
		else if(event.equalsIgnoreCase("33344-25.htm"))
		{
			st.takeItems(PETRIFIED_GIANTS_HAND, -1);
			st.takeItems(PETRIFIED_GIANTS_FOOT, -1);
			st.set("talk", "1");
		}
		else if(event.startsWith("red_") || event.startsWith("blue_") || event.startsWith("green_"))
		{
			if(player.getActiveClassId() != st.getInt("accepted_sub_id"))
				return "33344-16.htm";

			htmltext = "33344-29.htm";

			SubClass sub = player.getActiveSubClass();
			if(sub == null)
				return "Error! Active Subclass is null!";

			if(sub.isDual())
				return "Error! You already have double-class!";

			sub.setType(SubClassType.DUAL_SUBCLASS);

			// Для добавления дуал-класс скиллов.
			player.restoreSkills(true);
			player.sendSkillList();

			int classId = sub.getClassId();
			player.sendPacket(new SystemMessagePacket(SystemMsg.SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS).addClassName(classId).addClassName(classId));
			player.sendPacket(new ExSubjobInfo(player, true));
			player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.LEVEL_UP));

			StringTokenizer strt = new StringTokenizer(event, "_");
			String event2 = strt.nextToken();

			if(event.equalsIgnoreCase("red"))
				st.giveItems(10480, 1);
			else if(event.equalsIgnoreCase("blue"))
				st.giveItems(10481, 1);
			else if(event.equalsIgnoreCase("green"))
				st.giveItems(10482, 1);

			if(event2.equalsIgnoreCase("fire"))
				st.giveItems(9546, 1);
			else if(event2.equalsIgnoreCase("water"))
				st.giveItems(9547, 1);
			else if(event2.equalsIgnoreCase("earth"))
				st.giveItems(9548, 1);
			else if(event2.equalsIgnoreCase("wind"))
				st.giveItems(9549, 1);
			else if(event2.equalsIgnoreCase("dark"))
				st.giveItems(9550, 1);
			else if(event2.equalsIgnoreCase("holy"))
				st.giveItems(9551, 1);

			st.giveItems(17371, 5);
			st.giveItems(36791, 1);
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
		if(npcId == HADEL)
		{
			Player player = st.getPlayer();
			if(cond == 0)
				htmltext = startQuest(st, player);
			else
			{
				if(player.getActiveClassId() != st.getInt("accepted_sub_id"))
					return "33344-16.htm";

				switch(cond)
				{
					case 1:
					case 2:
						htmltext = "33344-15.htm";
						break;
					case 3:
						htmltext = "33344-17.htm";
						break;
					case 4:
					case 5:
						htmltext = "33344-20.htm";
						break;
					case 6:
						htmltext = "33344-21.htm";
						break;
					case 7:
					case 8:
						htmltext = "33344-23.htm";
						break;
					case 9:
						if(st.getInt("talk") == 1)
							htmltext = "33344-28.htm";
						else
							htmltext = "33344-24.htm";
						break;
				}
			}
		}
		else if(npcId == ISHUMA)
		{
			if(cond == 7)
				htmltext = "32615-01.htm";
			else if(cond == 8)
			{
				st.setCond(9);
				st.giveItems(PETRIFIED_GIANTS_HAND, 2);
				st.giveItems(PETRIFIED_GIANTS_FOOT, 2);
				htmltext = "32615-04.htm";
			}
			else
				htmltext = "32615-05.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == HADEL)
		{
			Player player = st.getPlayer();
			QuestState qs = player.getQuestState(10338);
			if(qs == null || player.getVar(qs.getQuest().getName()) == null)
				htmltext = "33344-12.htm";
			else
				htmltext = startQuest(st, player);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		if(st.getPlayer().getActiveClassId() != st.getInt("accepted_sub_id")) // TODO: [Bonux] Проверить, нужна ли данная заглушка.
			return null;

		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(st.getCond())
		{
			case 1:
				if(npcId == QUEST_MONSTER_VAMPIRIC_BERISE)
				{
					st.giveItems(PETRIFIED_GIANTS_HAND_PIECE, 1);
					st.setCond(2);
				}
				break;
			case 2:
				if(npcId == QUEST_MONSTER_VAMPIRIC_BERISE && Rnd.chance(60))
				{
					st.giveItems(PETRIFIED_GIANTS_HAND_PIECE, 1);
					if(st.getQuestItemsCount(PETRIFIED_GIANTS_HAND_PIECE) >= 10)
					{
						st.setCond(3);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				break;
			case 4:
				if(ArrayUtils.contains(FOOT_DROP_MONSTERS, npcId))
				{
					st.giveItems(PETRIFIED_GIANTS_FOOT_PIECE, 1);
					st.setCond(5);
				}
				break;
			case 5:
				if(ArrayUtils.contains(FOOT_DROP_MONSTERS, npcId) || Rnd.chance(60))
				{
					st.giveItems(PETRIFIED_GIANTS_FOOT_PIECE, 1);
					if(st.getQuestItemsCount(PETRIFIED_GIANTS_FOOT_PIECE) >= 10)
					{
						st.setCond(6);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				break;
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.isBaseClassActive())
			return "";

		for(SubClass sub : player.getSubClassList().values())
		{
			if(sub.isDual())
				return "";
		}

		if(!ClassId.VALUES[player.getBaseClassId()].isOfLevel(ClassLevel.AWAKED))
			return "";

		return super.checkStartCondition(npc, player);
	}
	
	public String startQuest(QuestState st, Player player)
	{
		if(!player.isBaseClassActive() && player.getClassId().isOfLevel(ClassLevel.THIRD) && player.getLevel() >= 80)
		{
			ClassId baseClassId = ClassId.VALUES[player.getBaseClassId()];
			if(baseClassId.isOfLevel(ClassLevel.AWAKED))
			{
				if(baseClassId.childOf(player.getClassId()))
					return "33344-noid-" + baseClassId.getBaseAwakedClassId().getId() + ".htm";

				boolean haveDualClass = false;
				for(SubClass sub : player.getSubClassList().values())
				{
					if(sub.isDual())
						haveDualClass = true;
				}

				if(haveDualClass)
					return "33344-12.htm";
				else
				{
					if(player.getClassId() == ClassId.INSPECTOR || player.getClassId() == ClassId.JUDICATOR)
						return "33344-30.htm";
					return "33344-13.htm";
				}
			}
			else
				return "33344-03.htm";
		}
		else
			return "33344-02.htm";	
	}
}