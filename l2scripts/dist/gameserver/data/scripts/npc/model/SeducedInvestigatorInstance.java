package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public SeducedInvestigatorInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setHasChatWindow(true);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		player.sendPacket(new HtmlMessage(this, "common/seducedinvestigator.htm").setPlayVoice(firstTalk));
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		Player player = attacker.getPlayer();
		if(player == null)
			return false;
		if(player.isPlayable())
			return false;
		return true;
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}