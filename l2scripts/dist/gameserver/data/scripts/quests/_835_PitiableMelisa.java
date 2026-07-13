package quests;

import instances.MysticTavernFreya;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.ItemFunctions;

public class _835_PitiableMelisa extends Quest
{
	private static final int Сиан = 34172;
	private static final int Сетлен = 34180;

	public _835_PitiableMelisa()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Сиан);
		addTalkId(Сетлен);

		addKillId(23686, 23687, 23726);
		addKillId(23689);
		addQuestItem(46594);

		addLevelCheck("1.htm", 99);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case Сиан:
			{
				if(cond == 1)
				{
					htmltext = "34172.htm";
				}
				else if(cond == 3)
				{
					htmltext = "34172-03.htm";
				}
			}
			break;
			case Сетлен:
			{
				if(cond == 5)
				{
					st.addExpAndSp(2067574604, 15270101);
					st.takeItems(46594, -1);
					st.giveItems(57, 1186000);
					st.finishQuest();
					htmltext = "34180.htm";
				}
			}
			break;
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		Player player = qs.getPlayer();

		String htmltext = event;

		switch(event)
		{
			case "enter":
			{
				MysticTavernFreya mysticTavernFrey = (MysticTavernFreya) player.getReflection();
				if(mysticTavernFrey.getStage() == 1)
				{
					player.teleToLocation(211496, -42840, -914);
					qs.setCond(2);
				}
				htmltext = null;
			}
			break;
			case "start":
			{
				MysticTavernFreya mysticTavernFrey = (MysticTavernFreya) player.getReflection();
				if(mysticTavernFrey.getStage() == 2)
				{
					setCondFromAll(npc, 4);
					mysticTavernFrey.despawnByGroup("mystic_tavern_freya_npc");
					player.startScenePlayer(SceneMovie.EPIC_FREYA_SCENE);
					ThreadPoolManager.getInstance().schedule(() -> mysticTavernFrey.spawnByGroup("mystic_tavern_freya_boss"), SceneMovie.EPIC_FREYA_SCENE.getDuration());
				}
			}
			break;
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(npc.getNpcId() != 23689)
		{
			for(Player member : npc.getReflection().getPlayers())
			{
				QuestState memberQS = member.getQuestState(835);
				if(memberQS != null && memberQS.getCond() == qs.getCond() && !ItemFunctions.haveItem(member, 46594, 10))
					ItemFunctions.addItem(member, 46594, 1);
			}

			if(qs.getPlayer().getInventory().getCountOf(46594) >= 10)
				setCondFromAll(npc, 3);
		}
		else
		{
			setCondFromAll(npc, 5);
			npc.getReflection().startCollapseTimer(30000);
		}
		return null;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Сетлен)
		{
			htmltext = "1.htm";
		}
		return htmltext;
	}

	private void setCondFromAll(NpcInstance npc, int cond)
	{
		for(Player player : npc.getReflection().getPlayers())
		{
			QuestState qs = player.getQuestState(835);
			if(qs != null)
				qs.setCond(cond);
		}
	}
}