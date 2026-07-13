package ai.tauti;

import gnu.trove.map.TIntObjectMap;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Skill;
import l2s.gameserver.ThreadPoolManager;
import l2s.commons.threading.RunnableImpl;

import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.listener.actor.OnMagicUseListener;
import l2s.gameserver.listener.actor.OnMagicHitListener;

/**
 * @author Rivelia
 */
public class TautiAI extends DefaultAI
{
	private final Skill tauti_push, tauti_rush, tauti_shockwave, tauti_whirlwind, tauti_ultra_whirlwind, tauti_shout_maximum, tauti_ultimate_chain_strike, tauti_leap_phase, tauti_leap_attack, tauti_ultra_typhoon;
	private boolean bUsedPush, bUsedUltimateChainStrike, bStartedLeapPhase, bTMStartedEnragePhase;
	private MagicUseListener _magicUseListener = new MagicUseListener();
	private MagicHitListener _magicHitListener = new MagicHitListener();

	// 1801783 = You rat-like creatures! Taste my attack!
	// 1801784 = Do you think you are safe outside? Feel my strength! (Tauti Ultra Typhoon)
	private final String YOU_RAT_LIKE_CREATURES_TASTE_MY_ATTACK = "You rat-like creatures! Taste my attack!";
	private final String DO_YOU_THINK_YOU_ARE_SAFE_OUTSIDE_FEEL_MY_STENGTH = "Do you think you are safe outside? Feel my strength!";

	public TautiAI(NpcInstance actor)
	{
		super(actor);
		tauti_push = SkillHolder.getInstance().getSkill(15042, 1);
		tauti_rush = SkillHolder.getInstance().getSkill(15047, 1);
		tauti_shockwave = SkillHolder.getInstance().getSkill(15046, 1);
		tauti_whirlwind = SkillHolder.getInstance().getSkill(15044, 1);
		tauti_ultra_whirlwind = SkillHolder.getInstance().getSkill(15200, 1);
		tauti_shout_maximum = SkillHolder.getInstance().getSkill(15051, 1);
		tauti_ultimate_chain_strike = SkillHolder.getInstance().getSkill(15168, 1);
		tauti_leap_phase = SkillHolder.getInstance().getSkill(16036, 1);
		tauti_leap_attack = SkillHolder.getInstance().getSkill(16037, 1);
		tauti_ultra_typhoon = SkillHolder.getInstance().getSkill(15202, 1);
		bUsedPush = false;
		bUsedUltimateChainStrike = false;
		bStartedLeapPhase = false;
		bTMStartedEnragePhase = false;
		actor.addListener(_magicUseListener);
		actor.addListener(_magicHitListener);
	}

