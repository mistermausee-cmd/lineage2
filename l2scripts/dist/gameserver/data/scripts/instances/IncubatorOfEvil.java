package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;

import java.util.HashMap;
import java.util.Map;


/**
 * Author: Cain
 */
// reworked by Evil_dnk
public class IncubatorOfEvil extends Reflection
{
	protected static int[][] QuestRace = new int[][] { { 0 }, { 1 }, { 2 }, { 3 }, { 4 }, { 5 } };

	protected static int[] Quests = { 10341, 10342, 10343, 10344, 10345, 10346 };

	private int[] mobsIds = {
			27434,		  //Guardian of Darknes
			27431,		  //Slayer
			27432,		  //Pursuer
			27433,		  //Priest of Darkness
			27430		   //Screaming Shaman
	};

	private static final String STAGE_1 = "ioe_attack1_1";
	private static final String STAGE_2 = "ioe_attack1_2";
	private static final String STAGE_3 = "ioe_attack1_3";
	private static final String STAGE_4 = "ioe_attack1_4";
	private static final String STAGE_5 = "ioe_attack1_5";
	private static final String STAGE_6 = "ioe_attack2_1";
	private static final String STAGE_7 = "ioe_attack2_2";
	private static final String STAGE_8 = "ioe_attack2_3";
	private static final String STAGE_9 = "ioe_attack2_4";
	private static final String STAGE_10 = "ioe_attack2_5";

	private DeathListener _deathListener = new DeathListener();

	private int stage = 0;

	public IncubatorOfEvil(Player player)
	{
		setReturnLoc(player.getLoc());
	}

	@Override
	public void onPlayerEnter(final Player player)
	{
		super.onPlayerEnter(player);
	}


	private class SpawnStage extends RunnableImpl
	{
		private String stage;

		public SpawnStage(String stage)
		{
			this.stage = stage;
		}
		
		@Override
		public void runImpl() throws Exception
		{
			spawnByGroup(stage);
			invokeDeathListener();
		}
	}

	public void nextStage()
	{
		stageStart(stage + 1);
	}

	public int getStage()
	{
		return stage;
	}

	public void stageStart(int nStage)
	{
		stage = nStage;
		switch(nStage)
		{
			case 1: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_1), 3000);
				break;
			case 2: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_2), 1000);
				break;
			case 3: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3), 1000);
				break;
			case 4: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_4), 1000);
				break;
			case 5: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_5), 1000);
				break;
			case 6: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_6), 1000);
				break;
			case 7: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_7), 1000);
				break;
			case 8: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_8), 1000);
				break;
			case 9: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_9), 1000);
				break;
			case 10: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_10), 1000);
				break;
		}
	}

	private void invokeDeathListener()
	{
		for(int mobid : mobsIds)
			for(NpcInstance mob : getAllByNpcId(mobid, true))
				mob.addListener(_deathListener);
	}

	public static QuestState findQuest(Player player)
	{
		QuestState st = null;
		for(int questId : Quests)
		{
			st = player.getQuestState(questId);
			if(st != null)
			{
				int[] qc = QuestRace[questId - 10341];
				for(int c : qc)
				{
					if(player.getClassId().getRace().ordinal() == c)
						return st;
				}
			}
		}
		return null;
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature victim, Creature killer)
		{
			if(getAllByNpcId(27434, true).isEmpty() && getAllByNpcId(27431,true).isEmpty() && getAllByNpcId(27432,true).isEmpty() && getAllByNpcId(27433,true).isEmpty() && getAllByNpcId(27430,true).isEmpty())
			{
				int stage = getStage();

				if(stage == 1)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 2)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 3)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 4)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 5)
				{
					for(Player player : getPlayers())
					{
						QuestState qs = findQuest(player);
						if(qs != null)
							qs.setCond(9);
					}
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_HAVE_STOPPED_THEIR_ATTACK_REST_AND_THEN_SPEAK_WITH_ADOLPH, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
					return;
				}
				else if(stage == 6)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 7)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 8)
					broadcastPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
				else if(stage == 9)
				{
					broadcastPacket(new ExShowScreenMessage(NpcString.I_DEATH_WOUND_CHAMPION_OF_SHILEN_SHALL_END_YOUR_WORLD, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
					for(Player player : getPlayers())
						player.sendPacket(new EarthQuakePacket(player.getLoc(), 40, 10));
				}

				if(stage < 10)
					nextStage();
				else
					broadcastPacket(new ExShowScreenMessage(NpcString.AGH_HUMANS_HA_IT_DOES_NOT_MATTER_YOUR_WORLD_WILL_END_ANYWAYS, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			else
				victim.removeListener(_deathListener);
		}
	}
}
