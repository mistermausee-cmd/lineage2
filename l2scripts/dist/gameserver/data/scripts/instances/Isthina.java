package instances;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

/**
 * @author Iqman, GW & Rivelia.
 */
public class Isthina extends Reflection
{
	private static final int ISTHINA_LIGHT = 29195;
	private static final int ISTHINA_HARD = 29196;
	private static final int BALLISTA = 19021;
	private static final int ENDING_RUMIESE = 33293;
	private static final int RUMIESE_OUTSIDE = 33151;
	private static final int RUMIESE_INSIDE = 33293;
	private static final int INSTANCE_ID_LIGHT = 169;
	private static final int INSTANCE_ID_HARD = 170;

	private static final int ISTINA_SCRYSTAL_ITEM_ID = 37506;

	private static final Location OUTSIDE = new Location(-178470, 147111, 2132);
	private static final Location ENTRANCE = new Location(-177120, 142293, -11274);
	private static final Location LAIR_ENTRANCE = new Location(-177104, 146452, -11389);
	private static final Location CENTER = new Location(-177125, 147856, -11384);
	private static final int BOX_CONTAINING_MATK = 30371;
	private static final int MAGIC_FILLED_BOX = 30374;
	private static final int BALLISTA_MAX_DAMAGE = 4660000;

	private ZoneListener _epicZoneListener = new ZoneListener();
	private CurrentHpListener _currentHpListener = new CurrentHpListener();
	private ScheduledFuture<?> _ballistaTimer = null;
	private boolean _ballistaPresented = false;
    private short _ballistaReadySeconds = 5;
	private boolean _startLaunched = false;
	private boolean _lockedTurn = false;
	private boolean _isHardInstance = false;
    private boolean _instanceDone = false;
    private int _ballistaSeconds = 30;
    private long _ballistaDamage = 0L;
    public NpcInstance _ballista = null;

