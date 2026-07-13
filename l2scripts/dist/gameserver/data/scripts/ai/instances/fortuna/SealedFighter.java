package ai.instances.fortuna;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.actor.OnMoveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class SealedFighter extends FortunaFighter implements OnMoveListener
{
	private static final int LIGHTNING_SPHERE_BLUE = 19082; // Световая Сфера (Синяя)
	private static final int LIGHTNING_SPHERE_ORANGE = 19083; // Световая Сфера (Оранжевая)

	private int _lightningObjectId = 0;

	public SealedFighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		getActor().addListener(this);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if(_lightningObjectId > 0)
			return;

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		if(_lightningObjectId > 0)
			return;

		super.onEvtAggression(attacker, aggro);
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		onMove(getActor(), getActor().getLoc());
	}

	@Override
	public void onMove(Creature actor, Location loc)
	{
		/*TODO [Bonux]: Доделать по оффу.
		NpcInstance npcActor = getActor();
		if(npcActor.getAggroList().isEmpty() || Rnd.chance(100))
		{
			List<NpcInstance> npcs = npcActor.getAroundNpc(900, 200);
			for(NpcInstance npc : npcs)
			{
				if(npc.getNpcId() == LIGHTNING_SPHERE_BLUE && (_lightningObjectId == 0 || _lightningObjectId == npc.getObjectId()))
				{
					if(npcActor.getDistance(npc) < 150)
					{
						_lightningObjectId = 0;
						npc.deleteMe();
						NpcUtils.spawnSingle(LIGHTNING_SPHERE_ORANGE, npc.getLoc(), npc.getReflection());
					}
					else if(_lightningObjectId == 0 || _lightningObjectId == npc.getObjectId())
					{
						_lightningObjectId = npc.getObjectId();
						setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
						npcActor.moveToLocation(npc.getLoc(), 20, true);
					}
				}
				break;
			}
		}*/
	}
}
