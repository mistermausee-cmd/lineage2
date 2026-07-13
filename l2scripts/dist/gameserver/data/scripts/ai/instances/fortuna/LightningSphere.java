package ai.instances.fortuna;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class LightningSphere extends NpcAI
{
	private static final int LIGHTNING_SPHERE_BLUE = 19082; // Световая Сфера (Синяя)
	private static final int LIGHTNING_SPHERE_ORANGE = 19083; // Световая Сфера (Оранжевая)

	private static final int SEALED_WARRIOR = 23076; // Скованный Воитель
	private static final int SEALED_MAGE = 23078; // Скованный Маг
	private static final int RAGE_WITHOUT_NAME = 23077; // Ярость без Имени

	private static final Skill FLASH_SKILL = SkillHolder.getInstance().getSkill(14254, 1);
	private static final Skill LARGE_FLASH_SKILL = SkillHolder.getInstance().getSkill(14255, 1);

	public LightningSphere(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		final NpcInstance actor = getActor();
		final int npcId = actor.getNpcId();
		if(npcId == LIGHTNING_SPHERE_BLUE)
		{
			actor.forceUseSkill(LARGE_FLASH_SKILL, attacker);

			List<NpcInstance> npcs = actor.getAroundNpc(150, 200);
			for(NpcInstance npc : npcs)
			{
				if(npc.getNpcId() == SEALED_WARRIOR || npc.getNpcId() == SEALED_MAGE)
				{
					int npcReplacerCount = Rnd.get(3, 5);
					for(int i = 0; i < npcReplacerCount; i++)
					{
						NpcInstance temp = NpcUtils.spawnSingle(RAGE_WITHOUT_NAME, Location.findPointToStay(npc.getLoc(), 50, npc.getReflection().getGeoIndex()), npc.getReflection());
						temp.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc.getAggroList().getMostHated(getMaxHateRange()), 1000);
					}

					npc.deleteMe();
				}
			}

			getActor().deleteMe();
		}
		else if(npcId == LIGHTNING_SPHERE_ORANGE)
			actor.forceUseSkill(FLASH_SKILL, attacker);
	}
}
