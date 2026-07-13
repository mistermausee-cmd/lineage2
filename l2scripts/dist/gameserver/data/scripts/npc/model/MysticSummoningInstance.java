package npc.model;

import ai.MysticTavernEmployeeAI;

import java.util.List;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public final class MysticSummoningInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private final int _tableId;

	public MysticSummoningInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_tableId = set.getInteger("table_id");
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -1803)
		{
			if(reply == 1)
			{
				Party party = player.getParty();
				if(party == null || !party.isLeader(player))
					showChatWindow(player, "default/ep_pub_control010.htm", false);
				else
				{
					NpcInstance employee = getEmployeeNpc();
					if(employee == null)
						return;

					if(!(employee.getAI() instanceof MysticTavernEmployeeAI))
						return;

					MysticTavernEmployeeAI employeeAI = (MysticTavernEmployeeAI) employee.getAI();
					if(!employeeAI.call(_tableId, party))
						showChatWindow(player, "default/ep_pub_control011.htm", false);
				}
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "default/ep_pub_control001.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	private NpcInstance getEmployeeNpc()
	{
		int npcId;
		switch(_tableId)
		{
			case 1:
				npcId = 34186; // Mei
				break;
			case 2:
				npcId = 34185; // Lupia
				break;
			case 3:
				npcId = 34184; // Brodien
				break;
			case 4:
				npcId = 34182; // Lollia
				break;
			case 5:
				npcId = 34183; // Hanna
				break;
			default:
				return null;
		}

		NpcInstance npc = null;

		List<NpcInstance> npcs = GameObjectsStorage.getAllByNpcId(npcId, true);
		for(NpcInstance n : npcs)
		{
			if(npc == null || n.getDistance(this) < npc.getDistance(this))
				npc = n;
		}
		return npc;
	}
}
