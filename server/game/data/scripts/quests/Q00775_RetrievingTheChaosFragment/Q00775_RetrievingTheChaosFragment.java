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
package quests.Q00775_RetrievingTheChaosFragment;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Faction;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.QuestType;
import org.l2jmobius.gameserver.model.script.State;

import quests.Q10455_ElikiasLetter.Q10455_ElikiasLetter;

/**
 * Retrieving the Fragment of Chaos (775)
 * @URL https://l2wiki.com/Retrieving_the_Fragment_of_Chaos
 * @author Gigi
 */
public class Q00775_RetrievingTheChaosFragment extends Quest
{
	// NPCs
	private static final int LEONA_BLACKBIRD = 31595;
	
	// Monsters
	private static final int[] MONSTERS =
	{
		23388, // Kandiloth
		23387, // Kanzaroth
		23385, // Lunatikan
		23384, // Smaug
		23386, // Jabberwok
		23395, // Garion
		23397, // Desert Wendigo
		23399, // Bend Beetle
		23398, // Koraza
		23395, // Garion
		23396, // Garion Neti
		23357, // Disorder Warrior
		23356, // Klien Soldier
		23361, // Mutated Fly
		23358, // Blow Archer
		23355, // Armor Beast
		23360, // Bizuard
		23354, // Dacey Hannibal
		23357, // Disorder Warrior
		23363, // Amos Officer
		23364, // Amos Master
		23362, // Amos Soldier
		23365, // Ailith Hunter
	};
	
	// Misc
	private static final int MIN_LEVEL = 99;
	
	// Item
	private static final int CHAOS_FRAGMENT = 37766;
	private static final int BASIC_SUPPLY_BOX = 47172;
	private static final int INTERMEDIATE_SUPPLY_BOX = 47173;
	private static final int ADVANCED_SUPPLY_BOX = 47174;
	
	public Q00775_RetrievingTheChaosFragment()
	{
		super(775);
		addStartNpc(LEONA_BLACKBIRD);
		addTalkId(LEONA_BLACKBIRD);
		addKillId(MONSTERS);
		registerQuestItems(CHAOS_FRAGMENT);
		addCondMinLevel(MIN_LEVEL, "31595-00.htm");
		addCondCompletedQuest(Q10455_ElikiasLetter.class.getSimpleName(), "31595-00.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "31595-05.html":
			{
				htmltext = event;
				break;
			}
			case "31595-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "31595-03.html":
			{
				if (qs.isCond(2))
				{
					final int factionLevel = player.getFactionLevel(Faction.BLACKBIRD_CLAN);
					if (factionLevel == 0)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 100);
						giveItems(player, BASIC_SUPPLY_BOX, 1);
						addExpAndSp(player, 4522369500L, 10853640);
					}
					else if (factionLevel == 1)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 200);
						giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
						addExpAndSp(player, 9044739000L, 21707280);
					}
					else if (factionLevel > 1)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 300);
						giveItems(player, ADVANCED_SUPPLY_BOX, 1);
						addExpAndSp(player, 13567108500L, 32560920);
					}
					
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (npc.getId() == LEONA_BLACKBIRD)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "31595-08.html";
						break;
					}
					
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "31595-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "31595-07.html";
					}
					else if (qs.isCond(2))
					{
						if (getQuestItemsCount(player, CHAOS_FRAGMENT) < 200)
						{
							htmltext = "31595-02.html";
						}
						else
						{
							htmltext = "31595-09.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "31595-08.html";
		}
		
		return htmltext;
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isCond(1))
		{
			final int factionLevel = killer.getFactionLevel(Faction.BLACKBIRD_CLAN);
			if (factionLevel == 0)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 300)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel == 1)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 600)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel > 1)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 900)
				{
					qs.setCond(2, true);
				}
			}
		}
	}
}
