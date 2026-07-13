package l2s.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.clanhall.OtherClanHall;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;


public class ClanHallMiniGameEvent extends SiegeEvent<OtherClanHall, CMGSiegeClanObject>
{
	public static final String NEXT_STEP = "next_step";
	public static final String REFUND = "refund";

	private boolean _arenaClosed = true;

	public ClanHallMiniGameEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void startEvent()
	{
		_oldOwner = getResidence().getOwner();
		if(_oldOwner != null)
			addObject("attackers", new CMGSiegeClanObject("attackers", _oldOwner, Long.MAX_VALUE));

		List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
		if(siegeClans.size() < 2)
		{
			addToRefund((CMGSiegeClanObject)CollectionUtils.safeGet(siegeClans, 0));
			
			siegeClans.clear();

			broadcastTo(SystemMsg.THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED, ATTACKERS);
			broadcastInZone2(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));
			reCalcNextTime(false);
			return;
		}

		CMGSiegeClanObject[] clans = siegeClans.toArray(new CMGSiegeClanObject[siegeClans.size()]);
		Arrays.sort(clans, SiegeClanObject.SiegeClanComparatorImpl.getInstance());

		List<CMGSiegeClanObject> temp = new ArrayList<CMGSiegeClanObject>(4);

		for(int i = 0; i < clans.length; i++)
		{
			CMGSiegeClanObject siegeClan = clans[i];

			if(temp.size() == 4)
			{
				siegeClans.remove(siegeClan);

				siegeClan.broadcast(SystemMsg.YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR);
				
				addToRefund(siegeClan);
			}
			else
			{
				temp.add(siegeClan);

				siegeClan.broadcast(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR);

				SiegeClanDAO.getInstance().delete(getResidence(), siegeClan);
			}
		}

		_arenaClosed = false;

		super.startEvent();
	}

    private void addToRefund(CMGSiegeClanObject siegeClan)
    {
        if(siegeClan == null || siegeClan.getClan() == this._oldOwner)
            return;

        CMGSiegeClanObject oldSiegeClan = getSiegeClan("refund", siegeClan.getObjectId());
        if(oldSiegeClan != null)
        {
            SiegeClanDAO.getInstance().delete(getResidence(), siegeClan);
            
            oldSiegeClan.setParam(oldSiegeClan.getParam() + siegeClan.getParam() / 2);
            
            SiegeClanDAO.getInstance().update(getResidence(), oldSiegeClan);
        }
        else
        {
            siegeClan.setType("refund");
            siegeClan.setParam(siegeClan.getParam() / 2);
            
            this.addObject("refund", siegeClan);
            
            SiegeClanDAO.getInstance().update(getResidence(), siegeClan);
        }
    }
    
	@Override
	public void stopEvent(boolean force)
	{
		removeBanishItems();

		Clan newOwner = getResidence().getOwner();
		if(newOwner != null)
		{
			if(_oldOwner != newOwner)
			{
				newOwner.broadcastToOnlineMembers(PlaySoundPacket.SIEGE_VICTORY);

				newOwner.incReputation(1700, false, toString());
			}

			broadcastTo(new SystemMessagePacket(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
			broadcastTo(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);
		}
		else
			broadcastTo(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()), ATTACKERS);

		updateParticles(false, ATTACKERS);

		removeObjects(ATTACKERS);

		super.stopEvent(force);

		_oldOwner = null;
	}

	public void nextStep()
	{
		List<CMGSiegeClanObject> siegeClans = getObjects(ATTACKERS);
		for(int i = 0; i < siegeClans.size(); i++)
			spawnAction("arena_" + i, true);

		_arenaClosed = true;

		updateParticles(true, ATTACKERS);

		broadcastTo(new SystemMessagePacket(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS);
	}

	@Override
	public void removeState(int val)
	{
		super.removeState(val);

		if(val == REGISTRATION_STATE)
			broadcastTo(SystemMsg.THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED, ATTACKERS);
	}

	@Override
	public CMGSiegeClanObject newSiegeClan(String type, int clanId, long param, long date)
	{
		Clan clan = ClanTable.getInstance().getClan(clanId);
		return clan == null ? null : new CMGSiegeClanObject(type, clan, param, date);
	}

	@Override
	public void announce(SystemMsg msgId, int val, int time)
	{
		int seconds = val % 60;
		int min = val / 60;
		if(min > 0)
		{
			SystemMsg msg = min > 10 ? SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA : SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW;

			broadcastTo(new SystemMessagePacket(msg).addInteger(min), ATTACKERS);
		}
		else
			broadcastTo(new SystemMessagePacket(SystemMsg.IN_S1_SECONDS_THE_GAME_WILL_BEGIN).addInteger(seconds), ATTACKERS);
	}

	@Override
	public void processStep(Clan clan)
	{
		if(clan != null)
			getResidence().changeOwner(clan);

		stopEvent(true);
	}

	@Override
	public void loadSiegeClans()
	{
		addObjects(ATTACKERS, SiegeClanDAO.getInstance().load(getResidence(), ATTACKERS));
		addObjects(REFUND, SiegeClanDAO.getInstance().load(getResidence(), REFUND));
	}

	@Override
	public void action(String name, boolean start)
	{
		if(name.equalsIgnoreCase(NEXT_STEP))
			nextStep();
		else
			super.action(name, start);
	}

	@Override
	public int getUserRelation(Player thisPlayer, int result)
	{
		return result;
	}

	@Override
	public int getRelation(Player thisPlayer, Player targetPlayer, int result)
	{
		return result;
	}

	public boolean isArenaClosed()
	{
		return _arenaClosed;
	}

	@Override
	public void onAddEvent(GameObject object)
	{
		if(object.isItem())
			addBanishItem((ItemInstance) object);
	}
}