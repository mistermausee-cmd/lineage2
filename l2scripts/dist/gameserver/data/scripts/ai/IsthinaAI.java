package ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import instances.Isthina;

/**
 * @author: ? & Rivelia.
 */
 // TODO: Rework acid cameras: make them use acid skill by using AI. Make them dispose from self.
 //		  Use a magic use listener and magic hit listener instead of schedulers.
public class IsthinaAI extends Fighter
{
	private static final Logger _log = LoggerFactory.getLogger(IsthinaAI.class);

	//NPC ID
	private static final int ISTINA_LIGHT = 29195;
	private static final int ISTINA_HARD = 29196;

	//SKILLS
	private static final SkillEntry MANIFESTATION_OF_AUTHORITY_RED = SkillHolder.getInstance().getSkillEntry(14212, 1);
	private static final SkillEntry MANIFESTATION_OF_AUTHORITY_BLUE = SkillHolder.getInstance().getSkillEntry(14213, 1);
	private static final SkillEntry MANIFESTATION_OF_AUTHORITY_GREEN = SkillHolder.getInstance().getSkillEntry(14214, 1);
	private static final SkillEntry BARRIER_OF_REFLECTION = SkillHolder.getInstance().getSkillEntry(14215, 1);
	private static final SkillEntry ISTINA_MARK = SkillHolder.getInstance().getSkillEntry(14218, 1);
	private static final SkillEntry DEATH_BLOW = SkillHolder.getInstance().getSkillEntry(14219, 1);
	private static final SkillEntry FLOOD = SkillHolder.getInstance().getSkillEntry(14220, 1);
	private static final SkillEntry ACID_ERUPTION_ISTINA = SkillHolder.getInstance().getSkillEntry(14221, 1);
	private static final SkillEntry ACID_ERUPTION_START = SkillHolder.getInstance().getSkillEntry(14222, 1);
	private static final SkillEntry ACID_ERUPTION_END = SkillHolder.getInstance().getSkillEntry(14223, 1);
	private static final SkillEntry MANIFESTATION_OF_AUTHORITY_DEBUFF_NORMAL = SkillHolder.getInstance().getSkillEntry(14289, 1);
	private static final SkillEntry MANIFESTATION_OF_AUTHORITY_DEBUFF_HARD = SkillHolder.getInstance().getSkillEntry(14289, 2);

	//RING zone (Trigger)
	private final static int RED_RING = 14220101;
	private final static int BLUE_RING = 14220102;
	private final static int GREEN_RING = 14220103;

	//RING LOCATIONS AND FIGHT ZONE.
	private final Zone _zone;
	private final Zone BLUE_RING_LOC;
	private final Zone GREEN_RING_LOC;
	private int _ring;

	private static final int ISTINAS_CREATION = 23125;
	private static final int SEALING_ENERGY = 19036;
	private static final int ACID_ERUPTION_CAMERA = 18919;

	private final boolean isHard;
	private ScheduledFuture<?> _effectCheckTask = null;
	private ScheduledFuture<?> _ringRoutineTask = null;
	private boolean _authorityLock = false;
	private boolean _hasFlood = false;
	private boolean _hasBarrier = false;
	private long _skillDelay = 30000;
	private long _noReuse = 0;
	private boolean finishLock = false;
	private boolean lastHitLock = false;

	private boolean bMustBroadcastFlood = false;
	private boolean bMustBroadcastBarrier = false;
	private boolean bForceAuthorityField = false;

	private SkillEntry lastSkill = null;
	private List<NpcInstance> unluckPlayersCameras;

	private static final int ISTINA_NORMAL = 29195;
	private static final int ISTINA_EXTREME = 29196;
	private static final Location ON_BALLISTA_TRIGGER_LOCATION = new Location(-177123, 146938, -11389);

	private boolean _acidCasting = false;

