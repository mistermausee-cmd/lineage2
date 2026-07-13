package handler.items;

import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.base.Race;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

import bosses.AntharasManager;
import bosses.ValakasManager;
import quests._466_PlacingMySmallPower;

public class Special extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		switch(itemId)
		{
			//Key of Enigma
			case 8060:
				return use8060(player, ctrl);
			//Dewdrop of Destruction
			case 8556:
				return use8556(player, ctrl);
			//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowder
			case 13853:
				return use13853(player, ctrl);
			//Holy Water for SSQ 2nd quest
			case 13808:
				return use13808(player, ctrl);
			//Court Mag Staff for SSQ 2nd quest
			case 13809:
				return use13809(player, ctrl);
			case 14835:
				return use14835(player, ctrl);
			//Strongbox of Promise
			case 15537:
				return use15537(player, ctrl);
			case 21899:
				return use21899(player, ctrl);
			case 21900:
				return use21900(player, ctrl);
			case 21901:
				return use21901(player, ctrl);
			case 21902:
				return use21902(player, ctrl);
			case 21903:
				return use21903(player, ctrl);
			case 21904:
				return use21904(player, ctrl);
			//Antharas Blood Crystal
			case 17268:
				return use17268(player, ctrl);
			case 17619: //cruma quest
				return use17619(player, ctrl);
			case 17604: //megameld quest
				return use17604(player, ctrl);
			case 34033:
				return use34033(player, ctrl);
			case 17603:
				return use17603(player, ctrl);
			case 37314:
				return use37314(player, ctrl);
			case 39629:
				return use39629(player, ctrl);
			case 39630:
				return use39630(player, ctrl);
			case 39631:
				return use39631(player, ctrl);
			case 39632:
				return use39632(player, ctrl);
			case 39633:
				return use39633(player, ctrl);
			case 36065:
				return use36065(player, ctrl);
			case 27605:
				return use27605(player, ctrl);
			case 27606:
				return use27606(player, ctrl);
			case 27607:
				return use27607(player, ctrl);
			default:
				return false;
		}
	}

	//Мешочек Кладоискателя - Ур. 1
	private boolean use39629(Player player, boolean ctrl)
	{
		if(player.getLevel() >= 85)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(39629));
			return false;
		}

		else if(ItemFunctions.deleteItem(player, 39629, 1))
		{
			player.addExpAndSp(400000, 0);
			return true;
		}
		return false;
	}
	//Мешочек Кладоискателя - Ур. 2
	private boolean use39630(Player player, boolean ctrl)
	{
		if(player.getLevel() >= 85)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(39630));
			return false;
		}

		else if(ItemFunctions.deleteItem(player, 39630, 1))
		{
			player.addExpAndSp(1600000, 0);
			return true;
		}
		return false;
	}
	//Мешочек Кладоискателя - Ур. 3
	private boolean use39631(Player player, boolean ctrl)
	{
		if(player.getLevel() >= 85)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(39631));
			return false;
		}

		else if(ItemFunctions.deleteItem(player, 39631, 1))
		{
			if(Rnd.chance(1))
				ItemFunctions.addItem(player, 39631, 10);
			else if(Rnd.chance(5))
				ItemFunctions.addItem(player, 39386, 1);
			else if(Rnd.chance(3))
				ItemFunctions.addItem(player, 39387, 1);
			else if(Rnd.chance(1))
				ItemFunctions.addItem(player, 39388, 1);
			else if(Rnd.chance(2))
				ItemFunctions.addItem(player, 39720, 1);

			if(Rnd.get(1, 1000) == 5)
				ItemFunctions.addItem(player, 57, 200000000);
			else if(Rnd.get(1, 1000) < 6)
				ItemFunctions.addItem(player, 57, 20000000);
			else if (Rnd.chance(1))
				ItemFunctions.addItem(player, 57, 2000000);
			else if (Rnd.chance(15))
				ItemFunctions.addItem(player, 57, 200000);
			else
				ItemFunctions.addItem(player, 57, 20000);
			player.addExpAndSp(6400000, 0);
			return true;
		}
		return false;
	}
	//Мешочек Кладоискателя - Ур. 4
	private boolean use39632(Player player, boolean ctrl)
	{
		if(player.getLevel() < 85)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(39632));
			return false;
		}

		else if(ItemFunctions.deleteItem(player, 39632, 1))
		{
			player.addExpAndSp(0, 80000);
			return true;
		}
		return false;
	}
	//Мешочек Кладоискателя - Ур. 5
	private boolean use39633(Player player, boolean ctrl)
	{
		if(player.getLevel() < 85)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(39633));
			return false;
		}

		else if(ItemFunctions.deleteItem(player, 39633, 1))
		{
			if(Rnd.chance(4))
				ItemFunctions.addItem(player, 39633, 10);
			else if(Rnd.chance(2))
				ItemFunctions.addItem(player, 39388, 1);
			else if(Rnd.chance(1))
				ItemFunctions.addItem(player, 39389, 1);
			else if(Rnd.chance(0.5))
				ItemFunctions.addItem(player, 39390, 1);
			else if(Rnd.chance(15))
				ItemFunctions.addItem(player, 39720, 1);

			if (Rnd.chance(3))
				ItemFunctions.addItem(player, 57, 100000000);
			else if (Rnd.chance(5))
				ItemFunctions.addItem(player, 57, 10000000);
			else if (Rnd.chance(15))
				ItemFunctions.addItem(player, 57, 1000000);
			else if(Rnd.get(1, 1000) < 5)
				ItemFunctions.addItem(player, 57, 1000000000);
			else
				ItemFunctions.addItem(player, 57, 100000);

			player.addExpAndSp(0, 256000);
			return true;
		}
		return false;
	}

	//Key of Enigma
	private boolean use8060(Player player, boolean ctrl)
	{
		if(ItemFunctions.deleteItem(player, 8058, 1))
		{
			ItemFunctions.addItem(player, 8059, 1);
			return true;
		}
		return false;
	}

	//Dewdrop of Destruction
	private boolean use8556(Player player, boolean ctrl)
	{
		int[] npcs = {29048, 29049};

		GameObject t = player.getTarget();
		if(t == null || !t.isNpc() || !ArrayUtils.contains(npcs, ((NpcInstance) t).getNpcId()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(8556));
			return false;
		}
		if(player.getDistance(t) > 200)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOUR_TARGET_IS_OUT_OF_RANGE));
			return false;
		}

		useItem(player, 8556, 1);
		((NpcInstance) t).doDie(player);
		return true;
	}

	//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowde
	private boolean use13853(Player player, boolean ctrl)
	{
		if(!player.isInZone(ZoneType.mother_tree))
		{
			player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
			return false;
		}
		useItem(player, 13853, 1);
		ItemFunctions.addItem(player, 13854, 1);
		return true;
	}

	//Holy Water for SSQ 2nd quest
	private boolean use13808(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240101, 17240105, 17240109};

		GameObject target = player.getTarget();
		if(player.getDistance(target) > 150)
			return false;
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
				player.getReflection().openDoor(_door);
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		return true;
	}

	//Court Mag Staff for SSQ 2nd quest
	private boolean use13809(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240103, 17240107};

		GameObject target = player.getTarget();
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
			{
				useItem(player, 13809, 1);
				player.getReflection().openDoor(_door);
				player.broadcastPacket(new MagicSkillUse(player, player, 2634, 1, 0, 0));
				return false;
			}
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
	}

	private boolean use14835(Player player, boolean ctrl)
	{
		//TODO [G1ta0] добавить кучу других проверок на возможность телепорта
		if(player.isActionsDisabled() || player.isInOlympiadMode() || player.isInZone(ZoneType.no_escape))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(14835));
			return false;
		}

		useItem(player, 14835, 1);
		//Stakato nest entrance
		player.teleToLocation(89464, -44712, -2167, ReflectionManager.MAIN);
		return true;
	}

	//Strongbox of Promise
	private boolean use15537(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(464);
		if(player.getLevel() >= 82 && qs == null)
		{
			useItem(player, 15537, 1);
			ItemFunctions.addItem(player, 15538, 1);
			Quest q = QuestHolder.getInstance().getQuest(464);
			QuestState st = player.getQuestState(q.getId());
			st = q.newQuestState(player);
			st.setCond(1);
		}
		else
		{
			player.sendMessage(new CustomMessage("Quest._464_Oath.QuestCannotBeTaken"));
			return false;
		}
		return true;
	}

	//Totem
	private boolean use21899(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
			return false;
		}
		NpcUtils.spawnSingle(143, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
		return true;
	}

	//Totem
	private boolean use21900(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21900));
			return false;
		}
		NpcUtils.spawnSingle(144, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
		return true;
	}

	//Totem
	private boolean use21901(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21901));
			return false;
		}
		NpcUtils.spawnSingle(145, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
		return true;
	}

	//Totem
	private boolean use21902(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21902));
			return false;
		}
		NpcUtils.spawnSingle(146, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
		return true;
	}

	// Refined Red Dragon Blood
	private boolean use21903(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21903));
			return false;
		}
		player.doCast(SkillHolder.getInstance().getSkillEntry(22298, 1), player, false);
		ItemFunctions.deleteItem(player, 21903, 1);
		return true;
	}

	// Refined Blue Dragon Blood
	private boolean use21904(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21904));
			return false;
		}
		player.doCast(SkillHolder.getInstance().getSkillEntry(22299, 1), player, false);
		ItemFunctions.deleteItem(player, 21904, 1);
		return true;
	}

	// Antharas Blood Crystal
	private boolean use17268(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17268));
			return false;
		}
		player.doCast(SkillHolder.getInstance().getSkillEntry(9179, 1), player, false);
		ItemFunctions.deleteItem(player, 17268, 1);
		return true;
	}

	private boolean use17619(Player player, boolean ctrl)	
	{
		//TODO[Iqman+Nosferatu] Define zone in cruma tower we can use this scroll only there!!
		QuestState qs = player.getQuestState(10352);
		QuestState qs2 = player.getQuestState(480);
		if(player.getVar("MechanismSpawn") != null || qs == null || qs.getCond() > 4)
		{
			if(qs2 == null || qs2.getCond() > 4 || player.getVar("MechanismSpawn") != null)
			{
				player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17619));
				return false;
			}	
		}	
		
		ItemFunctions.deleteItem(player, 17619, 1);
		NpcInstance npc = NpcUtils.spawnSingle(17619, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 120000L);
		player.setVar("MechanismSpawn", "1", System.currentTimeMillis() + 120000L);
		if(qs != null && !qs.isCompleted())
		{
			Quest q = QuestHolder.getInstance().getQuest(10352);
			player.processQuestEvent(q.getId(), "advanceCond3", null);
		}
		if(qs2 != null && !qs2.isCompleted())
		{
			Quest q2 = QuestHolder.getInstance().getQuest(480);
			player.processQuestEvent(q2.getId(), "advanceCond3", null);
		}
		return true;
	}

	private boolean use17604(Player player, boolean ctrl)
	{
		//TODO[Iqman+Nosferatu] Define zone in cruma tower we can use this scroll only there!!
		QuestState qs = player.getQuestState(10301);
		GameObject target = player.getTarget();
		if(target == null || !target.isNpc())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		NpcInstance _target = (NpcInstance) target;
		if(_target.getNpcId() != 33489)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		if(qs == null || qs.getCond() != 2 || player.getVar("CrystalsSpawn") != null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		ItemFunctions.deleteItem(player, 17604, 1);
		NpcInstance npc = NpcUtils.spawnSingle(32938, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 120000L);
		NpcInstance npc2 = NpcUtils.spawnSingle(32938, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 120000L);
		player.setVar("CrystalsSpawn", "1", System.currentTimeMillis() + 120000L);
		return true;
	}

	private boolean use34033(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(10304);
		HtmlMessage msg = new HtmlMessage(5);
		if(player.getLevel() >= 90 && qs == null)
		{
			Quest q = QuestHolder.getInstance().getQuest(10304);
			QuestState st;
			st = q.newQuestState(player);
			st.setCond(1);
			msg.setFile("quests/_10304_ForTheForgottenHeroes/scroll_02.htm");
			player.sendPacket(msg);
			st.takeItems(34033, -1);
			st.giveItems(17618, 1, false);
		}
		return true;
	}

	private boolean use17603(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(466);
		if(qs != null && qs.getCond() == 4)
		{
			if(qs.getQuestItemsCount(_466_PlacingMySmallPower.WingI) >= 5 && qs.getQuestItemsCount(_466_PlacingMySmallPower.CoconI) >= 5 && qs.getQuestItemsCount(_466_PlacingMySmallPower.BreathI) >= 5)
			{
				useItem(player, 17603, 1);

				qs.setCond(5);
				qs.takeItems(_466_PlacingMySmallPower.WingI, -1);
				qs.takeItems(_466_PlacingMySmallPower.CoconI, -1);
				qs.takeItems(_466_PlacingMySmallPower.BreathI, -1);
				qs.giveItems(_466_PlacingMySmallPower.NavozItem, 1);
				return true;
			}
		}
		return false;
	}

	private boolean use37314(Player player, boolean ctrl){
		useItem(player, 37314, 1);
		switch (player.getRace())
		{
			case ORC:
				if(player.isMageClass())
					ItemFunctions.addItem(player, 37321, 1);
				else
					ItemFunctions.addItem(player, 37320, 1);
				break;
			case KAMAEL:
				ItemFunctions.addItem(player, 37319, 1);
				break;
			case ERTHEIA:
				if(player.isMageClass())
					ItemFunctions.addItem(player, 26229, 1);
				else
					ItemFunctions.addItem(player, 26230, 1);
				break;
			default:
				if(player.isMageClass())
					ItemFunctions.addItem(player, 37316, 1);
				else
				{
					if(player.getClassId().getType2() == ClassType2.WARRIOR)
						ItemFunctions.addItem(player, 37315, 1);
					else if(player.getClassId().getType2() == ClassType2.ROGUE || player.getClassId().getType2() == ClassType2.ARCHER)
						ItemFunctions.addItem(player, 37318, 1);
					else if(player.getClassId().getType2() == ClassType2.KNIGHT)
						ItemFunctions.addItem(player, 37317, 1);
				}
				break;
		}
		return true;
	}

	private boolean use36065(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(753);
		if(qs != null && qs.getCond() == 1)
		{
			if(player.getTarget() instanceof NpcInstance)
			{
				if(((NpcInstance) player.getTarget()).getNpcId() == 19296)
				{
					GameObject target = player.getTarget();
					((NpcInstance) player.getTarget()).doDie(player);
					NpcInstance npc = (NpcInstance) target;
					//player.broadcastPacket(new MagicSkillUse(player, npc, 9584, 1, 2000, 0));
					player.forceUseSkill(SkillHolder.getInstance().getSkill(9584, 1), npc);
					return true;
				}
			}
		}
		return false;
	}

	private boolean use27605(Player player, boolean ctrl)
	{
		int[] _heliosW = {18069, 18070, 18071, 18072, 18073, 18074, 18075, 18076};
		if(ItemFunctions.deleteItem(player, 27605, 1))
		{
			ItemFunctions.addItem(player, Rnd.get(_heliosW), 1);
			return true;
		}
		return false;
	}

	private boolean use27606(Player player, boolean ctrl)
	{
		int[] _heliosM = {18077, 18078, 18079};
		if(ItemFunctions.deleteItem(player, 27606, 1))
		{
			ItemFunctions.addItem(player, Rnd.get(_heliosM), 1);
			return true;
		}
		return false;
	}

	private boolean use27607(Player player, boolean ctrl)
	{
		int[] _heliosP = {18080, 18081, 18082};
		if(ItemFunctions.deleteItem(player, 27607, 1))
		{
			ItemFunctions.addItem(player, Rnd.get(_heliosP), 1);
			return true;
		}
		return false;
	}

	private static boolean useItem(Player player, int itemId, long count)
	{
		player.sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(itemId));
		return ItemFunctions.deleteItem(player, itemId, count);
	}
}