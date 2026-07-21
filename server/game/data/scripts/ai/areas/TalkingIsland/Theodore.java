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

import java.util.concurrent.TimeUnit;

import org.l2jmobius.gameserver.config.custom.PremiumSystemConfig;
import org.l2jmobius.gameserver.managers.PremiumManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
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
	// Full set of booster runes from the Alt+B "Exp boosters" section. Handed out with a forced
	// 3-day lifetime (see setRemainingTime), so the original, full-duration runes stay in the shop.
	private static final int[] EXP_BOOSTERS =
	{
		45641, // Rodemai's Rune (XP/SP +7%)
		46250, // Blessed Venezia Rune (XP/SP +11%)
		23987, // Rune of Bountiful Growth (XP/SP +20%)
		46252, // Venezia Rune (XP/SP +22%)
		26414, // XP Rune IV 100%
		23873, // Prestige Rune 100% (drop/spoil/adena)
		23258, // XP Rune III 200%
		22762, // Drop Rate Rune 200%
		36079, // Yum Yum Candy (XP/SP +25%)
		36080, // Nom Nom Candy (XP/SP +25%)
	};
	// Handed-out boosters are limited to 3 days regardless of their template duration.
	private static final long BOOSTER_DURATION_MS = 3L * 24 * 60 * 60 * 1000;
	// Mentor's Guidance: XP/SP +50% (VP_MENTOR_RUNE). Intended for characters below level 85.
	private static final SkillHolder MENTOR_RUNE = new SkillHolder(9233, 1);
	private static final int MENTOR_RUNE_MAX_LEVEL = 85;
	// Mentee Certificate (graduation diploma). Given at level 85+.
	private static final int MENTEE_CERTIFICATE = 33800;
	private static final int CERTIFICATE_MIN_LEVEL = 85;
	// Free premium status handed out by Theodore. Premium is account-wide, so this service is
	// limited to ONCE PER ACCOUNT (account_variables) instead of per-character - otherwise a player
	// could relog alts on the same account to stack premium repeatedly.
	private static final int PREMIUM_DAYS = 3;
	// Per-character flags (persisted in character_variables) so each service can be claimed only once.
	private static final String VAR_BOOSTERS = "THEODORE_BOOSTERS_CLAIMED";
	private static final String VAR_MENTOR_RUNE = "THEODORE_MENTOR_RUNE_CLAIMED";
	private static final String VAR_CERTIFICATE = "THEODORE_CERTIFICATE_CLAIMED";
	// Per-account flag (persisted in account_variables) - premium can be claimed only once per account.
	private static final String VAR_PREMIUM = "THEODORE_PREMIUM_CLAIMED";
	
	private Theodore()
	{
		addSpawnId(THEODORE);
		addFirstTalkId(THEODORE);
		// Remove the mentor rune buff once the character reaches the cap level (it is a "+50% XP up to Lv. 85" bonus).
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LEVEL_CHANGED, (OnPlayerLevelChanged event) -> onLevelChanged(event), this));
		// The mentor rune is applied as a skill effect with infinite duration (abnormalTime = -1). Such
		// self-buffs are NOT persisted to character_skills_save, so they vanish on server restart/relog.
		// Re-apply it on login for characters that claimed it and are still below the cap level.
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
	}
	
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		// Only restore for characters that actually claimed the rune and are still under the cap level.
		if (!player.getVariables().hasVariable(VAR_MENTOR_RUNE) || (player.getLevel() >= MENTOR_RUNE_MAX_LEVEL))
		{
			return;
		}
		if (!player.isAffectedBySkill(MENTOR_RUNE.getSkillId()))
		{
			MENTOR_RUNE.getSkill().applyEffects(player, player);
		}
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
				if (player.getVariables().hasVariable(VAR_BOOSTERS))
				{
					return "32975-once.html";
				}
				for (int itemId : EXP_BOOSTERS)
				{
					final Item rune = player.addItem(ItemProcessType.REWARD, itemId, 1, player, true);
					if (rune != null)
					{
						rune.setRemainingTime(BOOSTER_DURATION_MS); // Limit the handed-out copy to 3 days.
					}
				}
				player.getVariables().set(VAR_BOOSTERS, true);
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
				if (player.getVariables().hasVariable(VAR_MENTOR_RUNE))
				{
					return "32975-once.html";
				}
				MENTOR_RUNE.getSkill().applyEffects(player, player);
				player.getVariables().set(VAR_MENTOR_RUNE, true);
				return "32975-mentor.html";
			}
			case "give_certificate":
			{
				if (player == null)
				{
					return null;
				}
				if (player.getLevel() < CERTIFICATE_MIN_LEVEL)
				{
					return "32975-cert-no.html";
				}
				if (player.getVariables().hasVariable(VAR_CERTIFICATE))
				{
					return "32975-once.html";
				}
				giveItems(player, MENTEE_CERTIFICATE, 1);
				player.getVariables().set(VAR_CERTIFICATE, true);
				return "32975-cert.html";
			}
			case "give_premium":
			{
				if (player == null)
				{
					return null;
				}
				if (!PremiumSystemConfig.PREMIUM_SYSTEM_ENABLED)
				{
					return "32975-premium-off.html";
				}
				// Once per ACCOUNT: premium applies to the whole account, not a single character.
				if (player.getAccountVariables().hasVariable(VAR_PREMIUM))
				{
					return "32975-premium-once.html";
				}
				PremiumManager.getInstance().addPremiumTime(player.getAccountName(), PREMIUM_DAYS, TimeUnit.DAYS);
				player.getAccountVariables().set(VAR_PREMIUM, true);
				player.getAccountVariables().storeMe();
				return "32975-premium.html";
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
