package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Craft
 * @reworked by Bonux
**/
public final class AltarOfTheSpiritsInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// Item's
	private static final int APPARATION_STONE_LV_88 = 38572;	// Призрачный Камень - 88 Ур.
	private static final int APPARATION_STONE_LV_93 = 38573;	// Призрачный Камень - 93 Ур.
	private static final int APPARATION_STONE_LV_98 = 38574;	// Призрачный Камень - 98 Ур.
	private static final int APPARATION_STONE_LV__88_93_98 = 26517;	// Призрачный Камень - Универсальный.

	// NPC's
	private static final int EARTH_TERAKAN_RAID_BOSS = 25944;	// Теракан Земли - Рейдовый Босс
	private static final int WIND_CASSIUS_RAID_BOSS = 25943;	// Гесиос Ветра - Рейдовый Босс
	private static final int FLAMING_LADAR_RAID_BOSS = 25942;	// Ратар Пламени - Рейдовый Босс

	private NpcInstance _bossNpc = null;

	public AltarOfTheSpiritsInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("requestboss"))
		{
			if(!st.hasMoreTokens())
				return;

			if(_bossNpc != null && !_bossNpc.isDead())
			{
				showChatWindow(player, "default/" + getNpcId() + "-already_spawned.htm", false);
				return;
			}

			int cmdId = Integer.parseInt(st.nextToken());
			switch(cmdId)
			{
				case 1:
				{
					if(ItemFunctions.deleteItem(player, APPARATION_STONE_LV_88, 1, true) || ItemFunctions.deleteItem(player, APPARATION_STONE_LV__88_93_98, 1, true))
					{
						_bossNpc = NpcUtils.spawnSingle(EARTH_TERAKAN_RAID_BOSS, Location.coordsRandomize(getLoc(), 300, 600), getReflection());
						showChatWindow(player, "default/" + getNpcId() + "-spawned_88.htm", false);
					}
					else
						showChatWindow(player, "default/" + getNpcId() + "-no_item_88.htm", false);
					break;
				}
				case 2:
				{
					if(ItemFunctions.deleteItem(player, APPARATION_STONE_LV_93, 1, true) || ItemFunctions.deleteItem(player, APPARATION_STONE_LV__88_93_98, 1, true))
					{
						_bossNpc = NpcUtils.spawnSingle(WIND_CASSIUS_RAID_BOSS, Location.coordsRandomize(getLoc(), 300, 600), getReflection());
						showChatWindow(player, "default/" + getNpcId() + "-spawned_93.htm", false);
					}
					else
						showChatWindow(player, "default/" + getNpcId() + "-no_item_93.htm", false);
					break;
				}
				case 3:
				{
					if(ItemFunctions.deleteItem(player, APPARATION_STONE_LV_98, 1, true) || ItemFunctions.deleteItem(player, APPARATION_STONE_LV__88_93_98, 1, true))
					{
						_bossNpc = NpcUtils.spawnSingle(FLAMING_LADAR_RAID_BOSS, Location.coordsRandomize(getLoc(), 300, 600), getReflection());
						showChatWindow(player, "default/" + getNpcId() + "-spawned_98.htm", false);
					}
					else
						showChatWindow(player, "default/" + getNpcId() + "-no_item_98.htm", false);
					break;
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}