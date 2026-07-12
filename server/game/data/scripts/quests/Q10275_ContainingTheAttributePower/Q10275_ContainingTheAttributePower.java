/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package quests.Q10275_ContainingTheAttributePower;

import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.AttributeType;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestSound;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.util.MathUtil;

/**
 * Containing the Attribute Power (10275)
 * @author nonom
 */
public class Q10275_ContainingTheAttributePower extends Quest
{
	// NPCs
	private static final int HOLLY = 30839;
	private static final int WEBER = 31307;
	private static final int YIN = 32325;
	private static final int YANG = 32326;
	private static final int WATER = 27380;
	private static final int AIR = 27381;
	
	// Items
	private static final int YINSWORD = 13845;
	private static final int YANGSWORD = 13881;
	private static final int SOULPIECEWATER = 13861;
	private static final int SOULPIECEAIR = 13862;
	
	// Skills
	private static final SkillHolder BLESSING_OF_FIRE = new SkillHolder(2635, 1);
	private static final SkillHolder BLESSING_OF_EARTH = new SkillHolder(2636, 1);
	
	public Q10275_ContainingTheAttributePower()
	{
		super(10275);
		addStartNpc(HOLLY, WEBER);
		addTalkId(HOLLY, WEBER, YIN, YANG);
		addKillId(AIR, WATER);
		registerQuestItems(YINSWORD, YANGSWORD, SOULPIECEWATER, SOULPIECEAIR);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30839-02.html":
			case "31307-02.html":
			{
				qs.startQuest();
				break;
			}
			case "30839-05.html":
			{
				qs.setCond(2, true);
				break;
			}
			case "31307-05.html":
			{
				qs.setCond(7, true);
				break;
			}
			case "32325-03.html":
			{
				qs.setCond(3, true);
				giveItems(player, YINSWORD, 1, AttributeType.FIRE, 10);
				break;
			}
			case "32326-03.html":
			{
				qs.setCond(8, true);
				giveItems(player, YANGSWORD, 1, AttributeType.EARTH, 10);
				break;
			}
			case "32325-06.html":
			{
				if (hasQuestItems(player, YINSWORD))
				{
					takeItems(player, YINSWORD, 1);
					htmltext = "32325-07.html";
				}
				
				giveItems(player, YINSWORD, 1, AttributeType.FIRE, 10);
				break;
			}
			case "32326-06.html":
			{
				if (hasQuestItems(player, YANGSWORD))
				{
					takeItems(player, YANGSWORD, 1);
					htmltext = "32326-07.html";
				}
				
				giveItems(player, YANGSWORD, 1, AttributeType.EARTH, 10);
				break;
			}
			case "32325-09.html":
			{
				qs.setCond(5, true);
				BLESSING_OF_FIRE.getSkill().applyEffects(player, player);
				giveItems(player, YINSWORD, 1, AttributeType.FIRE, 10);
				break;
			}
			case "32326-09.html":
			{
				qs.setCond(10, true);
				BLESSING_OF_EARTH.getSkill().applyEffects(player, player);
				giveItems(player, YANGSWORD, 1, AttributeType.EARTH, 10);
				break;
			}
		}
		
		if (StringUtil.isNumeric(event))
		{
			htmltext = npc.getId() + "-1" + event + ".html";
			giveItems(player, 10520 + MathUtil.clamp(Integer.parseInt(event), 0, 6), 2);
			addExpAndSp(player, 202160, 20375);
			qs.exitQuest(false, true);
		}
		
		return htmltext;
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return;
		}
		
		switch (npc.getId())
		{
			case AIR:
			{
				if ((qs.isCond(8) || qs.isCond(10)) && (getItemEquipped(player, Inventory.PAPERDOLL_RHAND) == YANGSWORD) && (getQuestItemsCount(player, SOULPIECEAIR) < 6) && (getRandom(100) < 30))
				{
					giveItems(player, SOULPIECEAIR, 1);
					if (getQuestItemsCount(player, SOULPIECEAIR) >= 6)
					{
						qs.setCond(qs.getCond() + 1, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case WATER:
			{
				if (((qs.getCond() >= 3) || (qs.getCond() <= 5)) && (getItemEquipped(player, Inventory.PAPERDOLL_RHAND) == YINSWORD) && (getQuestItemsCount(player, SOULPIECEWATER) < 6) && (getRandom(100) < 30))
				{
					giveItems(player, SOULPIECEWATER, 1);
					if (getQuestItemsCount(player, SOULPIECEWATER) >= 6)
					{
						qs.setCond(qs.getCond() + 1, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
		}
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (npc.getId())
		{
			case HOLLY:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = (player.getLevel() > 75) ? "30839-01.htm" : "30839-00.html";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "30839-03.html";
								break;
							}
							case 2:
							{
								htmltext = "30839-05.html";
								break;
							}
						}
						break;
					}
					case State.COMPLETED:
					{
						htmltext = "30839-0a.html";
						break;
					}
				}
				break;
			}
			case WEBER:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = (player.getLevel() > 75) ? "31307-01.htm" : "31307-00.html";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "31307-03.html";
								break;
							}
							case 7:
							{
								htmltext = "31307-05.html";
								break;
							}
						}
						break;
					}
					case State.COMPLETED:
					{
						htmltext = "31307-0a.html";
						break;
					}
				}
				break;
			}
			case YIN:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 2:
						{
							htmltext = "32325-01.html";
							break;
						}
						case 3:
						case 5:
						{
							htmltext = "32325-04.html";
							break;
						}
						case 4:
						{
							htmltext = "32325-08.html";
							takeItems(player, YINSWORD, 1);
							takeItems(player, SOULPIECEWATER, -1);
							break;
						}
						case 6:
						{
							htmltext = "32325-10.html";
							break;
						}
					}
				}
				break;
			}
			case YANG:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 7:
						{
							htmltext = "32326-01.html";
							break;
						}
						case 8:
						case 10:
						{
							htmltext = "32326-04.html";
							break;
						}
						case 9:
						{
							htmltext = "32326-08.html";
							takeItems(player, YANGSWORD, 1);
							takeItems(player, SOULPIECEAIR, -1);
							break;
						}
						case 11:
						{
							htmltext = "32326-10.html";
							break;
						}
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
}
