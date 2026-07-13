package l2s.gameserver.network.l2.s2c;

import org.napile.primitive.maps.IntObjectMap;

import l2s.gameserver.model.Player;


public class ExReceiveShowPostFriend extends L2GameServerPacket
{
	private IntObjectMap<String> _list;

	public ExReceiveShowPostFriend(Player player)
	{
		_list = player.getPostFriends();
	}

	@Override
	public void writeImpl()
	{
		writeD(_list.size());
		for(String t : _list.values())
			writeS(t);
	}
}
