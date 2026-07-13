package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.SiegeUtils;

public final class VillageMasterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public VillageMasterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equals("manage_clan"))
			showChatWindow(player, "pledge/pl001.htm", false);
		else if(command.equals("manage_alliance"))
			showChatWindow(player, "pledge/al001.htm", false);
		else if(command.equals("create_clan_check"))
		{
			if(player.getLevel() <= 9)
				showChatWindow(player, "pledge/pl002.htm", false);
			else if(player.isClanLeader())
				showChatWindow(player, "pledge/pl003.htm", false);
			else if(player.getClan() != null)
				showChatWindow(player, "pledge/pl004.htm", false);
			else
				showChatWindow(player, "pledge/pl005.htm", false);
		}
		else if(command.equals("lvlup_clan_check"))
		{
			if(!player.isClanLeader())
			{
				showChatWindow(player, "pledge/pl014.htm", false);
				return;
			}
			showChatWindow(player, "pledge/pl013.htm", false);
		}
		else if(command.equals("disband_clan_check"))
		{
			if(!player.isClanLeader())
			{
				showChatWindow(player, "pledge/pl_err_master.htm", false);
				return;
			}
			showChatWindow(player, "pledge/pl007.htm", false);
		}
		else if(command.equals("restore_clan_check"))
		{
			if(!player.isClanLeader())
			{
				showChatWindow(player, "pledge/pl011.htm", false);
				return;
			}
			showChatWindow(player, "pledge/pl010.htm", false);
		}
		else if(command.equals("change_leader_check"))
			showChatWindow(player, "pledge/pl_master.htm", false);
		else if(command.startsWith("request_change_leader_check"))
		{
			if(!player.isClanLeader())
			{
				showChatWindow(player, "pledge/pl_err_master.htm", false);
				return;
			}
			showChatWindow(player, "pledge/pl_transfer_master.htm", false);
		}
		else if(command.startsWith("cancel_change_leader_check"))
		{
			if(!player.isClanLeader())
			{
				showChatWindow(player, "pledge/pl_err_master.htm", false);
				return;
			}
			showChatWindow(player, "pledge/pl_cancel_master.htm", false);
		}
		else if(command.equals("academy_manage_check"))
			showChatWindow(player, "pledge/pl_aca_help.htm", false);
		else if(command.equals("guards_manage_check"))
			showChatWindow(player, "pledge/pl_sub_help.htm", false);
		else if(command.equals("knights_manage_check"))
			showChatWindow(player, "pledge/pl_sub2_help.htm", false);
		else if(command.startsWith("subpledge_upgrade"))
		{
			StringTokenizer tokenizer = new StringTokenizer(command);
			tokenizer.nextElement();

			int val = Integer.parseInt(tokenizer.nextToken());

			VillageMasterPledgeBypasses.upgradeSubPledge(this, player, val);
		}
		else if(command.startsWith("subpledge_rename_check"))
			VillageMasterPledgeBypasses.renameSubPledgeCheck(this, player, command);
		else if(command.startsWith("subpledge_rename"))
			VillageMasterPledgeBypasses.renameSubPledge(this, player, command);
		else if(command.startsWith("create_clan"))
		{
			if(command.length() > 12)
			{
				String val = command.substring(12);
				VillageMasterPledgeBypasses.createClan(this, player, val);
			}
		}
		else if(command.startsWith("create_academy"))
		{
			if(command.length() > 15)
			{
				String sub = command.substring(15, command.length());
				if(VillageMasterPledgeBypasses.createSubPledge(this, player, sub, Clan.SUBUNIT_ACADEMY, 5, ""))
					showChatWindow(player, "pledge/pl_create_ok_aca.htm", false);
				else
					showChatWindow(player, "pledge/pl_err_aca.htm", false);
			}
		}
		else if(command.startsWith("create_royal"))
		{
			if(command.length() > 15)
			{
				String[] sub = command.substring(13, command.length()).split(" ", 2);
				if(sub.length == 2)
				{
					if(VillageMasterPledgeBypasses.createSubPledge(this, player, sub[1], Clan.SUBUNIT_ROYAL1, 6, sub[0]))
						showChatWindow(player, "pledge/pl_create_ok_sub1.htm", false);
					else
						showChatWindow(player, "pledge/pl_err_sub.htm", false);
				}
			}
		}
		else if(command.startsWith("create_knight"))
		{
			if(command.length() > 16)
			{
				String[] sub = command.substring(14, command.length()).split(" ", 2);
				if(sub.length == 2)
				{
					if(VillageMasterPledgeBypasses.createSubPledge(this, player, sub[1], Clan.SUBUNIT_KNIGHT1, 7, sub[0]))
						showChatWindow(player, "pledge/pl_create_ok_sub2.htm", false);
					else
						showChatWindow(player, "pledge/pl_err_sub2.htm", false);
				}
			}
		}
		else if(command.startsWith("change_leader"))
		{
			StringTokenizer tokenizer = new StringTokenizer(command);
			if(tokenizer.countTokens() != 3)
				return;

			tokenizer.nextToken();

			VillageMasterPledgeBypasses.changeLeader(this, player, Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken());
		}
		else if(command.startsWith("check_subpledge_exists"))
		{
			StringTokenizer tokenizer = new StringTokenizer(command);
			tokenizer.nextToken();

			if(!VillageMasterPledgeBypasses.checkPlayerForClanLeader(this, player))
				return;

			int subunitId = Integer.parseInt(tokenizer.nextToken());
			String errorDialog = tokenizer.nextToken();
			String nextDialog = tokenizer.nextToken();

			Clan clan = player.getClan();
			SubUnit subUnit = clan.getSubUnit(subunitId);
			if(subUnit == null)
				showChatWindow(player, errorDialog, false);
			else
				showChatWindow(player, nextDialog, false);
		}
		else if(command.startsWith("cancel_change_leader"))
			VillageMasterPledgeBypasses.cancelLeaderChange(this, player);
		else if(command.startsWith("check_create_ally"))
			showChatWindow(player, "pledge/al005.htm", false);
		else if(command.startsWith("create_ally"))
		{
			if(command.length() > 12)
			{
				String val = command.substring(12);
				if(VillageMasterPledgeBypasses.createAlly(player, val))
					showChatWindow(player, "pledge/al006.htm", false);
			}
		}
		else if(command.startsWith("dissolve_clan"))
			VillageMasterPledgeBypasses.dissolveClan(this, player);
		else if(command.startsWith("restore_clan"))
			VillageMasterPledgeBypasses.restoreClan(this, player);
		else if(command.startsWith("increase_clan_level"))
			VillageMasterPledgeBypasses.levelUpClan(this, player, false);
		else if(command.startsWith("alternative_increase_clan_level"))
		      VillageMasterPledgeBypasses.levelUpClan(this, player, true);
		else if(command.startsWith("learn_clan_skills"))
			VillageMasterPledgeBypasses.showClanSkillList(this, player);
		else if(command.startsWith("ShowCouponExchange"))
		{
			if(ItemFunctions.getItemCount(player, 8869) > 0 || ItemFunctions.getItemCount(player, 8870) > 0)
		        command = "Multisell 800";
			else
		        command = "Link villagemaster/reflect_weapon_master_noticket.htm";
		       
			super.onBypassFeedback(player, command);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "villagemaster/";
	}

	public void setLeader(Player leader, String newLeader)
	{
		if(!leader.isClanLeader())
		{
			leader.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		if(leader.getEvent(SiegeEvent.class) != null)
		{
			leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow"));
			return;
		}

		Clan clan = leader.getClan();
		SubUnit mainUnit = clan.getSubUnit(0);
		UnitMember member = mainUnit.getUnitMember(newLeader);

		if(member == null)
		{
			showChatWindow(leader, "pledge/pl_err_man.htm", false);
			return;
		}

		if(member.isLeaderOf() != Clan.SUBUNIT_NONE)
		{
			leader.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader"));
			return;
		}

		setLeader(leader, clan, mainUnit, member);
	}

	public static void setLeader(Player player, Clan clan, SubUnit unit, UnitMember newLeader)
	{
		player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.ClanLeaderWillBeChangedFromS1ToS2").addString(clan.getLeaderName()).addString(newLeader.getName()));

		if(clan.getLevel() >= 5)
		{
			if(clan.getLeader() != null)
			{
				Player oldLeaderPlayer = clan.getLeader().getPlayer();
				if(oldLeaderPlayer != null)
					SiegeUtils.removeSiegeSkills(oldLeaderPlayer);
			}
			Player newLeaderPlayer = newLeader.getPlayer();
			if(newLeaderPlayer != null)
				SiegeUtils.addSiegeSkills(newLeaderPlayer);
		}
		unit.setLeader(newLeader, true);

		clan.broadcastClanStatus(true, true, false);
	}

	private Race getVillageMasterRace()
	{
		switch(getTemplate().getRace())
		{
			case 14:
				return Race.HUMAN;
			case 15:
				return Race.ELF;
			case 16:
				return Race.DARKELF;
			case 17:
				return Race.ORC;
			case 18:
				return Race.DWARF;
			case 25:
				return Race.KAMAEL;
		}
		return null;
	}
}