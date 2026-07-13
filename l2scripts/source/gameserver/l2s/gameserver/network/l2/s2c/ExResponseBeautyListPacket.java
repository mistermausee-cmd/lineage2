package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BeautyShopHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;
import l2s.gameserver.templates.beatyshop.BeautyStyleTemplate;
import l2s.gameserver.utils.ItemFunctions;
 
public class ExResponseBeautyListPacket extends L2GameServerPacket
{
	public static int HAIR_LIST = 0;
	public static int FACE_LIST = 1;

	private final int _type;
	private final long _adena;
	private final long _coins;
	private final BeautyStyleTemplate[] _styles;
 
	public ExResponseBeautyListPacket(Player player, int type)
	{
		_type = type;
		_adena = player.getAdena();
		_coins = ItemFunctions.getItemCount(player, Config.BEAUTY_SHOP_COIN_ITEM_ID);

		BeautyStyleTemplate[] styles = new BeautyStyleTemplate[0];

		BeautySetTemplate set = BeautyShopHolder.getInstance().getTemplate(player);
		if(set != null)
		{
			if(type == HAIR_LIST)
				styles = set.getHairs();
			else if(type == FACE_LIST)
				styles = set.getFaces();
		}

		_styles = styles;
	}
 
	@Override
	protected void writeImpl()
	{
		writeQ(_adena);
		writeQ(_coins);
		writeD(_type);
		writeD(_styles.length);
		for(BeautyStyleTemplate style : _styles)
		{
			writeD(style.getId());
			writeD(style.getValue());
		}
		writeD(0);
	}
}