package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.SpecialMonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class MelonInstance extends SpecialMonsterInstance
{
	private static final long serialVersionUID = 1L;

	private static final int ev_unripe_watermelon = 13271;	// Молодой Арбуз
	private static final int ev_bad_watermelon = 13272;	// Завядший Арбуз
	private static final int ev_great_watermelon = 13273;	// Превосходный Арбуз
	private static final int ev_kgreat_watermelon = 13274;	// Огромный Превосходный Арбуз
	private static final int ev_unripe_h_watermelon = 13275;	// Молодой Сладкий Арбуз
	private static final int ev_bad_h_watermelon = 13276;	// Завядший Сладкий Арбуз
	private static final int ev_great_h_watermelon = 13277;	// Превосходный Сладкий Арбуз
	private static final int ev_kgreat_h_watermelon = 13278;	// Огромный Превосходный Сладкий Арбуз

	public MelonInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(getNpcId() == ev_bad_h_watermelon || getNpcId() == ev_great_h_watermelon || getNpcId() == ev_kgreat_h_watermelon)
		{
			// Разрешенное оружие для больших тыкв:
			// 4202 Chrono Cithara
			// 5133 Chrono Unitus
			// 5817 Chrono Campana
			// 7058 Chrono Darbuka
			// 8350 Chrono Maracas
			int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
			if(weaponId != 4202 && weaponId != 5133 && weaponId != 5817 && weaponId != 7058 && weaponId != 8350)
				return;

			damage = 1;
		}
		else if(getNpcId() == ev_great_watermelon || getNpcId() == ev_bad_watermelon || getNpcId() == ev_kgreat_watermelon)
		{
			damage = 5;
		}
		else
			return;

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}
}