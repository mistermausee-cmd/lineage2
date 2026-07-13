package instances;

import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;

//By Evil_dnk

//TODO: Add to NpcStrings  Text: Spezion's status will only change when exposed to light.(1811153)

public class SpaciaHard extends Reflection
{
	private final static int SPEZION_HARD = 25867;
	private final static int SPEZION_CLONE = 25868;
	private final static int MINION = 25780;

	private static NpcInstance SpezionBossHard;
	private boolean _isSpawned = false;
	private DeathListener _deathListener = new DeathListener();

	@Override
	public void onPlayerEnter(final Player player)
	{
		super.onPlayerEnter(player);
	}

	public void spazionSpawn()
	{
		if(_isSpawned)
			return;
		SpezionBossHard = addSpawnWithoutRespawn(SPEZION_HARD, new Location(184920, 143576, -11794, 0), 0);
		SpezionBossHard.addListener(_deathListener);
		_isSpawned = true;
		//for(Player player : getPlayers())
		//{
		//	player.sendPacket(new ExShowScreenMessage(NpcString.SPEZIONS_STATUS_WILL_ONLY_CHANGE_WHEN_EXPOSED_TO_LIGHT, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		//}
	}
	
	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == SPEZION_HARD)
			{

				//После убийства Спасии ставим откат инста всем кто заходил в инстант
				setReenterTime(System.currentTimeMillis());

				for(NpcInstance npc : getNpcs())
				{
					if(npc.getNpcId() == MINION || npc.getNpcId() == SPEZION_CLONE)
						npc.deleteMe();
				}
						
				for(Player p : getPlayers())
				{
					p.getInventory().addItem(17740, 1);
				}

				clearReflection(5, true);
				addSpawnWithoutRespawn(33387, self.getLoc(), 100);
			}
		}
	}

}