package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class ExMonsterBook extends L2GameServerPacket
{
	public ExMonsterBook(Player player)
	{
	}

    @Override
    protected final void writeImpl()
    {
        int count = 0;
        writeH(count);
        for(int i = 0; i < count; i++)
        {
        	writeC(0);
        	writeD(0);
        }
    }
}