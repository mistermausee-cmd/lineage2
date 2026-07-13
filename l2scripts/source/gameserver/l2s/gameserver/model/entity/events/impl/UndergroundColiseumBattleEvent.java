package l2s.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.napile.primitive.lists.IntList;
import org.napile.primitive.lists.impl.CArrayIntList;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.JoinedIterator;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.listener.actor.OnReviveListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.objects.SpawnExObject;
import l2s.gameserver.model.entity.events.objects.UCMemberObject;
import l2s.gameserver.model.entity.events.objects.UCTeamObject;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExPVPMatchRecord;
import l2s.gameserver.network.l2.s2c.ExPVPMatchUserDie;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.utils.Location;

public class UndergroundColiseumBattleEvent extends Event implements Iterable<UCMemberObject>
{
    private class RessurectTask extends RunnableImpl
    {
        private Player _player;
        private int _seconds = 11;
        
        public RessurectTask(Player player)
        {
            _player = player;
        }
        
        public void runImpl()
        {
            if(_player.getTeam() == TeamType.NONE || !UndergroundColiseumBattleEvent.this.isInProgress())
                return;

            IntObjectMap<Future<?>> tasks = UndergroundColiseumBattleEvent.this._deadList.get(_player.getTeam());
            _seconds--;
            if(_seconds == 0)
            {
                tasks.remove(_player.getObjectId());
                final SpawnExObject spawnExObject = UndergroundColiseumBattleEvent.this.getFirstObject("towers");
                final List<NpcInstance> npcs = spawnExObject.getAllSpawned();
                final NpcInstance ressurectTower = CollectionUtils.safeGet(npcs, this._player.getTeam().ordinalWithoutNone());
                if(ressurectTower == null || ressurectTower.isDead())
                    return;
                
                UndergroundColiseumBattleEvent.this._reviveList.add(_player.getObjectId());
                final List<Location> locList = UndergroundColiseumBattleEvent.this._runnerEvent.getObjects((_player.getTeam() == TeamType.BLUE) ? "blue_teleport_locs" : "red_teleport_locs");
                _player.teleToLocation(Rnd.get(locList));
                _player.doRevive();
                _player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
                _player.setCurrentCp(_player.getMaxCp());
            }
            else
            {
                this._player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds));
                final Future<?> f = ThreadPoolManager.getInstance().schedule(this, 1000L);
                tasks.put(_player.getObjectId(), f);
            }
        }
    }

    private class Listeners implements OnKillListener, OnReviveListener, OnPlayerPartyLeaveListener, OnTeleportListener, OnPlayerExitListener
    {
        @Override
        public void onRevive(Creature actor)
        {
            if(actor.getTeam() == TeamType.NONE || !UndergroundColiseumBattleEvent.this.isInProgress())
                return;
            
            UndergroundColiseumBattleEvent.this._reviveList.remove(actor.getObjectId());
            final IntObjectMap<Future<?>> tasks = UndergroundColiseumBattleEvent.this._deadList.get(actor.getTeam());
            final Future<?> future = tasks.remove(actor.getObjectId());
            if(future != null)
                future.cancel(false);
        }
        
        @Override
        public void onKill(final Creature actor, final Creature victim)
        {
            if(victim.getTeam() == TeamType.NONE || !UndergroundColiseumBattleEvent.this.isInProgress() || !victim.isPlayer())
                return;
            
            final TeamType victimTeam = victim.getTeam();
            final IntObjectMap<Future<?>> tasks = UndergroundColiseumBattleEvent.this._deadList.get(victim.getTeam());
            if(!victim.containsEvent(UndergroundColiseumBattleEvent.this) || !actor.containsEvent(UndergroundColiseumBattleEvent.this))
                return;
            
            final UCTeamObject killerTeam = UndergroundColiseumBattleEvent.this.getFirstObject(actor.getTeam());
            final UCTeamObject deathTeam = UndergroundColiseumBattleEvent.this.getFirstObject(victim.getTeam());
            final UCMemberObject killerMember = this.getMember(actor.getPlayer());
            final UCMemberObject deathMember = this.getMember(victim.getPlayer());
            killerMember.incKills();
            deathMember.incDeaths();
            killerTeam.incKills();
            deathTeam.incDeaths();
            
            UndergroundColiseumBattleEvent.this.broadcastToMembers(new ExPVPMatchUserDie(UndergroundColiseumBattleEvent.this));
            final SpawnExObject spawnExObject = UndergroundColiseumBattleEvent.this.getFirstObject("towers");
            final List<NpcInstance> npcs = spawnExObject.getAllSpawned();
            UndergroundColiseumBattleEvent.this.checkForWinner();
            
            final NpcInstance ressurectTower = CollectionUtils.safeGet(npcs, victimTeam.ordinalWithoutNone());
            if(ressurectTower == null || ressurectTower.isDead())
                return;
            
            tasks.put(victim.getObjectId(), ThreadPoolManager.getInstance().schedule(new RessurectTask(victim.getPlayer()), 1000L));
        }
        
        @Override
        public boolean ignorePetOrSummon()
        {
            return true;
        }
        
        @Override
        public void onPartyLeave(final Player player)
        {
            exitPlayerAndCancelIfNeed(player, true);
        }
        
        @Override
        public void onPlayerExit(final Player player)
        {
            exitPlayerAndCancelIfNeed(player, false);
        }
        
        @Override
        public void onTeleport(final Player player, final int x, final int y, final int z, final Reflection reflection)
        {
            if(!UndergroundColiseumBattleEvent.this.isInProgress())
                return;
            
            if(UndergroundColiseumBattleEvent.this._reviveList.remove(player.getObjectId()))
                return;
            
            exitPlayerAndCancelIfNeed(player, false);
        }
        
        private void exitPlayerAndCancelIfNeed(final Player player, final boolean tp)
        {
            player.removeListener(UndergroundColiseumBattleEvent.this._listeners);
            for(final TeamType teamType : TeamType.VALUES)
            {
                final UCTeamObject teamObject = UndergroundColiseumBattleEvent.this.getFirstObject(teamType);
                if(teamObject == null)
                    return;
                
                for (int i = 0; i < teamObject.getMembers().length; i++)
                {
                    final UCMemberObject memberObject = teamObject.getMembers()[i];
                    if(memberObject != null && memberObject.getPlayer() == player)
                    {
                        player.sendPacket(new ExPVPMatchRecord(2, TeamType.NONE, UndergroundColiseumBattleEvent.this));
                        teamObject.getMembers()[i] = null;
                        player.setTeam(TeamType.NONE);
                        if(tp && UndergroundColiseumBattleEvent.this.isInProgress())
                            player.teleToLocation(player.getStablePoint());
                        break;
                    }
                }
                UndergroundColiseumBattleEvent.this.checkForWinner();
            }
        }
        
        private UCMemberObject getMember(final Player player)
        {
            final UCTeamObject teamObject = UndergroundColiseumBattleEvent.this.getFirstObject(player.getTeam());
            for(final UCMemberObject ucMemberObject : teamObject.getMembers())
            {
                if(ucMemberObject != null && ucMemberObject.getPlayer() == player)
                    return ucMemberObject;
            }
            throw new RuntimeException();
        }
    }

    private Map<TeamType, IntObjectMap<Future<?>>> _deadList = new ConcurrentHashMap<TeamType, IntObjectMap<Future<?>>>();
    private IntList _reviveList = new CArrayIntList();
    private UndergroundColiseumEvent _runnerEvent;
    private boolean _isInProgress;
    private TeamType _winner = TeamType.NONE;
    private Listeners _listeners = new Listeners();
    
    public UndergroundColiseumBattleEvent(MultiValueSet<String> set)
    {
        super(set);
    }
    
    protected UndergroundColiseumBattleEvent(UndergroundColiseumEvent event, Player... leaders)
    {
        super(0, "");
        
        UndergroundColiseumBattleEvent motherBattleEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 5);
        motherBattleEvent.cloneTo(this);
        addObjects("towers", event.getObjects("towers"));
        addObjects("doors", event.getObjects("doors"));
        addObjects("zones", event.getObjects("zones"));
        addObjects("boxes", event.getObjects("boxes"));
        _runnerEvent = event;
        event.stopTimer();
        for(int i = 0; i < leaders.length; ++i)
        {
            _deadList.put(TeamType.VALUES[i], new CHashIntObjectMap<Future<?>>());
            addObject(TeamType.VALUES[i], new UCTeamObject(leaders[i], _listeners));
        }
    }
    
    @Override
    public void initEvent()
    {}
    
    @Override
    public void startEvent()
    {
        SpawnExObject spawnEx = this._runnerEvent.getFirstObject("manager");
        NpcInstance manager = spawnEx.getFirstSpawned();
        for(final UCMemberObject member : this)
        {
            if(member == null)
                continue;
            
            if (member.getPlayer().getDistance(manager) > 400.0)
            {
                broadcastToMembers(new ExShowScreenMessage(NpcString.THE_MATCH_IS_AUTOMATICALLY_CANCELED_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_ADMISSION_MANAGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, new String[0]));
                cancel();
                return;
            }
        }
        
        broadcastRecord(0, TeamType.NONE);
        for(int i = 0; i < TeamType.VALUES.length; ++i)
        {
            TeamType teamType = TeamType.VALUES[i];
            UCTeamObject teamObject = getFirstObject(teamType);
            Party party = teamObject.getParty();
            UCMemberObject[] objects = teamObject.getMembers();
            
            for(final UCMemberObject memberObject : objects)
            {
                if(memberObject != null)
                {
                    Player player = memberObject.getPlayer();
                    player.setTeam(teamType);
                    player.addEvent(this);
                    SkillHolder.getInstance().getSkill(5661, 1).getEffects(player, player);
                    List<Location> locList = this._runnerEvent.getObjects((teamType == TeamType.BLUE) ? "blue_teleport_locs" : "red_teleport_locs");
                    int index = party.indexOf(player);
                    if(index < 0)
                        throw new UnsupportedOperationException();
                    
                    player.setStablePoint(player.getLoc());
                    player.teleToLocation(locList.get(index));
                    if(player.isDead())
                        player.doRevive();
                    for(Servitor servitor : player.getServitors())
                        servitor.teleToLocation(locList.get(index));
                }
            }
        }
        broadcastRecord(1, TeamType.NONE);
        _isInProgress = true;
        super.startEvent();
        SpawnExObject spawnExObject = getFirstObject("towers");
        List<NpcInstance> npcs = spawnExObject.getAllSpawned();
        for(int j = 0; j < npcs.size(); ++j)
            npcs.get(j).setTeam(TeamType.VALUES[j]);
    }
    
    @Override
    public void stopEvent(boolean force)
    {
        if(!_isInProgress)
            return;
        
        clearActions();
        super.stopEvent(force);
        _isInProgress = false;
        if(_winner == TeamType.NONE)
        {
            UCTeamObject blueTeam = this.getFirstObject(TeamType.BLUE);
            UCTeamObject redTeam = this.getFirstObject(TeamType.RED);
            int blueKills = blueTeam.getKills();
            int redKills = redTeam.getKills();
            if(blueKills > redKills)
                _winner = TeamType.BLUE;
            else if(redKills > blueKills)
                _winner = TeamType.RED;
            else if(redKills == blueKills)
            {
                if(blueTeam.getDeaths() < redTeam.getDeaths())
                    _winner = TeamType.BLUE;
                else if(redTeam.getDeaths() < blueTeam.getDeaths())
                    _winner = TeamType.RED;
                else if(blueTeam.getRegisterTime() < redTeam.getRegisterTime())
                    _winner = TeamType.BLUE;
                else if(redTeam.getRegisterTime() < blueTeam.getRegisterTime())
                    _winner = TeamType.RED;
            }
        }
        if(_winner == TeamType.NONE)
            throw new RuntimeException();
        
        broadcastRecord(2, _winner);
        for(TeamType teamType : TeamType.VALUES)
        {
            UCTeamObject teamObject = getFirstObject(teamType);
            UCMemberObject[] teams = teamObject.getMembers();
            cancelResurrects(teamType);
            for(UCMemberObject memberObject : teams)
            {
                if(memberObject != null)
                {
                    Player player = memberObject.getPlayer();
                    player.setTeam(TeamType.NONE);
                    player.removeEvent(this);
                    if(player.isDead())
                        player.doRevive();
                    
                    player.getAbnormalList().stop(5661);
                    player.setCurrentCp(player.getMaxCp());
                    player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                    if(teamType == _winner);
                    player.teleToLocation(player.getStablePoint());
                    for(Servitor servitor : player.getServitors())
                        servitor.teleToLocation(player.getStablePoint());
                }
            }
            if(teamType == _winner)
            {
                _runnerEvent.addToHistory(teamObject.getLeader().getName());
                _runnerEvent.register(teamObject.getLeader());
            }
        }
        cancel();
    }
    
    public void broadcastRecord(int type, TeamType teamType)
    {
        ExPVPMatchRecord packet = new ExPVPMatchRecord(type, teamType, this);
        ExPVPMatchUserDie packet2 = (type == 1) ? new ExPVPMatchUserDie(this) : null;
        for(UCMemberObject memberObject : this)
        {
            if(memberObject == null)
                continue;
            
            Player player = memberObject.getPlayer();
            player.sendPacket(packet);
            if(packet2 == null)
                continue;
            
            player.sendPacket(packet2);
        }
    }
    
    @Override
    public void announce(final SystemMsg msgId, final int val, final int time)
    {
        switch (val)
        {
            case -180:
            case -120:
            case -60:
            {
                broadcastToMembers(new ExShowScreenMessage(NpcString.MATCH_BEGINS_IN_S1_MINUTES, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, new String[] { String.valueOf(-(val / 60)) }));
                break;
            }
            case 590:
            case 591:
            case 592:
            case 593:
            case 594:
            case 595:
            case 596:
            case 597:
            case 598:
            case 599:
            {
                broadcastToMembers(new ExShowScreenMessage(NpcString.S1_SECONDS_REMAINING, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, true, new String[] { String.valueOf(600 - val) }));
                break;
            }
        }
    }
    
    public void broadcastToMembers(IBroadcastPacket... p)
    {
        for(final UCMemberObject obj : this)
        {
            if(obj != null)
                obj.getPlayer().sendPacket(p);
        }
    }
    
    @Override
    public void reCalcNextTime(boolean onInit)
    {
        registerActions();
    }
    
    @Override
    protected long startTimeMillis()
    {
        return System.currentTimeMillis() + 180000L;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Iterator<UCMemberObject> iterator()
    {
        UCTeamObject blue = getFirstObject(TeamType.BLUE);
        UCTeamObject red = getFirstObject(TeamType.RED);
        return new JoinedIterator(new Iterator[] { blue.iterator(), red.iterator() });
    }
    
    @Override
    public EventType getType()
    {
        return EventType.PVP_EVENT;
    }
    
    @Override
    public void checkRestartLocs(final Player player, final Map<RestartType, Boolean> r)
    {
        r.clear();
    }
    
    @Override
    public boolean isInProgress()
    {
        return _isInProgress;
    }
    
    @Override
    public void onRemoveEvent(GameObject o)
    {
        if(o.isPlayer())
            o.getPlayer().removeListener(_listeners);
    }
    
    @Override
    public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
    {
        if(target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
            return SystemMsg.INVALID_TARGET;

        return null;
    }
    
    @Override
    public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet)
    {
        return true;
    }
    
    private void checkForWinner()
    {
    	for(TeamType teamType : TeamType.VALUES)
    	{
    		UCTeamObject teamObject = getFirstObject(teamType);
            boolean allDead = true;
            int count = 0;
            UCMemberObject[] members = teamObject.getMembers();
            for(UCMemberObject memberObject : members)
            {
                if(memberObject != null)
                {
                    if(!memberObject.getPlayer().isDead())
                        allDead = false;
                    count++;
                }
            }
            if(allDead || count < 7)
            {
                if(_winner != TeamType.NONE)
                    return;
                
                _winner = teamType.revert();
                if(isInProgress())
                {
                    clearActions();
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl()
                    {
                        protected void runImpl() throws Exception
                        {
                            UndergroundColiseumBattleEvent.this.stopEvent(false);
                        }
                    }, 1000L);
                    break;
                }
                broadcastToMembers(new ExShowScreenMessage(NpcString.MATCH_CANCELLED, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, new String[0]));
                cancel();
                break;
            }
    	}
    }
    
    public void cancel()
    {
        List<Player> leaders = _runnerEvent.getObjects("registered_leaders");
        leaders.remove(0);
        leaders.remove(0);
        clearActions();
        _runnerEvent.startTimer();
        removeObjects(TeamType.RED);
        removeObjects(TeamType.BLUE);
    }
    
    public void cancelResurrects(TeamType team)
    {
        IntObjectMap<Future<?>> tasks = _deadList.get(team);
        for(Future<?> task : tasks.values())
            task.cancel(false);
        
        tasks.clear();
    }
}