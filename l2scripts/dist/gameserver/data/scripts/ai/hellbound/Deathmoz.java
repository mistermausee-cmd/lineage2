package ai.hellbound;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
 */
public class Deathmoz extends Fighter
{
	// Monster ID's
	private static final int DEATHMOZ = 19512;

	private static final int DISCIPLINED_LAVI = 23374;
	private static final int MAGICAL_LAVI = 23375;

	private static final int DISCIPLINED_LAVISYS = 23376;
	private static final int EXPLOSIVE_LAVISYS = 23377;
	private static final int SOOTHING_LAVISYS = 23378;
	private static final int MAGICAL_LAVISYS = 23400;

	private static final int BERSERK_TANYA = 23379;
	private static final int BERSERK_SCARLETT = 23380;

	// Orher
	private static final int NEXT_SPAWN_TIMER_ID = 100001;
	private static final double GROUP_4_SPAWN_CHANCE = 25; // TODO: Check chance.

	private boolean _lastMagicAttack = false;

	private int _state = 0;
	private boolean _isMagic = false;

	public Deathmoz(NpcInstance actor)
	{
		super(actor);
	}

	public int getState()
	{
		return _state;
	}

	public void setState(int value)
	{
		_state = value;
	}

	public boolean isMagic()
	{
		return _isMagic;
	}

	public void setIsMagic(boolean value)
	{
		_isMagic = value;
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcString npcTitle = NpcString.LAVIS_BOSS;
		if(getState() == 0)
		{
			getActor().getMinionList().addMinion(DISCIPLINED_LAVI, 1, 0);
			getActor().getMinionList().addMinion(MAGICAL_LAVI, 1, 0);
		}
		else if(getState() == 1)
			getActor().getMinionList().addMinion(isMagic() ? MAGICAL_LAVI : DISCIPLINED_LAVI, 3, 0);
		else if(getState() == 2)
		{
			npcTitle = NpcString.LAVISYSS_BOSS;
			if(!isMagic())
			{
				getActor().getMinionList().addMinion(DISCIPLINED_LAVISYS, 2, 0);
				getActor().getMinionList().addMinion(EXPLOSIVE_LAVISYS, 1, 0);
			}
			else
			{
				getActor().getMinionList().addMinion(MAGICAL_LAVISYS, 2, 0);
				getActor().getMinionList().addMinion(SOOTHING_LAVISYS, 1, 0);
			}
		}

		getActor().getMinionList().spawnMinions();
		getActor().setTitleNpcString(npcTitle);
		getActor().setTitle("#" + npcTitle.getId());
		getActor().broadcastCharInfoImpl(NpcInfoType.TITLE, NpcInfoType.TITLE_NPCSTRINGID);
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);

		if(damage > 0 && skill != null && skill.isMagic() && skill.isOffensive())
			_lastMagicAttack = true;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		addTimer(NEXT_SPAWN_TIMER_ID, killer, 500); // TODO: Check delay.
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == NEXT_SPAWN_TIMER_ID)
		{
			Location loc = getActor().getLoc();
			Creature killer = (Creature) arg1;
			NpcInstance npc;
			switch(getState())
			{
				case 0:
				case 1:
					npc = NpcUtils.createNpc(DEATHMOZ);
					if(npc.getAI() instanceof Deathmoz)
					{
						((Deathmoz) npc.getAI()).setState(getState() + 1);
						((Deathmoz) npc.getAI()).setIsMagic(getState() == 0 && _lastMagicAttack || isMagic());
					}
					NpcUtils.spawnNpc(npc, loc);
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
					getActor().doDecay();
					break;
				case 2:
					if(Rnd.chance(GROUP_4_SPAWN_CHANCE))
					{
						npc = NpcUtils.spawnSingle(isMagic() ? BERSERK_SCARLETT : BERSERK_TANYA, loc);
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
						getActor().doDecay();
					}
					break;
			}
		}
	}
}
