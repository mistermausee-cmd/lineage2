package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;

public class Transformation extends Skill
{
	public final boolean isDispell;

	public Transformation(StatsSet set)
	{
		super(set);
		isDispell = set.getBool("is_dispel", false);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		Player player = activeChar.getPlayer();
		if(player == null || player.getActiveWeaponFlagAttachment() != null)
			return false;

		if(player.isTransformed() && !isDispell)
		{
			
			activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
			return false;
		}

		
		if((getId() == SKILL_FINAL_FLYING_FORM || getId() == SKILL_AURA_BIRD_FALCON || getId() == SKILL_AURA_BIRD_OWL) && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || player.hasServitor() || !player.getReflection().isMain()))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		
		if(player.isInFlyingTransform() && isDispell && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		if(!Config.ALT_USE_TRANSFORM_IN_EPIC_ZONE && !isDispell && player.isInZone(Zone.ZoneType.epic))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
			return false;
		}

		if(player.isInWater())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
			return false;
		}

		if(player.isMounted())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
			return false;
		}

		
		if(player.getAbnormalList().contains(Skill.SKILL_MYSTIC_IMMUNITY))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
			return false;
		}

		if(player.isInBoat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
			return false;
		}

		if(player.getPet() != null && !isDispell)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET);
			return false;
		}

		return true;
	}
}