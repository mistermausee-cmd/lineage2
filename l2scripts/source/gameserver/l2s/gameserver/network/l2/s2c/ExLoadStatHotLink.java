package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.worldstatistics.CharacterStatistic;

public class ExLoadStatHotLink extends L2GameServerPacket
{
	private final int categoryId;
	private final int subCatId;
	private List<CharacterStatistic> globalStatistic;
	private List<CharacterStatistic> monthlyStatistic;

	public ExLoadStatHotLink(int categoryId, int subCatId, List<CharacterStatistic> globalStatistic, List<CharacterStatistic> monthlyStatistic)
	{
		this.categoryId = categoryId;
		this.subCatId = subCatId;
		this.globalStatistic = globalStatistic;
		this.monthlyStatistic = monthlyStatistic;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(categoryId); 
		writeD(subCatId); 
		
		writeD(monthlyStatistic.size()); 
		for (int i = 0; i < monthlyStatistic.size(); i++)
		{
			CharacterStatistic statistic = monthlyStatistic.get(i);
			writeH(i + 1); 
			writeD(statistic.getObjId()); 
			writeS(statistic.getName()); 
			writeQ(statistic.getValue()); 
			writeH(0x00);
			writeD(0x00);
			writeD(0x00);
		}
		
		writeD(globalStatistic.size()); 
		for (int i = 0; i < globalStatistic.size(); i++)
		{
			CharacterStatistic statistic = globalStatistic.get(i);
			writeH(i + 1); 
			writeD(statistic.getObjId()); 
			writeS(statistic.getName()); 
			writeQ(statistic.getValue()); 
			writeH(0x00);
			writeD(0x00);
			writeD(0x00);
		}
	}
}