/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package quests.Q00509_AClansFame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.script.QuestSound;
import org.l2jmobius.gameserver.model.script.QuestState;
import org.l2jmobius.gameserver.model.script.QuestType;
import org.l2jmobius.gameserver.model.script.State;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2jmobius.gameserver.network.serverpackets.RadarControl;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * A Clan's Fame (509)
 * @author Stayway
 */
public class Q00509_AClansFame extends Quest
{
	// NPC
	private static final int VALDIS = 31331;
	
	private static final Map<Integer, List<Integer>> REWARD_POINTS = new HashMap<>();
	static
	{
		REWARD_POINTS.put(2, Arrays.asList(25293, 8490, 1378)); // Hestia, Guardian Deity Of The Hot Springs
		REWARD_POINTS.put(3, Arrays.asList(25523, 8491, 1070)); // Plague Golem
		REWARD_POINTS.put(4, Arrays.asList(25322, 8492, 782)); // Demon's Agent Falston
	}
	
	private static final int[] RAID_BOSS =
	{
		25293,
		25523,
		25322
	};
	
	public Q00509_AClansFame()
	{
		super(509);
		addStartNpc(VALDIS);
		addTalkId(VALDIS);
		addKillId(RAID_BOSS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "31331-0.html":
			{
				qs.startQuest();
				break;
			}
			case "31331-1.html":
			{
				qs.set("raid", "1");
				player.sendPacket(new RadarControl(0, 2, 186304, -43744, -3193));
				break;
			}
			case "31331-2.html":
			{
				qs.set("raid", "2");
				player.sendPacket(new RadarControl(0, 2, 134672, -115600, -1216));
				break;
			}
			case "31331-3.html":
			{
				qs.set("raid", "3");
				player.sendPacket(new RadarControl(0, 2, 170000, -60000, -3500));
				break;
			}
			case "31331-4.html":
			{
				qs.set("raid", "4");
				player.sendPacket(new RadarControl(0, 2, 93296, -75104, -1824));
				break;
			}
			case "31331-5.html":
			{
				qs.exitQuest(true, true);
				break;
			}
		}
		
		return event;
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		if (player.getClan() == null)
		{
			return;
		}
		
		QuestState qs = null;
		if (player.isClanLeader())
		{
			qs = player.getQuestState(getName());
		}
		else
		{
			final Player pleader = player.getClan().getLeader().getPlayer();
			if ((pleader != null) && player.isInsideRadius3D(pleader, PlayerConfig.ALT_PARTY_RANGE))
			{
				qs = pleader.getQuestState(getName());
			}
		}
		
		if ((qs != null) && qs.isStarted())
		{
			final int raid = qs.getInt("raid");
			if (REWARD_POINTS.containsKey(raid) && (npc.getId() == REWARD_POINTS.get(raid).get(0)) && !hasQuestItems(player, REWARD_POINTS.get(raid).get(1)))
			{
				rewardItems(player, REWARD_POINTS.get(raid).get(1), 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		final Clan clan = player.getClan();
		String htmltext = getNoQuestMsg(player);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = ((clan == null) || !player.isClanLeader() || (clan.getLevel() < 6)) ? "31331-0a.htm" : "31331-0b.htm";
				break;
			}
			case State.STARTED:
			{
				if ((clan == null) || !player.isClanLeader())
				{
					qs.exitQuest(QuestType.REPEATABLE);
					return "31331-6.html";
				}
				
				final int raid = qs.getInt("raid");
				if (REWARD_POINTS.containsKey(raid))
				{
					if (hasQuestItems(player, REWARD_POINTS.get(raid).get(1)))
					{
						htmltext = "31331-" + raid + "b.html";
						if (!player.isSimulatingTalking())
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_FANFARE_1);
							takeItems(player, REWARD_POINTS.get(raid).get(1), -1);
							final int rep = REWARD_POINTS.get(raid).get(2);
							clan.addReputationScore(rep);
							player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINT_S_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION).addInt(rep));
							clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
						}
					}
					else
					{
						htmltext = "31331-" + raid + "a.html";
					}
				}
				else
				{
					htmltext = "31331-0.html";
				}
				break;
			}
			default:
			{
				break;
			}
		}
		
		return htmltext;
	}
}
