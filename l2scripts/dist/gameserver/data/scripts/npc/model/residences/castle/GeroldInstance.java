package npc.model.residences.castle;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;

/**
 * @author Bonux
**/
public class GeroldInstance extends NpcInstance
{
	private static final int GIFT_SKILL_ID = 19036;

	public GeroldInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "castle/gerold/";
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(val == 0)
		{
			Castle castle = getCastle(player);
			if(castle == null || castle.getOwnerId() == 0)
				return "no_clan.htm";
		}
		return super.getHtmlFilename(val, player);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equals("receive_gift"))
		{
			Skill skill = SkillHolder.getInstance().getSkill(GIFT_SKILL_ID, 1);
			skill.getEffects(player, player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		Castle castle = getCastle(player);
		Clan clan = (castle == null ? null : castle.getOwner());
		if(clan != null)
		{
			if(val == 0)
				Functions.npcSayToPlayer(this, player, NpcString.WHEN_THE_WORLD_PLUNGES_INTO_CHAOS_WE_WILL_NEED_YOUR_HELP_AT_THAT_TIME_PLEASE_JOIN_IN_WITH_US_I_HOPE_THAT_YOU_WILL_BECOME_STRONGER);

			if(arg == null)
				arg = new Object[0];

			arg = ArrayUtils.add(arg, "<?clan_name?>");
			arg = ArrayUtils.add(arg, clan.getName());
			arg = ArrayUtils.add(arg, "<?leader_name?>");
			arg = ArrayUtils.add(arg, clan.getLeaderName());
		}

		super.showChatWindow(player, val, firstTalk, arg);
	}
}