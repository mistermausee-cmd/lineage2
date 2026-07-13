package l2s.gameserver.model.instances;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.string.StringArrayUtils;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.SkillUtils;


public class TreeInstance extends SummonInstance
{
	private class HealTask extends RunnableImpl
	{
		private Skill _skill;

		public HealTask(Skill skill)
		{
			_skill = skill;
		}

		@Override
		public void runImpl() throws Exception
		{
			TimeStamp ts = getSkillReuse(_skill);
			if(ts != null && ts.getReuseCurrent() > 0)
				return;

			getAI().Cast(_skill, TreeInstance.this, false, false);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final String SKILL_DELAY_PARAMETER = "skill_delay";
	private static final String HEAL_SKILL_PARAMETER = "s_tree_heal";

	private ScheduledFuture<?> _healTask;

	private final int _skillDelay;
	private final Skill _healSkill;

	public TreeInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, int summonPoints, Skill skill, boolean saveable)
	{
		super(objectId, template, owner, lifetime, consumeid, consumecount, consumedelay, summonPoints, skill, saveable);

		_skillDelay = template.getAIParams().getInteger(SKILL_DELAY_PARAMETER, 1) * 1000;

		int[] skillArr = StringArrayUtils.stringToIntArray(template.getAIParams().getString(HEAL_SKILL_PARAMETER, ""), "-");
		if(skillArr.length > 0)
		{
			int skillLvl = (skillArr.length > 1) ? skillArr[1] : 1;
			int skillSubLvl = (skillArr.length > 2) ? skillArr[2] : 0;
			_healSkill = SkillHolder.getInstance().getSkill(skillArr[0], SkillUtils.getSkillLevelMask(skillLvl, skillSubLvl));
		}
		else
			_healSkill = null;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		if(_healSkill == null)
			return;

		_healTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(_healSkill), 0L, _skillDelay);
	}

	@Override
	protected void onDelete()
	{
		stopHealTask();
		super.onDelete();
	}

	@Override
	public boolean isImmobilized()
	{
		return true;
	}

	private void stopHealTask()
	{
		if(_healTask != null)
		{
			_healTask.cancel(false);
			_healTask = null;
		}
	}
}