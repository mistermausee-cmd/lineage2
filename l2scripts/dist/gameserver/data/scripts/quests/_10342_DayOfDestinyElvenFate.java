package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10342_DayOfDestinyElvenFate extends SagasSuperclass
{

	public _10342_DayOfDestinyElvenFate()
	{
		super();

		StartNPC = 30856;
		addRaceCheck(StartNPC + ".htm", true, Race.ELF);

		init();
	}
}
