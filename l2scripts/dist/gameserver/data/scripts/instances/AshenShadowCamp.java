package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcFriendInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.concurrent.ScheduledFuture;

//By Evil_dnk

public class AshenShadowCamp extends Reflection
{
	private final OnDeathListener _monsterDeathListener = new MonsterDeathListener();

	public int _stage = 0;
	public static int[] QuestNps = {34096, 34097, 34098, 34099, 34100};

	public static int[] CommanderFirst = {23653,23654,23655,23656,23657,23658,23659,23660,23661,23662,23663,23664};
	public static int[] CommanderSecond = {23665,23666,23667,23668,23669,23670,23671,23672,23673,23674,23675,23676};

	public static int[] Healer = {23623, 23631, 23639, 23647};
	public static int[] Tank = {23616, 23624, 23632, 23640};
	public static int[] Isa = {23621, 23629, 23637, 23645};
	public static int[] DamgaDealer = {23617, 23625, 23633, 23641, 23618, 23626, 23634, 23642, 23619,
			23627, 23635, 23643, 23620, 23628, 23636, 23644, 23622, 23630, 23638, 23646};

	private ScheduledFuture<?> _BoxTalkTask;

	public static final NpcString[] MsgText = {
		NpcString.HEY_HEY_YOU_COME_HERE,
		NpcString.OPEN_THIS,
		NpcString.OPEN_THIS_BOX
	};


