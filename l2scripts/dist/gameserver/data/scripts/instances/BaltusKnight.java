/*
 * Copyright Mazaffaka Project (c) 2013.
 */

package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

//By Evil_dnk

public class BaltusKnight extends Reflection
{
	private static final int Antharas = 29223;
	private static final int Lash = 19131;
	private Location Antharasspawn = new Location(176646, 114768, -7708, 21156);
	private CurrentHpListener _currentHpListener = new CurrentHpListener();
	public static int _stage = 0;
	protected final List<Location> _hidecave = new ArrayList<Location>();
	protected final List<Location> _returnecave = new ArrayList<Location>();
	private ScheduledFuture<?> _waveMovementTask;
	private static final Location CENTER = new Location(180664, 114904, -7689, 0);
	private static final Location INCAVE = new Location(186120, 114808, -8245, 0);
	private NpcInstance _antharas;
	private NpcInstance _lash;

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);

	}

	@Override
	protected void onCreate()
	{
		super.onCreate();
		for(NpcInstance npc : getNpcs())
			npc.setRandomWalk(false);
		_hidecave.add(new Location(179928, 114872, -7733));
		_hidecave.add(new Location(180936, 114888, -7702));
		_hidecave.add(new Location(183192, 114872, -7992));
		_hidecave.add(new Location(186120, 114808, -8245));

		_returnecave.add(new Location(186024, 114616, -8245));
		_returnecave.add(new Location(183192, 114872, -7992));
		_returnecave.add(new Location(180936, 114888, -7702));
		_returnecave.add(new Location(179928, 114872, -7733));

		_waveMovementTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MonsterMovementTask(), 1000L, 1000L);

		ThreadPoolManager.getInstance().schedule(new AntarasSpawn(this), 1);

	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{

			if(actor.getNpcId() == Antharas)
			{
				if(actor == null || actor.isDead())
				{
					return;
				}

				if(actor.getCurrentHpPercents() <= 15 && _stage == 0)
				{
					_stage = 1;
				}

				if(actor.getCurrentHpPercents() <= 7 && _stage == 6)
				{
					spawnByGroup("baltus_healers1");

					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.CHILDREN_HEAL_ME_WITH_YOUR_NOBLE_SACRIFICE, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
					_stage = 7;

					ThreadPoolManager.getInstance().schedule(() ->
					{
						if(getStage() == 7)
						{
							_stage = 8;
							spawnByGroup("baltus_healers2");

							ThreadPoolManager.getInstance().schedule(() ->
							{
								for(Player player : getPlayers())
									player.sendPacket(new ExShowScreenMessage(NpcString.YOUR_SACRIFICES_WILL_BECOME_A_NEW_RESCUE, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
								if(getStage() == 8)
								{
									_stage = 9;
									spawnByGroup("baltus_healers3");
									ThreadPoolManager.getInstance().schedule(() ->
									{
										if(getStage() == 9)
										{
											_stage = 10;
											spawnByGroup("baltus_healers4");
										}
									}, 35000);
								}
							}, 35000);
						}
					}, 35000);
				}
			}

			if(actor.getCurrentHp() <= 100000 && _stage < 11)
			{
				_stage = 11;
				for(Player player : getPlayers())
					player.sendPacket(new ExShowScreenMessage(NpcString.BE_HAPPY_THAT_IM_BACKING_OFF_TODAY, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
			}
		}
	}

	public class AntarasSpawn extends RunnableImpl
	{
		Reflection _r;

		public AntarasSpawn(Reflection r)
		{
			_r = r;
		}

		@Override
		public void runImpl()
		{
			Location Loc = Antharasspawn;
			_antharas = addSpawnWithoutRespawn(Antharas, Loc, 0);
			_antharas.setCurrentHp(_antharas.getMaxHp() * 0.2, false, true);

			NpcInstance Felo = addSpawnWithoutRespawn(19128, new Location(175176, 114744, -7708, 0), 0);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.IS_THIS_ANTHARAS);
			}, 10000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.I_THINK_WE_HURT_HIM_GOOD_WE_CAN_DEFEAT_HIM);
			}, 20000);


			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.WATCH_YOUR_WORDS);
			}, 35000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.EVERYONE_LISTEN);
			}, 40000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.THIS_IS_THEIR_LIMIT);
			}, 43000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.DO_YOUR_BEST_FOR_THOSE_WHO_DIED_FOR_US);
			}, 46000);

			NpcInstance Eiteld= addSpawnWithoutRespawn(19129, new Location(175176, 114819, -7712, 64), 0);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.WE_HAVE_NO_MORE_CHANCE_WE_MUST_GO_BACK);
			}, 14000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.YOU_WANT_MORE_LOSSES);

				for(Player player : getPlayers())
					player.sendPacket(new ExShowScreenMessage(NpcString.HOW_STUBBORN_SQUIRMING_TIL_THE_LAST_MINUTE, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));

			}, 30000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.I_CANT_DIE_LIKE_THIS_I_WILL_GET_BACKUP_FROM_THE_KINGDOM);
				DefaultAI ai = (DefaultAI) Eiteld.getAI();
				Eiteld.setRunning();
				ai.addTaskMove((Location.findPointToStay(175544, 115992, -7733, 40, 40, Eiteld.getGeoIndex())), false);
			}, 48000);

			ThreadPoolManager.getInstance().schedule(() ->
			{
				 Eiteld.deleteMe();

			}, 60000);

			NpcInstance Faula = addSpawnWithoutRespawn(19130, new Location(175604, 115413, -7708, -8848), 0);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(Felo, NpcString.CHARGE);
				for(NpcInstance npc : getAllByNpcId(19133, true))
					npc.setBusy(true);
				for(NpcInstance npc : getAllByNpcId(19136, true))
					npc.setBusy(true);
				Faula.setBusy(true);
				Eiteld.setBusy(true);
				Felo.setBusy(true);

			}, 50000);

			_antharas.addListener(_currentHpListener);
		}
	}

	private class MonsterMovementTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_antharas != null && !_antharas.isDead())
			{
				if(getStage() == 1 || getStage() == 2 || getStage() == 4 || getStage() == 11)
				{
					if(!_antharas.isMoving && _antharas.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST)
					{
						Location npcLoc = _antharas.getLoc();
						_antharas.setRunning();
						DefaultAI ai = (DefaultAI) _antharas.getAI();
						if (_stage == 1)
						{
							ai.addTaskMove(Location.findPointToStay(CENTER, 40, 40, _antharas.getGeoIndex()), true);
							double curloc = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), CENTER.getX(), CENTER.getY(), CENTER.getZ(), true);
							if (curloc <= 300)
							{
								_antharas.setTargetable(false);
								_antharas.teleToLocation(new Location(181256, 114792, -7702), _antharas.getReflection());
								_stage = 2;

							}
						}
						if (_stage == 2)
						{
							ai.addTaskMove(Location.findPointToStay(INCAVE, 40, 40, _antharas.getGeoIndex()), true);
							double curloc = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), INCAVE.getX(), INCAVE.getY(), INCAVE.getZ(), true);
							if (curloc <= 150)
							{
								_stage = 3;
								SecondStage();
								_antharas.getFlags().getInvulnerable().start();
								if (!_antharas.isParalyzed())
									_antharas.getFlags().getParalyzed().start();
								_antharas.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, _antharas);
							}
						}
						if (_stage == 4)
						{
							ai.addTaskMove(Location.findPointToStay(CENTER, 40, 40, _antharas.getGeoIndex()), true);
							double curloc = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), CENTER.getX(), CENTER.getY(), CENTER.getZ(), true);
							if (curloc <= 250)
							{
								_stage = 5;
								_antharas.setTargetable(true);
								for(Player player : getPlayers())
									player.sendPacket(new ExShowScreenMessage(NpcString.SHOOT_FIRE_AT_THE_IMBECILE, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
								ThreadPoolManager.getInstance().schedule(() ->
								{
									for(NpcInstance bombers : getNpcs())
									{
										_stage = 6;
										for (Player p : getPlayers())
											_antharas.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);
										if(bombers.getNpcId() ==  29227)
											bombers.getAI().getActor().doCast(SkillHolder.getInstance().getSkillEntry(14390, 1), bombers.getAI().getActor(), true);
									}
								}, 10000L);
							}
						}
						if (_stage == 11)
						{
							ai.addTaskMove(Location.findPointToStay(Antharasspawn, 40, 40, _antharas.getGeoIndex()), true);
							double curloc = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), Antharasspawn.getX(), Antharasspawn.getY(), Antharasspawn.getZ(), true);

							if (curloc <= 350)
							{
								_stage = 12;

								for(Player player : getPlayers())
								{
									player.sendPacket(new EarthQuakePacket(player.getLoc(), 60, 15));
									player.sendPacket(new ExShowScreenMessage(NpcString.IMBECILESYOULL_DISAPPEAR_ON_THE_DAY_OF_DESTRUCTION, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
								}
								_antharas.broadcastPacketToOthers(new MagicSkillUse(_antharas, _antharas, 2036, 1, 500, 600000));

								ThreadPoolManager.getInstance().schedule(() ->
								{
									_antharas.deleteMe();
									for(NpcInstance npc : getAllByNpcId(29230, true))
										npc.deleteMe();
								}, 1500);

								ThreadPoolManager.getInstance().schedule(() ->
								{
								    finishInst();

								}, 5000);
							}
						}
					}
				}
			}
		}
	}

	public void SecondStage()
	{
		addSpawnWithoutRespawn(29226, new Location(181496, 115320, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181432, 115224, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181368, 115112, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181352, 114968, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181336, 114824, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181336, 114568, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181336, 114568, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181640, 114488, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181640, 114552, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181640, 114664, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181624, 114808, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181624, 114968, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181640, 115128, -7702, 0), 0);
		addSpawnWithoutRespawn(29226, new Location(181640, 115288, -7702, 0), 0);
		spawnByGroup("baltus_bomber");

		ThreadPoolManager.getInstance().schedule(() ->
		{
			if(_antharas != null && !_antharas.isDead())
			{
				if(_antharas.isInvulnerable())
					_antharas.getFlags().getInvulnerable().stop();
				if(_antharas.isParalyzed())
					_antharas.getFlags().getParalyzed().stop();
				_antharas.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
			}

			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.NOT_ENOUGH_I_WILL_HAVE_TO_GO_MYSELF, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
			_stage = 4;
			_antharas.teleToLocation(new Location(181640, 114840, -7702), _antharas.getReflection());

		}, 60000L);

		for(Player player : getPlayers())
			player.sendPacket(new ExShowScreenMessage(NpcString.CHILDREN_WITH_NOBLE_YOUR_SACRIFICE_GIVE_THEM_PAIN, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER));
	}

	public void setStage(int stage)
	{
		_stage = stage;
	}

	public int getStage()
	{
		return _stage;
	}

	public NpcInstance getAntharas()
	{
		if(_antharas == null)
			return null;

		return _antharas;
	}

	public void finishInst()
	{
		for(Player p : getPlayers())
		{
			p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
		}
		setReenterTime(System.currentTimeMillis());
		startCollapseTimer(10 * 60 * 1000L);
		cleanup();
		addSpawnWithoutRespawn(19133, new Location(179589, 114623, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179589, 114688, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179589, 114749, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179589, 114816, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179592, 114880, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179591, 114944, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179591, 115005, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179588, 115071, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179589, 114557, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179590, 115135, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179525, 114557, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179525, 114623, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179525, 114688, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179525, 114752, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179525, 114816, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179527, 114882, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179524, 114943, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179526, 115007, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179529, 115069, -7712, 0), 0);
		addSpawnWithoutRespawn(19133, new Location(179526, 115135, -7712, 0), 0);

		addSpawnWithoutRespawn(19136, new Location(179452, 114813, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179453, 114878, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 114944, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 115008, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 115072, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 115136, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 114752, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 114688, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 114624, -7720, 0), 0);
		addSpawnWithoutRespawn(19136, new Location(179456, 114560, -7720, 0), 0);

		addSpawnWithoutRespawn(19137, new Location(179392, 115136, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 115072, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 115008, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114944, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114880, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114816, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114752, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114688, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179392, 114624, -7716, 0), 0);
		addSpawnWithoutRespawn(19137, new Location(179388, 114560, -7716, 0), 0);

		_lash = addSpawnWithoutRespawn(Lash, new Location(179687, 114849, -7712, 0), 0);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			Functions.npcSay(_lash, NpcString.AH_DID_THE_BACKUP_GET_WIPED_OUT_LOOKS_LIKE_WERE_LATE);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				Functions.npcSay(_lash, NpcString.YOU_GUYS_ARE_THE_MERCENARIES);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					Functions.npcSay(_lash, NpcString.HES_QUIET_AGAIN_THANKS);
					ThreadPoolManager.getInstance().schedule(() ->
					{
						Functions.npcSay(_lash, NpcString.THIS_WE_BROUGHT_THIS_TO_SUPPORT_THE_BACKUP_BUT_WE_COULD_GIVE_THESE_TO_YOU);
						ThreadPoolManager.getInstance().schedule(() ->
						{
							Functions.npcSay(_lash, NpcString.COURAGEOUS_ONES_WHO_SUPPORTED_ANTHARAS_FORCE_COME_AND_TAKE_THE_KINGDOMS_REWARD);
							ThreadPoolManager.getInstance().schedule(() ->
							{
								Functions.npcSay(_lash, NpcString.ARE_THERE_THOSE_WHO_DIDNT_RECEIVE_THE_REWARDS_YET_COME_AND_GET_IT_FROM_ME);
							}, 3000);

						}, 3000);

					}, 3000);

				}, 3000);

			}, 3000);

		}, 3000);
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();
		cleanup();
	}

	protected void cleanup()
	{
		if(_waveMovementTask != null)
			_waveMovementTask.cancel(true);
	}
}
