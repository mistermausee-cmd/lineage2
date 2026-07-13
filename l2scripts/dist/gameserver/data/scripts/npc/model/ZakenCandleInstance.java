package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class ZakenCandleInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int Anchor = 32468;
	private boolean used = false;

	public ZakenCandleInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setNpcState(1);
		_hasRandomAnimation = false;
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		Reflection r = getReflection();
		if(r.isDefault() || used)
			return;

		for(NpcInstance npc : getAroundNpc(1000, 100))
			if(npc.getNpcId() == Anchor)
			{
				setNpcState(3);
				broadcastCharInfo();
				used = true;
				return;
			}
		setNpcState(2);
		broadcastCharInfo();
		used = true;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{}
}