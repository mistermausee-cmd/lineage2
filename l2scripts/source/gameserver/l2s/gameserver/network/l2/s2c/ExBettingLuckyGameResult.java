package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.luckygame.LuckyGameData;
import l2s.gameserver.templates.luckygame.LuckyGameItem;


public class ExBettingLuckyGameResult extends L2GameServerPacket
{
	private final int _gameId;
	private long _feeItemsCount;
	private final List<LuckyGameItem> _items;

	public ExBettingLuckyGameResult(Player player, LuckyGameData data, List<LuckyGameItem> items)
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

		_items = items;
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x01); 
		writeD(0x00); 
		writeD((int) _feeItemsCount);
		writeD(_items.size()); 
		for(LuckyGameItem item : _items)
		{
			writeD(item.isFantastic() ? 2 : 0);
			writeD(item.getId());
			writeD((int) item.getCount());
		}
	}
}