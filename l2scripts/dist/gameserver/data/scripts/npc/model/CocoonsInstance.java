package npc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.AggroList.HateInfo;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Iqman info reworked from godworld.
 */
public final class CocoonsInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	
	private static final int[] little_simpleAttackMobs = { 22863, 22871, 22879, 22887, 22895, 22903 };
	private static final int[] little_nightAttackMobs = { 22864, 22872, 22880, 22888, 22896, 22904 };
	private static final int[] little_itemSkillMobs = { 22865, 22873, 22881, 22889, 22897, 22905 };
	private static final int[] little_skillAttackMobs = { 22866, 22874, 22882, 22890, 22898, 22906 };
	private static final int[] big_simpleAttackMobs = { 22867, 22875, 22883, 22891, 22899, 22907 };
	private static final int[] big_nightAttackMobs = { 22868, 22876, 22884, 22892, 22900, 22908 };
	private static final int[] big_itemSkillMobs = { 22869, 22877, 22885, 22893, 22901, 22909 };
	private static final int[] big_skillAttackMobs = { 22870, 22878, 22886, 22894, 22898, 22910 };
  
	public CocoonsInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!GameTimeController.getInstance().isNowNight())
		{
			if(command.equalsIgnoreCase("request_normal"))
			{
				if(getNpcId() == 32919)
				{
					NpcInstance mob = NpcUtils.spawnSingle(little_simpleAttackMobs[Rnd.get(little_simpleAttackMobs.length)], getLoc());
					doDie(player);
					decayMe();
					mob.getAggroList().addDamageHate(player, 10000, 0);
				}
				else
				{
					for(int i = 0; i < 3; i++)
					{
						NpcInstance mob = NpcUtils.spawnSingle(big_simpleAttackMobs[Rnd.get(big_simpleAttackMobs.length)], getLoc());
						mob.getAggroList().addDamageHate(player, 10000, 0);
					}
					doDie(player);
					decayMe();
				}
			}
			else if(command.equalsIgnoreCase("request_hard"))
			{
				if(getNpcId() == 32919)
				{
					NpcInstance mob = NpcUtils.spawnSingle(big_skillAttackMobs[Rnd.get(big_simpleAttackMobs.length)], getLoc());
					doDie(player);
					decayMe();
					mob.getAggroList().addDamageHate(player, 10000, 0);
				}
				else
				{
					for(int i = 0; i < 3; i++)
					{
						NpcInstance mob = NpcUtils.spawnSingle(big_skillAttackMobs[Rnd.get(big_simpleAttackMobs.length)], getLoc());
						mob.getAggroList().addDamageHate(player, 10000, 0);
					}
					doDie(player);
					decayMe();
				}

			}

		}
		else if(getNpcId() == 32919)
		{
			NpcInstance mob = NpcUtils.spawnSingle(little_nightAttackMobs[Rnd.get(little_nightAttackMobs.length)], getLoc());
			doDie(player);
			decayMe();
			mob.getAggroList().addDamageHate(player, 10000, 0);
		}
		else
		{
			for(int i = 0; i < 3; i++)
			{
				NpcInstance mob = NpcUtils.spawnSingle(big_nightAttackMobs[Rnd.get(big_nightAttackMobs.length)], getLoc());
				mob.getAggroList().addDamageHate(player, 10000, 0);
			}
			doDie(player);
			decayMe();
		}
	}
	
	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		if(!GameTimeController.getInstance().isNowNight())
		{
			if(getNpcId() == 32919)
			{
				if(skill == null)
				{
					NpcInstance mob = NpcUtils.spawnSingle(little_simpleAttackMobs[Rnd.get(little_simpleAttackMobs.length)], getLoc());
					mob.getAggroList().addDamageHate(attacker, 10000, 0);					
					doDie(attacker);
					decayMe();
				}
				else
				{
					NpcInstance mob = NpcUtils.spawnSingle(little_skillAttackMobs[Rnd.get(little_skillAttackMobs.length)], getLoc());
					mob.getAggroList().addDamageHate(attacker, 10000, 0);
					doDie(attacker);
					decayMe();
				}
			}
			else if(skill == null)
			{
				for(int i = 0; i < 3; i++)
				{
					NpcInstance mob = NpcUtils.spawnSingle(big_simpleAttackMobs[Rnd.get(big_simpleAttackMobs.length)], getLoc());
					mob.getAggroList().addDamageHate(attacker, 10000, 0);
				}
				doDie(attacker);
				decayMe();
			}
			else
			{
				for(int i = 0; i < 3; i++)
				{
					NpcInstance mob = NpcUtils.spawnSingle(big_skillAttackMobs[Rnd.get(big_skillAttackMobs.length)], getLoc());
					mob.getAggroList().addDamageHate(attacker, 10000, 0);
				}
				doDie(attacker);
				decayMe();
			}
		}
		else if(getNpcId() == 32919)
		{
			NpcInstance mob = NpcUtils.spawnSingle(little_nightAttackMobs[Rnd.get(little_nightAttackMobs.length)], getLoc());
			mob.getAggroList().addDamageHate(attacker, 10000, 0);
			doDie(attacker);
			decayMe();
		}
		else
		{
			NpcInstance mob = null;
			for(int i = 0; i < 3; i++)
			{
				mob = NpcUtils.spawnSingle(big_nightAttackMobs[Rnd.get(big_nightAttackMobs.length)], getLoc());
				mob.getAggroList().addDamageHate(attacker, 10000, 0);
			}
			doDie(attacker);
			decayMe();
		}		
	}	
	
	@Override
	protected void onDeath(Creature killer)
	{
		calculateRewards(killer);

		super.onDeath(killer);
	}
	
	public void calculateRewards(Creature lastAttacker)
	{
		Creature topDamager = getAggroList().getTopDamager(lastAttacker);
		if(lastAttacker == null || !lastAttacker.isPlayable())
			lastAttacker = topDamager;

		if(lastAttacker == null || !lastAttacker.isPlayable())
			return;

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

		Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if(quests != null && quests.size() > 0)
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<Player>();
				for(Playable pl : aggroMap.keySet())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if(!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
							players.add(pl.getPlayer());
			}
			else if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<Player>(killer.getParty().getMemberCount());
				for(Player pl : killer.getParty().getPartyMembers())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						players.add(pl);
			}

			for(Quest quest : quests)
			{
				Player toReward = killer;
				if(quest.getPartyType() != Quest.PARTY_NONE && players != null)
					if(isRaid() || quest.getPartyType() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getId());
							if(qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<Player>(players.size());
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getId());
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
								interested.add(pl);
						}

						if(interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
							toReward = killer;
					}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getId());
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
			}
		}

	}
}
