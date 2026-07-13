package quests;

	import l2s.gameserver.data.htm.HtmCache;
	import l2s.gameserver.data.xml.holder.ItemHolder;
	import l2s.gameserver.model.instances.NpcInstance;
	import l2s.gameserver.model.items.ItemInstance;
	import l2s.gameserver.model.quest.Quest;
	import l2s.gameserver.model.quest.QuestState;
	import l2s.gameserver.utils.HtmlUtils;

//By Evil_dnk

public class _10814_BefittingOfTheStatus extends Quest
{
	// NPC's
	private static final int GALADUCHI = 30097;

	// Item's
	private static final int CERTIF3 = 45625;
	private static final int TIARA = 37804;
	private int[] lady = {6844};//, 22166
	private int[] partyhat = {8184};
	private int[] daisy = {7696};//, 22156
	private int[] chapeau = {8185};//, 22171
	private int[] monocle = {6846,};// 22160
	private int[] outlaw = {7681};//, 22158
	private int[] forget = {7695};//, 22157
	private int[] maiden = {7682};
	private int[] eyepatch = {8916};
	private int[] angel = {8188};//, 22169
	private int[] artisian = {8186};//, 22172
	private int[] mask = {5808};//, 22163
	private int[] fairy = {8189};//, 22170
	private int[] kinghat = {21892};
	private int[] pirate = {6845};//, 22159
	private int[] arrow = {13490};

