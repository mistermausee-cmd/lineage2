package quests;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.StringTokenizer;

import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;

/**
 * @reworked to Lindvior by Bonux
**/
public class _255_Tutorial extends Quest
{
	private class PlayerEnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState st = player.getQuestState(_255_Tutorial.this);
			if(st == null)
			{
				newQuestState(player);
				st = player.getQuestState(_255_Tutorial.this);
			}
			onTutorialEvent(ENTER_WORLD_EVENT, false, "", st);
		}
	}

	// Events
	// Данные переменные не изменять, они вшиты в ядро.
	private static final String ENTER_WORLD_EVENT = "EW"; // Вход в мир.
	private static final String QUEST_TIMER_EVENT = "QT"; // Квестовый таймер.
	private static final String QUESTION_MARK_EVENT = "QM"; // Вопросытельный знак.
	private static final String CLIENT_EVENT = "CE"; // Дейтсвия клиента. (100 - Class Change, 200 - Death, 300 - Level UP)
	private static final String TUTORIAL_BYPASS_EVENT = "BYPASS"; // Использование байпасса в туториале.
	private static final String TUTORIAL_LINK_EVENT = "LINK"; // Использование ссылки в туториале.

	// table for Question Mark Clicked (5) 2nd class transfer [raceId, html]
	public final TIntObjectMap<String> QMCc2 = new TIntObjectHashMap<String>();
	// table for Question Mark Clicked (6) 3rd class transfer [raceId, html]
	public final TIntObjectMap<String> QMCc3 = new TIntObjectHashMap<String>();

	private final OnPlayerEnterListener _playerEnterListener = new PlayerEnterListener();

	public _255_Tutorial()
	{
		super(PARTY_NONE, ONETIME);

		QMCc2.put(0, "tutorial_2nd_ct_human.htm");
		QMCc2.put(1, "tutorial_2nd_ct_elf.htm");
		QMCc2.put(2, "tutorial_2nd_ct_dark_elf.htm");
		QMCc2.put(3, "tutorial_2nd_ct_orc.htm");
		QMCc2.put(4, "tutorial_2nd_ct_dwarf.htm");
		QMCc2.put(5, "tutorial_2nd_ct_kamael.htm");

		QMCc3.put(0, "tutorial_3rd_ct_human.htm");
		QMCc3.put(1, "tutorial_3rd_ct_elf.htm");
		QMCc3.put(2, "tutorial_3rd_ct_dark_elf.htm");
		QMCc3.put(3, "tutorial_3rd_ct_orc.htm");
		QMCc3.put(4, "tutorial_3rd_ct_dwarf.htm");
		QMCc3.put(5, "tutorial_3rd_ct_kamael.htm");

		CharListenerList.addGlobal(_playerEnterListener);
	}

	@Override
	public String onTutorialEvent(final String event, final boolean quest, final String value, final QuestState st)
	{
		String html = "";

		Player player = st.getPlayer();

		// Вход в мир
		if(event.equalsIgnoreCase(ENTER_WORLD_EVENT))
		{
			if(st.getInt("intro_ert_video") == 0)
			{
				st.set("intro_ert_video", 1);
				if(player.getRace() == Race.ERTHEIA)
					player.sendPacket(UsmVideo.ERTHEIA.packet(player));
				else
					player.sendPacket(UsmVideo.HEROES.packet(player));
			}

			int level = player.getLevel();
			if(level < 6)
			{
				int uc = st.getInt("uc_memo");
				if(uc == 0)
				{
					st.startQuestTimer("QT", 10000);
					st.set("ex_state", "1");
				}
				else if(uc == 1)
				{
					st.showQuestionMark(false, 1);
					st.playTutorialVoice("tutorial_voice_006");
					st.playSound(SOUND_TUTORIAL);
				}
			}
			else
			{
				// Внимание! Дальше порядок квестов важен. Чем выше находиться квест, тем он приоритетнее над другими.

				// Квесты на профессию:
				if(checkQuest(player, 10331)) // Начало судьбы
				{
					st.showQuestionMark(true, 10331);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10360)) // Путь Судьбы
				{
					st.showQuestionMark(true, 10360);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10341) || checkQuest(player, 10342) || checkQuest(player, 10343) || checkQuest(player, 10344) || checkQuest(player, 10345) || checkQuest(player, 10346)) // Роковой день
				{
					st.showQuestionMark(true, 101);
					st.playSound(SOUND_TUTORIAL);
				}

				// Квесты на профессию (Артей):
				else if(checkQuest(player, 10751)) // Ветра судьбы, Встреча
				{
					st.showQuestionMark(true, 10751);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10752)) // Ветра судьбы, Тень
				{
					st.showQuestionMark(true, 10752);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10753)) // Ветра судьбы, Выбор
				{
					st.showQuestionMark(true, 10753);
					st.playSound(SOUND_TUTORIAL);
				}

				// Квест на дуал-класс (Артей):
				else if(checkQuest(player, 10472)) // Ветра судьбы, Тень
				{
					st.showQuestionMark(true, 10472);
					st.playSound(SOUND_TUTORIAL);
				}

				// Цепочка квестов для прокачки:
				else if(checkQuest(player, 10390)) // Письмо Кекропуса (1/9)
				{
					st.showQuestionMark(true, 1);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10393)) // Письмо Кекропуса: Ключ к разгадке (2/9)
				{
					st.showQuestionMark(true, 10393);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10397)) // Письмо Кекропуса: Странный Знак (3/9)
				{
					st.showQuestionMark(true, 10397);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10401)) // Письмо Кекропуса: Расшифровка Знака (4/9)
				{
					st.showQuestionMark(true, 10401);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10404)) // Письмо Кекропуса: Скрытый смысл (5/9)
				{
					st.showQuestionMark(true, 10404);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10408)) // Письмо Кекропуса: Болото Криков (6/9)
				{
					st.showQuestionMark(true, 10408);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10411)) // Письмо Кекропуса: Лес Неупокоенных (6/9)
				{
					st.showQuestionMark(true, 10411);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10414)) // Письмо Кекропуса: Смельчак (7/9)
				{
					st.showQuestionMark(true, 10414);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10415)) // Письмо Кекропуса: Мудрец (7/9)
				{
					st.showQuestionMark(true, 10415);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10419)) // Письмо Кекропуса: Где лагерь? (8/9)
				{
					st.showQuestionMark(true, 10419);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10424)) // Письмо Кекропуса: Где Белус? (8/9)
				{
					st.showQuestionMark(true, 10424);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10530)) // Письмо Кекропуса, Странные Драконы
				{
					st.showQuestionMark(true, 10530);
					st.playSound(SOUND_TUTORIAL);
				}

				// Цепочка квестов для прокачки (Артей):
				else if(checkQuest(player, 10755)) // Письмо Серении, Холм Ветров
				{
					st.showQuestionMark(true, 10755);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10760)) // Письмо от Серении, Лагерь Орков
				{
					st.showQuestionMark(true, 10760);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10769)) // Письмо от Серении, Башня Крумы - 1
				{
					st.showQuestionMark(true, 10769);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10774)) // Письмо Серении, Башня Крумы - 2
				{
					st.showQuestionMark(true, 10774);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10779)) // Письмо Серении, Море Спор
				{
					st.showQuestionMark(true, 10779);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10782)) // Письмо от Королевы
				{
					st.showQuestionMark(true, 10782);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10785)) // Письмо Серении, Поле Брани
				{
					st.showQuestionMark(true, 10785);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10789)) // Письмо Серении, Болото Криков
				{
					st.showQuestionMark(true, 10789);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10792)) // Письмо Серении, Лес Неупокоенных
				{
					st.showQuestionMark(true, 10792);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10795)) // Письмо Серении, Стена Аргоса
				{
					st.showQuestionMark(true, 10795);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10521)) // Письмо Серении, Лагерь Фавнов Варка
				{
					st.showQuestionMark(true, 10521);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10525)) // Письмо Серении,  Застава Орков Кетра
				{
					st.showQuestionMark(true, 10525);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10798)) // Письмо Серении, Долина Драконов
				{
					st.showQuestionMark(true, 10798);
					st.playSound(SOUND_TUTORIAL);
				}

				// Прокачка после перерождения:
				else if(checkQuest(player, 10712)) // Песнь Барда - 1
				{
					st.showQuestionMark(true, 10712);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10717)) // Песнь Барда - 2
				{
					st.showQuestionMark(true, 10717);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10720)) // Песнь Барда - 3
				{
					st.showQuestionMark(true, 10720);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10723)) // Песнь Барда - 4
				{
					st.showQuestionMark(true, 10723);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10726)) // Песнь Барда - 5
				{
					st.showQuestionMark(true, 10726);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10731)) // Песнь Барда - 6
				{
					st.showQuestionMark(true, 10731);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 10461)) // Вобравший скрытые силы
				{
					st.showQuestionMark(false, 1);
					st.playSound(SOUND_TUTORIAL);
				}

				// Важные квесты:
				else if(checkQuest(player, 10301) && !st.haveQuestItem(17725)) // Тень страха, темно-красный Туман
				{
					st.showQuestionMark(true, 10301);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 192)) // Семь Печатей, Цепь Подозрительных Происшествий
				{
					st.showQuestionMark(true, 34);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 144)) // Пайлака - Раненый Дракон
				{
					st.showQuestionMark(true, 32);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 129)) // Пайлака - Наследие Дьявола
				{
					st.showQuestionMark(true, 31);
					st.playSound(SOUND_TUTORIAL);
				}
				else if(checkQuest(player, 128)) // Пайлака - Песня льда и огня
				{
					st.showQuestionMark(true, 30);
					st.playSound(SOUND_TUTORIAL);
				}
			}
			checkHermenkusMsg(st);
		}

		// Обработка таймера QT
		else if(event.equalsIgnoreCase(QUEST_TIMER_EVENT))
		{
			int exState = st.getInt("ex_state");
			if(exState == 1)
			{
				if(player.getRace() == Race.ERTHEIA)
				{
					if(st.getInt("@queen_called") == 0)
					{
						for(NpcInstance tempNpc : player.getAroundNpc(2000, 500))
						{
							if(tempNpc.getNpcId() == 33931)
							{
								st.set("@queen_called", 1);
								player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_SERENITY_IS_CAUSING_YOU, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							}
						}
					}
					html = "tutorial_start_e.htm";
				}
				else
					html = "tutorial_start.htm";

				st.set("ex_state", "2");
				st.cancelQuestTimer("QT");
				st.startQuestTimer("QT", 30000);
			}
			else if(exState == 2)
			{
				st.playTutorialVoice("tutorial_voice_002");
				st.set("ex_state", "0");
			}
			else if(exState == 3)
			{
				st.playTutorialVoice("tutorial_voice_008");
				st.set("ex_state", "-1");
			}
		}

		// Tutorial close
		else if(event.equalsIgnoreCase(TUTORIAL_BYPASS_EVENT))
		{
			if(value.startsWith("TE"))
			{
				st.cancelQuestTimer("TE");
				int event_id = 0;
				if(!value.equalsIgnoreCase("TE"))
					event_id = Integer.valueOf(value.substring(2));
				if(event_id == 0) // Закрыть окно.
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				else if(event_id == 1) // Способ перемещения.
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					st.playTutorialVoice("tutorial_voice_006");
					st.showQuestionMark(false, 1);
					st.playSound(SOUND_TUTORIAL);
					st.startQuestTimer("QT", 30000);
					st.set("ex_state", "3");
				}
				else if(event_id == 2) // Передвижение.
				{
					st.playTutorialVoice("tutorial_voice_003");
					html = "tutorial_move.htm";
					st.onTutorialClientEvent(1);
					st.set("ex_state", "-1");
				}
				else if(event_id == 3) // Выйти из режима обучения (Перемещение).
				{
					html = "tutorial_move_exit.htm";
					st.onTutorialClientEvent(0);
				}
				else if(event_id == 4)	// Телепорт к Королеве Артеас Сирении (Бенон)
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(-80565, 251763, -3080, ReflectionManager.MAIN);
				}
				else if(event_id == 5)	// Телепорт к Магистру Арис (Бенон)
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(-82139, 249852, -3360, ReflectionManager.MAIN);
				}
				else if(event_id == 6)	// Телепорт в Глудин, старт квеста 10755/10760
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(-80712, 149992, -3069, ReflectionManager.MAIN);
					st.takeItems(39486, 1);
				}
				else if(event_id == 7)	// Телепорт в Дион, старт квеста 10769/10774
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(15784, 142965, -2731, ReflectionManager.MAIN);
					st.takeItems(39595, 1);
				}
				else if(event_id == 8)	// Телепорт в Орен, старт квеста 10779/10393
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					if (player.getClassId().isOfRace(Race.ERTHEIA))
					{
						player.teleToLocation(83633, 53064, -1456, ReflectionManager.MAIN);
						st.takeItems(39574, 1);
					}
					else
					{
						player.teleToLocation(83656, 55528, -1537, ReflectionManager.MAIN);
						st.takeItems(37113, 1);
					}
				}
				else if(event_id == 9)	// Телепорт в Аден, старт квеста 10782/10785/10401
				{
					if (player.getClassId().isOfRace(Race.ERTHEIA))
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(147448, 22760, -2017, ReflectionManager.MAIN);
						st.takeItems(39576, 1);
					}
					else
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(147632, 24664, -1991, ReflectionManager.MAIN);
						st.takeItems(37115, 1);
						st.takeItems(37116, 1);
					}
				}
				else if(event_id == 10)	// Телепорт в Руну, старт квеста 10789/10792/10408/10411
				{
					if (player.getClassId().isOfRace(Race.ERTHEIA))
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(36563, -49178, -1128, ReflectionManager.MAIN);
						st.takeItems(39582, 1);
					}
					else
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(42808, -48024, -822, ReflectionManager.MAIN);
						st.takeItems(37117, 1);
					}
				}
				else if(event_id == 11)	// Телепорт в Годдард, старт квеста 10795/10414/10415
				{
					if (player.getClassId().isOfRace(Race.ERTHEIA))
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(147705, -53066, -2731, ReflectionManager.MAIN);
						st.takeItems(39582, 1);
					}
					else
					{
						player.sendPacket(TutorialCloseHtmlPacket.STATIC);
						player.teleToLocation(147560, -56424, -2806, ReflectionManager.MAIN);
						st.takeItems(37117, 1);
						st.takeItems(37119, 1);
					}
				}
				else if(event_id == 12)	// Телепорт в Гиран, старт квеста 10798 / 10530
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(86733, 148616, -3400, ReflectionManager.MAIN);
					st.takeItems(39586, 1);
				}
				else if(event_id == 14)	// Телепорт в Орен, старт квеста 10397
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(80872, 56040, -1585, ReflectionManager.MAIN);
					st.takeItems(37113, 1);
					st.takeItems(37114, 1);
				}
				else if(event_id == 15)	// Телепорт в Годдард, старт квеста 10424/10419/10525/10521
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147705, -53066, -2731, ReflectionManager.MAIN);
					st.takeItems(37119, 1);
				}
				else if(event_id == 17)	// Телепорт в Говорящий остров, старт квеста 10712
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(-113672, 256008, -1532, ReflectionManager.MAIN);
					st.takeItems(39553, -1);
				}
				else if(event_id == 18)	// Телепорт в Магмельд, старт квеста 10717/10720
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(207249, 87023, -1024, ReflectionManager.MAIN);
					st.takeItems(39554, -1);
				}
				else if(event_id == 19)	// Телепорт в Руну, старт квеста 10723
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(44094, -48299, -792, ReflectionManager.MAIN);
					st.takeItems(39556, -1);
				}
				else if(event_id == 20)	// Телепорт в Аден, старт квеста 10726/10731
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147080, 27288, -2230, ReflectionManager.MAIN);
					st.takeItems(39557, -1);
				}
				else if(event_id == 21)	// Телепорт в Хейн, старт квеста 10461
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(111379, 221137, -3536, ReflectionManager.MAIN);
				}
			}
		}

		// Client Event
		else if(event.equalsIgnoreCase(CLIENT_EVENT))
		{
			int event_id = Integer.valueOf(value);
			if(event_id == 1 && player.getLevel() < 6) // Способ перемещения.
			{
				if(player.getRace() == Race.ERTHEIA)
					html = "tutorial_way_to_move_e.htm";
				else
					html = "tutorial_way_to_move.htm";

				st.playSound(SOUND_TUTORIAL);
				st.playTutorialVoice("ItemSound.quest_tutorial");
				st.set("uc_memo", "1");
				st.set("ex_state", "-1");
			}
			else if(event_id == 100) // Смена класса.
				checkHermenkusMsg(st);
			else if(event_id == 200 && player.getLevel() < 10 && st.getInt("die") == 0) // Смерть.
			{
				st.playTutorialVoice("tutorial_voice_016");
				st.playSound(SOUND_TUTORIAL);
				st.set("die", "1");
				st.showQuestionMark(false, 8);
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 300) // Повышение уровня.
			{
				final int level = player.getLevel();

				// Выдаем книгу путишественника.
				if(level >= 40 && st.getInt("advent_book") == 0)
				{
					if(!st.haveQuestItem(32777))
						st.giveItems(32777, 1);
					st.set("advent_book", 1);
				}

				// Выдаем книгу перерождения.
				if(level >= 85 && st.getInt("awake_book") == 0)
				{
					if(!st.haveQuestItem(32778))
						st.giveItems(32778, 1);
					st.set("awake_book", 1);
				}

				// Базовый туториал:
				if(level == 10 && checkTutorial(st, 27)) // О штрафе при смерте
				{
					// st.playTutorialVoice("tutorial_voice_???");
				}
				else if(level == 15 && checkTutorial(st, 17)) // О квесте на волка
				{
					// st.playTutorialVoice("tutorial_voice_???");
				}

				// Квесты на профессию:
				if(checkQuest(player, 10331) && checkTutorial(st, 10331)) // Начало судьбы
				{
					// st.playTutorialVoice("tutorial_voice_???");
				}
				else if(checkQuest(player, 10360) && checkTutorial(st, 10360)) // Путь Судьбы
				{
					// st.playTutorialVoice("tutorial_voice_???");
				}
				else if((checkQuest(player, 10341) || checkQuest(player, 10342) || checkQuest(player, 10343) || checkQuest(player, 10344) || checkQuest(player, 10345) || checkQuest(player, 10346)) && checkTutorial(st, 101)) // Роковой день
				{
					// st.playTutorialVoice("tutorial_voice_???");
				}

				// Квесты на профессию (Артей):
				else if(checkQuest(player, 10751) && checkTutorial(st, 10751)) // Ветра судьбы, Встреча
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10752) && checkTutorial(st, 10752)) // Ветра судьбы, Тень
				{
					if(player.getClassId().isMage())
						player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					else
						player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10753) && checkTutorial(st, 10753)) // Ветра судьбы, Выбор
				{
					if(player.getClassId().isMage())
						player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					else
						player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}

				// Квест на дуал-класс (Артей):
				else if(checkQuest(player, 10472) && checkTutorial(st, 10472)) // Ветра судьбы, Тень
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}

				// Цепочка квестов для прокачки:
				else if(checkQuest(player, 10390) && checkTutorial(st, 10390)) // Письмо Кекропуса (1/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10393) && checkTutorial(st, 10393)) // Письмо Кекропуса: Ключ к разгадке (2/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10397) && checkTutorial(st, 10397)) // Письмо Кекропуса: Странный Знак (3/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10401) && checkTutorial(st, 10401)) // Письмо Кекропуса: Расшифровка Знака (4/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10404) && checkTutorial(st, 10404)) // Письмо Кекропуса: Скрытый смысл (5/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10408) && checkTutorial(st, 10408)) // Письмо Кекропуса: Болото Криков (6/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10411) && checkTutorial(st, 10411)) // Письмо Кекропуса: Лес Неупокоенных (6/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10414) && checkTutorial(st, 10414)) // Письмо Кекропуса: Смельчак (7/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10415) && checkTutorial(st, 10415)) // Письмо Кекропуса: Мудрец (7/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10419) && checkTutorial(st, 10419)) // Письмо Кекропуса: Где лагерь? (8/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10424) && checkTutorial(st, 10424)) // Письмо Кекропуса: Где Белус? (8/9)
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10530) && checkTutorial(st, 10530)) // //Письмо Кекропуса, Странные Драконы
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}

				// Цепочка квестов для прокачки (Артей):
				else if(checkQuest(player, 10755) && checkTutorial(st, 10755)) // Письмо Серении, Холм Ветров
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10760) && checkTutorial(st, 10760)) // Письмо от Серении, Лагерь Орков
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10769) && checkTutorial(st, 10769)) // Письмо от Серении, Башня Крумы - 1
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10774) && checkTutorial(st, 10774)) // Письмо Серении, Башня Крумы - 2
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10779) && checkTutorial(st, 10779)) // Письмо Серении, Море Спор
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10782) && checkTutorial(st, 10782)) // Письмо от Королевы
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10785) && checkTutorial(st, 10785)) // Письмо Серении, Поле Брани
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10789) && checkTutorial(st, 10789)) // Письмо Серении, Болото Криков
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10792) && checkTutorial(st, 10792)) // Письмо Серении, Лес Неупокоенных
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10795) && checkTutorial(st, 10795)) // Письмо Серении, Стена Аргоса
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10521) && checkTutorial(st, 10521)) // Письмо Серении, Кетра
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10525) && checkTutorial(st, 10525)) // Письмо Серении, Варка
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else if(checkQuest(player, 10798) && checkTutorial(st, 10798)) // Письмо Серении, Долина Драконов
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}

				// Прокачка после перерождения:
				else if(checkQuest(player, 10712) && checkTutorial(st, 10712)) // Песнь Барда - 1
				{
					//
				}
				else if(checkQuest(player, 10717) && checkTutorial(st, 10717)) // Песнь Барда - 2
				{
					//
				}
				else if(checkQuest(player, 10720) && checkTutorial(st, 10720)) // Песнь Барда - 3
				{
					//
				}
				else if(checkQuest(player, 10723) && checkTutorial(st, 10723)) // Песнь Барда - 4
				{
					//
				}
				else if(checkQuest(player, 10726) && checkTutorial(st, 10726)) // Песнь Барда - 5
				{
					//
				}
				else if(checkQuest(player, 10731) && checkTutorial(st, 10731)) // Песнь Барда - 6
				{
					//
				}
				else if(checkQuest(player, 10461) && checkTutorial(st, 1)) // Вобравший скрытые силы
				{
					//
				}

				// Важные квесты:
				else if(checkQuest(player, 10301) && !st.haveQuestItem(17725) && checkTutorial(st, 10301)) // Тень страха, темно-красный Туман
				{
					//
				}
				else if(checkQuest(player, 192) && checkTutorial(st, 34)) // Семь Печатей, Цепь Подозрительных Происшествий
				{
					//
				}
				else if(checkQuest(player, 144) && checkTutorial(st, 32)) // Пайлака - Раненый Дракон
				{
					//
				}
				else if(checkQuest(player, 129) && checkTutorial(st, 31)) // Пайлака - Наследие Дьявола
				{
					//
				}
				else if(checkQuest(player, 128) && checkTutorial(st, 30)) // Пайлака - Песня льда и огня
				{
					//
				}

				// Страницы с книги:
				else if(level == 40) // О локации: Башня Слоновой Кости.
				{
					if(st.getInt("lvl") < 40)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "40");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 50) // О локации: Лес Разбойников.
				{
					if(st.getInt("lvl") < 50)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "50");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 55) // О локации: Забытые Равнины.
				{
					if(st.getInt("lvl") < 55)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "55");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 60) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 60)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "60");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 66) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 66)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "65");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 75) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 75)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "75");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 80) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 80)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "80");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 85) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 85)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "85");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 90) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 90)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "90");
						st.showQuestionMark(false, 1);
					}
				}
				else if(level == 95) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 95)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "95");
						st.showQuestionMark(false, 1);
					}
				}
			}
		}

		// Question mark clicked
		else if(event.equalsIgnoreCase(QUESTION_MARK_EVENT))
		{
			int tutorialId = Integer.valueOf(value);
			if(!quest)
			{
				if(tutorialId == 1) // Чат
				{
					int lvl = player.getLevel();
					if(lvl < 6)
					{
						st.set("ex_state", "-1");
						if(player.getClassId().isOfRace(Race.ERTHEIA))
							html = "tutorial_way_to_move_e.htm";
						else
							html = "tutorial_way_to_move.htm";
					}
					else if(checkQuest(player, 10461)) // TODO: Найти правильный ID туториала.
					{
						html = "tutorial_10461.htm";
						startTutorialQuest(st, 10461, 1, -1);
					}
					else
					{
						if(lvl == 40) // Башня Слоновой Кости.
							st.showTutorialClientHTML("Guide_Ad_4050_01_ivorytower"); // На оффе этого нету.
						else if(lvl == 50) // Лес разбойников.
							st.showTutorialClientHTML("Guide_Ad_5055_01_outlaws");
						else if(lvl == 55) // Забытые Равнины.
							st.showTutorialClientHTML("Guide_Ad_5560_01_forsaken");
						else if(lvl == 60) // Глава "Путишествия".
							st.showTutorialClientHTML("Guide_Ad_6065_00_main"); // На оффе этого нету.
						else if(lvl == 65) // Глава "Путишествия".
							st.showTutorialClientHTML("Guide_Ad_6570_00_main");
						else if(lvl == 70) // Глава "Путишествия".
							st.showTutorialClientHTML("Guide_Ad_7075_00_main");
						else if(lvl == 75) // Глава "Путишествия".
							st.showTutorialClientHTML("Guide_Ad_7580_00_main");
						else if(lvl == 80) // Глава "Путишествия".
							st.showTutorialClientHTML("Guide_Ad_8085_00_main");
						else if(lvl == 85) // Глава "Перерождение".
							st.showTutorialClientHTML("Guide_Aw_8590_00_main");
						else if(lvl == 90) // Глава "Перерождение".
							st.showTutorialClientHTML("Guide_Aw_9095_00_main");
						else if(lvl == 95) // Глава "Перерождение".
							st.showTutorialClientHTML("Guide_Aw_9599_00_main");
						return null;
					}
				}
				else if(tutorialId == 8) // Смерть
					html = "tutorial_die.htm";
				else if(tutorialId == 17) // О питомцах
					html = "tutorial_pet_quest.htm";
				else if(tutorialId == 27) // Штраф за Смерть
					html = "tutorial_death_penalty.htm";
			}
			else
			{
				if(tutorialId == 30) // Пайлака - Песня льда и огня
				{
					if(checkQuest(player, 128))
						html = "tutorial_pailaka_49.htm";
				}
				else if(tutorialId == 31) // Пайлака - Наследие Дьявола
				{
					if(checkQuest(player, 129))
						html = "tutorial_pailaka_61.htm";
				}
				else if(tutorialId == 32) // Пайлака - Раненый Дракон
				{
					if(checkQuest(player, 144))
						html = "tutorial_pailaka_73.htm";
				}
				else if(tutorialId == 34) // Эпический квест семи печатей
				{
					if(checkQuest(player, 192))
						html = "tutorial_epic_quest.htm";
				}
				else if(tutorialId == 101) // Роковой день
				{
					if(checkQuest(player, 10341) || checkQuest(player, 10342) || checkQuest(player, 10343) || checkQuest(player, 10344) || checkQuest(player, 10345) || checkQuest(player, 10346))
					{
						if(QMCc3.containsKey(player.getRace().ordinal()))
							html = QMCc3.get(player.getRace().ordinal());
					}
				}
				else if(checkQuest(player, tutorialId))
				{
					if(tutorialId == 10301) // Письмо Рады
					{
						html = "tutorial_radas_letter.htm";
						st.set("radas_letter", 1);
						st.giveItems(17725, 1);
						st.playSound(SOUND_MIDDLE);
					}
					else if(tutorialId == 10331) // Начало судьбы
						html = "tutorial_1st_ct.htm";
					else if(tutorialId == 10360) // Путь Судьбы
					{
						if(player.getRace() == Race.HUMAN && player.isMageClass())
							html = "tutorial_2nd_ct_human_m.htm";
						else if(player.getRace() == Race.ELF && player.isMageClass())
							html = "tutorial_2nd_ct_elf_m.htm";
						else if(QMCc2.containsKey(player.getRace().ordinal()))
							html = QMCc2.get(player.getRace().ordinal());
					}
					else if(tutorialId == 10390)
					{
						startTutorialQuest(st, 10390, 1, -1);
						html = "tutorial_10390.htm";
					}
					else if(tutorialId == 10393) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10393, 2, 37113);
						html = "tutorial_10393.htm";
					}
					else if(tutorialId == 10397) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10397, 2, 37114);
						html = "tutorial_10397.htm";
					}
					else if(tutorialId == 10401) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10401, 2, 37115);
						html = "tutorial_10401.htm";
					}
					else if(tutorialId == 10404) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10404, 2, 37116);
						html = "tutorial_10404.htm";
					}
					else if(tutorialId == 10408) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10408, 2, 37117);
						html = "tutorial_10408.htm";
					}
					else if(tutorialId == 10411) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10411, 2, 37117);
						html = "tutorial_10411.htm";
					}
					else if(tutorialId == 10414) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10414, 2, 37117);
						html = "tutorial_10414.htm";
					}
					else if(tutorialId == 10415) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10415, 2, 37117);
						html = "tutorial_10415.htm";
					}
					else if(tutorialId == 10419) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10419, 2, 37119);
						html = "tutorial_10419.htm";
					}
					else if(tutorialId == 10530) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10530, 2, 39586);
						html = "tutorial_10530.htm";
					}
					else if(tutorialId == 10424) // Письмо Кекропуса
					{
						startTutorialQuest(st, 10424, 2, 37119);
						html = "tutorial_10424.htm";
					}
					else if(tutorialId == 10472) // Ветра судьбы, Тень
						html = "tutorial_ertheia_dualclass.htm";
					else if(tutorialId == 10712) // Песнь Барда - 1
					{
						html = "tutorial_10712.htm";
						startTutorialQuest(st, 10712, 1, 39553);
					}
					else if(tutorialId == 10717) // Песнь Барда - 2
					{
						html = "tutorial_10717.htm";
						startTutorialQuest(st, 10717, 1, 39554);
					}
					else if(tutorialId == 10720) // Песнь Барда - 3
					{
						html = "tutorial_10720.htm";
						startTutorialQuest(st, 10720, 1, 39554);
					}
					else if(tutorialId == 10723) // Песнь Барда - 4
					{
						html = "tutorial_10723.htm";
						startTutorialQuest(st, 10723, 1, 39556);
					}
					else if(tutorialId == 10726) // Песнь Барда - 5
					{
						html = "tutorial_10726.htm";
						startTutorialQuest(st, 10726, 1, 39557);
					}
					else if(tutorialId == 10731) // Песнь Барда - 6
					{
						html = "tutorial_10731.htm";
						startTutorialQuest(st, 10731, 1, 39557);
					}
					else if(tutorialId == 10751) // Ветра судьбы, Встреча
						html = "tutorial_1st_ct_ertheia.htm";
					else if(tutorialId == 10752) // Ветра судьбы, Обещание
						html = "tutorial_2nd_ct_ertheia.htm";
					else if(tutorialId == 10753) // Ветра судьбы, Выбор
						html = "tutorial_3rd_ct_ertheia.htm";
					else if(tutorialId == 10755) // Письмо Серении
					{
						startTutorialQuest(st, 10755, 1, 39486);
						html = "tutorial_10755.htm";
					}
					else if(tutorialId == 10760) // Письмо Серении
					{
						startTutorialQuest(st, 10760, 2, 39486);
						html = "tutorial_10760.htm";
					}
					else if(tutorialId == 10769) // Письмо Серении
					{
						startTutorialQuest(st, 10769, 2, 39595);
						html = "tutorial_10769.htm";
					}
					else if(tutorialId == 10774) // Письмо Серении
					{
						startTutorialQuest(st, 10774, 2, 39595);
						html = "tutorial_10774.htm";
					}
					else if(tutorialId == 10779) // Письмо Серении
					{
						startTutorialQuest(st, 10779, 2, 39574);
						html = "tutorial_10779.htm";
					}
					else if(tutorialId == 10782) // Письмо Серении
					{
						startTutorialQuest(st, 10782, 2, 39576);
						html = "tutorial_10782.htm";
					}
					else if(tutorialId == 10785) // Письмо Серении
					{
						startTutorialQuest(st, 10785, 2, 39576);
						html = "tutorial_10785.htm";
					}
					else if(tutorialId == 10789) // Письмо Серении
					{
						startTutorialQuest(st, 10789, 2, 39582);
						html = "tutorial_10789.htm";
					}
					else if(tutorialId == 10792) // Письмо Серении
					{
						startTutorialQuest(st, 10792, 2, 39582);
						html = "tutorial_10792.htm";
					}
					else if(tutorialId == 10795) // Письмо Серении
					{
						startTutorialQuest(st, 10795, 2, 39584);
						html = "tutorial_10795.htm";
					}
					else if(tutorialId == 10798) // Письмо Серении
					{
						startTutorialQuest(st, 10798, 2, 39586);
						html = "tutorial_10798.htm";
					}
					else if(tutorialId == 10521) // Письмо Серении
					{
						startTutorialQuest(st, 10521, 2, 37119);
						html = "tutorial_10521.htm";
					}
					else if(tutorialId == 10525) // Письмо Серении
					{
						startTutorialQuest(st, 10525, 2, 37119);
						html = "tutorial_10525.htm";
					}
				}
			}

			if(html.isEmpty())
			{
				player.sendPacket(SystemMsg.CANNOT_SHOW_BECAUSE_THE_CONDITIONS_ARE_NOT_MET);
				return null;
			}
		}

		if(html.isEmpty())
			return null;

		st.showTutorialHTML(html);
		return null;
	}

	@Override
	public String onEvent(final String event, final QuestState st, final NpcInstance npc)
	{
		final StringTokenizer tokenizer = new StringTokenizer(event, "_");
		final String cmd = tokenizer.nextToken();
		if(cmd.equalsIgnoreCase(QUEST_TIMER_EVENT))
			notifyTutorialEvent(event, false, "", st);
		return null;
	}

	private boolean checkQuest(Player player, int questId)
	{
		Quest q = QuestHolder.getInstance().getQuest(questId);
		if(q != null)
		{
			QuestState qs = player.getQuestState(q);
			if(qs == null || qs.isNotAccepted())
				return q.checkStartCondition(null, player) == null;
		}
		return false;
	}

	private boolean checkTutorial(QuestState st, int tutorialId)
	{
		if(st.getInt("tutorial_" + tutorialId) == 0)
		{
			st.playSound(SOUND_TUTORIAL);
			st.set("tutorial_" + tutorialId, 1);
			st.showQuestionMark(true, tutorialId);
			return true;
		}
		return false;
	}

	private void checkHermenkusMsg(QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return;

		// Сообщение от гермункуса.
		if(!player.getClassId().isOfRace(Race.ERTHEIA) && player.getLevel() >= 85 && player.isBaseClassActive() && player.getClassId().isOfLevel(ClassLevel.THIRD))
		{
			if(st.getInt("herm_msg_showed") == player.getClassId().getId())
				return;

			int classId = 0;
			for(ClassId c : ClassId.VALUES)
			{
				if(c.isOfLevel(ClassLevel.AWAKED) && c.childOf(player.getClassId()))
				{
					classId = c.getId();
					break;
				}
			}
			if(!player.getVarBoolean("GermunkusUSM"))
			{
				player.sendPacket(new ExCallToChangeClass(classId, true));
				player.sendPacket(new ExShowScreenMessage(NpcString.FREE_THE_GIANT_FROM_HIS_IMPRISONMENT_AND_AWAKEN_YOUR_TRUE_POWER, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				st.set("herm_msg_showed", player.getClassId().getId(), false);
			}
		}
	}

	private void startTutorialQuest(QuestState st, int questId, int cond, int giveItemId)
	{
		if(!checkQuest(st.getPlayer(), questId))
			return;

		if(st.getPlayer().getQuestState(questId) == null)
		{
			Quest quest = QuestHolder.getInstance().getQuest(questId);
			quest.newQuestState(st.getPlayer()).setCond(cond);
		}
		else
			st.getPlayer().getQuestState(questId).setCond(cond);

		if(giveItemId > 0)
		{
			if(!st.haveQuestItem(giveItemId))
				st.giveItems(giveItemId, 1, false);
		}
	}

	@Override
	public boolean isVisible(Player player)
	{
		return false;
	}
}