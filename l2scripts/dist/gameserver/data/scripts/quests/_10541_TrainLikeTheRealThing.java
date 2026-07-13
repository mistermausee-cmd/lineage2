package quests;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

//By Evil_dnk

public class _10541_TrainLikeTheRealThing extends Quest
{
	private static final int SHENON = 32974;
	private static final int HELPER = 32981;
	private static final int SCARE = 27457;

	private static final int EXP_REWARD = 2550;
	private static final int SP_REWARD = 7;

	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";

	public _10541_TrainLikeTheRealThing()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SHENON);
		addTalkId(SHENON);
		addTalkId(HELPER);
		addRaceCheck("si_illusion_shannon_q10541_02a.htm", Race.HUMAN, Race.ELF, Race.DARKELF, Race.ORC, Race.DWARF, Race.KAMAEL);
		addLevelCheck("si_illusion_shannon_q10541_02.htm", 1);
		addQuestCompletedCheck("si_illusion_shannon_q10541_02.htm", 10321);
		addKillNpcWithLog(1, 554107, A_LIST, 4, SCARE);
		addKillNpcWithLog(4, 1027457, B_LIST, 4, SCARE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("si_illusion_shannon_q10541_05.htm"))
		{
			st.setCond(1);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
		}
		else if (event.equalsIgnoreCase("close"))
		{
			return null;
		}
		else if (event.equalsIgnoreCase("si_newbie_guide_new_q10541_03"))
		{
			if(!st.getPlayer().isMageClass() || st.getPlayer().getRace() == Race.ORC)
				htmltext = "si_newbie_guide_new_q10541_03.htm";
			else
				htmltext = "si_newbie_guide_new_q10541_04.htm";
		}
		else if (event.equalsIgnoreCase("si_newbie_guide_new_q10541_05.htm") || event.equalsIgnoreCase("si_newbie_guide_new_q10541_06.htm"))
		{
			suppmagic(st.getPlayer(), st);
			st.setCond(4);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case SHENON:
				if (cond == 0)
					htmltext = "si_illusion_shannon_q10541_01.htm";
				else if (cond == 1)
				{
					htmltext = "si_illusion_shannon_q10541_06.htm";
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
				else if (cond == 2)
				{
					htmltext = "si_illusion_shannon_q10541_07.htm";
					st.setCond(3);
					//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					//1803329	u,Поговорите с Помощником Путешественников для Тренировки.\0
				}
				else if (cond == 3)
					htmltext = "si_illusion_shannon_q10541_08.htm";
				else if (cond == 4)
				{
					htmltext = "si_illusion_shannon_q10541_09.htm";
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
				else if (cond == 5)
				{
					htmltext = "si_illusion_shannon_q10541_10.htm";
					st.addExpAndSp(EXP_REWARD, SP_REWARD);
					st.finishQuest();
				}
				break;
			case HELPER:
				if (cond == 3)
				{
					htmltext = "si_newbie_guide_new_q10541_01.htm";
					st.showTutorialClientHTML("QT_002_Guide_01");
				}
				else if (cond == 4)
				{
					if(!st.getPlayer().isMageClass() || st.getPlayer().getRace() == Race.ORC)
						htmltext = "si_illusion_shannon_q10541_06.htm";
					else
						htmltext = "si_newbie_guide_new_q10541_08.htm";
					suppmagic(st.getPlayer(), st);
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ATTACK_THE_TRAINING_DUMMY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			if (updateKill(npc, st))
			{
				st.unset(A_LIST);
				st.setCond(2);
			}
		}
		else if (cond == 4)
		{
			if (updateKill(npc, st))
			{
				st.unset(B_LIST);
				st.setCond(5);
			}
		}
		return null;
	}

	private void suppmagic(Player player, QuestState st)
	{
		SkillHolder.getInstance().getSkill(15642, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15643, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15644, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15645, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15646, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15647, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15651, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15652, 1).getEffects(player, player);
		SkillHolder.getInstance().getSkill(15653, 1).getEffects(player, player);
		if(!player.isMageClass() || player.getRace() == Race.ORC)
			SkillHolder.getInstance().getSkill(15649, 1).getEffects(player, player);
		else
			SkillHolder.getInstance().getSkill(15650, 1).getEffects(player, player);
	}
}