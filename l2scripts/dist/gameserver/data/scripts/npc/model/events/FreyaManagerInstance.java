package npc.model.events;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class FreyaManagerInstance extends NpcInstance
{
	private static final int GIFT_ID = 15440;	// Зелье Поддержания Энергии - 30 мин
	private static final int GIFT_PRICE = 1;
	private static final int GIFT_RECEIVE_DELAY = 20 * 60 * 60 * 1000; // 20 часов
	private static final String EVENT_VARIABLE = "FreyaCelebration";

	public FreyaManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("receivegift"))
		{
			long remainingTime;
			long currTime = System.currentTimeMillis();
			String lastUseTime = player.getVar(EVENT_VARIABLE);

			if(lastUseTime != null)
				remainingTime = currTime - Long.parseLong(lastUseTime);
			else
				remainingTime = GIFT_RECEIVE_DELAY;

			if(remainingTime >= GIFT_RECEIVE_DELAY)
			{
				if(ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, GIFT_PRICE))
				{
					ItemFunctions.addItem(player, GIFT_ID, 1);
					player.setVar(EVENT_VARIABLE, String.valueOf(currTime), -1);
				}
				else
					player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
			}
			else
			{
				int hours = (int) (GIFT_RECEIVE_DELAY - remainingTime) / 3600000;
				int minutes = (int) (GIFT_RECEIVE_DELAY - remainingTime) % 3600000 / 60000;
				if(hours > 0)
					player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
				else if(minutes > 0)
					player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
				else if(ItemFunctions.deleteItem(player, ItemTemplate.ITEM_ID_ADENA, GIFT_PRICE))
				{
					ItemFunctions.addItem(player, GIFT_ID, 1);
					player.setVar(EVENT_VARIABLE, String.valueOf(currTime), -1);
				}
				else
					player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}