package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.Location;

import java.util.concurrent.ScheduledFuture;

//By Evil_dnk

public class DimensionalWarp extends Reflection
{
	private static final int FIRST_PART_DELAY = 12; // Время прохождения инстанса (в минутах)
	private static final int NEXT_STAGE_ADDITIONAL_DELAY = 3; // Дополнительное вромя прохождения инстанса при прохождении уровня (в минутах)

	public int _stage = 0;
	public int _level = 1;
	public int _skilllevel = 1;
	private final OnDeathListener _monsterDeathListener = new MonsterDeathListener();
	private boolean secondWave = true;
	private boolean thirdWave = true;
	private double chanceofBes = 0.012;
	private int[] BES = {19553, 19554, 19555};
	private ScheduledFuture<?> _spawntrapstask;
	private ScheduledFuture<?> _debuftask;
	private long _instanceEndTime;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		_stage = 0;
		_level = 1;
		_skilllevel = 1;
		_instanceEndTime = System.currentTimeMillis() + FIRST_PART_DELAY * 60 * 1000L;
		startCollapseTimer(_instanceEndTime - System.currentTimeMillis());
	}

	public void setStage(int stage)
	{
		_stage = stage;
	}

	public int getStage()
	{
		return _stage;
	}

	public void setLevel(int level)
	{
		_level = level;
	}

	public int getLevel()
	{
		return _level;
	}

	public void secondPart(Player player)
	{
		if (getStage() == 20)
		{
			//Старт второй части
			stageStart(21);
			for (Player p : getPlayers())
				currentLevel(p);
		}
		player.teleToLocation(-76136, -216216, 4040, player.getReflection());
	}

	private void nextStage()
	{
		stageStart(getStage() + 1);

		for (Player p : getPlayers())
		{
			p.sendPacket(new ExShowScreenMessage(NpcString.THE_SURROUNDING_ENERGY_HAS_DISSIPATED, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
		}

		ThreadPoolManager.getInstance().schedule(() ->
		{
			for (Player p : getPlayers())
			{
				p.sendPacket(new ExShowScreenMessage(NpcString.S1_SECONDS_HAVE_BEEN_ADDED_TO_THE_INSTANCED_ZONE_DURATION, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(180)));
			}
			_instanceEndTime += NEXT_STAGE_ADDITIONAL_DELAY * 60 * 1000L;
			startCollapseTimer(_instanceEndTime - System.currentTimeMillis());
		}, 3500L);

		ThreadPoolManager.getInstance().schedule(() ->
		{
			for (Player player : getPlayers())
				currentLevel(player);
		}, 10000L);

		if (getStage() >= 1 && getStage() <= 20)
			openDoor(Integer.valueOf(13249999 + getStage()));
		else if (getStage() >= 21 && getStage() <= 35)
			openDoor(Integer.valueOf(17109979 + getStage()));
	}

	public void startinst(Player player, int pieces, double chance)
	{
		boolean canStart = true;
		chanceofBes = chance;
		int countofitems = pieces / getPlayers().size();

		for (Player p : getPlayers())
		{
			if (p.getInventory().getCountOf(39597) < countofitems)
			{
				for (Player ps : getPlayers())
				{
					ps.sendMessage(p.getName() + " have not enought items.");
					canStart = false;
				}
			}
		}

		if (!canStart || getStage() != 0)
			return;

			stageStart(1);

		for (Player p : getPlayers())
		{
			p.getInventory().destroyItemByItemId(39597, countofitems);
			currentLevel(p);
		}
	}

	private void stageStart(int stage)
	{
		_stage = stage;

		if (stage == 1)
		{
			ThreadPoolManager.getInstance().schedule(new SpawnStage(stage), 1000);
		}

		else if (stage > 1 && stage <= 35)
		{
			ThreadPoolManager.getInstance().schedule(new SpawnStage(stage), 10000);
		}

		if (stage == 11)
			setLevel(2);
		if (stage == 21)
			setLevel(3);
	}

	private void deleteNpcses(int id)
	{
		for (NpcInstance mob : getAllByNpcId(id, true))
			mob.deleteMe();
	}

	private void deleteTraps()
	{
		deleteNpcses(19556);
		deleteNpcses(19557);
		deleteNpcses(19558);
		deleteNpcses(19559);
		deleteNpcses(19560);
		deleteNpcses(19561);
	}

	private void currentLevel(Player player)
	{
		if (getStage() >= 1 && getStage() <= 35)
			player.sendPacket(new ExShowScreenMessage(NpcString.DIMENSIONAL_WARP_LV_S1, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage())));
	}

	private class SpawnStage extends RunnableImpl
	{
		private final int _spawnStage;

		public SpawnStage(int spawnStage)
		{
			_spawnStage = spawnStage;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_spawntrapstask != null)
				_spawntrapstask.cancel(true);
			if(_debuftask != null)
				_debuftask.cancel(true);
			long respawntime = 80000 - (getStage() * 2000);
			secondWave = false;
			thirdWave = false;
			spawnByGroup("dimensioal_warps_" + _spawnStage + "_1");
			ThreadPoolManager.getInstance().schedule(() ->
			{
				spawnByGroup("dimensioal_warps_" + _spawnStage + "_2");
				secondWave = true;
			}, 55000);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				spawnByGroup("dimensioal_warps_" + _spawnStage + "_3");
				thirdWave = true;
			}, 110000);
			_debuftask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DebufTask(), 5000L, 10000);
			_spawntrapstask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTrapsTask(), 1000L, respawntime);
		}
	}

	private class MonsterDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if (victim.isMonster() && victim.getNpcId() == 26090)
			{
				clearReflection(10, true);
				setReenterTime(System.currentTimeMillis());
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.THE_INSTANCED_ZONE_WILL_CLOSE_SOON, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
				clear();
			}
			else if (checkIsEmpty() && getStage() <= 35 && thirdWave)
			{
				if (getStage() == 20)
				{
					addSpawnWithoutRespawn(33975, new Location(-206952, 243912, 12191, 0), 0);
					openDoor(13250020);
					deleteTraps();
					if (_spawntrapstask != null)
						_spawntrapstask.cancel(true);
					if (_debuftask != null)
						_debuftask.cancel(true);

					return;
				}
				else if (getStage() <= 35)
				{
					deleteTraps();
					if (_debuftask != null)
						_debuftask.cancel(true);
					nextStage();
				}

			}
			else
			{
				if(Rnd.chance(chanceofBes))
					spawnBes(victim);
				victim.removeListener(_monsterDeathListener);
			}
		}
	}

	private boolean checkIsEmpty()
	{
		int[] mobs = {23462, 23463, 23464, 23465, 23467, 23468, 23469, 23470, 23471, 23472, 23474, 23475, 23476, 23477, 23478, 23480, 23481, 23482, 23483};
		boolean isEmpty = false;

		for (int npcId : mobs)
		{
			if (!getAllByNpcId(npcId, true).isEmpty())
			{
				isEmpty = false;
				break;
			}
			else
				isEmpty = true;
		}

		if (isEmpty)
			return true;

		return false;
	}

	@Override
	public void addObject(GameObject o)
	{
		super.addObject(o);

		if (o.isMonster())
			((Creature) o).addListener(_monsterDeathListener);
	}

	private class SpawnTrapsTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			deleteTraps();
			spawnByGroup("dimensioal_warps_traps_" + getStage());
			if(getStage() >= 21 && Rnd.chance(50 + getStage()))
			{
				spawnByGroup("dimensioal_warps_traps_" + getStage()+"_1");
			}
		}
	}

	private class DebufTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getStage() > 11 && getStage() <= 20)
				_skilllevel = 2;
			else if(getStage() > 20 && getStage() <= 30)
				_skilllevel = 3;
			else if(getStage() > 30)
				_skilllevel = 4;
			SkillEntry skill = SkillHolder.getInstance().getSkillEntry(16415, _skilllevel);
			for (Player p : getPlayers())
			{
				if(p.getReflection().getInstancedZoneId() == 250)
					skill.getEffects(p, p);
			}
		}
	}

	private void clear()
	{
		deleteTraps();
		if (_spawntrapstask != null)
			_spawntrapstask.cancel(true);
		if (_debuftask != null)
			_debuftask.cancel(true);
	}

	private void spawnBes(Creature victim)
	{
		int _bes = Rnd.get(BES);
		addSpawnWithoutRespawn(_bes, victim.getLoc(), 0);
		for (Player p : getPlayers())
		{
			if(_bes == 19553 )
				p.sendPacket(new ExShowScreenMessage(NpcString.DIMENSIONAL_IMP, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage())));
			else if(_bes == 19554 )
				p.sendPacket(new ExShowScreenMessage(NpcString.UNWORLDLY_IMP, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage())));
			else if(_bes == 19555 )
				p.sendPacket(new ExShowScreenMessage(NpcString.ABYSSAL_IMP, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(getStage())));
		}

	}

	@Override
	protected void onCollapse()
	{
		clear();
		super.onCollapse();
	}
}