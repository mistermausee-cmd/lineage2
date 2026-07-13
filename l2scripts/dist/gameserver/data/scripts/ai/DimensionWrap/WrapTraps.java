package ai.DimensionWrap;

import instances.DimensionalWarp;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class WrapTraps extends DefaultAI
{
	//19556-19561 19563

	private long _lastUse;
	private int _salamanders;
	private int _distans = 100;
	private int _type = 1;
	private int _timefordespawn = 80000;
	private final int SALAMANDRAL = 23466;
	private final int SALAMANDRAM = 23473;
	private final int SALAMANDRAH = 23479;
	private int _period = 8000;
	private int _level = 1;
	private int[] trapSkills = {16408, 16409, 16410, 16411, 16412};


	public WrapTraps(NpcInstance actor)
	{
		super(actor);
		actor.getFlags().getImmobilized().start();
		actor.getFlags().getDamageBlocked().start();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(System.currentTimeMillis() - _lastUse > _period)
		{
			if (!actor.getAroundCharacters(_distans, _distans).isEmpty())
			{
				for (Creature obj : actor.getAroundCharacters(_distans, _distans))
				{
					if (obj.isPlayer())
					{
						timerS(obj);
						_lastUse = System.currentTimeMillis();
					}
				}
			}
		}
		return true;
	}

	private void timerS(Creature player)
	{
		SkillEntry skillEntry = null;

		if(getActor().getNpcId() == 19563)
		{
			if (_salamanders < 50)
			{
				_salamanders++;
				Reflection reflection = getActor().getReflection();

				if (reflection != null)
				{
					if (reflection instanceof DimensionalWarp)
					{
						final DimensionalWarp wrapStage = (DimensionalWarp) reflection;
						if (wrapStage.getStage() >= 21)
							NpcUtils.spawnSingle(SALAMANDRAH, getActor().getLoc(), getActor().getReflection());
						else if (wrapStage.getStage() >= 11)
							NpcUtils.spawnSingle(SALAMANDRAM, getActor().getLoc(), getActor().getReflection());
						else
							NpcUtils.spawnSingle(SALAMANDRAL, getActor().getLoc(), getActor().getReflection());
					}
				}
			}
		}
		else if(_type == 1)
	    {
		    skillEntry = SkillHolder.getInstance().getSkillEntry(Rnd.get(trapSkills), _level);
		    getActor().doCast(skillEntry, player, true);
		    _period = 8000;
	    }
		else if(_type == 2)
		{
			skillEntry = SkillHolder.getInstance().getSkillEntry(16413, _level);
			getActor().doCast(skillEntry, player, true);
			_period = 5000;
		}
		else if(_type == 3)
		{
			skillEntry = SkillHolder.getInstance().getSkillEntry(16379, 1);
			getActor().doCast(skillEntry, player, true);
			_period = 10000;
		}
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
        _salamanders = 0;

		if(Rnd.chance(70))
		{
			_type = 1;
		}
		else if(Rnd.chance(25))
		{
			_type = 2;
		}
		else
		{
			_type = 3;
			getActor().setNpcState(7);
		}

		Reflection reflection = getActor().getReflection();

		if (reflection != null)
		{
			if (reflection instanceof DimensionalWarp)
			{
				final DimensionalWarp wrapStage = (DimensionalWarp) reflection;
			    if(wrapStage.getStage() >= 11)
			    {
				    _distans = 180;
				    _level = 2;
				    if(_type == 1)
					    getActor().setNpcState(2);
			        else if(_type == 2)
					    getActor().setNpcState(5);
			    }
				else if(wrapStage.getStage() >= 21)
			    {
				    _distans = 235;
				    _level = 3;
				    if(_type == 1)
					    getActor().setNpcState(3);
				    else if(_type == 2)
					    getActor().setNpcState(5);
			    }
				else
			    {
				    _distans = 100;
				    if(_type == 1)
					    getActor().setNpcState(1);
				    else if(_type == 2)
					    getActor().setNpcState(4);
			    }
			}
		}
	}


}