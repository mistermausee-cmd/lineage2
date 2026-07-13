package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.cache.CrestCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Friend;


public class ExFriendDetailInfo extends L2GameServerPacket
{
	private final int _objectId;
	private final Friend _friend;
	private final int _clanCrestId;
	private final int _allyCrestId;

	public ExFriendDetailInfo(Player player, Friend friend)
	{
		_objectId = player.getObjectId();
		_friend = friend;
		_clanCrestId = _friend.getClanId() > 0 ? CrestCache.getInstance().getPledgeCrestId(_friend.getClanId()) : 0;
		_allyCrestId = _friend.getAllyId() > 0 ? CrestCache.getInstance().getAllyCrestId(_friend.getAllyId()) : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId); 
		writeS(_friend.getName()); 
		writeD(_friend.isOnline()); 
		writeD(_friend.isOnline() ? _friend.getObjectId() : 0); 
		writeH(_friend.getLevel()); 
		writeH(_friend.getClassId()); 
		writeD(_friend.getClanId()); 
		writeD(_clanCrestId); 
		writeS(_friend.getClanName()); 
		writeD(_friend.getAllyId()); 
		writeD(_allyCrestId); 
		writeS(_friend.getAllyName()); 
		writeC(_friend.getCreationMonth() + 1); 
		writeC(_friend.getCreationDay()); 
		writeD(_friend.getLastAccessDelay()); 
		writeS(_friend.getMemo()); 
	}
}
