package npc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.actor.instances.player.SubClassList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SubClassTable;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Bonux
 */
public final class SubClassManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// Предмет: Сертификат на Смену Профессии
	private static final int CERTIFICATE_ID = 30433;

	public SubClassManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(final Player player, final String command)
	{
		final StringTokenizer st = new StringTokenizer(command, "_");
		final String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("subclass"))
		{
			//Глобальные проверки, которые распространяются на любые операции с саб-классами.
			if(player.getRace() == Race.ERTHEIA)
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_ertheia.htm", false);
				return;
			}

			if(player.isTransformed())
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_transform.htm", false);
				return;
			}

			if(player.hasServitor())
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_servitor.htm", false);
				return;
			}

			if(!checkSubClassQuest(player))
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_quest.htm", false);
				return;
			}

			if(!player.isQuestContinuationPossible(false))
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_weight.htm", false);
				return;
			}

			if(player.getLevel() < 40 || player.getClassId().getClassLevel().ordinal() < 2)
			{
				showChatWindow(player, "default/" + getNpcId() + "-subclass_no_level.htm", false);
				return;
			}

			// Действия с саб-классами.
			final String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("add")) // TODO: [Bonux] Сверить с оффом.
			{
				if(player.getSubClassList().size() >= SubClassList.MAX_SUB_COUNT)
				{
					showChatWindow(player, "default/" + getNpcId() + "-subclass_add_no_limit.htm", false);
					return;
				}
	
				// Проверка хватает ли уровня
				final Collection<SubClass> subClasses = player.getSubClassList().values();
				for(SubClass subClass : subClasses)
				{
					if(subClass.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
					{
						showChatWindow(player, "default/" + getNpcId() + "-subclass_add_no_level.htm", false, "<?LEVEL?>", Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS);
						return;
					}
				}

				if(!st.hasMoreTokens())
				{
					final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-subclass_add_list.htm", player);
					final String html = tpls.get(0);
					String bypass = tpls.get(1);

					final StringBuilder classes = new StringBuilder();
					final int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, player.getActiveClassId(), ClassLevel.SECOND);
					for(int subClsId : availSubClasses)
					{
						// На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
						classes.append(bypass.replace("<?class_id?>", String.valueOf(subClsId)).replace("<?class_name?>", getClassIdNames(player, subClsId)));
					}
					showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
					return;
				}
				else
				{
					final int addSubClassId = Integer.parseInt(st.nextToken());
					if(!st.hasMoreTokens())
					{
						showChatWindow(player, "default/" + getNpcId() + "-subclass_add_confirm.htm", false, "<?class_id?>", String.valueOf(addSubClassId), "<?class_name?>", getClassIdNames(player, addSubClassId));
						return;
					}
					else
					{
						final String cmd3 = st.nextToken();
						if(cmd3.equalsIgnoreCase("confirm"))
						{
							/*TODO: [Bonux] Проверить на оффе.
							if(Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player))
							{
								player.sendPacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
								return;
							}*/

							if(player.addSubClass(addSubClassId, true, 0, 0, 0L, Config.STARTING_SP_SUB))
							{
								player.sendPacket(new ExSubjobInfo(player, true));
								player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(addSubClassId));

								showChatWindow(player, "default/" + getNpcId() + "-subclass_add_success.htm", false);
								return;
							}
							else
							{
								showChatWindow(player, "default/" + getNpcId() + "-subclass_add_error.htm", false);
								return;
							}
						}
					}
				}
			}
			else if(cmd2.equalsIgnoreCase("change"))
			{
				final List<SubClass> subClasses = new ArrayList<SubClass>();
				for(SubClass sub : player.getSubClassList().values())
				{
					if(sub != null && !sub.isBase() && !sub.isDual())
					{
						final ClassId classId = ClassId.VALUES[sub.getClassId()];
						if(classId.isOfLevel(ClassLevel.SECOND) || classId.isOfLevel(ClassLevel.THIRD))
							subClasses.add(sub);
					}
				}

				if(subClasses.isEmpty()) // TODO: [Bonux] Проверить сообщение на оффе.
				{
					showChatWindow(player, "default/" + getNpcId() + "-subclass_no_subs.htm", false);
					return;
				}

				if(ItemFunctions.getItemCount(player, CERTIFICATE_ID) == 0)
				{
					showChatWindow(player, "default/" + getNpcId() + "-subclass_no_certificate.htm", false);
					return;
				}

				if(!st.hasMoreTokens())
				{
					final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-subclass_change_list.htm", player);
					final String html = tpls.get(0);
					String bypass = tpls.get(1);

					final StringBuilder classes = new StringBuilder();
					for(SubClass sub : subClasses)
					{
						final int classId = sub.getClassId();
						// На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
						classes.append(bypass.replace("<?class_id?>", String.valueOf(classId)).replace("<?class_name?>", getClassIdNames(player, classId)));
					}
					showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
					return;
				}
				else
				{
					final int changeClassId = Integer.parseInt(st.nextToken());
					if(!st.hasMoreTokens())
					{
						final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-subclass_change_avail_list.htm", player);
						final String html = tpls.get(0);
						String bypass = tpls.get(1);

						final StringBuilder classes = new StringBuilder();
						final int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, changeClassId, ClassId.VALUES[changeClassId].getClassLevel());
						for(int subClsId : availSubClasses)
						{
							// На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
							classes.append(bypass.replace("<?change_class_id?>", String.valueOf(changeClassId)).replace("<?class_id?>", String.valueOf(subClsId)).replace("<?class_name?>", getClassIdNames(player, subClsId)));
						}
						showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
						return;
					}
					else
					{
						final int newSubClassId = Integer.parseInt(st.nextToken());
						if(!st.hasMoreTokens())
						{
							showChatWindow(player, "default/" + getNpcId() + "-subclass_change_confirm.htm", false, "<?change_class_id?>", String.valueOf(changeClassId), "<?class_id?>", String.valueOf(newSubClassId), "<?class_name?>", getClassIdNames(player, newSubClassId));
							return;
						}
						else
						{
							final String cmd3 = st.nextToken();
							if(cmd3.equalsIgnoreCase("confirm"))
							{
								if(player.modifySubClass(changeClassId, newSubClassId, true))
								{
									ItemFunctions.deleteItem(player, CERTIFICATE_ID, 1, true);

									player.sendPacket(new ExSubjobInfo(player, true));
									player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newSubClassId));

									showChatWindow(player, "default/" + getNpcId() + "-subclass_add_success.htm", false);
									return;
								}
								else
								{
									showChatWindow(player, "default/" + getNpcId() + "-subclass_add_error.htm", false);
									return;
								}
							}
						}
					}
				}
			}
			else if(cmd2.equalsIgnoreCase("cancel"))
			{
				final List<SubClass> subClasses = new ArrayList<SubClass>();
				for(SubClass sub : player.getSubClassList().values())
				{
					if(sub != null && !sub.isBase() && !sub.isDual())
					{
						final ClassId classId = ClassId.VALUES[sub.getClassId()];
						if(classId.isOfLevel(ClassLevel.SECOND) || classId.isOfLevel(ClassLevel.THIRD))
							subClasses.add(sub);
					}
				}

				if(subClasses.isEmpty()) // TODO: [Bonux] Проверить сообщение на оффе.
				{
					showChatWindow(player, "default/" + getNpcId() + "-subclass_no_subs.htm", false);
					return;
				}

				if(!st.hasMoreTokens())
				{
					final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-subclass_cancel_list.htm", player);
					final String html = tpls.get(0);
					String bypass = tpls.get(1);

					final StringBuilder classes = new StringBuilder();
					for(SubClass sub : subClasses)
					{
						final int classId = sub.getClassId();
						// На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
						classes.append(bypass.replace("<?class_id?>", String.valueOf(classId)).replace("<?class_name?>", getClassIdNames(player, classId)));
					}
					showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
					return;
				}
				else
				{
					final int cancelClassId = Integer.parseInt(st.nextToken());
					if(!st.hasMoreTokens())
					{
						final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-subclass_cancel_avail_list.htm", player);
						final String html = tpls.get(0);
						String bypass = tpls.get(1);

						final StringBuilder classes = new StringBuilder();
						final int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, cancelClassId, ClassLevel.SECOND);
						for(int subClsId : availSubClasses)
						{
							// На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
							classes.append(bypass.replace("<?cancel_class_id?>", String.valueOf(cancelClassId)).replace("<?class_id?>", String.valueOf(subClsId)).replace("<?class_name?>", getClassIdNames(player, subClsId)));
						}
						showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
						return;
					}
					else
					{
						final int newSubClassId = Integer.parseInt(st.nextToken());
						if(!st.hasMoreTokens())
						{
							showChatWindow(player, "default/" + getNpcId() + "-subclass_cancel_confirm.htm", false, "<?cancel_class_id?>", String.valueOf(cancelClassId), "<?class_id?>", String.valueOf(newSubClassId), "<?class_name?>", getClassIdNames(player, newSubClassId));
							return;
						}
						else
						{
							final String cmd3 = st.nextToken();
							if(cmd3.equalsIgnoreCase("confirm"))
							{
								if(player.modifySubClass(cancelClassId, newSubClassId, false))
								{
									player.sendPacket(new ExSubjobInfo(player, true));
									player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newSubClassId));

									showChatWindow(player, "default/" + getNpcId() + "-subclass_add_success.htm", false);
									return;
								}
								else
								{
									showChatWindow(player, "default/" + getNpcId() + "-subclass_add_error.htm", false);
									return;
								}
							}
						}
					}
				}
			}
		}
		else if(cmd.equalsIgnoreCase("reawake"))
		{
			if(!player.isDualClassActive() || !player.getClassId().isOfLevel(ClassLevel.AWAKED))
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no.htm", false);
				return;
			}

			if(!player.isQuestContinuationPossible(false))
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no_weight.htm", false);
				return;
			}

			final int cost = calcReawakeCost(player);
			if(player.getAdena() < cost)
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no_adena.htm", false, "<?reawake_price?>", String.valueOf(cost));
				return;
			}

			final int cloakId = getCloakId(player.getClassId());
			if(cloakId == 0 || ItemFunctions.getItemCount(player, cloakId) == 0)
			{
				// [Bonux] На оффе нету отдельного диалога при отсутствии плаща. На оффе выводится диалог отсутствия адены.
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no_adena.htm", false, "<?reawake_price?>", String.valueOf(cost));
				return;
			}

			if(player.isTransformed())
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no_transform.htm", false);
				return;
			}

			if(player.hasServitor())
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_no_servitor.htm", false);
				return;
			}

			if(!st.hasMoreTokens())
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake_continue.htm", false, "<?reawake_price?>", String.valueOf(cost));
				return;
			}

			final String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("continue"))
			{
				if(!st.hasMoreTokens())
				{
					final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-reawake_list.htm", player);
					final String html = tpls.get(0);
					String bypass = tpls.get(1);

					final StringBuilder classes = new StringBuilder();
					final int changeClassId = player.getClassId().getId();
					for(ClassId clsId : ClassId.VALUES)
					{
						if(!clsId.isOutdated())
							continue;

						if(!clsId.isOfLevel(ClassLevel.AWAKED))
							continue;

						classes.append(bypass.replace("<?change_class_id?>", String.valueOf(changeClassId)).replace("<?class_id?>", String.valueOf(clsId.getId())).replace("<?class_name?>", getClassIdNames(player, clsId.getId())));
					}

					showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
					return;
				}
				else
				{
					final int playerClassId = player.getClassId().getId();
					final int changeClassId = Integer.parseInt(st.nextToken());
					final int newClassId = Integer.parseInt(st.nextToken());

					if(changeClassId != playerClassId) // На всякий пожарный..
						return;

					if(!st.hasMoreTokens())
					{
						final int[] availClasses = SubClassTable.getInstance().getAvailableSubClasses(player, changeClassId, ClassLevel.AWAKED);
						final ClassId newClsId = ClassId.VALUES[newClassId];

						final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-reawake_last_list.htm", player);
						final String html = tpls.get(0);
						String bypass = tpls.get(1);

						boolean avail = false;

						final StringBuilder classes = new StringBuilder();
						for(ClassId c : ClassId.VALUES)
						{
							if(c.getBaseAwakedClassId() != newClsId)
								continue;

							if(!ArrayUtils.contains(availClasses, c.getId()))
								continue;

							classes.append(bypass.replace("<?change_class_id?>", String.valueOf(changeClassId)).replace("<?class_id?>", String.valueOf(c.getId())).replace("<?class_name?>", c.getName(player)));
							avail = true;
						}

						if(!avail)
						{
							showChatWindow(player, "default/" + getNpcId() + "-reawake_no_avail.htm", false);
							return;
						}

						showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
						return;
					}
					else
					{
						final String cmd3 = st.nextToken();
						if(cmd3.equalsIgnoreCase("finish"))
						{
							if(changeClassId == newClassId || playerClassId == newClassId) // На всякий пожарный..
								return;

							if(player.modifySubClass(changeClassId, newClassId, true))
							{
								// Сбрасываем до 85 уровня.
								final long newExp = Experience.getExpForLevel(85) - player.getExp();
								player.addExpAndSp(newExp, 0, true);

								player.reduceAdena(cost, true);

								ItemFunctions.deleteItem(player, cloakId, 1, true);
								ItemFunctions.addItem(player, getCloakId(ClassId.VALUES[newClassId]), 1, true);

								player.sendPacket(new ExSubjobInfo(player, true));
								player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newClassId));
								player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.REAWAKENING));

								showChatWindow(player, "default/" + getNpcId() + "-reawake_success.htm", false);
								return;
							}
							else
							{
								showChatWindow(player, "default/" + getNpcId() + "-reawake_error.htm", false);
								return;
							}
						}
					}
				}
			}
		}
		else if(cmd.equalsIgnoreCase("dualclass"))
		{
			if(player.getRace() != Race.ERTHEIA || player.getLevel() < 85 || !player.getClassId().isOfLevel(ClassLevel.THIRD) || !player.isBaseClassActive() || player.isDualClassActive() || player.getSubClassList().haveSubClasses())
			{
				showChatWindow(player, "default/" + getNpcId() + "-dualclass_no.htm", false);
				return;
			}

			if(!checkErtheiaDualClassQuest(player))
			{
				showChatWindow(player, "default/" + getNpcId() + "-dualclass_no_quest.htm", false);
				return;
			}

			if(player.isTransformed())
			{
				showChatWindow(player, "default/" + getNpcId() + "-dualclass_no_transform.htm", false);
				return;
			}

			if(player.hasServitor())
			{
				showChatWindow(player, "default/" + getNpcId() + "-dualclass_no_servitor.htm", false);
				return;
			}

			final String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("add"))
			{
				if(!st.hasMoreTokens())
				{
					final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-dualclass_add_list.htm", player);
					final String html = tpls.get(0);
					String bypass = tpls.get(1);

					final StringBuilder classes = new StringBuilder();
					final int changeClassId = player.getClassId().getId();
					for(ClassId clsId : ClassId.VALUES)
					{
						if(!clsId.isOutdated())
							continue;

						if(!clsId.isOfLevel(ClassLevel.AWAKED))
							continue;

						classes.append(bypass.replace("<?class_id?>", String.valueOf(clsId.getId())).replace("<?class_name?>", getClassIdNames(player, clsId.getId())));
					}

					showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
					return;
				}
				else
				{
					final int newClassId = Integer.parseInt(st.nextToken());

					if(!st.hasMoreTokens())
					{
						final int[] availClasses = SubClassTable.getInstance().getAvailableSubClasses(player, player.getActiveClassId(), ClassLevel.AWAKED);
						final ClassId newClsId = ClassId.VALUES[newClassId];

						final HtmTemplates tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-dualclass_add_last_list.htm", player);
						final String html = tpls.get(0);
						String bypass = tpls.get(1);

						boolean avail = false;

						final StringBuilder classes = new StringBuilder();
						for(ClassId c : ClassId.VALUES)
						{
							if(c.getBaseAwakedClassId() != newClsId)
								continue;

							if(!ArrayUtils.contains(availClasses, c.getId()))
								continue;

							classes.append(bypass.replace("<?class_id?>", String.valueOf(c.getId())).replace("<?class_name?>", c.getName(player)));
							avail = true;
						}

						if(!avail)
							return;

						showChatWindow(player, html, false, "<?CLASS_LIST?>", classes.toString());
						return;
					}
					else
					{
						final String cmd3 = st.nextToken();
						if(cmd3.equalsIgnoreCase("finish"))
						{
							if(!player.isQuestContinuationPossible(false))
							{
								player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
								showChatWindow(player, "default/" + getNpcId() + "-dualclass_no_weight.htm", false);
								return;
							}

							if(player.addSubClass(newClassId, true, 0, 0, SubClassType.DUAL_SUBCLASS, Experience.getExpForLevel(85), Config.STARTING_SP_SUB))
							{
								player.sendPacket(new ExSubjobInfo(player, true));
								player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newClassId));
								player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.REAWAKENING));
								showChatWindow(player, "default/" + getNpcId() + "-dualclass_add_success.htm", false);
								return;
							}
							else
							{
								showChatWindow(player, "default/" + getNpcId() + "-dualclass_add_error.htm", false);
								return;
							}
						}
					}
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static boolean checkSubClassQuest(Player player)
	{
		if(!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !player.isNoble())
			return player.isQuestCompleted(10385);
		return true;
	}

	private static boolean checkErtheiaDualClassQuest(Player player)
	{
		if(!Config.ALT_GAME_ERTHEIA_DUALCLASS_WITHOUT_QUESTS && !player.isNoble())
			return player.isQuestCompleted(10472);
		return true;
	}

	private static int calcReawakeCost(Player player)
	{
		int level = player.getLevel();
		switch(level)
		{
			case 86:
				return 90000000;
			case 87:
				return 80000000;
			case 88:
				return 70000000;
			case 89:
				return 60000000;
			case 90:
				return 50000000;
			case 91:
				return 40000000;
			case 92:
				return 30000000;
			case 93:
				return 20000000;
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
				return 10000000;
		}
		return 100000000;
	}

	private static String getClassIdNames(Player player, int id)
	{
		final ClassId classId = ClassId.VALUES[id];
		final StringBuilder className = new StringBuilder();
		if(classId.isOfLevel(ClassLevel.THIRD))
		{
			final ClassId parent = classId.getParent(player.getSex().ordinal());
			if(parent != null)
			{
				/*className.append(HtmlUtils.htmlClassName(parent.getId()));
				className.append("/");*/
			}
			className.append(HtmlUtils.htmlClassName(classId.getId()));
		}
		else if(classId.isOfLevel(ClassLevel.SECOND))
		{
			className.append(HtmlUtils.htmlClassName(classId.getId()));
			for(ClassId child : ClassId.VALUES)
			{
				if(child.isOfLevel(ClassLevel.THIRD) && child.getParent(player.getSex().ordinal()) == classId)
				{
					/*className.append("/");
					className.append(HtmlUtils.htmlClassName(child.getId()));*/
					break;
				}
			}
		}
		else
			className.append(HtmlUtils.htmlClassName(classId.getId()));
		return className.toString();
	}

	private static int getCloakId(ClassId classId)
	{
		if(!classId.isOfLevel(ClassLevel.AWAKED))
			return 0;

		final ClassId baseAwakedClassId = classId.getBaseAwakedClassId();
		if(baseAwakedClassId == null)
			return 0;

		switch(baseAwakedClassId)
		{
			case SIGEL_KNIGHT:
				return 30310;
			case TYR_WARRIOR:
				return 30311;
			case OTHELL_ROGUE:
				return 30312;
			case YR_ARCHER:
				return 30313;
			case FEOH_WIZARD:
				return 30314;
			case WYNN_SUMMONER:
				return 30315;
			case ISS_ENCHANTER:
				return 30316;
			case EOLH_HEALER:
				return 30317;
		}
		return 0;
	}
}