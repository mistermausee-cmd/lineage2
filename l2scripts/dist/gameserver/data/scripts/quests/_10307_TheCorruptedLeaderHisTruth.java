package quests;

import instances.Kimerian;
import instances.KimerianHard;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author coldy
 * @date 04.09.2012
 * Reworked by Evil_dnk
 */
public class _10307_TheCorruptedLeaderHisTruth extends Quest
{

	private static final int NPC_NAOMI_KASHERON = 32896;
	private static final int NPC_MIMILEAD = 32895;
	private static final int MOB_KIMERIAN = 25758;
	private static final int REWARD_ENCHANT_ARMOR_R = 17527;

	private static final int EXP_REWARD = 11779552;	private static final int SP_REWARD = 2827; 	public _10307_TheCorruptedLeaderHisTruth()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NPC_NAOMI_KASHERON);
		addTalkId(NPC_MIMILEAD);
		addKillId(MOB_KIMERIAN);

		addQuestCompletedCheck("noetieh_kisharu_q10307_03.htm", 10306);
		addLevelCheck("noetieh_kisharu_q10307_03.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(st == null)
			return NO_QUEST_DIALOG;

		if(event.equalsIgnoreCase("noetieh_kisharu_q10307_08.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("enter"))
		{
			enterInstance(st.getPlayer());
			st.setCond(1);
			return null;
		}
		else if(event.equalsIgnoreCase("noeti_mymirid_q10307_04.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(REWARD_ENCHANT_ARMOR_R, 5);
			st.finishQuest();
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		if(npc.getNpcId() == NPC_NAOMI_KASHERON)
		{
			if(cond == 0)
				htmltext = "noetieh_kisharu_q10307_01.htm";
			else if(cond == 1)
				htmltext = "noetieh_kisharu_q10307_05.htm";
			else if(cond == 2)
				htmltext = "noetieh_kisharu_q10307_06.htm";
		}
		else if(npc.getNpcId() == NPC_MIMILEAD)
		{
			if(cond == 3)
				htmltext = "noeti_mymirid_q10307_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NPC_NAOMI_KASHERON)
			htmltext = "noetieh_kisharu_q10307_02.htm";
		else if(npc.getNpcId() == NPC_MIMILEAD)
			htmltext = "noeti_mymirid_q10307_05.htm";
		return htmltext;
	}

	public String onKill(NpcInstance npc, QuestState st)
	{
			if(st.getCond() == 1)
				st.setCond(2);
		return null;
	}

	private void enterInstance(Player player)
	{
		Reflection reflection = player.getActiveReflection();
		if(reflection != null) {
			if(player.canReenterInstance(162))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(162))
			ReflectionUtils.enterReflection(player, new KimerianHard(), 162);
	}
}
