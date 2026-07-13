package quests;

import instances.AshenShadowCamp;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestNpcLogInfo;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Functions;
import org.apache.commons.lang3.ArrayUtils;

public class _828_EvasBlessing extends Quest
{
	// NPCs
	private static final int ADONIUS = 34097;
	private static final int ADONIUSEND = 34152;
	private static final int[] CAPTIV = {
			34104, 34105, 34106, 34107, 34108, 34109, 34110, 34111, 34112, 34113, 34114, 34115, 34116, 34117, 34118, 34119,
			34120, 34121, 34122, 34123, 34124, 34125, 34126, 34127, 34128, 34129, 34130, 34131, 34132, 34133, 34134, 34135
	};

	public static final String A_LIST = "A_LIST";

	private static final long EXP_REWARD = 2422697985l;
	private static final int SP_REWARD = 5814450; 

	public _828_EvasBlessing()
	{
		super(PARTY_ALL, DAILY);
		addStartNpc(ADONIUS);
		addTalkId(ADONIUS);
		addTalkId(ADONIUSEND);
		addTalkId(CAPTIV);
		addLevelCheck(ADONIUS, "as_priest_adonius_q0828_02.htm", 99);
		addKillNpcWithLog(1, 82811, A_LIST, 20, CAPTIV);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("as_priest_adonius_q0828_05.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("as_priest_adonius_q0828_08.htm"))
		{
			st.giveItems(46375, 1);
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmtext = NO_QUEST_DIALOG;
		if (npc.getNpcId() == ADONIUS)
		{
			if (st.getCond() == 0)
			{
				htmtext = "as_priest_adonius_q0828_01.htm";
			}
			else if (st.getCond() == 1)
			{
				htmtext = "as_priest_adonius_q0828_06.htm";
			}
			else if (st.getCond() == 2)
			{
				htmtext = "as_priest_adonius_q0828_07.htm";
			}
		}
		else if (npc.getNpcId() == ADONIUSEND)
		{
			if (st.getCond() == 2)
			{
				htmtext = "as_priest_adonius_q0828_07.htm";
			}
		}
		else if (ArrayUtils.contains(CAPTIV, npc.getNpcId()))
		{
			if (st.getCond() == 1 || st.getCond() == 2)
			{
				if (st.getCond() == 1)
				{
					boolean doneKill = updateKill(npc, st);
					npc.setBusy(true);
					if (doneKill)
					{
						st.unset(A_LIST);
						st.setCond(2);
					}
				}
				if (npc.isParalyzed())
					npc.getFlags().getParalyzed().stop();
				if (npc.getAbnormalEffects().contains(AbnormalEffect.FLESH_STONE))
					npc.stopAbnormalEffect(AbnormalEffect.FLESH_STONE);
				Functions.npcSay(npc, NpcString.YOU_DID_IT_THANK_YOU);
				npc.doCast(SkillHolder.getInstance().getSkillEntry(16544, 1), npc, true);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				}, 2000L);

				if (st.getPlayer().getParty() != null)
				{
					for (Player pl : st.getPlayer().getParty())
					{
						if (pl.getReflection() != null && pl.getReflection() instanceof AshenShadowCamp)
						{
							QuestState qs = pl.getQuestState(828);
							if (qs != null && qs.getCond() == 1 && pl != st.getPlayer())
							{
								boolean doneKill2 = updateKill(npc, qs);
								if (doneKill2)
								{
									qs.unset(A_LIST);
									qs.setCond(2);
								}
							}
						}
					}
				}
				return null;
			}
		}
		return htmtext;
	}
}
