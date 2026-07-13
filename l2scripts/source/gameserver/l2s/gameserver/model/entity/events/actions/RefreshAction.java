package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventAction;


public class RefreshAction implements EventAction
{
	private final String _name;

	public RefreshAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.refreshAction(_name);
	}
}