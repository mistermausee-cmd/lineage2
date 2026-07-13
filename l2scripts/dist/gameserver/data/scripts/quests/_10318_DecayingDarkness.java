package quests;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.commons.util.Rnd;

public class _10318_DecayingDarkness extends Quest
{
	// NPC's
	private static final int TIPIA = 32892;
	
	// Items
	private static final int CURSED_ITEM = 17733;

	// Monsters
	private static final int ORBIS_VICTIM = 18978; // Orbis' Victim
	private static final int ORBIS_GUARD = 18979; // Orbis' Guard
	private static final int ORBIS_THROWER = 18980; // Orbis' Thrower
	private static final int ORBIS_CURATOR = 18981; // Orbis' Curator
	private static final int ORBIS_ANCIENT_HERO = 18982; // Orbis' Ancient Hero
	private static final int ORBIS_CHIEF_CURATOR = 18982; // Orbis' Chief Curator

	private static final int EXP_REWARD = 79260650;	private static final int SP_REWARD = 19022; 	public _10318_DecayingDarkness()
	{
		super(PARTY_ALL, ONETIME);
		addStartNpc(TIPIA);
		addTalkId(TIPIA);

		addKillId(ORBIS_VICTIM);
		addKillId(ORBIS_GUARD);
		addKillId(ORBIS_THROWER);
		addKillId(ORBIS_CURATOR);
		addKillId(ORBIS_ANCIENT_HERO);
		addKillId(ORBIS_CHIEF_CURATOR);
		
		addQuestItem(CURSED_ITEM);

		addQuestCompletedCheck("32892-lvl.htm", 10317);
		addLevelCheck("32892-lvl.htm", 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32892-6.htm"))
			st.setCond(1);
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();	
		if(npcId == TIPIA)
		{
			if(cond == 0)
				return "32892.htm";
			if(cond == 1)
				return "32892-7.htm";	
			if(cond == 2)
			{
				st.takeItems(CURSED_ITEM, -1);
				st.giveItems(57, 5427900);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();					
				return "32892-9.htm";
			}	
		}
		return NO_QUEST_DIALOG;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;
			
		if(Rnd.chance(15))
		{
			st.giveItems(CURSED_ITEM, 1);
			if(st.getQuestItemsCount(CURSED_ITEM) >= 8)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}