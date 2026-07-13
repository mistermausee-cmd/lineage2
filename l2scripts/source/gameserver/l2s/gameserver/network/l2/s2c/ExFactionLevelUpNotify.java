package l2s.gameserver.network.l2.s2c;

public class ExFactionLevelUpNotify extends L2GameServerPacket
{
	public final L2GameServerPacket STATIC;
    
    public ExFactionLevelUpNotify()
    {
        STATIC = new ExFactionLevelUpNotify();
    }
    
    protected final void writeImpl() {}
}