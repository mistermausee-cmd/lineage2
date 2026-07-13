package l2s.gameserver.model.entity.events;

import l2s.commons.threading.RunnableImpl;


public class EventTimeTask extends RunnableImpl
{
	private final Event _event;
	private final int _time;

	public EventTimeTask(Event event, int time)
	{
		_event = event;
		_time = time;
	}

	@Override
	public void runImpl() throws Exception
	{
		_event.timeActions(_time);
	}
}