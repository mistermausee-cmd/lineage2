package l2s.gameserver.model.items.listeners;

import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.inventory.OnEquipListener;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.skills.SkillEntry;

public final class JewelsListener implements OnEquipListener
{
	private static final IntSet TOP_GRADE_JEWELS = new HashIntSet();
  
	static
	{
		TOP_GRADE_JEWELS.add(28376);
		TOP_GRADE_JEWELS.add(28377);
		TOP_GRADE_JEWELS.add(28378);
		TOP_GRADE_JEWELS.add(28379);
		TOP_GRADE_JEWELS.add(28380);
		TOP_GRADE_JEWELS.add(28383);
		TOP_GRADE_JEWELS.add(47687);
		TOP_GRADE_JEWELS.add(47688);
		TOP_GRADE_JEWELS.add(47689);
		TOP_GRADE_JEWELS.add(47690);
		TOP_GRADE_JEWELS.add(47691);
		TOP_GRADE_JEWELS.add(47692);
		TOP_GRADE_JEWELS.add(47693);
		TOP_GRADE_JEWELS.add(47694);
		TOP_GRADE_JEWELS.add(47695);
		TOP_GRADE_JEWELS.add(47696);
		TOP_GRADE_JEWELS.add(47697);
		TOP_GRADE_JEWELS.add(47698);
		TOP_GRADE_JEWELS.add(47699);
		TOP_GRADE_JEWELS.add(47700);
	}
  
	private static final JewelsListener _instance = new JewelsListener();
  
	public static JewelsListener getInstance()
	{
		return _instance;
	}
  
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;
		if(!actor.isPlayer())
			return;
		if(!TOP_GRADE_JEWELS.contains(item.getItemId()))
			return;
		checkEquippedJewels(actor.getPlayer());
	}
  
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;
		if(!actor.isPlayer())
			return;
		if(!TOP_GRADE_JEWELS.contains(item.getItemId()))
			return;
		checkEquippedJewels(actor.getPlayer());
	}
  
	private void checkEquippedJewels(Player player)
	{
		player.removeSkill(27717, false);
		player.removeSkill(27721, false);
		player.removeSkill(27722, false);
		player.removeSkill(27723, false);
		player.removeSkill(27724, false);
    
		Inventory inv = player.getInventory();
    
		IntSet equippedJewels = new HashIntSet();
		for(int slotId = 27; slotId <= 32; slotId++)
		{
			ItemInstance jewelItem = inv.getPaperdollItem(slotId);
			if(jewelItem != null && TOP_GRADE_JEWELS.contains(jewelItem.getItemId()))
				equippedJewels.add(jewelItem.getItemId());
		}
		int skillLevel = Math.min(6, equippedJewels.size());
		if(skillLevel > 0)
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(27717, skillLevel);
			if(skillEntry != null)
				player.addSkill(skillEntry, false);
			
			int activeSkillId = 0;
			if(skillLevel == 6)
				activeSkillId = 27724;
			else if(skillLevel == 5)
				activeSkillId = 27723;
			else if(skillLevel == 4)
				activeSkillId = 27722;
			else if(skillLevel == 3)
				activeSkillId = 27721;
			
			if(activeSkillId > 0)
			{
				skillEntry = SkillHolder.getInstance().getSkillEntry(activeSkillId, 1);
				if (skillEntry != null)
					player.addSkill(skillEntry, false);
			}
		}
		player.sendSkillList();
		player.updateStats();
	}
}