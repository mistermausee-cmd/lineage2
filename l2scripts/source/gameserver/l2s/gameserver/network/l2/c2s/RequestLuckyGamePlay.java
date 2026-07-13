package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.LuckyGameHolder;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExBettingLuckyGameResult;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.luckygame.LuckyGameData;
import l2s.gameserver.templates.luckygame.LuckyGameItem;
import l2s.gameserver.utils.ItemFunctions;


public class RequestLuckyGamePlay extends L2GameClientPacket
{
	private int _gameId;
	private int _gamesCount;

	@Override
	protected void readImpl()
	{
		_gameId = readD();
		_gamesCount = Math.min(readD(), 50);
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(_gamesCount <= 0)
			return;

		LuckyGameData gameData = LuckyGameHolder.getInstance().getData(_gameId);
		if(gameData == null)
			return;

		if(player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_EITHER_FULL_OR_OVERWEIGHT);
			return;
		}

		int playedGamesCount = 0;
		int gamesLimit = gameData.getGamesLimit();
		if(gamesLimit > 0)
		{
			playedGamesCount = player.getVarInt(LuckyGameData.PLAYED_LUCKY_GAMES_VAR + gameData.getGameId(), 0);
			if(playedGamesCount >= gamesLimit)
				return;

			_gamesCount = Math.min(_gamesCount, gamesLimit - playedGamesCount);

			if(_gamesCount <= 0)
				return;
		}

		int availableGames = 0;
		TIntLongMap consumedItemsMap = new TIntLongHashMap();
		for(LuckyGameItem feeItem : gameData.getFeeItems())
		{
			long itemsCount = ItemFunctions.getItemCount(player, feeItem.getId());
			int consumeGamesCount = Math.min((int) (itemsCount / feeItem.getCount()), _gamesCount - availableGames);
			if(consumeGamesCount > 0)
			{
				if(ItemFunctions.deleteItem(player, feeItem.getId(), consumeGamesCount * feeItem.getCount(), false))
				{
					availableGames += consumeGamesCount;
					consumedItemsMap.put(feeItem.getId(), consumedItemsMap.get(feeItem.getId()) + feeItem.getCount());
				}
				
				if(availableGames == _gamesCount)
					break;
			}
		}

		if(availableGames <= 0)
		{
			player.sendPacket(SystemMsg.NOT_ENOUGH_TICKETS);
			return;
		}

		if(_gameId == 2)
			player.sendPacket(new SystemMessagePacket(SystemMsg.ROUND_S1_OF_LUXURY_FORTUNE_READING_COMPLETE).addInteger(availableGames));
		else
			player.sendPacket(new SystemMessagePacket(SystemMsg.ROUND_S1_OF_FORTUNE_READING_COMPLETE).addInteger(availableGames));
		
		for(TIntLongIterator iterator = consumedItemsMap.iterator(); iterator.hasNext(); )
		{
			iterator.advance();
			player.sendPacket(SystemMessagePacket.removeItems(iterator.key(), iterator.value()));
		}

		if(gamesLimit > 0)
		{
			SchedulingPattern reusePattern = gameData.getReusePattern();
			player.setVar(LuckyGameData.PLAYED_LUCKY_GAMES_VAR + gameData.getGameId(), playedGamesCount + availableGames, reusePattern == null ? -1 : reusePattern.next(System.currentTimeMillis()));
		}

		int serverGamesCount = ServerVariables.getInt(LuckyGameData.LUCKY_GAMES_COUNT_VAR + gameData.getGameId(), 0);
		ServerVariables.set(LuckyGameData.LUCKY_GAMES_COUNT_VAR + gameData.getGameId(), serverGamesCount + availableGames);

		int personalGamesCount = player.getVarInt(LuckyGameData.LUCKY_GAMES_COUNT_VAR + gameData.getGameId(), 0);
		player.setVar(LuckyGameData.LUCKY_GAMES_COUNT_VAR + gameData.getGameId(), personalGamesCount + availableGames);

		TIntLongMap rewardsMap = new TIntLongHashMap();
		List<LuckyGameItem> rewards = new ArrayList<LuckyGameItem>();
		for(int i = 1; i <= availableGames; i++)
		{
			boolean uniqueReward = ((serverGamesCount + i) % Config.LUCKY_GAME_UNIQUE_REWARD_GAMES_COUNT) == 0;
			boolean additionalReward = ((personalGamesCount + i) % Config.LUCKY_GAME_ADDITIONAL_REWARD_GAMES_COUNT) == 0;

			LuckyGameItem reward = null;
			if(uniqueReward)
			{
				reward = LuckyGameData.rollItem(gameData.getUniqueRewards(), true);
				if(reward != null)
				{
					SystemMessagePacket sm;
					if(_gameId == 2)
						sm = new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_IN_THE_LUXURY_FORTUNE_READING);
					else
						sm = new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_THROUGH_FORTUNE_READING);
					
					sm.addName(player);
					sm.addItemName(reward.getId());
					sm.addLong(reward.getCount());

					for(Player p : GameObjectsStorage.getPlayers())
						p.sendPacket(sm);
				}
			}
			if(reward == null && additionalReward)
				reward = LuckyGameData.rollItem(gameData.getAdditionalRewards(), true);
			if(reward == null)
				reward = LuckyGameData.rollItem(gameData.getCommonRewards(), false);

			if(reward != null)
			{
				rewards.add(reward);
				rewardsMap.put(reward.getId(), rewardsMap.get(reward.getId()) + reward.getCount());

			}
		}

		for(TIntLongIterator iterator = rewardsMap.iterator(); iterator.hasNext(); )
		{
			iterator.advance();
			ItemFunctions.addItem(player, iterator.key(), iterator.value());
		}

		player.sendPacket(new ExBettingLuckyGameResult(player, gameData, rewards));
	}
}