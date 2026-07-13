package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.instancemanager.SoDManager;
import l2s.gameserver.instancemanager.SoIManager;


public class ExShowSeedMapInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeD(3);
		for(int i = 1; i <= 3; i++)
		{
			writeD(i);
			switch(i)
			{
				case 1: 
					if(SoDManager.isAttackStage())
						writeD(2771);
					else
						writeD(2772);
					break;
				case 2: 
					writeD(SoIManager.getCurrentStage() + 2765);
					break;
				case 3: 
					writeD(3301);
					break;
			}
		}
	}
}