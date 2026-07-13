package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Manor;


public class ExShowManorDefaultInfoPacket extends L2GameServerPacket
{
	private List<Integer> _crops = null;

	public ExShowManorDefaultInfoPacket()
	{
		_crops = Manor.getInstance().getAllCrops();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0);
		writeD(_crops.size());
		for(int cropId : _crops)
		{
			writeD(cropId); 
			writeD(Manor.getInstance().getSeedLevelByCrop(cropId)); 
			writeD(Manor.getInstance().getSeedBasicPriceByCrop(cropId)); 
			writeD(Manor.getInstance().getCropBasicPrice(cropId)); 
			writeC(1); 
			writeD(Manor.getInstance().getRewardItem(cropId, 1)); 
			writeC(1); 
			writeD(Manor.getInstance().getRewardItem(cropId, 2)); 
		}
	}
}