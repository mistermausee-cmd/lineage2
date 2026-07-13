package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.utils.ItemFunctions;
import quests._10447_TimingisEverything;

/**
 Eanseen
 15.12.2015
 */
public class NervasTemporaryPrisonKey extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
		{
			return false;
		}

		Player player = (Player) playable;

		QuestState state = player.getQuestState(10447);
		if(state == null || state.getCond() != 1)
		{
			return false;
		}

		if(player.getTarget() != null && player.getTarget().isNpc())
		{
			NpcInstance npc = (NpcInstance) player.getTarget();
			if(npc.getNpcId() == 19459)
			{
				state.playSound(Quest.SOUND_MIDDLE);
				state.setCond(2);
				ItemFunctions.deleteItem(player, 36665, 1, true);
			}
		}

		player.sendActionFailed();
		return true;
	}
}