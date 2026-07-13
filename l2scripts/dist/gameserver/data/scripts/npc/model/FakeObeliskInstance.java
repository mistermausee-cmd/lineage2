package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC 13193 в локации Seed of Destruction
 * @author SYS
 */
public class FakeObeliskInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public FakeObeliskInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{}

	@Override
	public void onAction(Player player, boolean shift)
	{
		player.sendActionFailed();
	}
}