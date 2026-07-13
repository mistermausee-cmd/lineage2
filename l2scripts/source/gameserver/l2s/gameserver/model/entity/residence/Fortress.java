package l2s.gameserver.model.entity.residence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.FortressDAO;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.ItemTemplate;

public class Fortress extends Residence
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(Fortress.class);

	private static final long REMOVE_CYCLE = 7 * 24; 
	private static final long REWARD_CYCLE = 6; 

	public static final long CASTLE_FEE = 25000;
	
	public static final int DOMAIN = 0;
	public static final int BOUNDARY = 1;
	
	public static final int NOT_DECIDED = 0;
	public static final int INDEPENDENT = 1;
	public static final int CONTRACT_WITH_CASTLE = 2;
	
	public static final int REINFORCE = 0;
	public static final int GUARD_BUFF = 1;
	public static final int DOOR_UPGRADE = 2;
	public static final int DWARVENS = 3;
	public static final int SCOUT = 4;

	public static final int FACILITY_MAX = 5;
	private int[] _facilities = new int[FACILITY_MAX];
	
	private int _state;
	private int _castleId;

	private int _supplyCount;

	private int _rewardCount;

	private final List<Castle> _relatedCastles = new ArrayList<Castle>(5);

	public Fortress(StatsSet set)
	{
		super(set);
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.FORTRESS;
	}

	@Override
	public void cancelCycleTask()
	{
		_rewardCount = 0;
		super.cancelCycleTask();
	}

	@Override
	public void changeOwner(Clan clan)
	{
		
		if(clan != null)
		{
			if(clan.getHasFortress() != 0)
			{
				Fortress oldFortress = ResidenceHolder.getInstance().getResidence(Fortress.class, clan.getHasFortress());
				if(oldFortress != null)
					oldFortress.changeOwner(null);
			}
			if(clan.getCastle() != 0)
			{
				Castle oldCastle = ResidenceHolder.getInstance().getResidence(Castle.class, clan.getCastle());
				if(oldCastle != null)
					oldCastle.changeOwner(null);
			}
		}

		
		if(getOwnerId() > 0 && (clan == null || clan.getClanId() != getOwnerId()))
		{
			
			removeSkills();
			Clan oldOwner = getOwner();
			if(oldOwner != null)
				oldOwner.setHasFortress(0);

			cancelCycleTask();
			clearFacility();
		}

		
	    setOwner(clan);

		
		removeFunctions();
	    if(clan != null)
	    {
	      clan.setHasFortress(getId());
	      clan.broadcastClanStatus(true, false, false);
	    }
		
		rewardSkills();

		setFortState(NOT_DECIDED, 0);
		setJdbcState(JdbcEntityState.UPDATED);

		update();
	}

	@Override
	protected void loadData()
	{
		FortressDAO.getInstance().select(this);
	}

	public void setFortState(int state, int castleId)
	{
		_state = state;
		_castleId = castleId;
	}

	public int getCastleId()
	{
		return _castleId;
	}

	public int getContractState()
	{
		return _state;
	}

	@Override
	public void chanceCycle()
	{
		super.chanceCycle();
		if(getCycle() >= REMOVE_CYCLE)
		{
			getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
			changeOwner(null);
			return;
		}

		setPaidCycle(getPaidCycle() + 1);
		
		if(getPaidCycle() >= REWARD_CYCLE)
		{
			setPaidCycle(0);
			setRewardCount(getRewardCount() + 1);

			if(getContractState() == CONTRACT_WITH_CASTLE)
			{
				Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _castleId);
				if(castle.getOwner() == null || castle.getOwner().getReputationScore() < 2 || _owner.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) > CASTLE_FEE)
				{
					setSupplyCount(0);
					setFortState(INDEPENDENT, 0);
					clearFacility();
				}
				else
				{
					if(_supplyCount < 6)
					{
						castle.getOwner().incReputation(-2, false, "Fortress:chanceCycle():" + getId());
						_owner.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, CASTLE_FEE);
						_supplyCount++;
					}
				}
			}
		}
	}

	@Override
	public void update()
	{
		FortressDAO.getInstance().update(this);
	}

	public int getSupplyCount()
	{
		return _supplyCount;
	}

	public void setSupplyCount(int c)
	{
		_supplyCount = c;
	}

	public int getFacilityLevel(int type)
	{
		return _facilities[type];
	}

	public void setFacilityLevel(int type, int val)
	{
		_facilities[type] = val;
	}

	public void clearFacility()
	{
		for(int i = 0; i < _facilities.length; i++)
			_facilities[i] = 0;
	}

	public int[] getFacilities()
	{
		return _facilities;
	}

	public void addRelatedCastle(Castle castle)
	{
		_relatedCastles.add(castle);
	}

	public List<Castle> getRelatedCastles()
	{
		return _relatedCastles;
	}

	public int getRewardCount()
	{
		return _rewardCount;
	}

	public void setRewardCount(int rewardCount)
	{
		_rewardCount = rewardCount;
	}
}