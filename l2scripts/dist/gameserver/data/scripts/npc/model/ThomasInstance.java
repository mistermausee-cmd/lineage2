package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import events.SavingSnowman.SavingSnowman;

/**
 * Данный инстанс используется мобом Thomas D. Turkey в эвенте Saving Snowman
 * @author SYS
 */
public class ThomasInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public ThomasInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		i = 10;
		if(attacker.getActiveWeaponInstance() != null)
			switch(attacker.getActiveWeaponInstance().getItemId())
			{
				// Хроно оружие наносит больший урон
				case 4202: // Chrono Cithara
				case 5133: // Chrono Unitus
				case 5817: // Chrono Campana
				case 7058: // Chrono Darbuka
				case 8350: // Chrono Maracas
					i = 100;
					break;
				default:
					i = 10;
			}

		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Creature topdam = getAggroList().getTopDamager(killer);
		SavingSnowman.freeSnowman(topdam);
		super.onDeath(killer);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}