package events.glitmedal;

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
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: darkevil
 * Date: 26.02.2008
 * Time: 1:17:42
 */
public class glitmedal implements OnInitScriptListener
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
				long count = Util.rollDrop(1, 1, Config.EVENT_GLITTMEDAL_NORMAL_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, killer.getPlayer().getRateItems());
				if(count > 0)
					ItemFunctions.addItem(killer.getPlayer(), EVENT_MEDAL, count);
				if(killer.getPlayer().getInventory().getCountOf(Badge_of_Wolf) == 0 && Rnd.chance(Config.EVENT_GLITTMEDAL_GLIT_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp))
					ItemFunctions.addItem(killer.getPlayer(), EVENT_GLITTMEDAL, 1);
			}
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.glitmedal.AnnounceEventStarted");
		}
	}

	private static int EVENT_MANAGER_ID1 = 31228; // Roy
	private static int EVENT_MANAGER_ID2 = 31229; // Winnie

	private static final Logger _log = LoggerFactory.getLogger(glitmedal.class);

	// Для временного статуса который выдается в игре рандомно либо 0 либо 1
	private int isTalker;

	// Медали
	private static int EVENT_MEDAL = 6392;
	private static int EVENT_GLITTMEDAL = 6393;

	private static int Badge_of_Rabbit = 6399;
	private static int Badge_of_Hyena = 6400;
	private static int Badge_of_Fox = 6401;
	private static int Badge_of_Wolf = 6402;

	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	private static final EventListeners EVENT_LISTENERS = new EventListeners();

	private static boolean _active = false;

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: L2 Medal Collection Event [state: activated]");
		}
		else
			_log.info("Loaded Event: L2 Medal Collection Event [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 *
	 * @return
	 */
	private static boolean isActive()
	{
		return Functions.IsActive("glitter");
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.glitmedal.glitmedal:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("glitter", true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event 'L2 Medal Collection Event' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.glitmedal.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'L2 Medal Collection Event' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.glitmedal.glitmedal:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("glitter", false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event 'L2 Medal Collection Event' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.glitmedal.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'L2 Medal Collection Event' not started.");

		_active = false;
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		// 1й эвент кот
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 147893, -56622, -2776, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, -81070, 149960, -3040, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 82882, 149332, -3464, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 44176, -48732, -800, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 147920, 25664, -2000, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 117498, 76630, -2695, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 111776, 221104, -3543, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, -84516, 242971, -3730, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, -13073, 122801, -3117, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, -44337, -113669, -224, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 11281, 15652, -4584, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 44122, 50784, -3059, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 80986, 54504, -1525, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 114733, -178691, -821, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID1, 18178, 145149, -3054, 0));

		// 2й эвент кот
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 147960, -56584, -2776, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, -81070, 149860, -3040, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 82798, 149332, -3464, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 44176, -48688, -800, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 147985, 25664, -2000, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 117459, 76664, -2695, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 111724, 221111, -3543, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, -84516, 243015, -3730, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, -13073, 122841, -3117, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, -44342, -113726, -240, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 11327, 15682, -4584, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 44157, 50827, -3059, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 80986, 54452, -1525, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 114719, -178742, -821, 0));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID2, 18154, 145192, -3054, 0));
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	@Bypass("events.glitmedal.glitmedal:glitchang")
	public void glitchang(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, EVENT_MEDAL) >= 1000)
		{
			ItemFunctions.deleteItem(player, EVENT_MEDAL, 1000);
			ItemFunctions.addItem(player, EVENT_GLITTMEDAL, 10);
			return;
		}
		player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
	}

	@Bypass("events.glitmedal.glitmedal:medal")
	public void medal(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, Badge_of_Wolf) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent1_q0996_05.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Fox) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent1_q0996_04.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Hyena) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent1_q0996_03.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Rabbit) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent1_q0996_02.htm", player);
			return;
		}

		Functions.show("scripts/events/glitmedal/event_col_agent1_q0996_01.htm", player);
	}

	@Bypass("events.glitmedal.glitmedal:medalb")
	public void medalb(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, Badge_of_Wolf) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_05.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Fox) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_04.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Hyena) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_03.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Rabbit) >= 1)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_02.htm", player);
			return;
		}

		Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_01.htm", player);
		return;
	}

	@Bypass("events.glitmedal.glitmedal:game")
	public void game(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, Badge_of_Fox) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 40)
			{
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
				return;
			}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm",  player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Hyena) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 20)
			{
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
				return;
			}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
			return;
		}
		else if(ItemFunctions.getItemCount(player, Badge_of_Rabbit) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 10)
			{
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
				return;
			}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm",  player);
			return;
		}

		else if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 5)
		{
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm",  player);
			return;
		}

		Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm",  player);
	}

	@Bypass("events.glitmedal.glitmedal:gamea")
	public void gamea(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		isTalker = Rnd.get(2);

		if(ItemFunctions.getItemCount(player, Badge_of_Fox) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 40)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Fox, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL));
					ItemFunctions.addItem(player, Badge_of_Wolf, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_24.htm", player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 40);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		else if(ItemFunctions.getItemCount(player, Badge_of_Hyena) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 20)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Hyena, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 20);
					ItemFunctions.addItem(player, Badge_of_Fox, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_23.htm", player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 20);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm",player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		else if(ItemFunctions.getItemCount(player, Badge_of_Rabbit) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 10)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Rabbit, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 10);
					ItemFunctions.addItem(player, Badge_of_Hyena, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_22.htm", player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 10);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm",  player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 5)
			if(isTalker == 1)
			{
				ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 5);
				ItemFunctions.addItem(player, Badge_of_Rabbit, 1);
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_21.htm", player);
				return;
			}
			else if(isTalker == 0)
			{
				ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 5);
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
				return;
			}
		Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm",  player);
	}

	// FIXME: нафига две идентичные функции?
	@Bypass("events.glitmedal.glitmedal:gameb")
	public void gameb(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		isTalker = Rnd.get(2);

		if(ItemFunctions.getItemCount(player, Badge_of_Fox) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 40)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Fox, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 40);
					ItemFunctions.addItem(player, Badge_of_Wolf, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_34.htm", player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 40);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		else if(ItemFunctions.getItemCount(player, Badge_of_Hyena) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 20)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Hyena, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 20);
					ItemFunctions.addItem(player, Badge_of_Fox, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_33.htm",  player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 20);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		else if(ItemFunctions.getItemCount(player, Badge_of_Rabbit) >= 1)
		{
			if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 10)
				if(isTalker == 1)
				{
					ItemFunctions.deleteItem(player, Badge_of_Rabbit, 1);
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 10);
					ItemFunctions.addItem(player, Badge_of_Hyena, 1);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_32.htm",player);
					return;
				}
				else if(isTalker == 0)
				{
					ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 10);
					Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
					return;
				}
			Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
			return;
		}

		if(ItemFunctions.getItemCount(player, EVENT_GLITTMEDAL) >= 5)
			if(isTalker == 1)
			{
				ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 5);
				ItemFunctions.addItem(player, Badge_of_Rabbit, 1);
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_31.htm", player);
				return;
			}
			else if(isTalker == 0)
			{
				ItemFunctions.deleteItem(player, EVENT_GLITTMEDAL, 5);
				Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
				return;
			}
		Functions.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
		return;
	}
}