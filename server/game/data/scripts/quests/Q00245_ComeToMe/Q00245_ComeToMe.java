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
package quests.Q00245_ComeToMe;

import java.util.Collection;

import org.l2jmobius.gameserver.managers.MentorManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.Mentee;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestSound;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.QuestType;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.util.ArrayUtil;
import org.l2jmobius.gameserver.util.LocationUtil;

/**
 * Come To Me (245)
 * @URL https://l2wiki.com/Come_to_Me
 * @author Gigi
 * @date 2017-08-18 - [13:01:14]
 */
public class Q00245_ComeToMe extends Quest
{
	// NPC
	private static final int FERRIS = 30847;
	
	// Monsters
	private static final int[] BLAZING_MOBS_1 =
	{
		21110, // Swamp Predator
		21111 // Lava Wyrm
	};
	private static final int[] BLAZING_MOBS_2 =
	{
		21112, // Hames Orc Foot Soldier
		21113, // Hames Orc Sniper
		21115, // Hames Orc Shaman
		21116 // Hames Orc Prefect
	};
	
	// Items
	private static final int FLAME_ASHES = 30322;
	private static final int CRYSTALS_OF_EXPERIENCE = 30323;
	private static final int CRYSTAL_A = 1461;
	private static final int MENTOR_RING = 30383;
	private static final int ACADEMY_DYE_STR = 47205;
	private static final int ACADEMY_DYE_WIT = 47210;
	
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	
	public Q00245_ComeToMe()
	{
		super(245);
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		addKillId(BLAZING_MOBS_1);
		addKillId(BLAZING_MOBS_2);
		addFirstTalkId(FERRIS);
		registerQuestItems(FLAME_ASHES, CRYSTALS_OF_EXPERIENCE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30847-02.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (player.isMentor() && event.equals("30847-13.html"))
		{
			final Player mentee = getCurrentMentee(player);
			if (mentee != null)
			{
				if (player.destroyItemByItemId(ItemProcessType.QUEST, CRYSTAL_A, 100, npc, true))
				{
					mentee.getQuestState(getName()).setCond(3, true);
					return event;
				}
				
				return "30847-14.html";
			}
			
			return "30847-12.html";
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return event;
		}
		else if (event.equals("30847-04.htm"))
		{
			qs.startQuest();
		}
		else if (event.equals("30847-07.htm"))
		{
			qs.set("talk", "1");
			takeItems(player, FLAME_ASHES, -1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (npc.getId() == FERRIS)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					if (player.isMentee() && player.isAcademyMember())
					{
						htmltext = "30847-01.htm";
					}
					else
					{
						htmltext = "30847-02.htm";
					}
					break;
				}
				case State.STARTED:
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30847-05.html";
							break;
						}
						case 2:
						{
							if (!qs.isSet("talk"))
							{
								htmltext = "30847-06.html";
							}
							else if (player.isMentee())
							{
								final Player mentor = MentorManager.getInstance().getMentor(player.getObjectId()).getPlayer();
								if ((mentor != null) && mentor.isOnline() && LocationUtil.checkIfInRange(200, npc, mentor, true))
								{
									htmltext = "30847-10.html";
								}
								else
								{
									htmltext = "30847-08.html";
								}
							}
							else
							{
								htmltext = "30847-09.html";
							}
							break;
						}
						case 3:
						{
							qs.setCond(4, true);
							htmltext = "30847-17.html";
							break;
						}
						case 4:
						{
							htmltext = "30847-18.html";
							break;
						}
						case 5:
						{
							if (player.getLevel() >= MIN_LEVEL)
							{
								if (player.isAcademyMember())
								{
									player.getClan().addReputationScore(500);
								}
								
								addExpAndSp(player, 2_018_733, 484);
								giveItems(player, MENTOR_RING, 1);
								giveItems(player, getRandom(ACADEMY_DYE_STR, ACADEMY_DYE_WIT), 10);
								htmltext = "30847-19.html";
							}
							else
							{
								htmltext = getNoQuestLevelRewardMsg(player);
							}
							
							qs.exitQuest(QuestType.ONE_TIME, true);
							break;
						}
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = "30847-03.htm";
					break;
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, true);
		if ((npc == null) || (qs == null))
		{
			return;
		}
		
		if (qs.getCond() == 1)
		{
			if (ArrayUtil.contains(BLAZING_MOBS_1, npc.getId()) && (getRandom(100) < 50))
			{
				giveItems(killer, FLAME_ASHES, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				if (getQuestItemsCount(killer, FLAME_ASHES) >= 15)
				{
					qs.setCond(2, true);
				}
			}
		}
		else if (qs.getCond() == 4)
		{
			if (ArrayUtil.contains(BLAZING_MOBS_2, npc.getId()))
			{
				if (killer.isMentee())
				{
					final Player mentor = MentorManager.getInstance().getMentor(killer.getObjectId()).getPlayer();
					if ((mentor != null) && LocationUtil.checkIfInRange(500, killer, mentor, false))
					{
						giveItems(killer, CRYSTALS_OF_EXPERIENCE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (getQuestItemsCount(killer, CRYSTALS_OF_EXPERIENCE) >= 12)
						{
							qs.setCond(5, true);
						}
					}
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (player.isMentor() && (npc.getId() == FERRIS))
		{
			final Player mentee = getCurrentMentee(player);
			if (mentee != null)
			{
				return "30847-11.html";
			}
		}
		
		npc.showChatWindow(player);
		return null;
	}
	
	private Player getCurrentMentee(Player mentor)
	{
		Player mentee = null;
		final Collection<Mentee> mentees = MentorManager.getInstance().getMentees(mentor.getObjectId());
		for (Mentee pl : mentees)
		{
			if (pl.isOnline() && LocationUtil.checkIfInRange(400, mentor, pl.getPlayer(), false))
			{
				final QuestState qs = getQuestState(pl.getPlayer(), true);
				if ((qs != null) && (qs.getCond() == 2))
				{
					mentee = pl.getPlayer();
				}
			}
		}
		
		return mentee;
	}
}
