package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.utils.Location;


public final class FightBattlePlayerObject implements Serializable, Comparable<FightBattlePlayerObject>
{
	private static final long serialVersionUID = 1L;

	private Player _player;

	private double _damage = 0.;
	private int _winCount = 0;
	private boolean _killed = false;

	private Location _returnLoc = null;

	public FightBattlePlayerObject(Player player)
	{
		_player = player;
	}

	public String getName()
	{
		if(_player == null)
			return "";

		return _player.getName();
	}

	public int getObjectId()
	{
		if(_player == null)
			return 0;

		return _player.getObjectId();
	}

	public Player getPlayer()
	{
		return _player;
	}

	public void setDamage(double val)
	{
		_damage = val;
	}

	public double getDamage()
	{
		return _damage;
	}

	public void setWinCount(int val)
	{
		_winCount = val;
	}

	public int getWinCount()
	{
		return _winCount;
	}

	public void setKilled(boolean val)
	{
		_killed = val;
	}

	public boolean isKilled()
	{
		return _killed;
	}

	@Override
	public int compareTo(FightBattlePlayerObject o)
	{
		return _player.getLevel() - o.getPlayer().getLevel();
	}

	public void teleportPlayer(FightBattleArenaObject arena)
	{
		Player player = _player;
		if(player == null)
			return;

		if(player.isTeleporting())
		{
			_player = null;
			return;
		}

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

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new RevivePacket(player));
			
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.broadcastUserInfo(true);

		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		_returnLoc = player.getStablePoint() == null ? player.getLoc() : player.getStablePoint();

		if(player.isSitting())
			player.standUp();

		player.setTarget(null);

		player.leaveParty();

		player.setStablePoint(_returnLoc);

		Location loc = arena.getMember1() == this ? arena.getInfo().getTeleportLoc1() : arena.getInfo().getTeleportLoc2();
		player.teleToLocation(Location.findPointToStay(loc, 0, arena.getReflection().getGeoIndex()), arena.getReflection());

		setDamage(0.);
		setKilled(false);
	}

	public void onStopEvent(FightBattleEvent event)
	{
		Player player = _player;
		if(player == null)
			return;

		if(_returnLoc == null) 
			return;

		player.removeEvent(event);

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

		player.setStablePoint(null);
		player.teleToLocation(_returnLoc, ReflectionManager.MAIN);
	}

	public void onDamage(double damage)
	{
		setDamage(getDamage() + damage);
	}

	public void onKill(FightBattleEvent event, FightBattlePlayerObject killer)
	{
		setKilled(true);
	}

	public void onTeleport(FightBattleEvent event, Player player, int x, int y, int z, Reflection reflection)
	{
		onExit(event);
	}

	public void onExit(FightBattleEvent event)
	{
		Player player = _player;
		if(player == null)
			return;

		player.removeEvent(event);

		if(player.isDead())
			player.setCurrentHp(player.getMaxHp(), true);
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
	}
}
