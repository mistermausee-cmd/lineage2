package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.utils.Location;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessage;

/**
 * @author Rivelia
 */
public class SpiculaZero extends Reflection
{
	// NPC IDs.
	private final static int SPICULA_ZERO = 25901;
	private final static int DECO_MOTHER_NORNIL = 19396;

	private final static int BOZ_STAGE1 = 19298;
	private final static int BOZ_STAGE2 = 19305;
	private final static int BOZ_STAGE3 = 19403;
	private final static int BOZ_STAGE4 = BOZ_STAGE2;

	private final static int BOZMOB_SPICULA_ELITE_CAPTAIN = 19299;
	private final static int BOZMOB_SPICULA_ELITE_LIEUTNANT = 19300;
	private final static int BOZMOB_ELITE_SOLDIER_CLONE_1 = 19301;
	private final static int BOZMOB_SPICULA_ELITE_GUARD_1 = 19302;
	private final static int BOZMOB_ELITE_SOLDIER_CLONE_2 = 19303;
	private final static int BOZMOB_SPICULA_ELITE_GUARD_2 = 19304;
	
	// DOORS IDs.
	private final static int OFFSET_DOOR = 16200012;
	private final static int STAGE1_DOOR = 16200014;
	private final static int STAGE2_DOOR = 16200015;
	private final static int STAGE3_DOOR = 16200016;
	private final static int STAGE4_DOOR = 16200201;	// Valid new door

	// BOSS VARIABLES AND REFERENCES.
	private NpcInstance spiculaZero;
	private SpiculaZeroDeathListener _spiculaZeroDeathListener = new SpiculaZeroDeathListener();
	private SpiculaZeroHPDamageListener _spiculaZeroHPDamageListener = new SpiculaZeroHPDamageListener();

	// LOCATIONS.
	private final static Location SPICULA_ZERO_SPAWN_LOC = new Location(-119544, 87176, -12618);
	private final static Location DECO_MOTHER_NORNIL_SPAWN_LOC = new Location(-120080, 87176, -12608);
	private final static Location STAGE1_BOZ_GENERATOR_SPAWN_LOC = new Location(-114984, 87176, -12804);
	private final static Location STAGE2_BOZ_GENERATOR1_SPAWN_LOC = new Location(-115160, 86888, -12810);
	private final static Location STAGE2_BOZ_GENERATOR2_SPAWN_LOC = new Location(-115160, 87464, -12810);
	private final static Location STAGE2_BOZ_GENERATOR3_SPAWN_LOC = new Location(-115736, 87464, -12810);
	private final static Location STAGE2_BOZ_GENERATOR4_SPAWN_LOC = new Location(-115736, 86904, -12810);
	private final static Location STAGE3_BOZ_GENERATOR_SPAWN_LOC = new Location(-117352, 87176, -12715);
	private final static Location STAGE4_BOZ_GENERATOR1_SPAWN_LOC = new Location(-119544, 86536, -12618);
	private final static Location STAGE4_BOZ_GENERATOR2_SPAWN_LOC = new Location(-119544, 87816, -12619);

	// BOZ MOBS AND STAGE INFOS.
	private final static int BOZ_ON_IDLE_STATE_OFFSET = 0;
	private final static int BOZ_ON_ACTION_STATE_OFFSET = 1;
	private final static int SECURITY_MAX_BOZ_NPCS = 128;
	private final static boolean bDebug = false;
	private NpcInstance stage1_Boz;
	private NpcInstance stage2_Boz1;
	private NpcInstance stage2_Boz2;
	private NpcInstance stage2_Boz3;
	private NpcInstance stage2_Boz4;
	private NpcInstance stage3_Boz;
	private NpcInstance stage4_Boz1;
	private NpcInstance stage4_Boz2;
	private Stage1BozDeathListener _stage1BozDeathListener = new Stage1BozDeathListener();
	private Stage2BozHPDamageListener _stage2Boz1HPDamageListener = new Stage2BozHPDamageListener();
	private Stage2BozHPDamageListener _stage2Boz2HPDamageListener = new Stage2BozHPDamageListener();
	private Stage2BozHPDamageListener _stage2Boz3HPDamageListener = new Stage2BozHPDamageListener();
	private Stage2BozHPDamageListener _stage2Boz4HPDamageListener = new Stage2BozHPDamageListener();
	private Stage3BozDeathListener _stage3BozDeathListener = new Stage3BozDeathListener();
	private Stage4BozHPDamageListener _stage4Boz1HPDamageListener = new Stage4BozHPDamageListener();
	private Stage4BozHPDamageListener _stage4Boz2HPDamageListener = new Stage4BozHPDamageListener();
	private int curStage;
	private List<NpcInstance> bozNpcs = new ArrayList<NpcInstance>();
	private ScheduledFuture<?> _stage1BozRoutine = null;
	private ScheduledFuture<?> _stage3BozRoutine = null;
	private ScheduledFuture<?> _stage4Boz1Routine = null;
	private ScheduledFuture<?> _stage4Boz2Routine = null;
	private ScheduledFuture<?> _stage4Boz1RoutineSub = null;
	private ScheduledFuture<?> _stage4Boz2RoutineSub = null;

