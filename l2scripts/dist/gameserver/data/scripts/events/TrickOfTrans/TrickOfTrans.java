package events.TrickOfTrans;

import java.util.ArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trick Of Transmutation Event
 */
public class TrickOfTrans implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(TrickOfTrans.class);

	private static class EventListeners implements OnDeathListener, OnPlayerEnterListener
	{
		/**
		 * Обработчик смерти мобов, управляющий эвентовым дропом
		 */
		@Override
		public void onDeath(final Creature cha, final Creature killer)
		{
			if(_active && Functions.SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_TRICK_OF_TRANS_CHANCE * killer.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
				((NpcInstance) cha).dropItem(killer.getPlayer(), A_CHEST_KEY, 1);
		}

		/**
		 * Анонсируется при заходе игроком в мир
		 */
		@Override
		public void onPlayerEnter(final Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.TrickOfTrans.AnnounceEventStarted");
		}
	}

	// Эвент Менеджеры
	private static int EVENT_MANAGER_ID = 32132; // Alchemist\'s Servitor
	private static int CHESTS_ID = 13036; // Alchemist\'s Chest

	// Рецепты
	private static int RED_PSTC = 9162; // Red Philosopher''s Stone Transmutation Circle
	private static int BLUE_PSTC = 9163; // Blue Philosopher''s Stone Transmutation Circle
	private static int ORANGE_PSTC = 9164; // Orange Philosopher''s Stone Transmutation Circle
	private static int BLACK_PSTC = 9165; // Black Philosopher''s Stone Transmutation Circle
	private static int WHITE_PSTC = 9166; // White Philosopher''s Stone Transmutation Circle
	private static int GREEN_PSTC = 9167; // Green Philosopher''s Stone Transmutation Circle

	// Награды
	private static int RED_PSTC_R = 9171; // Red Philosopher''s Stone
	private static int BLUE_PSTC_R = 9172; // Blue Philosopher''s Stone
	private static int ORANGE_PSTC_R = 9173; // Orange Philosopher''s Stone
	private static int BLACK_PSTC_R = 9174; // Black Philosopher''s Stone
	private static int WHITE_PSTC_R = 9175; // White Philosopher''s Stone
	private static int GREEN_PSTC_R = 9176; // Green Philosopher''s Stone

	// Ключ
	private static int A_CHEST_KEY = 9205; // Alchemist''s Chest Key

	private static boolean _active = false;

	private static final ArrayList<NpcInstance> _em_spawns = new ArrayList<NpcInstance>();
	private static final ArrayList<NpcInstance> _ch_spawns = new ArrayList<NpcInstance>();

	// Ингридиенты
	private static int PhilosophersStoneOre = 9168; // Philosopher''s Stone Ore
	private static int PhilosophersStoneOreMax = 17; // Максимальное Кол-во
	private static int PhilosophersStoneConversionFormula = 9169; // Philosopher''s Stone Conversion Formula
	private static int MagicReagents = 9170; // Magic Reagents
	private static int MagicReagentsMax = 30; // Максимальное Кол-во

	private static final EventListeners EVENT_LISTENERS = new EventListeners();

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: Trick of Trnasmutation [state: activated]");
		}
		else
			_log.info("Loaded Event: Trick of Trnasmutation [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 */
	private static boolean isActive()
	{
		return Functions.IsActive("trickoftrans");
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.TrickOfTrans.TrickOfTrans:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("trickoftrans", true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Trick of Transmutation' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.TrickOfTrans.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'Trick of Transmutation' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.TrickOfTrans.TrickOfTrans:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("trickoftrans", false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Trick of Transmutation' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.TrickOfTrans.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'Trick of Transmutation' not started.");

		_active = false;
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		// Эвент Менеджер
		_em_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147992, 28616, -2295, 0)); // Aden
		_em_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81919, 148290, -3472, 51432)); // Giran
		_em_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 18293, 145208, -3081, 6470)); // Dion
		_em_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -14694, 122699, -3122, 0)); // Gludio
		_em_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -81634, 150275, -3155, 15863)); // Gludin

		// Сундуки
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 148081, 28614, -2274, 2059)); // Aden
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 147918, 28615, -2295, 31471)); // Aden
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 147998, 28534, -2274, 49152)); // Aden
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 148053, 28550, -2274, 55621)); // Aden
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 147945, 28563, -2274, 40159)); // Aden
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 82012, 148286, -3472, 61567)); // Giran
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 81822, 148287, -3493, 29413)); // Giran
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 81917, 148207, -3493, 49152)); // Giran
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 81978, 148228, -3472, 53988)); // Giran
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 81851, 148238, -3472, 40960)); // Giran
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 18343, 145253, -3096, 7449)); // Dion
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 18284, 145274, -3090, 19740)); // Dion
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 18351, 145186, -3089, 61312)); // Dion
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 18228, 145265, -3079, 21674)); // Dion
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, 18317, 145140, -3078, 55285)); // Dion
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -14584, 122694, -3122, 65082)); // Gludio
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -14610, 122756, -3143, 13029)); // Gludio
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -14628, 122627, -3122, 50632)); // Gludio
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -14697, 122607, -3143, 48408)); // Gludio
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -14686, 122787, -3122, 12416)); // Gludio
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -81745, 150275, -3134, 32768)); // Gludin
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -81520, 150275, -3134, 0)); // Gludin
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -81628, 150379, -3134, 16025)); // Gludin
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -81696, 150347, -3155, 22854)); // Gludin
		_ch_spawns.add(NpcUtils.spawnSingle(CHESTS_ID, -81559, 150332, -3134, 3356)); // Gludin
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _em_spawns)
			npc.deleteMe();
		for(NpcInstance npc : _ch_spawns)
			npc.deleteMe();
	}

	@Bypass("events.TrickOfTrans.TrickOfTrans:accept")
	public void accept(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(!player.findRecipe(RED_PSTC_R))
			ItemFunctions.addItem(player, RED_PSTC, 1);
		if(!player.findRecipe(BLACK_PSTC_R))
			ItemFunctions.addItem(player, BLACK_PSTC, 1);
		if(!player.findRecipe(BLUE_PSTC_R))
			ItemFunctions.addItem(player, BLUE_PSTC, 1);
		if(!player.findRecipe(GREEN_PSTC_R))
			ItemFunctions.addItem(player, GREEN_PSTC, 1);
		if(!player.findRecipe(ORANGE_PSTC_R))
			ItemFunctions.addItem(player, ORANGE_PSTC, 1);
		if(!player.findRecipe(WHITE_PSTC_R))
			ItemFunctions.addItem(player, WHITE_PSTC, 1);

		Functions.show("scripts/events/TrickOfTrans/TrickOfTrans_01.htm", player);
	}

	@Bypass("events.TrickOfTrans.TrickOfTrans:open")
	public void open(Player player, NpcInstance npc, String[] param)
	{
		if(ItemFunctions.getItemCount(player, A_CHEST_KEY) > 0)
		{
			ItemFunctions.deleteItem(player, A_CHEST_KEY, 1);
			ItemFunctions.addItem(player, PhilosophersStoneOre, Rnd.get(1, PhilosophersStoneOreMax));
			ItemFunctions.addItem(player, MagicReagents, Rnd.get(1, MagicReagentsMax));
			if(Rnd.chance(80))
				ItemFunctions.addItem(player, PhilosophersStoneConversionFormula, 1);

			Functions.show("scripts/events/TrickOfTrans/TrickOfTrans_02.htm", player);
		}
		else
			Functions.show("scripts/events/TrickOfTrans/TrickOfTrans_03.htm", player);
	}
}