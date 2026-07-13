package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.entity.events.objects.ChaosFestivalArenaObject;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;


public class ExCuriousHouseMemberList extends L2GameServerPacket
{
	private final int _arenaId;
	private final int _joinedMembersCount;
	private final Collection<ChaosFestivalPlayerObject> _members;

	public ExCuriousHouseMemberList(ChaosFestivalArenaObject arena)
	{
		_arenaId = arena.getId();
		_joinedMembersCount = arena.getJoinedMembersCount();
		_members = arena.getMembers();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_arenaId);
		writeD(_joinedMembersCount);
		writeD(_members.size());
		for(ChaosFestivalPlayerObject member : _members)
		{
			writeD(member.getObjectId());
			writeD(member.getId());
			writeD(member.getMaxHp());
			writeD(member.getMaxCp());
			writeD(member.getCurrentHp());
			writeD(member.getCurrentCp());
		}
	}
}