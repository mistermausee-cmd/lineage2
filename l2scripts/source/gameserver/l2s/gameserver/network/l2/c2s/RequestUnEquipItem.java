package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;


public class RequestUnEquipItem extends L2GameClientPacket
{
	private int _slot;

	
	@Override
	protected void readImpl()
	{
		_slot = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		
		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		
		if((_slot == ItemTemplate.SLOT_R_HAND || _slot == ItemTemplate.SLOT_L_HAND || _slot == ItemTemplate.SLOT_LR_HAND) && (activeChar.isCursedWeaponEquipped() || activeChar.getActiveWeaponFlagAttachment() != null))
			return;

		if(_slot == ItemTemplate.SLOT_R_HAND)
		{
			ItemInstance weapon = activeChar.getActiveWeaponInstance();
			if(weapon == null)
				return;
			activeChar.abortAttack(true, true);
			activeChar.abortCast(true, true);
			activeChar.sendDisarmMessage(weapon);
		}

		activeChar.getInventory().unEquipItemInBodySlot(_slot);
	}
}