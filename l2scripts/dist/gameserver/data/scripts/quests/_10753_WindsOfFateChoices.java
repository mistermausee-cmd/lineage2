package quests;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import instances.WindOfFate;
import l2s.commons.util.Rnd;

import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.player.OnClassChangeListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExChangeToAwakenedClass;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
**/
/*
TODO:
1. Добавить выдачу\изъятие квестовых вещей.
2. Реализовать итем Устройство Предсказаний Артеи.
3. Реализовать у загадочного мага правильно ссылки и далоги начиная с 33980-03.htm
4. Добавить диалоги Эфира Де Ган, если такие есть.
5. Обновить все диалоги с пометкой TODO с оффа.
6. Добавить разговоры Кайн Ван Холтер и Эфира Де Ган во время прохождения инстанса.

8. Сверить все ньюансы с оффом.
*/
public class _10753_WindsOfFateChoices extends Quest
{
	public class ClassChangeListener implements OnClassChangeListener
	{
		public void onClassChange(Player player, ClassId oldClass, ClassId newClass)
		{
			QuestState qs = player.getQuestState(getId());
			if(qs != null)
			{
				if(!newClass.isOfLevel(ClassLevel.SECOND))
					qs.abortQuest();
			}
		}
	}

	// NPC's
	private static final int MAGISTER_AYANTHE = 33942;	// Арис - Магистр
	private static final int MASTER_KATALIN = 33943;	// Катрина - Мастер
	private static final int GRAND_MAGUSTER_ARKENIAS = 30174;	// Аркениас - Великий Магистр
	private static final int ALCHEMISTS_MIXING_URN = 31149;	// Алхимическая Ступка
	private static final int HARDIN = 30832;	// Хардин
	private static final int LICH_KING_ICARUS = 30835;	// Икар - Король Личей
	private static final int WITCH_RITASHA = 30758;	// Атрея - Ведьма
	private static final int RITASHAS_BOX = 33997;	// Сундук Атреи
	private static final int HIGH_PRIEST_GERETH = 33932;	// Терениус  - Верховный Жрец
	private static final int KAIN_VAN_HALTER = 33979;	// Кайн Ван Холтер
	private static final int FERIN = 34001;	// Эфира Де Ган
	private static final int GRAIL = 33996;	// Священный Кубок 
	private static final int WIZARD_MYSTERIOUS = 33980;	// Загадочный Маг
	private static final int QUEEN_NAVARI = 33931;	// Серения - Королева

	// Monster's
	private static final int QUEST_MONSTER_NEBULITE_EYE = 27544;	// Сумеречное Око - Квестовый Монстр
	private static final int QUEST_MONSTER_NEBULITE_WATCH = 27545;	// Сумеречная Сила - Квестовый Монстр
	private static final int QUEST_MONSTER_NEBULITE_GOLEM = 27546;	// Сумеречный Голем - Квестовый Монстр

	private static final int SACRED_SOLDIER = 19569;    // Посвященный солдат
	private static final int SACRED_SLAYER = 19570;    // Посвященный солдат
	private static final int SACRED_WIZARD = 19568;    // Посвященный чародей
	private static final int SECLUDED_SHADOW = 19573;    // Тень одиночества
	private static final int ABYSSAL_SHADOW = 19572;    // Тень бездны
	private static final int MAKKUM = 19571;    // Маккум

	// Item's
	private static final int CRYSTAL_EYE = 39545;    // Кристалл Ока
	private static final int BROKEN_STONE_OF_PURITY = 39546;    // Разбитый Камень Чистоты
	private static final int MIRACLE_DRUG_OF_ENCHANTMENT = 39547;    // Эликсир Усиления
	private static final int RITASHAS_BELONGINGS = 39550;    // Вещи Атреи

	// Timer's
	private static final String END_MINIGAME_TIMER = "end_minigame_timer";
	private static final String DESPAWN_BOXES = "despawn_boxes_timer";
	private static final String WIZARD_MSG_TIMER_ID_VAR = "wizard_msg_timer_id";
	private static final String DESPAWN_WIZARD_TIMER_ID_VAR = "despawn_wizard_timer_id";
	private static final String END_INSTANCE_TIMER_ID_VAR = "end_instance_timer_id";

	// Var's
	private static final String WIZARD_TALKED_VAR = "wizard_talked";

