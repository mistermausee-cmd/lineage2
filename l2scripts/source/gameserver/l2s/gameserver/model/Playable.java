package l2s.gameserver.model;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.Skill.SkillTargetType;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.basestats.PlayableBaseStats;
import l2s.gameserver.model.actor.flags.PlayableFlags;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.entity.events.impl.SingleMatchEvent;
import l2s.gameserver.model.instances.ChairInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.RelationChangedPacket;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.item.EtcItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.utils.Location;

public abstract class Playable extends Creature
{
	private boolean _isPendingRevive;

	
	protected final ReadWriteLock questLock = new ReentrantReadWriteLock();
	protected final Lock questRead = questLock.readLock();
	protected final Lock questWrite = questLock.writeLock();

	protected final IntObjectMap<TimeStamp> _sharedGroupReuses = new CHashIntObjectMap<TimeStamp>();

	public Playable(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<? extends Playable> getRef()
	{
		return (HardReference<? extends Playable>) super.getRef();
	}

	public abstract Inventory getInventory();

	public abstract long getWearedMask();

	private Boat _boat;
	private Location _inBoatPosition;

	
	@Override
	public boolean checkPvP(final Creature target, Skill skill)
	{
		Player player = getPlayer();

		if(isDead() || target == null || player == null || target == this || target == player || player.isMyServitor(target.getObjectId()) || player.isPK())
			return false;

		if(skill != null)
		{
			if(skill.altUse())
				return false;
			if(skill.getTargetType() == SkillTargetType.TARGET_FEEDABLE_BEAST)
				return false;
			if(skill.getTargetType() == SkillTargetType.TARGET_UNLOCKABLE)
				return false;
			if(skill.getTargetType() == SkillTargetType.TARGET_CHEST)
				return false;
		}

		
		for (SingleMatchEvent event : getEvents(SingleMatchEvent.class))
			if(!event.checkPvPFlag(player, target))
				return false;

		if(isInPeaceZone() && target.isInPeaceZone())
			return false;
		if(isInZoneBattle() && target.isInZoneBattle())
			return false;
		if(isInSiegeZone() && target.isInSiegeZone())
			return false;

		if(skill == null || skill.isOffensive())
		{
			if(target.isPK())
				return false;
			else if(target.isPlayable())
				return true;
		}
		else if(target.getPvpFlag() > 0 || target.isPK() || (target.isMonster() && !skill.isNoFlagNoForce()))
			return true;

		return false;
	}

	
	public boolean checkTarget(Creature target)
	{
		Player player = getPlayer();
		if(player == null)
			return false;

		if(target == null || target.isDead())
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(!isInRange(target, 2000))
		{
			player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		if(target.isDoor() && !target.isAttackable(this))
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(target.paralizeOnAttack(this))
		{
			if(Config.PARALIZE_ON_RAID_DIFF)
			{
				paralizeMe(target);
				return false;
			}
		}

		if(!Config.ALT_USE_TRANSFORM_IN_EPIC_ZONE && isCursedWeaponEquipped() && isInZone(Zone.ZoneType.epic))
		{
		      paralizeMe(target);
		      return false;
		}
		
		if(target.isInvisible(this) || getReflection() != target.getReflection() || !GeoEngine.canSeeTarget(this, target, false))
		{
			player.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			return false;
		}

		
		
		
		
		
		

		if(player.isInZone(ZoneType.epic) != target.isInZone(ZoneType.epic))
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(target.isPlayable())
		{
			
			if(isInZoneBattle() != target.isInZoneBattle())
			{
				player.sendPacket(SystemMsg.INVALID_TARGET);
				return false;
			}

			
			if((isInPeaceZone() || target.isInPeaceZone()))
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
				return false;
			}

			if(player.isInOlympiadMode() && !player.isOlympiadCompStart())
				return false;
		}

		return true;
	}

	@Override
	public void doAttack(Creature target)
	{
		Player player = getPlayer();
		if(player == null)
			return;

		if(isAMuted() || isAttackingNow())
		{
			player.sendActionFailed();
			return;
		}

		if(player.isInObserverMode())
		{
			player.sendActionFailed();
			return;
		}

		if(!checkTarget(target))
		{
			if(!isServitor()) 
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
			player.sendActionFailed();
			return;
		}

		
		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if(duelEvent != null && target.getEvent(DuelEvent.class) != duelEvent)
			duelEvent.abortDuel(getPlayer());

		WeaponTemplate weaponItem = getActiveWeaponTemplate();
		if(weaponItem != null)
		{
			int weaponMpConsume = weaponItem.getMpConsume();
			int[] reducedMPConsume = weaponItem.getReducedMPConsume();
			if(reducedMPConsume[0] > 0 && Rnd.chance(reducedMPConsume[0]))
				weaponMpConsume = reducedMPConsume[1];

			boolean isBowOrCrossbow = weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW || weaponItem.getItemType() == WeaponTemplate.WeaponType.TWOHANDCROSSBOW;
			if(isBowOrCrossbow)
			{
				double cheapShot = calcStat(Stats.CHEAP_SHOT, 0., target, null);
				if(Rnd.chance(cheapShot))
					weaponMpConsume = 0;
			}

			if(weaponMpConsume > 0)
			{
				if(_currentMp < weaponMpConsume)
				{
					getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
					player.sendPacket(SystemMsg.NOT_ENOUGH_MP);
					player.sendActionFailed();
					return;
				}
				reduceCurrentMp(weaponMpConsume, null);
			}

			if(isBowOrCrossbow)
			{
				if(!player.checkAndEquipArrows())
				{
					getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
					player.sendPacket(player.getActiveWeaponInstance().getItemType() == WeaponType.BOW ? SystemMsg.YOU_HAVE_RUN_OUT_OF_ARROWS : SystemMsg.NOT_ENOUGH_BOLTS);
					player.sendActionFailed();
					return;
				}
			}
		}

		super.doAttack(target);
	}

	@Override
	public void doCast(final SkillEntry skillEntry, final Creature target, boolean forceUse)
	{
		if(skillEntry == null)
			return;

		
		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if(duelEvent != null && target.getEvent(DuelEvent.class) != duelEvent)
			duelEvent.abortDuel(getPlayer());

		Skill skill = skillEntry.getTemplate();

		if(skill.getSkillType() == SkillType.DEBUFF && target.isNpc() && target.isInvulnerable() && !target.isMonster())
		{
			getPlayer().sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		super.doCast(skillEntry, target, forceUse);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(attacker == null || isDead() || (attacker.isDead() && !isDot))
			return;

		final boolean damageBlocked = isDamageBlocked(attacker);
		if(damageBlocked && transferDamage)
			return;

		if(damageBlocked && attacker != this)
		{
			if(attacker.isPlayer())
			{
				if(sendGiveMessage)
					attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
			}
			return; 
		}

		if(attacker != this && attacker.isPlayable())
		{
			Player player = getPlayer();
			Player pcAttacker = attacker.getPlayer();
			if(pcAttacker != player)
				if(player.isInOlympiadMode() && !player.isOlympiadCompStart())
				{
					if(sendGiveMessage)
						pcAttacker.sendPacket(SystemMsg.INVALID_TARGET);
					return;
				}

			if(isInZoneBattle() != attacker.isInZoneBattle())
			{
				if(sendGiveMessage)
					attacker.getPlayer().sendPacket(SystemMsg.INVALID_TARGET);
				return;
			}

			DuelEvent duelEvent = getEvent(DuelEvent.class);
			if(duelEvent != null && attacker.getEvent(DuelEvent.class) != duelEvent)
				duelEvent.abortDuel(player);
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return isCtrlAttackable(attacker, true, false);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return isCtrlAttackable(attacker, false, false);
	}

	
	public boolean isCtrlAttackable(Creature attacker, boolean force, boolean nextAttackCheck)
	{
		Player player = getPlayer();
		if(attacker == null || player == null || attacker == this || (attacker == player && !force) || isDead() || attacker.isAlikeDead()) 
			return false;

		if(isInvisible(attacker) || getReflection() != attacker.getReflection())
			return false;

		Boat boat = player.getBoat();
		if(boat != null && !boat.isAirShip())
			return false;

		if(isTransformed() && !getTransform().isNormalAttackable())
			return false;

		Player pcAttacker = attacker.getPlayer();
		if(isPlayer() && pcAttacker == this)
			return false;

		if(pcAttacker != null && pcAttacker != player)
		{
			boat = pcAttacker.getBoat();
			if(boat != null && !boat.isAirShip())
				return false;

			
			
			 
		    if(pcAttacker.getBlockCheckerArena() > -1 || player.getBlockCheckerArena() > -1)
		        return false; 
			if((player.isInOlympiadMode() || pcAttacker.isInOlympiadMode()) && player.getOlympiadGame() != pcAttacker.getOlympiadGame()) 
				return false;
			if(player.isInOlympiadMode() && !player.isOlympiadCompStart()) 
				return false;
			if(player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcAttacker.getOlympiadSide() && !force) 
				return false;
			if(player.isInNonPvpTime())
				return false;

			if (player.isInCtF())
			{
				if(pcAttacker.getTeam() != TeamType.NONE && player.getTeam() == TeamType.NONE)
					return false; 
		        if(player.getTeam() != TeamType.NONE && pcAttacker.getTeam() == TeamType.NONE)
		        	return false; 
		        if(player.getTeam() != TeamType.NONE && pcAttacker.getTeam() != TeamType.NONE && player.getTeam() == pcAttacker.getTeam())
		        	return false; 
			} 
		      
			if(!force && player.getParty() != null && player.getParty() == pcAttacker.getParty())
				return false;

			if(!force && player.isInParty() && player.getParty().getCommandChannel() != null && pcAttacker.isInParty() && pcAttacker.getParty().getCommandChannel() != null && player.getParty().getCommandChannel() == pcAttacker.getParty().getCommandChannel())
				return false;

			for(Event e : attacker.getEvents())
				if(e.checkForAttack(this, attacker, null, force) != null)
					return false;

			if(isInZoneBattle())
				return true;
			if(isInPeaceZone())
				return false;

			for(Event e : attacker.getEvents())
				if (e.canAttack(this, attacker, null, force, nextAttackCheck))
					return true;

		    if ((pcAttacker.isCursedWeaponEquipped() && player.getLevel() < 21) || (player.isCursedWeaponEquipped() && pcAttacker.getLevel() < 21))
		        return false;
		    
			if(!force && player.getClan() != null && player.getClan() == pcAttacker.getClan())
				return false;

			if(!force && player.getClan() != null && player.getClan().getAlliance() != null && pcAttacker.getClan() != null && pcAttacker.getClan().getAlliance() != null && player.getClan().getAlliance() == pcAttacker.getClan().getAlliance())
				return false;

			if(isInSiegeZone())
				return true;

			if(pcAttacker.atMutualWarWith(player))
				return true;

			if(player.isPK())
				return true;

			if(player.getPvpFlag() != 0)
				return !nextAttackCheck;

			return force;
		}

		return true;
	}

	@Override
	public int getKarma()
	{
		Player player = getPlayer();
		return player == null ? 0 : player.getKarma();
	}

	@Override
	public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills, boolean trigger)
	{
		Player player = getPlayer();
		if(player == null)
			return;
		 
	    if(skill.getSkillType().equals(Skill.SkillType.BEAST_FEED))
	    {
	      super.callSkill(skill, targets, useActionSkills, trigger);
	      return;
	    }

		for(Creature target : targets)
		{
			if(target.isNpc())
			{
				if(!trigger && skill.isOffensive())
				{
					
					if(target.paralizeOnAttack(player))
					{
						if(Config.PARALIZE_ON_RAID_DIFF)
						{
							paralizeMe(target);
							return;
						}
					}
				}
				target.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this, target);
			}
			else 
			if(target.isPlayable() && player != target && !player.isMyServitor(target.getObjectId()))
			{
				int aggro = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : Math.max(1, (int) skill.getPower());
				if(skill.isOffensive()) 
					aggro = -aggro;

				List<NpcInstance> npcs = World.getAroundNpc(target);
				for(NpcInstance npc : npcs)
				{
					npc.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this, target);

					if(!trigger && useActionSkills && !skill.altUse() && !npc.isDead() && npc.isInRangeZ(this, 2000))
					{
						if(npc.getAggroList().getHate(target) > 0)
						{
							if(!skill.isHandler() && npc.paralizeOnAttack(player))
							{
								if(Config.PARALIZE_ON_RAID_DIFF)
								{
									Skill revengeSkill = SkillHolder.getInstance().getSkill(4215, 1);
									if(revengeSkill != null)
										revengeSkill.getEffects(npc, this);
								}
								return;
							}
						}

						if(aggro > 0)
						{
							AggroInfo ai = npc.getAggroList().get(target);
							
							if(ai == null)
								continue;

							
							if(ai.hate < 100)
								continue;

							if(GeoEngine.canSeeTarget(npc, target, false)) 
								npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, ai.damage == 0 ? aggro / 2 : aggro);
						}
					}
				}
			}

			
			if(!trigger && checkPvP(target, skill))
				startPvPFlag(target);
		}
		super.callSkill(skill, targets, useActionSkills, trigger);
	}

	
	public void broadcastPickUpMsg(ItemInstance item)
	{
		Player player = getPlayer();

		if(item == null || player == null)
			return;

		if(item.isEquipable() && !(item.getTemplate() instanceof EtcItemTemplate))
		{
			SystemMessage msg = null;
			String player_name = player.getName();
			if(item.getEnchantLevel() > 0)
			{
				int msg_id = isPlayer() ? SystemMessage.ATTENTION_S1_PICKED_UP__S2_S3 : SystemMessage.ATTENTION_S1_PET_PICKED_UP__S2_S3;
				msg = new SystemMessage(msg_id).addString(player_name).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
			}
			else
			{
				int msg_id = isPlayer() ? SystemMessage.ATTENTION_S1_PICKED_UP_S2 : SystemMessage.ATTENTION_S1_PET_PICKED_UP__S2_S3;
				msg = new SystemMessage(msg_id).addString(player_name).addItemName(item.getItemId());
			}
			for(Player target : World.getAroundObservers(this))
			{
				if(!isInvisible(target))
					target.sendPacket(msg);
			}
		}
	}

	public void paralizeMe(Creature effector)
	{
		Skill revengeSkill = SkillHolder.getInstance().getSkill(Skill.SKILL_RAID_CURSE, 1);
		revengeSkill.getEffects(effector, this);
	}

	public final void setPendingRevive(boolean value)
	{
		_isPendingRevive = value;
	}

	public boolean isPendingRevive()
	{
		return _isPendingRevive;
	}

	
	public void doRevive()
	{
		getListeners().onRevive();

		if(!isTeleporting())
		{
			setPendingRevive(false);
			setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
			setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);

			if(isSalvation())
			{
				getAbnormalList().stop(AbnormalType.resurrection_special);
				setCurrentHp(getMaxHp(), true);
				setCurrentMp(getMaxMp());
				setCurrentCp(getMaxCp());
			}
			else
			{
				setCurrentHp(Math.max(1, getMaxHp() * Config.RESPAWN_RESTORE_HP), true);

				if(Config.RESPAWN_RESTORE_MP >= 0)
					setCurrentMp(getMaxMp() * Config.RESPAWN_RESTORE_MP);

				if(isPlayer() && Config.RESPAWN_RESTORE_CP >= 0)
					setCurrentCp(getMaxCp() * Config.RESPAWN_RESTORE_CP);
			}

			broadcastPacket(new RevivePacket(this));
			
		}
		else
			setPendingRevive(true);
	}

	public abstract void doPickupItem(GameObject object);

	public void sitDown(ChairInstance chair)
	{}

	public void standUp()
	{}

	private long _nonAggroTime;

	public boolean isInNonAggroTime()
	{
		return _nonAggroTime > System.currentTimeMillis();
	}

	public void setNonAggroTime(long time)
	{
		_nonAggroTime = time;
	}
	
	private long _nonPvpTime;
	
	public boolean isInNonPvpTime()
	{
		return _nonPvpTime > System.currentTimeMillis();
	}

	public void setNonPvpTime(long time)
	{
		_nonPvpTime = time;
	}

	
	public boolean isSilentMoving()
	{
		return getFlags().getSilentMoving().get();
	}

	public int getMaxLoad()
	{
		return 0;
	}

	public int getInventoryLimit()
	{
		return 0;
	}

	@Override
	public boolean isPlayable()
	{
		return true;
	}

	public boolean isSharedGroupDisabled(int groupId)
	{
		TimeStamp sts = _sharedGroupReuses.get(groupId);
		if(sts == null)
			return false;
		if(sts.hasNotPassed())
			return true;
		_sharedGroupReuses.remove(groupId);
		return false;
	}

	public TimeStamp getSharedGroupReuse(int groupId)
	{
		return _sharedGroupReuses.get(groupId);
	}

	public void addSharedGroupReuse(int group, TimeStamp stamp)
	{
		_sharedGroupReuses.put(group, stamp);
	}

	public Collection<IntObjectPair<TimeStamp>> getSharedGroupReuses()
	{
		return _sharedGroupReuses.entrySet();
	}

	public boolean useItem(ItemInstance item, boolean ctrlPressed, boolean force)
	{
		return false;
	}

	public int getCurrentLoad()
	{
		return 0;
	}

	public int getWeightPenalty()
	{
		return 0;
	}

	@Override
	public boolean isInBoat()
	{
		return _boat != null;
	}

	@Override
	public boolean isInShuttle()
	{
		return _boat != null && _boat.isShuttle();
	}

	public Boat getBoat()
	{
		return _boat;
	}

	public void setBoat(Boat boat)
	{
		_boat = boat;
	}

	public Location getInBoatPosition()
	{
		return _inBoatPosition;
	}

	public void setInBoatPosition(Location loc)
	{
		_inBoatPosition = loc;
	}
	
	public int getNameColor()
	{
		return 0;
	}

	@Override
	public PlayableBaseStats getBaseStats()
	{
		if(_baseStats == null)
			_baseStats = new PlayableBaseStats(this);
		return (PlayableBaseStats) _baseStats;
	}

	@Override
	public PlayableFlags getFlags()
	{
		if(_statuses == null)
			_statuses = new PlayableFlags(this);
		return (PlayableFlags) _statuses;
	}

	public abstract SkillEntry getAdditionalSSEffect(boolean spiritshot, boolean blessed);
	
	public int getRelation(Player target)
	{
		int result = 0;

		if(getClan() != null)
		{
			result |= RelationChangedPacket.RELATION_CLAN_MEMBER;
			if(getClan() == target.getClan())
				result |= RelationChangedPacket.RELATION_CLAN_MATE;
			if(getClan().getAllyId() != 0)
				result |= RelationChangedPacket.RELATION_ALLY_MEMBER;
		}
		if(getPlayer().isClanLeader())
			result |= RelationChangedPacket.RELATION_LEADER;

		Party party = getPlayer().getParty();
		if(party != null && party == target.getParty())
		{
			result |= RelationChangedPacket.RELATION_HAS_PARTY;

			switch(party.getPartyMembers().indexOf(this))
			{
				case 0:
					result |= RelationChangedPacket.RELATION_PARTYLEADER; 
					break;
				case 1:
					result |= RelationChangedPacket.RELATION_PARTY4; 
					break;
				case 2:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 3:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2; 
					break;
				case 4:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 5:
					result |= RelationChangedPacket.RELATION_PARTY3; 
					break;
				case 6:
					result |= RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 7:
					result |= RelationChangedPacket.RELATION_PARTY2; 
					break;
				case 8:
					result |= RelationChangedPacket.RELATION_PARTY1; 
					break;
			}
		}

		Clan clan1 = getClan();
		Clan clan2 = target.getClan();
		if(clan1 != null && clan2 != null)
		{
			if((target.getPledgeType() != Clan.SUBUNIT_ACADEMY || target.getLevel() >= 70) && (getPlayer().getPledgeType() != Clan.SUBUNIT_ACADEMY || getLevel() >= 70))
			{
				if(clan2.isAtWarWith(clan1.getClanId()))
				{
					result |= RelationChangedPacket.RELATION_1SIDED_WAR;
					if(clan1.isAtWarWith(clan2.getClanId()))
						result |= RelationChangedPacket.RELATION_MUTUAL_WAR;
				}
			}

			if(getPlayer().getBlockCheckerArena() != -1)
			{
				result |= RelationChangedPacket.RELATION_IN_SIEGE;
		        HandysBlockCheckerManager.ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getPlayer().getBlockCheckerArena());
		        if(holder.getPlayerTeam(getPlayer()) == 0)
		          result |= RelationChangedPacket.RELATION_ENEMY;
		        else
		          result |= RelationChangedPacket.RELATION_ALLY;
		         
		        result |= RelationChangedPacket.RELATION_ATTACKER;
			} 
		}
		for(Event e : getEvents())
			result = e.getRelation(getPlayer(), target, result);

		return result;
	}
}