	public IsthinaAI(NpcInstance actor)
	{
		super(actor);
		Reflection r = actor.getReflection();
		_zone = r.getZone("[istina_epic]");
		GREEN_RING_LOC = r.getZone("[istina_green_authority]");
		BLUE_RING_LOC = r.getZone("[istina_blue_authority]");
		isHard = getActor().getNpcId() == ISTINA_EXTREME;
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance npc = getActor();
		if(npc.getReflection() instanceof Isthina)
		{
			if(_effectCheckTask == null)
				_effectCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new EffectCheckTask(npc), 0, 500);

			double lastPercentHp = (npc.getCurrentHp() + damage) / npc.getMaxHp();
			double currentPercentHp = npc.getCurrentHp() / npc.getMaxHp();
			if(lastPercentHp > 0.95D && currentPercentHp <= 0.95D)
				onPercentHpReached(npc, 95);
			else if(lastPercentHp > 0.9D && currentPercentHp <= 0.9D)
				onPercentHpReached(npc, 90);
			else if(lastPercentHp > 0.85D && currentPercentHp <= 0.85D)
				onPercentHpReached(npc, 85);
			else if(lastPercentHp > 0.8D && currentPercentHp <= 0.8D)
				onPercentHpReached(npc, 80);
			else if(lastPercentHp > 0.75D && currentPercentHp <= 0.75D)
				onPercentHpReached(npc, 75);
			else if(lastPercentHp > 0.7D && currentPercentHp <= 0.7D)
				onPercentHpReached(npc, 70);
			else if(lastPercentHp > 0.65D && currentPercentHp <= 0.65D)
				onPercentHpReached(npc, 65);
			else if(lastPercentHp > 0.6D && currentPercentHp <= 0.6D)
				onPercentHpReached(npc, 60);
			else if(lastPercentHp > 0.55D && currentPercentHp <= 0.55D)
				onPercentHpReached(npc, 55);
			else if(lastPercentHp > 0.5D && currentPercentHp <= 0.5D)
				onPercentHpReached(npc, 50);
			else if(lastPercentHp > 0.45D && currentPercentHp <= 0.45D)
				onPercentHpReached(npc, 45);
			else if(lastPercentHp > 0.4D && currentPercentHp <= 0.4D)
				onPercentHpReached(npc, 40);
			else if(lastPercentHp > 0.35D && currentPercentHp <= 0.35D)
				onPercentHpReached(npc, 35);
			else if(lastPercentHp > 0.3D && currentPercentHp <= 0.3D)
				onPercentHpReached(npc, 30);
			else if(lastPercentHp > 0.25D && currentPercentHp <= 0.25D)
				onPercentHpReached(npc, 25);
			else if(lastPercentHp > 0.2D && currentPercentHp <= 0.2D)
				onPercentHpReached(npc, 20);
			else if(lastPercentHp > 0.15D && currentPercentHp <= 0.15D)
				onPercentHpReached(npc, 15);
			else if(lastPercentHp > 0.1D && currentPercentHp <= 0.1D)
				onPercentHpReached(npc, 10);
			else if(!lastHitLock && currentPercentHp <= 0.05D)
			{
				lastHitLock = true;
				onPercentHpReached(npc, 5);
			}
			if (!finishLock && !_authorityLock)
			{
				if (bForceAuthorityField)
					authorityField(npc);
				else
				{
					double seed = Rnd.get(1, 100);
					if (seed < 2)
						authorityField(npc);
				}
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	// Istina's Mark + Death Blow.
	@Override
	protected boolean createNewTask()
	{
		NpcInstance npc = getActor();
		if(npc == null || npc.isCastingNow() || Rnd.get() > 0.15D)
			return super.createNewTask();

		clearTasks();

		// Determinate target.
		Creature target;
		if((target = prepareTarget()) == null || target == npc)
			return super.createNewTask();

		SkillEntry r_skill = ISTINA_MARK;
		if (target.getAbnormalList().contains(ISTINA_MARK.getId()))
		{
			if (Rnd.get() < 0.5D)
				return super.createNewTask();
			r_skill = DEATH_BLOW;
		}

		lastSkill = r_skill;
		addTaskCast(target, r_skill.getTemplate());
		return true;
	}

	@Override
	protected boolean doTask()
	{
		if (lastSkill != null)
		{
			if (lastSkill == ISTINA_MARK)
				getActor().broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_MARK_SHINES_ABOVE_THE_HEAD, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true));
			lastSkill = null;
		}
		return super.doTask();
	}

