package l2s.gameserver.model.instances;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import l2s.gameserver.model.AggroList.HateInfo;
import l2s.gameserver.model.CommandChannel;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.HeroDiary;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

public class RaidBossInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public RaidBossInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isRaid()
	{
		return true;
	}

	@Override
	public double getRewardRate(Player player)
	{
		return Config.RATE_DROP_ITEMS_RAIDBOSS;
	}

	@Override
	public double getDropChanceMod(Player player)
	{
		return Config.DROP_CHANCE_MODIFIER_RAIDBOSS;
	}

	@Override
	protected void onDeath(Creature killer)
	{
		int points = getTemplate().rewardRp;
	    if(points > 0)
	    	calcRaidPointsReward(points);
	    
		if(this instanceof ReflectionBossInstance)
		{
			super.onDeath(killer);
			return;
		}
		if(killer != null && killer.isPlayable())
		{
			Player player = killer.getPlayer();
			if(player.isInParty())
			{
				for(Player member : player.getParty().getPartyMembers())
					if(member.isNoble())
						Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				player.getParty().broadCast(SystemMsg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
			}
			else
			{
				if(player.isNoble())
					Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				player.sendPacket(SystemMsg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
			}

			Quest q = QuestHolder.getInstance().getQuest(508);
			if(q != null)
			{
				if(player.getClan() != null && player.getClan().getLeader().isOnline())
				{
					QuestState st = player.getClan().getLeader().getPlayer().getQuestState(q);
					if(st != null)
						st.getQuest().onKill(this, st);
				}
			}
		}

		int boxId = 0;
		switch(getNpcId())
		{
			case 25035: 
				boxId = 31027;
				break;
			case 25054: 
				boxId = 31028;
				break;
			case 25126: 
				boxId = 31029;
				break;
			case 25220: 
				boxId = 31030;
				break;
		}

		if(boxId != 0)
		{
			NpcTemplate boxTemplate = NpcHolder.getInstance().getTemplate(boxId);
			if(boxTemplate != null)
			{
				final NpcInstance box = new NpcInstance(IdFactory.getInstance().getNextId(), boxTemplate, StatsSet.EMPTY);
				box.spawnMe(getLoc());
				box.setSpawnedLoc(getLoc());

				box.startDeleteTask(60000);
			}
		}
		
		if(killer != null && killer.getPlayer() != null && Config.RAID_DROP_GLOBAL_ITEMS && getLevel() >= Config.MIN_RAID_LEVEL_TO_DROP)
		{
			for(Config.RaidGlobalDrop drop_inf : Config.RAID_GLOBAL_DROP)
			{
				int id = drop_inf.getId();
				long count = drop_inf.getCount();
				double chance = drop_inf.getChance();
				if(Rnd.chance(chance))
					ItemFunctions.addItem(killer.getPlayer(), id, count, true);
			}
		}

		super.onDeath(killer);
		RaidBossSpawnManager.getInstance().onBossDeath(this);
	}

    private class GroupInfo
    {
        public HashSet<Player> players;
        public double reward;
        
        public GroupInfo()
        {
            players = new HashSet<Player>();
            reward = 0;
        }
    }

	
	private void calcRaidPointsReward(int totalPoints)
	{
		
		Map<Object, GroupInfo> groupsInfo = new HashMap<Object, GroupInfo>();
		double totalDamage = 0;

		
		for(HateInfo ai : getAggroList().getPlayableMap().values())
		{
			Player player = ai.attacker.getPlayer();
			Object key = player.getParty() != null ? player.getParty().getCommandChannel() != null ? player.getParty().getCommandChannel() : player.getParty() : player.getPlayer();
			GroupInfo info = groupsInfo.get(key);
			if(info == null)
			{
				info = new GroupInfo();
				groupsInfo.put(key, info);
			}

			
			
			if(key instanceof CommandChannel)
			{
				for(Player p : ((CommandChannel) key))
				{
					if(p.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						info.players.add(p);
				}
			}
			else if(key instanceof Party)
			{
				for(Player p : ((Party) key).getPartyMembers())
				{
					if(p.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						info.players.add(p);
				}
			}
			else
				info.players.add(player);

			info.reward += ai.damage;
			totalDamage += ai.damage;
		}

		for(GroupInfo groupInfo : groupsInfo.values())
		{
			HashSet<Player> players = groupInfo.players;
			
			int points = (int)(totalPoints * (totalDamage / groupInfo.reward / players.size() / 100));
			for(Player player : players)
			{
				
				int playerReward = (int) Math.round(points * Experience.penaltyModifier(calculateLevelDiffForDrop(player.getLevel()), 9));
				if(playerReward > 0)
					player.addRaidPoints(playerReward, true);
			}
		}
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		addSkill(SkillHolder.getInstance().getSkillEntry(4045, 1)); 
		RaidBossSpawnManager.getInstance().onBossSpawned(this);
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public boolean hasRandomWalk()
	{
		return false;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public void onZoneEnter(Zone zone)
	{
		if(!zone.checkIfInZone(getSpawnedLoc().getX(), getSpawnedLoc().getY(), getSpawnedLoc().getZ()))
		{
			if(zone.getType() == Zone.ZoneType.peace_zone || zone.getType() == Zone.ZoneType.battle_zone || zone.getType() == Zone.ZoneType.SIEGE)
				getAI().returnHomeAndRestore(isRunning());
		}
	}
}