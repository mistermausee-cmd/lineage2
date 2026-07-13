package instances;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.*;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk

public class CrystalHall extends Reflection
{
    private static final int[] RB1 = {25796, 26105, 26106, 26107};
	private static final int[] RB2 = {25881, 26108, 26109, 26110};
	private static final int Cannon1 = 19008;
	private static final int Cannon2 = 19008;
	private static final int Cannon3 = 19008;
	private static final int Cannon4 = 19008; 
	private static final int Cannon5 = 19008;
	private static final int Cannon6 = 19008;   
	private static final int Cannon7 = 19008;
	private static final int Cannon8 = 19008;
	private static final int Cannon9 = 19008;
	private static final int DoorOutside = 24220005;
	private static final int DoorInside = 24220006;

	private static final int[] ChestOfDim = {19576, 19577, 19578, 19579, 19580, 19581, 19582, 19583, 19584, 19585, 19586, 19587};
	private static final int Exchanger = 33388;

	private long _savedTime;

	private Location Cannon1Loc = new Location(143144, 145832, -12061);
	private Location Cannon2Loc = new Location(141912, 144200, -11949);
	private Location Cannon3Loc = new Location(143368, 143768, -11976);
	private Location Cannon4Loc = new Location(145544, 143746, -11841);
	private Location Cannon5Loc = new Location(147544, 142888, -12249);
	private Location Cannon6Loc = new Location(147464, 144776, -12252);
	private Location Cannon7Loc = new Location(148984, 144440, -12283);
	private Location Cannon8Loc = new Location(148696, 146504, -12346);
	private Location Cannon9Loc = new Location(149096, 146040, -12355);
    private Location RB1Loc = new Location(152984, 145960, -12609, 15640);
	private Location RB2Loc = new Location(152536, 145960, -12609, 15640);

	NpcInstance can1 = null;
	NpcInstance can2 = null;
	NpcInstance can3 = null;
	NpcInstance can4 = null;
	NpcInstance can5 = null;
	NpcInstance can6 = null;
	NpcInstance can7 = null;
	NpcInstance can8 = null;
	NpcInstance can9 = null;

	NpcInstance firstrb = null;
	NpcInstance secondrb = null;

	private DeathListener _deathListener = new DeathListener();
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		_savedTime = System.currentTimeMillis();

		can1 = addSpawnWithoutRespawn(Cannon1, Cannon1Loc, 0);
		can1.addListener(_deathListener);
		can2 = addSpawnWithoutRespawn(Cannon2, Cannon2Loc, 0);
		can2.addListener(_deathListener);
		can2.setTargetable(false);
		can3 = addSpawnWithoutRespawn(Cannon3, Cannon3Loc, 0);
		can3.addListener(_deathListener);
		can3.setTargetable(false);
		can4 = addSpawnWithoutRespawn(Cannon4, Cannon4Loc, 0);
		can4.addListener(_deathListener);
		can4.setTargetable(false);
		can5 = addSpawnWithoutRespawn(Cannon5, Cannon5Loc, 0);
		can5.addListener(_deathListener);
		can5.setTargetable(false);
		can6 = addSpawnWithoutRespawn(Cannon6, Cannon6Loc, 0);
		can6.addListener(_deathListener);
		can6.setTargetable(false);
		can7 = addSpawnWithoutRespawn(Cannon7, Cannon7Loc, 0);
		can7.addListener(_deathListener);
		can7.setTargetable(false);
		can8 = addSpawnWithoutRespawn(Cannon8, Cannon8Loc, 0);
		can8.addListener(_deathListener);
		can8.setTargetable(false);
		can9 = addSpawnWithoutRespawn(Cannon9, Cannon9Loc, 0);
		can9.addListener(_deathListener);
		can9.setTargetable(false);
	}

	@Override
	public void onPlayerEnter(Player player) 
	{
        super.onPlayerEnter(player);
		player.sendPacket(new ExSendUIEventPacket(player, 0, 1, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));
    }

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.sendPacket(new ExSendUIEventPacket(player, 1, 1, 0, 0));
	}	
	
	private class DeathListener implements OnDeathListener
	{
		@Override
        public void onDeath(Creature self, Creature killer) 
		{
            if (self.isNpc() && self == can1)
			{
				can2.setTargetable(true);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "1"));
				}
			}
			else if (self.isNpc() && self == can2)
			{
				can3.setTargetable(true);
				for (Player p : getPlayers())
					p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "2"));
			}
            else if (self.isNpc() && self == can3)
            {
	            can4.setTargetable(true);
	            for (Player p : getPlayers())
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "3"));
            }
            else if (self.isNpc() && self == can4)
            {
	            can5.setTargetable(true);
	            can6.setTargetable(true);
	            for (Player p : getPlayers())
	            {
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "4"));
	            }
            }
            else if (self.isNpc() && self == can5)
            {
	            can7.setTargetable(true);
	            for (Player p : getPlayers())
	            {
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "5"));
	            }
            }
            else if (self.isNpc() && self == can6)
            {
	            can7.setTargetable(true);
	            for (Player p : getPlayers())
	            {
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "6"));
	            }
            }
            else if (self.isNpc() && self == can7)
            {
	            can8.setTargetable(true);
	            for (Player p : getPlayers())
	            {
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "7"));
	            }
            }
            else if (self.isNpc() && self == can8)
            {
	            can9.setTargetable(true);
	            for (Player p : getPlayers())
	            {
		            p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "8"));
	            }
            }
            else if (self.isNpc() && self == can9)
			{
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_ENTRY_ACCESSED, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
				}

				getDoor(DoorOutside).openMe();
				getDoor(DoorInside).openMe();

				int rbnumber = Rnd.get(0, 3);

				firstrb = addSpawnWithoutRespawn(RB1[rbnumber], RB1Loc, 0);
				firstrb.addListener(_deathListener);
				secondrb = addSpawnWithoutRespawn(RB2[rbnumber], RB2Loc, 0);
				secondrb.addListener(_deathListener);

			}
			else if (self.isNpc() && (ArrayUtils.contains(RB1, self.getNpcId()) || ArrayUtils.contains(RB2, self.getNpcId())))
            {
	            if(firstrb != null && firstrb.isDead() && secondrb != null && secondrb.isDead())
	            {
		            clearReflection(5, true);
		            addSpawnWithoutRespawn(Rnd.get(ChestOfDim), RB2Loc, 0);
	            }
			}
		}
	}


}