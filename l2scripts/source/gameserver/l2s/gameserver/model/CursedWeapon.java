package l2s.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill.AddedSkill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import l2s.gameserver.network.l2.s2c.ExRedSkyPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

public class CursedWeapon
{
	private final String _name;

	private final int _itemId, _skillMaxLevel;
	private final int _skillId;
	private int _dropRate, _disapearChance;
	private int _durationMin, _durationMax, _durationLost;
	private int _transformId;
	private int _stageKills, _nbKills = 0, _playerKarma = 0, _playerPkKills = 0;

	private CursedWeaponState _state = CursedWeaponState.NONE;
	private Location _loc = null;
	private long _endTime = 0;
	private ItemInstance _item = null;
	private int _playerObjectId = 0;
	private Player _player = null;

	public enum CursedWeaponState
	{
		NONE,
		ACTIVATED,
		DROPPED,
	}

	public CursedWeapon(int itemId, int skillId, String name)
	{
		_name = name;
		_itemId = itemId;
		_skillId = skillId;
		_skillMaxLevel = SkillHolder.getInstance().getSkill(_skillId, 1).getMaxLevel();
	}

	public void initWeapon()
	{
		zeroOwner();
		setState(CursedWeaponState.NONE);
		_endTime = 0;
		_item = null;
		_nbKills = 0;
	}

	
	public void create(NpcInstance attackable, Player killer)
	{
		_item = ItemFunctions.createItem(_itemId);
		if(_item != null)
		{
			zeroOwner();
			setState(CursedWeaponState.DROPPED);

			if(_endTime == 0)
				_endTime = System.currentTimeMillis() + getRndDuration() * 60000;

			_item.dropToTheGround(attackable, Location.findPointToStay(attackable, 100));
			_loc = _item.getLoc();
			_item.setDropTime(0);

			
			L2GameServerPacket redSky = new ExRedSkyPacket(10);
			L2GameServerPacket eq = new EarthQuakePacket(killer.getLoc(), 30, 12);
			for(Player player : GameObjectsStorage.getPlayers())
				player.sendPacket(redSky, eq);
		}
	}

	
	public boolean dropIt(NpcInstance attackable, Player killer, Player owner)
	{
		if(Rnd.chance(_disapearChance))
			return false;

		Player player = getPlayer();
		if(player == null)
		{
			if(owner == null)
				return false;
			player = owner;
		}

		ItemInstance oldItem;
		if((oldItem = player.getInventory().removeItemByItemId(_itemId, 1L)) == null)
			return false;

		player.setKarma(_playerKarma);
		player.setPkKills(_playerPkKills);
		player.setCursedWeaponEquippedId(0);
		player.setTransform(null);
		player.validateLocation(0);

		SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, player.getSkillLevel(_skillId));
		if(skillEntry != null)
		{
			for(AddedSkill s : skillEntry.getTemplate().getAddedSkills())
				player.removeSkillById(s.id);
		}

		player.removeSkillById(_skillId);

		player.abortAttack(true, false);

		zeroOwner();
		setState(CursedWeaponState.DROPPED);

		oldItem.dropToTheGround(player, Location.findPointToStay(player, 100));
		_loc = oldItem.getLoc();

		oldItem.setDropTime(0);
		_item = oldItem;

