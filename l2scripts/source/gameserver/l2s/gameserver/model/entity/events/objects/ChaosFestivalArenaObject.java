package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import l2s.commons.geometry.Circle;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.ObservableArena;
import l2s.gameserver.model.ObservePoint;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseMemberList;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseMemberUpdate;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseObserveList;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseObserveMode;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseResult;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;


public final class ChaosFestivalArenaObject extends ObservableArena implements Serializable
{

	public static enum BattleState
	{
	    PREPARE,  BATTLE;
	}
	  
	private class FinishTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			int finishDelay = 33 - _taskTemp;
			switch(finishDelay)
			{
				case 33:
					calcWinner();
					showResultBoard();
					spawnRewardBoxes();
					break;
				case 30:
				case 20:
				case 10:
				case 9:
				case 8:
				case 7:
				case 6:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					broadcastPacket(new SystemMessagePacket(SystemMsg.IN_S1_SECONDS_YOU_WILL_BE_MOVED_TO_WHERE_YOU_WERE_BEFORE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS).addInteger(finishDelay), true, true);
					break;
				case 0:
					finishBattle();
					break;
			}
			_taskTemp++;
		}
	}

	private static final long serialVersionUID = 1L;

	private final ChaosFestivalEvent _event;
	private final int _id;
	private final boolean _pvp;
	private final Reflection _reflection;
	private final IntObjectMap<ChaosFestivalPlayerObject> _members = new CHashIntObjectMap<ChaosFestivalPlayerObject>();

	private AtomicBoolean _isBattleBegin = new AtomicBoolean(false);

	private ScheduledFuture<?> _finishTask = null;
	private int _taskTemp = 0;

	private ChaosFestivalPlayerObject _winner = null;
	private BattleState _state = BattleState.PREPARE;
	private int _joinedMembersCount = 0;
	
	public ChaosFestivalArenaObject(ChaosFestivalEvent event, int id, boolean pvp, int instanceId)
	{
		_event = event;
		_id = id;
		_pvp = pvp;

		_reflection = new Reflection();
		_reflection.init(InstantZoneHolder.getInstance().getInstantZone(instanceId));
	}

	public ChaosFestivalEvent getEvent()
	{
		return _event;
	}

	public int getId()
	{
		return _id;
	}

	public boolean isPvP()
	{
		return _pvp;
	}

	public BattleState getBattleState()
	{
	  return _state;
	}
	
	public Reflection getReflection()
	{
		return _reflection;
	}
	
    @Override
	public Location getObserverEnterPoint(Player player)
	{
	  List<Location> teleportCoords = _reflection.getInstancedZone().getTeleportCoords();
	  return (Location)Rnd.get(teleportCoords);
	}
    
    @Override
	public boolean showObservableArenasList(Player player)
	{
	  if(!this._event.isInProgress())
	  {
	    player.sendPacket(SystemMsg.THE_CEREMONY_OF_CHAOS_IS_NOT_CURRENTLY_OPEN);
	    return false;
	  }
	  player.sendPacket(new ExCuriousHouseObserveList(getId()));
	  return true;
	}
	
    @Override
	public void onAppearObserver(ObservePoint observer)
	{
	  broadcastInfo(observer.getPlayer(), true);
	}
	
    @Override
	public void onEnterObserverArena(Player player)
	{
	  player.sendPacket(ExCuriousHouseObserveMode.ENTER);
	}
    
    @Override
	public void onExitObserverArena(Player player)
	{
	  player.sendPacket(ExCuriousHouseObserveMode.LEAVE);
	  player.stopInvisible(_event, true);
	}
	
	public void addMember(ChaosFestivalPlayerObject member)
	{
		member.setId(_members.size());
		_members.put(member.getId(), member);
	    _joinedMembersCount ++;
	}

	public ChaosFestivalPlayerObject getMember(Player player)
	{
		for(ChaosFestivalPlayerObject member : getMembers())
		{
			if(member.getPlayer() == player)
				return member;
		}
		return null;
	}

	public Collection<ChaosFestivalPlayerObject> getMembers()
	{
		return _members.values();
	}

    public int getJoinedMembersCount()
    {
        return _joinedMembersCount;
    }

	public boolean removeMember(ChaosFestivalPlayerObject member)
	{
		if(_winner == member)
			_winner = null;

		return _members.remove(member.getId()) != null;
	}

	public ChaosFestivalPlayerObject getWinner()
	{
		return _winner;
	}

    public void broadcastInfo(Player receiver, boolean onlyToObservers)
    {
        if(receiver != null)
            receiver.sendPacket(new ExCuriousHouseMemberList(this));
        else
            broadcastPacket(new ExCuriousHouseMemberList(this), !onlyToObservers, true);
    }

	public void broadcastPacket(IBroadcastPacket packet, boolean toParticipants, boolean toObservers)
	{
		if(toParticipants)
		{
            for(ChaosFestivalPlayerObject member : getMembers())
            {
                Player player = member.getPlayer();
                if(player == null || player.isInObserverMode())
                	continue;

                player.sendPacket(packet);
            }
        }
        if (toObservers)
        {
            for(ObservePoint observer : getObservers())
                observer.sendPacket(packet);
        }
	}

	public void teleportPlayers()
	{
		if(_members.size() <= 1)
			return;

		for(ChaosFestivalPlayerObject member : getMembers())
			member.teleportPlayer(_reflection);
		
		broadcastInfo(null, false);
	}

	private void startFinishTask()
	{
		_taskTemp = 0;
		_finishTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FinishTask(), 0L, 1000L);
	}

	private void stopFinishTask()
	{
		_taskTemp = 0;
		if(_finishTask != null)
		{
			_finishTask.cancel(false);
			_finishTask = null;
		}
	}

	public boolean isBattleBegin()
	{
		return _isBattleBegin.get();
	}

	public void startBattle()
	{
		if(!_isBattleBegin.compareAndSet(false, true))
			return;
		
		_state = BattleState.BATTLE;
		for(ChaosFestivalPlayerObject member : getMembers())
			member.onStartBattle();
	}

	public void stopBattle()
	{
		if(!_isBattleBegin.compareAndSet(true, false))
			return;

		startFinishTask();

		for(ChaosFestivalPlayerObject member : getMembers())
			member.onStopBattle();
	}

	private void breakBattle()
	{
		clearObservers();
		removeArenaFromEvent();
		stopFinishTask();
	}

	private void finishBattle()
	{
		breakBattle();

		for(ChaosFestivalPlayerObject member : getMembers())
			member.onFinishBattle(this);
		_members.clear();
	}

	public boolean isAllKilled(ChaosFestivalPlayerObject exception)
	{
		for(ChaosFestivalPlayerObject member : getMembers())
		{
			if(member == exception)
				continue;

			if(member.getPlayer() != null && !member.isKilled())
				return false;
		}
		return true;
	}

	public void broadcastStatusUpdate(Player player)
	{
		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		broadcastPacket(new ExCuriousHouseMemberUpdate(player), true, true);
	}

	public void onDamage(Creature attacker, Player target, double damage)
	{
		ChaosFestivalPlayerObject targetMember = getMember(target);
		if(targetMember == null)
			return;

		ChaosFestivalPlayerObject attackerMember = getMember(attacker.getPlayer());
		if(attackerMember == null)
			return;

		attackerMember.onDamage(damage);
	}

	public void onDeath(Player player, Creature killer)
	{
		ChaosFestivalPlayerObject victimMember = getMember(player);
		if(victimMember == null)
			return;

		ChaosFestivalPlayerObject killerMember = getMember(killer.getPlayer());
		victimMember.onDeath(this, killerMember);

		if(isAllKilled(killerMember))
			stopBattle();
	}

	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		if(reflection == _reflection)
			return;

		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onTeleport(this, x, y, z, reflection);
	}

	public void onExit(Player player)
	{
		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onExit(this);
	}

    public void onLeave(Player player)
    {
        ChaosFestivalPlayerObject member = getMember(player);
        if (member == null)
            return;
        
        member.onLeave(this);
        removeMember(member);
        if(_members.size() == 1)
            stopBattle();
        else if(_members.size() == 0)
        {
            breakBattle();
            return;
        }
        broadcastInfo(null, false);
    }

	private void removeArenaFromEvent()
	{
		_event.removeArena(this);
	}

	private void calcWinner()
	{
		if(this._members.isEmpty())
		      return;
		
		List<ChaosFestivalPlayerObject> members = new ArrayList<ChaosFestivalPlayerObject>(getMembers());
		Collections.sort(members, new ChaosFestivalPlayerObject.WinnerComparator());

		ChaosFestivalPlayerObject winner = members.get(0);
		if(winner.getKills() > 0)
			_winner = winner;
	}

	private void showResultBoard()
	{
		ExCuriousHouseResult.ResultState state = _winner == null ? ExCuriousHouseResult.ResultState.TIE : ExCuriousHouseResult.ResultState.LOSE;
	    
		Collection<ChaosFestivalPlayerObject> members = getMembers();

		for(ChaosFestivalPlayerObject member : members)
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;
			
			IBroadcastPacket message;
			if(_winner != null)
				message = new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(new CustomMessage("chaos_festival.player").addNumber(_winner.getId()).toString(player));
			else
				message = SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE;
			
            player.sendPacket(message);
            player.sendPacket(new ExCuriousHouseResult(this, player.getObjectId(), _winner == member ? ExCuriousHouseResult.ResultState.WIN : state, members));
		}
		for(ObservePoint observer : getObservers())
		      observer.sendPacket(new ExCuriousHouseResult(this, observer.getPlayer().getObjectId(), state, members));
	}

	private void spawnRewardBoxes()
	{
		if(_winner == null)
			return;
		
		if (_members.isEmpty())
		      return;
		
		Player player = _winner.getPlayer();
		if(player == null)
			return;

		Territory territory = new Territory();
		territory.add(new Circle(player.getX(), player.getY(), 150).setZmin(player.getZ() - 50).setZmax(player.getZ() + 50));

		int boxesCount = Rnd.get(_event.getRewardBoxSpawnMinCount(), _event.getRewardBoxSpawnMaxCount());
		if(boxesCount <= 0)
			return;

		for(int i = 0; i < boxesCount; i++)
		{
			NpcUtils.spawnSingle(_event.getRewardBoxId(), territory.getRandomLoc(_reflection.getGeoIndex()), _reflection);
		}
	}
}