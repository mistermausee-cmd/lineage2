package ai;

import instances.Kimerian;
import instances.KimerianHard;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Priest;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

public class KimerianAI extends Priest
{
	private int RaidHpState = 0;
	private int minionid = 25746;
	private long lastSayTime = 0;
	private static final int SAY_INTERVAL = 2000;


	private static final NpcString[] SAY_TEXT = new NpcString[] {
			NpcString.PHANTOM_IMAGE,
			NpcString.I_WILL_COME_BACK_ALIVE_WITH_ROTTING_AURA,
			NpcString.DARKNESS_THAT_ENGULFED_ME_ONE_DAY_MADE_ME_THIS_WAY,
			NpcString.I_WONT_LET_YOU_GET_AWAY_ANY_MORE,
			};

	public KimerianAI(NpcInstance actor)
	{
		super(actor);
		lastSayTime = 0;

	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();

		if (actor.isDead())
			return;
		if(lastSayTime + SAY_INTERVAL < System.currentTimeMillis())
		{
			lastSayTime = System.currentTimeMillis();
			Functions.npcSay(actor, SAY_TEXT[Rnd.get(0, SAY_TEXT.length - 1)]);
		}

		if (actor.getCurrentHpPercents() <= 55.0D && actor.getCurrentHpPercents() > 15.0D && RaidHpState == 0)
		{
			spawnMinions(actor);
			actor.getFlags().getInvulnerable().start();
			Skill tempSkill = SkillHolder.getInstance().getSkill(922, 1);
			tempSkill.getEffects(getActor(), getActor());
			actor.setTargetable(false);
			actor.getFlags().getParalyzed().start();
			RaidHpState += 1;
			if (actor.getNpcId() == 25745)
				Functions.npcSay(actor, NpcString.FOOLISH_INSIGNIFICANT_CREATURES_HOW_DARE_YOU_CHALLENGE_ME_);
			else
				Functions.npcSay(actor, NpcString.PHANTOM_IMAGE);

			ThreadPoolManager.getInstance().schedule(() -> {
				removeAll(actor);
			}, 25000);
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	private void spawnMinions(NpcInstance actor)
	{
		if (actor.getNpcId() == 25745)
		{
			minionid = 25746;
			Reflection reflection = actor.getReflection();
			if (reflection != null)
			{
				if (reflection instanceof Kimerian)
				{
					final Kimerian kimerian = (Kimerian) reflection;
					kimerian.spawnMinions(minionid);
				}
			}
		}
		else if (actor.getNpcId() == 25758)
		{
			minionid = 25759;
			Reflection reflection = actor.getReflection();
			if (reflection != null)
			{
				if (reflection instanceof KimerianHard)
				{
					final KimerianHard kimerianh = (KimerianHard) reflection;
					kimerianh.spawnMinions(minionid);
				}
			}
		}
	}

	private void removeAll(NpcInstance actor)
	{
		actor.getFlags().getInvulnerable().stop();
		actor.getAbnormalList().stop(922);
		actor.setTargetable(true);
		actor.getFlags().getParalyzed().stop();
		Functions.npcSay(actor, NpcString.UMM_YOURE_STILL_ALIVE);
	}
}