package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.luckygame.LuckyGameData;


public final class LuckyGameHolder extends AbstractHolder
{
	private static final LuckyGameHolder _instance = new LuckyGameHolder();

	private TIntObjectMap<LuckyGameData> _data = new TIntObjectHashMap<LuckyGameData>();

	public static LuckyGameHolder getInstance()
	{
		return _instance;
	}

	public void addData(LuckyGameData data)
	{
		if(_data.containsKey(data.getGameId()))
		{
			warn("Conflict while parsing lucky game data! Dublicate game data by id: " + data.getGameId());
			return;
		}
		if(data.getCommonRewards().isEmpty())
		{
			warn("Lucky game dont have common rewards id: " + data.getGameId());
		}
		_data.put(data.getGameId(), data);
	}

	public LuckyGameData getData(int id)
	{
		return _data.get(id);
	}

	@Override
	public int size()
	{
		return _data.size();
	}

	@Override
	public void clear()
	{
		_data.clear();
	}
}