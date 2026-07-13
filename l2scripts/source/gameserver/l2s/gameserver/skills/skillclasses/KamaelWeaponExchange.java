package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.ShortCutInitPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.WeaponTemplate;

public class KamaelWeaponExchange extends Skill
{
	public KamaelWeaponExchange(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		Player p = (Player) activeChar;
		if(p.isInStoreMode() || p.isProcessingRequest())
			return false;

		ItemInstance item = activeChar.getActiveWeaponInstance();
		if(item != null && ((WeaponTemplate) item.getTemplate()).getKamaelConvert() == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CONVERT_THIS_ITEM);
			return false;
		}

		return true;
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

		final Player player = (Player) activeChar;
		final ItemInstance item = activeChar.getActiveWeaponInstance();
		if(item == null)
			return;

		final int itemId = ((WeaponTemplate) item.getTemplate()).getKamaelConvert();
		if(itemId == 0)
			return;

		player.getInventory().unEquipItem(item);
		player.sendPacket(new InventoryUpdatePacket().addRemovedItem(player, item));
		item.setItemId(itemId);

		player.sendPacket(new ShortCutInitPacket(player));
		player.sendActiveAutoShots();
		player.sendPacket(new InventoryUpdatePacket().addNewItem(player, item));
		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemNameWithAugmentation(item));
		player.getInventory().equipItem(item);
	}
}