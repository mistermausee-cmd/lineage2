/*
 * Custom (solo server): grants reputation when a raid boss is killed.
 *  - Personal Fame to the killer (any player).
 *  - Clan Reputation Points to the killer's clan (if any).
 * Both amounts scale with the raid boss level.
 */
package custom.listeners;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.creature.OnCreatureDeath;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Raid Boss -> Reputation rewards.<br>
 * When a player lands the killing blow on a raid boss (or grand boss):<br>
 * - the player personally gains Fame ("личная репутация") = boss level * FAME_PER_LEVEL;<br>
 * - the player's clan (if any) gains Clan Reputation = boss level * CLAN_REP_PER_LEVEL.<br>
 * Raid minions are ignored.
 */
public class RaidBossClanReputation extends Script
{
	// Clan Reputation Points granted per raid boss level (goes to the clan pool).
	private static final int CLAN_REP_PER_LEVEL = 30;
	// Personal Fame granted per raid boss level (goes to the killer, capped at MaxPersonalFamePoints).
	private static final int FAME_PER_LEVEL = 10;

	private RaidBossClanReputation()
	{
		Containers.Npcs().addListener(new ConsumerEventListener(Containers.Npcs(), EventType.ON_CREATURE_DEATH, (OnCreatureDeath event) -> onNpcDeath(event), this));
	}

	private void onNpcDeath(OnCreatureDeath event)
	{
		final Creature target = event.getTarget();
		// Only real raid bosses / grand bosses, not their minions.
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

		// 1) Personal Fame ("личная репутация") - granted to the killer regardless of clan.
		final int fame = Math.max(1, level * FAME_PER_LEVEL);
		player.setFame(player.getFame() + fame);
		player.sendMessage("Личная Слава +" + fame + " за победу над рейд-боссом (ур. " + level + ").");

		// 2) Clan Reputation ("репутация клана") - granted to the killer's clan, if any.
		final Clan clan = player.getClan();
		if (clan != null)
		{
			final int reputation = Math.max(1, level * CLAN_REP_PER_LEVEL);
			clan.addReputationScore(reputation);
			player.sendMessage("Клан получил " + reputation + " Очков Славы за победу над рейд-боссом (ур. " + level + ").");
		}

		// Refresh the client so the Fame value updates immediately.
		player.broadcastUserInfo();
	}

	public static void main(String[] args)
	{
		new RaidBossClanReputation();
	}
}
