package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.SubClassTable;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.Util;

public class Rename
{
	@Bypass("services.Rename:rename_page")
	public void rename_page(Player player, NpcInstance npc, String[] param)
	{
		String append = "!Rename";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Rename.RenameFor").addString(Util.formatAdena(Config.SERVICES_CHANGE_NICK_PRICE)).addString(ItemNameHolder.getInstance().getItemName(player, Config.SERVICES_CHANGE_NICK_ITEM)).toString(player) + "</font>";
		append += "<table>";
		append += "<tr><td>" + new CustomMessage("scripts.services.Rename.NewName").toString(player) + " <edit var=\"new_name\" width=80></td></tr>";
		append += "<tr><td></td></tr>";
		append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Rename.RenameButton").toString(player) + "\" action=\"bypass -h htmbypass_services.Rename:rename $new_name\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
		append += "</table>";
		Functions.show(append, player);
	}

	@Bypass("services.Rename:changesex_page")
	public void changesex_page(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isInPeaceZone())
		{
			Functions.show("You must be in peace zone to use this service.", player);
			return;
		}

		String append = "Sex changing";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.SexChange.SexChangeFor").addString(Util.formatAdena(Config.SERVICES_CHANGE_SEX_PRICE)).addString(ItemNameHolder.getInstance().getItemName(player, Config.SERVICES_CHANGE_SEX_ITEM)).toString(player) + "</font>";
		append += "<table>";
		append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.SexChange.Button").toString(player) + "\" action=\"bypass -h htmbypass_services.Rename:changesex\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
		append += "</table>";
		Functions.show(append, player);
	}

	@Bypass("services.Rename:separate_page")
	public void separate_page(Player player, NpcInstance npc, String[] param)
	{
		if(player.isHero())
		{
			Functions.show("Not available for heroes.", player);
			return;
		}

		if(player.getSubClassList().size() == 1)
		{
			Functions.show("You must have at least 1 subclass.", player);
			return;
		}

		if(!player.isBaseClassActive())
		{
			Functions.show("You must be at main class.", player);
			return;
		}

		if(player.getActiveSubClass().getLevel() < 75)
		{
			Functions.show("You must have at least 75 level.", player);
			return;
		}

		String append = "Subclass separation";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Separate.Price").addString(Util.formatAdena(Config.SERVICES_SEPARATE_SUB_PRICE)).addString(ItemNameHolder.getInstance().getItemName(player, Config.SERVICES_SEPARATE_SUB_ITEM)).toString(player) + "</font>&nbsp;";
		append += "<edit var=\"name\" width=80 height=15 /><br>";
		append += "<table>";

		for(SubClass s : player.getSubClassList().values())
			if(!s.isBase() && s.getClassId() != ClassId.INSPECTOR.getId() && s.getClassId() != ClassId.JUDICATOR.getId())
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Separate.Button").addString(ClassId.VALUES[s.getClassId()].toString()).toString(player) + "\" action=\"bypass -h htmbypass_services.Rename:separate " + s.getClassId() + " $name\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";

		append += "</table>";
		Functions.show(append, player);
	}

	@Bypass("services.Rename:separate")
	public void separate(Player player, NpcInstance npc, String[] param)
	{
		if(player.isHero())
		{
			Functions.show("Not available for heroes.", player);
			return;
		}

		if(player.getSubClassList().size() == 1)
		{
			Functions.show("You must have at least 1 subclass.", player);
			return;
		}

		if(!player.getActiveSubClass().isBase())
		{
			Functions.show("You must be at main class.", player);
			return;
		}

		if(player.getActiveSubClass().getLevel() < 75)
		{
			Functions.show("You must have at least 75 level.", player);
			return;
		}

		if(param.length < 2)
		{
			Functions.show("You must specify target.", player);
			return;
		}

		if(ItemFunctions.getItemCount(player, Config.SERVICES_SEPARATE_SUB_ITEM) < Config.SERVICES_SEPARATE_SUB_PRICE)
		{
			if(Config.SERVICES_SEPARATE_SUB_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		int classtomove = Integer.parseInt(param[0]);
		int newcharid = 0;
		for(Entry<Integer, String> e : player.getAccountChars().entrySet())
			if(e.getValue().equalsIgnoreCase(param[1]))
				newcharid = e.getKey();

		if(newcharid == 0)
		{
			Functions.show("Target not exists.", player);
			return;
		}

		if(mysql.simple_get_int("level", "character_subclasses", "char_obj_id=" + newcharid + " AND level > 1") > 1)
		{
			Functions.show("Target must have level 1.", player);
			return;
		}

		mysql.set("DELETE FROM character_subclasses WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills_save WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_effects_save WHERE object_id=" + newcharid);
		mysql.set("DELETE FROM character_hennas WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_shortcuts WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_variables WHERE obj_id=" + newcharid);

		mysql.set("UPDATE character_subclasses SET char_obj_id=" + newcharid + ", isBase=1, certification=0 WHERE char_obj_id=" + player.getObjectId() + " AND class_id=" + classtomove);
		mysql.set("UPDATE character_skills SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_skills_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_effects_save SET object_id=" + newcharid + " WHERE object_id=" + player.getObjectId() + " AND id=" + classtomove);
		mysql.set("UPDATE character_hennas SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_shortcuts SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);

		mysql.set("UPDATE character_variables SET obj_id=" + newcharid + " WHERE obj_id=" + player.getObjectId() + " AND name like 'TransferSkills%'");

		player.modifySubClass(classtomove, 0, false);

		ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE);
		player.logout();
		//Log.add("Character " + player + " base changed to " + target, "services");
	}

	@Bypass("services.Rename:changebase_page")
	public void changebase_page(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isInPeaceZone())
		{
			Functions.show("You must be in peace zone to use this service.", player);
			return;
		}

		if(player.isHero())
		{
			player.sendMessage("Not available for heroes.");
			return;
		}

		String append = "Base class changing";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.BaseChange.Price").addString(Util.formatAdena(Config.SERVICES_CHANGE_BASE_PRICE)).addString(ItemNameHolder.getInstance().getItemName(player, Config.SERVICES_CHANGE_BASE_ITEM)).toString(player) + "</font>";
		append += "<table>";

		List<SubClass> possible = new ArrayList<SubClass>();
		if(player.getActiveSubClass().isBase())
		{
			possible.addAll(player.getSubClassList().values());
			possible.remove(player.getSubClassList().getByClassId(player.getBaseClassId()));

			for(SubClass s : player.getSubClassList().values())
				for(SubClass s2 : player.getSubClassList().values())
					if(s != s2 && !SubClassTable.areClassesComportable(ClassId.VALUES[s.getClassId()], ClassId.VALUES[s2.getClassId()]) || s2.getLevel() < 75)
						possible.remove(s2);
		}

		if(possible.isEmpty())
			append += "<tr><td width=300>" + new CustomMessage("scripts.services.BaseChange.NotPossible").toString(player) + "</td></tr>";
		else
			for(SubClass s : possible)
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.BaseChange.Button").addString(ClassId.VALUES[s.getClassId()].toString()).toString(player) + "\" action=\"bypass -h htmbypass_services.Rename:changebase " + s.getClassId() + "\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
		append += "</table>";
		Functions.show(append, player);
	}

	@Bypass("services.Rename:changebase")
	public void changebase(Player player, NpcInstance npc, String[] param)
	{
		if(!player.isInPeaceZone())
		{
			Functions.show("You must be in peace zone to use this service.", player);
			return;
		}

		if(!player.getActiveSubClass().isBase())
		{
			Functions.show("You must be on your base class to use this service.", player);
			return;
		}

		if(player.isHero())
		{
			Functions.show("Not available for heroes.", player);
			return;
		}

		if(ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_BASE_ITEM) < Config.SERVICES_CHANGE_BASE_PRICE)
		{
			if(Config.SERVICES_CHANGE_BASE_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		int target = Integer.parseInt(param[0]);
		SubClass newBase = player.getSubClassList().getByClassId(target);

		player.getActiveSubClass().setType(SubClassType.SUBCLASS);
		player.getActiveSubClass().setCertification(newBase.getCertification());

		newBase.setCertification(0);
		player.getActiveSubClass().setExp(player.getExp(), false);
		player.checkSkills();

		newBase.setType(SubClassType.BASE_CLASS);

		player.setHairColor(0);
		player.setHairStyle(0);
		player.setFace(0);
		Olympiad.unregisterParticipant(player);
		ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE);
		player.logout();
		//Log.add("Character " + player + " base changed to " + target, "services");
	}

	@Bypass("services.Rename:rename")
	public void rename(Player player, NpcInstance npc, String[] param)
	{
		if(player.isHero())
		{
			player.sendMessage("Not available for heroes.");
			return;
		}

		if(param.length != 1)
		{
			Functions.show(new CustomMessage("scripts.services.Rename.incorrectinput"), player);
			return;
		}

		if(player.getEvent(SiegeEvent.class) != null)
		{
			Functions.show(new CustomMessage("scripts.services.Rename.SiegeNow"), player);
			return;
		}

		String name = param[0];
		if(!Util.isMatchingRegexp(name, Config.CNAME_TEMPLATE))
		{
			Functions.show(new CustomMessage("scripts.services.Rename.incorrectinput"), player);
			return;
		}

		if(ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_NICK_ITEM) < Config.SERVICES_CHANGE_NICK_PRICE)
		{
			if(Config.SERVICES_CHANGE_NICK_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		if(CharacterDAO.getInstance().getObjectIdByName(name) > 0)
		{
			Functions.show(new CustomMessage("scripts.services.Rename.Thisnamealreadyexists"), player);
			return;
		}

		ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_NICK_ITEM, Config.SERVICES_CHANGE_NICK_PRICE);

		String oldName = player.getName();
		player.reName(name, true);
		Log.add("Character " + oldName + " renamed to " + name, "renames");
		Functions.show(new CustomMessage("scripts.services.Rename.changedname").addString(oldName).addString(name).toString(player), player);
	}

	@Bypass("services.Rename:changesex")
	public void changesex(Player player, NpcInstance npc, String[] param)
	{
		if(player.getRace() == Race.KAMAEL)
		{
			Functions.show("Not available for Kamael.", player);
			return;
		}

		if(!player.isInPeaceZone())
		{
			Functions.show("You must be in peace zone to use this service.", player);
			return;
		}

		if(ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_SEX_ITEM) < Config.SERVICES_CHANGE_SEX_PRICE)
		{
			if(Config.SERVICES_CHANGE_SEX_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		Connection con = null;
		PreparedStatement offline = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?");
			offline.setInt(1, player.getSex() == Sex.FEMALE ? 0 : 1);
			offline.setInt(2, player.getObjectId());
			offline.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Functions.show(new CustomMessage("common.Error"), player);
			return;
		}
		finally
		{
			DbUtils.closeQuietly(con, offline);
		}

		player.setHairColor(0);
		player.setHairStyle(0);
		player.setFace(0);
		ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_SEX_ITEM, Config.SERVICES_CHANGE_SEX_PRICE);
		player.logout();
		Log.add("Character " + player + " sex changed to " + (player.getSex() == Sex.FEMALE ? "male" : "female"), "renames");
	}

	@Bypass("services.Rename:rename_clan_page")
	public void rename_clan_page(Player player, NpcInstance npc, String[] param)
	{
		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		String append = "!Rename clan";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Rename.RenameFor").addString(Util.formatAdena(Config.SERVICES_CHANGE_CLAN_NAME_PRICE)).addString(ItemNameHolder.getInstance().getItemName(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM)).toString(player) + "</font>";
		append += "<table>";
		append += "<tr><td>" + new CustomMessage("scripts.services.Rename.NewName").toString(player) + ": <edit var=\"new_name\" width=80></td></tr>";
		append += "<tr><td></td></tr>";
		append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Rename.RenameButton").toString(player) + "\" action=\"bypass -h htmbypass_services.Rename:rename_clan $new_name\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
		append += "</table>";
		Functions.show(append, player);
	}

	@Bypass("services.Rename:rename_clan")
	public void rename_clan(Player player, NpcInstance npc, String[] param)
	{
		if(player == null || param == null || param.length == 0)
			return;

		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		if(player.getEvent(SiegeEvent.class) != null)
		{
			Functions.show(new CustomMessage("scripts.services.Rename.SiegeNow"), player);
			return;
		}

		if(!Util.isMatchingRegexp(param[0], Config.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
			return;
		}
		if(ClanTable.getInstance().getClanByName(param[0]) != null)
		{
			player.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		if(ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM) < Config.SERVICES_CHANGE_CLAN_NAME_PRICE)
		{
			if(Config.SERVICES_CHANGE_CLAN_NAME_ITEM == 57)
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		Functions.show(new CustomMessage("scripts.services.Rename.changedname").addString(player.getClan().getName()).addString(param[0]).toString(player), player);
		SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		sub.setName(param[0], true);

		ItemFunctions.deleteItem(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM, Config.SERVICES_CHANGE_CLAN_NAME_PRICE);
		player.getClan().broadcastClanStatus(true, true, false);
	}
}