package ai.spirits_garden;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class GardenFighter extends Fighter
{
	private static final int[] MONSTERS_DROP = {23797, 23559, 23560};
	private static final int[] MONSTERS_NOT = {19647, 23553, 23544};

	public GardenFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(Rnd.chance(20) && !ArrayUtils.contains(MONSTERS_NOT, getActor().getNpcId()))
			NpcUtils.spawnSingle(Rnd.get(MONSTERS_NOT), getActor().getX() + Rnd.get(100, 500), getActor().getY() + Rnd.get(100, 500), getActor().getZ());
		if(Rnd.chance(50) && getActor().getNpcId() == 19647)
			NpcUtils.spawnSingle(Rnd.get(MONSTERS_DROP), getActor().getX() + Rnd.get(100, 500), getActor().getY() + Rnd.get(100, 500), getActor().getZ());
		if(Rnd.chance(35) && (getActor().getNpcId() == 23544 || getActor().getNpcId() == 23553))
			NpcUtils.spawnSingle(23545, getActor().getX() + Rnd.get(100, 500), getActor().getY() + Rnd.get(100, 500), getActor().getZ());
	}
}