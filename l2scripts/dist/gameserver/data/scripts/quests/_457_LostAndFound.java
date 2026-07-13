package quests;

import java.util.concurrent.ScheduledFuture;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.ThreadPoolManager;

//TODO: [Bonux] Переписать этот бред!
public class _457_LostAndFound extends Quest
{
	private ScheduledFuture<?> _followTask;

	public _457_LostAndFound()
	{
		super(PARTY_NONE, DAILY);
		addStartNpc(32759);
		addLevelCheck("lost_villager_q0457_03.htm", 82);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("lost_villager_q0457_06.htm"))
		{
			st.setCond(1);

			npc.setFollowTarget(st.getPlayer());
			if(_followTask != null)
			{
				_followTask.cancel(false);
				_followTask = null;
			}
			_followTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Follow(npc, player, st), 0L, 1000L);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 32759)
		{
			if(cond == 0)
				return "lost_villager_q0457_01.htm";
			else if(cond == 1)
			{
				if(npc.getFollowTarget() != null && npc.getFollowTarget() != player)
					return "lost_villager_q0457_01a.htm";
				return "lost_villager_q0457_08.htm";
			}
			else if(cond == 2)
			{
				npc.deleteMe();

				st.giveItems(15716, 1);
				st.finishQuest();
				return "lost_villager_q0457_09.htm";
			}
		}
		return NO_QUEST_DIALOG;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == 32759)
			htmltext = "lost_villager_q0457_02.htm";
		return htmltext;
	}

	private void checkInRadius(int id, QuestState st, NpcInstance npc)
	{
		NpcInstance quest0457 = GameObjectsStorage.getByNpcId(id);
		if(npc.getRealDistance3D(quest0457) <= 150)
		{
			st.setCond(2);
			if(_followTask != null)
				_followTask.cancel(false);
			_followTask = null;
			npc.stopMove();
		}
	}

	private class Follow implements Runnable
	{
		private NpcInstance _npc;
		private Player player;
		private QuestState st;

		private Follow(NpcInstance npc, Player pl, QuestState _st)
		{
			_npc = npc;
			player = pl;
			st = _st;
		}

		@Override
		public void run()
		{
			_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, 150);
			checkInRadius(32764, st, _npc);
		}
	}
}