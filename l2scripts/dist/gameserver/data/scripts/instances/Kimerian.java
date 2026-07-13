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
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

//By Evil_dnk

public class Kimerian extends Reflection
{

	private List<NpcInstance> _followers = new ArrayList<NpcInstance>();
	private ScheduledFuture<?> _supportTask;
	private static final int KIMERIAN = 25745;
	private static boolean show1 = false;
	private ZoneListener _startZoneListener = new ZoneListener();
	private final OnDeathListener _monsterDeathListener = new DeathListener();
	NpcInstance follower1 = null;
	NpcInstance follower2 = null;
	NpcInstance follower3 = null;
	NpcInstance kimerian = null;
	private List<NpcInstance> _minions = new ArrayList<NpcInstance>();
	private List<Player> _players = new ArrayList<Player>();


	@Override
	public void onPlayerEnter(final Player player) {

		super.onPlayerEnter(player);
		if(getPlayers() != null && !getPlayers().isEmpty())
		{
			follower1.setFollowTarget(getPlayers().get(0));
			follower2.setFollowTarget(getPlayers().get(0));
			follower3.setFollowTarget(getPlayers().get(0));
		}
	}

	@Override
	protected void onCreate()
	{
		getZone("[kimerian_10306_l]").addListener(_startZoneListener);

		follower1 = addSpawnWithoutRespawn(32914, new Location(215416, 80024, 796, 49152), 0);
		_followers.add(follower1);

		follower2 = addSpawnWithoutRespawn(32913, new Location(215416, 80024, 796, 49152), 0);
		_followers.add(follower2);

		follower3 = addSpawnWithoutRespawn(32913, new Location(215416, 80024, 796, 49152), 0);
		_followers.add(follower3);

		kimerian = addSpawnWithoutRespawn(KIMERIAN, new Location(223952, 71136, 1632, 22228), 0);
		kimerian.addListener(_monsterDeathListener);
		_supportTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new KimerianSupportTask(), 2000L, 2000L);


	}

	private class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(!cha.isPlayer())
				return;

			if(zone.getName().equalsIgnoreCase("[kimerian_10306_l]"))
			{
				if(!show1)
				{
					NpcInstance kimrianshow = addSpawnWithoutRespawn(KIMERIAN, new Location(217184, 78672, 928, 24763), 0);
					kimrianshow.setTargetable(false);
					kimrianshow.getFlags().getInvulnerable().start();
					show1 = true;
					Functions.npcSay(kimrianshow, NpcString.HOW_RIDICULOUS_YOU_THINK_YOU_CAN_FIND_ME);

					ThreadPoolManager.getInstance().schedule(() ->
					{
						Functions.npcSay(kimrianshow, NpcString.THEN_TRY_HA_HA_HA);
					}, 4000L);
					ThreadPoolManager.getInstance().schedule(() ->
					{
						kimrianshow.deleteMe();
					}, 8000L);
				}
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}


	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if (self.isNpc() && self.getNpcId() == KIMERIAN)
			{
				clearReflection(5, true);
				NpcInstance phantom = addSpawnWithoutRespawn(25746, self.getLoc(), 0);
				phantom.setTargetable(false);
				phantom.getFlags().getInvulnerable().start();
				Functions.npcSay(phantom, NpcString.I_WILL_COME_BACK_ALIVE_WITH_ROTTING_AURA);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					Functions.npcSay(phantom, NpcString.HA_HA_HA_HA);
				}, 4000L);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					phantom.deleteMe();
					if(follower1 != null && !follower1.isDead())
						Functions.npcSay(follower1, NpcString.THANK_YOU_FOR_FIGHTING_WELL_UNTIL_THE_END_I_WILL_REQUEST_THAT_YOU_BE_ADDITIONALLY_REWARDED);
				}, 7000L);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					NpcInstance noeti = addSpawnWithoutRespawn(33099, self.getLoc(), 0);
					Functions.npcSay(noeti, NpcString.UNFORTUNATELY_THEY_RAN_AWAY);
				}, 8800L);


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
