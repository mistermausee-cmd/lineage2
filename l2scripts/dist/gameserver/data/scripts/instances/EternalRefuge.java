package instances;

import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class EternalRefuge extends Reflection
{
	private final OnDeathListener _monsterDeathListener = new MonsterDeathListener();

	private class MonsterDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if (victim.isMonster() && victim.getNpcId() == 26102)
			{
				clearReflection(5, true);
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.THE_INSTANCED_ZONE_WILL_CLOSE_SOON, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
			}
			else if (victim.isMonster() && victim.getNpcId() == 26093)
			{
				spawnByGroup("eternal_rb2");
				openDoor(18170002);
			}
			else if (victim.isMonster() && victim.getNpcId() == 26094)
			{
				spawnByGroup("eternal_rb3");
				openDoor(18170004);
			}
			else if (victim.isMonster() && victim.getNpcId() == 26096)
			{
				spawnByGroup("eternal_rb4");
				openDoor(18170006);
			}
			else if (victim.isMonster() && victim.getNpcId() == 26099)
			{
				spawnByGroup("eternal_rb5");
				openDoor(18170008);
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
}