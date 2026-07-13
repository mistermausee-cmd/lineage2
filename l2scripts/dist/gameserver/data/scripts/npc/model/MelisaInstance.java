package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public final class MelisaInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public MelisaInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			/*if(Принцесса не вылечена)*/
				showChatWindow(player, "default/ep_fr_melisa_bad001.htm", firstTalk);
			/*else 
				showChatWindow(player, "default/ep_fr_melisa001.htm", firstTalk);*/
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
