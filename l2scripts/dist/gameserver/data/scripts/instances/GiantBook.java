package instances;

import ai.incubatorOfEvil.NpcWarriorAI;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

import java.util.List;

public class GiantBook extends Reflection
{
	private boolean bookTaken = false;
	private int bookDeskObjectId = 0;
	private NpcInstance Assassin1 = null;
	private NpcInstance Assassin2 = null;
	private static final int assassin = 23121;
	private NpcInstance Tairen = null;
	private final OnDeathListener _monsterDeathListener = new MonsterDeathListener();

	@Override
	protected void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onPlayerEnter(final Player player)
	{
		List<NpcInstance> desks = getAllByNpcId(33126, true);
		double seed = Math.random();
		int counter = 0;

		for (NpcInstance desk : desks)
		{
			if ((seed <= 0.25 && counter == 0) ||
					(seed > 0.25 && seed <= 0.5 && counter == 1) ||
					(seed > 0.5 && seed <= 0.75 && counter == 2) ||
					(seed > 0.75 && counter == 3))
				bookDeskObjectId = desk.getObjectId();
			++counter;
		}

		if (bookDeskObjectId == 0 && desks.size() > 0)
			bookDeskObjectId = desks.get(0).getObjectId();
		super.onPlayerEnter(player);
	}

	public GiantBook(Player player)
	{
		setReturnLoc(player.getLoc());
	}

	public void setTaken()
	{
		bookTaken = true;
	}

	public boolean getTaken()
	{
		return bookTaken;
	}

	public int getbookdesk()
	{
		return bookDeskObjectId;
	}

	public void stage2(Player player)
	{
		Assassin1 = addSpawnWithoutRespawn(assassin, new Location(-114815, 244966, -7976, 0), 0);
		Assassin2 = addSpawnWithoutRespawn(assassin, new Location(-114554, 244954, -7976, 0), 0);
		Assassin1.addListener(_monsterDeathListener);
		Assassin2.addListener(_monsterDeathListener);

		Functions.npcSayToPlayer(Assassin1, player, NpcString.FINALLY_I_THOUGHT_I_WAS_GOING_TO_DIE_WAITING);
		Assassin1.getAggroList().addDamageHate(player, 0, 10000);
		Assassin1.setAggressionTarget(player);
		Assassin2.getAggroList().addDamageHate(player, 0, 10000);
		Assassin2.setAggressionTarget(player);
	}

	public NpcInstance getTairen()
	{
		if(getAllByNpcId(33004, true).size() != 0)
			Tairen = getAllByNpcId(33004, true).get(0);
		return Tairen;
	}

	public void Attack(NpcInstance npc)
	{
		Tairen = getTairen();
		Tairen.setRunning();
		Tairen.getAggroList().addDamageHate(npc, 0, 10000);
		Tairen.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
		Tairen.setAI(new NpcWarriorAI(Tairen));
	}

	private class MonsterDeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if (getAllByNpcId(assassin, true).isEmpty())
			{
				for(Player p : getPlayers())
				{
					QuestState st = p.getQuestState(10542);
					if (st != null)
					{
						st.setCond(5);
					}
				}
			}
			else
			{
				victim.removeListener(_monsterDeathListener);
			}
		}
	}
}