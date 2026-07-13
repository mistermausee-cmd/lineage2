package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;

public class VitalityUse extends Skill
{
	public VitalityUse(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(target.isPlayer())
		{
			final Player player = target.getPlayer();
			if(player.getVitalityPotionsLeft() == player.getVitalityPotionsLimit())
			{	
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		final Player player = target.getPlayer();
		final int points = (int) this.getPower();
		player.setUsedVitalityPotions((player.getUsedVitalityPotions() - points), true);
		player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NUMBER_OF_VITALITY_EFFECTS_USABLE_DURING_THIS_PERIOD_HAS_INCREASED_BY_S1_YOU_CAN_CURRENTLY_USE_S2_VITALITY_ITEMS).addInteger(points).addInteger(player.getVitalityPotionsLeft()));
	}
}