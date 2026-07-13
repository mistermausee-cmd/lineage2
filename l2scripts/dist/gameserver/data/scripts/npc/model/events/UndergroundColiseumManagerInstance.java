package npc.model.events;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.UndergroundColiseumEvent;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 15:40/12.07.2011
 */
public class UndergroundColiseumManagerInstance extends UndergroundColiseumHelperInstance
{
	private String _startHtm;

	public UndergroundColiseumManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_startHtm = getParameter("start_htm", StringUtils.EMPTY);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		UndergroundColiseumEvent coliseumEvent = getEvent(UndergroundColiseumEvent.class);
		if(coliseumEvent == null)
			return;

		List<Player> leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);

		if(command.equals("register"))
		{
			Party party = player.getParty();
			if(party == null)
				showChatWindow(player, "events/kerthang_manager008.htm", false);
			else if(party.getPartyLeader() != player)
				showChatWindow(player, "events/kerthang_manager004.htm", false);
			else if(party.getMemberCount() < UndergroundColiseumEvent.PARTY_SIZE)
				showChatWindow(player, "events/kerthang_manager010.htm", false);
			else
			{
				for(int i = 2; i <= 6; i++)
				{
					UndergroundColiseumEvent $event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, i);
					if($event == null)
						continue;

					List<Player> $leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);

					for(Player object : $leaders)
						if(object == player)
						{
							showChatWindow(player, "events/kerthang_manager009.htm", false);
							return;
						}
				}

				for(Player $player : party)
				{
					if($player.getAbnormalList().contains(5661))
					{
						showChatWindow(player, "events/kerthang_manager021.htm", false, "%name%", $player.getName());
						return;
					}

					if($player.getLevel() < coliseumEvent.getMinLevel() || $player.getLevel() > coliseumEvent.getMaxLevel())
					{
						showChatWindow(player, "events/kerthang_manager011.htm", false, "%name%", $player.getName());
						return;
					}

					if($player.getDistance(this) > 400)
					{
						showChatWindow(player, "events/kerthang_manager012.htm", false);
						return;
					}
				}

				if(leaders.size() >= 5)
				{
					showChatWindow(player, "events/kerthang_manager013.htm", false);
					return;
				}

				coliseumEvent.addObject(UndergroundColiseumEvent.REGISTERED_LEADERS, player);

				showChatWindow(player, "events/kerthang_manager014.htm", false);
			}
		}
		else if(command.equals("viewMostWins"))
		{
			Pair<String, Integer> mostWin = coliseumEvent.getTopWinner();
			if(mostWin == null)
				showChatWindow(player, "events/kerthang_manager020.htm", false);
			else
				showChatWindow(player, "events/kerthang_manager019.htm", false, "%name%", mostWin.getKey(), "%count%", mostWin.getValue());
		}
		else if(command.equals("cancel"))
		{
			Party party = player.getParty();
			if(party == null)
				showChatWindow(player, "events/kerthang_manager008.htm", false);
			else if(party.getPartyLeader() != player)
				showChatWindow(player, "events/kerthang_manager004.htm", false);
			else
			{
				for(Player temp : leaders)
					if(temp == player)
					{
						leaders.remove(player);

						showChatWindow(player, "events/kerthang_manager005.htm", false);
						return;
					}

				showChatWindow(player, "events/kerthang_manager006.htm", false);
			}
		}
		else if(command.equals("viewTeams"))
		{
			HtmlMessage msg = new HtmlMessage(this);
			msg.setFile("events/kerthang_manager003.htm");
			for(int i = 0; i < UndergroundColiseumEvent.REGISTER_COUNT; i++)
			{
				Player team = CollectionUtils.safeGet(leaders, i);

				msg.replace("%team" + i + "%", team == null ? StringUtils.EMPTY : team.getName());
			}

			player.sendPacket(msg);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... ar)
	{
		showChatWindow(player, _startHtm, firstTalk);
	}
}
