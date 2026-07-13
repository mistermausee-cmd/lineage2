package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.RecipeTemplate;


public class RecipeItemMakeInfoPacket extends L2GameServerPacket
{
	private final int _id;
	private final boolean _isCommon;
	private final int _status;
	private final int _curMP;
	private final int _maxMP;
	private final long _addAdenaRate;

	public RecipeItemMakeInfoPacket(Player player, RecipeTemplate recipe, int status)
	{
		_id = recipe.getId();
		_isCommon = recipe.isCommon();
		_status = status;
		_curMP = (int) player.getCurrentMp();
		_maxMP = player.getMaxMp();
		_addAdenaRate = recipe.getAddRateAdena();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_id); 
		writeD(_isCommon ? 0x01 : 0x00);
		writeD(_curMP);
		writeD(_maxMP);
		writeD(_status); 
		writeC(_addAdenaRate == 0 ? 0 : 1);	
	    if(_addAdenaRate > 0)
	    	writeQ(_addAdenaRate);
	}
}