	// Other
	private static final String RITASHAS_BOX_SPAWN_GROUP = "q10753_9_boxes";
	private static final String GRAIL_SPAWN_GROUP = "q10753_16_instance_grail";
	private static final String WIZARD_SPAWN_GROUP = "q10753_16_instance_wizard";
	private static final String HALTER_SPAWN_GROUP_1 = "q10753_16_instance_halter_1";
	private static final String HALTER_SPAWN_GROUP_2 = "q10753_16_instance_halter_2";
	private static final int INSTANCE_ZONE_ID = 255; // Зал Предсказаний

	private final OnClassChangeListener _classChangeListener = new ClassChangeListener();

	// Minigame parameters
	private int _minigameOwner = 0;
	private int _successBoxObjectId1 = 0;
	private int _successBoxObjectId2 = 0;
	private int _successBoxObjectId3 = 0;
	private int _successBoxObjectId4 = 0;
	private boolean _boxSearched1 = false;
	private boolean _boxSearched2 = false;
	private boolean _boxSearched3 = false;
	private boolean _boxSearched4 = false;

	private long _wait_timeout = 0;

	private static final int EXP_REWARD = 0;
	private static final int SP_REWARD = 500000;

	public _10753_WindsOfFateChoices() 
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(MAGISTER_AYANTHE, MASTER_KATALIN);
		addTalkId(GRAND_MAGUSTER_ARKENIAS, ALCHEMISTS_MIXING_URN, HARDIN, LICH_KING_ICARUS, WITCH_RITASHA, RITASHAS_BOX, HIGH_PRIEST_GERETH, KAIN_VAN_HALTER, GRAIL, WIZARD_MYSTERIOUS, QUEEN_NAVARI);
		addKillId(QUEST_MONSTER_NEBULITE_EYE, QUEST_MONSTER_NEBULITE_WATCH, QUEST_MONSTER_NEBULITE_GOLEM, SACRED_SLAYER, SACRED_SOLDIER, SACRED_WIZARD, SECLUDED_SHADOW, ABYSSAL_SHADOW);

		addAttackId(MAKKUM);
		addFirstTalkId(RITASHAS_BOX, FERIN, WIZARD_MYSTERIOUS);

		addQuestItemWithLog(2, 575311, 3, CRYSTAL_EYE);
		addQuestItemWithLog(2, 575312, 3, BROKEN_STONE_OF_PURITY);
		addQuestItemWithLog(2, 575313, 3, MIRACLE_DRUG_OF_ENCHANTMENT);
		addQuestItem(RITASHAS_BELONGINGS);

		addLevelCheck(MAGISTER_AYANTHE, "33942-00.htm", 85);
		addClassLevelCheck(MAGISTER_AYANTHE, "33942-00.htm", true, ClassLevel.SECOND);
		addRaceCheck(MAGISTER_AYANTHE, "33942-00.htm", Race.ERTHEIA);

		addLevelCheck(MASTER_KATALIN, "33943-00.htm", 85);
		addClassLevelCheck(MASTER_KATALIN, "33943-00.htm", true, ClassLevel.SECOND);
		addRaceCheck(MASTER_KATALIN, "33943-00.htm", Race.ERTHEIA);
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		WindOfFate wof = (WindOfFate) player.getActiveReflection();

		if(npcId == MAKKUM && System.currentTimeMillis() > _wait_timeout)
		{
			_wait_timeout = System.currentTimeMillis() + 8000;
			player.sendPacket(new ExShowScreenMessage(NpcString.LEAVE_THIS_PLACE_TO_KAINNGO_TO_THE_NEXT_ROOM, 7000, ScreenMessageAlign.TOP_CENTER, true));
			if(wof != null)
			{
				if(wof.getVanHalter() != null && Rnd.chance(30))
					Functions.npcSay(wof.getVanHalter(), NpcString.LEAVE_THIS_TO_ME_GO);
				if(wof.getFerrin() != null && Rnd.chance(30))
					Functions.npcSay(wof.getFerrin(), NpcString.GO_NOW_KAIN_CAN_HANDLE_THIS);
			}
		}
		return null;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();
		final Player player = st.getPlayer();
		final Reflection reflection = player.getReflection();

		StringTokenizer stkn = new StringTokenizer(event, " ");

		event = stkn.nextToken();

