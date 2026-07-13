package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.HtmlUtils;

/**
 * @author Bonux
 */
public final class ClassMasterInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public ClassMasterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "custom/";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equals("change_class_list"))
		{
			ClassId playerClassId = player.getClassId();
			if(playerClassId.isLast())
			{
				showChatWindow(player, "custom/" + getNpcId() + "-no_last_class.htm", false);
				return;
			}

			int newClassLvl = player.getClassLevel().ordinal() + 1;
			int priceLvl = newClassLvl;
			if(playerClassId.isOfRace(Race.ERTHEIA)) // У расы артеас професии на голову выше, тобишь 1 профессия равняется второй другой рассы и т.д.
				priceLvl++;

			if(!Config.ALLOW_CLASS_MASTERS_LIST.containsKey(priceLvl))
			{
				showChatWindow(player, "custom/" + getNpcId() + "-no_class_lvl_" + newClassLvl + ".htm", false);
				return;
			}

			if(!checkMinLvl(player))
			{
				showChatWindow(player, "custom/" + getNpcId() + "-no_player_lvl_" + newClassLvl + ".htm", false);
				return;
			}

			int[] pay = Config.ALLOW_CLASS_MASTERS_LIST.get(priceLvl);
			int payItemId = 0;
			int payItemCount = 0;
			if(pay.length >= 2)
			{
				payItemId = pay[0];
				payItemCount = pay[1];
			}

			if(!playerClassId.isOfRace(Race.ERTHEIA) && playerClassId.isOfLevel(ClassLevel.THIRD))
			{
				if(payItemId > 0 && payItemCount > 0)
				{
					showChatWindow(player, "custom/" + getNpcId() + "-awakening_pay.htm", false, "<?PAY_ITEM?>", HtmlUtils.htmlItemName(payItemId), "<?PAY_ITEM_COUNT?>", String.valueOf(payItemCount));
					return;
				}
				showChatWindow(player, "custom/" + getNpcId() + "-awakening.htm", false);
			}
			else
			{
				String availClassList = generateAvailClassList(player.getClassId());
				if(payItemId > 0 && payItemCount > 0)
				{
					showChatWindow(player, "custom/" + getNpcId() + "-class_list_pay.htm", false, "<?AVAIL_CLASS_LIST?>", availClassList, "<?PAY_ITEM?>", HtmlUtils.htmlItemName(payItemId), "<?PAY_ITEM_COUNT?>", String.valueOf(payItemCount));
					return;
				}
				showChatWindow(player, "custom/" + getNpcId() + "-class_list.htm", false, "<?AVAIL_CLASS_LIST?>", availClassList);
			}
		}
		else if(cmd.equals("change_class"))
		{
			int val = Integer.parseInt(st.nextToken());
			ClassId classId = ClassId.VALUES[val];
			int newClassLvl = classId.getClassLevel().ordinal();
			int priceLvl = newClassLvl;

			if(classId.isOfRace(Race.ERTHEIA)) // У расы артеас професии на голову выше, тобишь 1 профессия равняется второй другой рассы и т.д.
				priceLvl++;

			if(classId == ClassId.INSPECTOR || !classId.childOf(player.getClassId()) || newClassLvl != player.getClassLevel().ordinal() + 1)
				return;

			if(!Config.ALLOW_CLASS_MASTERS_LIST.containsKey(priceLvl))
				return;

			if(!checkMinLvl(player))
				return;

			int[] pay = Config.ALLOW_CLASS_MASTERS_LIST.get(priceLvl);
			if(pay.length >= 2)
			{
				int payItemId = pay[0];
				int payItemCount = pay[1];
				long notEnoughItemCount = payItemCount - ItemFunctions.getItemCount(player, payItemId);
				if(notEnoughItemCount > 0)
				{
					showChatWindow(player, "custom/" + getNpcId() + "-no_item.htm", false, "<?PAY_ITEM?>", HtmlUtils.htmlItemName(payItemId), "<?NOT_ENOUGH_ITEM_COUNT?>", String.valueOf(notEnoughItemCount));
					return;
				}
				ItemFunctions.deleteItem(player, payItemId, payItemCount, true);
			}

			player.setClassId(val, false);
			player.broadcastUserInfo(true);
			showChatWindow(player, "custom/" + getNpcId() + "-class_changed.htm", false, "<?CLASS_NAME?>", HtmlUtils.htmlClassName(val));
		}
		else if(cmd.equals("awake"))
		{
			int newClassLvl = ClassLevel.AWAKED.ordinal();
			if(!Config.ALLOW_CLASS_MASTERS_LIST.containsKey(newClassLvl))
				return;

			if(!checkMinLvl(player))
				return;

			if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) == 0)
			{
				int[] pay = Config.ALLOW_CLASS_MASTERS_LIST.get(newClassLvl);
				if(pay.length >= 2)
				{
					int payItemId = pay[0];
					int payItemCount = pay[1];
					long notEnoughItemCount = payItemCount - ItemFunctions.getItemCount(player, payItemId);
					if(notEnoughItemCount > 0)
					{
						showChatWindow(player, "custom/" + getNpcId() + "-no_item_awake.htm", false, "<?PAY_ITEM?>", HtmlUtils.htmlItemName(payItemId), "<?NOT_ENOUGH_ITEM_COUNT?>", String.valueOf(notEnoughItemCount));
						return;
					}
					ItemFunctions.deleteItem(player, payItemId, payItemCount, true);
				}

				ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE, 1, true);
			}

			player.teleToLocation(AwakeningManagerInstance.TELEPORT_LOC);
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}

	private String generateAvailClassList(ClassId classId)
	{
		StringBuilder classList = new StringBuilder();
		for(ClassId cid : ClassId.VALUES)
		{
			// Инспектор является наследником trooper и warder, но сменить его как профессию нельзя,
			// т.к. это сабкласс. Наследуется с целью получения скилов родителей.
			if(cid == ClassId.INSPECTOR)
				continue;

			if(cid.childOf(classId) && cid.getClassLevel().ordinal() == classId.getClassLevel().ordinal() + 1)
				classList.append("<button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h npc_").append(getObjectId()).append("_change_class ").append(cid.getId()).append("\">").append(HtmlUtils.htmlClassName(cid.getId())).append("</button>");
		}
		return classList.toString();
	}

	private static boolean checkMinLvl(Player player)
	{
		if(player.getLevel() < player.getClassId().getClassMinLevel(true))
			return false;
		return true;
	}
}