package instances;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

/**
 Obi-Wan
 04.11.2016
 */
public class Beleth extends Reflection
{
	private AtomicInteger stage = new AtomicInteger();
	private AtomicBoolean belethRing = new AtomicBoolean(false);
	private NpcInstance leona = null;
	private IntSet wins = new HashIntSet();

	private NpcInstance npc1 = null;
	private NpcInstance npc2 = null;
	private NpcInstance npc3 = null;

	private NpcInstance npc4 = null;

	@Override
	protected void onCreate()
	{
		super.onCreate();

		ThreadPoolManager.getInstance().schedule(()->{
			npc1 = addSpawnWithoutRespawn(29244, new Location(-16776, 245944, -848, 32767), 0);
			npc2 = addSpawnWithoutRespawn(29244, new Location(-16792, 246152, -848, 32767), 0);
			npc3 = addSpawnWithoutRespawn(29244, new Location(-16792, 245720, -848, 32767), 0);
			npc4 = addSpawnWithoutRespawn(31595, new Location(-18296, 246440, -848), 0);
		}, 15 * 60 * 1000);
	}

	public AtomicInteger getStage()
	{
		return stage;
	}

	public AtomicBoolean getBelethRing()
	{
		return belethRing;
	}

	public IntSet getWins()
	{
		return wins;
	}

	public void removeBeleths()
	{
		npc1.deleteMe();
		npc2.deleteMe();
		npc3.deleteMe();
	}

	public void removeNpc()
	{
		npc4.deleteMe();
	}
}