	// UTILITY FUNCTIONS.
	private void openGate(int id)
	{
		if (!getDoor(id).isOpen())
			openDoor(id);
	}
	private void closeGate(int id)
	{
		if (getDoor(id).isOpen())
			closeDoor(id);
	}
	private void goNextStage()
	{
		switch (curStage)
		{
			case 0:
				beginStage1();
				break;
			case 1:
				beginStage2();
				break;
			case 2:
				beginStage3();
				break;
			case 3:
				beginStage4();
				break;
		}
	}
	private void spawnAllBozAndSpiculaZero()
	{
		stage1_Boz = addSpawnWithoutRespawn(BOZ_STAGE1, STAGE1_BOZ_GENERATOR_SPAWN_LOC, 0);
		stage2_Boz1 = addSpawnWithoutRespawn(BOZ_STAGE2, STAGE2_BOZ_GENERATOR1_SPAWN_LOC, 0);
		stage2_Boz2 = addSpawnWithoutRespawn(BOZ_STAGE2, STAGE2_BOZ_GENERATOR2_SPAWN_LOC, 0);
		stage2_Boz3 = addSpawnWithoutRespawn(BOZ_STAGE2, STAGE2_BOZ_GENERATOR3_SPAWN_LOC, 0);
		stage2_Boz4 = addSpawnWithoutRespawn(BOZ_STAGE2, STAGE2_BOZ_GENERATOR4_SPAWN_LOC, 0);
		stage3_Boz = addSpawnWithoutRespawn(BOZ_STAGE3, STAGE3_BOZ_GENERATOR_SPAWN_LOC, 0);
		stage4_Boz1 = addSpawnWithoutRespawn(BOZ_STAGE4, STAGE4_BOZ_GENERATOR1_SPAWN_LOC, 0);
		stage4_Boz2 = addSpawnWithoutRespawn(BOZ_STAGE4, STAGE4_BOZ_GENERATOR2_SPAWN_LOC, 0);
		stage1_Boz.setRandomWalk(false);
		stage2_Boz1.setRandomWalk(false);
		stage2_Boz2.setRandomWalk(false);
		stage2_Boz3.setRandomWalk(false);
		stage2_Boz4.setRandomWalk(false);
		stage3_Boz.setRandomWalk(false);
		stage4_Boz1.setRandomWalk(false);
		stage4_Boz2.setRandomWalk(false);
		//stage1_Boz.setTargetable(false);
		//stage1_Boz.getFlags().getInvulnerable().start();
		stage4_Boz1.getFlags().getDeathImmunity().start();
		stage4_Boz2.getFlags().getDeathImmunity().start();

		spiculaZero = addSpawnWithoutRespawn(SPICULA_ZERO, SPICULA_ZERO_SPAWN_LOC, 0);
	}
	private void cancelTargetFor(NpcInstance npc)
	{
		int npcObjectID = npc.getObjectId();
		for (Player player : npc.getReflection().getPlayers())
		{
			if(player.getTarget() != null && player.getTarget().getObjectId() == npcObjectID)
			{
				player.setTarget(null);
				player.abortAttack(true, true);
				player.abortCast(true, true);
				player.sendActionFailed();
			}
		}
	}
	private boolean isABozMob(int npcId)
	{
		if (npcId == BOZMOB_SPICULA_ELITE_CAPTAIN ||
			npcId == BOZMOB_SPICULA_ELITE_LIEUTNANT ||
			npcId == BOZMOB_ELITE_SOLDIER_CLONE_1 ||
			npcId == BOZMOB_SPICULA_ELITE_GUARD_1 ||
			npcId == BOZMOB_ELITE_SOLDIER_CLONE_2 ||
			npcId == BOZMOB_SPICULA_ELITE_GUARD_2)
			return true;
		return false;
	}
	private void addToBozNpcs(NpcInstance npc)
	{
		bozNpcs.add(npc);
	}
	private void delFromBozNpcs(NpcInstance npc)
	{
		if (!bozNpcs.remove(npc))
			System.out.println("ERROR: Failed to remove Boz NPC from list!");
	}
	private void clearBozNpcs()
	{
		bozNpcs.clear();
	}
	private boolean isBozNpcsEmpty()
	{
		return bozNpcs.isEmpty();
	}
	private void spawnBozMobGroupsFor(NpcInstance _boz)
	{
		/*
		* Possible mobs:
		* BOZMOB_SPICULA_ELITE_CAPTAIN
		* BOZMOB_SPICULA_ELITE_LIEUTNANT
		* BOZMOB_ELITE_SOLDIER_CLONE_1
		* BOZMOB_SPICULA_ELITE_GUARD_1
		* BOZMOB_ELITE_SOLDIER_CLONE_2
		* BOZMOB_SPICULA_ELITE_GUARD_2
		* 
		* Generates the list and then spawn the listed mobs,
		* add them to bozNpcs list and attach them to a death listener
		* so it gets removed from bozNpcs upon death.
		*/
		if (bozNpcs.size() >= SECURITY_MAX_BOZ_NPCS)
		{
			if (bDebug)
				System.out.println("Warning: Prevented eventual server crash in Spicula Zero instance! Reflection ID: " + _boz.getReflection().getId() + ".");
			return;
		}

		List<Integer> npcIds = new ArrayList<Integer>();
		switch (curStage)
		{
			case 1:
			case 2:
				npcIds.add(BOZMOB_SPICULA_ELITE_LIEUTNANT);
				npcIds.add(BOZMOB_ELITE_SOLDIER_CLONE_1);
				npcIds.add(BOZMOB_SPICULA_ELITE_GUARD_1);
				npcIds.add(BOZMOB_ELITE_SOLDIER_CLONE_2);
				npcIds.add(BOZMOB_SPICULA_ELITE_GUARD_2);
				break;
			case 3:
			case 4:
				npcIds.add(BOZMOB_SPICULA_ELITE_CAPTAIN);
				npcIds.add(BOZMOB_SPICULA_ELITE_LIEUTNANT);
				npcIds.add(BOZMOB_ELITE_SOLDIER_CLONE_1);
				npcIds.add(BOZMOB_SPICULA_ELITE_GUARD_1);
				npcIds.add(BOZMOB_ELITE_SOLDIER_CLONE_2);
				npcIds.add(BOZMOB_SPICULA_ELITE_GUARD_2);
				break;
		}
		for (int i : npcIds)
		{
			final NpcInstance bozMob = addSpawnWithoutRespawn(i, _boz.getLoc(), 0);
			bozMob.addListener(new BozMobDeathListener());
			// Generate aggro to all players.
			for(Player p : bozMob.getReflection().getPlayers())
				bozMob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
			addToBozNpcs(bozMob);
		}
	}
	@Override
	public void clearReflection(int timeInMinutes, boolean message)
	{
		if (isDefault())
			return;

		if (_stage1BozRoutine != null)
			_stage1BozRoutine.cancel(true);
		if (_stage3BozRoutine != null)
			_stage3BozRoutine.cancel(true);
		if (_stage4Boz1Routine != null)
			_stage4Boz1Routine.cancel(true);
		if (_stage4Boz2Routine != null)
			_stage4Boz2Routine.cancel(true);
		if (_stage4Boz1RoutineSub != null)
			_stage4Boz1RoutineSub.cancel(true);
		if (_stage4Boz2RoutineSub != null)
			_stage4Boz2RoutineSub.cancel(true);
		for(NpcInstance npc : getNpcs())
		{
			final int npcId = npc.getNpcId();
			if (npcId != DECO_MOTHER_NORNIL && npcId != SPICULA_ZERO && !isABozMob(npcId))
				npc.deleteMe();
		}
		startCollapseTimer(timeInMinutes * 60000L);
		if (message)
			for (Player pl : getPlayers())
				if (pl != null)
					pl.sendPacket(new SystemMessage(2106).addNumber(timeInMinutes));
	}
	// .

