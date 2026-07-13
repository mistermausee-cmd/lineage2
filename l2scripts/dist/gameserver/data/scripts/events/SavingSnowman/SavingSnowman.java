package events.SavingSnowman;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.geometry.Shape;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MTLPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.PositionUtils;
import l2s.gameserver.utils.Util;
import l2s.gameserver.model.Zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingSnowman implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(SavingSnowman.class);

	private static class EventListeners implements OnDeathListener, OnPlayerEnterListener
	{
		/**
		 * Обработчик смерти мобов
		 */
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if(_active && killer != null)
			{
				Player pKiller = killer.getPlayer();
				if(pKiller != null && Functions.SimpleCheckDrop(cha, killer) && Rnd.get(1000) < Config.EVENT_SAVING_SNOWMAN_REWARDER_CHANCE)
				{
					List<Player> players = new ArrayList<Player>();
					if(pKiller.isInParty())
						players = pKiller.getParty().getPartyMembers();
					else
						players.add(pKiller);

					spawnRewarder(players.get(Rnd.get(players.size())));
				}
			}
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.SavingSnowman.AnnounceEventStarted");
		}
	}

	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();

	private static ScheduledFuture<?> _snowmanShoutTask;
	private static ScheduledFuture<?> _saveTask;
	private static ScheduledFuture<?> _sayTask;
	private static ScheduledFuture<?> _eatTask;

	public static SnowmanState _snowmanState;

	private static NpcInstance _snowman;
	private static Creature _thomas;

	public static enum SnowmanState
	{
		CAPTURED,
		KILLED,
		SAVED;
	}

	private static final int INITIAL_SAVE_DELAY = 10 * 60 * 1000; // 10 мин
	private static final int SAVE_INTERVAL = 60 * 60 * 1000; // 60 мин
	private static final int SNOWMAN_SHOUT_INTERVAL = 1 * 60 * 1000; // 1 мин
	private static final int THOMAS_EAT_DELAY = 10 * 60 * 1000; // 10 мин
	private static final int SATNA_SAY_INTERVAL = 5 * 60 * 1000; // 5 мин
	private static final int EVENT_MANAGER_ID = 13184;
	private static final int CTREE_ID = 13006;
	private static final int EVENT_REWARDER_ID = 13186;
	private static final int SNOWMAN_ID = 13160;
	private static final int THOMAS_ID = 13183;

	private static final int SANTA_BUFF_REUSE = 12 * 3600 * 1000; // 12 hours
	private static final int SANTA_LOTTERY_REUSE = 3 * 3600 * 1000; // 3 hours

	// Оружие для обмена купонов
	private static final int WEAPONS[][] = { { 20109, 20110, 20111, 20112, 20113, 20114, 20115, 20116, 20117, 20118, 20119, 20120, 20121, 20122 }, // D
			{ 20123, 20124, 20125, 20126, 20127, 20128, 20129, 20130, 20131, 20132, 20133, 20134, 20135, 20136 }, // C
			{ 20137, 20138, 20139, 20140, 20141, 20142, 20143, 20144, 20145, 20146, 20147, 20148, 20149, 20150 }, // B
			{ 20151, 20152, 20153, 20154, 20155, 20156, 20157, 20158, 20159, 20160, 20161, 20162, 20163, 20164 }, // A
			{ 20165, 20166, 20167, 20168, 20169, 20170, 20171, 20172, 20173, 20174, 20175, 20176, 20177, 20178 } // S
	};

	private static EventListeners EVENT_LISTENERS = new EventListeners();

	private static boolean _active = false;

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: SavingSnowman [state: activated]");
			_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			_sayTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			_log.info("Loaded Event: SavingSnowman [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return Functions.IsActive("SavingSnowman");
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.SavingSnowman.SavingSnowman:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		/* FIXME */
		if(Boolean.FALSE)
		{
			player.sendMessage("Event is currently disabled");
			return;
		}

		if(Functions.SetActive("SavingSnowman", true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event 'SavingSnowman' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.SavingSnowman.AnnounceEventStarted");
			if(_saveTask == null)
				_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			if(_sayTask == null)
				_sayTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SavingSnowman' already started.");

		_active = true;
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.SavingSnowman.SavingSnowman:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("SavingSnowman", false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			if(_snowman != null)
				_snowman.deleteMe();
			if(_thomas != null)
				_thomas.deleteMe();
			System.out.println("Event 'SavingSnowman' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.SavingSnowman.AnnounceEventStoped");
			if(_saveTask != null)
			{
				_saveTask.cancel(false);
				_saveTask = null;
			}
			if(_sayTask != null)
			{
				_sayTask.cancel(false);
				_sayTask = null;
			}
			if(_eatTask != null)
			{
				_eatTask.cancel(false);
				_eatTask = null;
			}
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SavingSnowman' not started.");

		_active = false;
	}

	/**
	 * Спавнит эвент менеджеров и рядом ёлки
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81921, 148921, -3467, 16384));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 146405, 28360, -2269, 49648));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 19319, 144919, -3103, 31135));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -82805, 149890, -3129, 16384));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -12347, 122549, -3104, 16384));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 110642, 220165, -3655, 61898));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 116619, 75463, -2721, 20881));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 85513, 16014, -3668, 23681));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81999, 53793, -1496, 61621));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 148159, -55484, -2734, 44315));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 44185, -48502, -797, 27479));
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 86899, -143229, -1293, 8192));

		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 81961, 148921, -3467, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 146445, 28360, -2269, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 19319, 144959, -3103, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -82845, 149890, -3129, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, -12387, 122549, -3104, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 110602, 220165, -3655, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 116659, 75463, -2721, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 85553, 16014, -3668, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 81999, 53743, -1496, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 148199, -55484, -2734, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 44185, -48542, -797, 0));
		_spawns.add(NpcUtils.spawnSingle(CTREE_ID, 86859, -143229, -1293, 0));
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	public static void spawnRewarder(final Player rewarded)
	{
		// Два санты рядом не должно быть
		for(NpcInstance npc : rewarded.getAroundNpc(1500, 300))
			if(npc.getNpcId() == EVENT_REWARDER_ID)
				return;

		// Санта появляется в зоне прямой видимости
		Location spawnLoc = Location.findPointToStay(rewarded, 300, 400);
		for(int i = 0; i < 20 && !GeoEngine.canSeeCoord(rewarded, spawnLoc.x, spawnLoc.y, spawnLoc.z, false); i++)
			spawnLoc = Location.findPointToStay(rewarded, 300, 400);

		// Спауним
		final NpcInstance rewarder = NpcUtils.spawnSingle(EVENT_REWARDER_ID, spawnLoc, rewarded.getReflection());
		rewarder.setHeading(PositionUtils.calculateHeadingFrom(rewarder, rewarded), true);

		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase1");

		Location targetLoc = Location.findFrontPosition(rewarded, rewarded, 40, 50);
		rewarder.setSpawnedLoc(targetLoc);
		rewarder.broadcastPacket(new MTLPacket(rewarder.getObjectId(), rewarder.getLoc(), targetLoc));

		ThreadPoolManager.getInstance().schedule(() -> reward(rewarder, rewarded), 5000);
	}

	public static void reward(final NpcInstance rewarder, Player rewarded)
	{
		if(!_active || rewarder == null || rewarded == null)
			return;
		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase2", rewarded.getName());
		ItemFunctions.addItem(rewarded, 14616, 1); // Gift from Santa Claus
		ThreadPoolManager.getInstance().schedule(() -> removeRewarder(rewarder), 5000);
	}

	public static void removeRewarder(final NpcInstance rewarder)
	{
		if(!_active || rewarder == null)
			return;

		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase3");

		Location loc = rewarder.getSpawnedLoc();

		double radian = PositionUtils.convertHeadingToRadian(rewarder.getHeading());
		int x = loc.x - (int) (Math.sin(radian) * 300);
		int y = loc.y + (int) (Math.cos(radian) * 300);
		int z = loc.z;

		rewarder.broadcastPacket(new MTLPacket(rewarder.getObjectId(), loc, new Location(x, y, z)));

		ThreadPoolManager.getInstance().schedule(() -> unspawnRewarder(rewarder), 2000);
	}

	public static void unspawnRewarder(NpcInstance rewarder)
	{
		if(!_active || rewarder == null)
			return;
		rewarder.deleteMe();
	}

	@Bypass("events.SavingSnowman.SavingSnowman:buff")
	public void buff(Player player, NpcInstance npc, String[] param)
	{
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		String var = player.getVar("santaEventTime");
		if(var != null && Long.parseLong(var) > System.currentTimeMillis())
		{
			Functions.show("default/13184-4.htm", player);
			return;
		}

		if(_snowmanState != SnowmanState.SAVED)
		{
			Functions.show("default/13184-3.htm", player);
			return;
		}

		player.broadcastPacket(new MagicSkillUse(player, player, 23017, 1, 0, 0));
		player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(23017, 1));
		player.setVar("santaEventTime", String.valueOf(System.currentTimeMillis() + SANTA_BUFF_REUSE), -1);

		for(Servitor s : player.getServitors())
		{
			s.broadcastPacket(new MagicSkillUse(s, s, 23017, 1, 0, 0));
			s.altOnMagicUse(s, SkillHolder.getInstance().getSkill(23017, 1));
		}
	}

	@Bypass("events.SavingSnowman.SavingSnowman:locateSnowman")
	public void locateSnowman(Player player, NpcInstance npc, String[] param)
	{
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(_snowman != null)
		{
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControlPacket(2, 2, _snowman.getLoc()), new RadarControlPacket(0, 1, _snowman.getLoc()));
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(_snowman.getLoc()).addString("Ищите Снеговика в "));
		}
		else
			player.sendPacket(SystemMsg.YOUR_TARGET_CANNOT_BE_FOUND);
	}

	@Bypass("events.SavingSnowman.SavingSnowman:coupon")
	public void coupon(Player player, NpcInstance npc, String[] var)
	{
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, 20107) < 1)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		int num = Integer.parseInt(var[0]);
		if(num < 0 || num > 13)
			return;

		int expertise = Math.min(player.getExpertiseIndex(), 5);
		expertise = Math.max(expertise, 1);
		expertise--;

		ItemFunctions.deleteItem(player, 20107, 1);

		int item_id = WEAPONS[expertise][num];
		int enchant = Rnd.get(4, 16);
		ItemInstance item = ItemFunctions.createItem(item_id);
		item.setEnchantLevel(enchant);
		player.getInventory().addItem(item);
		player.sendPacket(SystemMessagePacket.obtainItems(item_id, 1, enchant));
	}

	@Bypass("events.SavingSnowman.SavingSnowman:lotery")
	public void lotery(Player player, NpcInstance npc, String[] param)
	{
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(ItemFunctions.getItemCount(player, 57) < Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		String var = player.getVar("santaLotteryTime");
		if(var != null && Long.parseLong(var) > System.currentTimeMillis())
		{
			Functions.show("default/13184-5.htm", player);
			return;
		}

		ItemFunctions.deleteItem(player, 57, Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE);
		player.setVar("santaLotteryTime", String.valueOf(System.currentTimeMillis() + SANTA_LOTTERY_REUSE), -1);

		int chance = Rnd.get(RewardList.MAX_CHANCE);

		// Special Christmas Tree            30%
		if(chance < 300000)
			ItemFunctions.addItem(player, 5561, 1);
		// Christmas Red Sock                18%
		else if(chance < 480000)
			ItemFunctions.addItem(player, 14612, 1);
		// Santa Claus' Weapon Exchange Ticket - 12 Hour Expiration Period      15%
		else if(chance < 630000)
			ItemFunctions.addItem(player, 20107, 1);
		// Gift from Santa Claus             5%
		else if(chance < 680000)
			ItemFunctions.addItem(player, 14616, 1);
		// Rudolph's Nose                    5%
		else if(chance < 730000 && ItemFunctions.getItemCount(player, 14611) == 0)
			ItemFunctions.addItem(player, 14611, 1);
		// Santa's Hat                       5%
		else if(chance < 780000 && ItemFunctions.getItemCount(player, 7836) == 0)
			ItemFunctions.addItem(player, 7836, 1);
		// Santa's Antlers                   5%
		else if(chance < 830000 && ItemFunctions.getItemCount(player, 8936) == 0)
			ItemFunctions.addItem(player, 8936, 1);
		// Agathion Seal Bracelet - Rudolph (постоянный предмет)                5%
		else if(chance < 880000 && ItemFunctions.getItemCount(player, 10606) == 0)
			ItemFunctions.addItem(player, 10606, 1);
		// Agathion Seal Bracelet: Rudolph - 30 дней со скилом на виталити      5%
		else if(chance < 930000 && ItemFunctions.getItemCount(player, 20094) == 0)
			ItemFunctions.addItem(player, 20094, 1);
		// Chest of Experience (Event)       3%
		else if(chance < 960000)
			ItemFunctions.addItem(player, 20575, 1);
		// Призрачные аксессуары             2.5%
		else if(chance < 985000)
			ItemFunctions.addItem(player, Rnd.get(9177, 9204), 1);
		// BOSE или BRES                     1.2%
		else if(chance < 997000)
			ItemFunctions.addItem(player, Rnd.get(9156, 9157), 1);
	}

	/*private static Location getRandomSpawnPoint()
	{
		return new Location().getRandomLoc(0);
		//return new Location(0, 0, 0);
	}*/

	// Индюк захватывает снеговика
	public void captureSnowman()
	{
		Location spawnPoint = new Location().getRandomLoc(0); //getRandomSpawnPoint();

		for(Player player : GameObjectsStorage.getPlayers())
		{
			Announcements.criticalAnnounceToPlayerFromStringHolder(player, "scripts.events.SavingSnowman.AnnounceSnowmanCaptured");
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(spawnPoint).addString("Ищите Снеговика в "));
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControlPacket(2, 2, spawnPoint), new RadarControlPacket(0, 1, spawnPoint));
		}

		// Спауним снеговика
		_snowman = NpcUtils.spawnSingle(SNOWMAN_ID, spawnPoint);
		_thomas = NpcUtils.spawnSingle(THOMAS_ID, Location.findPointToStay(_snowman, 100, 120));
		_snowmanState = SnowmanState.CAPTURED;

		// Если по каким-то причинам таск существует, останавливаем его
		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}
		_snowmanShoutTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShoutTask(), 1, SNOWMAN_SHOUT_INTERVAL);

		if(_eatTask != null)
		{
			_eatTask.cancel(false);
			_eatTask = null;
		}
		_eatTask = ThreadPoolManager.getInstance().schedule(() -> eatSnowman(), THOMAS_EAT_DELAY);
	}

	// Индюк захавывает снеговика
	public static void eatSnowman()
	{
		if(_snowman == null || _thomas == null)
			return;

		Announcements.criticalAnnounceToAllFromStringHolder("scripts.events.SavingSnowman.AnnounceSnowmanKilled");

		_snowmanState = SnowmanState.KILLED;

		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}

		_snowman.deleteMe();
		_thomas.deleteMe();
	}

	// Индюк умер, освобождаем снеговика
	public static void freeSnowman(Creature topDamager)
	{
		if(_snowman == null || topDamager == null || !topDamager.isPlayable())
			return;

		Announcements.criticalAnnounceToAllFromStringHolder("scripts.events.SavingSnowman.AnnounceSnowmanSaved");

		_snowmanState = SnowmanState.SAVED;

		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}
		if(_eatTask != null)
		{
			_eatTask.cancel(false);
			_eatTask = null;
		}

		Player player = topDamager.getPlayer();
		Functions.npcSayCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanSayTnx", player.getName());
		ItemFunctions.addItem(player, 20034, 3); // Revita-Pop
		ItemFunctions.addItem(player, 20338, 1); // Rune of Experience Points 50%	10 Hour Expiration Period
		ItemFunctions.addItem(player, 20344, 1); // Rune of SP 50% 10 Hour Expiration Period

		ThreadPoolManager.getInstance().execute(() -> _snowman.deleteMe());
	}

	public class SayTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!_active)
				return;

			for(NpcInstance npc : _spawns)
				if(npc.getNpcId() == EVENT_MANAGER_ID)
					Functions.npcSayCustomMessage(npc, "scripts.events.SavingSnowman.SantaSay");
		}
	}

	public class ShoutTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!_active || _snowman == null || _snowmanState != SnowmanState.CAPTURED)
				return;

			Functions.npcShoutCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanShout");
		}
	}

	public class SaveTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!_active || _snowmanState == SnowmanState.CAPTURED)
				return;

			captureSnowman();
		}
	}
}