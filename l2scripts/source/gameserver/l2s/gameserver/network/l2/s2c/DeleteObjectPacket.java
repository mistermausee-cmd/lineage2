package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;


public class DeleteObjectPacket extends L2GameServerPacket
{
	private int _objectId;

	public DeleteObjectPacket(GameObject obj)
	{
		_objectId = obj.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getObjectId() == _objectId)
			return;

		writeD(_objectId);
		writeD(0x01); 
	}

	@Override
	public String getType()
	{
		return super.getType() + " " + GameObjectsStorage.findObject(_objectId) + " (" + _objectId + ")";
	}
}