package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;
import l2s.gameserver.utils.ItemFunctions;


public class ExBR_ProductListPacket extends L2GameServerPacket
{
	private final long _adena;
	private final long _freeCoins;
	private final boolean _history;
	private final List<ProductItem> _products = new ArrayList<ProductItem>();

	public ExBR_ProductListPacket(Player player, boolean history)
	{
		_adena = player.getAdena();
		_freeCoins = ItemFunctions.getItemCount(player, 23805);
		_history = history;
		if (history)
			_products.addAll(player.getProductHistoryList().productValues());
		else
		{
			_products.addAll(ProductDataHolder.getInstance().getProductsOnSale(player));
			Collections.sort(_products);
		}
	}

	@Override
	protected void writeImpl()
	{
		writeQ(_adena);                                              
		writeQ(_freeCoins);                                          
		writeC(_history);                                            
		writeD(_products.size());
		for(ProductItem product : _products)
		{
			writeD(product.getId());                                
			writeC(product.getCategory());                          
			writeC(product.getPointsType().ordinal());              
			writeD(product.getPoints(true));                         
			writeC(product.getTabId());                             
			writeD(product.getMainCategory());                      
			writeD((int) (product.getStartTimeSale() / 1000));      
			writeD((int) (product.getEndTimeSale() / 1000));        
			writeC(127);                                      
			writeC(product.getStartHour());                         
			writeC(product.getStartMin());                          
			writeC(product.getEndHour());                           
			writeC(product.getEndMin());                            
			writeD(0x00);                                           
			writeD(-1);                                             
			writeC(product.getDiscount()); 
			writeC(0x00);                                           
			writeC(0x00);                                           
			writeD(0x00);                                           
			writeD(0x00);                                           
			writeD(0x00);                                           
			writeD(0x00);                                           

			writeC(product.getComponents().size());                 
			for(ProductItemComponent component : product.getComponents())
			{
				writeD(component.getId());
				writeD((int)component.getCount());
				writeD(component.getWeight());
				writeD(component.isDropable() ? 1 : 0);
			}
		}
	}
}