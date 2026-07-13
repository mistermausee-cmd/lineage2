package quests;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;

public class _10273_GoodDayToFly extends Quest
{
	private final static int Lekon = 32557;
	private final static int VultureRider1 = 22614;
	private final static int VultureRider2 = 22615;

	private final static int Mark = 13856;

	private static final int EXP_REWARD = 6660000;	private static final int SP_REWARD = 1598; 	public _10273_GoodDayToFly()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Lekon);

		addQuestItem(Mark);

		addKillId(VultureRider1, VultureRider2);
		
		addLevelCheck("32557-00.htm", 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("32557-06.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("32557-09.htm"))
		{
			if(player.isTransformed())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			st.set("transform", "1");
			SkillHolder.getInstance().getSkill(5982, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("32557-10.htm"))
		{
			if(player.isTransformed())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			SkillHolder.getInstance().getSkill(5983, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("32557-13.htm"))
		{
			if(player.isTransformed())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			if(st.getInt("transform") == 1)
				SkillHolder.getInstance().getSkill(5982, 1).getEffects(player, player);
			else if(st.getInt("transform") == 2)
				SkillHolder.getInstance().getSkill(5983, 1).getEffects(player, player);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(cond == 0)
			htmltext = "32557-01.htm";
		else if(cond > 0)
		{
			int transform = st.getInt("transform");
			if(st.getQuestItemsCount(Mark) >= 5)
			{
				htmltext = "32557-14.htm";
				if(transform == 1)
					st.giveItems(13553, 1);
				else if(transform == 2)
					st.giveItems(13554, 1);
				st.takeAllItems(Mark);
				st.giveItems(13857, 1);
				st.addExpAndSp(EXP_REWARD, SP_REWARD);
				st.finishQuest();
			}
			else if(transform < 1)
				htmltext = "32557-07.htm";
			else
				htmltext = "32557-11.htm";
		}

		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == Lekon)
			htmltext = "32557-0a.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(!st.isStarted())
			return null;

		int cond = st.getCond();
		long count = st.getQuestItemsCount(Mark);
		if(cond == 1 && count < 5)
		{
			st.giveItems(Mark, 1);
			if(count == 4)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}