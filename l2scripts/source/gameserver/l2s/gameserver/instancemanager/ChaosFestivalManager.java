package l2s.gameserver.instancemanager;

import org.napile.pair.primitive.IntLongPair;
import org.napile.primitive.maps.IntLongMap;
import org.napile.primitive.maps.impl.HashIntLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.Announcements;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.ChaosFestivalDAO;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.utils.TimeUtils;

public class ChaosFestivalManager
{
    private static final Logger _log = LoggerFactory.getLogger(ChaosFestivalManager.class);
    private static final ChaosFestivalManager _instance = new ChaosFestivalManager();
    
    private static final String CYCLE_VAR = "chaosfest_cycle";
    private static final String CYCLE_START_TIME_VAR = "chaosfest_cycle_start";
    private static final String WINNER_RECEIVED_VAR = "chaosfest_win_receive";
    private static final String WINNER_PLAYER_OBJECT_ID_VAR = "chaosfest_win_id";
    private static final String WINNER_PLAYER_NAME_VAR = "chaosfest_win_name";
    private static final String WINNER_CLAN_OBJECT_ID_VAR = "chaosfest_win_clan";
    
    private final IntLongMap _statistic = new HashIntLongMap();
    private ChaosFestivalEvent _event = null;
    private int _cycle = 0;
    private boolean _winnerRewardReceived = false;
    private int _winnerObjectId = 0;
    private String _winnerName = null;
    private Clan _winnerClan = null;

    public static ChaosFestivalManager getInstance()
    {
        return _instance;
    }

    public void init()
    {
        _event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 6);
        if(_event == null)
        {
            _log.warn("Cannot find Chaos Festival event!");
            return;
        }
        
        ChaosFestivalDAO.getInstance().restore(_statistic);
        
        _cycle = ServerVariables.getInt(CYCLE_VAR, 0);
        _winnerRewardReceived = ServerVariables.getBool(WINNER_RECEIVED_VAR, false);
        _winnerName = ServerVariables.getString(WINNER_PLAYER_NAME_VAR, null);
        
