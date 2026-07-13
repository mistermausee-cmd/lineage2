package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.instances.DoorInstance;


public class DoorInfo extends L2GameServerPacket
{
	private int obj_id, door_id, view_hp;

	
	public DoorInfo(DoorInstance door)
	{
		obj_id = door.getObjectId();
		door_id = door.getDoorId();
		view_hp = door.isHPVisible() ? 1 : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(obj_id);
		writeD(door_id);
		writeD(view_hp); 
	}
}