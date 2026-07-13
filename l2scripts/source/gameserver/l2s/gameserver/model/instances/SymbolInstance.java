package l2s.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.string.StringArrayUtils;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.SkillUtils;


public class SymbolInstance extends NpcInstance
{
	private class SkillCast extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(isDead())
			{
				if(_castTask != null)
				{
					_castTask.cancel(false);
					_castTask = null;
				}
				return;
			}
			doCast(_unionSkills.get(0), null, false);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final String DESPAWN_TIME_PARAMETER = "despawn_time";
	private static final String SKILL_DELAY_PARAMETER = "skill_delay";
	private static final String UNION_SKILL_PARAMETER = "union_skill";

	private final int _despawnTime;
	private final int _skillDelay;
	private final List<SkillEntry> _unionSkills = new ArrayList<SkillEntry>();
	
	private ScheduledFuture<?> _castTask;
	private int _usedSkillIndx = 0;

	public SymbolInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_despawnTime = getParameter(DESPAWN_TIME_PARAMETER, 120) * 1000 + 100;
		_skillDelay = getParameter(SKILL_DELAY_PARAMETER, 1) * 1000;

		String skillsStr = getParameter(UNION_SKILL_PARAMETER, null);
		if(skillsStr != null)
		{
			int[][] skills = StringArrayUtils.stringToIntArray2X(skillsStr, ";", "-");
			for(int[] skill : skills)
			{
				int skillLvl = (skill.length > 1) ? skill[1] : 1;
		        int skillSubLvl = (skill.length > 2) ? skill[2] : 0;
				_unionSkills.add(SkillHolder.getInstance().getSkillEntry(skill[0], SkillUtils.getSkillLevelMask(skillLvl, skillSubLvl)));
			}
		}
	}

	public void setOwner(Player owner)
	{
        super.setOwner(owner);
		if(owner != null)
		{
			setLevel(owner.getLevel());
			setTitle(owner.getName());
		}
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

        startDeleteTask(_despawnTime);

        if(_unionSkills != null && !_unionSkills.isEmpty())
			_castTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SkillCast(), _skillDelay, _skillDelay);
	}

	@Override
	protected void onDelete()
	{
		Player owner = getPlayer();
		if(owner != null)
			owner.setSymbol(null);

		if(_castTask != null)
		{
			_castTask.cancel(false);
			_castTask = null;
		}

		super.onDelete();
	}

	@Override
	public void onCastEndTime(Creature aimingTarget, List<Creature> targets, boolean dual, boolean success)
	{
		super.onCastEndTime(aimingTarget, targets, dual, success);

		_usedSkillIndx++;

		if(_usedSkillIndx >= _unionSkills.size())
			_usedSkillIndx = 0;

		if(_usedSkillIndx == 0)
			return;

		doCast(_unionSkills.get(_usedSkillIndx), null, false);
	}

	@Override
	public int getPAtk(Creature target) 
	{
		Player owner = getPlayer();
		return owner == null ? 0 : owner.getPAtk(target);
	}

	@Override
	public int getMAtk(Creature target, Skill skill) 
	{
		Player owner = getPlayer();
		return owner == null ? 0 : owner.getMAtk(target, skill);
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isImmobilized()
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}

	@Override
	public boolean isHasChatWindow()
	{
		return false;
	}

	@Override
	public boolean isInvulnerable()
	{
		return !isTargetable(null);
	}

	@Override
	public boolean isEffectImmune(Creature caster)
	{
		return true;
	}

	@Override
	public boolean isSymbolInstance()
	{
		return true;
	}	
}