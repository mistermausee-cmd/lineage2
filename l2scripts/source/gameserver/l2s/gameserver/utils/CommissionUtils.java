package l2s.gameserver.utils;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.CommissionItem;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExUnReadMailCount;
import l2s.gameserver.templates.item.ExItemType;
import l2s.gameserver.templates.item.ItemQuality;
import l2s.gameserver.templates.item.ItemTemplate;


public class CommissionUtils
{
	public static boolean checkItem(CommissionItem item, int tree, int type, int quality, int grade, String searchWords)
	{
		if(item.getEndPeriodDate() < (int) (System.currentTimeMillis() / 1000L)) 
			return false;

		ItemTemplate itemTemplate = item.getItem();
		if(quality >= 0)
		{
			switch(quality)
			{
				case 0: 
					if(itemTemplate.getQuality() == ItemQuality.MASTERWORK)
						return false;
					break;
				case 1: 
					if(itemTemplate.getQuality() != ItemQuality.MASTERWORK)
						return false;
					break;
			}
		}

		if(grade >= 0)
		{
			if(itemTemplate.getGrade().ordinal() != grade)
				return false;
		}

		if(searchWords != null && !searchWords.isEmpty())
		{
			String itemName = "";
			for(Language lang : Language.VALUES)
				itemName += ItemNameHolder.getInstance().getItemName(lang, item.getItemId());

			if(itemName == null || searchWords.isEmpty())
				return false;

			searchWords = searchWords.toUpperCase();
			itemName = itemName.toUpperCase();
			if(!itemName.contains(searchWords))
				return false;
		}

		ExItemType exItemType = itemTemplate.getExType();
		if(tree == 1)
		{
			if(exItemType.mask() != type)
				return false;
		}
		else if(tree == 2)
		{
			if(exItemType.ordinal() != type)
				return false;
		}
		return true;
	}

	public static int getPeriodDays(int period)
	{
		switch(period)
		{
			case 0:
				return 1;
			case 1:
				return 3;
			case 2:
				return 5;
			case 3:
				return 7;
		}
		return 1;
	}

	public static long getCommissionPrice(long priceAmount, int perdioDays)
	{
		long result = (int) (priceAmount / 10000.0 * perdioDays);
		result = Math.max(1000, result);
		return result;
	}

	public static long getCommissionTax(long price, long count, int periodDays)
	{
		long result = (int) (price * count * 0.005) * periodDays;
		result = Math.max(1000, result);
		return result;
	}

	public static ItemInstance getItemFromCommissionItem(CommissionItem commissionItem)
	{
		ItemInstance item = ItemFunctions.createItem(commissionItem.getItemId());
		
		item.setCount(commissionItem.getCount());

		item.setEnchantLevel(commissionItem.getEnchantLevel());

		item.getAttributes().setFire(commissionItem.getAttributeFire() - item.getTemplate().getBaseAttributeValue(Element.FIRE));
        item.getAttributes().setWater(commissionItem.getAttributeWater() - item.getTemplate().getBaseAttributeValue(Element.WATER));
        item.getAttributes().setWind(commissionItem.getAttributeWind() - item.getTemplate().getBaseAttributeValue(Element.WIND));
        item.getAttributes().setEarth(commissionItem.getAttributeEarth() - item.getTemplate().getBaseAttributeValue(Element.EARTH));
        item.getAttributes().setHoly(commissionItem.getAttributeHoly() - item.getTemplate().getBaseAttributeValue(Element.HOLY));
        item.getAttributes().setUnholy(commissionItem.getAttributeUnholy() - item.getTemplate().getBaseAttributeValue(Element.UNHOLY));
        
		return item;
	}

	public static void sendBuyMailToOwner(Player sender, CommissionItem item)
	{
		Mail mail = new Mail();
		mail.setSenderId(sender.getObjectId());
		mail.setSenderName(sender.getName());
		mail.setReceiverId(item.getOwnerId());
		mail.setReceiverName(item.getOwnerName());
		mail.setTopic(Mail.COMMISSION_BUY_TOPIC);

		Player owner = World.getPlayer(item.getOwnerId());
		Language lang;
		if(owner != null)
			lang = owner.getLanguage();
		else
			lang = Language.getLanguage(CharacterVariablesDAO.getInstance().getVarFromPlayer(item.getOwnerId(), Language.LANG_VAR));
		mail.setBody(ItemNameHolder.getInstance().getItemName(lang, item.getItemId()));
		mail.setPrice(0L);
		mail.setUnread(true);
		mail.setType(Mail.SenderType.SYSTEM);
		mail.setExpireTime(31536000 + (int) (System.currentTimeMillis() / 1000L)); 
		mail.setSystemParam(1, item.getEnchantLevel());
		mail.setSystemParam(2, item.getAttributeFire());
		mail.setSystemParam(3, item.getAttributeWater());
		mail.setSystemParam(4, item.getAttributeWind());
		mail.setSystemParam(5, item.getAttributeEarth());
		mail.setSystemParam(6, item.getAttributeHoly());
		mail.setSystemParam(7, item.getAttributeUnholy());
		mail.setSystemTopic(SystemMsg.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD);
		mail.setSystemBody(SystemMsg.S1_HAS_BEEN_SOLD);

		long count = (item.getCommissionPrice() * item.getCount()) - getCommissionTax(item.getCommissionPrice(), item.getCount(), item.getPeriodDays());
		if(count > 0)
		{
			ItemInstance adena = ItemFunctions.createItem(ItemTemplate.ITEM_ID_ADENA);
			adena.setOwnerId(item.getOwnerId());
			adena.setCount(count);
			adena.setLocation(ItemInstance.ItemLocation.MAIL);
			if(adena.getJdbcState().isSavable())
				adena.save();
			else
			{
				adena.setJdbcState(JdbcEntityState.UPDATED);
				adena.update();
			}

			mail.addAttachment(adena);
		}

		mail.save();

		if(owner != null)
		{
			owner.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			owner.sendPacket(new ExUnReadMailCount(owner));
			owner.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}
	}

	public static void sendReturnMailToOwner(CommissionItem item)
	{
		Mail mail = new Mail();
		mail.setSenderId(item.getOwnerId());
		mail.setSenderName(Mail.COMMISSION_BUY_TOPIC);
		mail.setReceiverId(item.getOwnerId());
		mail.setReceiverName(item.getOwnerName());
		mail.setTopic(Mail.COMMISSION_BUY_TOPIC);
		mail.setBody("");
		mail.setPrice(0L);
		mail.setUnread(true);
		mail.setType(Mail.SenderType.SYSTEM);
		mail.setExpireTime(31536000 + (int) (System.currentTimeMillis() / 1000L)); 
		mail.setSystemTopic(SystemMsg.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED);
		mail.setSystemBody(SystemMsg.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED);

		ItemInstance returnedItem = getItemFromCommissionItem(item);
		returnedItem.setOwnerId(item.getOwnerId());
		returnedItem.setLocation(ItemInstance.ItemLocation.MAIL);
		if(returnedItem.getJdbcState().isSavable())
			returnedItem.save();
		else
		{
			returnedItem.setJdbcState(JdbcEntityState.UPDATED);
			returnedItem.update();
		}

		Log.LogItem("Player", item.getOwnerId(), Log.CommissionExpiredReturn, returnedItem);

		mail.addAttachment(returnedItem);
		mail.save();

		Player receiver = World.getPlayer(item.getOwnerId());
		if(receiver != null)
		{
			receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			receiver.sendPacket(new ExUnReadMailCount(receiver));
			receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}
	}
}
