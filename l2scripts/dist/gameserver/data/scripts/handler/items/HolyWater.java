package handler.items;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.PositionUtils;
import npc.model.HellboundRemnantInstance;

public class HolyWater extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		GameObject target = player.getTarget();

		if(item.getItemId() == 9673)
		{
			if (target == null || !(target instanceof HellboundRemnantInstance))
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}

			HellboundRemnantInstance npc = (HellboundRemnantInstance) target;
			if (npc.isDead())
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}

			player.broadcastPacket(new MagicSkillUse(player, npc, 2358, 1, 0, 0));
			npc.onUseHolyWater(player);
		}
		else if(item.getItemId() == 47170)
		{
			int[] MONSTERS = {23574, 23575, 23576, 23577};
			if (target != null && target instanceof MonsterInstance)
			{
				if (target.isMonster() && ((MonsterInstance) target).getNpcId() == 19600)
				{
					player.broadcastPacket(new MagicSkillUse(player, player, 2358, 1, 0, 0)); //TODO FIND
					if(Rnd.chance(50))
					{
						NpcInstance npc = NpcUtils.spawnSingle(Rnd.get(MONSTERS), target.getLoc(), target.getReflection());
						npc.setHeading(PositionUtils.calculateHeadingFrom(npc, player));
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 1000);
					}
					target.deleteMe();
					return true;
				}
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		return true;
	}
}
