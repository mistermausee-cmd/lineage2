package events.PcCafePointsExchange;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PcCafePointsExchange implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(PcCafePointsExchange.class);
	private static final String EVENT_NAME = "PcCafePointsExchange";
	private static final int EVENT_MANAGER_ID = 32130; // npc id
	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 15880, 143704, -2888, 0)); //Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 83656, 148440, -3430, 32768)); //Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147272, 27416, -2228, 16384)); //Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 42808, -47896, -822, 49152)); //Rune
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
	@Bypass("events.PcCafePointsExchange.PcCafePointsExchange:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event: 'PcCafePointsExchange' started.");
		}
		else
			player.sendMessage("Event 'PcCafePointsExchange' already started.");
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.PcCafePointsExchange.PcCafePointsExchange:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			System.out.println("Event: 'PcCafePointsExchange' stopped.");
		}
		else
			player.sendMessage("Event: 'PcCafePointsExchange' not started.");
	}

	@Override
	public void onInit()
	{
		if(isActive())
		{
			spawnEventManagers();
			_log.info("Loaded Event: PcCafePointsExchange [state: activated]");
		}
		else
			_log.info("Loaded Event: PcCafePointsExchange [state: deactivated]");
	}
}