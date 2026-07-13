package npc.model;

import java.util.Collection;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class MonkOfChaosInstance extends NpcInstance
{
	private static final int CHAOS_POMANDER = 37374; // Благовоние Хаоса
	private static final int CHAOS_POMANDER_DUAL_CLASS = 37375; // Благовоние Хаоса

	private static final long CHAOS_SKILLS_CANCEL_PRICE = 100000000L;

	private static final long serialVersionUID = 1L;

	public MonkOfChaosInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("learn_chaos_skills"))
		{
			if(!player.getClassId().isAwaked())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm", false);
				return;
			}

			ClassId classId = player.getClassId();
			if(classId.isOutdated())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm", false);
				return;
			}

			if(!player.isBaseClassActive() && !player.isDualClassActive())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_subclass.htm", false);
				return;
			}

			showChaosSkillList(player);
		}
		else if(command.equals("cancel_chaos_skills"))
		{
			if(!player.getClassId().isAwaked())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm", false);
				return;
			}

			ClassId classId = player.getClassId();
			if(classId.isOutdated())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm", false);
				return;
			}

			if(!player.isBaseClassActive() && !player.isDualClassActive())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_subclass.htm", false);
				return;
			}

			if(player.getAdena() < CHAOS_SKILLS_CANCEL_PRICE)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_adena.htm", false);
				return;
			}

			int cancelled = 0;
			Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, (player.isBaseClassActive() ? AcquireType.CHAOS : AcquireType.DUAL_CHAOS));
			for(SkillLearn learn : skillLearnList)
			{
				SkillEntry skillEntry = player.getKnownSkill(learn.getId());
				if(skillEntry == null)
					continue;

				player.removeSkill(skillEntry, true);
				cancelled++;
			}

			if(cancelled == 0)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_cancelled.htm", false);
				return;
			}

			player.reduceAdena(CHAOS_SKILLS_CANCEL_PRICE, true);
			ItemFunctions.addItem(player, (player.isBaseClassActive() ? CHAOS_POMANDER : CHAOS_POMANDER_DUAL_CLASS), cancelled, true);
			showChatWindow(player, "default/" + getNpcId() + "-cancelled.htm", false);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
