package npc.model;

import instances.DimensionalWarp;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

//By Evil_dnk
public class DimensionalWarpGatekeeperInstance extends NpcInstance
{
	private static final int Warp = 250;

	public DimensionalWarpGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Reflection reflection = player.getReflection();
		if(command.startsWith("enter"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(Warp))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(Warp))
			{
				ReflectionUtils.enterReflection(player, new DimensionalWarp(), Warp);
			}
		}
		else if(command.startsWith("upgradebracelet"))
		{
			QuestState quest1 = player.getQuestState(10802);
			QuestState quest2 = player.getQuestState(10803);
			QuestState quest3 = player.getQuestState(10804);
			QuestState quest4 = player.getQuestState(10805);
			QuestState quest5 = player.getQuestState(10806);
			if(quest5 != null && quest5.isCompleted())
			{
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(3397405, player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else if(quest4 != null && quest4.isCompleted())
			{
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(3397404, player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else if(quest3 != null && quest3.isCompleted())
			{
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(3397403, player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else if(quest2 != null && quest2.isCompleted())
			{
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(3397402, player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else if(quest1 != null && quest1.isCompleted())
			{
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(3397401, player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else
				showChatWindow(player, "default/" + getNpcId() + ".htm", false);
		}
		else if(reflection != null && reflection instanceof DimensionalWarp)
		{
				final DimensionalWarp warpdim = (DimensionalWarp) reflection;
				if (command.equalsIgnoreCase("request_warp12"))
				{
					warpdim.startinst(player, 12, 0.012);
				}
				else if (command.equalsIgnoreCase("request_warp240"))
				{
					warpdim.startinst(player, 240, 0.09);
				}
				else if (command.equalsIgnoreCase("request_warp1200"))
				{
					warpdim.startinst(player, 1200, 0.35);
				}
				else if (command.equalsIgnoreCase("request_next"))
				{
					warpdim.secondPart(player);
				}
				else
					super.onBypassFeedback(player, command);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if (val == 0)
		{
			if(getNpcId() == 33975)
			{
				Reflection reflection = player.getReflection();
				if (reflection != null)
				{
					if (reflection instanceof DimensionalWarp)
					{
						final DimensionalWarp warpdim = (DimensionalWarp) reflection;
						if (warpdim.getStage() >= 20)
							showChatWindow(player, "default/33975-1.htm", firstTalk);
						else
							showChatWindow(player, "default/33975.htm", firstTalk);
					}
				}
			}
			else if(getNpcId() == 33974)
				showChatWindow(player, "default/33974.htm", firstTalk);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
