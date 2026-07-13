package npc.model;

import java.util.Collection;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public final class CertificationManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int CERTIFICATION_ITEM = 10280; // Сертификат - Новые Навыки
	private static final int DUALCERTIFICATION_ITEM = 36078; // Сертификат - Новые Навыки
	private static final long CERTIFICATIN_CANCEL_PRICE = 10000000L;
	private static final long DUALCERTIFICATIN_CANCEL_PRICE = 20000000L;

	public CertificationManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "certification/";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("certification"))
		{
			if(player.getRace() == Race.ERTHEIA)
			{
				player.sendMessage("Для Артей недоступно"); // TODO Офф диалог
				return;
			}

			// Действия с сертификациями.
			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("get"))
			{
				if(player.isBaseClassActive())
				{
					showChatWindow(player, "certification/" + getNpcId() + "-no_sub.htm", false);
					return;
				}

				if(player.getLevel() < 65)
				{
					showChatWindow(player, "certification/" + getNpcId() + "-no_level.htm", false);
					return;
				}

				if(!st.hasMoreTokens())
				{
					SubClass subClass = player.getActiveSubClass();
					if(subClass.isCertificationGet(SubClass.CERTIFICATION_65) && subClass.isCertificationGet(SubClass.CERTIFICATION_70) && subClass.isCertificationGet(SubClass.CERTIFICATION_75) && subClass.isCertificationGet(SubClass.CERTIFICATION_80))
					{
						showChatWindow(player, "certification/" + getNpcId() + "-all_ceritficate_gived.htm", false);
						return;
					}
					showChatWindow(player, "certification/" + getNpcId() + "-certification_list.htm", false);
					return;
				}
				else
				{
					int certificationLvl = Integer.parseInt(st.nextToken());

					if(player.getLevel() < certificationLvl)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_level_" + certificationLvl + ".htm", false);
						return;
					}

					SubClass subClass = player.getActiveSubClass();
					switch(certificationLvl)
					{
						case 65:
							if(subClass.isCertificationGet(SubClass.CERTIFICATION_65))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-no_already_received.htm", false);
								return;
							}
							break;
						case 70:
							if(subClass.isCertificationGet(SubClass.CERTIFICATION_70))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-no_already_received.htm", false);
								return;
							}
							break;
						case 75:
							if(subClass.isCertificationGet(SubClass.CERTIFICATION_75))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-no_already_received.htm", false);
								return;
							}
							break;
						case 80:
							if(subClass.isCertificationGet(SubClass.CERTIFICATION_80))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-no_already_received.htm", false);
								return;
							}
							break;
						default:
							return;
					}

					if(!st.hasMoreTokens())
					{
						showChatWindow(player, "certification/" + getNpcId() + "-get_certification_" + certificationLvl + ".htm", false);
						return;
					}
					else
					{
						String cmd3 = st.nextToken();
						if(cmd3.equalsIgnoreCase("give"))
						{
							switch(certificationLvl)
							{
								case 65:
									subClass.addCertification(SubClass.CERTIFICATION_65);
									break;
								case 70:
									subClass.addCertification(SubClass.CERTIFICATION_70);
									break;
								case 75:
									subClass.addCertification(SubClass.CERTIFICATION_75);
									break;
								case 80:
									subClass.addCertification(SubClass.CERTIFICATION_80);
									break;
								default:
									return;
							}

							ItemFunctions.addItem(player, CERTIFICATION_ITEM, 1, true);
							player.store(true);
							showChatWindow(player, "certification/" + getNpcId() + "-give_certification.htm", false);
							return;
						}
					}
				}
			}
			else if(cmd2.equalsIgnoreCase("skills"))
			{
				if(!st.hasMoreTokens())
					return;

				String cmd3 = st.nextToken();
				if(cmd3.equalsIgnoreCase("learn"))
				{
					if(!checkTransformationQuest(player))
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_quest.htm", false);
						return;
					}

					long certificateItemCount = ItemFunctions.getItemCount(player, CERTIFICATION_ITEM);
					if(certificateItemCount == 0)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_base.htm", false);
						return;
					}

					showAcquireList(AcquireType.CERTIFICATION, player);
					return;
				}
				else if(cmd3.equalsIgnoreCase("cancel"))
				{
					if(player.getAdena() < CERTIFICATIN_CANCEL_PRICE)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_adena_cancel.htm", false);
						return;
					}

					if(!checkTransformationQuest(player))
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_quest.htm", false);
						return;
					}

					if(!player.isBaseClassActive())
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_base.htm", false);
						return;
					}

					boolean hasCertification = false;
					for(SubClass subClass : player.getSubClassList().values())
					{
						if(subClass.isBase())
							continue;

						if(subClass.getCertification() == 0)
							continue;

						subClass.setCertification(0);
						hasCertification = true;
					}

					if(!hasCertification)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-no_cancel.htm", false);
						return;
					}

					player.store(true);

					Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, AcquireType.CERTIFICATION);
					for(SkillLearn learn : skillLearnList)
					{
						SkillEntry skillEntry = player.getKnownSkill(learn.getId());
						if(skillEntry == null)
							continue;

						player.removeSkill(skillEntry, true);
					}

					player.reduceAdena(CERTIFICATIN_CANCEL_PRICE, true);
					ItemFunctions.deleteItem(player, CERTIFICATION_ITEM, ItemFunctions.getItemCount(player, CERTIFICATION_ITEM), true);
					showChatWindow(player, "certification/" + getNpcId() + "-certification_cancel.htm", false);
					return;
				}
			}
		}
		else if(cmd.equalsIgnoreCase("dualcertification")) // TODO: [Bonux] Сверить с оффом Хтмлки.
		{
			// Действия с сертификациями.
			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("get"))
			{
				if(!player.isDualClassActive())
				{
					showChatWindow(player, "certification/" + getNpcId() + "-d_no_sub.htm", false);
					return;
				}

				if(player.getLevel() < 85)
				{
					showChatWindow(player, "certification/" + getNpcId() + "-d_no_level.htm", false);
					return;
				}

				SubClass subClass = player.getActiveSubClass();

//				if(!subClass.isCertificationGet(SubClass.CERTIFICATION_65) || !subClass.isCertificationGet(SubClass.CERTIFICATION_70) || !subClass.isCertificationGet(SubClass.CERTIFICATION_75) || !subClass.isCertificationGet(SubClass.CERTIFICATION_80))
//				{
//					showChatWindow(player, "certification/" + getNpcId() + "-d_no_have_certification.htm", false);
//					return;
//				}

				if(!st.hasMoreTokens())
				{
					if(subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_85) && subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_90) && subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_95) && subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_99))
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_all_ceritficate_gived.htm", false);
						return;
					}
					showChatWindow(player, "certification/" + getNpcId() + "-d_certification_list.htm", false);
					return;
				}
				else
				{
					int certificationLvl = Integer.parseInt(st.nextToken());

					if(player.getLevel() < certificationLvl)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_no_level_" + certificationLvl + ".htm", false);
						return;
					}

					switch(certificationLvl)
					{
						case 85:
							if(subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_85))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-d_no_already_received.htm", false);
								return;
							}
							break;
						case 90:
							if(subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_90))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-d_no_already_received.htm", false);
								return;
							}
							break;
						case 95:
							if(subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_95))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-d_no_already_received.htm", false);
								return;
							}
							break;
						case 99:
							if(subClass.isDualCertificationGet(SubClass.DUALCERTIFICATION_99))
							{
								showChatWindow(player, "certification/" + getNpcId() + "-d_no_already_received.htm", false);
								return;
							}
							break;
						default:
							return;
					}

					if(!st.hasMoreTokens())
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_get_certification_" + certificationLvl + ".htm", false);
						return;
					}
					else
					{
						String cmd3 = st.nextToken();
						if(cmd3.equalsIgnoreCase("give"))
						{
							switch(certificationLvl)
							{
								case 85:
									subClass.addDualCertification(SubClass.DUALCERTIFICATION_85);
									break;
								case 90:
									subClass.addDualCertification(SubClass.DUALCERTIFICATION_90);
									break;
								case 95:
									subClass.addDualCertification(SubClass.DUALCERTIFICATION_95);
									break;
								case 99:
									subClass.addDualCertification(SubClass.DUALCERTIFICATION_99);
									break;
								default:
									return;
							}

							ItemFunctions.addItem(player, DUALCERTIFICATION_ITEM, 1, true);
							player.store(true);
							showChatWindow(player, "certification/" + getNpcId() + "-d_give_certification.htm", false);
							return;
						}
					}
				}
			}
			else if(cmd2.equalsIgnoreCase("skills"))
			{
				if(!st.hasMoreTokens())
					return;

				String cmd3 = st.nextToken();
				if(cmd3.equalsIgnoreCase("learn"))
				{
					long certificateItemCount = ItemFunctions.getItemCount(player, DUALCERTIFICATION_ITEM);
					if(certificateItemCount == 0)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_no_have_certification.htm", false);
						return;
					}

					showAcquireList(AcquireType.DUAL_CERTIFICATION, player);
					return;
				}
				else if(cmd3.equalsIgnoreCase("cancel"))
				{
					if(player.getAdena() < DUALCERTIFICATIN_CANCEL_PRICE) // ok
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_no_adena_cancel.htm", false);
						return;
					}

					if(!player.isBaseClassActive())
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_no_base.htm", false);
						return;
					}

					boolean hasCertification = false;
					for(SubClass subClass : player.getSubClassList().values())
					{
						if(subClass.isBase())
							continue;

						if(subClass.getDualCertification() == 0)
							continue;

						subClass.setDualCertification(0);
						hasCertification = true;
					}

					if(!hasCertification)
					{
						showChatWindow(player, "certification/" + getNpcId() + "-d_no_cancel.htm", false);
						return;
					}

					player.store(true);

					Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, AcquireType.DUAL_CERTIFICATION);
					for(SkillLearn learn : skillLearnList)
					{
						SkillEntry skillEntry = player.getKnownSkill(learn.getId());
						if(skillEntry == null)
							continue;

						player.removeSkill(skillEntry, true);
					}

					player.reduceAdena(DUALCERTIFICATIN_CANCEL_PRICE, true);
					ItemFunctions.deleteItem(player, DUALCERTIFICATION_ITEM, ItemFunctions.getItemCount(player, DUALCERTIFICATION_ITEM), true);
					showChatWindow(player, "certification/" + getNpcId() + "-d_certification_cancel.htm", false);
					return;
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static boolean checkTransformationQuest(Player player)
	{
		if(!Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST)
			return player.isQuestCompleted(136);
		return true;
	}
}