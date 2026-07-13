package handler.items;

import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

/**
 LIO
 24.01.2016
 */
public class StartQuest extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		switch(item.getItemId())
		{
			case 35702:
			{
				Quest quest = QuestHolder.getInstance().getQuest(756);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					qs = quest.newQuestState(player);
					qs.setCond(1);
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46090:
			{
				//TODO
				//Functions.show("quests/_665_GreatPirateZakensTreasure/46090-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(665);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46096, 1, true);
					qs = quest.newQuestState(player);
					Functions.show("quests/_665_GreatPirateZakensTreasure/46090-02.htm", player, item.getItemId());
					qs.setCond(1);
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46091:
			{
				//TODO
				//Functions.show("quests/_666_LeonaBlackbirdsTreasure/46091-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(666);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46097, 1, true);
					qs = quest.newQuestState(player);
					Functions.show("quests/_666_LeonaBlackbirdsTreasure/46091-02.htm", player, item.getItemId());
					qs.setCond(1);
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46092:
			{
				//TODO
				//Functions.show("quests/_667_QueenBeorasLegacy/46092-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(667);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46098, 1, true);
					qs = quest.newQuestState(player);
					qs.setCond(1);
					Functions.show("quests/_667_QueenBeorasLegacy/46092-02.htm", player, item.getItemId());
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46093:
			{
				//TODO
				//Functions.show("quests/_668_TheGladiatorsTreasure/46093-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(668);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46099, 1, true);
					qs = quest.newQuestState(player);
					qs.setCond(1);
					Functions.show("quests/_668_TheGladiatorsTreasure/46093-02.htm", player, item.getItemId());
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46094:
			{
				//TODO
				//Functions.show("quests/_668_TheGladiatorsTreasure/46093-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(669);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46100, 1, true);
					qs = quest.newQuestState(player);
					qs.setCond(1);
					Functions.show("quests/_669_HighPriestsTreasure/46094-02.htm", player, item.getItemId());
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;
			case 46095:
			{
				//TODO
				//Functions.show("quests/_668_TheGladiatorsTreasure/46093-02.htm", player, item.getItemId());
				//player.sendActionFailed();

				Quest quest = QuestHolder.getInstance().getQuest(670);
				QuestState qs = player.getQuestState(quest.getId());
				if(qs == null || qs.isCompleted())
				{
					ItemFunctions.deleteItem(player, item, 1, true);
					ItemFunctions.addItem(player, 46101, 1, true);
					qs = quest.newQuestState(player);
					qs.setCond(1);
					Functions.show("quests/_670_TurekOrcsTreasure/46095-02.htm", player, item.getItemId());
				}
				else
					player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_USE_QUEST_ITEMS));
			}
			break;


			case 47513:  // Поручение - Алтарь Зла
			{
				Quest quest = QuestHolder.getInstance().getQuest(800);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_800_HunterGuildRequestAltarOfEvil/hunter_commission_paper1_q0800_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_800_HunterGuildRequestAltarOfEvil/hunter_commission_paper1_q0800_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_800_HunterGuildRequestAltarOfEvil/hunter_commission_paper1_q0800_02.htm", player, item.getItemId());
			}
			break;
			case 47514:
			{
				Quest quest = QuestHolder.getInstance().getQuest(801);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_801_HunterGuildRequestGludioTerritory/hunter_commission_paper2_q0801_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_801_HunterGuildRequestGludioTerritory/hunter_commission_paper2_q0801_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_801_HunterGuildRequestGludioTerritory/hunter_commission_paper2_q0801_02.htm", player, item.getItemId());
			}
			break;
			case 47515:
			{
				Quest quest = QuestHolder.getInstance().getQuest(802);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_802_HunterGuildRequestTurekOrcCampsite/hunter_commission_paper3_q0802_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_802_HunterGuildRequestTurekOrcCampsite/hunter_commission_paper3_q0802_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_802_HunterGuildRequestTurekOrcCampsite/hunter_commission_paper3_q0802_02.htm", player, item.getItemId());
			}
			break;
			case 47516:
			{
				Quest quest = QuestHolder.getInstance().getQuest(803);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_803_HunterGuildRequestElvenForest/hunter_commission_paper4_q0803_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_803_HunterGuildRequestElvenForest/hunter_commission_paper4_q0803_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_803_HunterGuildRequestElvenForest/hunter_commission_paper4_q0803_02.htm", player, item.getItemId());
			}
			break;
			case 47517:
			{
				Quest quest = QuestHolder.getInstance().getQuest(804);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_804_HunterGuildRequestFairySettlement/hunter_commission_paper5_q0804_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_804_HunterGuildRequestFairySettlement/hunter_commission_paper5_q0804_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_804_HunterGuildRequestFairySettlement/hunter_commission_paper5_q0804_02.htm", player, item.getItemId());
			}
			break;
			case 47518:
			{
				Quest quest = QuestHolder.getInstance().getQuest(805);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_805_HunterGuildRequestGardenOfBeasts/hunter_commission_paper6_q0805_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_805_HunterGuildRequestGardenOfBeasts/hunter_commission_paper6_q0805_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_805_HunterGuildRequestGardenOfBeasts/hunter_commission_paper6_q0805_02.htm", player, item.getItemId());
			}
			break;
			case 47519:
			{
				Quest quest = QuestHolder.getInstance().getQuest(806);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_806_HunterGuildRequestNorthOfTheTownOfGiran/hunter_commission_paper7_q0806_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_806_HunterGuildRequestNorthOfTheTownOfGiran/hunter_commission_paper7_q0806_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_806_HunterGuildRequestNorthOfTheTownOfGiran/hunter_commission_paper7_q0806_02.htm", player, item.getItemId());
			}
			break;
			case 47520:
			{
				Quest quest = QuestHolder.getInstance().getQuest(807);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_807_HunterGuildRequestCrumaMarshlands/hunter_commission_paper8_q0807_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_807_HunterGuildRequestCrumaMarshlands/hunter_commission_paper8_q0807_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_807_HunterGuildRequestCrumaMarshlands/hunter_commission_paper8_q0807_02.htm", player, item.getItemId());
			}
			break;
			case 47521:
			{
				Quest quest = QuestHolder.getInstance().getQuest(808);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_808_HunterGuildRequestTheFields/hunter_commission_paper9_q0808_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_808_HunterGuildRequestTheFields/hunter_commission_paper9_q0808_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_808_HunterGuildRequestTheFields/hunter_commission_paper9_q0808_02.htm", player, item.getItemId());
			}
			break;
			case 47522:
			{
				Quest quest = QuestHolder.getInstance().getQuest(809);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_809_HunterGuildRequestTheImmortalPlateau/hunter_commission_paper10_q0809_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_809_HunterGuildRequestTheImmortalPlateau/hunter_commission_paper10_q0809_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_809_HunterGuildRequestTheImmortalPlateau/hunter_commission_paper10_q0809_02.htm", player, item.getItemId());
			}
			break;
			case 47523:
			{
				Quest quest = QuestHolder.getInstance().getQuest(810);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_810_HunterGuildRequestIsleOfSouls/hunter_commission_paper11_q0810_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_810_HunterGuildRequestIsleOfSouls/hunter_commission_paper11_q0810_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_810_HunterGuildRequestIsleOfSouls/hunter_commission_paper11_q0810_02.htm", player, item.getItemId());
			}
			break;
			case 47524:
			{
				Quest quest = QuestHolder.getInstance().getQuest(811);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_811_HunterGuildRequestCemetery/hunter_commission_paper12_q0811_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_811_HunterGuildRequestCemetery/hunter_commission_paper12_q0811_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_811_HunterGuildRequestCemetery/hunter_commission_paper12_q0811_02.htm", player, item.getItemId());
			}
			break;
			case 47525:
			{
				Quest quest = QuestHolder.getInstance().getQuest(825);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_825_HunterGuildRequestValleyOfSaints/hunter_commission_paper13_q0825_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_825_HunterGuildRequestValleyOfSaints/hunter_commission_paper13_q0825_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_825_HunterGuildRequestValleyOfSaints/hunter_commission_paper13_q0825_02.htm", player, item.getItemId());
			}
			case 47526:
			{
				Quest quest = QuestHolder.getInstance().getQuest(832);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_832_HunterGuildRequestSouthernRegionIsleOfPrayer/hunter_commission_paper14_q0832_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_832_HunterGuildRequestSouthernRegionIsleOfPrayer/hunter_commission_paper14_q0832_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_832_HunterGuildRequestSouthernRegionIsleOfPrayer/hunter_commission_paper14_q0832_02.htm", player, item.getItemId());
			}
			break;
			case 47527:
			{
				Quest quest = QuestHolder.getInstance().getQuest(922);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_922_HunterGuildRequestNorthernRegionIsleOfPrayer/hunter_commission_paper15_q0922_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_922_HunterGuildRequestNorthernRegionIsleOfPrayer/hunter_commission_paper15_q0922_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_922_HunterGuildRequestNorthernRegionIsleOfPrayer/hunter_commission_paper15_q0922_02.htm", player, item.getItemId());
			}
			break;
			case 47528:
			{
				Quest quest = QuestHolder.getInstance().getQuest(925);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_925_HunterGuildRequestGardenOfSpirits/hunter_commission_paper16_q0925_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_925_HunterGuildRequestGardenOfSpirits/hunter_commission_paper16_q0925_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_925_HunterGuildRequestGardenOfSpirits/hunter_commission_paper16_q0925_02.htm", player, item.getItemId());
			}
			break;
			case 47529:
			{
				Quest quest = QuestHolder.getInstance().getQuest(940);
				QuestState qs = player.getQuestState(quest.getId());
				if (qs != null && qs.getCond() > 0)
					Functions.show("quests/_940_HunterGuildRequestAteliaFortress/hunter_commission_paper17_q0940_03.htm", player, item.getItemId());
				else if(checkQuest(player, quest.getId()))
				{
					startQuest(player, qs, quest.getId(), 1, 0, item);
					Functions.show("quests/_940_HunterGuildRequestAteliaFortress/hunter_commission_paper17_q0940_03.htm", player, item.getItemId());
				}
				else
					Functions.show("quests/_940_HunterGuildRequestAteliaFortress/hunter_commission_paper17_q0940_02.htm", player, item.getItemId());
			}
			break;
		}
		return true;
	}

	private boolean checkQuest(Player player, int questId)
	{
		Quest q = QuestHolder.getInstance().getQuest(questId);
		if(q != null)
		{
			QuestState qs = player.getQuestState(q);
			if(qs == null || qs.isNotAccepted())
				return q.checkStartCondition(null, player) == null;
		}
		return false;
	}

	private void startQuest(Player player, QuestState st, int questId, int cond, int giveItemId, ItemInstance deleteitem)
	{
		if(st == null)
		{
			Quest quest = QuestHolder.getInstance().getQuest(questId);
			QuestState qs = quest.newQuestState(player);
			qs.setCond(cond);
		}
		else
			st.getPlayer().getQuestState(questId).setCond(cond);

		if(giveItemId > 0)
		{
			if(!st.haveQuestItem(giveItemId))
				st.giveItems(giveItemId, 1, false);
		}

		ItemFunctions.deleteItem(player, deleteitem, 1, true);
	}

}