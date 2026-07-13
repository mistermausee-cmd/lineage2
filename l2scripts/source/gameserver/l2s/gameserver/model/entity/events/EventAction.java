package l2s.gameserver.model.entity.events;


public interface EventAction
{
	void call(Event event);
}