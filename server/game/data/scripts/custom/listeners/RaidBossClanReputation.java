/*
 * Custom (solo server): grants Clan Reputation Points when a clan member kills
 * a raid boss. The amount scales with the raid boss level.
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
 * Raid Boss -> Clan Reputation.<br>
 * When a member of a clan lands the killing blow on a raid boss (or grand boss),
 * the clan receives Clan Reputation Points equal to (boss level * REPUTATION_PER_LEVEL).<br>
 * Raid minions are ignored.
 */
public class RaidBossClanReputation extends Script
{
	// Reputation granted per raid boss level. Example: level 99 boss -> 990 points.
	private static final int REPUTATION_PER_LEVEL = 10;

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

		final Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}

		final int reputation = Math.max(1, target.getLevel() * REPUTATION_PER_LEVEL);
		clan.addReputationScore(reputation);
		player.sendMessage("Клан получил " + reputation + " Очков Славы за победу над рейд-боссом (ур. " + target.getLevel() + ").");
	}

	public static void main(String[] args)
	{
		new RaidBossClanReputation();
	}
}
