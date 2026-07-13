package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2s.gameserver.network.l2.s2c.ExPVPMatchRecord;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
	    Player player = getClient().getActiveChar();
	    if(player == null)
	    	return;
	    
	    UndergroundColiseumBattleEvent battleEvent = player.getEvent(UndergroundColiseumBattleEvent.class);
	    if(battleEvent == null)
	    	return; 
	    
	    player.sendPacket(new ExPVPMatchRecord(1, TeamType.NONE, battleEvent));
	}
}