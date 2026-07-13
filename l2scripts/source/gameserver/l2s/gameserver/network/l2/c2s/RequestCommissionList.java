package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.model.Player;


public class RequestCommissionList extends L2GameClientPacket
{
	public int _tree;
	public int _type;
	public int _quality;
	public int _grade;
	public String _searchWords;

	@Override
	protected void readImpl()
	{
		_tree = readD(); 
		_type = readD(); 
		_quality = readD(); 
		_grade = readD(); 
		_searchWords = readS(); 

	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		CommissionManager.getInstance().sendCommissionList(activeChar, _tree, _type, _quality, _grade, _searchWords);
	}
}