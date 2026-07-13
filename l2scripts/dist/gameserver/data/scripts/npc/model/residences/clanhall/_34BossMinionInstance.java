package npc.model.residences.clanhall;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import npc.model.residences.SiegeGuardInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;

/**
 * @author VISTALL
 * @date 17:50/13.05.2011
 */
public abstract class _34BossMinionInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	public _34BossMinionInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onDeath(Creature killer)
	{
		setCurrentHp(1, true);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		Functions.npcShout(this, spawnChatSay());
	}

	public abstract NpcString spawnChatSay();
	public abstract NpcString teleChatSay();
}
