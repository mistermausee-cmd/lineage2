package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.SkillEnchantInfo;


public final class SkillEnchantInfoHolder extends AbstractHolder
{
	private static final SkillEnchantInfoHolder _instance = new SkillEnchantInfoHolder();

	private TIntObjectMap<SkillEnchantInfo> _infos = new TIntObjectHashMap<SkillEnchantInfo>();

	public static SkillEnchantInfoHolder getInstance()
	{
		return _instance;
	}

	public void addInfo(SkillEnchantInfo group)
	{
		_infos.put(group.getEnchantLevel(), group);
	}

	public SkillEnchantInfo getInfo(int level)
	{
		return _infos.get(level);
	}

	@Override
	public int size()
	{
		return _infos.size();
	}

	@Override
	public void clear()
	{
		_infos.clear();
	}
}