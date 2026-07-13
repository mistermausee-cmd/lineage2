package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Experience;

public class ExTrainingZone_Admission extends L2GameServerPacket
{
	private final int _timeElapsed;
	private final int _timeRemaining;
	private final double _maxExp;
	private final double _maxSp;

    public ExTrainingZone_Admission(Player player)
    {
        _timeElapsed = 0;
        _timeRemaining = Config.TRAINING_CAMP_MAX_DURATION;
        _maxExp = Experience.getExpForLevel(player.getLevel()) * Experience.getTrainingRate(player.getLevel()) * Config.RATE_XP_BY_LVL[player.getLevel()] / _timeRemaining;
        _maxSp = _maxExp / 250.0;
    }
    
    public ExTrainingZone_Admission(int level, int timeElapsed, int timeRemaing)
    {
        _timeElapsed = timeElapsed;
        _timeRemaining = timeRemaing;
        
        _maxExp = Experience.getExpForLevel(level) * Experience.getTrainingRate(level) * Config.RATE_XP_BY_LVL[level] / _timeRemaining;
        _maxSp = _maxExp / 250.0;
    }

	@Override
	public void writeImpl()
	{
		writeD(_timeElapsed);
		writeD(_timeRemaining);
		writeF(_maxExp);
		writeF(_maxSp);
	}
}