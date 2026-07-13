package instances;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;

/**
 * @reworked by Bonux
**/
public class Fortuna extends Reflection
{
	private class FortunaProcessTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(_state ==  FortunaState.FIRST_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.WHO_DARE_TO_INTERRUPT_OUR_REST, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.FIRST_STAGE, 2, 3000L);
				}
				else if(_exState >= 2 && _exState <= 7)
				{
					spawnByGroup("fortuna_179_1");
					startProcessTask(FortunaState.FIRST_STAGE, _exState + 1, 11400L);
				}
				else if(_exState == 8)
					spawnByGroup("fortuna_179_1");
			}
			else if(_state ==  FortunaState.SECOND_STAGE)
			{
				if(_exState == 1)
				{
					spawnByGroup("fortuna_179_2");
					startProcessTask(FortunaState.SECOND_STAGE, 2, 1000L);
				}
				else if(_exState == 2)
				{
					List<NpcInstance> npcs = getAllByNpcId(CELLPHINE, false);
					for(NpcInstance npc : npcs)
						Functions.npcSay(npc, NpcString.FOR_THE_ETERNAL_REST_OF_THE_FORGOTTEN_HEROES);

					startProcessTask(FortunaState.SECOND_STAGE, 3, 3000L);
				}
				else if(_exState == 3)
				{
					List<NpcInstance> npcs = getAllByNpcId(CELLPHINE, false);
					for(NpcInstance npc : npcs)
						Functions.npcSay(npc, NpcString.THEIR_POSSESSION_CAN_BE_BROKEN_BY_BREAKING_THE_SPHERE_OF_LIGHT);

					spawnByGroup("fortuna_179_3");
					startProcessTask(FortunaState.SECOND_STAGE, 4, 5000L);
				}
				else if(_exState == 4)
				{
					List<NpcInstance> npcs = getAllByNpcId(CELLPHINE, false);
					for(NpcInstance npc : npcs)
						npc.deleteMe();

					startProcessTask(FortunaState.SECOND_STAGE, 5, 3000L);
				}
				else if(_exState == 5)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.THOSE_WHO_CAME_HERE_LOOKING_FOR_CURSED_ONES_WELCOME, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.SECOND_STAGE, 6, 5000L);
				}
				else if(_exState == 6)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_2, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_1");
					startProcessTask(FortunaState.SECOND_STAGE, 7, 13000L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.SECOND_STAGE, 8, 13000L);
				}
				else if(_exState == 8)
				{
					spawnByGroup("fortuna_179_1");
					startProcessTask(FortunaState.SECOND_STAGE, 9, 13000L);
				}
				else if(_exState == 9)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.SECOND_STAGE, 10, 13000L);
				}
				else if(_exState == 10)
				{
					spawnByGroup("fortuna_179_1");
					startProcessTask(FortunaState.SECOND_STAGE, 11, 13000L);
				}
				else if(_exState == 11)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
				}

			}
			else if(_state ==  FortunaState.THIRD_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.LETS_SEE_HOW_MUCH_YOU_CAN_ENDURE, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.THIRD_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_3, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.THIRD_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.THIRD_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.THIRD_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.THIRD_STAGE, 6, 12500L);
				}
				else if(_exState == 6)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.THIRD_STAGE, 7, 12500L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.THIRD_STAGE, 8, 12500L);
				}
				else if(_exState == 8)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.THIRD_STAGE, 9, 19500L);
				}
				else if(_exState == 9)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.WEEPING_YUI_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_6");
				}
			}
			else if(_state ==  FortunaState.FOURTH_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.BLOODSUCKING_CREATURES_ABSORB_THE_LIGHT_AND_FILL_IT_INTO_DARKNESS, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.FOURTH_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_4, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 6, 12500L);
				}
				else if(_exState == 6)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 7, 12500L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 8, 12500L);
				}
				else if(_exState == 8)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FOURTH_STAGE, 9, 19500L);
				}
				else if(_exState == 9)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.ENRAGED_MASTER_KINEN_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_7");
				}
			}
			else if(_state ==  FortunaState.FIFTH_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.WE_NEED_A_LITTLE_MORE, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.FIFTH_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_5, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 6, 12500L);
				}
				else if(_exState == 6)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 7, 12500L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 8, 12500L);
				}
				else if(_exState == 8)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 9, 12500L);
				}
				else if(_exState == 9)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.FIFTH_STAGE, 10, 19500L);
				}
				else if(_exState == 10)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.MAGICAL_WARRIOR_KONYAR_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_8");
				}
			}
			else if(_state ==  FortunaState.BONUS_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.BONUS_STAGE, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.BONUS_STAGE, 2, 12500L);
				}
				else if(_exState == 2)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.BONUS_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.BONUS_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.BONUS_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_9_3");
				}
			}
			else if(_state ==  FortunaState.SIXTH_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.THIS_IS_ONLY_THE_START, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.SIXTH_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_6, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_5_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 6, 12500L);
				}
				else if(_exState == 6)
				{
					spawnByGroup("fortuna_179_4_1");
					spawnByGroup("fortuna_179_5_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 7, 12500L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_9_1");
					spawnByGroup("fortuna_179_4_2");
					spawnByGroup("fortuna_179_9_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 8, 12500L);
				}
				else if(_exState == 8)
				{
					spawnByGroup("fortuna_179_5_1");
					spawnByGroup("fortuna_179_9_2");
					spawnByGroup("fortuna_179_4_3");
					startProcessTask(FortunaState.SIXTH_STAGE, 9, 19500L);
				}
				else if(_exState == 9)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.SIR_LESYINDA_OF_THE_BLACK_SHADOW_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_10");
				}
			}
			else if(_state ==  FortunaState.SEVENTH_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.DARKNESS_SWALLOW_EVERYTHING_AWAY, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.SEVENTH_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_7, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 3, 12500L);
				}
				else if(_exState == 3)
				{
					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 4, 12500L);
				}
				else if(_exState == 4)
				{
					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 5, 12500L);
				}
				else if(_exState == 5)
				{
					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 6, 12500L);
				}
				else if(_exState == 6)
				{
					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 7, 12500L);
				}
				else if(_exState == 7)
				{
					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.SEVENTH_STAGE, 8, 19500L);
				}
				else if(_exState == 8)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.MUKSHU_THE_COWARD_AND_BLIND_HORNAFI_APPEAR, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_12");
				}
			}
			else if(_state ==  FortunaState.EIGHT_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.AMAZING_BUT_THIS_IS_THE_END_FULL_FORCE_ADVANCE, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.EIGHT_STAGE, 2, 10000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_8, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.EIGHT_STAGE, 3, 11500L);
				}
				else if(_exState == 3)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.YOENTUMAK_THE_WAITER_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_13");
				}
			}
			else if(_state ==  FortunaState.FINAL_STAGE)
			{
				if(_exState == 1)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.I_NOW_HAVE_TO_GO_AND_HANDLE_IT, 6000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));

					startProcessTask(FortunaState.FINAL_STAGE, 2, 3000L);
				}
				else if(_exState == 2)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.FINAL_STAGE, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_11");
					startProcessTask(FortunaState.FINAL_STAGE, 3, 11500L);
				}
				else if(_exState == 3)
				{
					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.TRONE_APPEARS, 6000, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true));

					spawnByGroup("fortuna_179_14");
				}
			}
		}
	}

	private static enum FortunaState
	{
		NONE,
		FIRST_STAGE,
		SECOND_STAGE,
		THIRD_STAGE,
		FOURTH_STAGE,
		FIFTH_STAGE,
		BONUS_STAGE,
		SIXTH_STAGE,
		SEVENTH_STAGE,
		EIGHT_STAGE,
		FINAL_STAGE
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature creature)
		{
			if(_state != FortunaState.NONE) 
				return;

			Player player = creature.getPlayer();
			if(player == null || !creature.isPlayer())
				return;

			startProcessTask(FortunaState.FIRST_STAGE, 1, 5000L);
		}
		
		@Override
		public void onZoneLeave(Zone zone, Creature creature)
		{
			//
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			if(actor.isMonster())
			{
				final int npcId = actor.getNpcId();
				if(_state ==  FortunaState.THIRD_STAGE)
				{
					if(npcId == WEEPING_YUI_RAID_BOSS)
						startProcessTask(FortunaState.FOURTH_STAGE, 1, 30000L);
				}
				else if(_state ==  FortunaState.FOURTH_STAGE)
				{
					if(npcId == ENRAGED_MASTER_KINEN_RAID_BOSS)
						startProcessTask(FortunaState.FIFTH_STAGE, 1, 30000L);
				}
				else if(_state ==  FortunaState.FIFTH_STAGE)
				{
					if(npcId == MAGICAL_WARRIOR_KONYAR_RAID_BOSS)
						startProcessTask(FortunaState.BONUS_STAGE, 1, 30000L);
				}
				else if(_state ==  FortunaState.SIXTH_STAGE)
				{
					if(npcId == SIR_LESYINDA_OF_THE_BLACK_SHADOW_RAID_BOSS)
						startProcessTask(FortunaState.SEVENTH_STAGE, 1, 30000L);
				}
				else if(_state ==  FortunaState.SEVENTH_STAGE)
				{
					if(npcId == BLIND_HORNAFI_RAID_BOSS)
					{
						if(getAllByNpcId(MUKSHU_THE_COWARD_RAID_BOSS, true).isEmpty())
							startProcessTask(FortunaState.EIGHT_STAGE, 1, 30000L);
					}
					else if(npcId == MUKSHU_THE_COWARD_RAID_BOSS)
					{
						if(getAllByNpcId(BLIND_HORNAFI_RAID_BOSS, true).isEmpty())
							startProcessTask(FortunaState.EIGHT_STAGE, 1, 30000L);
					}
				}
				else if(_state ==  FortunaState.EIGHT_STAGE)
				{
					if(npcId == YOENTUMAK_THE_WAITER_RAID_BOSS)
						startProcessTask(FortunaState.FINAL_STAGE, 1, 30000L);
				}
				else if(_state ==  FortunaState.FINAL_STAGE)
				{
					if(npcId == RON_RAID_BOSS)
					{
						setReenterTime(System.currentTimeMillis());
						startCollapseTimer(300000L);

						for(Player p : getPlayers())
							p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5.0D));
					}
				}
			}
		}
	}

	// NPC's
	private static final int CELLPHINE = 33589; // Сельфина
	private static final int LIGHTNING_SPHERE_BLUE = 19082; // Световая Сфера (Синяя)
	private static final int LIGHTNING_SPHERE_ORANGE = 19083; // Световая Сфера (Оранжевая)

	// Monster's
	private static final int RON_RAID_BOSS = 25825; // Фрон Рейдовый Босс
	private static final int WEEPING_YUI_RAID_BOSS = 25837; // Рыдающая Юи Рейдовый Босс
	private static final int MUKSHU_THE_COWARD_RAID_BOSS = 25838; // Трусливый Мукшу Рейдовый Босс
	private static final int BLIND_HORNAFI_RAID_BOSS = 25839; // Слепой Хорнапи Рейдовый Босс
	private static final int ENRAGED_MASTER_KINEN_RAID_BOSS = 25840; // Разъяренный Мастер Киннен Рейдовый Босс
	private static final int SIR_LESYINDA_OF_THE_BLACK_SHADOW_RAID_BOSS = 25841; // Сэр Тьмы Ресинда Рейдовый Босс
	private static final int MAGICAL_WARRIOR_KONYAR_RAID_BOSS = 25845; // Магический Воин Коняр Рейдовый Босс
	private static final int YOENTUMAK_THE_WAITER_RAID_BOSS = 25846; // Йоентумак Ожидающий Рейдовый Босс

	private final FortunaProcessTask _fortunaProcessTask = new FortunaProcessTask();
	private final DeathListener _deathListener = new DeathListener();
	private final ZoneListener _fortunaZoneListener = new ZoneListener();

	private ScheduledFuture<?> _processTask = null;
	private FortunaState _state = FortunaState.NONE;
	private int _exState = 0;

	@Override
	protected void onCreate()
	{
		super.onCreate();

		getZone("[fortuna_begin]").addListener(_fortunaZoneListener);
	}

	@Override
	public void addObject(GameObject o)
	{
		super.addObject(o);

		if(o.isMonster())
		{
			NpcInstance npc = (NpcInstance) o;
			npc.addListener(_deathListener);

			if(npc.getAI() instanceof DefaultAI)
				((DefaultAI) npc.getAI()).setGlobalAggro(0);
		}
	}

	@Override
	public void removeObject(GameObject o)
	{
		super.removeObject(o);

		if(o.isMonster())
		{
			if(_state ==  FortunaState.FIRST_STAGE)
			{
				if(_exState == 8)
				{
					if(!haveAliveMonsters())
						startProcessTask(FortunaState.SECOND_STAGE, 1, 30000L);
				}
			}
			else if(_state ==  FortunaState.SECOND_STAGE)
			{
				if(_exState == 11)
				{
					if(!haveAliveMonsters())
						startProcessTask(FortunaState.THIRD_STAGE, 1, 30000L);
				}
			}
			else if(_state ==  FortunaState.BONUS_STAGE)
			{
				if(_exState == 5)
				{
					if(!haveAliveMonsters())
						startProcessTask(FortunaState.SIXTH_STAGE, 1, 30000L);
				}
			}
		}
	}

	private boolean haveAliveMonsters()
	{
		for(NpcInstance npc : getNpcs())
		{
			if(npc.getNpcId() == LIGHTNING_SPHERE_BLUE)
				continue;

			if(npc.getNpcId() == LIGHTNING_SPHERE_ORANGE)
				continue;

			if(!npc.isDead())
				return true;
		}
		return false;
	}

	private void startProcessTask(FortunaState state, int exState, long delay)
	{
		_state = state;
		_exState = exState;

		if(_processTask != null)
			_processTask.cancel(false);

		_processTask = ThreadPoolManager.getInstance().schedule(_fortunaProcessTask, delay);
	}
}