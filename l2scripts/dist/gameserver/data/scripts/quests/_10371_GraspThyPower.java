package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10371_GraspThyPower extends Quest
{
	private static final int gerkenshtein = 33648;

	// Mobs to hunt
	private static final int Soldier = 23181;
	private static final int Warrior = 23182;
	private static final int Archer = 23183;
	private static final int Shaman = 23184;
	private static final int Bloody = 23185;


	private static final String Soldier_item = "Soldier";
	private static final String Warrior_item = "Warrior";
	private static final String Archer_item = "Archer";
	private static final String Shaman_item = "Shaman";
	private static final String Bloody_item = "Bloody";

	private static final int EXP_REWARD = 22641900;	private static final int SP_REWARD = 5434; 	public _10371_GraspThyPower()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(gerkenshtein);
		addTalkId(gerkenshtein);

		addKillNpcWithLog(1, Soldier_item, 12, Soldier);
		addKillNpcWithLog(1, Warrior_item, 12, Warrior);
		addKillNpcWithLog(1, Archer_item, 8, Archer);
		addKillNpcWithLog(1, Shaman_item, 8, Shaman);
		addKillNpcWithLog(1, Bloody_item, 5, Bloody);

		addLevelCheck(NO_QUEST_DIALOG, 76/*, 81*/);
		addQuestCompletedCheck(NO_QUEST_DIALOG, 10370);
		addClassIdCheck(NO_QUEST_DIALOG, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 136, 135, 134, 132, 133);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			htmltext = "0-4.htm";
		} 
		else if(event.equalsIgnoreCase("quest_rev"))
		{
			htmltext = "0-8.htm";
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(57, 484990);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		String htmltext = NO_QUEST_DIALOG;

		if(npcId == gerkenshtein)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "0-5.htm";
			else if(cond == 2)
				htmltext = "0-6.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == gerkenshtein)
			htmltext = "0-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);

		if(doneKill)
		{
			st.unset(Soldier_item);
			st.unset(Shaman_item);
			st.unset(Warrior_item);
			st.unset(Bloody_item);
			st.unset(Archer_item);
			st.setCond(2);
		}
		return null;
	}
}