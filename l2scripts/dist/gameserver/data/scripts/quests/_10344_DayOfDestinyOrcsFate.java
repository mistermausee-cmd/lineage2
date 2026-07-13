package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10344_DayOfDestinyOrcsFate extends SagasSuperclass
{

	public _10344_DayOfDestinyOrcsFate()
	{
		super();

		StartNPC = 30865;
		addRaceCheck(StartNPC + ".htm", true, Race.ORC);

		init();
	}
}
