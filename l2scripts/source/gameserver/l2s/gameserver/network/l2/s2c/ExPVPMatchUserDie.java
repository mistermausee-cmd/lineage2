package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2s.gameserver.model.entity.events.objects.UCTeamObject;


public class ExPVPMatchUserDie extends L2GameServerPacket
{
	private int _blueKills, _redKills;

    public ExPVPMatchUserDie(UndergroundColiseumBattleEvent e)
    {
        UCTeamObject team = e.getFirstObject(TeamType.BLUE);
        _blueKills = team.getKills();
        team = e.getFirstObject(TeamType.RED);
        _redKills = team.getKills();
    }
   
	public ExPVPMatchUserDie(int blueKills, int redKills)
	{
		_blueKills = blueKills;
		_redKills = redKills;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_blueKills);
		writeD(_redKills);
	}
}