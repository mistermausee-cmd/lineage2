package l2s.gameserver.handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;


public interface IItemHandler
{
	
	public boolean forceUseItem(Playable playable, ItemInstance item, boolean ctrl);

	
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl);

	
	public void dropItem(Player player, ItemInstance item, long count, Location loc);

	
	public boolean pickupItem(Playable playable, ItemInstance item);

	
	public void onRestoreItem(Playable playable, ItemInstance item);

	
	public void onAddItem(Playable playable, ItemInstance item);

	
	public void onRemoveItem(Playable playable, ItemInstance item);

	public boolean isAutoUse();

	public SystemMsg checkCondition(Playable playable, ItemInstance item);
}