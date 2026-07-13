package instances;

import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;

/**
 Eanseen
 12.04.2016
 */
public class MysticTavernFreyaHook extends ListenerHook implements OnInitScriptListener
{
	@Override
	public void onInit()
	{
		addHookNpc(ListenerHookType.NPC_ASK, 34173);
		addHookNpc(ListenerHookType.NPC_ASK, 34174);
		addHookNpc(ListenerHookType.NPC_KILL, 23687);
	}

	@Override
	public void onNpcAsk(NpcInstance npc, int ask, long reply, Player player)
	{
		switch(ask)
		{
			case 34173:
				switch((int) reply)
				{
					case 1:
					{
						player.getReflection().openDoor(26160602);

						for(Player p : player.getReflection().getPlayers())
						{
							Quest quest = QuestHolder.getInstance().getQuest(835);
							QuestState qs = p.getQuestState(quest.getId());
							if(qs == null || qs.isCompleted())
							{
								qs = quest.newQuestState(p);
								qs.setCond(1);
							}
						}
					}
					break;
				}
				break;
			case 34174:
				switch((int) reply)
				{
					case 1:
					{
						if(ItemFunctions.getItemCount(player, 46594) >= 10)
						{
							player.teleToLocation(213016, -48600, -11230);
						}
						else
						{
							player.sendPacket(new HtmlMessage(5).setFile("default/34174-01.htm"));
						}
					}
					break;
				}
				break;
		}
	}

	@Override
	public void onNpcKill(NpcInstance npc, Player killer)
	{
		NpcUtils.spawnSingle(34174, npc.getLoc(), npc.getReflection());
		MysticTavernFreya mysticTavernFrey = (MysticTavernFreya) npc.getReflection();
		mysticTavernFrey.setStage(2);
	}
}