	// STAGE FUNCTIONS.
	private void beginStage1()
	{
		/*
		* Stage description:
		* Open door.
		* Spawn Boz. Is untargetable and invulnerable to any attack.
		* The Boz spawns Spicula clones (1 of each except Captain) every 30 seconds.
		* When all the spawned mobs from Boz dies, it becomes targetable and vulnerable, if it is killed, next stage goes.
		*/
		if (curStage > 0)
			return;

		curStage = 1;
		clearBozNpcs();
		spawnAllBozAndSpiculaZero();
		openGate(STAGE1_DOOR);
		stage1_Boz.addListener(_stage1BozDeathListener);
		_stage1BozRoutine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage1BozRoutine(stage1_Boz), 0, 30000);
	}
	private void beginStage2()
	{
		/*
		* Stage description:
		* Open door.
		* Spawn 4 Boz. Is vulnerable and attackable at any time. If any of those 4 Boz dies, the other dies too.
		* The Boz spawns Spicula clones when Boz dies.
		* When all spawned mobs including Boz are killed, next stage goes.
		*/
		if (curStage > 1)
			return;

		curStage = 2;
		clearBozNpcs();
		openGate(STAGE2_DOOR);
		stage2_Boz1.addListener(_stage2Boz1HPDamageListener);
		stage2_Boz2.addListener(_stage2Boz2HPDamageListener);
		stage2_Boz3.addListener(_stage2Boz3HPDamageListener);
		stage2_Boz4.addListener(_stage2Boz4HPDamageListener);
	}
	private void beginStage3()
	{
		/*
		* Stage description:
		* Open door.
		* Spawn Boz. Is vulnerable and attackable at any time.
		* The Boz spawns Spicula clones (1 of each) every 5? seconds.
		* 1 of each clones are pre-spawned when stage goes on.
		* When all spawned mobs including Boz are killed, next stage goes.
		*/
		if (curStage > 2)
			return;

		curStage = 3;
		clearBozNpcs();
		openGate(STAGE3_DOOR);
		stage3_Boz.addListener(_stage3BozDeathListener);
		_stage3BozRoutine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage3BozRoutine(stage3_Boz), 5000, 5000);
	}
	private void beginStage4()
	{
		/*
		* Stage description:
		* Open door.
		* Spawn Spicula Zero.
		* Spawn 2 Boz on each sides of Spicula Zero. Is untargetable and invulnerable to any attack. Until Spicula Zero is attacked, they have no effect whatsoever.
		* When Spicula Zero is attacked, the door closes, and 1 Boz out of the 2 are chosen to activate after 40 seconds.
		* The Boz spawns Spicula clones (1 of each) every 10? seconds until its about to die, to schedule again for 40 sec later. When it starts summoning, the other Boz is activated for 40 sec.
		* When Boz is casting the skill that summons monsters, it becomes targetable and vulnerable to any attack.
		* However, it cannot die. When its HP reach 1, the Boz becomes untargetable again and cancels the summons of the Spicula Clones.
		*/
		if (curStage > 3)
			return;

		curStage = 4;
		clearBozNpcs();
		openGate(STAGE4_DOOR);
		spiculaZero.addListener(_spiculaZeroDeathListener);
		spiculaZero.addListener(_spiculaZeroHPDamageListener);
		stage4_Boz1.addListener(_stage4Boz1HPDamageListener);
		stage4_Boz2.addListener(_stage4Boz2HPDamageListener);
	}
	// .

	// LISTENERS.
	private class SpiculaZeroDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == SPICULA_ZERO)
			{
				clearReflection(5, true);
				setReenterTime(System.currentTimeMillis());
			}
		}
	}
	private class SpiculaZeroHPDamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature self, double damage, Creature attacker, Skill skill)
		{
			if(self.isNpc() && self.getNpcId() == SPICULA_ZERO)
			{
				int _Rnd = (int)Math.round(Rnd.get());
				_stage4Boz1Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz1, 1), _Rnd == 0 ? 40000 : 80000, 40000);
				_stage4Boz2Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz2, 2), _Rnd == 1 ? 40000 : 80000, 40000);
				self.removeListener(_spiculaZeroHPDamageListener);
			}
		}
	}
	private class Stage1BozDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == BOZ_STAGE1)
			{
				if (_stage1BozRoutine != null)
					_stage1BozRoutine.cancel(true);
				if (isBozNpcsEmpty())
					goNextStage();
			}	
		}
	}
	private class Stage2BozHPDamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature self, double damage, Creature attacker, Skill skill)
		{
			if(self.isNpc() && self.getNpcId() == BOZ_STAGE2)
			{
				if (stage2_Boz1 != null && !stage2_Boz1.isDead())
				{
					stage2_Boz1.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
					stage2_Boz1.removeListener(_stage2Boz1HPDamageListener);
					spawnBozMobGroupsFor(stage2_Boz1);
					stage2_Boz1.doDie(attacker);
				}
				if (stage2_Boz2 != null && !stage2_Boz2.isDead())
				{
					stage2_Boz2.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
					stage2_Boz2.removeListener(_stage2Boz2HPDamageListener);
					spawnBozMobGroupsFor(stage2_Boz2);
					stage2_Boz2.doDie(attacker);
				}
				if (stage2_Boz3 != null && !stage2_Boz3.isDead())
				{
					stage2_Boz3.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
					stage2_Boz3.removeListener(_stage2Boz3HPDamageListener);
					spawnBozMobGroupsFor(stage2_Boz3);
					stage2_Boz3.doDie(attacker);
				}
				if (stage2_Boz4 != null && !stage2_Boz4.isDead())
				{
					stage2_Boz4.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
					stage2_Boz4.removeListener(_stage2Boz4HPDamageListener);
					spawnBozMobGroupsFor(stage2_Boz4);
					stage2_Boz4.doDie(attacker);
				}
			}
		}
	}
	private class Stage3BozDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == BOZ_STAGE3)
			{
				if (_stage3BozRoutine != null)
					_stage3BozRoutine.cancel(true);
				if (isBozNpcsEmpty())
					goNextStage();
			}	
		}
	}
	private class Stage4BozHPDamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature self, double damage, Creature attacker, Skill skill)
		{
			if(self.isNpc() && self.getNpcId() == BOZ_STAGE4)
			{
				if (self.getCurrentHp() - damage < 2)
				{
					cancelTargetFor((NpcInstance)self);
					self.setTargetable(false);
					self.setCurrentHp(self.getMaxHp(), false);
					self.getFlags().getInvulnerable().start();
				}
				if (stage4_Boz1 != null && stage4_Boz1 == self)
				{
					if (_stage4Boz1Routine != null)	// != null means we are not running Sub. Reset the timer.
					{
						_stage4Boz1Routine.cancel(true);
						_stage4Boz1Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz1, 1), 40000, 40000);
					}
				}
				else if (stage4_Boz2 != null && stage4_Boz2 == self)
				{
					if (_stage4Boz2Routine != null)	// != null means we are not running Sub. Reset the timer.
					{
						_stage4Boz2Routine.cancel(true);
						_stage4Boz2Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz2, 2), 40000, 40000);
					}
				}
			}
		}
	}
	private class BozMobDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && isABozMob(self.getNpcId()))
			{
				delFromBozNpcs((NpcInstance)self);
				if (isBozNpcsEmpty())
				{
					if (curStage == 1)	// Make Boz vulnerable.
					{
						if (stage1_Boz == null || stage1_Boz.isDead())
						{
							goNextStage();
							return;
						}
						stage1_Boz.setTargetable(true);
						stage1_Boz.getFlags().getInvulnerable().stop();
					}
					else if (curStage == 2)
					{
						if ((stage2_Boz1 == null || stage2_Boz1.isDead()) &&
							(stage2_Boz2 == null || stage2_Boz2.isDead()) &&
							(stage2_Boz3 == null || stage2_Boz3.isDead()) &&
							(stage2_Boz4 == null || stage2_Boz4.isDead()))
							goNextStage();
					}
					else if (curStage == 3)
					{
						if (stage3_Boz == null || stage3_Boz.isDead())
							goNextStage();
					}
				}
			}
		}
	}
	// .

	// SCHEDULED TASKS.
	private class Stage1BozRoutine extends RunnableImpl
	{
		private final NpcInstance _npc;

		public Stage1BozRoutine(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_npc == null || _npc.isDead())
			{
				_stage1BozRoutine.cancel(true);
				return;
			}
			_npc.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
			spawnBozMobGroupsFor(_npc);
			cancelTargetFor(_npc);
//			_npc.setTargetable(false);
//			_npc.getFlags().getInvulnerable().start();
		}
	}
	private class Stage3BozRoutine extends RunnableImpl
	{
		private final NpcInstance _npc;

		public Stage3BozRoutine(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_npc == null || _npc.isDead())
			{
				_stage3BozRoutine.cancel(true);
				return;
			}
			_npc.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
			spawnBozMobGroupsFor(_npc);
		}
	}
	private class Stage4BozRoutine extends RunnableImpl
	{
		private final NpcInstance _npc;
		private final int _bozNum;

		public Stage4BozRoutine(NpcInstance npc, int bozNum)
		{
			_npc = npc;
			_bozNum = bozNum;
		}

		@Override
		public void runImpl() throws Exception
		{
			_npc.setTargetable(true);
			if(_npc.isInvulnerable())
				_npc.getFlags().getInvulnerable().stop();
			_npc.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
			switch (_bozNum)
			{
				case 1:
					if (_stage4Boz1RoutineSub == null)
						_stage4Boz1RoutineSub = ThreadPoolManager.getInstance().schedule(new Stage4BozRoutineSub(_npc, 1), 5000);
					break;
				case 2:
					if (_stage4Boz2RoutineSub == null)
						_stage4Boz2RoutineSub = ThreadPoolManager.getInstance().schedule(new Stage4BozRoutineSub(_npc, 2), 5000);
					break;
			}
		}
	}
	private class Stage4BozRoutineSub extends RunnableImpl
	{
		private final NpcInstance _npc;
		private final int _bozNum;

		public Stage4BozRoutineSub(NpcInstance npc, int bozNum)
		{
			_npc = npc;
			_bozNum = bozNum;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_bozNum == 1 && _stage4Boz1Routine != null)
				_stage4Boz1Routine.cancel(true);
			if (_bozNum == 2 && _stage4Boz2Routine != null)
				_stage4Boz2Routine.cancel(true);

			if (_npc.isTargetable(_npc))
			{
				_npc.setNpcState(BOZ_ON_ACTION_STATE_OFFSET);
				spawnBozMobGroupsFor(_npc);
				switch (_bozNum)
				{
					case 1:
						_stage4Boz1RoutineSub = ThreadPoolManager.getInstance().schedule(new Stage4BozRoutineSub(_npc, 1), 5000);
						break;
					case 2:
						_stage4Boz2RoutineSub = ThreadPoolManager.getInstance().schedule(new Stage4BozRoutineSub(_npc, 2), 5000);
						break;
				}
			}
			else
			{
				_npc.setNpcState(BOZ_ON_IDLE_STATE_OFFSET);
				switch (_bozNum)
				{
					case 1:
						_stage4Boz1RoutineSub = null;	// Is that necessary?
						_stage4Boz1Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz1, 1), 40000, 40000);
						break;
					case 2:
						_stage4Boz2RoutineSub = null;	// Is that necessary?
						_stage4Boz2Routine = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Stage4BozRoutine(stage4_Boz2, 2), 40000, 40000);
						break;
				}
			}
		}
	}
	// .

	// EVENTS.
	@Override
	protected void onCreate()
	{
		super.onCreate();

		closeGate(OFFSET_DOOR); closeGate(STAGE1_DOOR); closeGate(STAGE2_DOOR); closeGate(STAGE3_DOOR); closeGate(STAGE4_DOOR);
		final NpcInstance motherNornil = addSpawnWithoutRespawn(DECO_MOTHER_NORNIL, DECO_MOTHER_NORNIL_SPAWN_LOC, 0);
		motherNornil.getFlags().getParalyzed().start();
		motherNornil.setRandomWalk(false);
		motherNornil.setTargetable(false);
		motherNornil.getFlags().getInvulnerable().start();
		ThreadPoolManager.getInstance().schedule(() ->
		{
			goNextStage();
		}, 5000);
	}
	// .
}