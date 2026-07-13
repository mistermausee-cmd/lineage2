package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.item.AlchemyDataTemplate;
import l2s.gameserver.utils.SkillUtils;


public final class AlchemyDataHolder extends AbstractHolder
{
	private static final AlchemyDataHolder _instance = new AlchemyDataHolder();

	private final TIntObjectMap<AlchemyDataTemplate> _datas = new TIntObjectHashMap<AlchemyDataTemplate>();

	public static AlchemyDataHolder getInstance()
	{
		return _instance;
	}

	public void addData(AlchemyDataTemplate data)
	{
		_datas.put(SkillUtils.generateSkillHashCode(data.getSkillId(), data.getSkillLevel()), data);
	}

	public AlchemyDataTemplate getData(Skill skill)
	{
		return _datas.get(skill.hashCode());
	}

	public AlchemyDataTemplate getData(int skillId, int skillLevel)
	{
		return _datas.get(SkillUtils.generateSkillHashCode(skillId, skillLevel));
	}

	@Override
	public int size()
	{
		return _datas.size();
	}

	@Override
	public void clear()
	{
		_datas.clear();
	}
}