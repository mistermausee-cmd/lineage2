package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalArenaObject;


public class ExCuriousHouseObserveList extends L2GameServerPacket
{
	public ExCuriousHouseObserveList(int currentId)
	{
	    ChaosFestivalEvent event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 6);
	    if(event == null || !event.isInProgress())
	    	return; 
	    for(ChaosFestivalArenaObject arena : event.getArenas())
	    {
	    	if(arena.getId() == currentId)
	    		continue; 
	    	_arenas.add(new ArenaInfo(arena.getId(), "Arena #" + arena.getId(), arena.getBattleState().ordinal(), arena.getMembers().size()));
	    }
	}

	private static class ArenaInfo
	{
		public final int id;
		public final String unk;
		public final int status;
		public final int participants;

		public ArenaInfo(int id, String unk, int status, int participants)
		{
			this.id = id;
			this.unk = unk;
			this.status = status;
			this.participants = participants;
		}
	}

	private final List<ArenaInfo> _arenas = new ArrayList<ArenaInfo>();
 
	public ExCuriousHouseObserveList()
	{
		this(-1);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_arenas.size());
		for (ArenaInfo arena : _arenas)
		{
			writeD(arena.id);
			writeS(arena.unk);
			writeH(arena.status);
			writeD(arena.participants);
		}
	}
}