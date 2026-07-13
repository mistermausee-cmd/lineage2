package quests;

import instances.Kimerian;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author coldy
 * @date 04.09.2012
 * Reworked by Evil_dnk
 */
public class _10306_TheCorruptedLeader extends Quest
{

	private static final int NPC_NAOMI_KASHERON = 32896;
	private static final int MOB_KIMERIAN = 25745;
	private static final int[] CRYSTALS = { 9552, 9553, 9554, 9555, 9556, 9557 };

	private static final int EXP_REWARD = 9479594;	private static final int SP_REWARD = 2275; 	public _10306_TheCorruptedLeader()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(NPC_NAOMI_KASHERON);
		addKillId(MOB_KIMERIAN);
		addQuestCompletedCheck("noetieh_kisharu_q10306_03.htm", 10305);
		addLevelCheck("noetieh_kisharu_q10306_03.htm", 90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("noetieh_kisharu_q10306_06.htm") || event.equalsIgnoreCase("noetieh_kisharu_q10306_07.htm"))
		{
			st.setCond(1);
			enterInstance(st.getPlayer());
			return null;
		}
		else if(event.equalsIgnoreCase("noetieh_kisharu_q10306_08.htm"))
		{
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(CRYSTALS[Rnd.get(0, CRYSTALS.length - 1)],1);
			st.giveItems(17527,2);
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
				htmltext = "noetieh_kisharu_q10306_01.htm";
			else if(cond == 1)
				htmltext = "noetieh_kisharu_q10306_06.htm";
			else if(cond == 2)
				htmltext = "noetieh_kisharu_q10306_07.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		if(npc.getNpcId() == NPC_NAOMI_KASHERON)
			htmltext = "noetieh_kisharu_q10306_02.htm";
		return htmltext;
	}

	private void enterInstance(Player player)
	{
		Reflection reflection = player.getActiveReflection();
		if(reflection != null) {
			if(player.canReenterInstance(161))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(161))
			ReflectionUtils.enterReflection(player, new Kimerian(), 161);
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
		return null;
	}

}
