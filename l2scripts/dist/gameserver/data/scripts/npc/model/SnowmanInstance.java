package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC Snowman в эвенте Saving Snowman
 * @author SYS
 */
public class SnowmanInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public SnowmanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		player.sendActionFailed();
	}
}