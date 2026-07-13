package ai;

import gnu.trove.map.TIntObjectMap;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.Party;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.utils.Functions;

import java.util.HashMap;
import java.util.Map;

//By Evil_dnk

public class BaylorS extends DefaultAI
{
	final Skill Berserk; // Increases P. Atk. and P. Def.
	final Skill Invincible; // Неуязвимость при 30% hp
	final Skill GroundStrike; // Массовая атака, 2500 каст
	final Skill JumpAttack; // Массовая атака, 2500 каст
	final Skill StrongPunch; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
	final Skill Stun1; // Массовое оглушение, 5000 каст
	final Skill Stun2; // Массовое оглушение, 3000 каст
	final Skill Stun3; // Массовое оглушение, 2000 каст

	private long _last_use_invul = 0;

	public BaylorS(NpcInstance actor)
	{
		super(actor);

		TIntObjectMap<Skill> skills = getActor().getTemplate().getSkills();

		Berserk = skills.get(5224);
		Invincible = skills.get(5225);
		GroundStrike = skills.get(5227);
		JumpAttack = skills.get(5228);
		StrongPunch = skills.get(5229);
		Stun1 = skills.get(5230);
		Stun2 = skills.get(5231);
		Stun3 = skills.get(5232);
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if((target = prepareTarget()) == null)
		{
			return false;
		}

		NpcInstance actor = getActor();
		if(actor.isDead())
		{
			return false;
		}

		double distance = actor.getDistance(target);
		double actor_hp_precent = actor.getCurrentHpPercents();

		if(actor_hp_precent < 30 && System.currentTimeMillis() > _last_use_invul + 300000 && !actor.getAbnormalList().contains(5225) && Rnd.chance(30))
		{
			addTaskBuff(actor, Invincible);
			//Functions.npcSay(actor, NpcString.DONT_EXPECT_TO_GET_OUT_ALIVE, ChatType.NPC_ALL, 800);
			return true;
		}

		int rnd_per = Rnd.get(100);
		if(rnd_per < 3 && !actor.getAbnormalList().contains(Berserk))
		{
			addTaskBuff(actor, Berserk);
			//Functions.npcSay(actor, NpcString.DEMON_KING_BELETH_GIVE_ME_THE_POWER_AAAHH, ChatType.NPC_ALL, 800);
			return true;
		}

		if(rnd_per < 10 || rnd_per < 15 && !actor.getAbnormalList().contains(Berserk))
		{
			return chooseTaskAndTargets(StrongPunch, target, distance);
		}

		if(!actor.isAMuted() && rnd_per < 15)
		{
			return chooseTaskAndTargets(null, target, distance);
		}

		Map<Skill, Integer> skills = new HashMap<Skill, Integer>();

		addDesiredSkill(skills, target, distance, GroundStrike);
		addDesiredSkill(skills, target, distance, JumpAttack);
		addDesiredSkill(skills, target, distance, Stun1);
		addDesiredSkill(skills, target, distance, Stun2);
		addDesiredSkill(skills, target, distance, Stun3);

		Skill skill = selectTopSkill(skills);
		if(skill != null && !skill.isOffensive())
		{
			target = actor;
		}

		return chooseTaskAndTargets(skill, target, distance);
	}
}