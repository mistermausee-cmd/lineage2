package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ChaosFestivalManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseEnter;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseLeave;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseState;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;


public final class ChaosFestivalPlayerObject implements Serializable
{
	public static class WinnerComparator implements Comparator<ChaosFestivalPlayerObject>
	{
		@Override
		public int compare(ChaosFestivalPlayerObject o1, ChaosFestivalPlayerObject o2)
		{
			if(o1.getKills() == o2.getKills())
				return (int) (o2.getDamage() - o1.getDamage());
			return o2.getKills() - o1.getKills();
		}
	}

	public static class LevelComparator implements Comparator<ChaosFestivalPlayerObject>
	{
		@Override
		public int compare(ChaosFestivalPlayerObject o1, ChaosFestivalPlayerObject o2)
		{
			return o2.getPlayer().getLevel() - o1.getPlayer().getLevel();
		}
	}

	private static final long serialVersionUID = 1L;

	private final ChaosFestivalEvent _event;
	
	private Player _player;

	private int _objectId;
	private int _activeClassId;
	private int _maxHp;
	private int _maxCp;
	private double _currentHp;
	private double _currentCp;

	private int _id = 0;
	private int _kills = 0;
	private int _lifeTime = 0;
	private double _damage = 0;

	private boolean _killed = false;

	private Location _returnLoc = null;

	public ChaosFestivalPlayerObject(ChaosFestivalEvent event, Player player)
	{
		_event = event;
		setPlayer(player);
	}

	public int getObjectId()
	{
		if(_player != null)
            _objectId = _player.getObjectId();
        
        return _objectId;
	}

	public int getActiveClassId()
	{
		if(_player != null)
            _activeClassId = _player.getActiveClassId();
        
        return _activeClassId;
	}

	public int getMaxHp()
	{
		if(_player != null)
            _maxHp = _player.getMaxHp();
        
        return _maxHp;
	}

	public int getMaxCp()
	{
		if(_player != null)
            _maxCp = _player.getMaxCp();
        
        return _maxCp;
	}

	public int getCurrentHp()
	{
		if(_player != null)
            _currentHp = _player.getCurrentHp();
        
        return (int) _currentHp;
	}

	public int getCurrentCp()
	{
		if(_player != null)
            _currentCp = _player.getCurrentCp();
        
        return (int) _currentCp;
	}

	public Player getPlayer()
	{
		return _player;
	}

    public void setPlayer(Player player)
    {
        _player = player;
    }

	public void setId(int val)
	{
		_id = val;
	}

	public int getId()
	{
		return _id;
	}

	public void setKills(int val)
	{
		_kills = val;
	}

	public int getKills()
	{
		return _kills;
	}

	public void setLifeTime(int val)
	{
		_lifeTime = val;
	}

	public int getLifeTime()
	{
		return _lifeTime;
	}

	public void setDamage(double val)
	{
		_damage = val;
	}

	public double getDamage()
	{
		return _damage;
	}

	public void setKilled(boolean val)
	{
		_killed = val;
	}

	public boolean isKilled()
	{
		return _killed;
	}

	public void teleportPlayer(Reflection reflection)
	{
		Player player = getPlayer();

		if(player == null)
			return;

		if(player.isTeleporting())
		{
			setPlayer(null);
			return;
		}

		player.addEvent(_event);

		if(player.isInObserverMode())
			player.leaveObserverMode();

		
		if(player.getClan() != null)
			player.getClan().disableSkills(player);

		
		player.activateHeroSkills(false);

		
		if(player.isCastingNow())
			player.abortCast(true, true);

		
		if(player.isAttackingNow())
			player.abortAttack(true, true);

		
		for(Abnormal abnormal : player.getAbnormalList())
		{
			if(!player.isSpecialAbnormal(abnormal.getSkill()))
				abnormal.exit();
		}

		for(Cubic cubic : player.getCubics())
		{
            if(player.getSkillLevel(cubic.getSkill().getId()) <= 0)
            	cubic.delete();
        }

		
		for(Servitor servitor : player.getServitors())
		{
			if(servitor.isPet())
				servitor.unSummon(false);
			else
			{
				servitor.getAbnormalList().stopAll();
				servitor.transferOwnerBuffs();
			}
		}

		
		if(player.getAgathionId() > 0)
			player.setAgathion(0);

		
		for(TimeStamp sts : player.getSkillReuses())
		{
			if(sts == null)
				continue;

			Skill skill = SkillHolder.getInstance().getSkill(sts.getId(), sts.getLevel());
			if(skill == null)
				continue;

			if(sts.getReuseBasic() <= 15 * 60001L)
				player.enableSkill(skill);
		}

		
		player.sendSkillList();

		
		player.getInventory().validateItems();

		
		player.removeAutoShots(true);

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new RevivePacket(player));
			
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		player.broadcastUserInfo(true);

		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		_returnLoc = player.getStablePoint() == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player.getStablePoint();

		if(player.isSitting())
			player.standUp();

		player.setTarget(null);

		player.leaveParty();
		player.startInvisible(_event, true);

