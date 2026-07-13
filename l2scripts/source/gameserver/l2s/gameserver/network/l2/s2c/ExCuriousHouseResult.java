package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.entity.events.objects.ChaosFestivalArenaObject;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;


public class ExCuriousHouseResult extends L2GameServerPacket
{
	public static enum ResultState
	{
		TIE, 
		WIN, 
		LOSE;
	}

	private final int _objectId;
	private final ResultState _state;
	private final int _joinedMembersCount;
	private final Collection<ChaosFestivalPlayerObject> _members;

	public ExCuriousHouseResult(ChaosFestivalArenaObject arena, int objectId, ResultState state, Collection<ChaosFestivalPlayerObject> members)
	{
		_objectId = objectId;
		_state = state;
		_joinedMembersCount = arena.getJoinedMembersCount();
		_members = members;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objectId);
		writeH(_state.ordinal());
		writeD(_joinedMembersCount);
		writeD(_members.size());
		for(ChaosFestivalPlayerObject member : _members)
		{
			writeD(member.getObjectId());
			writeD(member.getId());
			writeD(member.getActiveClassId());
			writeD(member.getLifeTime());
			writeD(member.getKills());
		}
	}
}