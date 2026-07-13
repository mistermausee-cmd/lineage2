package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10343_DayOfDestinyDarkElfsFate extends SagasSuperclass
{

	public _10343_DayOfDestinyDarkElfsFate()
	{
		super();

		StartNPC = 30862;
		addRaceCheck(StartNPC + ".htm", true, Race.DARKELF);

		init();
	}
}