		player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(oldItem.getItemId()));
		player.broadcastUserInfo(true);
		player.broadcastPacket(new EarthQuakePacket(player.getLoc(), 30, 12));

		return true;
	}

	private void giveSkill(Player player)
	{
		for(SkillEntry skillEntry : getSkills(player))
		{
			player.addSkill(skillEntry, false);
			player.addTransformSkill(skillEntry);
		}
		player.sendSkillList();
	}

	private Collection<SkillEntry> getSkills(Player player)
	{
		int level = 1 + _nbKills / _stageKills;
		if(level > _skillMaxLevel)
			level = _skillMaxLevel;

		SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(_skillId, level);
		List<SkillEntry> ret = new ArrayList<SkillEntry>();
		ret.add(skillEntry);
		for(AddedSkill s : skillEntry.getTemplate().getAddedSkills())
			ret.add(SkillHolder.getInstance().getSkillEntry(s.id, s.level));
		return ret;
	}

	
	public boolean reActivate()
	{
		if(getTimeLeft() <= 0)
		{
			if(getPlayerId() != 0) 
				setState(CursedWeaponState.ACTIVATED);
			return false;
		}

		if(getPlayerId() == 0)
		{
			if(_loc == null || (_item = ItemFunctions.createItem(_itemId)) == null)
				return false;

			_item.dropMe(null, _loc);
			_item.setDropTime(0);

			setState(CursedWeaponState.DROPPED);
		}
		else
			setState(CursedWeaponState.ACTIVATED);
		return true;
	}

	public void activate(Player player, ItemInstance item, boolean onRestore)
	{
		if(isDropped() || getPlayerId() != player.getObjectId()) 
		{
			setPlayerId(player.getObjectId());
			setPlayerKarma(player.getKarma());
			setPlayerPkKills(player.getPkKills());
		}

		setPlayer(player);
		setState(CursedWeaponState.ACTIVATED);

		player.leaveParty();
		if(player.isMounted())
			player.setMount(null);

		_item = item;

		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
		player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, _item);

		player.setTransform(null);
		player.setCursedWeaponEquippedId(_itemId);
		player.setTransform(_transformId);
		player.setKarma(-9999999);
		player.setPkKills(_nbKills);

		if(_endTime == 0)
			_endTime = System.currentTimeMillis() + getRndDuration() * 60000;

		giveSkill(player);

		if(!onRestore)
		{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(_item.getItemId()));
			player.broadcastUserInfo(true);
		}
	}

	public void increaseKills()
	{
		Player player = getPlayer();
		if(player == null)
			return;

		_nbKills++;
		player.setPkKills(_nbKills);
		player.updateStats();
		if(_nbKills % _stageKills == 0 && _nbKills <= _stageKills * (_skillMaxLevel - 1))
			giveSkill(player);
		_endTime -= _durationLost * 60000; 
	}

	public void setDisapearChance(int disapearChance)
	{
		_disapearChance = disapearChance;
	}

	public void setDropRate(int dropRate)
	{
		_dropRate = dropRate;
	}

	public void setDurationMin(int duration)
	{
		_durationMin = duration;
	}

	public void setDurationMax(int duration)
	{
		_durationMax = duration;
	}

	public void setDurationLost(int durationLost)
	{
		_durationLost = durationLost;
	}

	public void setStageKills(int stageKills)
	{
		_stageKills = stageKills;
	}

	public void setTransformId(int transformationId)
	{
		_transformId = transformationId;
	}

	public int getTransformId()
	{
		return _transformId;
	}

	public void setNbKills(int nbKills)
	{
		_nbKills = nbKills;
	}

	public void setPlayerId(int playerId)
	{
		_playerObjectId = playerId;
	}

	public void setPlayerKarma(int playerKarma)
	{
		_playerKarma = playerKarma;
	}

	public void setPlayerPkKills(int playerPkKills)
	{
		_playerPkKills = playerPkKills;
	}

	public void setState(CursedWeaponState state)
	{
		_state = state;
	}

	public void setEndTime(long endTime)
	{
		_endTime = endTime;
	}

	public void setPlayer(Player player)
	{
		_player = player;
	}

	private void zeroOwner()
	{
		_player = null;
		_playerObjectId = 0;
		_playerKarma = 0;
		_playerPkKills = 0;
	}

	public void setItem(ItemInstance item)
	{
		_item = item;
	}

	public void setLoc(Location loc)
	{
		_loc = loc;
	}

	public CursedWeaponState getState()
	{
		return _state;
	}

	public boolean isActivated()
	{
		return getState() == CursedWeaponState.ACTIVATED;
	}

	public boolean isDropped()
	{
		return getState() == CursedWeaponState.DROPPED;
	}

	public long getEndTime()
	{
		return _endTime;
	}

	public String getName()
	{
		return _name;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public ItemInstance getItem()
	{
		return _item;
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getDropRate()
	{
		return _dropRate;
	}

	public int getPlayerId()
	{
		return _playerObjectId;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public int getPlayerKarma()
	{
		return _playerKarma;
	}

	public int getPlayerPkKills()
	{
		return _playerPkKills;
	}

	public int getNbKills()
	{
		return _nbKills;
	}

	public int getStageKills()
	{
		return _stageKills;
	}

	
	public Location getLoc()
	{
		return _loc;
	}

	public int getRndDuration()
	{
		if(_durationMin > _durationMax)
			_durationMax = 2 * _durationMin;
		return Rnd.get(_durationMin, _durationMax);
	}

	public boolean isActive()
	{
		return isActivated() || isDropped();
	}

	public int getLevel()
	{
		return Math.min(1 + _nbKills / _stageKills, _skillMaxLevel);
	}

	public long getTimeLeft()
	{
		return _endTime - System.currentTimeMillis();
	}

	public Location getWorldPosition()
	{
		if(isActivated())
		{
			Player player = getPlayer();
			if(player != null)
				return player.getLoc();
		}
		else if(isDropped())
			if(_item != null)
				return _item.getLoc();

		return null;
	}
}