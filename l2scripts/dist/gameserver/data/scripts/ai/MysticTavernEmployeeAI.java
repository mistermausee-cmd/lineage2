package ai;

import instances.MysticTavernFreya;
import instances.MysticTavernKelbim;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.instancemanager.EventTriggersManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.TavernChairInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.StaticObjectInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.ChatUtils;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
**/
public class MysticTavernEmployeeAI extends NpcAI
{
	private static final Location ENTER_FAIL_LOCATION = new Location(-49833, -148022, -14152);

	private static final int[] INSTANCES_ID = new int[]{
		261, // Mystic Tavern - Freya
		262, // Mystic Tavern - Kelbim
		//263 // Mystic Tavern - Tauti
	};

	private static final NpcString[][] MESSAGES = new NpcString[][]{
		{ 
			NpcString.SHOULD_I_START_LETS_SEE_IF_WERE_READY,
			NpcString.HURRY_SIT_DOWN,
			NpcString.I_HAVE_MANY_STORIES_TO_TELL,
			NpcString.ARE_YOU_READY_TO_HEAR_THE_STORY,
			NpcString.WELL_WHICH_STORY_DO_YOU_WANT_TO_HEAR
		},
		{
			NpcString.ILL_BE_STARTING_NOW_SO_TAKE_A_SEAT_,
			NpcString.WHOSE_STORY_DO_YOU_WANT_TO_HEAR,
			NpcString.PLEASE_SIT_DOWN_SO_THAT_I_CAN_START,
			NpcString.ILL_START_ONCE_EVERYONE_IS_SEATED,
			NpcString.I_WONDER_WHAT_KIND_OF_STORIES_ARE_POPULAR_WITH_THE_CUSTOMERS
		},
		{
			NpcString.WHICH_STORY_DO_YOU_WANT_TO_HEAR,
			NpcString.YOU_HAVE_TO_BE_READY,
			NpcString.WELL_WHOSE_STORY_SHOULD_I_TELL_YOU_TODAY,
			NpcString.HEH_WHAT_SHOULD_I_TALK_ABOUT_NEXT_HMM,
			NpcString.SIT_DOWN_FIRST_I_CANT_START_OTHERWISE
		}
	};

	private int _tableId = 0;
	private Party _callerParty = null;
	private Location _spawnedLoc = null;

	public MysticTavernEmployeeAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();

		if(!isHaveWalkerRoute())
			addTimer(1, 10000);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if(timerId == 1)
		{
			ChatUtils.say(actor, MESSAGES[0][_tableId - 1]);
			addTimer(2, 10000);
		}
		else if(timerId == 2)
		{
			ChatUtils.say(actor, MESSAGES[1][_tableId - 1]);
			addTimer(3, 10000);
		}
		else if(timerId == 3)
		{
			ChatUtils.say(actor, MESSAGES[2][_tableId - 1]);
			addTimer(4, 3000);
		}
		else if(timerId == 4)
		{
			Player leader = _callerParty.getPartyLeader();
			if(leader != null)
			{
				for(Player member : _callerParty)
					member.standUp();

				int instanceId = Rnd.get(INSTANCES_ID);

				boolean success = false;
				if(leader.canEnterInstance(instanceId))
				{
					if(checkPartySeated())
					{
						success = true;

						if(instanceId == 261)
							ReflectionUtils.enterReflection(leader, new MysticTavernFreya(), instanceId);
						else if(instanceId == 262)
							ReflectionUtils.enterReflection(leader, new MysticTavernKelbim(), instanceId);
						/*else if(instanceId == 263)
							ReflectionUtils.enterReflection(leader, new MysticTavernTauti(), instanceId);*/
					}
					else
						leader.sendPacket(SystemMsg.CANNOT_ENTER_SOME_USERS_MAY_NOT_YET_BE_SEATED);
				}

				if(!success)
				{
					for(Player member : _callerParty)
						member.teleToLocation(ENTER_FAIL_LOCATION, ReflectionManager.MAIN);
				}
			}

			final int chairId = 18130000 + (_tableId * 10);
			for(int i = 0; i < 7; i++)
			{
				StaticObjectInstance staticObject = GameObjectsStorage.getStaticObject(chairId + i);
				if(staticObject != null && (staticObject instanceof TavernChairInstance))
				{
					TavernChairInstance chair = (TavernChairInstance) staticObject;
					Player seatedPlayer = chair.getSeatedPlayer();
					if(seatedPlayer != null)
					{
						seatedPlayer.standUp();
						seatedPlayer.teleToLocation(ENTER_FAIL_LOCATION, ReflectionManager.MAIN);
					}
					chair.setOwner(null);
				}
			}

			EventTriggersManager.getInstance().removeTrigger(18, 13, 18130000 + (_tableId * 1000));

			setWalkerRoute(-1);
			actor.setHeading(_spawnedLoc.h);
			actor.teleToLocation(_spawnedLoc);

			_tableId = 0;
			_callerParty = null;
			_spawnedLoc = null;
		}
		else
			super.onEvtTimer(timerId, arg1, arg2);
	}

	public boolean call(int tableId, Party party)
	{
		if(_callerParty == null && tableId > 0 && party != null)
		{
			_spawnedLoc = getActor().getSpawnedLoc();
			_spawnedLoc.setH(getActor().getHeading());

			setWalkerRoute(1);
			if(isHaveWalkerRoute())
			{
				_tableId = tableId;
				_callerParty = party;

				final int chairId = 18130000 + (_tableId * 10);
				for(int i = 0; i < 7; i++)
				{
					StaticObjectInstance staticObject = GameObjectsStorage.getStaticObject(chairId + i);
					if(staticObject != null && (staticObject instanceof TavernChairInstance))
						((TavernChairInstance) staticObject).setOwner(_callerParty);
				}

				EventTriggersManager.getInstance().addTrigger(18, 13, 18130000 + (_tableId * 1000));
				return true;
			}
		}
		return false;
	}

	private boolean checkPartySeated()
	{
		final int chairId = 18130000 + (_tableId * 10);
		loop: for(Player member : _callerParty)
		{
			for(int i = 0; i < 7; i++)
			{
				StaticObjectInstance staticObject = GameObjectsStorage.getStaticObject(chairId + i);
				if(staticObject != null && (staticObject instanceof TavernChairInstance))
				{
					if(((TavernChairInstance) staticObject).getSeatedPlayer() == member)
						continue loop;
				}
			}
			return false;
		}
		return true;
	}
}