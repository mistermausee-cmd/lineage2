package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.instancemanager.itemauction.ItemAuction;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionBid;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionState;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;


public final class CustomItemAuctionBrokerInstance extends ItemAuctionBrokerInstance
{
	private static final long serialVersionUID = 1L;

	public CustomItemAuctionBrokerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, final int val, boolean firstTalk, Object... arg)
	{
		String filename = val == 0 ? "itemauction/customitembroker.htm" : "itemauction/customitembroker-" + val + ".htm";
		player.sendPacket(new HtmlMessage(this, filename).setPlayVoice(firstTalk));
	}

	@Override
	public int getAuctionInstanceId()
	{
		return ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID;
	}

	@Override
	public final void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("auction"))
		{
			if(_instance == null)
			{
				_instance = ItemAuctionManager.getInstance().getManagerInstance(getAuctionInstanceId());
				if(_instance == null)
					
					return;
			}

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("bid"))
			{
				ItemAuction auction = _instance.getCurrentAuction();
				if(auction == null || auction.getAuctionState() != ItemAuctionState.STARTED)
				{
					onBypassFeedback(player, "auction show");
					return;
				}

				String cmd3 = st.nextToken();
				int bidType = Integer.parseInt(cmd3);

				ItemAuctionBid highestBid = auction.getHighestBid();
				long highBid = highestBid != null ? highestBid.getLastBid() : auction.getAuctionInitBid();

				long newBid = 0;
				switch(bidType)
				{
					case 1: 
						newBid = (int) (highBid * 2);
						break;
					case 2: 
						newBid = (int) (highBid * 1.5);
						break;
					case 3: 
						newBid = (int) (highBid * 1.1);
						break;
					case 4: 
						newBid = (int) (highBid * 1.05);
						break;
					case 5:
						String cmd4 = st.nextToken();
						newBid = Long.parseLong(cmd4);
						break;
				}

				ItemInstance bid = player.getInventory().getItemByItemId(auction.getAuctionItem().getActionBidItemId());
				if(bid != null && newBid > 0 && newBid <= bid.getCount())
					auction.registerBid(player, newBid);
				else
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);

				onBypassFeedback(player, "auction show");
			}
			else if(cmd2.equalsIgnoreCase("show"))
			{
				ItemAuction auction = _instance.getCurrentAuction();
				if(auction == null || auction.getAuctionState() != ItemAuctionState.STARTED)
					auction = _instance.getNextAuction();

				if(auction == null)
				{
					player.sendPacket(SystemMsg.IT_IS_NOT_AN_AUCTION_PERIOD);
					return;
				}

				

				if(auction.getAuctionState() == ItemAuctionState.STARTED || auction.getStartingTime() > System.currentTimeMillis())
				{
					int time;
					if(auction.getAuctionState() == ItemAuctionState.STARTED)
						time = (int) ((auction.getEndingTime() - System.currentTimeMillis()) / 1000);
					else
						time = (int) ((auction.getStartingTime() - System.currentTimeMillis()) / 1000);

					int hh = time / 60 / 60;
					int mm = (time - (hh * 60 * 60)) / 60;
					int ss = (time - (hh * 60 * 60) - (mm * 60));

					int bidItemId = auction.getAuctionItem().getActionBidItemId();
					String bidItemName = HtmlUtils.htmlItemName(bidItemId);
					long bidCount;
					if(auction.getAuctionState() == ItemAuctionState.STARTED)
					{
						ItemAuctionBid highestBid = auction.getHighestBid();
						bidCount = highestBid != null ? highestBid.getLastBid() : auction.getAuctionInitBid();
					}
					else
						bidCount = auction.getAuctionInitBid();

					int auctionItemId = auction.getAuctionItem().getItemId();
					ItemTemplate auctionItemTemplate = ItemHolder.getInstance().getTemplate(auctionItemId);
					String auctionItemName = HtmlUtils.htmlItemName(auctionItemId);
					long auctionItemCount = auction.getAuctionItem().getCount();
					int auctionItemEnchant = auction.getAuctionItem().getEnchantLevel();
					if(auctionItemEnchant > 0)
						auctionItemName = "+" + auctionItemEnchant + " " + auctionItemName;

					long haveBidItemCount = ItemFunctions.getItemCount(player, bidItemId);

					String html;
					if(auction.getAuctionState() == ItemAuctionState.STARTED)
						html = "itemauction/custom_auction.htm";
					else
						html = "itemauction/custom_auction_unactive.htm";

					showChatWindow(player, html, false, "<?HH?>", hh, "<?MM?>", mm, "<?SS?>", ss, "<?BID_ITEM_NAME?>", bidItemName, "<?BID_COUNT?>", bidCount, "<?AUCTION_ITEM_NAME?>", auctionItemName, "<?AUCTION_ITEM_ICON?>", auctionItemTemplate.getIcon(), "<?AUCTION_ITEM_COUNT?>", auctionItemCount, "<?HAVE_BID_ITEM_COUNT?>", haveBidItemCount);
				}
				else
					player.sendPacket(SystemMsg.IT_IS_NOT_AN_AUCTION_PERIOD);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}