package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
* @author cruel
* @name 751 - Exemption Ghosts
* @category Daily quest. Party
* @see http://l2on.net/?c=quests&id=751
*/
 
public class _751_ExemptionGhosts extends Quest
{
	private static final int Roderik = 30631;
	private static final int Deadmans_Flesh = 34971;
	private static final int[] Mobs = { 23199, 23201, 23202, 23200, 23203, 23204, 23205, 23206, 23207, 23208, 23209, 23242, 23243, 23244, 23245 };
	private int Scaldisect = 23212;
	private static final String SCALDISECT_KILL = "Scaldisect";

	private static final int EXP_REWARD = 600000000;	private static final int SP_REWARD = 144000; 	public _751_ExemptionGhosts()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(Roderik);
		addKillId(Mobs);
		addKillId(Scaldisect);
		addQuestItem(Deadmans_Flesh);
		addKillNpcWithLog(1, SCALDISECT_KILL, 1, Scaldisect);
		addLevelCheck("lvl.htm", 95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30631-3.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30631-5.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.takeItems(Deadmans_Flesh, 40);
			st.unset(SCALDISECT_KILL);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(npc.getNpcId() == Roderik)
		{
			if(cond == 0)
				htmltext = "30631.htm";
			else if(cond == 1)
				htmltext = "30631-3.htm";
			else if(cond == 2)
				htmltext = "30631-4.htm";
		}

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == Roderik)
			htmltext = "30631-0.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		boolean doneKill = updateKill(npc, st);
		if(cond == 1) {
			if(ArrayUtils.contains(Mobs, npc.getNpcId())) 
			{
				Party party = st.getPlayer().getParty();
				if(party != null) {
					for(Player member : party.getPartyMembers())
					{
						QuestState qs = member.getQuestState(getId());
						if(qs != null && qs.isStarted())
						{
							if(st.getQuestItemsCount(Deadmans_Flesh) < 40) {
								qs.giveItems(Deadmans_Flesh, 1);
								if(doneKill && st.getQuestItemsCount(Deadmans_Flesh) == 40)
									st.setCond(2);
								else
									qs.playSound(SOUND_ITEMGET);
							}
						}
					}
				}
				else
				{
					if(st.getQuestItemsCount(Deadmans_Flesh) < 50) {
						st.giveItems(Deadmans_Flesh, 1);
						if(doneKill && st.getQuestItemsCount(Deadmans_Flesh) == 40)
							st.setCond(2);
						else
							st.playSound(SOUND_ITEMGET);
					}
				}
			}
			if (npc.getNpcId() == Scaldisect)
			{
				Party party = st.getPlayer().getParty();
				if(party != null) {
					for(Player member : party.getPartyMembers())
					{
						QuestState qs = member.getQuestState(getId());
						if(qs != null && qs.isStarted())
						{
							updateKill(npc, st);
							if(st.getQuestItemsCount(Deadmans_Flesh) == 40)
								st.setCond(2);
						}
					}
				}
				else
				{
					updateKill(npc, st);
					if(st.getQuestItemsCount(Deadmans_Flesh) == 40)
						st.setCond(2);
				}
			}
		}
		return null;
	}
}