		if(event.equalsIgnoreCase("33942-03.htm") || event.equalsIgnoreCase("33943-03.htm"))
		{
			if(cond == 0)
			{
				st.setCond(1);
			}
		}
		else if(event.equalsIgnoreCase("30174-03.htm")) {
			if(cond == 1){
				st.setCond(2);
			}
		}
		else if(event.equalsIgnoreCase("31149-03.htm"))
		{
			if(cond == 4)
			{
				st.setCond(5);
			}
		}
		else if(event.equalsIgnoreCase("30174-10.htm"))
		{
			if(cond == 5)
			{
				st.setCond(6);
			}
		}
		else if(event.equalsIgnoreCase("30832-03.htm"))
		{
			if(cond == 6)
			{
				st.setCond(7);
			}
		}
		else if(event.equalsIgnoreCase("30835-05.htm"))
		{
			if(cond == 7)
			{
				st.setCond(8);
			}
		}
		else if(event.equalsIgnoreCase("30758-04.htm"))
		{
			if(cond == 8)
			{
				if(_minigameOwner == 0)
				{
					SpawnManager.getInstance().spawn(RITASHAS_BOX_SPAWN_GROUP);

					List<Spawner> boxSpawners = SpawnManager.getInstance().getSpawners(RITASHAS_BOX_SPAWN_GROUP);
					List<NpcInstance> boxes = new ArrayList<NpcInstance>();
					for(Spawner spawner : boxSpawners)
						boxes.addAll(spawner.getAllSpawned());

					if(boxes.size() < 4)
					{
						event = "Error quest 10753, state 9! Contact the administrator.";
						SpawnManager.getInstance().despawn(RITASHAS_BOX_SPAWN_GROUP);
					}
					else
					{
						st.setCond(9, false);
						player.sendPacket(new ExSendUIEventPacket(player, 0, 0, 180, 0, NpcString.TIME_REMAINING));
						st.startQuestTimer(END_MINIGAME_TIMER, 180000);

						_minigameOwner = player.getObjectId();

						NpcInstance box = Rnd.get(boxes);
						_successBoxObjectId1 = box.getObjectId();
						boxes.remove(box);

						box = Rnd.get(boxes);
						_successBoxObjectId2 = box.getObjectId();
						boxes.remove(box);

						box = Rnd.get(boxes);
						_successBoxObjectId3 = box.getObjectId();
						boxes.remove(box);

						box = Rnd.get(boxes);
						_successBoxObjectId4 = box.getObjectId();
						boxes.remove(box);
					}
				}
				else
					event = "30758-05.htm";
			}
		}
		else if(event.equalsIgnoreCase("33997-01.htm"))
		{
			if(cond == 9)
			{
				if(_minigameOwner == player.getObjectId())
				{
					int npcObjectId = npc.getObjectId();
					boolean searched = false;
					boolean success = false;
					if(npcObjectId == _successBoxObjectId1)
					{
						if(_boxSearched1)
							searched = true;
						else
						{
							_boxSearched1 = true;
							success = true;
						}
					}
					else if(npcObjectId == _successBoxObjectId2)
					{
						if(_boxSearched2)
							searched = true;
						else
						{
							_boxSearched2 = true;
							success = true;
						}
					}
					else if(npcObjectId == _successBoxObjectId3)
					{
						if(_boxSearched3)
							searched = true;
						else
						{
							_boxSearched3 = true;
							success = true;
						}
					}
					else if(npcObjectId == _successBoxObjectId4)
					{
						if(_boxSearched4)
							searched = true;
						else
						{
							_boxSearched4 = true;
							success = true;
						}
					}

					if(searched)
						event = "33997-03.htm";
					else if(success)
					{
						st.giveItems(RITASHAS_BELONGINGS, 1);
						if(_boxSearched1 && _boxSearched2 && _boxSearched3 && _boxSearched4)
						{
							event = "33997-04.htm";
							st.setCond(10);
							st.cancelQuestTimer(END_MINIGAME_TIMER);
							st.startQuestTimer(DESPAWN_BOXES, 3000);
						}
						else
						{
							event = "33997-02.htm";
							st.playSound(SOUND_ITEMGET);
						}
					}
				}
			}
		}
		else if(event.equalsIgnoreCase(END_MINIGAME_TIMER))
		{
			st.setCond(8);
			// TODO: Нужно ли какое-то сообщение по завершению таймера?
			despawnBoxes(st);
			return null;
		}
		else if(event.equalsIgnoreCase(DESPAWN_BOXES))
		{
			despawnBoxes(st);
			return null;
		}
		else if(event.equalsIgnoreCase("30758-08.htm"))
		{
			if(cond == 10)
			{
				st.setCond(11);
			}
		}
		else if(event.equalsIgnoreCase("30835-08.htm"))
		{
			if(cond == 11)
			{
				if(player.isMageClass())
					st.setCond(12);
				else
					st.setCond(13);
			}
		}
		else if(event.equalsIgnoreCase("33942-09.htm"))
		{
			if(cond == 12)
			{
				st.setCond(14);
			}
		}
		else if(event.equalsIgnoreCase("33943-09.htm"))
		{
			if(cond == 13)
			{
				st.setCond(15);
			}
		}
		else if(event.equalsIgnoreCase("enter_instance"))
		{
			if(cond == 14 || cond == 15 || cond == 16)
			{
				if(cond != 16){
					st.setCond(16, false);
				}
				if(!enterInstance(st.getPlayer()))
					return "you cannot enter this instance";

			}
			return null;
		}
		else if(event.equalsIgnoreCase("continue_instance"))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection))
				{
					WindOfFate wof = (WindOfFate) player.getActiveReflection();
					if(wof != null)
						wof.initFriend(player);
					player.teleToLocation(-88504, 184680, -10476, player.getReflection()); 	//1 room
					return null;
				}
				return null;
			}
		}
		else if(event.equalsIgnoreCase("33996-02.htm"))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection))
				{
					player.sendPacket(UsmVideo.Q015.packet(player));
					reflection.despawnByGroup(GRAIL_SPAWN_GROUP);
					reflection.spawnByGroup(WIZARD_SPAWN_GROUP);
					player.sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_MYSTERIOUS_WIZARD, 10000, ScreenMessageAlign.TOP_CENTER));
					st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 12000);
				}
			}
		}
		else if(event.equalsIgnoreCase(WIZARD_MSG_TIMER_ID_VAR))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection) && st.getInt(WIZARD_TALKED_VAR) == 0)
				{
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TALK_TO_THE_MYSTERIOUS_WIZARD, 5000, ScreenMessageAlign.TOP_CENTER));
					st.startQuestTimer(WIZARD_MSG_TIMER_ID_VAR, 7000);
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase("33980-03.htm"))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection))
					player.sendPacket(new ExShowScreenMessage(NpcString.THIS_CHOICE_CANNOT_BE_REVERSED, 10000, ScreenMessageAlign.TOP_CENTER));
			}
		}
		else if(event.equalsIgnoreCase("33980-05.htm"))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection))
				{
					WindOfFate wof = (WindOfFate) player.getActiveReflection();
					if(wof != null)
						wof.clear();
					st.set(WIZARD_TALKED_VAR, 2, false);
					st.startQuestTimer(DESPAWN_WIZARD_TIMER_ID_VAR, 3000);
					reflection.despawnByGroup(HALTER_SPAWN_GROUP_1);
					reflection.spawnByGroup(HALTER_SPAWN_GROUP_2);
				}
			}
		}
		else if(event.equalsIgnoreCase(DESPAWN_WIZARD_TIMER_ID_VAR))
		{
			if(cond == 16)
			{
				final Reflection activeReflection = player.getActiveReflection();
				if(checkReflection(activeReflection))
					activeReflection.despawnByGroup(WIZARD_SPAWN_GROUP);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("finish_instance"))
		{
			if(cond == 16)
			{
				if(checkReflection(reflection))
				{
					st.setCond(17);
					st.startQuestTimer(END_INSTANCE_TIMER_ID_VAR, 3000);
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase(END_INSTANCE_TIMER_ID_VAR))
		{
			if(cond == 17)
			{
				final Reflection activeReflection = player.getActiveReflection();
				if(checkReflection(activeReflection))
					activeReflection.collapse();
			}
			return null;
		}
		else if(event.equalsIgnoreCase("33932-09.htm"))
		{
			if(cond == 17)
			{
				st.setCond(18);
			}
		}
		else if(event.equalsIgnoreCase("33931-03.htm"))
		{
			if(player.isMageClass())
				event = "33931-03a.htm";
		}
		else if(event.equalsIgnoreCase("change_class"))
		{
			if(cond == 18)
			{
				if(player.isMageClass())
					player.sendPacket(new ExChangeToAwakenedClass(player, npc, ClassId.SAIHA_RULER.getId()));
				else
					player.sendPacket(new ExChangeToAwakenedClass(player, npc, ClassId.RANGER_GRAVITY.getId()));
			}
			return null;
		}
		else if(event.equalsIgnoreCase("request_change_class"))
		{
			if(cond == 18)
			{
				event = player.isMageClass() ? "33931-04a.htm" : "33931-04.htm";

				final ClassId newClassId = player.isMageClass() ? ClassId.SAIHA_RULER : ClassId.RANGER_GRAVITY;
				player.setClassId(newClassId.getId(), false);
				player.broadcastUserInfo(true);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.AWAKENING));
				st.giveItems(ADENA_ID, 2030400);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.giveItems(46919, 1);
				st.finishQuest(SOUND_FANFARE2);
				player.sendPacket(new ExShowScreenMessage(NpcString.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES, 10000, ScreenMessageAlign.MIDDLE_CENTER, true));
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;

		if(npcId == MAGISTER_AYANTHE)
		{
			if(cond == 0)
				htmltext = "33942-01.htm";
			else if(cond == 1)
				htmltext = "33942-04.htm";
			else if(cond == 12)
				htmltext = "33942-05.htm";
			else if(cond == 14)
				htmltext = "33942-10.htm";
		}
		else if(npcId == MASTER_KATALIN)
		{
			if(cond == 0)
				htmltext = "33943-01.htm";
			else if(cond == 1)
				htmltext = "33943-04.htm";
			else if(cond == 13)
				htmltext = "33943-05.htm";
			else if(cond == 15)
				htmltext = "33943-10.htm";
		}
		else if(npcId == GRAND_MAGUSTER_ARKENIAS)
		{
			if(cond == 1)
				htmltext = "30174-01.htm";
			else if(cond == 2)
				htmltext = "30174-04.htm";
			else if(cond == 3)
			{
				htmltext = "30174-05.htm";
				st.setCond(4);
			}
			else if(cond == 4)
				htmltext = "30174-06.htm";
			else if(cond == 5)
				htmltext = "30174-07.htm";
			else if(cond == 6)
				htmltext = "30174-11.htm";
		}
		else if(npcId == ALCHEMISTS_MIXING_URN)
		{
			if(cond == 4)
				htmltext = "31149-01.htm";
			else if(cond == 5)
				htmltext = "31149-04.htm";
		}
		else if(npcId == HARDIN)
		{
			if(cond == 6)
				htmltext = "30832-01.htm";
			else if(cond == 7)
				htmltext = "30832-04.htm";
		}
		else if(npcId == LICH_KING_ICARUS)
		{
			if(cond == 7)
				htmltext = "30835-01.htm";
			else if(cond == 8)
				htmltext = "30835-06.htm";
			else if(cond == 11)
				htmltext = "30835-07.htm";
			else if(cond == 12)
				htmltext = "30835-09.htm";
		}
		else if(npcId == WITCH_RITASHA)
		{
			if(cond == 8)
				htmltext = "30758-01.htm";
			else if(cond == 10)
				htmltext = "30758-06.htm";
			else if(cond == 11)
				htmltext = "30758-09.htm";
		}
		else if(npcId == HIGH_PRIEST_GERETH)
		{
			if(cond == 14 || cond == 15)
				htmltext = "33932-01.htm";
			else if(cond == 16)
				htmltext = "33932-07.htm";
			else if(cond == 17)
				htmltext = "33932-08.htm";
			else if(cond == 18)
				htmltext = "33932-10.htm";
		}
		else if(npcId == KAIN_VAN_HALTER)
		{
			if(cond == 16)
			{
				final int talk = st.getInt(WIZARD_TALKED_VAR);
				if(talk == 0 || talk == 1)
					htmltext = "33979-01.htm";
				else if(talk == 2)
					htmltext = "33979-02.htm";
			}
			else if(cond == 17)
				htmltext = "33979-03.htm";
		}
		else if(npcId == GRAIL)
		{
			if(cond == 16)
				htmltext = "33996-01.htm";
		}
		else if(npcId == QUEEN_NAVARI)
		{
			if(cond == 18)
				htmltext = "33931-01.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		if(cond == 2)
		{
			if(npcId == QUEST_MONSTER_NEBULITE_EYE)
			{
				if(st.getQuestItemsCount(CRYSTAL_EYE) < 3)
					st.giveItems(CRYSTAL_EYE, 1);

				if(st.haveQuestItem(CRYSTAL_EYE, 3) && st.haveQuestItem(BROKEN_STONE_OF_PURITY, 3) && st.haveQuestItem(MIRACLE_DRUG_OF_ENCHANTMENT, 3))
				{
					st.setCond(3);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
			else if(npcId == QUEST_MONSTER_NEBULITE_WATCH)
			{
				if(st.getQuestItemsCount(BROKEN_STONE_OF_PURITY) < 3)
					st.giveItems(BROKEN_STONE_OF_PURITY, 1);

				if(st.haveQuestItem(CRYSTAL_EYE, 3) && st.haveQuestItem(BROKEN_STONE_OF_PURITY, 3) && st.haveQuestItem(MIRACLE_DRUG_OF_ENCHANTMENT, 3))
				{
					st.setCond(3);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
			else if(npcId == QUEST_MONSTER_NEBULITE_GOLEM)
			{
				if(st.getQuestItemsCount(MIRACLE_DRUG_OF_ENCHANTMENT) < 3)
					st.giveItems(MIRACLE_DRUG_OF_ENCHANTMENT, 1);

				if(st.haveQuestItem(CRYSTAL_EYE, 3) && st.haveQuestItem(BROKEN_STONE_OF_PURITY, 3) && st.haveQuestItem(MIRACLE_DRUG_OF_ENCHANTMENT, 3))
				{
					st.setCond(3);
				}
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		if (cond == 16)
		{

		}
		return null;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		final QuestState st = player.getQuestState(getId());
		if(st == null)
			return null;

		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		if(npcId == RITASHAS_BOX)
		{
			if(cond == 9)
			{
				if(_minigameOwner == player.getObjectId())
					return "33997-00.htm";
			}
		}
		else if(npcId == WIZARD_MYSTERIOUS)
		{
			if(cond == 16)
			{
				final int talk = st.getInt(WIZARD_TALKED_VAR);
				if(talk == 0 || talk == 1)
				{
					if(talk == 0)
						st.set(WIZARD_TALKED_VAR, 1, false);
					return "33980-00.htm";
				}
			}
		}
		return null;
	}

	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case MAGISTER_AYANTHE:
				if(player.isMageClass())
					return true;
				return false;
			case MASTER_KATALIN:
				if(!player.isMageClass())
					return true;
				return false;
		}
		return true;
	}

	private void despawnBoxes(QuestState st)
	{
		st.getPlayer().sendPacket(new ExSendUIEventPacket(st.getPlayer(), 1, 0, 0, 0));
		SpawnManager.getInstance().despawn(RITASHAS_BOX_SPAWN_GROUP);

		_minigameOwner = 0;
		_successBoxObjectId1 = 0;
		_successBoxObjectId2 = 0;
		_successBoxObjectId3 = 0;
		_successBoxObjectId4 = 0;
		_boxSearched1 = false;
		_boxSearched2 = false;
		_boxSearched3 = false;
		_boxSearched4 = false;
	}

	private boolean checkReflection(Reflection reflection)
	{
		return reflection != null && reflection.getInstancedZoneId() == INSTANCE_ZONE_ID;
	}

	private boolean enterInstance(Player player)
	{
		Reflection reflection = player.getActiveReflection();
		if(reflection != null)
		{
			if(player.canReenterInstance(INSTANCE_ZONE_ID))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(INSTANCE_ZONE_ID))
		{
			WindOfFate ioe = (WindOfFate) ReflectionUtils.enterReflection(player, new WindOfFate(player), INSTANCE_ZONE_ID);
			if(ioe != null)
				ioe.stageStart(1);
		}
		else
			return false;
		return true;
	}

	@Override
	public void onRestore(QuestState qs)
	{
		if(qs.isStarted())
			qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onAccept(QuestState qs)
	{
		qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onExit(QuestState qs)
	{
		qs.getPlayer().removeListener(_classChangeListener);
	}
}