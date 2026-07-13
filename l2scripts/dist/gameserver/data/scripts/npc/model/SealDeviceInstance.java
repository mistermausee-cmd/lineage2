package npc.model;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

/**
 * @author pchayka
 */
public final class SealDeviceInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	private boolean _gaveItem = false;

	public SealDeviceInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		//super.onDeath(killer);
		System.out.println("1");
		if(!_gaveItem && ItemFunctions.getItemCount(killer.getPlayer(), 13846) < 4)
		{
			setRHandId(15281);
			broadcastCharInfo();
			ItemFunctions.addItem(killer.getPlayer(), 13846, 1, true);
			_gaveItem = true;

			if(ItemFunctions.getItemCount(killer.getPlayer(), 13846) >= 4)
			{
				killer.getPlayer().startScenePlayer(SceneMovie.SSQ_SEAL_EMPEROR_2);
				ThreadPoolManager.getInstance().schedule(new TeleportPlayer(killer.getPlayer()), 26500L);
			}
			//i = this.getCurrentHp() - 1;
		}
		killer.reduceCurrentHp(450, this, null, true, false, true, false, false, false, true);		
	}	
	private class TeleportPlayer extends RunnableImpl
	{
		Player _p;

		public TeleportPlayer(Player p)
		{
			_p = p;
		}

		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance n : _p.getReflection().getNpcs())
				if(n.getNpcId() != 32586 && n.getNpcId() != 32587)
					n.deleteMe();
			_p.getPlayer().teleToLocation(new Location(-89560, 215784, -7488));
		}
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}
}