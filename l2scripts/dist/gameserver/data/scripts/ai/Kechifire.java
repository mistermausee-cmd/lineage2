package ai;

import instances.SteamCorridor;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;

//By Evil_dnk

public class Kechifire extends Fighter
{
	private boolean spawnedMinions = false;

	public Kechifire(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		if (!spawnedMinions)
		{
			Reflection reflection = attacker.getReflection();

			if (reflection != null)
			{
				if (reflection instanceof SteamCorridor)
				{
					final SteamCorridor steamcor = (SteamCorridor) reflection;

					steamcor.spawnMinions();
					spawnedMinions = true;
				}
			}
		}
		super.onEvtAttacked(attacker, skill, damage);
	}
}