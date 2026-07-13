package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.entity.boat.Shuttle;
import l2s.gameserver.templates.ShuttleTemplate.ShuttleDoor;


public class ExShuttleInfoPacket extends L2GameServerPacket
{
    private final Shuttle _shuttle;
    private final Collection<ShuttleDoor> _doors;

    public ExShuttleInfoPacket(Shuttle shuttle)
    {
        _shuttle = shuttle;
        _doors = shuttle.getTemplate().getDoors();
    }

    @Override
    protected final void writeImpl()
    {
        writeD(_shuttle.getBoatId()); 
        writeD(_shuttle.getX()); 
        writeD(_shuttle.getY()); 
        writeD(_shuttle.getZ()); 
        writeD(0); 
        writeD(_shuttle.getBoatId()); 
        writeD(_doors.size()); 
        for(ShuttleDoor door : _doors)
        {
            int doorId = door.getId();
            writeD(doorId); 
            writeD(door.unkParam[0]); 
            writeD(door.unkParam[1]); 
            writeD(door.unkParam[2]); 
            writeD(door.unkParam[3]); 
            writeD(door.unkParam[4]); 
            writeD(door.unkParam[5]); 
            writeD(door.unkParam[6]); 
            writeD(door.unkParam[7]); 
            writeD(door.unkParam[8]); 
            boolean thisFloorDoor = _shuttle.getCurrentFloor().isThisFloorDoor(doorId);
            writeD(thisFloorDoor && _shuttle.isDocked());
            writeD(thisFloorDoor);
        }
    }
}