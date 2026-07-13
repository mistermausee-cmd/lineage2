package events;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.l2.s2c.ExBrBroadcastEventState;

/**
 * @author Bonux
**/
public class AprilFoolsDayEvent extends FunEvent
{
	private class EventListeners implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			if(isInProgress())
				player.sendPacket(new ExBrBroadcastEventState(ExBrBroadcastEventState.APRIL_FOOLS_10, 1));
		}
	}

	private final OnPlayerEnterListener EVENT_LISTENERS = new EventListeners();

	public AprilFoolsDayEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void startEvent()
	{
		super.startEvent();
		CharListenerList.addGlobal(EVENT_LISTENERS);

		ExBrBroadcastEventState es = new ExBrBroadcastEventState(ExBrBroadcastEventState.APRIL_FOOLS, 1);
		for(Player p : GameObjectsStorage.getPlayers())
			p.sendPacket(es);
	}

	@Override
	public void stopEvent(boolean force)
	{
		super.stopEvent(force);
		CharListenerList.removeGlobal(EVENT_LISTENERS);
	}
}