	@Override
	protected void onCreate()
	{
		super.onCreate();
		getZone("[istina_epic]").addListener(_epicZoneListener);
		_isHardInstance = getInstancedZoneId() == INSTANCE_ID_HARD;
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(_startLaunched)
				return;

			if(!cha.isPlayer())
				return;
		
			if(zone.getInsidePlayers().size() >= getPlayers().size())
			{
				closeDoor(14220100);
				closeDoor(14220101);			
				ThreadPoolManager.getInstance().schedule(new StartIsthinaOpenMovie(), 15000L);
				_startLaunched = true;
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}

	private class StartIsthinaOpenMovie extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.startScenePlayer(SceneMovie.SCENE_ISTINA_OPENING);

			ThreadPoolManager.getInstance().schedule(new SpawnIsthina(), SceneMovie.SCENE_ISTINA_OPENING.getDuration());
		}
	}

	private class SpawnIsthina extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(_isHardInstance ? ISTHINA_HARD : ISTHINA_LIGHT, new Location(-177125, 147856, -11384, 49140), 0);
		}
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(actor == null || actor.isDead() || actor.getNpcId() != BALLISTA)
				return;
			_ballistaDamage += damage;
		}
	}

	public void presentBallista(NpcInstance npc)
	{
		if(_ballistaPresented)
			return;

		_ballistaPresented = true;

		for(Player player : getPlayers())
			player.startScenePlayer(SceneMovie.SCENE_ISTINA_BRIDGE);

		for(NpcInstance _npc : getNpcs())
		{
			if(_npc.isMonster() && !_npc.isRaid())
				_npc.deleteMe();
		}

		ThreadPoolManager.getInstance().schedule(new SpawnBallista(npc), 7200L); // 7.2 secs for movie
	}

	private class SpawnBallista extends RunnableImpl
	{
		private NpcInstance _boss;

		SpawnBallista(NpcInstance boss)
		{
			_boss = boss;
		}	
	
		@Override
		public void runImpl() throws Exception
		{
			_ballista = addSpawnWithoutRespawn(BALLISTA, new Location(-177125, 147856, -11384, 49140), 0);
			_ballista.addListener(_currentHpListener);
			_ballista.getFlags().getDeathImmunity().start();
			_ballista.getFlags().getParalyzed().start();
			_ballista.setRandomWalk(false);
			_ballista.setTargetable(false);

			ThreadPoolManager.getInstance().schedule(new BallistaPrepareTask(_boss), 8000L);	

			setReenterTime(System.currentTimeMillis());	
		}
	}

	private class BallistaPrepareTask extends RunnableImpl
	{
		private NpcInstance _boss;

		BallistaPrepareTask(NpcInstance boss)
		{
			_boss = boss;
		}	
	
		@Override
		public void runImpl() throws Exception
		{
			if(_ballistaReadySeconds > 0)
			{
				for(Player player : getPlayers())
					player.sendPacket(new ExShowScreenMessage(NpcString.AFTER_S1_SECONDS_THE_CHARGING_MAGIC_BALLISTA_STARTS, 500, ScreenMessageAlign.MIDDLE_CENTER, String.valueOf(_ballistaReadySeconds)));
			}
			else
			{
				for(Player player : getPlayers())
					player.sendPacket(new ExShowScreenMessage(NpcString.START_CHARGING_MANA_BALLISTA, 3000, ScreenMessageAlign.MIDDLE_CENTER, String.valueOf(_ballistaReadySeconds)));

				startBallista(_boss);

				_ballista.setTargetable(true);
				return;
			}

			_ballistaReadySeconds -= 1;

			ThreadPoolManager.getInstance().schedule(this, 1000L);
		}
	}

	private NpcInstance getBalista()
	{
		if(_ballista == null)
			System.out.println("_ballista is null!");
		return _ballista;
	}

	public void startBallista(final NpcInstance istina)
	{
		_ballistaTimer = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if(_ballistaSeconds <= 0 && !_instanceDone)
			{
				_ballistaTimer.cancel(true);

				if(_ballistaDamage < getBalista().getMaxHp())
				{
					if(getPlayers().size() < 1)
						return;

					istina.getFlags().getParalyzed().stop();
					istina.getFlags().getInvulnerable().stop();
					//istina.getAggroList().addDamageHate(getPlayers().get(0), 1000000, 999);
					//istina.doDie(getPlayers().get(0));
					istina.deleteMe();
					for(Player player : getPlayers())
					{
						player.startScenePlayer(SceneMovie.SCENE_ISTINA_ENDING_B);
					}
				}
				else
				{
					if(getPlayers().size() < 1)
						return;
					istina.getFlags().getParalyzed().stop();
					istina.getFlags().getInvulnerable().stop();
					istina.getAggroList().addDamageHate(getPlayers().get(0), 1000000, 999);
					istina.doDie(getPlayers().get(0));
					//istina.decayMe();

					double damagePercent = _ballistaDamage / 4660000.0D;
					int rewardId = 0;
					if(damagePercent > 0.5D)
						rewardId = MAGIC_FILLED_BOX;
					else if(damagePercent > 0.15D)
						rewardId = BOX_CONTAINING_MATK;

					for(Player player : getPlayers())
					{
						if(player != null)
						{
							player.startScenePlayer(SceneMovie.SCENE_ISTINA_ENDING_A);

							//TODO: Проверить как правильно должен выдаваться "Кристалл Истхины" на оффе.
							if(istina.getNpcId() == ISTHINA_HARD)
								ItemFunctions.addItem(player, ISTINA_SCRYSTAL_ITEM_ID, 1L, true);

							if(rewardId > 0)
								ItemFunctions.addItem(player, rewardId, 1L, true);
						}
					}
					_instanceDone = true;
				}
				//addSpawnWithoutRespawn(33293, _ballista.getLoc(), 0); dublicate
				_ballista.deleteMe();

				// TODO: Add timer depending on movie.
				ThreadPoolManager.getInstance().schedule(new ClearInstance(), 300000L); //5 min before instance closed
				addSpawnWithoutRespawn(ENDING_RUMIESE, new Location(-177033, 147933, -11387), 0);
				// .
			}

			int progress = (int) (100 - (_ballista.getCurrentHp() / _ballista.getMaxHp() * 100));
			if(_ballistaDamage >= _ballista.getMaxHp())
				progress = 100;

			for(Player player : getPlayers())
				//2 30 10 122520 1 0 0 0 ""
				player.sendPacket(new ExSendUIEventPacket(player, 2, _ballistaSeconds, progress, 122520, NpcString.REPLENISH_BALLISTA_MAGIC_POWER));

			if(_instanceDone)
			{
				if(_ballistaTimer != null)
				{
					_ballistaTimer.cancel(true);
					_ballistaTimer = null;
				}
			
				for(NpcInstance npc : getNpcs())
				{
					if(npc.isMonster() && !npc.isRaid() && npc.getNpcId() != 33293)
						npc.deleteMe();
				}
			}
			_ballistaSeconds -= 1;
		}
      , 5000L, 1000L);
	}

	private class ClearInstance extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			startCollapseTimer(60000);
		}
	}
}