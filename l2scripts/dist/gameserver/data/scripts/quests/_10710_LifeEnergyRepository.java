package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

//By Evil_dnk

public class _10710_LifeEnergyRepository extends Quest
{
	// NPCs
	private static final int SHUVANN = 33867;
	private static final int LIFE_ENERGY = 33962; // Life Energy Repository
	private static final int EMBRYO = 27521;
	// Items
	private static final int FRAGMENT = 39512; // Shine Stone Fragment
	private static final int EAA = 730; // Scroll: Enchant Armor (A-grade)
	// Locations
	private static final Location[] EMBRYO_LOC =
			{
					new Location(177832, -14365, -2464),
					new Location(177531, -14191, -2464),
					new Location(177746, -14364, -2464),
					new Location(177658, -14223, -2464),
					new Location(177555, -14281, -2464),
			};

	private static final int EXP_REWARD = 15207327;	private static final int SP_REWARD = 750; 	public _10710_LifeEnergyRepository()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(SHUVANN);
		addTalkId(SHUVANN, LIFE_ENERGY);
		addQuestItem(FRAGMENT);
		addLevelCheck(SHUVANN, "33867-08.htm", 61, 65);
		addQuestCompletedCheck(SHUVANN, "33867-08.htm", 10406);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("33867-04.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("action"))
		{
			st.setCond(2, true);
			st.giveItems(FRAGMENT, 1, false);

			for (Location loc : EMBRYO_LOC)
			{
				final NpcInstance embryo = addSpawn(EMBRYO, loc, 0, 120000);
				Functions.npcSay(embryo, NpcString.THE_REPOSITORY_IS_ATTACKED_FIGHT_FIGHT);
				embryo.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			return "";
		}
		else if (event.equalsIgnoreCase("33867-07.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;

		if (npc.getNpcId() == SHUVANN)
		{
			if (st.getCond() == 0)
			{
				htmtext = "33867-01.htm";
			}
			else if (st.getCond() == 1)
			{
				htmtext = "33867-05.htm";
			}
			else if (st.getCond() == 2)
			{
				htmtext = "33867-06.htm";
			}
		}
		else if (npc.getNpcId() == LIFE_ENERGY)
		{
			if (st.getCond() == 1)
			{
				htmtext = "33962-02.htm";
			}
		}
		return htmtext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			st.setCond(2);
		}
		return null;
	}
}