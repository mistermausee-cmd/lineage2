package npc.model.residences.fortress;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * @author VISTALL
 * @date 8:44/18.04.2011
 */
public abstract class FacilityManagerInstance extends NpcInstance
{
	public FacilityManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	protected boolean buyFacility(Player player, int type, int lvl, long price)
	{
		Fortress fortress = getFortress();

		if((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
		{
			showChatWindow(player, "residence2/fortress/fortress_not_authorized.htm", false);
			return false;
		}

		if(fortress.getContractState() != Fortress.CONTRACT_WITH_CASTLE)
		{
			showChatWindow(player, "residence2/fortress/fortress_supply_officer005.htm", false);
			return false;
		}

		if(fortress.getFacilityLevel(type) >= lvl)
		{
			showChatWindow(player, "residence2/fortress/fortress_already_upgraded.htm", false);
			return false;
		}

		if(player.consumeItem(ItemTemplate.ITEM_ID_ADENA, price, true))
		{
			fortress.setFacilityLevel(type, lvl);
			fortress.setJdbcState(JdbcEntityState.UPDATED);
			fortress.update();

			showChatWindow(player, "residence2/fortress/fortress_supply_officer006.htm", false);
			return true;
		}
		else
		{
			showChatWindow(player, "residence2/fortress/fortress_not_enough_money.htm", false);
			return false;
		}
	}
}
