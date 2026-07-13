package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.data.xml.holder.BeautyShopHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.beatyshop.BeautyColorTemplate;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;
import l2s.gameserver.templates.beatyshop.BeautyStyleTemplate;


public class ExBeautyItemList extends L2GameServerPacket
{
	private static final int HAIR_TYPE = 0;
	private static final int FACE_TYPE = 1;
	private static final int COLOR_TYPE = 2;

	private final BeautyStyleTemplate[] _hairStyles;
	private final BeautyStyleTemplate[] _faces;
	private final int _colorsCount;
	
	public ExBeautyItemList(Player player)
	{
		BeautyStyleTemplate[] hairStyles = new BeautyStyleTemplate[0];
		BeautyStyleTemplate[] faces = new BeautyStyleTemplate[0];

		BeautySetTemplate set = BeautyShopHolder.getInstance().getTemplate(player);
		if(set != null)
		{
			hairStyles = set.getHairs();
			faces = set.getFaces();
		}

		_hairStyles = hairStyles;
		_faces = faces;

		int colorsCount = 0;
		for(BeautyStyleTemplate style : hairStyles)
			colorsCount += style.getColors().size();

		_colorsCount = colorsCount;
	}
	
	@Override
	protected void writeImpl()
	{
		writeD(HAIR_TYPE);
		writeD(_hairStyles.length);
		for(BeautyStyleTemplate style : _hairStyles)
		{
			writeD(0); 
			writeD(style.getId());
			writeD((int) style.getAdena());
			writeD((int) style.getResetPrice());
			writeD((int) style.getCoins());
			writeD(style.getValue()); 
		}
		
		writeD(FACE_TYPE);
		writeD(_faces.length);
		for(BeautyStyleTemplate style : _faces)
		{
			writeD(0); 
			writeD(style.getId());
			writeD((int) style.getAdena());
			writeD((int) style.getResetPrice());
			writeD((int) style.getCoins());
			writeD(style.getValue()); 
		}
		
		writeD(COLOR_TYPE);
		writeD(_colorsCount);
		for(BeautyStyleTemplate style : _hairStyles)
		{
			for(BeautyColorTemplate color : style.getColors())
			{
				writeD(style.getId());
				writeD(color.getId());
				writeD((int) color.getAdena());
				writeD(0);
				writeD((int) color.getCoins());
				writeD(1);
			}
		}
	}
}
