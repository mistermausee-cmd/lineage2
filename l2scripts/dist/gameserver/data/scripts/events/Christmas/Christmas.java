package events.Christmas;

import java.util.ArrayList;
import java.util.List;

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
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Christmas implements OnInitScriptListener
{
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
				int dropCounter = 0;
				for(int[] drop : _dropdata)
					if(Rnd.chance(drop[1] * killer.getPlayer().getRateItems() * 0.1))
					{
						dropCounter++;
						((NpcInstance) cha).dropItem(killer.getPlayer(), drop[0], 1);

						// Из одного моба выпадет не более 3-х эвентовых предметов
						if(dropCounter > 2)
							break;
					}
			}
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.Christmas.AnnounceEventStarted");
		}
	}

	private static int EVENT_MANAGER_ID = 31863;
	private static int CTREE_ID = 13006;
	private static final Logger _log = LoggerFactory.getLogger(Christmas.class);

	private static final EventListeners EVENT_LISTENERS = new EventListeners();

	private static int[][] _dropdata = {
			// Item, chance
			{ 5556, 20 }, //Star Ornament 2%
			{ 5557, 20 }, //Bead Ornament 2%
			{ 5558, 50 }, //Fir Tree Branch 5%
			{ 5559, 5 }, //Flower Pot 0.5%
	/*
	// Музыкальные кристаллы 0.2%
	{ 5562, 2 },
	{ 5563, 2 },
	{ 5564, 2 },
	{ 5565, 2 },
	{ 5566, 2 },
	{ 5583, 2 },
	{ 5584, 2 },
	{ 5585, 2 },
	{ 5586, 2 },
	{ 5587, 2 },
	{ 4411, 2 },
	{ 4412, 2 },
	{ 4413, 2 },
	{ 4414, 2 },
	{ 4415, 2 },
	{ 4416, 2 },
	{ 4417, 2 },
	{ 5010, 2 },
	{ 7061, 2 },
	{ 7062, 2 },
	{ 6903, 2 },
	{ 8555, 2 }
	 */
	};

	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	private static boolean _active = false;

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: Christmas [state: activated]");
		}
		else
			_log.info("Loaded Event: Christmas [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return Functions.IsActive("Christmas");
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.Christmas.Christmas:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("Christmas", true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Christmas' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.Christmas.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'Christmas' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.Christmas.Christmas:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("Christmas", false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Christmas' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.Christmas.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'Christmas' not started.");

		_active = false;
	}

	/**
	 * Спавнит эвент менеджеров и рядом ёлки
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -79256, 248056, -3321, 57343)); // Faeron
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -114760, 256120, -1537, 49151)); // Talking Island
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 208392, 87560, -1052, 24575)); // Arcan
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 80968, 148168, -3472, 0)); // Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 146920, 26680, -2230, 16383)); // Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147464, -56984, -2784, 3742)); // Goddard
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 44584, -48408, -822, 16383)); // Rune
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82904, 54232, -1521, 49151)); // Oren
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 87592, -141720, -1344, 35323)); // Schuttgart
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 111816, 218968, -3568, 16383)); // Heine
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 16376, 142792, -2731, 16383)); // Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -14824, 123144, -3143, 0)); // Gludio
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -80728, 150632, -3069, 40959)); // Gludin

		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -79240, 248104, -3321, 0)); // Faeron
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -114712, 256120, -1537, 0)); // Talking Island
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 208344, 87512, -1052, 0)); // Arcan
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 80968, 148216, -3494, 0)); // Giran
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 146856, 26680, -2230, 0)); // Aden
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 147448, -56936, -2803, 0)); // Goddard
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 44536, -48424, -822, 0)); // Rune
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 82968, 54232, -1521, 0)); // Oren
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 87624, -141784, -1368, 0)); // Schuttgart
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 111768, 218952, -3568, 0)); // Heine
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 16328, 142792, -2731, 0)); // Dion
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -14840, 123192, -3141, 0)); // Gludio
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -80696, 150584, -3069, 0)); // Gludin
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	@Bypass("events.Christmas.Christmas:exchange")
	public void exchange(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(param[0].equalsIgnoreCase("0"))
		{
			if(ItemFunctions.getItemCount(player, 5556) >= 4 && ItemFunctions.getItemCount(player, 5557) >= 4 && ItemFunctions.getItemCount(player, 5558) >= 10 && ItemFunctions.getItemCount(player, 5559) >= 1)
			{
				ItemFunctions.deleteItem(player, 5556, 4);
				ItemFunctions.deleteItem(player, 5557, 4);
				ItemFunctions.deleteItem(player, 5558, 10);
				ItemFunctions.deleteItem(player, 5559, 1);
				ItemFunctions.addItem(player, 5560, 1); // Christmas Tree
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(param[0].equalsIgnoreCase("1"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 10)
			{
				ItemFunctions.deleteItem(player, 5560, 10);
				ItemFunctions.addItem(player, 5561, 1); // Special Christmas Tree
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(param[0].equalsIgnoreCase("2"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 10)
			{
				ItemFunctions.deleteItem(player, 5560, 10);
				ItemFunctions.addItem(player, 7836, 1); // Santa's Hat
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(param[0].equalsIgnoreCase("3"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 10)
			{
				ItemFunctions.deleteItem(player, 5560, 10);
				ItemFunctions.addItem(player, 8936, 1); // Santa's Antlers
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
		if(param[0].equalsIgnoreCase("4"))
		{
			if(ItemFunctions.getItemCount(player, 5560) >= 20)
			{
				ItemFunctions.deleteItem(player, 5560, 20);
				ItemFunctions.addItem(player, 10606, 1); // Agathion Seal Bracelet - Rudolph
				return;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
		}
	}
}