	public _10814_BefittingOfTheStatus()
	{
		super(PARTY_ONE, ONETIME);
		addStartNpc(GALADUCHI);
		addTalkId(GALADUCHI);
		addLevelCheck("galladuchi_q10814_02.htm", 99);
		addNobleCheck("galladuchi_q10814_02.htm", true);
		addItemHaveCheck("galladuchi_q10814_03.htm", 45627, 1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("galladuchi_q10814_06.htm"))
		{
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("galladuchi_q10814_09.htm"))
		{
			htmltext = HtmCache.getInstance().getHtml("quests/_10814_BefittingOfTheStatus/galladuchi_q10814_09.htm", st.getPlayer());
			htmltext = htmltext.replace("<?result1?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result2"))).getName()));
		}
		else if (event.equalsIgnoreCase("galladuchi_q10814_11.htm"))
		{
			htmltext = HtmCache.getInstance().getHtml("quests/_10814_BefittingOfTheStatus/galladuchi_q10814_11.htm", st.getPlayer());
			htmltext = htmltext.replace("<?result1?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result2"))).getName()));
			htmltext = htmltext.replace("<?result2?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result3"))).getName()));
		}
		else if (event.equalsIgnoreCase("galladuchi_q10814_13.htm"))
		{
			htmltext = HtmCache.getInstance().getHtml("quests/_10814_BefittingOfTheStatus/galladuchi_q10814_13.htm", st.getPlayer());
			htmltext = htmltext.replace("<?result1?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result2"))).getName()));
			htmltext = htmltext.replace("<?result2?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result3"))).getName()));
			htmltext = htmltext.replace("<?result3?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result4"))).getName()));
		}
		else if (event.equalsIgnoreCase("galladuchi_q10814_15.htm"))
		{
			htmltext = HtmCache.getInstance().getHtml("quests/_10814_BefittingOfTheStatus/galladuchi_q10814_15.htm", st.getPlayer());
			htmltext = htmltext.replace("<?result1?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result2"))).getName()));
			htmltext = htmltext.replace("<?result2?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result3"))).getName()));
			htmltext = htmltext.replace("<?result3?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result4"))).getName()));
			htmltext = htmltext.replace("<?result4?>", String.valueOf(ItemHolder.getInstance().getTemplate(Integer.parseInt(st.getVars().get("result5"))).getName()));
		}
		else if (event.equalsIgnoreCase("galladuchi_q10814_20.htm"))
		{
			st.giveItems(CERTIF3, 1, false);
			st.giveItems(TIARA, 1, false);
			if (checkReward(st))
				htmltext = "galladuchi_q10814_21.htm";
			st.finishQuest();
		}
		else if (event.equalsIgnoreCase("lady"))
		{
		    return deleteHarpin(st, lady, event);
		}
		else if (event.equalsIgnoreCase("partyhat"))
		{
			return deleteHarpin(st, partyhat, event);
		}
		else if (event.equalsIgnoreCase("daisy"))
		{
			return deleteHarpin(st, daisy, event);
		}
		else if (event.equalsIgnoreCase("chapeau"))
		{
			return deleteHarpin(st, chapeau, event);
		}
		else if (event.equalsIgnoreCase("monocle"))
		{
			return deleteHarpin(st, monocle, event);
		}
		else if (event.equalsIgnoreCase("outlaw"))
		{
			return deleteHarpin(st, outlaw, event);
		}
		else if (event.equalsIgnoreCase("forget"))
		{
			return deleteHarpin(st, forget, event);
		}
		else if (event.equalsIgnoreCase("maiden"))
		{
			return deleteHarpin(st, maiden, event);
		}
		else if (event.equalsIgnoreCase("eyepatch"))
		{
			return deleteHarpin(st, eyepatch, event);
		}
		else if (event.equalsIgnoreCase("angel"))
		{
			return deleteHarpin(st, angel, event);
		}
		else if (event.equalsIgnoreCase("artisian"))
		{
			return deleteHarpin(st, artisian, event);
		}
		else if (event.equalsIgnoreCase("mask"))
		{
			return deleteHarpin(st, mask, event);
		}
		else if (event.equalsIgnoreCase("fairy"))
		{
			return deleteHarpin(st, fairy, event);
		}
		else if (event.equalsIgnoreCase("kinghat"))
		{
			return deleteHarpin(st, kinghat, event);
		}
		else if (event.equalsIgnoreCase("pirate"))
		{
			return deleteHarpin(st, pirate, event);
		}
		else if (event.equalsIgnoreCase("arrow"))
		{
			return deleteHarpin(st, arrow, event);
		}
		return htmltext;
	}

	public String deleteHarpin(QuestState st, int[] ar, String event)
	{
		String htmltext = NO_QUEST_DIALOG;

		if(st.get("" +event) != null)
          return "You can't bring the same item twice.";

		boolean finded = false;

		for(ItemInstance item : st.getPlayer().getInventory().getItems())
		{
			if (finded)
				continue;

			for (int acc : ar)
			{
				if (item.getItemId() == acc && !item.isAugmented())
				{
					st.takeItems(item.getItemId(), 1);
					st.setCond(st.getCond() + 1);
					finded = true;
					st.set("result" +st.getCond(),""+item.getItemId());
					st.set("" +event,1);
					continue;
				}
			}
		}
		if (finded)
		{
			if (st.getCond() == 2)
				htmltext = "galladuchi_q10814_08.htm";
			else if (st.getCond() == 3)
				htmltext = "galladuchi_q10814_10.htm";
			else if (st.getCond() == 4)
				htmltext = "galladuchi_q10814_12.htm";
			else if (st.getCond() == 5)
				htmltext = "galladuchi_q10814_14.htm";
			else if (st.getCond() == 6)
				htmltext = "galladuchi_q10814_19.htm";
		}
		else
			htmltext = "galladuchi_q10814_17.htm";

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
			case GALADUCHI:
				if (cond == 0)
					htmltext = "galladuchi_q10814_01.htm";
				else if (cond == 1)
					htmltext = "galladuchi_q10814_07.htm";
				else if (cond == 2)
					htmltext = "galladuchi_q10814_08.htm";
				else if (cond == 3)
					htmltext = "galladuchi_q10814_10.htm";
				else if (cond == 4)
					htmltext = "galladuchi_q10814_12.htm";
				else if (cond == 5)
					htmltext = "galladuchi_q10814_14.htm";
				else if (cond == 6)
					htmltext = "galladuchi_q10814_19.htm";
				break;
		}
		return htmltext;
	}

	public boolean checkReward(QuestState st)
	{
		if (st.getPlayer().getUsedAbilitiesPoints() >= 16 && st.haveQuestItem(45623) && st.haveQuestItem(45624) && st.haveQuestItem(45625) && st.haveQuestItem(45626))
		{
			st.getPlayer().getQuestState(10811).setCond(3);
			return true;
		}

		return false;
	}
}