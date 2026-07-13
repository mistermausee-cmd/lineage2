package l2s.gameserver.model.quest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExShowQuestMarkPacket;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.QuestListPacket;
import l2s.gameserver.network.l2.s2c.ShowTutorialMarkPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.TutorialEnableClientEventPacket;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;

public final class QuestState
{
	public class OnDeathListenerImpl implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			Player player = actor.getPlayer();
			if(player == null)
				return;

			player.removeListener(this);

			_quest.notifyDeath(killer, actor, QuestState.this);
		}
	}

	public class PlayerOnKillListenerImpl implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if(!victim.isPlayer())
				return;

			Player actorPlayer = (Player) actor;
			List<Player> players = null;
			switch(_quest.getPartyType())
			{
				case PARTY_NONE:
					players = Collections.singletonList(actorPlayer);
					break;
				case PARTY_ALL:
					if(actorPlayer.getParty() == null)
						players = Collections.singletonList(actorPlayer);
					else
					{
						players = new ArrayList<Player>(actorPlayer.getParty().getMemberCount());
						for(Player $member : actorPlayer.getParty().getPartyMembers())
							if($member.checkInteractionDistance(actorPlayer))
								players.add($member);
					}
					break;
				case COMMAND_CHANNEL:
					if(actorPlayer.getParty() == null || actorPlayer.getParty().getCommandChannel() == null)
						players = Collections.singletonList(actorPlayer);
					else
					{
						players = new ArrayList<Player>(actorPlayer.getParty().getCommandChannel().getMemberCount());
						for(Player p : actorPlayer.getPlayer().getParty().getCommandChannel())
							if(p.checkInteractionDistance(actorPlayer))
								players.add(p);
					}
					break;					
				default:
					players = Collections.emptyList();
					break;
			}

			for(Player player : players)
			{
				QuestState questState = player.getQuestState(_quest);
				if(questState != null && !questState.isCompleted())
					_quest.notifyKill((Player) victim, questState);
			}
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(QuestState.class);

	public static final int RESTART_HOUR = 6;
	public static final int RESTART_MINUTES = 30;
	public static final String VAR_COND = "cond";

	public final static QuestState[] EMPTY_ARRAY = new QuestState[0];

	private final Player _player;
	private Quest _quest;
	private Integer _cond = null;
	private Integer _condsMask = null;
	private long _restartTime = 0L;
	private Map<String, String> _vars = new ConcurrentHashMap<String, String>();
	private Map<String, QuestTimer> _timers = new ConcurrentHashMap<String, QuestTimer>();
	private OnKillListener _onKillListener = null;

	
	public QuestState(Quest quest, Player player)
	{
		_quest = quest;
		_player = player;

		
		player.setQuestState(this);

		quest.onRestore(this);
	}

	
	public void addExpAndSp(long exp, long sp)
	{
		Player player = getPlayer();
		if(player == null)
			return;
		if(exp > 0)
			player.addExpAndSp((long) (exp * getRateQuestsReward()), 0);
		if(sp > 0)
			player.addExpAndSp(0, (long) (sp * getRateQuestsReward()));
	}

	
	public void addNotifyOfDeath(Player player, boolean withPet)
	{
		OnDeathListenerImpl listener = new OnDeathListenerImpl();
		player.addListener(listener);
		if(withPet)
		{
			for(Servitor servitor : player.getServitors())
				servitor.addListener(listener);
		}
	}

	public void addPlayerOnKillListener()
	{
		if(_onKillListener != null)
			throw new IllegalArgumentException("Cant add twice kill listener to player");

		_onKillListener = new PlayerOnKillListenerImpl();
		_player.addListener(_onKillListener);
	}

	public void removePlayerOnKillListener()
	{
		if(_onKillListener != null)
			_player.removeListener(_onKillListener);
	}

	public void addRadar(int x, int y, int z)
	{
		Player player = getPlayer();
		if(player != null)
			player.addRadar(x, y, z);
	}

	public void addRadarWithMap(int x, int y, int z)
	{
		Player player = getPlayer();
		if(player != null)
			player.addRadarWithMap(x, y, z);
	}

	
	private boolean exitCurrentQuest(QuestRepeatType repeatType)
	{
		Player player = getPlayer();
		if(player == null)
			return false;

		removePlayerOnKillListener();
		
		for(int itemId : _quest.getItems())
		{
			
			ItemInstance item = player.getInventory().getItemByItemId(itemId);
			if(item == null || itemId == Quest.ADENA_ID)
				continue;
			long count = item.getCount();
			
			player.getInventory().destroyItemByItemId(itemId, count);
			player.getWarehouse().destroyItemByItemId(itemId, count);
		}

		for(String var : _vars.keySet())
			if(var != null)
				unset(var);
		if(repeatType == Quest.REPEATABLE)
			player.removeQuestState(_quest);
		else
		{
			if(repeatType == Quest.DAILY)
				recalcRestartTime();
			setCond(-1);
		}
		getQuest().onExit(this);

		player.sendPacket(new QuestListPacket(player));

		return true;
	}

	public boolean finishQuest(String... sound)
	{
		if(exitCurrentQuest(getQuest().getRepeatType()))
		{
			if(sound.length > 0)
				playSound(sound[0]);
			else
				playSound(Quest.SOUND_FINISH);

			getQuest().onFinish(this);
			getPlayer().getListeners().onQuestFinish(getQuest().getId());
			return true;
		}
		return false;
	}

	public boolean abortQuest()
	{
		if(getQuest().isAbortable() && exitCurrentQuest(QuestRepeatType.REPEATABLE))
		{
			getQuest().onAbort(this);
			return true;
		}
		return false;
	}

	
	public String get(String var)
	{
		return _vars.get(var);
	}

	public Map<String, String> getVars()
	{
		return _vars;
	}

	
	public int getInt(String var)
	{
		int varint = 0;
		try
		{
			String val = get(var);
			if(val == null)
				return 0;
			varint = Integer.parseInt(val);
		}
		catch(Exception e)
		{
			_log.error(getPlayer().getName() + ": variable " + var + " isn't an integer: " + varint, e);
		}
		return varint;
	}

	
	public int getItemEquipped(int loc)
	{
		return getPlayer().getInventory().getPaperdollItemId(loc);
	}

	
	public Player getPlayer()
	{
		return _player;
	}

	
	public Quest getQuest()
	{
		return _quest;
	}

	public boolean checkQuestItemsCount(int... itemIds)
	{
		Player player = getPlayer();
		if(player == null)
			return false;
		for(int itemId : itemIds)
			if(player.getInventory().getCountOf(itemId) <= 0)
				return false;
		return true;
	}

	public long getSumQuestItemsCount(int... itemIds)
	{
		Player player = getPlayer();
		if(player == null)
			return 0;
		long count = 0;
		for(int itemId : itemIds)
			count += player.getInventory().getCountOf(itemId);
		return count;
	}

	
	public long getQuestItemsCount(int itemId)
	{
		Player player = getPlayer();
		return player == null ? 0 : player.getInventory().getCountOf(itemId);
	}

	public long getQuestItemsCount(int... itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += getQuestItemsCount(id);
		return result;
	}

	public boolean haveQuestItem(int itemId, int count)
	{
		if(getQuestItemsCount(itemId) >= count)
			return true;
		return false;
	}

	public boolean haveQuestItem(int itemId)
	{
		return haveQuestItem(itemId, 1);
	}

	
	public void giveItems(int itemId, long count)
	{
		giveItems(itemId, count, -1, itemId == ItemTemplate.ITEM_ID_ADENA);
	}

	public void giveItems(int itemId, long count, long limit)
	{
		giveItems(itemId, count, limit, itemId == ItemTemplate.ITEM_ID_ADENA);
	}

	public void giveItems(int itemId, long count, boolean rate)
	{
		giveItems(itemId, count, -1, rate);
	}

	
	public void giveItems(int itemId, long count, long limit, boolean rate)
	{
		Player player = getPlayer();
		if(player == null)
			return;

		if(count <= 0)
			count = 1;

		if(rate)
		{
			if(!Config.RATE_QUEST_REWARD_EXP_SP_ADENA_ONLY || itemId == ItemTemplate.ITEM_ID_ADENA)
				count = (long) (count * getRateQuestsReward());

			if(limit > 0)
				count = (long) Math.min(limit * Config.QUESTS_REWARD_LIMIT_MODIFIER, count);
		}

		ItemFunctions.addItem(player, itemId, count, true);
		player.sendChanges();
	}

	public void giveItems(int itemId, long count, Element element, int power)
	{
		Player player = getPlayer();
		if(player == null)
			return;

		if(count <= 0)
			count = 1;

		
		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if(template == null)
			return;

		for(int i = 0; i < count; i++)
		{
			ItemInstance item = ItemFunctions.createItem(itemId);

			if(element != Element.NONE)
				item.setAttributeElement(element, power);

			
			player.getInventory().addItem(item);
		}

		player.sendPacket(SystemMessagePacket.obtainItems(template.getItemId(), count, 0));
		player.sendChanges();
	}

	public void dropItem(NpcInstance npc, int itemId, long count)
	{
		Player player = getPlayer();
		if(player == null)
			return;

		ItemInstance item = ItemFunctions.createItem(itemId);
		item.setCount(count);
		item.dropToTheGround(player, npc);
	}

	
	public long rollDrop(long count, double calcChance)
	{
		if(calcChance <= 0 || count <= 0)
			return 0;
		return rollDrop(count, count, calcChance);
	}

	
	public long rollDrop(long min, long max, double calcChance)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0)
			return 0;
		int dropmult = 1;
		calcChance *= getPlayer().getRateQuestsDrop();
		if(getQuest().getPartyType() != Quest.PARTY_NONE)
		{
			Player player = getPlayer();
			if(player.getParty() != null)
				calcChance *= Config.ALT_PARTY_BONUS[Math.min(Config.ALT_PARTY_BONUS.length, player.getParty().getMemberCountInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE)) - 1];
		}
		if(calcChance > 100)
		{
			if((int) Math.ceil(calcChance / 100) <= calcChance / 100)
				calcChance = Math.nextUp(calcChance);
			dropmult = (int) Math.ceil(calcChance / 100);
			calcChance = calcChance / dropmult;
		}
		return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
	}

	public double getRateQuestsReward()
	{
		double rate = _quest.getRewardRate();
		Player player = getPlayer();
		if(player == null)
			return rate * Config.RATE_QUESTS_REWARD;
		return rate * player.getRateQuestsReward();
	}

	
	public boolean rollAndGive(int itemId, long min, long max, long limit, double calcChance)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0)
			return false;
		long count = rollDrop(min, max, calcChance);
		if(count > 0)
		{
			long alreadyCount = getQuestItemsCount(itemId);
			if(alreadyCount + count > limit)
				count = limit - alreadyCount;
			if(count > 0)
			{
				giveItems(itemId, count, false);
				if(count + alreadyCount < limit)
					playSound(Quest.SOUND_ITEMGET);
				else
					return true;
			}
		}
		return false;
	}

	
	public void rollAndGive(int itemId, long min, long max, double calcChance)
	{
		if(calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0)
			return;
		long count = rollDrop(min, max, calcChance);
		if(count > 0)
		{
			giveItems(itemId, count, false);
			playSound(Quest.SOUND_ITEMGET);
		}
	}

	
	public boolean rollAndGive(int itemId, long count, double calcChance)
	{
		if(calcChance <= 0 || count <= 0 || itemId <= 0)
			return false;
		long countToDrop = rollDrop(count, calcChance);
		if(countToDrop > 0)
		{
			giveItems(itemId, countToDrop, false);
			playSound(Quest.SOUND_ITEMGET);
			return true;
		}
		return false;
	}

	
	public boolean isCompleted()
	{
		return getCond() == -1;
	}

	
	public boolean isStarted()
	{
		return getCond() > 0;
	}

	
	public boolean isNotAccepted()
	{
		return getCond() == 0;
	}

	public void killNpcByObjectId(int _objId)
	{
		NpcInstance npc = GameObjectsStorage.getNpc(_objId);
		if(npc != null)
			npc.doDie(null);
		else
			_log.warn("Attemp to kill object that is not npc in quest " + getQuest().getId());
	}

	public String set(String var, String val)
	{
		return set(var, val, true);
	}

	public String set(String var, int intval)
	{
		return set(var, String.valueOf(intval), true);
	}

	
	public String set(String var, String val, boolean store)
	{
		if(val == null)
			val = StringUtils.EMPTY;

		_vars.put(var, val);

		if(store)
			Quest.updateQuestVarInDb(this, var, val);

		return val;
	}

	public String set(String var, int intval, boolean store)
	{
		return set(var, String.valueOf(intval), store);
	}

	
	public void playSound(String sound)
	{
		Player player = getPlayer();
		if(player != null)
			player.sendPacket(new PlaySoundPacket(sound));
	}

	public void playTutorialVoice(String voice)
	{
		Player player = getPlayer();
		if(player != null)
			player.sendPacket(new PlaySoundPacket(PlaySoundPacket.Type.VOICE, voice, 0, 0, player.getLoc()));
	}

	public void onTutorialClientEvent(int number)
	{
		Player player = getPlayer();
		if(player != null)
			player.sendPacket(new TutorialEnableClientEventPacket(number));
	}

	public void showQuestionMark(boolean quest, int tutorialId)
	{
		Player player = getPlayer();
		if(player != null)
			player.sendPacket(new ShowTutorialMarkPacket(quest, tutorialId));
	}

	public void showTutorialHTML(String html)
	{
		Player player = getPlayer();
		if(player == null)
			return;
		getQuest().showTutorialHtmlFile(player, html);
	}

	public void showTutorialClientHTML(String fileName)
	{
		Player player = getPlayer();
		if(player == null)
			return;
		player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2text\\" + fileName + ".htm"));
	}

	
	public void startQuestTimer(String name, long time)
	{
		startQuestTimer(name, time, null);
	}

	
	public void startQuestTimer(String name, long time, NpcInstance npc)
	{
		QuestTimer timer = new QuestTimer(name, time, npc);
		timer.setQuestState(this);
		QuestTimer oldTimer = getTimers().put(name, timer);
		if(oldTimer != null)
			oldTimer.stop();
		timer.start();
	}

	public boolean isRunningQuestTimer(String name)
	{
		return getTimers().get(name) != null;
	}

	public boolean cancelQuestTimer(String name)
	{
		QuestTimer timer = removeQuestTimer(name);
		if(timer != null)
			timer.stop();
		return timer != null;
	}

	QuestTimer removeQuestTimer(String name)
	{
		QuestTimer timer = getTimers().remove(name);
		if(timer != null)
			timer.setQuestState(null);
		return timer;
	}

	public void pauseQuestTimers()
	{
		getQuest().pauseQuestTimers(this);
	}

	public void stopQuestTimers()
	{
		for(QuestTimer timer : getTimers().values())
		{
			timer.setQuestState(null);
			timer.stop();
		}
		_timers.clear();
	}

	public void resumeQuestTimers()
	{
		getQuest().resumeQuestTimers(this);
	}

	Map<String, QuestTimer> getTimers()
	{
		return _timers;
	}

	
	public long takeItems(int itemId, long count)
	{
		Player player = getPlayer();
		if(player == null)
			return 0;

		
		ItemInstance item = player.getInventory().getItemByItemId(itemId);
		if(item == null)
			return 0;
		
		if(count < 0 || count > item.getCount())
			count = item.getCount();

		
		player.getInventory().destroyItemByItemId(itemId, count);
		
		player.sendPacket(SystemMessagePacket.removeItems(itemId, count));

		return count;
	}

	public long takeAllItems(int itemId)
	{
		return takeItems(itemId, -1);
	}

	public long takeAllItems(int... itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += takeAllItems(id);
		return result;
	}

	public long takeAllItems(Collection<Integer> itemsIds)
	{
		long result = 0;
		for(int id : itemsIds)
			result += takeAllItems(id);
		return result;
	}

	
	public String unset(String var)
	{
		if(var == null)
			return null;
		String old = _vars.remove(var);
		if(old != null)
			Quest.deleteQuestVarInDb(this, var);
		return old;
	}

	private boolean checkPartyMember(Player member, int cond, int maxrange, GameObject rangefrom)
	{
		if(member == null)
			return false;
		if(rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, maxrange))
			return false;
		QuestState qs = member.getQuestState(getQuest().getId());
		if(qs == null || qs.getCond() != cond)
			return false;
		return true;
	}

	public List<Player> getPartyMembers(int cond, int maxrange, GameObject rangefrom)
	{
		List<Player> result = new ArrayList<Player>();
		Party party = getPlayer().getParty();
		if(party == null)
		{
			if(checkPartyMember(getPlayer(), cond, maxrange, rangefrom))
				result.add(getPlayer());
			return result;
		}

		for(Player _member : party.getPartyMembers())
			if(checkPartyMember(_member, cond, maxrange, rangefrom))
				result.add(getPlayer());

		return result;
	}

	public Player getRandomPartyMember(int cond, int maxrangefromplayer)
	{
		return getRandomPartyMember(cond, maxrangefromplayer, getPlayer());
	}

	public Player getRandomPartyMember(int cond, int maxrange, GameObject rangefrom)
	{
		List<Player> list = getPartyMembers(cond, maxrange, rangefrom);
		if(list.size() == 0)
			return null;
		return list.get(Rnd.get(list.size()));
	}

	
	public NpcInstance addSpawn(int npcId)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, 0);
	}

	public NpcInstance addSpawn(int npcId, int despawnDelay)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, despawnDelay);
	}

	public NpcInstance addSpawn(int npcId, int x, int y, int z)
	{
		return addSpawn(npcId, x, y, z, 0, 0, 0);
	}

	
	public NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay)
	{
		return addSpawn(npcId, x, y, z, 0, 0, despawnDelay);
	}

	
	public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
	}

	public int calculateLevelDiffForDrop(int mobLevel, int player)
	{
		if(!Config.DEEPBLUE_DROP_RULES)
			return 0;
		return Math.max(player - mobLevel - Config.DEEPBLUE_DROP_MAXDIFF, 0);
	}

	public int getCond()
	{
		if(_cond == null)
		{
			int condsMask = getCondsMask();
			if(condsMask != -1 && (condsMask & 0x80000000) != 0)
			{
				condsMask &= 0x7fffffff;
				for(int i = 1; i < 32; i++)
				{
					condsMask = (condsMask >> 1);
					if(condsMask == 0)
					{
						condsMask = i;
						break;
					}
				}
			}
			_cond = condsMask;
		}

		return _cond.intValue();
	}

	public int getCondsMask()
	{
		if(_condsMask == null)
			_condsMask = getInt(VAR_COND);

		if(_condsMask.intValue() == -1)
		{
			if(getRestartTime() > 0 && isNowAvailable())
				_condsMask = 0;
		}
		return _condsMask.intValue();
	}

	public void setCond(int cond, String... sound)
	{
		setCond(cond, true, sound);
	}

	public void setCond(int cond, boolean store, String... sound)
	{
		if(cond < -1)
		{
			_log.warn("Cannot set negate cond in quest ID[" + getQuest().getId() + "]!");
			return;
		}

		if(cond == getCond())
			return;

		boolean accepted = cond > 0 && !isStarted();

		_cond = cond;

		if(cond != -1)
		{
			int condsMask = getCondsMask();
			if((condsMask & 0x80000000) != 0)
			{
				condsMask &= 0x80000001 | ((1 << cond) - 1);
				cond = condsMask | (1 << (cond - 1));
			}
			else
				cond = 0x80000001 | (1 << (cond - 1)) | ((1 << condsMask) - 1);
		}
		_condsMask = cond;

		set(VAR_COND, String.valueOf(cond), store);

		if(accepted)
			getQuest().onAccept(this);

		final Player player = getPlayer();
		if(player != null)
		{
			if(getQuest().isVisible(player))
			{
				if(isStarted())
				{
					player.sendPacket(new ExShowQuestMarkPacket(getQuest().getId(), _cond.intValue()));
					if(sound.length > 0)
						playSound(sound[0]);
					else
						playSound(accepted ? Quest.SOUND_ACCEPT : Quest.SOUND_MIDDLE);
				}
				player.sendPacket(new QuestListPacket(player));
			}
		}
	}

	
	private void recalcRestartTime()
	{
		Calendar reDo = Calendar.getInstance();
		if(reDo.get(Calendar.HOUR_OF_DAY) >= RESTART_HOUR)
			reDo.add(Calendar.DATE, 1);
		reDo.set(Calendar.HOUR_OF_DAY, RESTART_HOUR);
		reDo.set(Calendar.MINUTE, RESTART_MINUTES);

		_restartTime = reDo.getTimeInMillis();

		set("restartTime", String.valueOf(_restartTime));
	}

	private long getRestartTime()
	{
		if(_restartTime == 0)
		{
			String val = get("restartTime");
			if(val != null)
				_restartTime = Long.parseLong(val);
		}
		return _restartTime;
	}

	
	private boolean isNowAvailable()
	{
		return getRestartTime() <= System.currentTimeMillis();
	}
}