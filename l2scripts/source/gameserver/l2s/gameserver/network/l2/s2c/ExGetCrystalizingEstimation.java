package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.data.ChancedItemData;
import l2s.gameserver.templates.item.support.CrystallizationInfo;


public class ExGetCrystalizingEstimation extends L2GameServerPacket
{
	private List<ChancedItemData> _crysItems = new ArrayList<ChancedItemData>();

	public ExGetCrystalizingEstimation(ItemInstance item)
	{
		int crystalId = item.getGrade().getCrystalId();
		int crystalCount = item.getCrystalCountOnCrystallize();

	    if(crystalId > 0 && crystalCount > 0)
	    	_crysItems.add(new ChancedItemData(crystalId, crystalCount, 100.0)); 
	    CrystallizationInfo info = item.getTemplate().getCrystallizationInfo();
	    if(info != null)
	    	_crysItems.addAll(info.getItems());
	}

	@Override
	protected final void writeImpl()
	{
	    writeD(_crysItems.size());
	    for(ChancedItemData item : _crysItems)
	    {
	      writeD(item.getId());
	      writeQ(item.getCount());
	      writeF(item.getChance());
	    }
	}
}