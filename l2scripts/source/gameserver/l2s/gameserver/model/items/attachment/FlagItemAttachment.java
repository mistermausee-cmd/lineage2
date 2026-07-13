package l2s.gameserver.model.items.attachment;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;


public interface FlagItemAttachment extends PickableAttachment
{
	
	void onLogout(Player player);

	
	void onDeath(Player owner, Creature killer);

	void onLeaveSiegeZone(Player player);

	boolean canAttack(Player player);

	boolean canCast(Player player, Skill skill);
}