package events.heart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.text.PrintfFormat;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event Change of Heart
 * @author Drin
 * http://www.lineage2.com/archive/2007/02/change_of_heart.html
 */

public class heart implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(heart.class);

	private static class EventListeners implements OnDeathListener, OnPlayerEnterListener
	{
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if(_active && Functions.SimpleCheckDrop(cha, killer))
				((NpcInstance) cha).dropItem(killer.getPlayer(), hearts[Rnd.get(hearts.length)], Util.rollDrop(1, 1, Config.EVENT_CHANGE_OF_HEART_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, killer.getPlayer().getRateItems()));
		}

		@Override
		public void onPlayerEnter(Player player)
		{
			if(_active)
				Announcements.announceToPlayerFromStringHolder(player, "scripts.events.ChangeofHeart.AnnounceEventStarted");
		}
	}

	private static boolean _active = false;
	private static final List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
	private static final Map<Integer, Integer> Guesses = new HashMap<Integer, Integer>();
	private static String links_en = "", links_ru = "";
	private static final String[][] variants = { { "Rock", "Камень" }, { "Scissors", "Ножницы" }, { "Paper", "Бумага" } };
	static
	{
		PrintfFormat fmt = new PrintfFormat("<br><Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h htmbypass_events.heart.heart:play %d\">\"%s!\"</button>");
		for(int i = 0; i < variants.length; i++)
		{
			links_en += fmt.sprintf(new Object[] { i, variants[i][0] });
			links_ru += fmt.sprintf(new Object[] { i, variants[i][1] });
		}
	}

	private static final EventListeners EVENT_LISTENERS = new EventListeners();
	private static final int EVENT_MANAGER_ID = 31227; //Buzz the Cat
	private static final int[] hearts = { 4209, 4210, 4211, 4212, 4213, 4214, 4215, 4216, 4217 };
	private static final int[] potions = { 1374, // Greater Haste Potion
			1375, // Greater Swift Attack Potion
			6036, // Greater Magic Haste Potion
			1539 // Greater Healing Potion
	};
	private static final int[] scrolls = { 3926, //	L2Day - Scroll of Guidance
			3927, //	L2Day - Scroll of Death Whisper
			3928, //	L2Day - Scroll of Focus
			3929, //	L2Day - Scroll of Greater Acumen
			3930, //	L2Day - Scroll of Haste
			3931, //	L2Day - Scroll of Agility
			3932, //	L2Day - Scroll of Mystic Empower
			3933, //	L2Day - Scroll of Might
			3934, //	L2Day - Scroll of Windwalk
			3935 //	L2Day - Scroll of Shield
	};

	@Bypass("events.heart.heart:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("heart", true))
		{
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Change of Heart' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.ChangeofHeart.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'Change of Heart' already started.");

		_active = true;
	}

	@Bypass("events.heart.heart:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive("heart", false))
		{
			unSpawnEventManagers();
			CharListenerList.removeGlobal(EVENT_LISTENERS);
			System.out.println("Event 'Change of Heart' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.ChangeofHeart.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event 'Change of Heart' not started.");

		_active = false;
	}

	@Bypass("events.heart.heart:letsplay")
	public void letsplay(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		zeroGuesses(player);
		if(haveAllHearts(player))
			Functions.show(link(HtmCache.getInstance().getHtml("scripts/events/heart/hearts_01.htm", player), isRus(player)), player);
		else
			Functions.show("scripts/events/heart/hearts_00.htm", player);
	}

	@Bypass("events.heart.heart:play")
	public void play(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isQuestContinuationPossible(true) || param.length == 0)
			return;

		if(!haveAllHearts(player))
		{
			if(param[0].equalsIgnoreCase("Quit"))
				Functions.show("scripts/events/heart/hearts_00b.htm", player);
			else
				Functions.show("scripts/events/heart/hearts_00a.htm", player);
			return;
		}

		if(param[0].equalsIgnoreCase("Quit"))
		{
			int curr_guesses = getGuesses(player);
			takeHeartsSet(player);
			reward(player, curr_guesses);
			Functions.show("scripts/events/heart/hearts_reward_" + curr_guesses + ".htm", player);
			zeroGuesses(player);
			return;
		}

		int var_cat = Rnd.get(variants.length);
		int var_player = 0;
		try
		{
			var_player = Integer.parseInt(param[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		if(var_player == var_cat)
		{
			Functions.show(fillvars(HtmCache.getInstance().getHtml("scripts/events/heart/hearts_same.htm", player), var_player, var_cat, player), player);
			return;
		}

		if(playerWins(var_player, var_cat))
		{
			incGuesses(player);
			int curr_guesses = getGuesses(player);
			if(curr_guesses == 10)
			{
				takeHeartsSet(player);
				reward(player, curr_guesses);
				zeroGuesses(player);
			}
			Functions.show(fillvars(HtmCache.getInstance().getHtml("scripts/events/heart/hearts_level_" + curr_guesses + ".htm", player), var_player, var_cat, player), player);
			return;
		}

		takeHeartsSet(player);
		reward(player, getGuesses(player) - 1);
		Functions.show(fillvars(HtmCache.getInstance().getHtml("scripts/events/heart/hearts_loose.htm", player), var_player, var_cat, player), player);
		zeroGuesses(player);
	}

	private void reward(Player player, int guesses)
	{
		switch(guesses)
		{
			case -1:
			case 0:
				ItemFunctions.addItem(player, scrolls[Rnd.get(scrolls.length)], 1);
				break;
			case 1:
				ItemFunctions.addItem(player, potions[Rnd.get(potions.length)], 10);
				break;
			case 2:
				ItemFunctions.addItem(player, 1538, 1); // 1  Blessed Scroll of Escape
				break;
			case 3:
				ItemFunctions.addItem(player, 3936, 1); // 1  Blessed Scroll of Resurrection
				break;
			case 4:
				ItemFunctions.addItem(player, 951, 2); // 2  Scroll: Enchant Weapon (C)
				break;
			case 5:
				ItemFunctions.addItem(player, 948, 4); // 4  Scroll: Enchant Armor (B)
				break;
			case 6:
				ItemFunctions.addItem(player, 947, 1); // 1  Scroll: Enchant Weapon (B)
				break;
			case 7:
				ItemFunctions.addItem(player, 730, 3); // 3  Scroll: Enchant Armor (A)
				break;
			case 8:
				ItemFunctions.addItem(player, 729, 1); // 1  Scroll: Enchant Weapon (A)
				break;
			case 9:
				ItemFunctions.addItem(player, 960, 2); // 2  Scroll: Enchant Armor (S)
				break;
			case 10:
				ItemFunctions.addItem(player, 959, 1); // 1  Scroll: Enchant Weapon (S)
				break;
		}
	}

	private String fillvars(String s, int var_player, int var_cat, Player player)
	{
		boolean rus = isRus(player);
		return link(s.replaceFirst("Player", player.getName()).replaceFirst("%var_payer%", variants[var_player][rus ? 1 : 0]).replaceFirst("%var_cat%", variants[var_cat][rus ? 1 : 0]), rus);
	}

	private boolean isRus(Player player)
	{
		return player.isLangRus();
	}

	private String link(String s, boolean rus)
	{
		return s.replaceFirst("%links%", rus ? links_ru : links_en);
	}

	private boolean playerWins(int var_player, int var_cat)
	{
		if(var_player == 0) // Rock vs Scissors
			return var_cat == 1;
		if(var_player == 1) // Scissors vs Paper
			return var_cat == 2;
		if(var_player == 2) // Paper vs Rock
			return var_cat == 0;
		return false;
	}

	private int getGuesses(Player player)
	{
		return Guesses.containsKey(player.getObjectId()) ? Guesses.get(player.getObjectId()) : 0;
	}

	private void incGuesses(Player player)
	{
		int val = 1;
		if(Guesses.containsKey(player.getObjectId()))
			val = Guesses.remove(player.getObjectId()) + 1;
		Guesses.put(player.getObjectId(), val);
	}

	private void zeroGuesses(Player player)
	{
		if(Guesses.containsKey(player.getObjectId()))
			Guesses.remove(player.getObjectId());
	}

	private void takeHeartsSet(Player player)
	{
		for(int heart_id : hearts)
			ItemFunctions.deleteItem(player, heart_id, 1);
	}

	private boolean haveAllHearts(Player player)
	{
		for(int heart_id : hearts)
			if(player.getInventory().getCountOf(heart_id) < 1)
				return false;
		return true;
	}

	private static boolean isActive()
	{
		return Functions.IsActive("heart");
	}

	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 146936, 26654, -2208, 16384)); // Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82168, 148842, -3464, 7806)); // Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82204, 53259, -1488, 16384)); // Oren
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 18924, 145782, -3088, 44034)); // Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 111794, 218967, -3536, 20780)); // Heine
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -14539, 124066, -3112, 50874)); // Gludio
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147271, -55573, -2736, 60304)); // Goddard
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 87801, -143150, -1296, 28800)); // Shuttgard
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -80684, 149458, -3040, 16384)); // Gludin
	}

	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	@Override
	public void onInit()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			CharListenerList.addGlobal(EVENT_LISTENERS);
			_log.info("Loaded Event: Change of Heart [state: activated]");
		}
		else
			_log.info("Loaded Event: Change of Heart[state: deactivated]");
	}
}