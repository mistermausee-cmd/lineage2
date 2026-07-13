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
 * Date: 02.06.2008
 * Time: 12:19:36
 */
public class fenrir
{
	private static final int GREAT_WOLF = PetDataHolder.GREAT_WOLF_ID;
	private static final int FENRIR_WOLF = PetDataHolder.FENRIR_WOLF_ID;
	private static final int GREAT_WOLF_NECKLACE = 9882;
	private static final int FENRIR_NECKLACE = 10426;

	@Bypass("services.petevolve.fenrir:evolve")
	public void evolve(Player player, NpcInstance npc, String[] param)
	{
		if(player.getInventory().getItemByItemId(GREAT_WOLF_NECKLACE) == null)
		{
			Functions.show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		PetInstance pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			Functions.show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != GREAT_WOLF)
		{
			Functions.show("scripts/services/petevolve/no_wolf.htm", player, npc);
			return;
		}
		if(pl_pet.getLevel() < 70)
		{
			Functions.show("scripts/services/petevolve/no_level_gw.htm", player, npc);
			return;
		}

		int controlItemId = pl_pet.getControlItemObjId();
		pl_pet.unSummon(false);

		NpcTemplate template = NpcHolder.getInstance().getTemplate(FENRIR_WOLF);
		if(template == null)
			return;

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(FENRIR_NECKLACE);
		control.setEnchantLevel(template.level);
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();

		player.sendItemList(false);

		Functions.show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}
}