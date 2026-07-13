package services;

import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.base.NobleType;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.ItemFunctions;

public class NoblessSell
{
	@Bypass("services.NoblessSell:get")
	public void get(Player player, NpcInstance npc, String[] param)
	{
		if(player.isNoble())
			return;

		if(player.getSubLevel() < 75)
		{
			player.sendMessage("You must make sub class level 75 first.");
			return;
		}

		if(ItemFunctions.deleteItem(player, Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE))
		{
			makeSubQuests(player, npc, param);
			becomeNoble(player, npc, param);
		}
		else if(Config.SERVICES_NOBLESS_SELL_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	@Bypass("services.NoblessSell:makeSubQuests")
	public void makeSubQuests(Player player, NpcInstance npc, String[] param)
	{
		QuestState qs = player.getQuestState(10385);
		if(qs != null)
			qs.finishQuest();
	}

	@Bypass("services.NoblessSell:becomeNoble")
	public void becomeNoble(Player player, NpcInstance npc, String[] param)
	{
		if(player.isNoble())
			return;

		player.setNobleType(NobleType.NORMAL);
	}
}