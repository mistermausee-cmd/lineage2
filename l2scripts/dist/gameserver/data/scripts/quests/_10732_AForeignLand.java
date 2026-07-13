package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.UsmVideo;

/**
 * @author blacksmoke
 */
public class _10732_AForeignLand extends Quest
{
	// Npcs
	private static final int Navari = 33931;
	private static final int Gereth = 33932;
	
	private static final int EXP_REWARD = 75;	private static final int SP_REWARD = 2; 	public _10732_AForeignLand()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Navari);
		addTalkId(Navari);
		addTalkId(Gereth);
		addLevelCheck(NO_QUEST_DIALOG, 1/*, 20*/);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_ac":
				qs.setCond(1);
				qs.getPlayer().sendPacket(UsmVideo.Q014.packet(qs.getPlayer()));
				htmltext = "33931-3.htm";
				break;
			
			case "qet_rev":
				qs.showTutorialClientHTML("QT_001_Radar_01");
				htmltext = "33932-2.htm";
				qs.addExpAndSp(EXP_REWARD, SP_REWARD);
				qs.finishQuest();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = NO_QUEST_DIALOG;
		final int cond = qs.getCond();
		
		switch(npc.getNpcId())
		{
			case Navari:
				if(cond == 0)
				{
					htmltext = "33931-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33931-4.htm";
				}
				break;
			
			case Gereth:
				if(cond == 0)
				{
					htmltext = "33932-3.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33932-1.htm";
				}
				break;
		}
		
		return htmltext;
	}
}