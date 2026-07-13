package npc.model;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;

import instances.LindviorBoss;

public class GeneratorLindviorInstance extends MonsterInstance
{
	public GeneratorLindviorInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	private static boolean blockedChargeBlocked = true;
	private ScheduledFuture<?> _checkState;
	public static final SkillEntry SKILL = SkillHolder.getInstance().getSkillEntry(15605, 1);
	
	@Override
	protected void onSpawn()
	{
		_checkState = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckState(this), 3000, 3000);
	}	
	
	private class CheckState implements Runnable
	{
		NpcInstance _npc;
		public CheckState(NpcInstance npc)
		{
			_npc = npc;
		}
		@Override
		public void run()
		{
			/*for(NpcInstance mob : World.getAroundNpc(_npc, 700, 700))
			{
				if(mob != null && mob.isMonster() && !mob.isDead())
				{
					blockedChargeBlocked = true;
					setNpcState(2);
					return;
				}	
			}//if not found we presume that no need to continue
			*/

			//blockedChargeBlocked = false;
			if(!getAbnormalList().contains(SKILL.getTemplate()) && !isCastingNow() && getNpcState() != 0x02) //to avoid flood
			{
				setNpcState(1);
				doCast(SKILL, _npc, false);
			}
		}
	}		

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		Reflection r = getReflection();	
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;	
				lInst.endInstance();
			}	
		}		
	}
	
	@Override
	public boolean isChargeBlocked()
	{
		if(getNpcState() == 0x02)
			return true;
		return !getAbnormalList().contains(SKILL.getTemplate());
	}
	
	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		if(attacker.getPlayer() != null)
			return;
		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);	
	}	
}