	private void group1()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-79080, 155000, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-79096, 155080, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-79128, 155144, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79160, 155208, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79176, 155272, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79192, 155320, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79080, 154936, -3168, 0), 0);
	}

	private void group2()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-80424, 153640, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-79896, 153800, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-79928, 153736, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79992, 153688, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80056, 153656, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80168, 153656, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80264, 153672, -3168, 0), 0);
	}

	private void group3()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-80728, 154952, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-80776, 155000, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-80888, 155048, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80952, 155000, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80968, 154936, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80904, 154872, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80728, 154872, -3168, 0), 0);
	}

	private void group4()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-82536, 155128, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-82504, 155064, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-82504, 155016, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82456, 154712, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82456, 154760, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82456, 154824, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82440, 154888, -3168, 0), 0);
	}

	private void group5()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-83784, 154584, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-83704, 154584, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-83768, 154520, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83720, 154504, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83752, 154456, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83688, 154456, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83736, 154408, -3168, 0), 0);
	}

	private void group6()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-81496, 153720, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-81512, 153784, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-81432, 153768, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-81464, 153832, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-81368, 153816, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-81256, 153752, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-81272, 153672, -3168, 0), 0);
	}

	private void group7()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-82680, 154392, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-82632, 154360, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-82696, 154344, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82648, 154312, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82712, 154280, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82664, 154232, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82696, 154200, -3168, 0), 0);
	}

	private void group8()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-82984, 153272, -3168, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-83016, 153288, -3168, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-83048, 153272, -3168, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83080, 153256, -3168, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83112, 153240, -3168, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83144, 153240, -3168, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83192, 153224, -3168, 0), 0);
	}

	private void group9()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-82040, 151512, -3120, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-82088, 151512, -3120, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-82136, 151512, -3120, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82184, 151512, -3120, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82216, 151512, -3120, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82280, 151512, -3120, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82376, 151512, -3120, 0), 0);
	}

	private void group10()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-83784, 151672, -3120, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-83848, 151672, -3120, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-83912, 151656, -3120, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-83976, 151624, -3120, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-84024, 151560, -3120, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-84040, 151496, -3120, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-84056, 151416, -3120, 0), 0);
	}

	private void group11()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-82264, 150072, -3120, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-82216, 15056, -3120, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-82168, 15056, -3120, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82104, 15040, -3120, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82136, 149944, -3120, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82216, 149928, -3120, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-82280, 149928, -3120, 0), 0);
	}

	private void group12()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-80808, 149848, -3040, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-80776, 149864, -3040, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-80744, 149896, -3040, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80712, 149944, -3040, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80696, 149976, -3040, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80696, 150024, -3040, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80712, 150072, -3040, 0), 0);
	}

	private void group13()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-80296, 151752, -3040, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-80344, 151800, -3040, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-80392, 151848, -3040, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80424, 151896, -3040, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80456, 151944, -3040, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80344, 151912, -3040, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-80296, 151864, -3040, 0), 0);
	}

	private void group14()
	{
		//Tank
		addSpawnWithoutRespawn(Rnd.get(Tank), new Location(-79192, 150744, -3040, 0), 0);
		//Isa
		addSpawnWithoutRespawn(Rnd.get(Isa), new Location(-79160, 150712, -3040, 0), 0);
		//Healer
		addSpawnWithoutRespawn(Rnd.get(Healer), new Location(-79240, 150712, -3040, 0), 0);
		//1 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79112, 150920, -3040, 0), 0);
		//2 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79112, 150968, -3040, 0), 0);
		//3 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79240, 150952, -3040, 0), 0);
		//4 DD
		addSpawnWithoutRespawn(Rnd.get(DamgaDealer), new Location(-79256, 150824, -3040, 0), 0);
	}

	@Override
	protected void onCreate()
	{
		super.onCreate();
		group1();
		group2();
		group3();
		group4();
		group5();
		group6();
		group7();
		group8();
		group9();
		group10();
		group11();
		group12();
		group13();
		group14();
		if(Rnd.chance(25))
			addSpawnWithoutRespawn(23650, new Location(-84232, 154584, -3176, 0), 0);
		_BoxTalkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MonsterMovementTask(), 2000L, 3000L);

	}

	public void setStage(int stage)
	{
		_stage = stage;
	}

	public int getStage()
	{
		return _stage;
	}

	public void Start(Player player)
	{
		if (getStage() == 0)
		{
			spawnByGroup("shadow_camp_signal1");
			if(_BoxTalkTask != null)
				_BoxTalkTask.cancel(true);
			setStage(1);
			int TYPE = Rnd.get(QuestNps);
			NpcInstance quest = addSpawnWithoutRespawn(TYPE, new Location(-77784, 155688, -3184, 0), 0);
			Functions.npcSay(quest, NpcString.THERES_NO_ONE_RIGHT);
			for (Player p : getPlayers())
			{
				//p.sendPacket(new ExShowScreenMessage(NpcString.ASHEN_SHADOW_REVOLUTIONARIES_KEEP_THE_FORMATION, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				player.sendPacket(new ExShowScreenMessage("Революционеры Серой Тени! Держать строй", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}

			if(TYPE == 34100)
				spawnByGroup("shadow_camp_altar");
			if(TYPE == 34097)
				spawnByGroup("shadow_camp_prison");
			if(TYPE == 34098)
				spawnByGroup("shadow_camp_box");
		}
	}

	private class MonsterMovementTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			for (NpcInstance npc : getAllByNpcId(34101, true))
			{
				Functions.npcSay(npc, Rnd.get(MsgText));
			}
		}
	}

	private class MonsterDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if (victim.isMonster() && ArrayUtils.contains(CommanderSecond, victim.getNpcId()) && _stage == 2)
			{
				clearReflection(10, true);
				setReenterTime(System.currentTimeMillis());
				addSpawnWithoutRespawn(34151, new Location(-83016, 151000, -3120, 0), 0);
				addSpawnWithoutRespawn(34152, new Location(-83016, 150936, -3120, 0), 0);
				addSpawnWithoutRespawn(34153, new Location(-83016, 150904, -3120, 0), 0);
				addSpawnWithoutRespawn(34154, new Location(-83016, 150840, -3120, 0), 0);
				addSpawnWithoutRespawn(34155, new Location(-83016, 150792, -3120, 0), 0);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.THE_INSTANCED_ZONE_WILL_CLOSE_SOON, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
			}
			else if (victim.isMonster() && victim.getNpcId() == 23651 && _stage == 1)
			{
				spawnByGroup("shadow_camp_signal2");
				setStage(2);
				addSpawnWithoutRespawn(Rnd.get(CommanderFirst), new Location(-81944, 154120, -3168, 0), 0);
			}
			else if (victim.isMonster() && victim.getNpcId() == 23651 && _stage == 2)
			{
				addSpawnWithoutRespawn(Rnd.get(CommanderSecond), new Location(-83000, 150872, -3120, 0), 0);
			}
			else
			{
				victim.removeListener(_monsterDeathListener);
			}
		}
	}

	@Override
	public void addObject(GameObject o)
	{
		super.addObject(o);
		if (o.isMonster())
			((Creature) o).addListener(_monsterDeathListener);
	}

	@Override
	protected void onCollapse()
	{
		if(_BoxTalkTask != null)
			_BoxTalkTask.cancel(false);
		super.onCollapse();
	}
}