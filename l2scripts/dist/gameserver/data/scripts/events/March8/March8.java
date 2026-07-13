package events.March8;

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
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Эвент к 8 марта: http://www.lineage2.com/archive/2009/01/the_valentine_e.html
 * 
 * @author SYS
 */
public class March8 implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(March8.class);

	private static class EventListeners implements OnDeathListener, OnPlayerEnterListener
	{
		/**
		 * Обработчик смерти мобов, управляющий эвентовым дропом
		 */
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if(_active && Functions.SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_MARCH8_DROP_CHANCE * killer.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
				((NpcInstance) cha).dropItem(killer.getPlayer(), DROP[Rnd.get(DROP.length)], 1);
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.March8.AnnounceEventStarted");
		}
	}

	private static final String EVENT_NAME = "March8";
	private static final int EVENT_MANAGER_ID = 4301;
	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
	private static final int[] DROP = { 20192, 20193, 20194 };

	private static EventListeners EVENT_LISTENERS = new EventListeners();

	private static boolean _active = false;

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -14823, 123567, -3143, 8192)); // Gludio
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -83159, 150914, -3155, 49152)); // Gludin
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 18600, 145971, -3095, 40960)); // Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82158, 148609, -3493, 60)); // Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 110992, 218753, -3568, 0)); // Hiene
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 116339, 75424, -2738, 0)); // Hunter Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81140, 55218, -1551, 32768)); // Oren
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147148, 27401, -2231, 2300)); // Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 43532, -46807, -823, 31471)); // Rune
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 87765, -141947, -1367, 6500)); // Schuttgart
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147154, -55527, -2807, 61300)); // Goddard
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
	@Bypass("events.March8.March8:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event: March 8 started.");
			Announcements.announceToAllFromStringHolder("scripts.events.March8.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'March 8' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.March8.March8:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event: March 8 stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.March8.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'March 8' not started.");

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
			_log.info("Loaded Event: March 8 [state: activated]");
		}
		else
			_log.info("Loaded Event: March 8 [state: deactivated]");
	}
}