package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Manor;
import l2s.gameserver.templates.manor.SeedProduction;


public class ExShowSeedInfoPacket extends L2GameServerPacket
{
	private List<SeedProduction> _seeds;
	private int _manorId;

	public ExShowSeedInfoPacket(int manorId, List<SeedProduction> seeds)
	{
		_manorId = manorId;
		_seeds = seeds;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0);
		writeD(_manorId); 
		writeD(0);
		writeD(_seeds.size());
		for(SeedProduction seed : _seeds)
		{
			writeD(seed.getId()); 

			writeQ(seed.getCanProduce()); 
			writeQ(seed.getStartProduce()); 
			writeQ(seed.getPrice()); 
			writeD(Manor.getInstance().getSeedLevel(seed.getId())); 

			writeC(1); 
			writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 1)); 

			writeC(1); 
			writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 2)); 
		}
	}
}