package npc.model.events;

import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.objects.FightBattleArenaObject;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestBypassToServer;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket.MatchList;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class FightBattleManagerInstance extends NpcInstance
{
	public FightBattleManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		// до всех проверок
		if(command.startsWith("_olympiad?")) // _olympiad?command=move_op_field&field=1
		{
			String[] ar = command.split("&");
			if(ar.length < 2)
				return;

			if(ar[0].equalsIgnoreCase("_olympiad?command=move_op_field"))
			{
				String[] command2 = ar[1].split("=");
				if(command2.length < 2)
					return;

				List<FightBattleEvent> events = EventHolder.getInstance().getEvents(FightBattleEvent.class);
				if(events.isEmpty())
					return;

				FightBattleEvent event = events.get(0);
				if(event == null || !event.isInProgress())
					return;

				FightBattleArenaObject arena = event.getArena(Integer.parseInt(command2[1]) - 1);
				if(arena == null || !arena.isBattleBegin())
					return;

				player.enterArenaObserverMode(arena);
			}
			return;
		}

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("event"))
		{
			List<FightBattleEvent> events = EventHolder.getInstance().getEvents(FightBattleEvent.class);
			if(events.isEmpty())
			{
				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm", false);
				return;
			}

			FightBattleEvent event = events.get(0);
			if(event == null)
			{
				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm", false);
				return;
			}

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("register"))
			{
				if(!event.isRegistrationActive())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-registration_over.htm", false);
					return;
				}

				if(event.isParticle(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-already_registered.htm", false);
					return;
				}

				if(!event.checkParticipationCond(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_cond.htm", false);
					return;
				}

				if(event.getParticlePlayers().size() >= event.getMaxParticipants())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-max_participants.htm", false);
					return;
				}

				if(!event.tryAddParticipant(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-reg_error.htm", false);
					return;
				}

				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-succ_registered.htm", false);
			}
			else if(cmd2.equalsIgnoreCase("unregister"))
			{
				if(!event.isRegistrationActive())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-registration_over.htm", false);
					return;
				}

				if(!event.isParticle(player) || !event.removeParticipant(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_registered.htm", false);
					return;
				}

				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-succ_unregistered.htm", false);
			}
			else if(cmd2.equalsIgnoreCase("observ"))
			{
				if(!event.isInProgress())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm", false);
					return;
				}

				if(!event.isAllowObserv())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_observ.htm", false);
					return;
				}

				player.sendPacket(new MatchList(event));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestBypassToServer.class && arg.length == 1 && arg[0].equals("_olympiad?command=move_op_field");
	}
}