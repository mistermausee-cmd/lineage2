package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class ChaosHornInstance extends MonsterInstance
{
	public static enum HornType
	{
		NORMAL,
		RED,
		GREEN,
		BLUE;
	}

	private final HornType _type;

    public ChaosHornInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
    {
        super(objectId, template, set);
	
		_type = HornType.valueOf(getParameter("horn_type", "NORMAL").toUpperCase());
    }

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(_type == HornType.NORMAL)
			super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	public HornType getHornType()
	{
		return _type;
	}
}
