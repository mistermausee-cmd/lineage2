package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.luckygame.LuckyGameData;
import l2s.gameserver.templates.luckygame.LuckyGameItem;


public class ExStartLuckyGame extends L2GameServerPacket
{
	private final int _gameId;
	private long _feeItemsCount;

	public ExStartLuckyGame(Player player, LuckyGameData data)
	{
		_gameId = data.getGameId();

		for(LuckyGameItem feeItem : data.getFeeItems())
		{
			_feeItemsCount += player.getInventory().getCountOf(feeItem.getId()) / feeItem.getCount(); 
		}

		int gamesLimit = data.getGamesLimit();
		if(gamesLimit > 0)
		{
			int playedGamesCount = player.getVarInt(LuckyGameData.PLAYED_LUCKY_GAMES_VAR + data.getGameId(), 0);
			_feeItemsCount = Math.max(0, Math.min(_feeItemsCount, gamesLimit - gamesLimit));
		}
	}

	@Override
	protected void writeImpl()
	{
		writeD(_gameId);
		writeQ(_feeItemsCount);
	}
}