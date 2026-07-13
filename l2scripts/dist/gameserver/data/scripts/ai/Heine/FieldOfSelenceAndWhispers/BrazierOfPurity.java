package ai.Heine.FieldOfSelenceAndWhispers;

import java.util.List;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

/**
 * - User: Mpa3uHaKaMa3e
 * - Date: 26.06.12
 * - Time: 16:51
 * - AI для нпц Brazier Of Purity (18806).
 * - Если был атакован то кричит в чат и зовут на помощь.
 */
public class BrazierOfPurity extends NpcAI
{
	private boolean _firstTimeAttacked = true;

	public BrazierOfPurity(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = (NpcInstance) getActor();
		if(actor == null || actor.isDead())
		{
			return;
		}

		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, NpcString.THE_PURIFICATION_FIELD_IS_BEING_ATTACKED_GUARDIAN_SPIRITS_PROTECT_THE_MAGIC_FORCE, ChatType.ALL, 15000);
			List<NpcInstance> around = actor.getAroundNpc(1500, 300);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(npc.isMonster() && npc.getNpcId() == 22658 || npc.getNpcId() == 22659)
					{
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
					}
				}
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}