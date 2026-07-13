package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ai.kartia.SupportAAI;
import ai.kartia.SupportFAI;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

//By Evil_dnk

public class KimerianHard extends Reflection
{

	private List<NpcInstance> _followers = new ArrayList<NpcInstance>();
	private ScheduledFuture<?> _supportTask;
	private static final int KIMERIAN = 25758;
	private static int stage = 0;
	private static boolean show1 = false;
	private final OnDeathListener _monsterDeathListener = new DeathListener();
	NpcInstance follower1 = null;
	NpcInstance kimerian = null;
	NpcInstance phantom1 = null;
	NpcInstance phantom2 = null;
	NpcInstance phantom3 = null;
	NpcInstance phantom4 = null;
	private ZoneListener _startZoneListener = new ZoneListener();

	private List<NpcInstance> _minions = new ArrayList<NpcInstance>();
	private List<Player> _players = new ArrayList<Player>();

	@Override
	public void onPlayerEnter(final Player player) {

		super.onPlayerEnter(player);
		if(getPlayers() != null && !getPlayers().isEmpty())
		{
			follower1.setFollowTarget(getPlayers().get(0));
		}
	}

	private class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(!cha.isPlayer())
				return;

			if(zone.getName().equalsIgnoreCase("[kimerian_10306_h]"))
			{
				if(!show1)
				{
					NpcInstance kimrianshow = addSpawnWithoutRespawn(KIMERIAN, new Location(224961, 69223, 1592, 14672), 0);
					kimrianshow.setTargetable(false);
					kimrianshow.getFlags().getInvulnerable().start();
					show1 = true;
					Functions.npcSay(kimrianshow, NpcString.HOW_DARE_YOU_CAME_TO_ME_THIS_IS_WHERE_YOULL_BE_BURIED);
					ThreadPoolManager.getInstance().schedule(() ->
					{
						Functions.npcSay(kimrianshow, NpcString.BUT_YOU_CAME_THIS_FAR_I_SHOULD_AT_LEAST_PLAY_WITH_YOU_A_LITTLE);
					}, 4000L);
					ThreadPoolManager.getInstance().schedule(() ->
					{
						kimrianshow.deleteMe();
						phantom1 = addSpawnWithoutRespawn(25746, new Location(224771, 68889, 1568, 15836), 0);
						phantom1.addListener(_monsterDeathListener);
						phantom2 = addSpawnWithoutRespawn(25746, new Location(224864, 68815, 1568, 14052), 0);
						phantom2.addListener(_monsterDeathListener);
						phantom3 = addSpawnWithoutRespawn(25746, new Location(225161, 68821, 1568, 16608), 0);
						phantom3.addListener(_monsterDeathListener);
						phantom4 = addSpawnWithoutRespawn(25746, new Location(225004, 68798, 1568, 16016), 0);
						phantom4.addListener(_monsterDeathListener);
						stage = 1;
					}, 8000L);
				}
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}

	@Override
	protected void onCreate()
	{
		stage = 0;
		openDoor(26200100);

		getZone("[kimerian_10306_h]").addListener(_startZoneListener);

		follower1 = addSpawnWithoutRespawn(32914, new Location(215416, 80024, 796, 49152), 0);
		_followers.add(follower1);

		_supportTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new KimerianSupportTask(), 2000L, 2000L);
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if (self.isNpc() && self.getNpcId() == KIMERIAN)
			{
				clearReflection(5, true);

				Functions.npcSay(kimerian, NpcString.ALL_FOR_NOTHING);
				if(follower1 != null && !follower1.isDead())
					Functions.npcSay(follower1, NpcString.YOU_DID_A_LOT_OF_WORK_LETS_GO_BACK_TO_THE_VILLAGE_AND_HAVE_A_CONGRATULATORY_DRINK);
				NpcInstance noeti = addSpawnWithoutRespawn(33099, self.getLoc(), 0);
				Functions.npcSay(noeti, NpcString.YOU_DID_IT_THANK_YOU);
			}
			if (getAllByNpcId(25746, true).isEmpty())
			{
				  if(stage == 1)
				  {
					  kimerian = addSpawnWithoutRespawn(KIMERIAN, new Location(getPlayers().get(0).getX() + 100, getPlayers().get(0).getY() + 100, getPlayers().get(0).getZ(), 22228), 0);
					  kimerian.addListener(_monsterDeathListener);
					  stage = 2;
					  Functions.npcSay(kimerian, NpcString.RESISTANCE_UNDERLINGS);
				  }
			}
		}
	}

	private class KimerianSupportTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getPlayers().isEmpty())
				return;

			for(NpcInstance follower : _followers)
			{
				List<NpcInstance> around = follower.getAroundNpc(600, 150);
				if(follower.isDead())
					continue;

				if(getPlayers() != null && !getPlayers().isEmpty())
					follower.setFollowTarget(getPlayers().get(0));

				follower.setBusy(true);

				for(AggroList.HateInfo aggro : follower.getAggroList().getPlayableMap().values())
					if(aggro.attacker.isNpc() || aggro.attacker.isPlayer())
						follower.getAggroList().remove(aggro.attacker, true);

				if(around != null && !around.isEmpty())
				{
					for(NpcInstance npc : around)
					{
						if(npc instanceof GuardInstance || npc.isPet() || npc.isSummon() || npc.isPlayer() || npc.isPeaceNpc() || npc.isBusy())
						{
							if(follower.getTarget() == null)
							{
								follower.setSpawnedLoc(follower.getLoc());
								follower.broadcastCharInfoImpl(NpcInfoType.VALUES);
								follower.setAI(new SupportFAI(follower));
								if(getPlayers() != null && !getPlayers().isEmpty())
									follower.setFollowTarget(getPlayers().get(0));
							}
						}
						else
						{
							follower.setSpawnedLoc(follower.getLoc());
							follower.broadcastCharInfoImpl(NpcInfoType.VALUES);
							follower.setAI(new SupportAAI(follower));
							break;
						}
					}
				}
				else
				{
					follower.setSpawnedLoc(follower.getLoc());
					follower.broadcastCharInfoImpl(NpcInfoType.VALUES);
					follower.setAI(new SupportFAI(follower));
				}
			}
		}
	}

	public void spawnMinions(int npcId)
	{
		for(Creature pl : getPlayers())
		{
			if(pl.isPlayer())
				_players.add((Player) pl);
		}

		NpcInstance minion1 = addSpawnWithoutRespawn(npcId, kimerian.getLoc(), 0);
		_minions.add(minion1);
		NpcInstance minion2 = addSpawnWithoutRespawn(npcId, kimerian.getLoc(), 0);
		_minions.add(minion2);
		NpcInstance minion3 = addSpawnWithoutRespawn(npcId, kimerian.getLoc(), 0);
		_minions.add(minion3);
		NpcInstance minion4 = addSpawnWithoutRespawn(npcId, kimerian.getLoc(), 0);
		_minions.add(minion4);
		NpcInstance minion5 = addSpawnWithoutRespawn(npcId, kimerian.getLoc(), 0);
		_minions.add(minion5);

		if(_players != null && !_players.isEmpty())
		{
			for(NpcInstance minion : _minions)
			{
				minion.getAggroList().addDamageHate(Rnd.get(_players), 0, 50);
			}
		}
	}
}
