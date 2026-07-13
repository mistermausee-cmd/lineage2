package l2s.gameserver.ai;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;


public class SpecialTautiMonsters extends Fighter
{
	public SpecialTautiMonsters(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(!canAttack(actor.getNpcId(), attacker))
			return;

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		NpcInstance actor = getActor();

		return canAttack(actor.getNpcId(), target);
	}

	@Override
	protected boolean maybeMoveToHome(boolean force)
	{
		return returnHome(false);
	}

	@Override
	protected void onEvtArrived()
	{
	    super.onEvtArrived();
	    NpcInstance actor = getActor();
	    if(actor.isInRangeZ(actor.getSpawnedLoc(), 50))
	    	actor.setHeading((actor.getSpawnedLoc()).h, true); 
	}

	private boolean canAttack(int selfId, Creature target)
	{
		if(selfId == 33680 || selfId == 33679) 
		{
			if(target.isPlayable())
				return false;
			else 
				return target.isMonster();
		}

		if(target.isMonster())
		{
			MonsterInstance monster = (MonsterInstance) target;
			if(monster.getNpcId() == 19262 || monster.getNpcId() == 19263 || monster.getNpcId() == 19264 || monster.getNpcId() == 19265 || monster.getNpcId() == 19266)
				return false;
			else
				return true;
		}
		else
			return !target.isNpc();
				
	}
}