package quests;

import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.listener.actor.player.OnClassChangeListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowUsmPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.utils.HtmlUtils;

//By Evil_dnk dev.fairytale-world.ru
public class _10360_CertificationOfFate extends Quest
{
	public class ClassChangeListener implements OnClassChangeListener
	{
		public void onClassChange(Player player, ClassId oldClass, ClassId newClass)
		{
			QuestState qs = player.getQuestState(getId());
			if(qs != null)
			{
				if(!newClass.isOfLevel(ClassLevel.FIRST))
					qs.abortQuest();
			}
		}
	}

	private static final int reins = 30288;  //Human Warrior
	private static final int raimon = 30289;  //Human Mag
	private static final int tobias = 30297;  //   Dark Elf
	private static final int Drikus = 30505;  //  Orc
	private static final int mendius = 30504;  //  Dwarf
	private static final int gershfin = 32196;  // Kamael
	private static final int elinia = 30155;  //  Elf mag
	private static final int ershandel = 30158;  // Elf warrior

	//Guards
	private static final int renpard = 33524;
	private static final int joel = 33516;
	private static final int shachen = 33517;
	private static final int shelon = 33518;

	//mobs
	private static final int poslov = 27460;
	private static final int kanilov = 27459;
	private static final int sakum = 27453;

	private static final int Stone = 17587;

	private final OnClassChangeListener _classChangeListener = new ClassChangeListener();

	private int killedkanilov;
	private int killedposlov;
	private int killedsakum;

	private static final int EXP_REWARD = 2700000;	private static final int SP_REWARD = 648; 	public _10360_CertificationOfFate()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(reins);
		addStartNpc(raimon);
		addStartNpc(tobias);
		addStartNpc(Drikus);
		addStartNpc(mendius);
		addStartNpc(gershfin);
		addStartNpc(elinia);
		addStartNpc(ershandel);
		addTalkId(renpard);
		addTalkId(joel);
		addTalkId(shelon);
		addTalkId(shachen);

		addKillId(poslov);
		addKillId(kanilov);
		addKillId(sakum);

		addQuestItem(Stone);

