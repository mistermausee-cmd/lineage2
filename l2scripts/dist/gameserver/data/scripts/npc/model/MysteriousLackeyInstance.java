package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ChaosFestivalManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestBypassToServer;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseObserveList;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ChatUtils;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class MysteriousLackeyInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int TID_SHOUT_ALONE = 83001;
	private static final int TIME_SHOUT_ALONE = 60000;

	public MysteriousLackeyInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		getAI().addTimer(TID_SHOUT_ALONE, TIME_SHOUT_ALONE);
	}

	@Override
	public void onSeeCreatue(Creature creature)
	{
		if(creature.isPlayer())
		{
			Clan clan = ChaosFestivalManager.getInstance().getWinnerClan();
			if(clan != null && clan.isAnyMember(creature.getObjectId()))
			{
				ChatUtils.shout(this, NpcString.YOU_ARE_THE_CLAN_MEMBER_WHO_PRODUCED_S1_THIS_WEEKS_WINNER_YOU_HAVE_TREMENDOUS_ABILITIES_AND_LUCK_I_GIVE_YOU_MY_MASTERS_BLESSING, clan.getName());
				forceUseSkill(SkillHolder.getInstance().getSkill(15109, 1), creature);
			}
		}
	}

	@Override
	public void onTimerFired(int timerId)
	{
		if(timerId == TID_SHOUT_ALONE)
		{
			if(getAI().isActive())
			{
				String winnerClanName = ChaosFestivalManager.getInstance().getWinnerClanName();
				if(winnerClanName == null)
					ChatUtils.shout(this, NpcString.IS_THERE_NO_CLAN_THAT_CAN_RAISE_A_TRUE_ULTIMATE_WARRIOR);
				else
					ChatUtils.shout(this, NpcString.THE_CLAN_THAT_SEIZED_THE_MOST_HONOR_THIS_WEEK_IS_S1, winnerClanName);
			}
			getAI().addTimer(TID_SHOUT_ALONE, TIME_SHOUT_ALONE);
		}
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -1 && reply == 1)
		{
			if(ChaosFestivalManager.getInstance().isWinner(player))
				showChatWindow(player, "default/grankain_lumiere004.htm", false, "<?talker?>", ChaosFestivalManager.getInstance().getWinnerName());
			else
				showChatWindow(player, "default/grankain_lumiere005.htm", false);
		}
		else if(ask == -2 && reply == 1)
		{
			String winnerName = ChaosFestivalManager.getInstance().getWinnerName();
			String winnerPledgeName = ChaosFestivalManager.getInstance().getWinnerClanName();
			if(winnerName == null || winnerPledgeName == null)
				showChatWindow(player, "default/grankain_lumiere015.htm", false);
			else
				showChatWindow(player, "default/grankain_lumiere007.htm", false, "<?winner_player?>", winnerName, "<?winner_pledge?>", winnerPledgeName);
		}
		else if(ask == -3 && reply == 1)
		{
			Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend(865, player, castle != null ? castle.getSellTaxRate() : 0);
		}
		else if(ask == 850 && reply == 1)
		{
			Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend(3002, player, castle != null ? castle.getSellTaxRate() : 0);
		}
		else if(ask == -3 && reply == 3)
		{
			if(ItemFunctions.getItemCount(player, 34900) >= 8)
			{
				ItemFunctions.deleteItem(player, 34900, 8, true);

				if(Rnd.get(100) < 50)
					ItemFunctions.addItem(player, 34907, 1, true);
				else if(Rnd.get(100 - 50) < 30)
					ItemFunctions.addItem(player, 34906, 1, true);
				else
					ItemFunctions.addItem(player, 34905, 1, true);

				showChatWindow(player, "default/grankain_lumiere012.htm", false);
			}
			else
				showChatWindow(player, "default/grankain_lumiere013.htm", false);
		}
		else if(ask == -4 && reply == 1)
		{
			if(ChaosFestivalManager.getInstance().isWinnerNotReceived(player))
			{
				showChatWindow(player, "default/grankain_lumiere009.htm", false);
				ItemFunctions.addItem(player, 35564, 1, true);
				Clan clan = player.getClan();
				if(clan != null && clan.getLevel() >= 5)
				{
					clan.incReputation(5000, false, "Receive Chaos Festival reward");
					player.sendPacket(new SayPacket2(0, ChatType.SCREEN_ANNOUNCE, "", NpcString.YOUR_CLAN_HAS_ACQUIRED_S1_POINTS_TO_ITS_CLAN_REPUTATION, "5000"));
				}
				player.setFame(5000, "Receive Chaos Festival reward", true);
				Announcements.announceToAll(NpcString.THE_WINNER_OF_THE_CEREMONY_OF_CHAOS_HAS_BEEN_BORN);
				ChaosFestivalManager.getInstance().setWinnerRewardReceived();
			}
			else
				showChatWindow(player, "default/grankain_lumiere010.htm", false);
		}
		else if(ask == -5 && reply == 1)
		{
			showChatWindow(player, "default/grankain_lumiere017.htm", false, "<?point?>", ChaosFestivalManager.getInstance().getTopRankerPoints());
		}
		else if(ask == -6 && reply == 1)
		{
			Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend(2095, player, castle != null ? castle.getSellTaxRate() : 0);
		}
		else if(ask == -7 && reply == 1)
		{
			Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend(3003, player, castle != null ? castle.getSellTaxRate() : 0);
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			if(player.isPK())
				showChatWindow(player, "default/grankain_lumiere016.htm", firstTalk);
			else
				showChatWindow(player, "default/grankain_lumiere001.htm", firstTalk);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("pledgegame?command=op_field_list"))
		{
			ChaosFestivalEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 6);
			if(event == null || !event.isInProgress())
			{
				player.sendPacket(SystemMsg.THE_CEREMONY_OF_CHAOS_IS_NOT_CURRENTLY_OPEN);
				return;
			}
			player.sendPacket(new ExCuriousHouseObserveList());
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestBypassToServer.class && arg.length == 1 && arg[0].equals("pledgegame?command=op_field_list");
	}
}
