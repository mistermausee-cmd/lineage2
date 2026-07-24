/*
 * Custom (solo server): rewards for killing raid bosses.
 *  - Personal Fame to the killer.
 *  - Clan Reputation Points to the killer's clan (if any).
 *  - Daily "Clan Teamwork" bonus by raid boss kills (2/4/6/8 kills -> stage 1..4), resets daily.
 *  - Clan hunting supplies ("prana") boxes at the same daily milestones.
 */
package custom.listeners;

import java.time.LocalDate;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureDeath;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;

/**
 * Raid Boss reward listener (solo server).
 */
public class RaidBossClanReputation extends Script
{
	// Reward amounts per raid boss level.
	private static final int CLAN_REP_PER_LEVEL = 30;
	private static final int FAME_PER_LEVEL = 10;

	// Daily raid-boss "Clan Teamwork" bonus (replaces the members-online bonus for solo play).
	// 2 kills -> stage 1, 4 -> stage 2, 6 -> stage 3, 8 -> stage 4 (max).
	private static final int[] TEAMWORK_SKILLS =
	{
		23774, // Clan Teamwork - Stage 1
		23775, // Clan Teamwork - Stage 2
		23776, // Clan Teamwork - Stage 3
		23777 // Clan Teamwork - Stage 4
	};
	// Clan hunting supply boxes ("prana") granted at the same milestones (2/4/6/8 kills).
	private static final int[] SUPPLY_BOXES =
	{
		27589, // Supply Box - Standard
		27590, // Supply Box - Mid-grade
		27591, // Supply Box - High-grade
		27663 // Supply Box - Top-grade
	};
	private static final int MAX_STAGE = 4; // reached at 8 kills

	private static final String VAR_DAY = "rbBonusDay";
	private static final String VAR_KILLS = "rbBonusKills";

	private RaidBossClanReputation()
	{
		Containers.Npcs().addListener(new ConsumerEventListener(Containers.Npcs(), EventType.ON_CREATURE_DEATH, (OnCreatureDeath event) -> onNpcDeath(event), this));
	}

	private void onNpcDeath(OnCreatureDeath event)
	{
		final Creature target = event.getTarget();
		if ((target == null) || !target.isRaid() || target.isRaidMinion())
		{
			return;
		}

		final Creature attacker = event.getAttacker();
		if ((attacker == null) || !attacker.isPlayable())
		{
			return;
		}

		final Player player = attacker.asPlayer();
		if (player == null)
		{
			return;
		}

		final int level = target.getLevel();

		// 1) Personal Fame.
		final int fame = Math.max(1, level * FAME_PER_LEVEL);
		player.setFame(player.getFame() + fame);
		player.sendMessage("Личная Слава +" + fame + " за победу над рейд-боссом (ур. " + level + ").");

		// 2) Clan Reputation.
		final Clan clan = player.getClan();
		if (clan != null)
		{
			final int reputation = Math.max(1, level * CLAN_REP_PER_LEVEL);
			clan.addReputationScore(reputation);
			player.sendMessage("Клан получил " + reputation + " Очков Славы за победу над рейд-боссом (ур. " + level + ").");
		}

		// 3) Daily Clan Teamwork bonus + supply boxes.
		handleDailyRaidBonus(player);

		player.broadcastUserInfo();
	}

	private void handleDailyRaidBonus(Player player)
	{
		final PlayerVariables vars = player.getVariables();
		final String today = LocalDate.now().toString();

		// New day -> reset counter and clear the buff.
		if (!today.equals(vars.getString(VAR_DAY, "")))
		{
			vars.set(VAR_DAY, today);
			vars.set(VAR_KILLS, 0);
			clearTeamwork(player);
		}

		final int kills = vars.getInt(VAR_KILLS, 0) + 1;
		vars.set(VAR_KILLS, kills);

		// Give a supply box exactly when hitting an even milestone (2/4/6/8), once per milestone.
		if ((kills <= (MAX_STAGE * 2)) && ((kills % 2) == 0))
		{
			final int idx = (kills / 2) - 1; // 2->0, 4->1, 6->2, 8->3
			player.addItem(ItemProcessType.REWARD, SUPPLY_BOXES[idx], 1, null, true);
		}

		// Apply the Clan Teamwork stage that matches current kills.
		applyTeamworkStage(player, stageForKills(kills));
	}

	private int stageForKills(int kills)
	{
		if (kills >= 8)
		{
			return 4;
		}
		if (kills >= 6)
		{
			return 3;
		}
		if (kills >= 4)
		{
			return 2;
		}
		if (kills >= 2)
		{
			return 1;
		}
		return 0;
	}

	private void applyTeamworkStage(Player player, int stage)
	{
		clearTeamwork(player);
		if (stage >= 1)
		{
			final Skill skill = SkillData.getInstance().getSkill(TEAMWORK_SKILLS[stage - 1], 1);
			if (skill != null)
			{
				player.addSkill(skill, false); // not saved to DB (transient, re-applied on login)
				player.sendMessage("Клановый бонус за рейд-боссов: Взаимодействие Клана - Ступень " + stage + ".");
			}
		}
		player.sendSkillList();
	}

	private void clearTeamwork(Player player)
	{
		for (int id : TEAMWORK_SKILLS)
		{
			final Skill known = player.getKnownSkill(id);
			if (known != null)
			{
				player.removeSkill(known, false);
			}
		}
	}

	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}

		final PlayerVariables vars = player.getVariables();
		final String today = LocalDate.now().toString();
		if (today.equals(vars.getString(VAR_DAY, "")))
		{
			// Same day: restore the buff based on kills done today.
			applyTeamworkStage(player, stageForKills(vars.getInt(VAR_KILLS, 0)));
		}
		else
		{
			// New day: reset.
			vars.set(VAR_DAY, today);
			vars.set(VAR_KILLS, 0);
			clearTeamwork(player);
		}
	}

	public static void main(String[] args)
	{
		new RaidBossClanReputation();
	}
}
