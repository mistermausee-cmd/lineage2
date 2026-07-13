package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

//By Evil_dnk
/*TODO:
Так же на 1 и 2 этаже включается таймер Вида: Количество жертв оставшихся и время идет.
на 1ом этаже в тймере 5 жертв и каждые 3 минуты таймер начинается опять с 3х минут но уже -1 жертва остается.
когда жертв 0 то значит не успел пройти этаж, если успеваешь убить РБ на 1оэтаже то таймер пропадает.
При переходе на второй этаж когда говоришь с нпс и запускаешь инстант счетчик такой же но уже 2 минуты и так же 5 жертв.
тоесть фактически сделать таймер который 5 раз по 3 минуты и 5 раз по 2 минуты.
и на последнем этаже там есть алтарь когда начинаешь эту стадию появляется Шкала заполнения(как у балисты на Истхине) и таймер в 1 минуту.
Если таймер доходит до 0 то РБ получают баф Благославление Шилен.
Если в этот период начать бить Алтарь то шкала заполняется и при заполнении обнуляет таймер и рб не получают бафа.
*/
public class AltarShilen extends Reflection
{
	//1 этаж
	private static final int KeyStage1 = 23131;
	private static final int BossStage1 = 25857;

	//2 этаж
	private static final int KeyStage2 = 23138;
	private static final int BossStage2 = 25858;

	//3этаж
	private static final int BossStage3_1 = 25855;
	private static final int BossStage3_2 = 25856;

	private static final int DoorEnter4 = 25180004;
	private static final int DoorEnter5 = 25180005;
	private static final int DoorEnter6 = 25180006;

	private int stage = 0;

	private static final String STAGE_1_BOSS = "BossStage1";
	private static final String STAGE_2_BOSS = "BossStage2";
	private static final String STAGE_3_BOS_1 = "BossStage3_1";
	private static final String STAGE_3_BOS_2 = "BossStage3_2";
	private static final String STAGE_1_ALTAR = "altar_1_stage";
	private static final String STAGE_2_ALTAR = "altar_2_stage";
	private static final String STAGE_3_ALTAR = "altar_3_stage";

	Location EXCHANGE = new Location(178152, 14280, -13717, 0);
	Location VICTIM1 = new Location(178152, 14408, -8115, 0);
	Location VICTIM2 = new Location(178152, 14408, -10547, 0);

	private DeathListener _deathListener = new DeathListener();


	private int[] mobsIds = {
			KeyStage1,
			KeyStage2,
			BossStage1,
			BossStage2,
			BossStage3_1,
			BossStage3_2,
			23132,
			23133,
			23134,
			23135,
			23136,
			23137
	};

	private class SpawnStage extends RunnableImpl
	{
		private String stage;

		public SpawnStage(String stage)
		{
			this.stage = stage;
		}

		@Override
		public void runImpl() throws Exception
		{
			spawnByGroup(stage);
			invokeDeathListener();
		}
	}

	private void invokeDeathListener()
	{
		for (int mobid : mobsIds)
			for (NpcInstance mob : getAllByNpcId(mobid, true))
				mob.addListener(_deathListener);
	}

	public void stageStart(int nStage)
	{
		invokeDeathListener();
		stage = nStage;
		switch (nStage)
		{
			case 1:
				addSpawnWithoutRespawn(19142, VICTIM1, 0);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.YOU_MUST_STOP_THE_ALTAR_BEFORE_EVERYTHING_IS_SACRIFICED, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
					p.sendPacket(new ExSendUIEventPacket(p, 0, 0, 3 * 60, 0, NpcString.SACRIFICE_LEFT_S1));
				}
				break;
			case 2:
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_1_BOSS), 1000);
				break;
			case 3:
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_1_ALTAR), 1000);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.ALTAR_HAS_STOPPED, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
					p.sendPacket(new ExSendUIEventPacket(p, 1, 1, 0, 0));
				}
				break;
			case 4:
				addSpawnWithoutRespawn(19147, VICTIM2, 0);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.YOU_MUST_STOP_THE_ALTAR_BEFORE_EVERYTHING_IS_SACRIFICED, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
					p.sendPacket(new ExSendUIEventPacket(p, 0, 0, 2 * 60, 0, NpcString.SACRIFICE_LEFT_S1));
				}
				break;
			case 5:
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_2_BOSS), 1000);
				break;
			case 6:
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_2_ALTAR), 1000);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.ALTAR_HAS_STOPPED, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
					p.sendPacket(new ExSendUIEventPacket(p, 1, 1, 0, 0));
				}
				break;
			case 7:
				break;
			case 8:
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3_BOS_2), 1000);
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3_ALTAR), 1000);
				ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3_BOS_1), 1000);
				break;
			case 9:
				break;
		}
	}


	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
	}

	public int getStage()
	{
		return stage;
	}

	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
	}

	private class CloseOpenedDoor extends RunnableImpl
	{

		@Override
		public void runImpl() throws Exception
		{
			getDoor(DoorEnter4).closeMe();
			getDoor(DoorEnter5).closeMe();
			getDoor(DoorEnter6).closeMe();
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
		  if(self.getNpcId() == 23132 || self.getNpcId() == 23133 || self.getNpcId() == 23134 || self.getNpcId() == 23135
				  || self.getNpcId() == 23136 || self.getNpcId() == 23137)
		  {
			  getDoor(DoorEnter4).openMe();
			  getDoor(DoorEnter5).openMe();
			  getDoor(DoorEnter6).openMe();
			  ThreadPoolManager.getInstance().schedule(new CloseOpenedDoor(), 6000);
		  }
			else if(stage == 1 && getAllByNpcId(KeyStage1, true).isEmpty())
			{
				stageStart(2);
			}
			else if(stage == 2 && self.getNpcId() == BossStage1)
			{
				stageStart(3);
			}
			else if(stage == 4 && getAllByNpcId(KeyStage2, true).isEmpty())
			{
				stageStart(5);
			}
			else if(stage == 5 && self.getNpcId() == BossStage2)
			{
				stageStart(6);
			}
			else if((self.getNpcId() == BossStage3_1 && getAllByNpcId(BossStage3_2, true).isEmpty()) || (self.getNpcId() == BossStage3_2 && getAllByNpcId(BossStage3_1, true).isEmpty()))
			{
				clearReflection(5, true);
				setReenterTime(System.currentTimeMillis());

			}
			else
			  self.removeListener(_deathListener);
		}
	}
}