		addLevelCheck(NO_QUEST_DIALOG, 38);
		addClassLevelCheck(NO_QUEST_DIALOG, false, ClassLevel.FIRST);
		//addQuestCompletedCheck(NO_QUEST_DIALOG, 10359); WTF?? no condition spoted
	}

	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case raimon:
				return player.getRace() == Race.HUMAN && player.isMageClass();
			case reins:
				return player.getRace() == Race.HUMAN && !player.isMageClass();
			case tobias:
				return player.getRace() == Race.DARKELF;
			case Drikus:
				return player.getRace() == Race.ORC;
			case gershfin:
				return player.getRace() == Race.KAMAEL;
			case elinia:
				return player.getRace() == Race.ELF && !player.isMageClass();
			case ershandel:
				return player.getRace() == Race.ELF && player.isMageClass();
			case mendius:
				return player.getRace() == Race.DWARF;
		}
		return true;
	}

	@Override
	public boolean checkTalkNpc(NpcInstance npc, QuestState st)
	{
		return checkStartNpc(npc, st.getPlayer());
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("1-3.htm"))
		{
			st.setCond(2);
			htmltext = "1-3.htm";
		}
		else if(event.equalsIgnoreCase("telep"))
		{
			player.teleToLocation(-24776, 188696, -3993);
			htmltext = "";
		}
		else if(event.equalsIgnoreCase("master"))
		{
			if(st.getPlayer().getRace() == Race.HUMAN)
			{
				if(st.getPlayer().isMageClass())
				{
					htmltext = "4-5re.htm";
					st.setCond(9);
				}
				else
				{
					htmltext = "4-5r.htm";
					st.setCond(8);
				}
			}
			else if(st.getPlayer().getRace() == Race.ELF)
			{
				if(st.getPlayer().isMageClass())
				{
					htmltext = "4-5e.htm";
					st.setCond(11);
				}
				else
				{
					htmltext = "4-5ew.htm";
					st.setCond(10);
				}
			}
			else if(st.getPlayer().getRace() == Race.DARKELF)
			{
				htmltext = "4-5t.htm";
				st.setCond(12);
			}
			else if(st.getPlayer().getRace() == Race.ORC)
			{
				htmltext = "4-5d.htm";
				st.setCond(13);
			}
			else if(st.getPlayer().getRace() == Race.DWARF)
			{
				htmltext = "4-5m.htm";
				st.setCond(14);
			}
			else if(st.getPlayer().getRace() == Race.KAMAEL)
			{
				htmltext = "4-5g.htm";
				st.setCond(15);
			}
		}
		else if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setCond(1);
			if(st.getPlayer().getRace() == Race.HUMAN)
			{
				if(st.getPlayer().isMageClass())
					htmltext = "0-3re.htm";
				else
					htmltext = "0-3r.htm";
			}
			else if(st.getPlayer().getRace() == Race.ELF)
			{
				if(st.getPlayer().isMageClass())
					htmltext = "0-3e.htm";
				else
					htmltext = "0-3ew.htm";
			}
			else if(st.getPlayer().getRace() == Race.DARKELF)
				htmltext = "0-3t.htm";
			else if(st.getPlayer().getRace() == Race.ORC)
				htmltext = "0-3d.htm";
			else if(st.getPlayer().getRace() == Race.DWARF)
				htmltext = "0-3m.htm";
			else if(st.getPlayer().getRace() == Race.KAMAEL)
				htmltext = "0-3g.htm";
		}
		else if(event.equalsIgnoreCase("3-3.htm"))
		{
			st.setCond(6);
			player.sendPacket(new ExShowUsmPacket(4));
		}
		else if(event.equalsIgnoreCase("2-4.htm"))
		{
			htmltext = "2-4.htm";
			st.setCond(4);
		}
		else if(event.startsWith("changeclass"))
		{
			int newClassId = 0;
			try
			{
				newClassId = Integer.parseInt(event.substring(12, event.length()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
			player.setClassId(newClassId, false);
			player.broadcastCharInfo();

			//player.broadcastCharInfo();
			st.addExpAndSp(EXP_REWARD, SP_REWARD);
			st.giveItems(ADENA_ID, 461880);
			st.giveItems(46850, 1, false);
			st.giveItems(3949, 3000);
			st.giveItems(1464, 3000);
			st.giveItems(1061, 50);
			st.finishQuest();
			st.takeAllItems(Stone);
			if(st.getPlayer().getRace() == Race.HUMAN)
			{
				if(st.getPlayer().isMageClass())
					htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6re.htm", st.getPlayer());
				else
					htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6r.htm", st.getPlayer());
			}
			else if(st.getPlayer().getRace() == Race.ELF)
			{
				if(st.getPlayer().isMageClass())
					htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6e.htm", st.getPlayer());
				else
					htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6ew.htm", st.getPlayer());
			}
			else if(st.getPlayer().getRace() == Race.DARKELF)
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6t.htm", st.getPlayer());
			else if(st.getPlayer().getRace() == Race.ORC)
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6d.htm", st.getPlayer());
			else if(st.getPlayer().getRace() == Race.DWARF)
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6m.htm", st.getPlayer());
			else if(st.getPlayer().getRace() == Race.KAMAEL)
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-6g.htm", st.getPlayer());

			htmltext = htmltext.replace("%showproof%", HtmlUtils.htmlClassName(newClassId));
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		if(npcId == raimon)
		{
			if(cond == 0)
				htmltext = "0-1re.htm";
			else if(cond == 1)
				htmltext = "0-3re.htm";
			else if(cond == 9)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5re.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == reins)
		{
			if(cond == 0)
				htmltext = "0-1r.htm";
			else if(cond == 1)
				htmltext = "0-3r.htm";
			else if(cond == 8)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5r.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == tobias)
		{
			if(cond == 0)
				htmltext = "0-1t.htm";
			else if(cond == 1)
				htmltext = "0-3t.htm";
			else if(cond == 12)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5t.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == Drikus)
		{
			if(cond == 0)
				htmltext = "0-1d.htm";
			else if(cond == 1)
				htmltext = "0-3d.htm";
			else if(cond == 13)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5d.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == gershfin)
		{
			if(cond == 0)
				htmltext = "0-1g.htm";
			else if(cond == 1)
				htmltext = "0-3g.htm";
			else if(cond == 15)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5g.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == elinia)
		{
			if(cond == 0)
				htmltext = "0-1e.htm";
			else if(cond == 1)
				htmltext = "0-3e.htm";
			else if(cond == 10)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5e.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == ershandel)
		{
			if(cond == 0)
				htmltext = "0-1ew.htm";
			else if(cond == 1)
				htmltext = "0-3ew.htm";
			else if(cond == 11)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5ew.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == mendius)
		{
			if(cond == 0)
				htmltext = "0-1m.htm";
			else if(cond == 1)
				htmltext = "0-3m.htm";
			else if(cond == 14)
			{
				htmltext = HtmCache.getInstance().getHtml("quests/_10360_CertificationOfFate/0-5m.htm", st.getPlayer());
				htmltext = htmltext.replace("%classmaster%", makeMessage(st.getPlayer()));
			}
		}
		else if(npcId == renpard)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
			else if(cond == 2)
				htmltext = "1-4.htm";
		}
		else if(npcId == joel)
		{
			if(cond == 2)
				htmltext = "2-1.htm";
			else if(cond == 3)
				htmltext = "2-2.htm";
			else if(cond == 4)
				htmltext = "2-5.htm";
		}
		else if(npcId == shachen)
		{
			if(cond == 5)
				htmltext = "3-1.htm";
		}
		else if(npcId == shelon)
		{
			if(cond == 7)
				htmltext = "4-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onCompleted(NpcInstance npc, QuestState st)
	{
		String htmltext = COMPLETED_DIALOG;
		int npcId = npc.getNpcId();
		if(npcId == raimon && st.getPlayer().getRace() == Race.HUMAN && st.getPlayer().isMageClass())
			htmltext = "0re-c.htm";
		else if(npcId == reins && st.getPlayer().getRace() == Race.HUMAN && !st.getPlayer().isMageClass())
			htmltext = "0r-c.htm";
		else if(npcId == tobias && st.getPlayer().getRace() == Race.DARKELF)
			htmltext = "0t-c.htm";
		else if(npcId == Drikus && st.getPlayer().getRace() == Race.ORC)
			htmltext = "0d-c.htm";
		else if(npcId == gershfin && st.getPlayer().getRace() == Race.KAMAEL)
			htmltext = "0g-c.htm";
		else if(npcId == elinia && st.getPlayer().getRace() == Race.ELF && !st.getPlayer().isMageClass())
			htmltext = "0e-c.htm";
		else if(npcId == ershandel && st.getPlayer().getRace() == Race.ELF && st.getPlayer().isMageClass())
			htmltext = "0ew-c.htm";
		else if(npcId == mendius && st.getPlayer().getRace() == Race.DWARF)
			htmltext = "0m-c.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == poslov && (st.getCond() == 4))
		{
			killedposlov++;
			if(killedposlov >= 1)
			{
				st.setCond(5);
				killedposlov = 0;
			}
		}

		if(npcId == kanilov && st.getCond() == 2)
		{
			killedkanilov++;
			if(killedkanilov >= 1)
			{
				st.setCond(3);
				killedkanilov = 0;
			}
		}

		if(npcId == sakum && st.getCond() == 6)
		{
			killedsakum++;
			if(killedsakum >= 1)
			{
				st.setCond(7);
				killedsakum = 0;
				st.giveItems(Stone, 1, false);
			}
		}
		return null;
	}

	private String makeMessage(Player player)
	{
		ClassId classId = player.getClassId();
		StringBuilder html = new StringBuilder();
		for(ClassId cid : ClassId.VALUES) 
		{
			// Инспектор является наследником trooper и warder, но сменить его как профессию нельзя,
			// т.к. это сабкласс. Наследуется с целью получения скилов родителей.
			if(cid == ClassId.INSPECTOR)
				continue;
			if(cid.childOf(classId) && cid.getClassLevel().ordinal() == player.getClassId().getClassLevel().ordinal() + 1)
				html.append("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h npc_%objectId%_QuestEvent ").append(getId()).append(" changeclass ").append(cid.getId()).append("\">").append(HtmlUtils.htmlClassName(cid.getId())).append("</button>");
		}
		return html.toString();
	}

	@Override
	public void onRestore(QuestState qs)
	{
		if(qs.isStarted())
			qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onAccept(QuestState qs)
	{
		qs.getPlayer().addListener(_classChangeListener);
	}

	@Override
	public void onExit(QuestState qs)
	{
		qs.getPlayer().removeListener(_classChangeListener);
	}
}
