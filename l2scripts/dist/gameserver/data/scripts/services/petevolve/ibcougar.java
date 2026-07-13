package services.petevolve;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;

/**
 * User: darkevil
 * Date: 07.06.2008
 * Time: 16:28:42
 */
public class ibcougar
{
	private static final int BABY_COUGAR = PetDataHolder.BABY_COUGAR_ID;
	private static final int IMPROVED_BABY_COUGAR = PetDataHolder.IMPROVED_BABY_COUGAR_ID;
	private static final int BABY_COUGAR_CHIME = 6649;
	private static final int IMPROVED_COUGAR_CHIME = 10312;

	@Bypass("services.petevolve.ibcougar:evolve")
	public void evolve(Player player, NpcInstance npc, String[] param)
	{
		PetInstance pl_pet = player.getPet();
		if(player.getInventory().getItemByItemId(BABY_COUGAR_CHIME) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		if(pl_pet == null || pl_pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != BABY_COUGAR)
		{
			Functions.show("scripts/services/petevolve/no_pet.htm", player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			Functions.show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = pl_pet.getControlItemObjId();
		pl_pet.unSummon(false);

		NpcTemplate template = NpcHolder.getInstance().getTemplate(IMPROVED_BABY_COUGAR);
		if(template == null)
			return;

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(IMPROVED_COUGAR_CHIME);
		control.setEnchantLevel(template.level);
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();

		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_pet.htm", player, npc);
	}
}