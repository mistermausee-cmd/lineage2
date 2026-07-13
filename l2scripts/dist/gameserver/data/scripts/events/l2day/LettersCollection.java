package events.l2day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LettersCollection implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(LettersCollection.class);

	private static class EventListeners implements OnDeathListener, OnPlayerEnterListener
	{
		/**
		 * Обработчик смерти мобов, управляющий эвентовым дропом
		 */
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if(_active && Functions.SimpleCheckDrop(cha, killer))
			{
				int[] letter = letters[Rnd.get(letters.length)];
				if(Rnd.chance(letter[1] * Config.EVENT_L2DAY_LETTER_CHANCE * ((NpcTemplate) cha.getTemplate()).rateHp))
					((NpcInstance) cha).dropItem(killer.getPlayer(), letter[0], 1);
			}
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, _msgStarted);
		}
	}

	private static final EventListeners EVENT_LISTENERS = new EventListeners();

	// Переменные, определять
	protected static boolean _active;
	protected static String _name;
	protected static int[][] letters;
	protected static int EVENT_MANAGERS[][] = null;
	protected static String _msgStarted;
	protected static String _msgEnded;

	// Буквы, статика
	protected static int A = 3875;
	protected static int C = 3876;
	protected static int E = 3877;
	protected static int F = 3878;
	protected static int G = 3879;
	protected static int H = 3880;
	protected static int I = 3881;
	protected static int L = 3882;
	protected static int N = 3883;
	protected static int O = 3884;
	protected static int R = 3885;
	protected static int S = 3886;
	protected static int T = 3887;
	protected static int II = 3888;
	protected static int Y = 13417;
	protected static int _5 = 13418;

	protected static int EVENT_MANAGER_ID = 31230;

	// Контейнеры, не трогать
	protected static Map<String, Integer[][]> _words = new HashMap<String, Integer[][]>();
	protected static Map<String, RewardData[]> _rewards = new HashMap<String, RewardData[]>();
	protected static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: " + _name + " [state: activated]");
		}
		else
			_log.info("Loaded Event: " + _name + " [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 */
	protected static boolean isActive()
	{
		return Functions.IsActive(_name);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	protected void spawnEventManagers()
	{
		for(int[] point : EVENT_MANAGERS)
			_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, point[0], point[1], point[2], point.length > 3 ? point[3] : 0)); // Dion
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	protected void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.l2day.l2day:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(_name, true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event '" + _name + "' started.");
			Announcements.announceToAllFromStringHolder(_msgStarted);
		}
		else
			player.sendMessage("Event '" + _name + "' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.l2day.l2day:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(_name, false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event '" + _name + "' stopped.");
			Announcements.announceToAllFromStringHolder(_msgEnded);
		}
		else
			player.sendMessage("Event '" + _name + "' not started.");

		_active = false;
	}

	/**
	 * Обмен эвентовых вещей, где var[0] - слово.
	 */
	@Bypass("events.l2day.l2day:exchange")
	public void exchange(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || npc == null)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(!npc.canBypassCheck(player))
			return;

		Integer[][] mss = _words.get(param[0]);

		for(Integer[] l : mss)
			if(ItemFunctions.getItemCount(player, l[0]) < l[1])
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}

		for(Integer[] l : mss)
			ItemFunctions.deleteItem(player, l[0], l[1]);

		RewardData[] rewards = _rewards.get(param[0]);
		int sum = 0;
		for(RewardData r : rewards)
			sum += r.getChance();
		int random = Rnd.get(sum);
		sum = 0;
		for(RewardData r : rewards)
		{
			sum += r.getChance();
			if(sum > random)
			{
				ItemFunctions.addItem(player, r.getItemId(), Rnd.get(r.getMinDrop(), r.getMaxDrop()));
				return;
			}
		}
	}
}