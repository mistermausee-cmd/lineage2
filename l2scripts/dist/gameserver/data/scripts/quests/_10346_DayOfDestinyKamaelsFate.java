package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10346_DayOfDestinyKamaelsFate extends SagasSuperclass
{

	public _10346_DayOfDestinyKamaelsFate()
	{
		super();

		StartNPC = 32221;
		addRaceCheck(StartNPC + ".htm", true, Race.KAMAEL);

		init();
	}
}
