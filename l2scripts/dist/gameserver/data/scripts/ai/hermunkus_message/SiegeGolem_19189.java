package ai.hermunkus_message;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import l2s.gameserver.utils.Location;

import instances.MemoryOfDisaster;

/**
 * @author : Ragnarok
 * @date : 31.03.12  12:18
 */
public class SiegeGolem_19189 extends DefaultAI
{
	private static final int SKILL_ID = 16024;
	private static final int[] ATTACK_IDS = {19172, 19217};

	private static final Location[] MOVE_LOC =
	{
			new Location(116560, -179440, -1144),
			new Location(116608, -179205, -1176)
	};

	private long lastCastTime = 0;
	private int diedTeredor = 0;
	private int currentPoint = -1;
	private Location loc;

	public SiegeGolem_19189(NpcInstance actor)
	{
		super(actor);
		_attackAITaskDelay = 50;
		_activeAITaskDelay = 250;
	}

	@Override
	protected void onEvtThink()
	{
		super.onEvtThink();

		NpcInstance actor = getActor();
		if(!actor.getAggroList().isEmpty())
		{
			int count = 0;
			List<Creature> targets = World.getAroundCharacters(actor);
			while(!targets.isEmpty())
			{
				count++;
				if(count > 1000)
				{
					//Log.debug("AI loop count exceeded, "+getActor()+" "+getActor().getLoc()+" "+targets);
					return;
				}

				Creature target = getNearestTarget(targets);
				if(target == null)
					break;

				if(actor.getAggroList().get(target) != null)
				{
					if(checkAggression(target))
					{
						Skill skill = SkillHolder.getInstance().getSkill(SKILL_ID, 1);
						if(lastCastTime + skill.getHitTime() + skill.getReuseDelay() <= System.currentTimeMillis())
						{
							lastCastTime = System.currentTimeMillis();
							addTaskCast(target, skill);
						}
					}
				}
			}
		}
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(_def_think)
		{
			doTask();
			return true;
		}

		if(diedTeredor < 3 || currentPoint >= MOVE_LOC.length - 1)
		{
			List<Creature> list = World.getAroundCharacters(getActor(), getActor().getAggroRange(), getActor().getAggroRange());
			for(Creature target : list)
			{
				if(target != null && !target.isDead() && ArrayUtils.contains(ATTACK_IDS, target.getNpcId()))
				{
					Skill sk = SkillHolder.getInstance().getSkill(SKILL_ID, 1);
					if(lastCastTime + sk.getHitTime() + sk.getReuseDelay() <= System.currentTimeMillis())
					{
						lastCastTime = System.currentTimeMillis();
						clearTasks();
						addTaskCast(target, sk);
						return true;
					}
					return false;
				}
			}
		}
		else if(diedTeredor >= 3 && currentPoint < MOVE_LOC.length - 1)
		{
			if(loc == null || getActor().getDistance(loc) <= 100)
			{
				currentPoint++;
				loc = new Location(MOVE_LOC[currentPoint].getX() + Rnd.get(50) - Rnd.get(50),
						MOVE_LOC[currentPoint].getY() + Rnd.get(50) - Rnd.get(50),
						MOVE_LOC[currentPoint].getZ() + Rnd.get(50) - Rnd.get(50)
				);
				if(currentPoint == 0)
				{
					Reflection r = getActor().getReflection();
					if(r instanceof MemoryOfDisaster)
					{
						((MemoryOfDisaster) r).spawnTransparentTeredor();
					}
				}
			}
			actor.setWalking();
			clearTasks();
			addTaskMove(loc, true);
			doTask();
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtFinishCasting(Skill skill, Creature target, boolean success)
	{
		if(success && skill.getId() == SKILL_ID)
		{
			getActor().broadcastPacket(new EarthQuakePacket(getActor().getLoc(), 50, 4));
		}
	}

	@Override
	protected void onEvtScriptEvent(String event, Object arg1, Object arg2)
	{
		super.onEvtScriptEvent(event, arg1, arg2);
		if(event.equalsIgnoreCase("TEREDOR_DIE"))
		{
			diedTeredor++;
		}
	}


	@Override
	public boolean canAttackCharacter(Creature target)
	{
		return ArrayUtils.contains(ATTACK_IDS, target.getNpcId());
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return ArrayUtils.contains(ATTACK_IDS, target.getNpcId());
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		return false;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}
}