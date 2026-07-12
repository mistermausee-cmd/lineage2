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
package quests.Q10462_TemperARustingBlade;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerAugment;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.State;

/**
 * Temper a Rusting Blade (10462) TODO Update to Helios cronicle. Quest start Iv 85
 * @URL https://l2wiki.com/Temper_a_Rusting_Blade
 * @author Gigi
 */
public class Q10462_TemperARustingBlade extends Quest
{
	// NPCs
	private static final int FLUTTER = 30677;
	
	// quest_items
	private static final int PRACTICE_WEAPON = 36717;
	private static final int PRACTICE_LIFE_STONE = 36718;
	private static final int PRACTICE_LIFE_GEMSTONE = 36719;
	
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MAX_LEVEL = 105;
	
	public Q10462_TemperARustingBlade()
	{
		super(10462);
		addStartNpc(FLUTTER);
		addTalkId(FLUTTER);
		registerQuestItems(PRACTICE_WEAPON, PRACTICE_LIFE_STONE, PRACTICE_LIFE_GEMSTONE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30677-00.htm");
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30677-02.htm":
			case "30677-03.htm":
			case "30677-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30677-05.htm":
			{
				qs.startQuest();
				giveItems(player, PRACTICE_WEAPON, 1);
				giveItems(player, PRACTICE_LIFE_STONE, 1);
				giveItems(player, PRACTICE_LIFE_GEMSTONE, 25);
				htmltext = event;
				break;
			}
			case "30677-08.html":
			{
				addExpAndSp(player, 504210, 121);
				qs.exitQuest(false, true);
				htmltext = event;
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30677-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30677-06.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "30677-07.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_AUGMENT)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(PRACTICE_WEAPON)
	public void onItemAugment(OnPlayerAugment event)
	{
		final Player player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		final Item item = qs.getPlayer().getInventory().getItemByItemId(PRACTICE_WEAPON);
		if ((item != null) && qs.isCond(1) && item.isAugmented())
		{
			qs.setCond(2, true);
		}
	}
}
