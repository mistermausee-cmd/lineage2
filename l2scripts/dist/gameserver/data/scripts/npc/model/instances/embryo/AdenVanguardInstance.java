package npc.model.instances.embryo;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DefenderInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

import instances.EmbryoCommandPost;
import instances.EmbryoCommandPost.InstanceState;

/**
 * @author Bonux
 */
public class AdenVanguardInstance extends DefenderInstance
{
	private static final long serialVersionUID = 1L;

	// NPC's
	private static final int ADOLPH_NPC_ID = 34090;
	private static final int BARTON_NPC_ID = 34091;
	private static final int HAYUK_NPC_ID = 34092;
	private static final int ELISE_NPC_ID = 34093;
	private static final int ELIYAH_NPC_ID = 34094;

	// Items
	private static final int EMERGENCY_WHISTLE__ADEN_VANGUARD = 46404;

	public AdenVanguardInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -9801 && reply == 1)
		{
			Party party = player.getParty();
			if(party != null && !party.isLeader(player))
			{
				showChatWindow(player, "default/adolf_aden_fcc_not_leader.htm", false);
				return;
			}

			Reflection reflection = getReflection();
			if(reflection instanceof EmbryoCommandPost)
			{
				EmbryoCommandPost embryo = (EmbryoCommandPost) reflection;
				if(embryo.getState() != InstanceState.SECOND_STAGE_ADOLPH_TALK)
				{
					showChatWindow(player, "default/adolf_aden_fcc_already_given.htm", false);
					return;
				}

				ItemFunctions.addItem(player, EMERGENCY_WHISTLE__ADEN_VANGUARD, 1);
				embryo.processNextState(InstanceState.THIRD_STAGE_START);
				showChatWindow(player, "default/adolf_aden_fcc_open_area.htm", false);
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			Reflection reflection = getReflection();
			if(reflection instanceof EmbryoCommandPost)
			{
				InstanceState state = ((EmbryoCommandPost) reflection).getState();
				switch(getNpcId())
				{
					case ADOLPH_NPC_ID:
					{
						if(state.ordinal() >= InstanceState.SECOND_STAGE_ADOLPH_TALK.ordinal())
							showChatWindow(player, "default/adolf_aden_fcc_no_battlemode.htm", firstTalk);
						else
							showChatWindow(player, "default/adolf_aden_fcc_battlemode.htm", firstTalk);
						break;
					}
					case BARTON_NPC_ID:
					{
						if(state.ordinal() >= InstanceState.SECOND_STAGE_ADOLPH_TALK.ordinal())
							showChatWindow(player, "default/barton_aden_fcc_no_battlemode.htm", firstTalk);
						else
							showChatWindow(player, "default/barton_aden_fcc_battlemode.htm", firstTalk);
						break;
					}
					case HAYUK_NPC_ID:
					{
						if(state.ordinal() >= InstanceState.SECOND_STAGE_ADOLPH_TALK.ordinal())
							showChatWindow(player, "default/hayuk_aden_fcc_no_battlemode.htm", firstTalk);
						else
							showChatWindow(player, "default/hayuk_aden_fcc_battlemode.htm", firstTalk);
						break;
					}
					case ELISE_NPC_ID:
					{
						if(state.ordinal() >= InstanceState.SECOND_STAGE_ADOLPH_TALK.ordinal())
							showChatWindow(player, "default/alice_aden_fcc_no_battlemode.htm", firstTalk);
						else
							showChatWindow(player, "default/alice_aden_fcc_battlemode.htm", firstTalk);
						break;
					}
					case ELIYAH_NPC_ID:
					{
						if(state.ordinal() >= InstanceState.SECOND_STAGE_ADOLPH_TALK.ordinal())
							showChatWindow(player, "default/elliyah_aden_fcc_no_battlemode.htm", firstTalk);
						else
							showChatWindow(player, "default/elliyah_aden_fcc_battlemode.htm", firstTalk);
						break;
					}
				}
			}
			else
				super.showChatWindow(player, val, firstTalk, arg);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}
