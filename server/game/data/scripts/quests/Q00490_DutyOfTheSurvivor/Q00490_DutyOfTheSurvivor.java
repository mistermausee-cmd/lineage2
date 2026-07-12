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
package quests.Q00490_DutyOfTheSurvivor;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestSound;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.QuestType;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * Duty of the Survivor (400)
 * @author St3eT, Trevor The Third
 */
public class Q00490_DutyOfTheSurvivor extends Quest
{
	// NPCs
	private static final int VOLLODOS = 30137;
	private static final int[] EXTRACT_MONSTERS =
	{
		23174, // Arbitor of Darkness
		23175, // Altar of Evil Spirit Offering Box
		23176, // Mutated Cerberos
		23177, // Dartanion
		23178, // Insane Phion
		23179, // Dimensional Rifter
		23180, // Hellgate Fighting Dog
	};
	private static final int[] BLOOD_MONSTERS =
	{
		23162, // Corpse Devourer
		23163, // Corpse Absorber
		23164, // Corpse Shredder
		23165, // Plagueworm
		23166, // Contaminated Rotten Root
		23167, // Decayed Spore
		23168, // Swamp Tracker
		23169, // Swamp Assassin
		23170, // Swamp Watcher
		23171, // Corpse Collector
		23172, // Delegate of Blood
		23173, // Blood Aide
	};
	
	// Items
	private static final int PUTREFIED_EXTRACT = 34059;
	private static final int ROTTEN_BLOOD = 34060;
	
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int DROP_CHANCE = 70;
	private static final int ITEM_COUNT = 100;
	
	// Rewards
	private static final int EXP_REWARD = 145557000;
	private static final int SP_REWARD = 34933;
	private static final int ADENA_REWARD = 1010124;
	
	public Q00490_DutyOfTheSurvivor()
	{
		super(490);
		addStartNpc(VOLLODOS);
		addTalkId(VOLLODOS);
		addKillId(EXTRACT_MONSTERS);
		addKillId(BLOOD_MONSTERS);
		registerQuestItems(PUTREFIED_EXTRACT, ROTTEN_BLOOD);
		addCondMinLevel(MIN_LEVEL, "30137-09.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30137-02.htm":
			case "30137-03.htm":
			case "30137-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30137-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		if (npc.getId() == VOLLODOS)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30137-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "30137-06.htm";
					}
					else
					{
						giveAdena(player, ADENA_REWARD, true);
						qs.exitQuest(QuestType.DAILY, true);
						if (player.getLevel() >= MIN_LEVEL)
						{
							addExpAndSp(player, EXP_REWARD, SP_REWARD);
						}
						
						htmltext = "30137-07.htm";
					}
					break;
				}
				case State.COMPLETED:
				{
					if (qs.isNowAvailable())
					{
						qs.setState(State.CREATED);
						htmltext = "30137-01.htm";
					}
					else
					{
						htmltext = "30137-08.htm";
					}
					break;
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player member = getRandomPartyMember(player, 1);
		if (member != null)
		{
			final QuestState qs = getQuestState(member, false);
			if (qs.isCond(1) && (getRandom(100) < DROP_CHANCE))
			{
				if ((ArrayUtil.contains(EXTRACT_MONSTERS, npc.getId())) && (getQuestItemsCount(player, PUTREFIED_EXTRACT) < ITEM_COUNT))
				{
					giveItems(player, PUTREFIED_EXTRACT, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else if ((ArrayUtil.contains(BLOOD_MONSTERS, npc.getId())) && (getQuestItemsCount(player, ROTTEN_BLOOD) < ITEM_COUNT))
				{
					giveItems(player, ROTTEN_BLOOD, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else if ((getQuestItemsCount(player, PUTREFIED_EXTRACT) == ITEM_COUNT) && (getQuestItemsCount(player, ROTTEN_BLOOD) == ITEM_COUNT))
				{
					qs.setCond(2);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
			}
		}
	}
}
