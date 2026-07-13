package npc.model;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.AbnormalList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class NoelerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// Skill's
	private static final Skill STOCKING_FAIRYS_BLESSING = SkillHolder.getInstance().getSkill(16419, 1);	// Новогодний носок - Благословение
	private static final Skill TREE_FAIRYS_BLESSING = SkillHolder.getInstance().getSkill(16420, 1);	// Новогоднее Дерево - Благословение
	private static final Skill SNOWMAN_FAIRYS_BLESSING = SkillHolder.getInstance().getSkill(16421, 1);	// Снеговик - Благословение
	private static final Skill APPEARANCE_STONE_SANTA_SUIT = SkillHolder.getInstance().getSkill(16425, 1);	// Перевоплощение в Деда Мороза

	private static final int BUFF_DISTANCE = 3000;

	public NoelerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("event"))
		{
			if(player.getServitorsCount() > 0)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_servitor.htm", false);
				return;
			}

			String cmd1 = st.nextToken();
			if(cmd1.equalsIgnoreCase("reward"))
				MultiSellHolder.getInstance().SeparateAndSend(2082, player, 0);
			else if(cmd1.equalsIgnoreCase("givesuit"))
			{
				forceUseSkill(APPEARANCE_STONE_SANTA_SUIT, player);
				showChatWindow(player, "default/" + getNpcId() + "-received_suit.htm", false);
			}
			else if(cmd1.equalsIgnoreCase("givefairy"))
			{
				String cmd2 = st.nextToken();
				if(cmd2.equalsIgnoreCase("all"))
				{
					Party party = player.getParty();
					if(party != null && party.isLeader(player))
					{
						Set<Race> races = new HashSet<Race>();
						for(Player member : party.getPartyMembers())
							races.add(member.getRace());

						if(party.getMemberCount() >= 7 || races.size() >= 3)
						{
							forceUseSkill(STOCKING_FAIRYS_BLESSING, player);
							forceUseSkill(TREE_FAIRYS_BLESSING, player);
							forceUseSkill(SNOWMAN_FAIRYS_BLESSING, player);

							for(Player member : party.getPartyMembers())
							{
								if(member == player)
									continue;

								if(getDistance(member) > BUFF_DISTANCE)
									continue;

								STOCKING_FAIRYS_BLESSING.getEffects(this, member);
								TREE_FAIRYS_BLESSING.getEffects(this, member);
								SNOWMAN_FAIRYS_BLESSING.getEffects(this, member);
							}

							showChatWindow(player, "default/" + getNpcId() + "-received_fairy.htm", false);
							return;
						}
					}
		
					showChatWindow(player, "default/" + getNpcId() + "-no_receive_all_fairies.htm", false);
					return;
				}

				AbnormalList effectList = player.getAbnormalList();
				if(effectList.contains(STOCKING_FAIRYS_BLESSING) || effectList.contains(TREE_FAIRYS_BLESSING) || effectList.contains(SNOWMAN_FAIRYS_BLESSING))
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_receive_fairy.htm", false);
					return;
				}

				if(cmd2.equalsIgnoreCase("stocking"))
					forceUseSkill(STOCKING_FAIRYS_BLESSING, player);
				else if(cmd2.equalsIgnoreCase("tree"))
					forceUseSkill(TREE_FAIRYS_BLESSING, player);
				else if(cmd2.equalsIgnoreCase("snowman"))
					forceUseSkill(SNOWMAN_FAIRYS_BLESSING, player);

				showChatWindow(player, "default/" + getNpcId() + "-received_fairy.htm", false);
			}
			else if(cmd1.equalsIgnoreCase("removefairy"))
			{
				AbnormalList effectList = player.getAbnormalList();
				effectList.stop(STOCKING_FAIRYS_BLESSING);
				effectList.stop(TREE_FAIRYS_BLESSING);
				effectList.stop(SNOWMAN_FAIRYS_BLESSING);

				showChatWindow(player, 1, false);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}