        setWinnerObjectId(ServerVariables.getInt(WINNER_PLAYER_OBJECT_ID_VAR, 0));
        setWinnerClanId(ServerVariables.getInt(WINNER_CLAN_OBJECT_ID_VAR, 0));
        recycle(ServerVariables.getInt(CYCLE_START_TIME_VAR, 0) * 1000L, true);
    }

    public int getWinnerObjectId()
    {
        return _winnerObjectId;
    }

    private void setWinnerObjectId(int objectId)
    {
        _winnerObjectId = objectId;
        if(objectId > 0)
        {
            Player player = GameObjectsStorage.getPlayer(_winnerObjectId);
            if(player != null)
                _winnerName = player.getName();
            else
            {
                String name = CharacterDAO.getInstance().getNameByObjectId(objectId, true);
                if(name != null)
                    _winnerName = name;
            }
        }
        else
            _winnerName = null;
    }

    public String getWinnerName()
    {
        Player player = GameObjectsStorage.getPlayer(_winnerObjectId);
        if(player != null)
            _winnerName = player.getName();
        
        return _winnerName;
    }

    public boolean isWinner(int objectId)
    {
        return _winnerObjectId > 0 && _winnerObjectId == objectId;
    }

    public boolean isWinner(Player player)
    {
        return isWinner(player.getObjectId());
    }

    public boolean isWinnerReceived(int objectId)
    {
        return _winnerRewardReceived && _winnerObjectId > 0 && _winnerObjectId == objectId;
    }

    public boolean isWinnerReceived(Player player)
    {
        return isWinnerReceived(player.getObjectId());
    }

    public boolean isWinnerNotReceived(int objectId)
    {
        return !_winnerRewardReceived && _winnerObjectId > 0 && _winnerObjectId == objectId;
    }

    public boolean isWinnerNotReceived(Player player)
    {
        return isWinnerNotReceived(player.getObjectId());
    }

    public void setWinnerRewardReceived()
    {
        _winnerRewardReceived = true;
        ServerVariables.set(WINNER_RECEIVED_VAR, _winnerRewardReceived);
        Player player = GameObjectsStorage.getPlayer(_winnerObjectId);
        
        if(player != null)
            player.broadcastUserInfo(true);
    }

    public Clan getWinnerClan()
    {
        return _winnerClan;
    }

    private void setWinnerClan(Clan clan)
    {
        _winnerClan = clan;
    }

    private void setWinnerClanId(int clanId)
    {
        _winnerClan = ClanTable.getInstance().getClan(clanId);
    }

    public String getWinnerClanName()
    {
        if(_winnerClan != null)
            return _winnerClan.getName();
        
        return null;
    }

    public boolean isFromWinnerClan(Player player)
    {
        return _winnerClan != null && _winnerClan.isAnyMember(player.getObjectId());
    }

    public long getPoints(int objectId)
    {
        return _statistic.get(objectId);
    }

    public void addPoints(int objectId, long points)
    {
        long currentPoints = _statistic.get(objectId) + points;
        _statistic.put(objectId, currentPoints);
        
        ChaosFestivalDAO.getInstance().insert(objectId, currentPoints);
    }

    private void recycle(long initTime, boolean onInit)
    {
        long startTime = initTime > System.currentTimeMillis() ? 0L : initTime;
        long endTime = _event.getStartCycleTimePattern().next(startTime);
        while(endTime <= System.currentTimeMillis())
        {
            startTime = endTime;
            endTime = _event.getStartCycleTimePattern().next(startTime);
        }
        long cycleStartTime = startTime;
        long cycleEndTime = endTime;
        if(!onInit || cycleStartTime > initTime)
        {
            if(_cycle > 0)
            {
                Announcements.announceToAll(new SystemMessagePacket(SystemMsg.CYCLE_S1_OF_THE_CEREMONY_OF_CHAOS_HAS_ENDED).addInteger(_cycle));
                _log.info("Chaos Festival: Cycle #" + _cycle + " ended.");
                calcWinner();
            }
            _statistic.clear();
            ChaosFestivalDAO.getInstance().clear();
            ThreadPoolManager.getInstance().schedule(() -> {
                ServerVariables.set(CYCLE_VAR, ++_cycle);
                ServerVariables.set(CYCLE_START_TIME_VAR, (int)(cycleStartTime / 1000L));
                Announcements.announceToAll(new SystemMessagePacket(SystemMsg.CYCLE_S1_OF_THE_CEREMONY_OF_CHAOS_HAS_BEGUN).addInteger(_cycle));
                _log.info("Chaos Festival: Started new cycle #" + _cycle + ". Cycle end time: " + TimeUtils.toSimpleFormat(cycleEndTime));
            }, onInit ? 0L : 60000L);
        }
        else
            _log.info("Chaos Festival: Continued cycle #" + this._cycle + ". Cycle end time: " + TimeUtils.toSimpleFormat(cycleEndTime));
        
        ThreadPoolManager.getInstance().schedule(() -> recycle(cycleEndTime, false), cycleEndTime - System.currentTimeMillis());
    }

    private IntLongPair getTopRanker()
    {
        IntLongPair top = null;
        for(IntLongPair pair : _statistic.entrySet())
        {
            if(pair.getValue() > 0 && (top == null || pair.getValue() > top.getValue()));
            top = pair;
        }
        return top;
    }

    public int getTopRankerId()
    {
        IntLongPair top = getTopRanker();
        if(top != null)
            return top.getKey();
        
        return 0;
    }

    public long getTopRankerPoints()
    {
        IntLongPair top = getTopRanker();
        if(top != null)
            return top.getValue();
        
        return 0;
    }

    private void calcWinner()
    {
        IntLongPair top = null;
        Clan winnerClan = null;
        while(!_statistic.isEmpty())
        {
            Clan clan;
            top = getTopRanker();
            _statistic.remove(top.getKey());
            if(CharacterDAO.getInstance().getNameByObjectId(top.getKey(), true) != null)
            {
                clan = ClanTable.getInstance().getClanByCharId(top.getKey());
                if (clan != null)
                {
                    winnerClan = clan;
                    break;
                }
                continue;
            }
        }
        _winnerRewardReceived = false;
        if(top == null || winnerClan == null)
        {
            setWinnerObjectId(0);
            setWinnerClan(null);
        }
        else
        {
            setWinnerObjectId(top.getKey());
            setWinnerClan(winnerClan);
        }
        
        ServerVariables.set(WINNER_RECEIVED_VAR, _winnerRewardReceived);
        ServerVariables.set(WINNER_PLAYER_NAME_VAR, _winnerName);
        ServerVariables.set(WINNER_PLAYER_OBJECT_ID_VAR, _winnerObjectId);
        ServerVariables.set(WINNER_CLAN_OBJECT_ID_VAR, _winnerClan == null ? 0 : _winnerClan.getClanId());
    }
}