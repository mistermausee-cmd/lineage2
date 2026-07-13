package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.instancemanager.CastleManorManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.templates.manor.CropProcure;
import l2s.gameserver.utils.NpcUtils;


public class RequestSetCrop extends L2GameClientPacket
{
	private int _count, _manorId;

	private long[] _items; 

	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_count = readD();
		if(_count * 21 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new long[_count * 4];
		for(int i = 0; i < _count; i++)
		{
			_items[i * 4 + 0] = readD();
			_items[i * 4 + 1] = readQ();
			_items[i * 4 + 2] = readQ();
			_items[i * 4 + 3] = readC();
			if(_items[i * 4 + 0] < 1 || _items[i * 4 + 1] < 0 || _items[i * 4 + 2] < 0)
			{
				_count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _count == 0)
			return;

		if(activeChar.getClan() == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
		if(castle.getOwnerId() != activeChar.getClanId() 
				|| (activeChar.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) != Clan.CP_CS_MANOR_ADMIN) 
		{
			activeChar.sendActionFailed();
			return;
		}

	    NpcInstance chamberlain = NpcUtils.canPassPacket(activeChar, this);
	    if(chamberlain == null || chamberlain.getCastle() != castle)
	    {
	    	activeChar.sendActionFailed();
	    	return;
	    }
	    
		List<CropProcure> crops = new ArrayList<CropProcure>(_count);
		for(int i = 0; i < _count; i++)
		{
			int id = (int) _items[i * 4 + 0];
			long sales = _items[i * 4 + 1];
			long price = _items[i * 4 + 2];
			int type = (int) _items[i * 4 + 3];
			if(id > 0)
			{
				CropProcure s = CastleManorManager.getInstance().getNewCropProcure(id, sales, type, price, sales);
				crops.add(s);
			}
		}

		castle.setCropProcure(crops, CastleManorManager.PERIOD_NEXT);
		castle.saveCropData(CastleManorManager.PERIOD_NEXT);
	}
}