/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.areas.TalkingIsland;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;

/**
 * Theodore AI.
 * @author Gladicek
 */
public class Theodore extends Script
{
	// NPC
	private static final int THEODORE = 32975;
	// Custom services
	// Exp/farm booster runes with a built-in time limit (auto-expire, no permanent clutter).
	private static final int[] EXP_BOOSTERS =
	{
		23258, // XP Rune III 200% (7-day)
		26414, // XP Rune IV 100% (7-day)
		23987, // Rune of Bountiful Growth +20% (30-day)
		45641, // Rodemai's Rune +7% (15-day)
		22762, // Drop Rate Rune 200% (7-day)
		23873, // Prestige Rune 100% (7-day)
	};
	// Mentor's Guidance: XP/SP +50% (VP_MENTOR_RUNE). Intended for characters below level 85.
	private static final SkillHolder MENTOR_RUNE = new SkillHolder(9233, 1);
	private static final int MENTOR_RUNE_MAX_LEVEL = 85;
	
	private Theodore()
	{
		addSpawnId(THEODORE);
		addFirstTalkId(THEODORE);
		// Remove the mentor rune buff once the character reaches the cap level (it is a "+50% XP up to Lv. 85" bonus).
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LEVEL_CHANGED, (OnPlayerLevelChanged event) -> onLevelChanged(event), this));
	}
	
	private void onLevelChanged(OnPlayerLevelChanged event)
	{
		final Player player = event.getPlayer();
		if ((player == null) || (player.getLevel() < MENTOR_RUNE_MAX_LEVEL))
		{
			return;
		}
		if (player.isAffectedBySkill(MENTOR_RUNE.getSkillId()))
		{
			player.stopSkillEffects(MENTOR_RUNE.getSkill());
			player.sendMessage("Руна Наставника исчерпала силу: вы достигли 85 уровня.");
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "SPAM_TEXT":
			{
				if (npc != null)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.AND_NOW_YOUR_JOURNEY_BEGINS, 1000);
				}
				return super.onEvent(event, npc, player);
			}
			case "give_boosters":
			{
				if (player == null)
				{
					return null;
				}
				for (int itemId : EXP_BOOSTERS)
				{
					giveItems(player, itemId, 1);
				}
				return "32975-boosters.html";
			}
			case "give_mentor_rune":
			{
				if (player == null)
				{
					return null;
				}
				if (player.getLevel() >= MENTOR_RUNE_MAX_LEVEL)
				{
					return "32975-mentor-no.html";
				}
				MENTOR_RUNE.getSkill().applyEffects(player, player);
				return "32975-mentor.html";
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "32975-menu.html";
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		startQuestTimer("SPAM_TEXT", 12000, npc, null, true);
	}
	
	public static void main(String[] args)
	{
		new Theodore();
	}
}
