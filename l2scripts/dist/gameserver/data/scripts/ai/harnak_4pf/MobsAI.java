package ai.harnak_4pf;

import instances.HarnakUndergroundRuins;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

public class MobsAI extends Fighter
{
	private static final Location moveLoc1 = new Location(-107930, 206328, -10872);
	private static final Location moveLoc2 = new Location(-107930, 208861, -10872);
	private static final int[] SKILL_IDS = { 14612, 14613, 14614 };
	private static final int ULTIMATE_BUFF_ID = 4318;

	private boolean selected = false;
	private final int NEXT_MOB_ID;
	private final int MSG1_ID, MSG2_ID;
	private final int ROOM_ID;
	private final String NEXT_GROUP;
	private final boolean IS_LAST_GROUP;

	public MobsAI(NpcInstance actor)
	{
		super(actor);
		NEXT_MOB_ID = actor.getParameter("nextMobId", -1);
		MSG1_ID = actor.getParameter("msg1Id", -1);
		MSG2_ID = actor.getParameter("msg2Id", -1);
		ROOM_ID = actor.getParameter("room", -1);
		NEXT_GROUP = actor.getParameter("nextGroup", "");
		IS_LAST_GROUP = actor.getParameter("lastGroup", false);
		_attackAITaskDelay = 50;
		_activeAITaskDelay = 250;
		getActor().setRunning();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		if(ROOM_ID == 2)
		{
			if(MSG2_ID > 0)
				selectMe();
		}
		else if(ROOM_ID == 3)
		{
			Reflection r = getActor().getReflection();
			if(!(r instanceof HarnakUndergroundRuins))
				return;

			for(Player p : getActor().getReflection().getPlayers())
			{
				getActor().getAggroList().addDamageHate(p, 1, 10000000);
				addTaskAttack(p);
			}
		}
	}

	public void selectMe()
	{
		selected = true;
		if(MSG1_ID > 0)
			Functions.npcSayInRange(getActor(), 1500, NpcString.valueOf(MSG1_ID));
		addTaskMove(ROOM_ID == 1 ? moveLoc1 : moveLoc2, false);
		doTask();
		addTimer(1, 3000);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);
		if(!selected)
			broadCastScriptEvent("ATTACK_HIM", attacker, 1500);

		if(ROOM_ID == 2 || ROOM_ID == 3)
			if(Rnd.chance(5))
			{
				int SKILL_ID = SKILL_IDS[0];
				if(getActor().getCurrentHpPercents() < 60)
					SKILL_ID = SKILL_IDS[1];
				else if(getActor().getCurrentHpPercents() < 35)
					SKILL_ID = SKILL_IDS[2];

				Skill tempSkill = SkillHolder.getInstance().getSkill(SKILL_ID, 1);
				tempSkill.getEffects(getActor(), getActor());
			}
		if(ROOM_ID == 2)
			if(IS_LAST_GROUP && getActor().getCurrentHpPercents() < 80 && !getActor().getAbnormalList().contains(ULTIMATE_BUFF_ID))
			{
				Skill tempSkill = SkillHolder.getInstance().getSkill(ULTIMATE_BUFF_ID, 1);
				tempSkill.getEffects(getActor(), getActor());
			}
	}

	@Override
	protected void onEvtScriptEvent(String event, Object arg1, Object arg2)
	{
		super.onEvtScriptEvent(event, arg1, arg2);
		if(event.equalsIgnoreCase("ATTACK_HIM"))
		{
			selected = false;
			Creature attacker = (Creature) arg1;
			getActor().getAggroList().addDamageHate(attacker, 1, 10000000);
			addTaskAttack(attacker);
		}
		else if(event.equalsIgnoreCase("SELECT_ME"))
		{/* Заускается только для ROOM_ID == 1 */
			if(ROOM_ID == 1)
				selectMe();
		}
		else if(event.equalsIgnoreCase("FAIL_INSTANCE"))
			getActor().deleteMe();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Reflection r = getActor().getReflection();
		if(!(r instanceof HarnakUndergroundRuins))
			return;

		if(ROOM_ID == 1)
		{ /* Заускается только для ROOM_ID == 1 */
			((HarnakUndergroundRuins) r).decreaseFirstRoomMobsCount();
			if(selected)
			{
				selected = false;
				if(NEXT_MOB_ID > 0)
				{
					List<NpcInstance> npcs = r.getAllByNpcId(NEXT_MOB_ID, true);
					if(!npcs.isEmpty())
						npcs.get(0).getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, "SELECT_ME", "empty", "empty");
				}
			}
		}
		else if(ROOM_ID == 2)
			if(!NEXT_GROUP.isEmpty() && r.getAllByNpcId(getActor().getNpcId(), true).isEmpty())
			{
				((HarnakUndergroundRuins) r).increaseSecondRoomGroup();
				r.spawnByGroup(NEXT_GROUP);
				broadCastScriptEvent("ATTACK_HIM", killer, 3000);
			}
			else if(IS_LAST_GROUP)
				((HarnakUndergroundRuins) r).increaseSecondRoomGroup();
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);
		if(!isActive())
			return;
		if(timerId == 1)
			if(MSG2_ID > 0)
				Functions.npcSayInRange(getActor(), 1500, NpcString.valueOf(MSG2_ID));
	}

	@Override
	protected boolean returnHome(boolean clearAggro, boolean teleport, boolean running, boolean force)
	{
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		return false;
	}

	@Override
	protected boolean hasRandomWalk()
	{
		return false;
	}
}
