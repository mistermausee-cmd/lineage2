package quests;

import l2s.gameserver.model.base.Race;

//By viRUS
public class _10341_DayOfDestinyHumanFate extends SagasSuperclass
{

	public _10341_DayOfDestinyHumanFate()
	{
		super();

		StartNPC = 30857;
		addRaceCheck(StartNPC + ".htm", true, Race.HUMAN);

		init();
	}
}
