package ai;

import instances.CorallGarden;
import instances.SteamCorridor;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

import java.util.ArrayList;
import java.util.List;

public class Michael extends Fighter
{
	private boolean attacked = false;
	protected final List<Location> _raidclones = new ArrayList<Location>();
	private int RaidHpState = 0;
	private int minionid = 25800;
	private int countattack = 0;
	private boolean locked = false;

	public Michael(NpcInstance actor)
	{
		super(actor);
		_raidclones.add(new Location(144184, 219576, -11824));
		_raidclones.add(new Location(143944, 219752, -11825));
		_raidclones.add(new Location(143848, 220024, -11825));
		_raidclones.add(new Location(143928, 220296, -11824));
		_raidclones.add(new Location(144168, 220472, -11824));
		_raidclones.add(new Location(144456, 220456, -11824));
		_raidclones.add(new Location(144680, 220296, -11824));
		_raidclones.add(new Location(144776, 220024, -11824));
		_raidclones.add(new Location(144680, 219752, -11824));
		_raidclones.add(new Location(144440, 219576, -11825));
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if (!attacked)
		{
			Reflection reflection = attacker.getReflection();

			if (reflection != null)
			{
				if (reflection instanceof CorallGarden)
				{
					final CorallGarden steamcor = (CorallGarden) reflection;

					steamcor.closeDoor(24240026);
					attacked = true;
				}
			}
		}

		NpcInstance actor = getActor();

		if (actor.isDead())
		{
			return;
		}

		if (actor.getCurrentHpPercents() <= 80.0D && actor.getCurrentHpPercents() > 60.0D && RaidHpState == 0)
		{
			spawnMinions(actor);
			actor.teleToLocation(Rnd.get(_raidclones));
			RaidHpState += 1;
		}
		else if (actor.getCurrentHpPercents() <= 60.0D && actor.getCurrentHpPercents() > 40.0D && RaidHpState == 1)
		{
			spawnMinions(actor);
			actor.teleToLocation(Rnd.get(_raidclones));
			RaidHpState += 1;
		}
		else if (actor.getCurrentHpPercents() <= 40.0D && actor.getCurrentHpPercents() > 20.0D && RaidHpState == 2)
		{
			spawnMinions(actor);
			actor.teleToLocation(Rnd.get(_raidclones));
			RaidHpState += 1;
		}
		else if (actor.getCurrentHpPercents() <= 20.0D && actor.getCurrentHpPercents() > 10.0D && RaidHpState == 3)
		{
			spawnMinions(actor);
			actor.teleToLocation(Rnd.get(_raidclones));
			RaidHpState += 1;
		}
		if (locked)
		{
			countattack++;
			if (countattack > 45)
			{
				Reflection reflection = attacker.getReflection();
				if (reflection != null)
				{
					if (reflection instanceof CorallGarden)
					{
						final CorallGarden coralg = (CorallGarden) reflection;
						coralg.deleteMinions(minionid);
					}
					countattack = 0;
					locked = false;
				}
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	private void spawnMinions(NpcInstance actor)
	{
		if (actor.getNpcId() == 25799)
		{
			minionid = 25800;
		}
		else if (actor.getNpcId() == 26114)
		{
			minionid = 26120;
		}
		else if (actor.getNpcId() == 26115)
		{
			minionid = 26121;
		}
		else if (actor.getNpcId() == 26116)
		{
			minionid = 26122;
		}

		if (!locked)
		{
			Reflection reflection = actor.getReflection();
			if (reflection != null)
			{
				if (reflection instanceof CorallGarden)
				{
					final CorallGarden coralg = (CorallGarden) reflection;
					coralg.spawnMinions(minionid);
				}
			}
			locked = true;
		}

	}
}