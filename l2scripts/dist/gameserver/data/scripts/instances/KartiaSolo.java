package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.AggroList.HateInfo;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;

import ai.kartia.SupportAAI;
import ai.kartia.SupportFAI;

/**
 * @author Evil_dnk
 * @reworked by Bonux
**/
public class KartiaSolo extends Kartia
{
	private class LifeThiefDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if(victim.getNpcId() == _lifeThiefNpcId)
			{
				SkillEntry skill = SkillHolder.getInstance().getSkillEntry(15401, 1);
				if(skill != null)
				{
					ExShowScreenMessage message = new ExShowScreenMessage(NpcString.BURNING_BLOODS_EFFECT_IS_FELT, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage()));
					for(Player player : getPlayers())
					{
						skill.getEffects(victim, player);
						player.sendPacket(message);
					}
				}
			}
		}
	}

	private class KartiaSupportTask extends RunnableImpl
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

				for(HateInfo aggro : follower.getAggroList().getPlayableMap().values())
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
	
	private class HealTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getPlayers().isEmpty())
				return;

			final Player player = getPlayers().get(0);
			if(_healer != null)
			{
				double percentHp = player.getCurrentHp() / player.getMaxHp();
				if(percentHp <= 0.5D)
				{
					_healer.setTarget(player);
					_healer.doCast(SkillHolder.getInstance().getSkillEntry(14899, 1), player, true);

					boolean needTree = true;
					for(NpcInstance npc : getNpcs())
					{
						if(npc.getNpcId() == 19256)
						{
							needTree = false;
							break;
						}
					}
					if(needTree)
					{
						ThreadPoolManager.getInstance().schedule(new RunnableImpl()
						{
							@Override
							public void runImpl()
							{
								_healer.doCast(SkillHolder.getInstance().getSkillEntry(14903, 1), player, true);

								ThreadPoolManager.getInstance().schedule(new RunnableImpl()
								{
									@Override
									public void runImpl()
									{
										spawnHealingTree();
									}
								}
								, 2000L);
							}
						}
						, 2000L);
					}

				}
				else if(percentHp <= 0.85D)
				{
					_healer.setTarget(player);
					_healer.doCast(SkillHolder.getInstance().getSkillEntry(14899, 1), player, true);
				}
			}
		}
	}

	private static final Location SOLO_ENTRANCE = new Location(-108983, -10446, -11920);

	// NPC's
	private int _lifeThiefNpcId;
	private int _adolfNpcId;
	private int _bartonNpcId;
	private int _hayukNpcId;
	private int _eliahNpcId;
	private int _eliseNpcId;
	private int _eliahSpiritNpcId;

	private List<NpcInstance> _followers = new ArrayList<NpcInstance>();

	private OnDeathListener _lifeThiefDeathListener = new LifeThiefDeathListener();

	private NpcInstance _warrior = null;
	private NpcInstance _archer = null;
	private NpcInstance _summoner = null;
	private NpcInstance _healer = null;
	private NpcInstance _knight = null;
	private NpcInstance _lifethief = null;

	private int _excludedSupport = 0;

	private ScheduledFuture<?> _healTask;
	private ScheduledFuture<?> _supportTask;

	@Override
	protected void onCreate()
	{
		StatsSet params = getInstancedZone().getAddParams();

		_lifeThiefNpcId = params.getInteger("life_thief_npc_id");
		_adolfNpcId = params.getInteger("adolf_npc_id");
		_bartonNpcId = params.getInteger("barton_npc_id");
		_hayukNpcId = params.getInteger("hayuk_npc_id");
		_eliahNpcId = params.getInteger("eliah_npc_id");
		_eliseNpcId = params.getInteger("elise_npc_id");
		_eliahSpiritNpcId = params.getInteger("eliah_spirit_npc_id");

		_roomDoorId = 16170002;
		_raidDoorId = 16170003;

		_excludedZoneTeleportLoc = new Location(-110264, -10456, -11949);

		_rulerSpawnLoc = new Location(-111296, -15872, -11400, 15596);
		_supportTroopsSpawnLoc = new Location(-110936, -14472, -11452, 47595);

		_kartiaAltharSpawnLoc = new Location(-110116, -10453, -11307, 0);
		_ssqCameraLightSpawnLoc = new Location(-110116, -10453, -11307, 0);
		_ssqCameraZoneSpawnLoc = new Location(-110339, -10443, -11924, 0);

		_instanceZone = getZone("[kartia_instance_solo]");
		_excludedInstanceZone = getZone("[kartia_excluded_zone_solo]");

		_aggroStartPointLoc = new Location(-111288, -13944, -11453);
		_aggroMovePointLoc = new Location(-111288, -13944, -11453);

		_monsterMoveNearestPointLoc = new Location(-111256, -10456, -11711);
		_monsterMovePointLoc = new Location(-111304, -15112, -11452);

		_leftKillerRoutes.add(new Location(-111256, -10456, -11711));
		_leftKillerRoutes.add(new Location(-110440, -10472, -11926));
		_leftKillerRoutes.add(new Location(-110085, -10876, -11920));
		_leftKillerRoutes.add(new Location(-109182, -10791, -11920));
		_leftKillerRoutes.add(new Location(-109162, -10453, -11926));
		_leftKillerRoutes.add(new Location(-109933, -10451, -11688));
		_leftKillerRoutes.add(new Location(-109933, -10451, -11688));

		_rightKillerRoutes.add(new Location(-111256, -10456, -11711));
		_rightKillerRoutes.add(new Location(-110440, -10472, -11926));
		_rightKillerRoutes.add(new Location(-110020, -9980, -11920));
		_rightKillerRoutes.add(new Location(-109157, -10009, -11920));
		_rightKillerRoutes.add(new Location(-109162, -10453, -11926));
		_rightKillerRoutes.add(new Location(-109933, -10451, -11688));
		_rightKillerRoutes.add(new Location(-109933, -10451, -11688));

		super.onCreate();

		spawnByGroup(_spawnGroupPrefix + "_support");

		for(NpcInstance npc : getNpcs())
			npc.setRandomWalk(false);
	}

	@Override
	public void addObject(GameObject o)
	{
		super.addObject(o);

		if(o.isNpc())
		{
			NpcInstance npc = (NpcInstance) o;
			if(npc.getNpcId() == _lifeThiefNpcId)
				npc.addListener(_lifeThiefDeathListener);
		}
	}
	@Override
	protected void startState1()
	{
		super.startState1();

		_lifethief = addSpawnWithoutRespawn(_lifeThiefNpcId, new Location(-110344, -10440, -11949, 0), 0);
		_lifethief.setNpcState(1);

		ExShowScreenMessage message = new ExShowScreenMessage(NpcString.THE_LIFE_PLUNDERERS_TRUE_FORM_IS_REVEALED, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage()));
		for(Player player : getPlayers())
			player.sendPacket(message);
	}

	@Override
	protected void startState2()
	{
		super.startState2();

		if(_lifethief != null && !_lifethief.isDead())
		{
			_lifethief.deleteMe();

			ExShowScreenMessage message = new ExShowScreenMessage(NpcString.THE_LIFE_PLUNDERER_HAS_DISAPPEARED, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage()));
			for(Player player : getPlayers())
				player.sendPacket(message);
		}
	}

	@Override
	protected void cleanup()
	{
		super.cleanup();
		if(_healTask != null)
			_healTask.cancel(true);
		if(_supportTask != null)
			_supportTask.cancel(true);
	}

	@Override
	protected void startChallenge()
	{
		_knight = addSpawnWithoutRespawn(_adolfNpcId, new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
		_followers.add(_knight);
		if(_excludedSupport != 1)
		{
			_warrior = addSpawnWithoutRespawn(_bartonNpcId, new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
			_followers.add(_warrior);
		}

		if(_excludedSupport != 2)
		{
			_archer = addSpawnWithoutRespawn(_hayukNpcId, new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
			_followers.add(_archer);
		}

		if(_excludedSupport != 3)
		{
			_summoner = addSpawnWithoutRespawn(_eliahNpcId, new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
			_followers.add(_summoner);

			for(byte i = 0; i < 3; i = (byte)(i + 1))
			{
				NpcInstance light = addSpawnWithoutRespawn(_eliahSpiritNpcId, new Location(_summoner.getX(), _summoner.getY(), _summoner.getZ(), 0), 0);
				_followers.add(light);
			}
		}

		if(_excludedSupport != 4)
		{
			_healer = addSpawnWithoutRespawn(_eliseNpcId, new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
			_followers.add(_healer);

			_healTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(), 2000L, 7000L);
		}

		_supportTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new KartiaSupportTask(), 2000L, 2000L);

		super.startChallenge();
	}

	public void deselectSupport(int support)
	{
		if(_excludedSupport == 0)
		{
			_excludedSupport = support;
			for(Player player : getPlayers())
				player.teleToLocation(SOLO_ENTRANCE);
		}
		startChallenge();
		_excludedSupport = 0;
	}

	private void spawnHealingTree()
	{
		SkillEntry buff = SkillHolder.getInstance().getSkillEntry(15003, 1);
		SkillEntry heal = SkillHolder.getInstance().getSkillEntry(15002, 1);

		if(getPlayers().isEmpty())
			return;

		final Player player = getPlayers().get(0);

		Location loc = player.getLoc();

		final Creature tree = addSpawnWithoutRespawn(19256, new Location(loc.getX(), loc.getY(), loc.getZ(), loc.h), 0);
		tree.setTarget(player);
		tree.doCast(buff, player, true);
		tree.doCast(heal, player, true);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if((tree != null) && (!player.isDead()))
				{
					tree.setTarget(player);

					tree.doCast(SkillHolder.getInstance().getSkillEntry(15002, 1), player, true);

					ThreadPoolManager.getInstance().schedule(this, 10000L);
				}
			}
		}
		, 10000L);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl(){
				if((tree != null) && (!player.isDead())){
					tree.setTarget(player);

					tree.doCast(SkillHolder.getInstance().getSkillEntry(15003, 1), player, true);

					ThreadPoolManager.getInstance().schedule(this, 20000L);
				}
			}
		}
				, 20000L);
	}
}