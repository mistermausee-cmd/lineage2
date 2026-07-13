package ai.octavis;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.WalkerRouteType;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @reworked by Bonux
**/
public class OctavisBeastAI extends Fighter
{
	// Skill ID's
	private static final int CHAIN_STRIKE_SKILL_ID = 10015;
	private static final int CHAIN_HYDRA_SKILL_ID = 10016;
	private static final int BEAST_HERO_MOVEMENT_SKILL_ID = 14023;
	private static final int BEAST_ANCIENT_POWER_SKILL_ID = 14024;

	private static final Location[] OCTAVIS_MOVE_POINTS = {
		new Location(207992, 120904, -10038),
		new Location(207528, 121384, -10038),
		new Location(206856, 121368, -10038),
		new Location(206376, 120904, -10038),
		new Location(206376, 120248, -10038),
		new Location(206856, 119768, -10037),
		new Location(207528, 119768, -10038),
		new Location(208008, 120232, -10037)
	};

	public OctavisBeastAI(NpcInstance actor)
	{
		super(actor);
		setWalkerRoute(NpcUtils.makeWalkerRoute(OCTAVIS_MOVE_POINTS, true, WalkerRouteType.ROUND));
	}

	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		//
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		notifyFriends(attacker, skill, damage);
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		//
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster, Creature target)
	{
		int skillId = skill.getId();
		if(skillId == CHAIN_STRIKE_SKILL_ID || skillId == CHAIN_HYDRA_SKILL_ID)
		{
			if(Rnd.chance(40))
			{
				Skill castSkill;
				if(Rnd.chance(50))
					castSkill = SkillHolder.getInstance().getSkill(BEAST_HERO_MOVEMENT_SKILL_ID, 1);
				else
					castSkill = SkillHolder.getInstance().getSkill(BEAST_ANCIENT_POWER_SKILL_ID, 1);

				getActor().forceUseSkill(castSkill, caster);
			}
		}
	}
}