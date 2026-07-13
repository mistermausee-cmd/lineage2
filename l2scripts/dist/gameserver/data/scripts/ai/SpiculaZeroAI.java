package ai;

import gnu.trove.map.TIntObjectMap;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author: Rivelia.
 */
public class SpiculaZeroAI extends Fighter
{
	/*
		15234	1	a,Dark Spheres\0	a,Shoot dark red spheres toward enemies within 120 degrees of you. Additional damage on PCs debuffed with Soul Drain.\0	a,none\0	a,none\0
		15235	1	a,Dark Wind\0	a,Create a strong wind around you to push back enemies.\0	a,none\0	a,none\0
		15236	1	a,Dark Thrust\0	a,Powerful attack on the target.\0	a,none\0	a,none\0
		15237	1	a,Dark Buster\0	a,Inflicts strong damage upon the enemies near the target.\0	a,none\0	a,none\0
		15238	1	a,Dark Breath\0	a,Absorb the souls of enemies within 180 degrees of you and decrease their P. Def. and M. Def. by 30%.\0	a,none\0	a,none\0
		skID 	sLv	op	U	mp	cs	cr	UNK_1	hit_time[0]	hit_time[1]	hit_time[2]	is_magic	UNK_2[0]	UNK_2[1]	UNK_2[2]	UNK_2[3]	UNK_2[4]	ani_char	desc	icon_name	icon_name2	extra_eff	is_ench	ench_skill_id	hp_consume	nonetext1	UNK_3[0]	UNK_3[1]	UNK_3[2]	UNK_3[3]	nonetext2
		15234	1	0	0	0	10	200	8	5.00000000	0.00000000	10.00000000	0	0	0	0	1	0	D	15234	icon.skill0003		0	0	0	0	a,none\0	0	9	11	0	a,none\0
		15235	1	0	0	1	10	-1	1	1.00000000	3.00000000	10.00000000	0	0	0	0	1	0	MP03	15235	icon.skill4107		0	0	0	0	a,none\0	0	9	11	0	a,none\0
		15236	1	0	0	0	10	100	1	2.00000000	0.00000000	0.00000000	0	0	0	0	1	0	MP01	15236	icon.skill0003		0	0	0	0	a,none\0	0	9	11	0	a,none\0
		15237	1	0	0	0	10	100	1	3.00000000	0.00000000	0.00000000	0	0	0	0	1	0	MP02	15237	icon.skill0036		0	0	0	0	a,none\0	0	9	11	0	a,none\0
		15238	1	0	0	1	10	250	4	3.00000000	0.00000000	0.00000000	1	0	0	0	1	0	f	15238	icon.skill11769		1	0	0	0	a,none\0	0	-1	-1	0	a,none\0
	*/
	// SKILLS INFOS.
	private final static Skill DARK_SPHERES = SkillHolder.getInstance().getSkill(15234, 1);
	private final static Skill DARK_WIND = SkillHolder.getInstance().getSkill(15235, 1);
	private final static Skill DARK_THRUST = SkillHolder.getInstance().getSkill(15236, 1);
	private final static Skill DARK_BUSTER = SkillHolder.getInstance().getSkill(15237, 1);
	private final static Skill DARK_BREATH = SkillHolder.getInstance().getSkill(15238, 1);

	// SKILLS CHANCES.
	private final static int CHANCE_DARK_SPHERES = 33;
	private final static int CHANCE_DARK_WIND = 66;
	private final static int CHANCE_DARK_THRUST = 33;
	private final static int CHANCE_DARK_BUSTER = 33;
	private final static int CHANCE_DARK_BREATH = 66;

	private Skill lastSkill = null;
	private Skill lastCastedSkill = null;

	public SpiculaZeroAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean createNewTask()
	{
		NpcInstance npc = getActor();
		// <= 50% HP, 5% chance to not cast a skill (95% chance to cast a skill).
		// > 50% HP, 50% chance to not cast a skill (50% chance to cast a skill).
		if(npc == null || npc.isCastingNow() || !Rnd.chance(npc.getCurrentHpPercents() > 50 ? 50 : 95))
			return super.createNewTask();

		clearTasks();

		// Determinate target.
		Creature target;
		if((target = prepareTarget()) == null || target == npc)
			return super.createNewTask();

		// Determinate distance to the target.
		double distance = npc.getDistance(target);

		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		Skill r_skill = null;

		if (Rnd.chance(CHANCE_DARK_SPHERES))
			addDesiredSkill(d_skill, target, distance, DARK_SPHERES);
		if (Rnd.chance(CHANCE_DARK_WIND))
			addDesiredSkill(d_skill, target, distance, DARK_WIND);
		if (Rnd.chance(CHANCE_DARK_THRUST))
			addDesiredSkill(d_skill, target, distance, DARK_THRUST);
		if (Rnd.chance(CHANCE_DARK_BUSTER))
			addDesiredSkill(d_skill, target, distance, DARK_BUSTER);
		if (Rnd.chance(CHANCE_DARK_BREATH))
			addDesiredSkill(d_skill, target, distance, DARK_BREATH);

		r_skill = selectTopSkill(d_skill);

		if (r_skill != null && ((lastCastedSkill != null && (r_skill != lastCastedSkill || Rnd.chance(25))) || lastCastedSkill == null))
		{
			lastSkill = r_skill;
			addTaskCast(target, r_skill);
			return true;
		}
		return super.createNewTask();
	}

	@Override
	protected boolean doTask()
	{
		lastCastedSkill = lastSkill;
		lastSkill = null;
		return super.doTask();
	}
}