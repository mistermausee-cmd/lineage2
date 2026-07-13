package events.MasterOfEnchanting;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.Announcements;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Autor: Bonux
 * Date: 30.08.09
 * Time: 17:49
 * http://www.lineage2.com/archive/2009/06/master_of_encha.html
 **/
public class MasterOfEnchanting implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(MasterOfEnchanting.class);

	private static class EventListeners implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.MasOfEnch.AnnounceEventStarted");
		}
	}

	private static final EventListeners EVENT_LISTENERS = new EventListeners();

	private static final String EVENT_NAME = "MasterOfEnchanting";
	private static int EVENT_MANAGER_ID = 32599;
	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
	private static boolean _active = false;
	private static final int[][] _herbdrop = { { 20000, 100 }, //Spicy Kimchee
			{ 20001, 100 }, //Spicy Kimchee
			{ 20002, 100 }, //Spicy Kimchee
			{ 20003, 100 } }; //Sweet-and-Sour White Kimchee
	private static final int[][] _energydrop = { { 20004, 30 }, //Energy Ginseng
			{ 20005, 100 } }; //Energy Red Ginseng

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -119494, 44882, 360, 24576)); //Kamael Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -117239, 46842, 360, 49151));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -84023, 243051, -3728, 4096)); //Talking Island Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -84411, 244813, -3728, 57343));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 45538, 48357, -3056, 18000)); //Elven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 46908, 50856, -2992, 8192));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 9929, 16324, -4568, 62999)); //Dark Elven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 11546, 17599, -4584, 46900));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 115096, -178370, -880, 0)); //Dwarven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 116199, -182694, -1488, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -45372, -114104, -240, 16384)); //Orc Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -45278, -112766, -240, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -83156, 150994, -3120, 0)); //Gludin
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -81031, 150038, -3040, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -13727, 122117, -2984, 16384)); //Gludio
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -14129, 123869, -3112, 40959));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 16111, 142850, -2696, 16000)); //Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 17275, 145000, -3032, 25000));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 111004, 218928, -3536, 16384)); //Heine
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 108426, 221876, -3592, 49151));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81755, 146487, -3528, 32768)); //Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82145, 148609, -3464, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 83037, 149324, -3464, 44000));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81083, 56118, -1552, 32768)); //Oren
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81987, 53723, -1488, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 117356, 76708, -2688, 49151)); //Hunters Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 115887, 76382, -2712, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147200, 25614, -2008, 16384)); //Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 148557, 26806, -2200, 32768));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 43966, -47709, -792, 49999)); //Rune
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 43165, -48461, -792, 17000));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147421, -55435, -2728, 49151)); //Goddart
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 148206, -55786, -2776, 904));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 85584, -142490, -1336, 0)); //Schutgard
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 86865, -142915, -1336, 26000));
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return Functions.IsActive(EVENT_NAME);
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.MasterOfEnchanting.MasterOfEnchanting:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event: Master of Enchanting started.");
			Announcements.announceToAllFromStringHolder("scripts.events.MasOfEnch.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'Master of Enchanting' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.MasterOfEnchanting.MasterOfEnchanting:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event: Master of Enchanting stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.MasOfEnch.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'Master of Enchanting' not started.");

		_active = false;
	}

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: Master of Enchanting [state: activated]");
		}
		else
			_log.info("Loaded Event: Master of Enchanting [state: deactivated]");
	}

	//TODO: Надо реализовать ивентовые хербы и их дроп
	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	/** public static void onDeath(L2Character cha, L2Character killer)
	{
		if(_active && cha.isMonster && !cha.isRaid && killer != null && killer.getPlayer() != null && Math.abs(cha.getLevel() - killer.getLevel()) < 10)
		{
			for(int[] drop : _herbdrop)
				if(Rnd.get(1000) <= drop[1])
				{
					L2ItemInstance item = ItemTable.getInstance().createItem(drop[0], killer.getPlayer().getObjectId(), 0, "Master of Enchanting");
					((L2NpcInstance) cha).dropItem(killer.getPlayer(), item);

					break;
				}
			for(int[] drop : _energydrop)
				if(Rnd.get(1000) <= drop[1])
				{
					L2ItemInstance item = ItemTable.getInstance().createItem(drop[0], killer.getPlayer().getObjectId(), 0, "Master of Enchanting");
					((L2NpcInstance) cha).dropItem(killer.getPlayer(), item);

					break;
				}
		}
	}**/
}