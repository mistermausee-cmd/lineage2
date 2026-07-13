package zones;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public class AteliaFortress extends ListenerHook implements OnInitScriptListener
{
	// Monster's
	private static final int ATELIA_TRIAL_GUARDIAN_CAPTAIN = 23539; // Atelia Trial Guardian Captain
	private static final int GEORK = 23586; // Geork
	private static final int BURNSTEIN = 23587; // Burnstein
	private static final int HUMMEL = 23588; // Hummel

	// Zone's
	private static final String ATELIA_FORTRESS_1ST_STAGE = "[atelia_fortress_1]";
	private static final String ATELIA_FORTRESS_2ND_STAGE = "[atelia_fortress_2]";
	private static final String ATELIA_FORTRESS_3RD_STAGE = "[atelia_fortress_3]";
	private static final String ATELIA_FORTRESS_4TH_STAGE = "[atelia_fortress_4]";

	private static final String EMBRIO_GATEKEEPER_SPAWN = "embrio_gatekeeper";

	private static final int[] ATELIA_DOORS = { 18190002, 18190004 };

	@Override
	public void onInit()
	{
		addHookNpc(ListenerHookType.NPC_KILL, ATELIA_TRIAL_GUARDIAN_CAPTAIN);
		addHookNpc(ListenerHookType.NPC_KILL, GEORK);
		addHookNpc(ListenerHookType.NPC_KILL, BURNSTEIN);
		addHookNpc(ListenerHookType.NPC_KILL, HUMMEL);
		addHookNpc(ListenerHookType.NPC_SPAWN, ATELIA_TRIAL_GUARDIAN_CAPTAIN);
		addHookNpc(ListenerHookType.NPC_SPAWN, GEORK);
		addHookNpc(ListenerHookType.NPC_SPAWN, BURNSTEIN);
		addHookNpc(ListenerHookType.NPC_SPAWN, HUMMEL);

		if(GameObjectsStorage.getAllByNpcId(HUMMEL, true).isEmpty())
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_1ST_STAGE).setActive(true);

		if(GameObjectsStorage.getAllByNpcId(GEORK, true).isEmpty())
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_2ND_STAGE).setActive(true);

		if(GameObjectsStorage.getAllByNpcId(BURNSTEIN, true).isEmpty())
		{
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_3RD_STAGE).setActive(true);
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_4TH_STAGE).setActive(true);
			ReflectionManager.MAIN.spawnByGroup(EMBRIO_GATEKEEPER_SPAWN);
		}

		if(GameObjectsStorage.getAllByNpcId(ATELIA_TRIAL_GUARDIAN_CAPTAIN, true).isEmpty())
		{
			for(int doorId : ATELIA_DOORS)
				ReflectionManager.MAIN.getDoor(doorId).openMe();
		}
	}

	@Override
	public void onNpcKill(NpcInstance npc, Player killer)
	{
		if(npc.getNpcId() == HUMMEL)
		{
			if(GameObjectsStorage.getAllByNpcId(HUMMEL, true).isEmpty())
				ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_1ST_STAGE).setActive(true);
		}
		else if(npc.getNpcId() == GEORK)
		{
			if(GameObjectsStorage.getAllByNpcId(GEORK, true).isEmpty())
				ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_2ND_STAGE).setActive(true);
		}
		else if(npc.getNpcId() == BURNSTEIN)
		{
			if(GameObjectsStorage.getAllByNpcId(BURNSTEIN, true).isEmpty())
			{
				ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_3RD_STAGE).setActive(true);
				ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_4TH_STAGE).setActive(true);
				ReflectionManager.MAIN.spawnByGroup(EMBRIO_GATEKEEPER_SPAWN);
			}
		}
		else if(npc.getNpcId() == ATELIA_TRIAL_GUARDIAN_CAPTAIN)
		{
			if(GameObjectsStorage.getAllByNpcId(ATELIA_TRIAL_GUARDIAN_CAPTAIN, true).isEmpty())
			{
				for(int doorId : ATELIA_DOORS)
					ReflectionManager.MAIN.getDoor(doorId).openMe();
			}
		}
	}

	@Override
	public void onNpcSpawn(NpcInstance npc)
	{
		if(npc.getNpcId() == HUMMEL)
		{
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_1ST_STAGE).setActive(false);
		}
		else if(npc.getNpcId() == GEORK)
		{
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_2ND_STAGE).setActive(false);
		}
		else if(npc.getNpcId() == BURNSTEIN)
		{
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_3RD_STAGE).setActive(false);
			ReflectionManager.MAIN.getZone(ATELIA_FORTRESS_4TH_STAGE).setActive(false);
			ReflectionManager.MAIN.despawnByGroup(EMBRIO_GATEKEEPER_SPAWN);
		}
		else if(npc.getNpcId() == ATELIA_TRIAL_GUARDIAN_CAPTAIN)
		{
			for(int doorId : ATELIA_DOORS)
				ReflectionManager.MAIN.getDoor(doorId).closeMe();
		}
	}
}