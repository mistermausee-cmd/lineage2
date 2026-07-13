package l2s.gameserver.model.actor.instances.player;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;

public class DeathPenalty
{
	private static final int PENALTY_SKILL_ID = 14571;
	private static final int MAX_PENALTY_LVL = 5;
	private static final int FORTUNE_OF_NOBLESSE_SKILL_ID = 1325;
	private static final int CHARM_OF_LUCK_SKILL_ID = 2168;
	private static final int[] RAID_BOSSES_IDs = { 25779, 
			25867, 
			25868, 
			
			
			25532, 
			25714, 
			25797, 
			25799, 
			25800, 
			25837, 
			25840, 
			25843, 
			25844, 
			25845, 
			25841, 
			25838, 
			25839, 
			25846, 
			25824, 
			25825, 
			25855, 
			25876, 
			25856, 
			25877, 
			29191, 
			29193, 
			29194, 
			29209, 
			29211, 
			29212, 
			29195, 
			29196, 
			29218, 
			29099, 
			29103, 
			29186, 
			29213, 
			29068, 
	};

	private HardReference<Player> _playerRef;
	private boolean _hasCharmOfLuck;

	public DeathPenalty(Player player)
	{
		_playerRef = player.getRef();
	}

	public Player getPlayer()
	{
		return _playerRef.get();
	}

	public int getLevel()
	{
		if(!Config.ALLOW_DEATH_PENALTY)
			return 0;

		Player player = getPlayer();
		if(player == null)
			return 0;

		for(Abnormal e : player.getAbnormalList())
		{
			if(e.getSkill().getId() == PENALTY_SKILL_ID)
				return e.getSkill().getLevel();
		}

		return 0;
	}

	public void notifyDead(Creature killer)
	{
		if(!Config.ALLOW_DEATH_PENALTY)
			return;

		if(_hasCharmOfLuck)
		{
			_hasCharmOfLuck = false;
			return;
		}

		if(killer == null || !killer.isNpc())
			return;

		Player player = getPlayer();
		if(player == null || player.getLevel() <= 9 || player.isFakePlayer())
			return;

		double chance = 0;
		NpcInstance npc = (NpcInstance) killer;
		if(ArrayUtils.contains(RAID_BOSSES_IDs, npc.getNpcId()))
			chance = 100.;
		else
		{
			int karmaBonus = player.getKarma() / Config.ALT_DEATH_PENALTY_KARMA_PENALTY;
			if(karmaBonus < 0)
				karmaBonus = 0;

			chance = Config.ALT_DEATH_PENALTY_CHANCE + karmaBonus;
		}

		if(Rnd.chance(chance))
			addLevel();
	}

	public void addLevel()
	{
		Player player = getPlayer();
		if(player == null || player.isGM() || player.isFakePlayer())
			return;

		int level = getLevel();
		if(level >= MAX_PENALTY_LVL)
			return;

		if(level != 0)
			player.getAbnormalList().stop(PENALTY_SKILL_ID);

		level++;

		Skill skill = SkillHolder.getInstance().getSkill(PENALTY_SKILL_ID, level);
		if(skill != null)
		{
			double durationMod = 1.0D;
			if(player.getAbnormalList().contains(22410) || player.getAbnormalList().contains(22411))
		        durationMod = 0.1D;
			skill.getEffects(player, player, 0, durationMod);
			player.sendPacket(new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(level));
		}
		else
			player.sendPacket(SystemMsg.YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED);
	}

	public void reduceLevel()
	{
		Player player = getPlayer();
		if(player == null)
			return;

		int level = getLevel();
		if(level <= 0)
			return;

		player.getAbnormalList().stop(PENALTY_SKILL_ID);

		level--;

		Skill skill = SkillHolder.getInstance().getSkill(PENALTY_SKILL_ID, level);
		if(skill != null && level > 0)
		{
			skill.getEffects(player, player);
			player.sendPacket(new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(level));
		}
		else
			player.sendPacket(SystemMsg.YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED);
	}

	public void checkCharmOfLuck()
	{
		Player player = getPlayer();
		if(player != null)
		{
			for(Abnormal e : player.getAbnormalList())
			{
				if(e.getSkill().getId() == CHARM_OF_LUCK_SKILL_ID || e.getSkill().getId() == FORTUNE_OF_NOBLESSE_SKILL_ID)
				{
					_hasCharmOfLuck = true;
					return;
				}
			}
		}

		_hasCharmOfLuck = false;
	}
}