		player.setStablePoint(_returnLoc);

		ItemFunctions.addItem(player, 35991, 1, true);
	    ItemFunctions.addItem(player, 35992, 1, true);
	    ItemFunctions.addItem(player, 35993, 1, true);

		List<Location> teleportCoords = reflection.getInstancedZone().getTeleportCoords();
		Location loc = teleportCoords.get(Math.min(getId(), teleportCoords.size() - 1));
		player.teleToLocation(Location.findPointToStay(loc, 0, reflection.getGeoIndex()), reflection);

        player.getFlags().getImmobilized().start(_event);
        player.getFlags().getInvulnerable().start(_event);

		SkillEntry chaosEnergySkill = SkillHolder.getInstance().getSkillEntry(7115, 1);
        if(chaosEnergySkill != null)
            chaosEnergySkill.getEffects(player, player);

		player.sendPacket(ExCuriousHouseState.IDLE);
		player.sendPacket(ExCuriousHouseEnter.STATIC);

		Functions.show("chaos_festival/rules.htm", player);
	}

	public void onStartBattle()
	{
		Player player = this.getPlayer();

		if(player == null)
			return;

		player.getFlags().getImmobilized().stop(_event);
	    player.getFlags().getInvulnerable().stop(_event);

		player.stopInvisible(_event, true);
	}

	public void onStopBattle()
	{
		if(!isKilled())
			setLifeTime((int) (System.currentTimeMillis() / 1000L) - _event.getBattleStartTime());
	}

    public void onFinishBattle(ChaosFestivalArenaObject arena)
    {
        returnPlayer(arena, false, false);
    }

	public void returnPlayer(ChaosFestivalArenaObject arena, boolean observe, boolean leave)
	{
        Player player = getPlayer();

		if(player == null)
			return;
		boolean winner = arena.getWinner() == this;
		if(!leave)
		{
			if(!winner)
			{
                ItemFunctions.addItem(player, 45584, 4, true);
                ChaosFestivalManager.getInstance().addPoints(player.getObjectId(), 4);
            }
			else
			{
                int marksCount = Rnd.get(1, 50) + 4;
                ItemFunctions.addItem(player, 45584, marksCount, true);
                ItemFunctions.addItem(player, 36333, Rnd.get(1, 5), true);
                ChaosFestivalManager.getInstance().addPoints(player.getObjectId(), marksCount);
            }
		}
		
		onReturnPlayer(observe);
        if(observe)
        {
            player.startInvisible(_event, true);
            player.enterArenaObserverMode(arena);
        }
        player.teleToLocation(_returnLoc, ReflectionManager.MAIN);
        if(!leave)
            player.getListeners().onChaosFestivalFinishBattle(winner);
	}

    public void onReturnPlayer(boolean observe)
    {
        Player player = getPlayer();
        if(player == null)
            return;
        
        player.removeEvent(_event);
        if(player.isDead())
        {
            player.setCurrentHp(player.getMaxHp(), true);
            player.broadcastPacket(new RevivePacket(player));
        }
        else
            player.setCurrentHp(player.getMaxHp(), false);
        
        player.setCurrentCp(player.getMaxCp());
        player.setCurrentMp(player.getMaxMp());
        
        if(player.getClan() != null && player.getClan().getReputationScore() >= 0)
            player.getClan().enableSkills(player);
        
        player.activateHeroSkills(true);
        player.sendSkillList();
        player.getAbnormalList().stop(7115);
        
        ItemFunctions.deleteItem(player, 35991, ItemFunctions.getItemCount(player, 35991), false);
        ItemFunctions.deleteItem(player, 35992, ItemFunctions.getItemCount(player, 35992), false);
        ItemFunctions.deleteItem(player, 35993, ItemFunctions.getItemCount(player, 35993), false);
        
        player.getFlags().getImmobilized().stop(_event);
        player.getFlags().getInvulnerable().stop(_event);
        
        if(!observe)
            player.stopInvisible((Object)this._event, true);
        
        player.sendPacket(ExCuriousHouseLeave.STATIC);
        player.setStablePoint(null);
    }

	public void onLeave(ChaosFestivalArenaObject arena)
	{
	    returnPlayer(arena, false, true);
	}
	  
	public void onDamage(double damage)
	{
		setDamage(getDamage() + damage);
	}

	public void onDeath(ChaosFestivalArenaObject arena, ChaosFestivalPlayerObject killer)
	{
		if(killer != null)
			killer.setKills(killer.getKills() + 1);

		setKilled(true);
		setLifeTime((int) (System.currentTimeMillis() / 1000L) - arena.getEvent().getBattleStartTime());
		
        Player player = this.getPlayer();
        if(player == null)
            return;
        
        for(Servitor servitor : player.getServitors())
            servitor.unSummon(false);
        
        returnPlayer(arena, true, false);
	}

	public void onTeleport(ChaosFestivalArenaObject arena, int x, int y, int z, Reflection reflection)
	{
		onReturnPlayer(false);
	}

	public void onExit(ChaosFestivalArenaObject arena)
	{
		returnPlayer(arena, false, true);
	}
}