package instances;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

import java.util.concurrent.ScheduledFuture;

/**
 * Класс контролирует инстанс LindviorBoss
 *
 * @author iqman
 */
public class LindviorBoss extends Reflection
{
	private static final int LINDVIOR_FAKE_NPC_ID = 19423;
	private static final int LINDVIOR_GROUND_NPC_ID = 25899;
	private static final int LINDVIOR_FLY_NPC_ID = 19424;
	private static final int LINDVIOR_RAID_NPC_ID = 29240;

	private ScheduledFuture<?> _announceTask;
	private int _totalCharges = 0;
	private int _raidProgress = 0;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		_announceTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Announce(), 15000, 15000);
	}

	private class Announce implements Runnable
	{
		@Override
		public void run()
		{
			for(Player player : getPlayers())
			{
				if(Rnd.chance(50))
					player.sendPacket(new ExShowScreenMessage(NpcString.YOU_MUST_ACTIVATE_THE_4_GENERATORS, 7000, ScreenMessageAlign.TOP_CENTER));
				else
					player.sendPacket(new ExShowScreenMessage(NpcString.PROTECT_THE_GENERATOR, 7000, ScreenMessageAlign.TOP_CENTER));
			}
		}
	}

	private class spawnMeLindviorServant implements Runnable
	{
		private int _id;
		private Location _loc;
		private AggroList _aggroList;

		public spawnMeLindviorServant(int id, Location loc, AggroList aggroList)
		{
			_id = id;
			_loc = loc;
			_aggroList = aggroList;
		}

		@Override
		public void run()
		{
			NpcInstance npc = addSpawnWithoutRespawn(_id, _loc, 300);
			if(_aggroList != null)
				npc.getAggroList().copy(_aggroList);
		}
	}

	public void announceToInstance(NpcString string)
	{
		for(Player player : getPlayers())
			player.sendPacket(new ExShowScreenMessage(string, 7000, ScreenMessageAlign.TOP_CENTER));
	}

	public void endInstance()
	{
		startCollapseTimer(1 * 60 * 1000L);
		for(Player p : getPlayers())
		{
			p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
			p.sendPacket(new ExShowScreenMessage(NpcString.THE_GENERATOR_HAS_BEEN_DESTROYED, 10000, ScreenMessageAlign.TOP_CENTER));
		}
	}

	public void scheduleNextSpawnFor(int id, long delay, Location loc, AggroList aggroList)
	{
		ThreadPoolManager.getInstance().schedule(new spawnMeLindviorServant(id, loc, aggroList), delay);
	}

	public void increaseCharges()
	{
		_totalCharges++;

		if(_totalCharges == 1)
		{
			for(Player player : getPlayers())
				player.startScenePlayer(SceneMovie.LINDVIOR_ARRIVE);

			addSpawnWithoutRespawn(LINDVIOR_FAKE_NPC_ID, new Location(44472, -25528, -1432), 0); //starting to fly around
		}
		else if(_totalCharges == 4)
			finishStage1();
	}

	private void finishStage1()
	{
		for(NpcInstance npc : getNpcs())
		{
			if(npc.getNpcId() == 19479)
			{
				Functions.npcSay(npc, NpcString.ALL_4_GENERATORS_MUST_BE_ACTIVATED, ChatType.NPC_ALL, 5000);
				npc.deleteMe();
			}
			else if(npc.getNpcId() == 19477) //del generator
				npc.deleteMe();
			else if(npc.getNpcId() == LINDVIOR_FAKE_NPC_ID) //del flying lindvior
				npc.deleteMe();
		}

		_announceTask.cancel(false);
		_announceTask = null;

		setRaidProgress(1, null);
	}

	public int getRaidProgress()
	{
		return _raidProgress;
	}

	public boolean setRaidProgress(int value, NpcInstance previousNpc)
	{
		if((value - _raidProgress) != 1)
			return false;

		_raidProgress = value;

		if(value == 1)
		{
			final NpcInstance instance = addSpawnWithoutRespawn(LINDVIOR_GROUND_NPC_ID, new Location(46424, -26200, -1430), 0);
			ThreadPoolManager.getInstance().schedule(() -> {
				announceToInstance(NpcString.LINDVIOR_HAS_FALLEN_FROM_THE_SKY);
				instance.setRHandId(15);
				instance.broadcastCharInfo();
			}, 15000);
			return true;
		}
		else if(value == 2)
		{
			scheduleNextSpawnFor(LINDVIOR_FLY_NPC_ID, 10000L, previousNpc.getLoc(), previousNpc.getAggroList());
			previousNpc.broadcastPacket(new SocialActionPacket(previousNpc.getObjectId(), 1));
			previousNpc.deleteMe();
			return true;
		}
		else if(value == 3)
		{
			scheduleNextSpawnFor(LINDVIOR_RAID_NPC_ID, 10000L, previousNpc.getLoc(), previousNpc.getAggroList());
			announceToInstance(NpcString.LINDVIOR_HAS_LANDED);
			previousNpc.deleteMe();
			return true;
		}
		else if(value == 4)
		{
			scheduleNextSpawnFor(LINDVIOR_FLY_NPC_ID, 10000L, previousNpc.getLoc(), previousNpc.getAggroList());
			scheduleNextSpawnFor(25898, 10000L, previousNpc.getLoc(), null);
			announceToInstance(NpcString.A_GIGANTIC_WHIRLWIND_HAS_APPEARED);
			previousNpc.deleteMe();
			return true;
		}
		else if(value == 5)
		{
			scheduleNextSpawnFor(LINDVIOR_RAID_NPC_ID, 10000L, previousNpc.getLoc(), previousNpc.getAggroList());
			scheduleNextSpawnFor(25898, 10000L, previousNpc.getLoc(), null);
			announceToInstance(NpcString.LINDVIOR_HAS_LANDED);
			previousNpc.deleteMe();
			startCollapseTimer(10 * 60 * 1000L);
			for(Player p : getPlayers())
				p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(10));
			return true;
		}
		return false;
	}
}