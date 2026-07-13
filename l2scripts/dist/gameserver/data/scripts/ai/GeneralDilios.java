package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

/**
 * @author Bonux
**/
public class GeneralDilios extends NpcAI
{
	private static final int ANIMATION_TIMER_ID = 1525002;
	private static final int ANNOUNCE_TIMER_ID = 780001;
	private static final int ANNOUNCE_TIMER_DELAY = 300;

	public GeneralDilios(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		addTimer(ANNOUNCE_TIMER_ID, ANNOUNCE_TIMER_DELAY * 1000);
		addTimer(ANIMATION_TIMER_ID, 1000);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == ANIMATION_TIMER_ID)
		{
			broadCastScriptEvent("show_animation", 2000);	// 1525010 - offlike event ID.
			Functions.npcSay(getActor(), NpcString.STABBING_THREE_TIMES);
			addTimer(ANIMATION_TIMER_ID, 60 * 1000);
		}
		else if(timerId == ANNOUNCE_TIMER_ID)
		{
			NpcString npcString = null;
			if(Rnd.chance(50)) // Сообщения Семени Бессмертия
			{
				int cycle = Rnd.get(1, 5);	// = gg->GetStep_FieldCycle(3); TODO: Сделать, чтобы сообщение было в зависимости от стадии семени.
				if(cycle <= 1)
					npcString = NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_BRAVE_ADVENTURERS_WHO_HAVE_CHALLENGED_THE_SEED_OF_INFINITY_ARE_CURRENTLY_INFILTRATING_THE_HALL_OF_EROSION_THROUGH_THE_DEFENSIVELY_WEAK_HALL_OF_SUFFERING;
				else if(cycle == 2)
					npcString = NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_SWEEPING_THE_SEED_OF_INFINITY_IS_CURRENTLY_COMPLETE_TO_THE_HEART_OF_THE_SEED_EKIMUS_IS_BEING_DIRECTLY_ATTACKED_AND_THE_UNDEAD_REMAINING_IN_THE_HALL_OF_SUFFERING_ARE_BEING_ERADICATED;
				else if(cycle == 3)
					npcString = NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_INFINITY_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE;
				else if(cycle == 4)
					npcString = NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_RESURRECTED_UNDEAD_IN_THE_SEED_OF_INFINITY_ARE_POURING_INTO_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION;
				else if(cycle == 5)
					npcString = NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_EKIMUS_IS_ABOUT_TO_BE_REVIVED_BY_THE_RESURRECTED_UNDEAD_IN_SEED_OF_INFINITY_SEND_ALL_REINFORCEMENTS_TO_THE_HEART_AND_THE_HALL_OF_SUFFERING;
			}
			else // Сообщения Семени Разрушения
			{
				int cycle = Rnd.get(1, 3);	// = gg->GetStep_FieldCycle(2); TODO: Сделать, чтобы сообщение было в зависимости от стадии семени.
				if(cycle <= 1)
					npcString = NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_WERE_GATHERING_BRAVE_ADVENTURERS_TO_ATTACK_TIATS_MOUNTED_TROOP_THATS_ROOTED_IN_THE_SEED_OF_DESTRUCTION;
				else if(cycle == 2)
					npcString = NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_DESTRUCTION_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE;
				else if(cycle >= 3)
					npcString = NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_TIATS_MOUNTED_TROOP_IS_CURRENTLY_TRYING_TO_RETAKE_SEED_OF_DESTRUCTION_COMMIT_ALL_THE_AVAILABLE_REINFORCEMENTS_INTO_SEED_OF_DESTRUCTION;
			}

			if(npcString != null)
				Functions.npcShout(getActor(), npcString);

			addTimer(ANNOUNCE_TIMER_ID, ANNOUNCE_TIMER_DELAY * 1000);
		}
		super.onEvtTimer(timerId, arg1, arg2);
	}
}