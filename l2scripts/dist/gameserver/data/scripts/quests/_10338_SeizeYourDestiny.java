package quests;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import instances.HarnakUndergroundRuins;
import instances.MemoryOfDisaster;

/**
 * @author Bonux
**/
public class _10338_SeizeYourDestiny extends Quest
{
	// NPC's
	private static final int CELLPHINE = 33477; // Сельфин - Слуга Гиганта
	private static final int HADEL = 33344; // Хадел - Слуга Гиганта
	private static final int HERMUNCUS = 33340; // Гермункус - Последний Гигант

	// Monster's
	private static final int HARANAKS_WRAITH = 27445; // Дух Харнака - Дух-Смотритель

	// Item's
	private static final int SCROLL_OF_AFTERLIFE = 17600; // Записи Ада

	// Other's
	private static final Location TOMB_OF_SPIRITS = new Location(-114962, 226564, -2864);

	// Instance ID's
	private static final int INSTANCE_ID = 195;
	private static final int MEMORY_OF_DISASTER_ID = 200;

	public _10338_SeizeYourDestiny()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(CELLPHINE);
		addTalkId(CELLPHINE, HADEL, HERMUNCUS);
		addKillId(HARANAKS_WRAITH);

		addLevelCheck("selphin_q10338_04.htm", 85);
		addClassLevelCheck("selphin_q10338_04.htm", false, ClassLevel.THIRD);
		addRaceCheck("selphin_q10338_04.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if(htmltext.equalsIgnoreCase("MemoryOfDisaster")) 
		{
			Player player = st.getPlayer();
			if(checkStartCondition(npc, player) == null)
			{
				Reflection reflection = player.getActiveReflection();
				if(reflection != null) 
				{
					if(player.canReenterInstance(MEMORY_OF_DISASTER_ID))
						player.teleToLocation(reflection.getTeleportLoc(), reflection);
				} 
				else if(player.canEnterInstance(MEMORY_OF_DISASTER_ID)) 
					ReflectionUtils.enterReflection(player, new MemoryOfDisaster(player), MEMORY_OF_DISASTER_ID);
			}
			htmltext = null;
		}
		else if(htmltext.equalsIgnoreCase("enter_to_instance")) 
		{
			Player player = st.getPlayer();
			Reflection reflection = player.getActiveReflection();
			if(reflection != null) 
			{
				if(player.canReenterInstance(INSTANCE_ID))
					player.teleToLocation(reflection.getTeleportLoc(), reflection);
			} 
			else if(player.canEnterInstance(INSTANCE_ID)) 
			{
				if(cond < 3)
					ReflectionUtils.enterReflection(player, new HarnakUndergroundRuins(1), INSTANCE_ID);
				else
					ReflectionUtils.enterReflection(player, new HarnakUndergroundRuins(2), INSTANCE_ID);
			}
			htmltext = null;
		}
		else if(htmltext.equalsIgnoreCase("to_tomb_of_spirits"))
		{
			st.getPlayer().teleToLocation(TOMB_OF_SPIRITS, ReflectionManager.MAIN);
			htmltext = null;
		}
		else if(htmltext.equalsIgnoreCase("selphin_q10338_03.htm"))
		{
			st.setCond(1);
		}
		else if(htmltext.equalsIgnoreCase("hadel_q10338_05.htm"))
		{
			if(cond == 1)
			{
				st.setCond(2);
			}
		}
		else if(htmltext.equalsIgnoreCase("herumankos_q10338_02.htm"))
		{
			if(cond == 3)
			{
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_MAY_USE_SCROLL_OF_AFTERLIFE_FROM_HERMUNCUS_TO_AWAKEN, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				st.giveItems(SCROLL_OF_AFTERLIFE, 1);
				st.finishQuest();
				st.getPlayer().setVar("q10338", 1, -1);
				newQuestState(st.getPlayer());
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == CELLPHINE)
		{
			if(st.isStarted())
				htmltext = "selphin_q10338_06.htm";
			else
				htmltext = "selphin_q10338_01.htm";
		}
		else if(npcId == HADEL)
		{
			Player player = st.getPlayer();
			if(player.getLevel() < 85)
				htmltext = "hadel_q10338_06.htm";
			else if(!player.isBaseClassActive() && !player.isDualClassActive())
				htmltext = "hadel_q10338_09.htm";
			else if(st.getQuestItemsCount(SCROLL_OF_AFTERLIFE) > 0)
				htmltext = "hadel_q10338_10.htm";
			else if(player.getClassId().isOfLevel(ClassLevel.AWAKED))
				htmltext = "hadel_q10338_07.htm";
			else
			{
				switch(cond)
				{
					case 1:
						htmltext = "hadel_q10338_01.htm";
						break;
					case 2:
						htmltext = "hadel_q10338_08.htm";
						break;
					case 3:
						htmltext = "hadel_q10338_12.htm";
						break;
				}
			}
		}
		else if(npcId == HERMUNCUS)
		{
			Player player = st.getPlayer();
			if(!player.isBaseClassActive() && !player.isDualClassActive())
				htmltext = "herumankos_q10338_04.htm";
			else if(cond == 3)
				htmltext = "herumankos_q10338_01.htm";
			else if(st.getQuestItemsCount(SCROLL_OF_AFTERLIFE) > 0)
				htmltext = "herumankos_q10338_03.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == HARANAKS_WRAITH)
		{
			if(cond == 2)
			{
				st.setCond(3);
			}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().isOfLevel(ClassLevel.AWAKED) || ItemFunctions.haveItem(player, SCROLL_OF_AFTERLIFE, 1))
			return "selphin_q10338_05.htm";
					
		if(player.getClassId() == ClassId.JUDICATOR)
			return "selphin_q10338_04.htm";

		if(!player.isBaseClassActive() && !player.isDualClassActive())
			return "selphin_q10338_04.htm";

		return super.checkStartCondition(npc, player);
	}
}