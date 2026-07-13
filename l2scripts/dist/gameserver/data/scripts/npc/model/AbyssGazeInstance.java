package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.SoIManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import instances.HeartInfinityAttack;
import instances.HeartInfinityDefence;

/**
 * @author Bonux
 */
public final class AbyssGazeInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Location RETURN_LOCATION = new Location(-212836, 209824, 4288);

	private static final Location LOCATION_1 = new Location(-179537, 209551, -15504);
	private static final Location LOCATION_2 = new Location(-179779, 212540, -15520);
	private static final Location LOCATION_3 = new Location(-177028, 211135, -15520);
	private static final Location LOCATION_4 = new Location(-176355, 208043, -15520);
	private static final Location LOCATION_5 = new Location(-179284, 205990, -15520);
	private static final Location LOCATION_6 = new Location(-182268, 208218, -15520);
	private static final Location LOCATION_7 = new Location(-182069, 211140, -15520);
	private static final Location LOCATION_8 = new Location(-176036, 210002, -11948);
	private static final Location LOCATION_9 = new Location(-176039, 208203, -11949);
	private static final Location LOCATION_10 = new Location(-183288, 208205, -11939);
	private static final Location LOCATION_11 = new Location(-183290, 210004, -11939);
	private static final Location LOCATION_12 = new Location(-187776, 205696, -9536);
	private static final Location LOCATION_13 = new Location(-186327, 208286, -9536);
	private static final Location LOCATION_14 = new Location(-184429, 211155, -9536);
	private static final Location LOCATION_15 = new Location(-182811, 213871, -9504);
	private static final Location LOCATION_16 = new Location(-180921, 216789, -9536);
	private static final Location LOCATION_17 = new Location(-177264, 217760, -9536);
	private static final Location LOCATION_18 = new Location(-173727, 218169, -9536);

	private static final int ATTACK_INSTANTZONE_ID = 121;
	private static final int DEFENCE_INSTANTZONE_ID = 122;

	public AbyssGazeInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		String filename;
		if(val == 0)
		{
			if(getNpcId() == 32539)
				return "vigil_immortality001.htm";
			else if(getNpcId() == 32540)
				return "vigil_immortality_dis001.htm";
		}
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -1002 && reply == 1003)
		{
			int fieldCycle = SoIManager.getCurrentStage();
			if(fieldCycle == 1)
				showChatWindow(player, "default/vigil_immortality003.htm", false);
			else if(fieldCycle == 2 || fieldCycle == 5)
				showChatWindow(player, "default/vigil_immortality002b.htm", false);
			else
				showChatWindow(player, "default/vigil_immortality002a.htm", false);
		}
		else if(ask == -1004 && reply == 1005)
		{
			player.teleToLocation(RETURN_LOCATION, ReflectionManager.MAIN);
		}
		else if(ask == -1006 && reply == 1007)
		{
			int fieldCycle = SoIManager.getCurrentStage();
			if(fieldCycle == 3)
			{
				Location loc = LOCATION_1;

				int locationId = Rnd.get(18) + 1;
				switch(locationId)
				{
					case 1:
					{
						loc = LOCATION_1;
						break;
					}
					case 2:
					{
						loc = LOCATION_2;
						break;
					}
					case 3:
					{
						loc = LOCATION_3;
						break;
					}
					case 4:
					{
						loc = LOCATION_4;
						break;
					}
					case 5:
					{
						loc = LOCATION_5;
						break;
					}
					case 6:
					{
						loc = LOCATION_6;
						break;
					}
					case 7:
					{
						loc = LOCATION_7;
						break;
					}
					case 8:
					{
						loc = LOCATION_8;
						break;
					}
					case 9:
					{
						loc = LOCATION_9;
						break;
					}
					case 10:
					{
						loc = LOCATION_10;
						break;
					}
					case 11:
					{
						loc = LOCATION_11;
						break;
					}
					case 12:
					{
						loc = LOCATION_12;
						break;
					}
					case 13:
					{
						loc = LOCATION_13;
						break;
					}
					case 14:
					{
						loc = LOCATION_14;
						break;
					}
					case 15:
					{
						loc = LOCATION_15;
						break;
					}
					case 16:
					{
						loc = LOCATION_16;
						break;
					}
					case 17:
					{
						loc = LOCATION_17;
						break;
					}
					case 18:
					{
						loc = LOCATION_18;
						break;
					}
				}
				player.teleToLocation(loc, ReflectionManager.MAIN);
			}
			else if(fieldCycle == 4)
			{
				Location loc = LOCATION_1;

				int locationId = Rnd.get(7) + 1;
				switch(locationId)
				{
					case 1:
					{
						loc = LOCATION_1;
						break;
					}
					case 2:
					{
						loc = LOCATION_2;
						break;
					}
					case 3:
					{
						loc = LOCATION_3;
						break;
					}
					case 4:
					{
						loc = LOCATION_4;
						break;
					}
					case 5:
					{
						loc = LOCATION_5;
						break;
					}
					case 6:
					{
						loc = LOCATION_6;
						break;
					}
					case 7:
					{
						loc = LOCATION_7;
						break;
					}
				}
				player.teleToLocation(loc, ReflectionManager.MAIN);
			}
			else
				showChatWindow(player, "default/vigil_immortality003.htm", false);
		}
		else if(ask == -1008 && reply == 1009)
		{
			int fieldCycle = SoIManager.getCurrentStage();
			if(fieldCycle == 2)
			{
				Reflection r = player.getActiveReflection();
				if(r != null)
				{
					if(player.canReenterInstance(ATTACK_INSTANTZONE_ID))
						player.teleToLocation(r.getTeleportLoc(), r);
				}
				else if(player.canEnterInstance(ATTACK_INSTANTZONE_ID))
				{
					ReflectionUtils.enterReflection(player, new HeartInfinityAttack(), ATTACK_INSTANTZONE_ID);
				}
			}
			else if(fieldCycle == 5)
			{
				Reflection r = player.getActiveReflection();
				if(r != null)
				{
					if(player.canReenterInstance(DEFENCE_INSTANTZONE_ID))
						player.teleToLocation(r.getTeleportLoc(), r);
				}
				else if(player.canEnterInstance(DEFENCE_INSTANTZONE_ID))
				{
					ReflectionUtils.enterReflection(player, new HeartInfinityDefence(), DEFENCE_INSTANTZONE_ID);
				}
			}
			else
				showChatWindow(player, "default/vigil_immortality003.htm", false);
		}
	}
}