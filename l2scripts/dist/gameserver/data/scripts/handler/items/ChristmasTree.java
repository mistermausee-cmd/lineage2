package handler.items;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;

import handler.items.ScriptItemHandler;

public class ChristmasTree extends ScriptItemHandler
{
	public class DeSpawnScheduleTimerTask extends RunnableImpl
	{
		SimpleSpawner spawnedTree = null;

		public DeSpawnScheduleTimerTask(SimpleSpawner spawn)
		{
			spawnedTree = spawn;
		}

		@Override
		public void runImpl() throws Exception
		{
			spawnedTree.deleteAll();
		}
	}

	private static final TIntIntMap TREES = new TIntIntHashMap();
	static
	{
		TREES.put(5560, 13006); // Christmas Tree
		TREES.put(5561, 13007); // Special Christmas Tree
	}

	private static final int DESPAWN_TIME = 600000; //10 min

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Player activeChar = (Player) playable;

		int itemId = item.getItemId();
		if(!TREES.containsKey(itemId))
			return false;

		NpcTemplate template = NpcHolder.getInstance().getTemplate(TREES.get(itemId));
		if(template == null)
			return false;

		for(NpcInstance npc : World.getAroundNpc(activeChar, 300, 200))
			if(npc.getNpcId() == 13006 || npc.getNpcId() == 13007)
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(npc));
				return false;
			}

		// Запрет на саммон елок слищком близко к другим НПЦ
		if(World.getAroundNpc(activeChar, 100, 200).size() > 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return false;
		}

		if(!activeChar.getInventory().destroyItem(item, 1L))
			return false;

		SimpleSpawner spawn = new SimpleSpawner(template);
		spawn.setLoc(activeChar.getLoc());
		NpcInstance npc = spawn.doSpawn(false);
		npc.setTitle(activeChar.getName()); //FIXME Почему-то не устанавливается
		spawn.respawnNpc(npc);

		// АИ вещающее бафф регена устанавливается только для большой елки
		if(itemId == 5561)
			npc.setAI(new ai.events.ChristmassTree(npc));

		ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(spawn), (activeChar.isInPeaceZone() ? DESPAWN_TIME / 3 : DESPAWN_TIME));
		return true;
	}
}