	private void onPercentHpReached(NpcInstance npc, int percent)
	{
		if (percent == 5 && !finishLock)
		{
			finishLock = true;
			npc.getFlags().getInvulnerable().start();
			npc.getFlags().getParalyzed().start();
			npc.teleToLocation(ON_BALLISTA_TRIGGER_LOCATION, npc.getReflection());
			// TODO: Set heading.
			npc.setTargetable(false);

			for (Player player : npc.getReflection().getPlayers())
			{
				if(player.getTarget() != null)
				{
					player.setTarget(null);
					player.abortAttack(true, true);
					player.abortCast(true, true);
					player.sendActionFailed();
				}
			}
			Isthina refl = null;
			if(npc.getReflection() instanceof Isthina)
				refl = (Isthina) npc.getReflection();	
			if(refl != null)	
				refl.presentBallista(npc);
			return;
		}

		tryAcidCast();

		if (!_hasFlood && (percent < 50 && percent % 5 == 0))
		{
			if(!npc.getAbnormalList().contains(FLOOD.getId()) && !npc.isCastingNow())
			{
				npc.doCast(FLOOD, npc, false);
				_hasFlood = true;
				bMustBroadcastFlood = true;
			}
		}
		else if ((percent >= 50 && percent % 10 == 0) || (percent < 50 && percent % 10 == 0))
		{
			if(!npc.getAbnormalList().contains(BARRIER_OF_REFLECTION.getId()) && !npc.isCastingNow())
			{
				npc.doCast(BARRIER_OF_REFLECTION, npc, false);
				_hasBarrier = true;
				bMustBroadcastBarrier = true;
			}
		}

		if (isHard)
		{
			if ((percent <= 50) && (percent % 5 == 0))
			{
				npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINA_CALLS_HER_CREATURES_WITH_TREMENDOUS_ANGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true));
				for (int i = 0; i < 7; i++)			
					npc.getReflection().addSpawnWithoutRespawn(ISTINAS_CREATION, npc.getLoc(), 0);
			}

