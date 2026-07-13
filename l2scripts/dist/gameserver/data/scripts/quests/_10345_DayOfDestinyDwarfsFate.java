package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10345_DayOfDestinyDwarfsFate extends SagasSuperclass
{

	public _10345_DayOfDestinyDwarfsFate()
	{
		super();

		StartNPC = 30847;
		addRaceCheck(StartNPC + ".htm", true, Race.DWARF);

		init();
	}
}

