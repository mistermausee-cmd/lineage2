package ai.octavis;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @reworked by Bonux
**/
public class OctavisThirdAI extends Fighter
{
	// Location's
	private static final Location LAIR_CENTER = new Location(207190, 120574, -10009);

	// NPC's
	private static final int FIRE_REGION_NPC_ID = 19161;
	private static final int OCTAVIS_INFLUENCE_NPC_ID = 18984;

	// Skill ID's
	private static final int OCTAVIS_POWER1_SKILL_ID = 14028;
	private static final int OCTAVIS_POWER2_SKILL_ID = 14029;

	public OctavisThirdAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(Rnd.chance(3))
		{
			Reflection reflection = getActor().getReflection();
			NpcInstance volcano = NpcUtils.spawnSingle(FIRE_REGION_NPC_ID, LAIR_CENTER, reflection, 10000L);
			if(Rnd.chance(50))
			{
				volcano.doCast(SkillHolder.getInstance().getSkillEntry(OCTAVIS_POWER1_SKILL_ID, 1), volcano, true);

				List<NpcInstance> octavisPowers = reflection.getAllByNpcId(OCTAVIS_INFLUENCE_NPC_ID, true);
				for(NpcInstance octavisPower : octavisPowers)
					octavisPower.setNpcState((octavisPower.getNpcState() + 1) % 7);
			}
			else
				volcano.doCast(SkillHolder.getInstance().getSkillEntry(OCTAVIS_POWER2_SKILL_ID, 1), volcano, true);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		return false;
	}
}