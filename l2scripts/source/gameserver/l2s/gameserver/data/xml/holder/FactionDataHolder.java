package l2s.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.FactionType;

public final class FactionDataHolder extends AbstractHolder
{
    private static final FactionDataHolder _instance = new FactionDataHolder();
    private TIntObjectHashMap<TIntIntHashMap> _data = new TIntObjectHashMap<TIntIntHashMap>();

    public static FactionDataHolder getInstance()
    {
        return _instance;
    }

    public void addData(FactionType type, TIntIntHashMap data)
    {
        _data.put(type.ordinal(), data);
    }

    public int getLevel(FactionType type, int points)
    {
        int level = 0;
        for (TIntIntIterator iterator = _data.get(type.ordinal()).iterator(); iterator.hasNext();)
        {
          iterator.advance();
          if ((iterator.key() > level) && (iterator.value() <= points))
            level = iterator.key();
        }
        return level;
    }

    public int getPoints(FactionType type, int level)
    {
        return (_data.get(type.ordinal())).get(level);
    }

    public int size()
    {
        return _data.size();
    }

    public void clear()
    {
        _data.clear();
    }
}