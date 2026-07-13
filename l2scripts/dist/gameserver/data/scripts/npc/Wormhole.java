package npc;

import instances.Beleth;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.ReflectionUtils;

/**
 Obi-Wan
 04.11.2016
 */
public class Wormhole extends ListenerHook implements OnInitScriptListener
{
	private static final int WORMHOLE_NPC_ID = 19518;

	@Override
	public void onInit()
	{
		addHookNpc(ListenerHookType.NPC_ASK, WORMHOLE_NPC_ID);
	}

	@Override
	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{
		switch(ask)
		{
			case 1:
			{
				switch((int) reply)
				{
					case 1:
					{
						Reflection reflection = null;

						for(Reflection r : ReflectionManager.getInstance().getAll())
						{
							if(r.getInstancedZone() != null && r.getInstancedZone().getId() == 5003)
							{
								reflection = r;
								break;
							}
						}

						if(player.canEnterInstance(5003))
						{
							if(reflection != null)
							{
								if(!reflection.isClosed())
								{
									ReflectionUtils.enterReflection(player, reflection, 5003);
								}
							}
							else
							{
								Beleth beleth = new Beleth();
								ReflectionUtils.enterReflection(player, beleth, 5003);

								/*
								Когда Белеф / Дарион возрождаются на Острове Ада происходит землетрясения
								EarthQuakePacket eq = new EarthQuakePacket(baium.getLoc(), 40, 5);
								baium.broadcastPacket(eq);
 								Время возрождения Дариона / Белефа - 7 дней
								анонс Император Острова Ада Белеф явится в этот мир через 15 минут
								 */

								ThreadPoolManager.getInstance().schedule(() ->
								{
									beleth.close();
								}, 15 * 60 * 1000);
							}
						}
					}
					break;
				}
			}
			break;
		}
	}
}