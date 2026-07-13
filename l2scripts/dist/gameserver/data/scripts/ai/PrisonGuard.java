package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.ItemFunctions;

/**
 * AI мобов Prison Guard на Isle of Prayer.<br>
 * - Не используют функцию Random Walk<br>
 * - Ругаются на атаковавших чаров без эффекта Event Timer<br>
 * - Ставят в петрификацию атаковавших чаров без эффекта Event Timer<br>
 * - Не могут быть убиты чарами без эффекта Event Timer<br>
 * - Не проявляют агресии к чарам без эффекта Event Timer<br>
 * ID: 18367, 18368
 *
 * @author SYS
 */
public class PrisonGuard extends Fighter
{
	private static final int RACE_STAMP = 10013;

	public PrisonGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		// 18367 не агрятся
		NpcInstance actor = getActor();
		if(actor.isDead() || actor.getNpcId() == 18367)
			return false;

		if(!target.getAbnormalList().contains(Skill.SKILL_EVENT_TIMER))
			return false;

		return super.checkAggression(target);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;
		if(attacker.isSummon() || attacker.isPet())
			attacker = attacker.getPlayer();
		if(!attacker.getAbnormalList().contains(Skill.SKILL_EVENT_TIMER))
		{
			if(actor.getNpcId() == 18367)
				Functions.npcSay(actor, "It's not easy to obtain.");
			else if(actor.getNpcId() == 18368)
				Functions.npcSay(actor, "You're out of mind comming here...");

			SkillEntry petrification = SkillHolder.getInstance().getSkillEntry(4578, 1); // Petrification
			actor.doCast(petrification, attacker, true);

			for(Servitor s : attacker.getServitors())
				actor.doCast(petrification, s, true);

			return;
		}

		// 18367 не отвечают на атаку, но зовут друзей
		if(actor.getNpcId() == 18367)
		{
			notifyFriends(attacker, skill, damage);
			return;
		}

		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(actor.getNpcId() == 18367 && killer.getPlayer().getAbnormalList().contains(Skill.SKILL_EVENT_TIMER))
			ItemFunctions.addItem(killer.getPlayer(), RACE_STAMP, 1);

		super.onEvtDead(killer);
	}
}