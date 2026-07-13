package npc;

import instances.Beleth;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.ItemFunctions;

import java.util.List;
import java.util.stream.Collectors;

/**
 Obi-Wan
 10.11.2016
 */
public class LeonaBlackbird extends ListenerHook implements OnInitScriptListener
{
	@Override
	public void onInit()
	{
		addHookNpc(ListenerHookType.NPC_FIRST_TALK, 31595);
		addHookNpc(ListenerHookType.NPC_ASK, 31595);
	}

	@Override
	public boolean onNpcFirstTalk(NpcInstance npc, Player player)
	{
		if(npc.getReflectionId() > 0)
		{
			Beleth beleth = (Beleth) npc.getReflection();
			switch(beleth.getStage().get())
			{
				case 0:
					npc.showChatWindow(player, "default/31595-4.htm", true);
					break;
				case 3:
					if(beleth.getBelethRing().compareAndSet(false, true))
					{
						List<Player> wins = beleth.getPlayers().stream().filter(p -> !p.isDead()).collect(Collectors.toList());
						Player win = Rnd.get(wins);
						npc.broadcastPacket(new ExShowScreenMessage(NpcString.LEONA_BLACKBIRD_GAVE_BELETHS_RING_AS_A_GIFT_TO_S1, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, win.getName()));
						ItemFunctions.addItem(win, 10314, 1);
					}
				case 4:
					npc.showChatWindow(player, "default/31595-6.htm", true);
					break;
			}
			return true;
		}
		return false;
	}

	@Override
	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{
		Beleth beleth = (Beleth) npc.getReflection();
		if(ask == 1 && reply == 1)
		{
			if(beleth.getWins().contains(player.getObjectId()))
			{
				npc.showChatWindow(player, "default/31595-7.htm", false);
			}
			else
			{
				beleth.getWins().add(player.getObjectId());
				ItemFunctions.addItem(player, 37823, 1);
			}
		}
	}
}