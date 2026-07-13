package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.templates.npc.NpcTemplate;

public class KukuruInstance extends NpcInstance
{
	public KukuruInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("gokukuru"))
		{
			Skill skill = SkillHolder.getInstance().getSkill(9209, 1);
			player.altUseSkill(skill, player);
			player.broadcastPacket(new MagicSkillUse(player, player, skill.getId(), 1, 0, 0, false));
		}
		else
			super.onBypassFeedback(player, command);
	}
}
