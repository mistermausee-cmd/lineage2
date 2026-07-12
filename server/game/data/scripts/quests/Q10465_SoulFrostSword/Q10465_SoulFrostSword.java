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
package quests.Q10465_SoulFrostSword;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.Race;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * Soul Frost Sword (10465)
 * @URL http://l2on.net/?c=quests&id=10465
 * @author Gigi
 */
public class Q10465_SoulFrostSword extends Quest
{
	// NPC
	private static final int RUPIO = 30471;
	
	// Items
	private static final int PRACTICE_STORMBRINGER = 46629;
	private static final int PRACTICE_SOUL_CRYSTAL = 46526;
	
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MAX_LEVEL = 105;
	
	public Q10465_SoulFrostSword()
	{
		super(10465);
		addStartNpc(RUPIO);
		addTalkId(RUPIO);
		addCondNotRace(Race.ERTHEIA, "30471-00.html");
		registerQuestItems(PRACTICE_STORMBRINGER, PRACTICE_SOUL_CRYSTAL);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "noLevel.html");
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
			case "30471-02.htm":
			case "30471-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30471-04.htm":
			{
				qs.startQuest();
				giveItems(player, PRACTICE_STORMBRINGER, 1);
				giveItems(player, PRACTICE_SOUL_CRYSTAL, 1);
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_028_ensoul_01.htm", TutorialShowHtml.LARGE_WINDOW));
				htmltext = event;
				break;
			}
			case "30471-06.html":
			{
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_028_ensoul_01.htm", TutorialShowHtml.LARGE_WINDOW));
				htmltext = event;
				break;
			}
			case "30471-08.html":
			{
				giveAdena(player, 700000, true);
				addExpAndSp(player, 336000, 403);
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
		final Item wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		String htmltext = getNoQuestMsg(player);
		if ((qs == null) || (player.getActiveWeaponInstance() == null))
		{
			return htmltext;
		}
		
		if (qs.isCreated())
		{
			htmltext = "30471-01.htm";
		}
		else if ((qs.isCond(1)) && (wpn.getId() != PRACTICE_STORMBRINGER))
		{
			htmltext = "Weapon.html";
		}
		else if (qs.isCond(1))
		{
			if (!hasQuestItems(player, PRACTICE_SOUL_CRYSTAL) && (wpn.getId() == PRACTICE_STORMBRINGER))
			{
				htmltext = "30471-07.html";
			}
			else
			{
				htmltext = "30471-05.html";
			}
		}
		
		if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		
		return htmltext;
	}
}