	private class MagicUseListener implements OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature caster, Skill skill, Creature effected, boolean aBool)
		{
			if (caster == getActor())
			{
				if (skill == tauti_leap_attack)
					broadcastScreenMsgToReflection(YOU_RAT_LIKE_CREATURES_TASTE_MY_ATTACK);
				else if (skill == tauti_ultra_whirlwind)
					broadcastScreenMsgToReflection("Taste my power and perish in the name of Bremmnon!");	// TODO. This is a temporary message. Need to find real one.
				else if (skill == tauti_ultra_typhoon)
					broadcastScreenMsgToReflection(DO_YOU_THINK_YOU_ARE_SAFE_OUTSIDE_FEEL_MY_STENGTH);
				onTautiSkillUse(caster, skill, effected);
			}
		}
	}

	private class MagicHitListener implements OnMagicHitListener
	{
		@Override
		public void onMagicHit(Creature caster, Skill skill, Creature effected)
		{
			if (caster == getActor())
				onTautiSkillUse(caster, skill, effected);
		}
	}

	private void onTautiSkillUse(Creature caster, Skill skill, Creature effected)
	{
		bUsedPush = false;
		if (skill == tauti_push)
			bUsedPush = true;
		else if (skill == tauti_ultimate_chain_strike)
			bUsedUltimateChainStrike = true;
		else if (skill == tauti_leap_phase)
		{
			bStartedLeapPhase = true;
			caster.getFlags().getInvulnerable().start();
			caster.setTargetable(false);
			for (Player player : caster.getReflection().getPlayers())
			{
				if(player.getTarget() == caster)
				{
					player.setTarget(null);
					player.abortAttack(true, true);
					player.abortCast(true, true);
					player.sendActionFailed();
				}
			}
		}
		else if (skill == tauti_ultra_whirlwind)
			bUsedUltimateChainStrike = false;
		else if (skill == tauti_leap_attack)
		{
			bStartedLeapPhase = false;
			caster.getFlags().getInvulnerable().stop();
			caster.setTargetable(true);
		}
	}

	// Tauti Manager functions.
	public boolean isOnEnragePhase()
	{
		return bTMStartedEnragePhase;
	}
	public void setOnEnragePhase()
	{
		bTMStartedEnragePhase = true;
	}
	// .

	protected void broadcastScreenMsgToReflection(String s)
	{
		getActor().broadcastPacket(new ExShowScreenMessage(s, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true));
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		// @Rivelia. Generate aggro on all players.
		Reflection r = getActor().getReflection();
		if (r != null)
		{
			for(Player p : r.getPlayers())
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
		}
		super.onEvtSpawn();
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
	}

	// @Rivelia. Decide what skill should be casted.
	@Override
	protected boolean createNewTask()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return true;

		clearTasks();

		// Determinate target.
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		// Determinate distance to the target.
		double distance = actor.getDistance(target);
		int tautiAtkRange = actor.getBaseStats().getAtkRange() + 150;

		// Skill casts chance.
		//int s_tauti_push = 50;
		int s_tauti_rush = bUsedPush ? 90 : 25;
		int s_tauti_shockwave = 50;
		int s_tauti_whirlwind = 50;
		int s_tauti_ultimate_chain_strike = actor.getCurrentHpPercents() > 50 ? 0 : 90;
		int s_tauti_shout_maximum = actor.getCurrentHpPercents() > 75 ? 10 : 90;
		int s_tauti_leap_phase = actor.getCurrentHpPercents() > 90 ? 0 : 90;
		//int s_tauti_ultra_typhoon = actor.getCurrentHpPercents() > 50 ? 0 : 80;
		int s_tauti_ultra_typhoon = 0;

		if (isOnEnragePhase())
		{
			s_tauti_ultimate_chain_strike = Math.min(s_tauti_ultimate_chain_strike + 10, 100);
			s_tauti_shout_maximum = Math.min(s_tauti_shout_maximum + 10, 100);
			s_tauti_leap_phase = Math.min(s_tauti_leap_phase + 10, 100);
			//s_tauti_ultra_typhoon = Math.min(s_tauti_ultra_typhoon + 10, 100);
		}

		// r_skill is the decided skill.
		Skill r_skill = null;

		if (bUsedUltimateChainStrike)
			r_skill = tauti_ultra_whirlwind;
		else if (bStartedLeapPhase)
			r_skill = tauti_leap_attack;
		else
		{
			Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();

			if (distance > tautiAtkRange)
				addDesiredSkill(d_skill, target, distance, tauti_rush);
			else
			{
				if (Rnd.chance(s_tauti_shockwave))
					addDesiredSkill(d_skill, target, distance, tauti_shockwave);
				if (Rnd.chance(s_tauti_whirlwind))
					addDesiredSkill(d_skill, target, distance, tauti_whirlwind);
				if (Rnd.chance(s_tauti_ultimate_chain_strike))
					addDesiredSkill(d_skill, target, distance, tauti_ultimate_chain_strike);
				if (Rnd.chance(s_tauti_rush))
					addDesiredSkill(d_skill, target, distance, tauti_rush);
				if (Rnd.chance(s_tauti_leap_phase))
					addDesiredSkill(d_skill, target, distance, tauti_leap_phase);
				if (Rnd.chance(s_tauti_ultra_typhoon))
					addDesiredSkill(d_skill, target, distance, tauti_ultra_typhoon);
			}

			if (Rnd.chance(s_tauti_shout_maximum))
				addDesiredSkill(d_skill, actor, 0, tauti_shout_maximum);

			r_skill = selectTopSkill(d_skill);
		}

		// Tauti normal attack is always Tauti Push.
		if (r_skill != null)
		{
			if (r_skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF)
				target = actor;
		}
		else if (/*(r_skill == tauti_rush && distance <= tautiAtkRange && !bUsedPush) || */r_skill == null)
			r_skill = tauti_push;

		if (r_skill.getSkillType() == Skill.SkillType.BUFF || r_skill == tauti_shout_maximum || r_skill == tauti_leap_phase)
			addTaskBuff(target, r_skill);
		else
			addTaskCast(target, r_skill);
		r_skill = null;
		return true;
	}
}
