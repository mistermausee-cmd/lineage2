package ai;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Mystic;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * AI for:
 * Hot Springs Atrox (id 21321)
 * Hot Springs Atroxspawn (id 21317)
 * Hot Springs Bandersnatch (id 21322)
 * Hot Springs Bandersnatchling (id 21314)
 * Hot Springs Flava (id 21316)
 * Hot Springs Nepenthes (id 21319)
 *
 * @author Diamond
 */
public class HotSpringsMob extends Mystic
{
	private static final int[] DEBUFF_IDS = { 4554, 4552 };

	public HotSpringsMob(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker != null && Rnd.chance(5))
		{
			int debuffId = DEBUFF_IDS[Rnd.get(DEBUFF_IDS.length)];
			int level = 0;

			for(Abnormal effect : attacker.getAbnormalList())
			{
				if(effect.getSkill().getId() == debuffId)
				{
					level = effect.getSkill().getLevel();
					break;
				}
			}

			if(level == 0)
			{
				Skill tempSkill = SkillHolder.getInstance().getSkill(debuffId, 1);
				if(tempSkill != null)
					tempSkill.getEffects(actor, attacker);
			}
			else if(level < 10)
			{
				Skill tempSkill = SkillHolder.getInstance().getSkill(debuffId, level + 1);
				if(tempSkill != null)
					tempSkill.getEffects(actor, attacker);
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}