package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.instances.NpcInstance;

public class MonRaceInfoPacket extends L2GameServerPacket
{
	private int _unknown1;
	private int _unknown2;
	private NpcInstance[] _monsters;
	private int[][] _speeds;

	public MonRaceInfoPacket(int unknown1, int unknown2, NpcInstance[] monsters, int[][] speeds)
	{
		
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_unknown1);
		writeD(_unknown2);
		writeD(8);

		for(int i = 0; i < 8; i++)
		{
			
			writeD(_monsters[i].getObjectId()); 
			writeD(_monsters[i].getNpcId() + 1000000); 
			writeD(14107); 
			writeD(181875 + 58 * (7 - i)); 
			writeD(-3566); 
			writeD(12080); 
			writeD(181875 + 58 * (7 - i)); 
			writeD(-3566); 
			writeF(_monsters[i].getCollisionHeight()); 
			writeF(_monsters[i].getCollisionRadius()); 
			writeD(120); 
			for(int j = 0; j < 20; j++)
				writeC(_unknown1 == 0 ? _speeds[i][j] : 0);
			writeD(0);
			writeD(0x00); 
		}
	}
}