			int energyCount = Rnd.get(1, 4);
			for (int i = 0; i < energyCount; i++)
				npc.getReflection().addSpawnWithoutRespawn(SEALING_ENERGY, npc.getLoc(), 0);
		}

	}

	private synchronized void tryAcidCast()
	{
		if(_acidCasting)
			return;

		_acidCasting = true;

		NpcInstance npc = getActor();
		if(npc == null)
			return;

		byte acidsCount = (byte)(Rnd.get(1,3));
		int playerInside = npc.getReflection().getPlayers().size();
		if(playerInside == 0)
			_log.warn(getClass().getSimpleName() + ": No players in Istina reflection instance.");

		List<Player> unluckPlayers = new ArrayList<Player>(acidsCount);
		for (byte i = 0; i < acidsCount; i = (byte)(i + 1))
		{
			while (unluckPlayers.size() < playerInside)
			{
				Player unluckyPlayer = npc.getReflection().getPlayers().get(Rnd.get(playerInside/* - 1*/));
				if(!unluckPlayers.contains(unluckyPlayer))
				{
					unluckPlayers.add(unluckyPlayer);
					break;
				}
			}
		}

		int index = 0;
		if(unluckPlayers.isEmpty())
			_log.warn(getClass().getSimpleName() + ": Istina AI returned unluckPlayers array empty!!!");
		else
		{
			if(unluckPlayersCameras != null && !unluckPlayersCameras.isEmpty())
			{
				Iterator<NpcInstance> acidCams = unluckPlayersCameras.iterator();
				while(acidCams.hasNext())
				{
					NpcInstance camera = acidCams.next();
					/*if (camera.isCastingNow())
						continue;*/

					camera.deleteMe();
					acidCams.remove();
				}
			}

			unluckPlayersCameras = new ArrayList<NpcInstance>(unluckPlayers.size());
			for(Player player : unluckPlayers)
			{
				Location playerLoc = player.getLoc();
				NpcInstance camera = npc.getReflection().addSpawnWithoutRespawn(ACID_ERUPTION_CAMERA, playerLoc, 0);
				camera.getFlags().getInvulnerable().start();
				camera.setRandomWalk(false);
				unluckPlayersCameras.add(camera);
				castAcidEruptionOn(player, camera);
			}
			npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINA_SHOOTS_POWERFUL_ACID_INTO_THE_AIR, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true));
		}

		_acidCasting = false;
	}

	// @Rivelia.
	private void castAcidEruptionOn(Player player, NpcInstance camera)
	{
		camera.doCast(ACID_ERUPTION_START, camera, false);
		ThreadPoolManager.getInstance().schedule(new CameraScheduledTask(camera, player), 1100L);
	}

	private class CameraScheduledTask extends RunnableImpl
	{
		NpcInstance _npc;
		Player _player;

		public CameraScheduledTask(NpcInstance npc, Player player)
		{
			_npc = npc;
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_npc != null)
				_npc.doCast(ACID_ERUPTION_END, _player, false);
		}
	}

	private void authorityField(final NpcInstance npc)
	{
		if(npc.isCastingNow())
		{
			bForceAuthorityField = true;
			return;
		}

		bForceAuthorityField = false;
		_authorityLock = true;

		double seed = Rnd.get();

		final int ring = (seed >= 0.33D) && (seed < 0.66D) ? 1 : seed < 0.33D ? 0 : 2;
		_ring = ring;
		if(seed < 0.33D)
		{
			npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_GREEN, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
			npc.doCast(MANIFESTATION_OF_AUTHORITY_GREEN, npc, false);
		}
		else
		{
			if((seed >= 0.33D) && (seed < 0.66D))
			{
				npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_BLUE, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
				npc.doCast(MANIFESTATION_OF_AUTHORITY_BLUE, npc, false);
			}
			else
			{
				npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_RED, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
				npc.doCast(MANIFESTATION_OF_AUTHORITY_RED, npc, false);
			}
		}
		npc.broadcastPacket(new PlaySoundPacket("istina.istina_voice_01"));

		ThreadPoolManager.getInstance().schedule(new runAuthorityRing(npc), 8000L);
	}

	private class EffectCheckTask extends RunnableImpl
	{
		private NpcInstance _npc;
		private boolean bMustShootAcids;
		private int waitForDisposal;
		private List<NpcInstance> tempAcidCameras;

		public EffectCheckTask(NpcInstance npc)
		{
			_npc = npc;
			bMustShootAcids = false;
			tempAcidCameras = null;
		}
		
		@Override
		public void runImpl()
		{
			if(_npc == null)
			{
				if(_effectCheckTask != null)
					_effectCheckTask.cancel(false);
			}

			if(_npc.isCastingNow())
				return;

			if(bMustShootAcids && (tempAcidCameras == null || tempAcidCameras.isEmpty()))
			{
				tempAcidCameras = new ArrayList<NpcInstance>(_npc.getReflection().getPlayers().size());
				for (Player player : _npc.getReflection().getPlayers())
				{
					Location playerLoc = player.getLoc();
					NpcInstance camera = _npc.getReflection().addSpawnWithoutRespawn(ACID_ERUPTION_CAMERA, playerLoc, 0);
					camera.getFlags().getInvulnerable().start();
					camera.setRandomWalk(false);
					tempAcidCameras.add(camera);
					castAcidEruptionOn(player, camera);
				}
				bMustShootAcids = false;
				waitForDisposal = 8;	// Wait 4 seconds.
			}
			else if(tempAcidCameras != null && !tempAcidCameras.isEmpty())
			{
				if (waitForDisposal > 0)
					waitForDisposal--;
				else
				{
					Iterator<NpcInstance> acidCams = tempAcidCameras.iterator();
					while (acidCams.hasNext())
					{
						NpcInstance camera = acidCams.next();
						if (camera.isCastingNow())
							continue;

						camera.deleteMe();
						acidCams.remove();
					}
				}
			}

			boolean hasBarrier = _npc.getAbnormalList().contains(BARRIER_OF_REFLECTION.getTemplate());
			boolean hasFlood = _npc.getAbnormalList().contains(FLOOD.getTemplate());

			if(_hasBarrier && hasBarrier)
			{
				if (bMustBroadcastBarrier)
				{
					_npc.setNpcState(1);
					_npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINA_SPREADS_THE_REFLECTING_PROTECTIVE_SHEET, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true));
					bMustBroadcastBarrier = false;
				}
			}
			else if(_hasBarrier && !hasBarrier)
			{
				_npc.setNpcState(2);
				_npc.doCast(ACID_ERUPTION_ISTINA, _npc, false);
				_npc.broadcastPacket(new ExShowScreenMessage(NpcString.POWERFUL_ACIDIC_ENERGY_IS_ERUPTING_FROM_ISTINAS_BODY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
				bMustShootAcids = true;
				_hasBarrier = false;
			}
			else if(!_hasBarrier && hasBarrier)
				_npc.setNpcState(0);

			if(_hasFlood && hasFlood)
			{
				if (bMustBroadcastFlood)
				{
					_npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINA_GETS_FURIOUS_AND_RECKLESSLY_CRAZY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
					bMustBroadcastFlood = false;
				}
			}
			else if(_hasFlood && !hasFlood)
				_npc.broadcastPacket(new ExShowScreenMessage(NpcString.BERSERKER_OF_ISTINA_HAS_BEEN_DISABLED, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
		}
	}

	private final static void triggerRings(NpcInstance npc, int ring)
	{
		if(ring == 1 || ring == 2)
			npc.addEventTrigger(GREEN_RING);
		else
			npc.removeEventTrigger(GREEN_RING);

		if(ring == 0 || ring == 2)
			npc.addEventTrigger(BLUE_RING);
		else
			npc.removeEventTrigger(BLUE_RING);

		if(ring == 0 || ring == 1)
			npc.addEventTrigger(RED_RING);
		else
			npc.removeEventTrigger(RED_RING);
	}

	private class runAuthorityRing extends RunnableImpl
	{
		private NpcInstance _npc;

		runAuthorityRing(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			triggerRings(_npc, _ring);
			if(_ringRoutineTask != null)
				_ringRoutineTask.cancel(true);
			_ringRoutineTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ringRoutineTask(_npc, _ring, _zone, GREEN_RING_LOC, BLUE_RING_LOC, 5), 0, 3000);
		}
	}

	private class ringRoutineTask extends RunnableImpl
	{
		private final NpcInstance _npc;
		private final int __ring;
		private final Zone __zone;
		private final Zone _GREEN_RING_LOC;
		private final Zone _BLUE_RING_LOC;
		private int _checks;

		ringRoutineTask(NpcInstance npc, int ring, Zone xZone, Zone GreenRingLoc, Zone BlueRingLoc, int checks)
		{
			_npc = npc;
			__ring = ring;
			__zone = xZone;
			_GREEN_RING_LOC = GreenRingLoc;
			_BLUE_RING_LOC = BlueRingLoc;
			_checks = checks;
		}

		@Override
		public void runImpl()
		{
			_checks--;
			for(Player player : __zone.getInsidePlayers())
			{
				boolean bMustGetDebuffed = false;
				switch(__ring)
				{
					case 0:	// Green. Must get debuffed if player is standing out of Green zone.
						bMustGetDebuffed = !player.isInZone(_GREEN_RING_LOC);
						break;
					case 1:	// Blue. Must get debuffed if player is standing out of Blue zone or is standing in Green zone.
						bMustGetDebuffed = !player.isInZone(_BLUE_RING_LOC) || player.isInZone(_GREEN_RING_LOC);
						break;
					case 2:	// Red. Must get debuffed if player is standing in Blue zone or is standing in Green zone.
						bMustGetDebuffed = player.isInZone(_BLUE_RING_LOC) || player.isInZone(_GREEN_RING_LOC);
						break;

					default:
						_log.warn(getClass().getSimpleName() + ": Istina AI __ring < 0 or __ring > 2!!! ringRoutineTask.runImpl()");
						break;
				}
				if(bMustGetDebuffed)
				{
					if (isHard)
						MANIFESTATION_OF_AUTHORITY_DEBUFF_HARD.getEffects(_npc, player);
					else
						MANIFESTATION_OF_AUTHORITY_DEBUFF_NORMAL.getEffects(_npc, player);
				}
			}
			if (_checks < 1)
			{
				triggerRings(_npc, -1);
				_authorityLock = false;
				_ringRoutineTask.cancel(true);
			}
		}
	}
}