package l2s.gameserver.model.entity.events.impl;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.UndergroundColiseumHistoryDAO;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;


public class UndergroundColiseumEvent extends Event
{
	private class Timer extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			List<Player> leaders = UndergroundColiseumEvent.this.getObjects("registered_leaders");

			Player leader1 = (Player)CollectionUtils.safeGet(leaders, 0);
			Player leader2 = (Player)CollectionUtils.safeGet(leaders, 1);
			if(leader1 == null || leader2 == null)
				return;

			if((!UndergroundColiseumEvent.this.isValid(leader1, leaders)) || (!UndergroundColiseumEvent.this.isValid(leader2, leaders)))
			{
		        ExShowScreenMessage p = new ExShowScreenMessage(NpcString.THE_MATCH_IS_AUTOMATICALLY_CANCELED_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_ADMISSION_MANAGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, new String[0]);
		        
		        broadCast(leader1, p);
		        broadCast(leader2, p);
		        return;
			}
			UndergroundColiseumBattleEvent battleEvent = new UndergroundColiseumBattleEvent(UndergroundColiseumEvent.this, new Player[] { leader1, leader2 });
			battleEvent.reCalcNextTime(false);		      
		}


        private void broadCast(Player player, IBroadcastPacket packet)
		{
            if(player.getParty() == null)
                player.sendPacket(packet);
            else
                player.getParty().broadCast(packet);
        }
	}

	public static final String REGISTERED_LEADERS = "registered_leaders";
	public static final String MANAGER = "manager";
	public static final String DOORS = "doors";
	public static final String TOWERS = "towers";
	public static final String ZONES = "zones";
	public static final String BOXES = "boxes";
	public static final String BLUE_TELEPORT_LOCS = "blue_teleport_locs";
	public static final String RED_TELEPORT_LOCS = "red_teleport_locs";
	public static final String HISTORY = "history";

	public static final int REGISTER_COUNT = 5;
	public static final int PARTY_SIZE = 7;
	private long _startTime;

	private final int _minLevel;
	private final int _maxLevel;

	private final int _circleStart;
	private Future<?> _timerTask;
	
	public UndergroundColiseumEvent(MultiValueSet<String> set)
	{
		super(set);
		_minLevel = set.getInteger("min_level", 1);
		_maxLevel = set.getInteger("max_level", Integer.MAX_VALUE);
		_circleStart = set.getInteger("circle_count", 6);
	}
	  
    @Override
    public void initEvent()
    {
        super.initEvent();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.startTimeMillis());
        if(calendar.get(7) == _circleStart)
            UndergroundColiseumHistoryDAO.getInstance().delete(getId());
        else
            addObjects("history", UndergroundColiseumHistoryDAO.getInstance().select(getId()));
    }

    public void register(Player player)
    {
        if(!isValid(player, Collections.emptyList()))
            return;
        
        List<Player> leaders = getObjects("registered_leaders");
        if(leaders.size() >= 5)
            return;
        leaders.add(player);
    }
    
    public void addToHistory(final String name)
    {
        List<Pair<String, Integer>> history = getObjects("history");
        Pair<String, Integer> winner = null;
        for(Pair<String, Integer> pair : history)
        {
            if(((String)pair.getKey()).equals(name))
            {
                winner = pair;
                break;
            }
        }
        if (winner == null)
        {
            winner = new MutablePair<String, Integer>(name, 1);
            addObject("history", winner);
            UndergroundColiseumHistoryDAO.getInstance().insert(getId(), winner);
        }
        else
        {
            winner.setValue(winner.getValue() + 1);
            UndergroundColiseumHistoryDAO.getInstance().update(getId(), winner);
        }
    }
    
	public Pair<String, Integer> getTopWinner()
	{
		List<Pair<String, Integer>> history = getObjects(UndergroundColiseumEvent.HISTORY);

		int max = Integer.MIN_VALUE;
		Pair<String, Integer> pair = null;
		for(Pair<String, Integer> temp : history)
		{
			if(temp.getValue() > max)
			{
				pair = temp;
				max = pair.getValue();
			}
		}

		return pair;
	}

	@Override
	public void startEvent()
	{
		startTimer();
	}

	@Override
	public void stopEvent(boolean force)
	{
		removeObjects("registered_leaders");
	    
	    stopTimer();
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		_startTime = System.currentTimeMillis();

		registerActions();
	}

	@Override
	protected long startTimeMillis()
	{
		return _startTime;
	}

	@Override
	public EventType getType()
	{
		return EventType.MAIN_EVENT;
	}

    @Override
    public boolean isInProgress()
    {
        return true;
    }
    
	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

    public void startTimer()
    {
        _timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Timer(), 5000L, 5000L);
    }
    
    public void stopTimer()
    {
        if(_timerTask != null)
        {
            _timerTask.cancel(false);
            _timerTask = null;
        }
    }
    
    public boolean isValid(Player leaderPlayer, List<Player> players)
    {
        if(leaderPlayer == null)
            return false;
        
        boolean fail = false;
        Party party = leaderPlayer.getParty();
        if(party == null || party.getMemberCount() < 7)
            fail = true;
        else
        {
            for(Player member : party)
            {
                if(member.getLevel() < _minLevel || member.getLevel() > _maxLevel)
                {
                    fail = true;
                    break;
                }
            }
        }
        if(fail)
            players.remove(leaderPlayer);
        return true;
    }
}