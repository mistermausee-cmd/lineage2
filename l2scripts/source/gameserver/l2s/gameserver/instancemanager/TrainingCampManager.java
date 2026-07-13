package l2s.gameserver.instancemanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.Config;
import l2s.gameserver.dao.AccountVariablesDAO;
import l2s.gameserver.dao.CharacterTrainingCampDAO;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.TrainingCamp;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.l2.s2c.ExTrainingZone_Admission;
import l2s.gameserver.network.l2.s2c.ExTrainingZone_Leaving;
import l2s.gameserver.network.l2.s2c.ExUserInfoEquipSlot;
import l2s.gameserver.utils.Location;

public class TrainingCampManager
{
	private class TrainingCampListeners implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			TrainingCamp trainingCamp = getTrainingCamp(player);
			if(trainingCamp == null)
				return;

			if(trainingCamp.isValid(player) && trainingCamp.isTraining())
			{
				long time = trainingCamp.getTime(System.currentTimeMillis(), TimeUnit.SECONDS);
				if(time < Config.TRAINING_CAMP_MAX_DURATION)
				{
                    player.startTrainingCampTask(trainingCamp.getRemainingTime(time) * 1000L);
                    onEnterTrainingCamp(player);
                    player.sendPacket(new ExTrainingZone_Admission(trainingCamp.getLevel(), (int) TimeUnit.SECONDS.toMinutes(time), (int) trainingCamp.getRemainingTime(time)));
                }
                else
                {
                    long trainingCampDuration = TimeUnit.SECONDS.toMillis(Config.TRAINING_CAMP_MAX_DURATION) - getTrainingCampDuration(player);
                    trainingCamp.setEndTime(trainingCamp.getStartTime() + trainingCampDuration);
                }
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(TrainingCampManager.class);

	private static final String TRAINING_CAMP_DURATION_VAR = "@training_camp_duration";

	public static final Location TRAINING_LOCATION = new Location(-56516, 135938, -2672);

	private static TrainingCampManager _instance = new TrainingCampManager();

	private final Map<String, TrainingCamp> _trainingCamps = new HashMap<String, TrainingCamp>();

	public static TrainingCampManager getInstance()
	{
		return _instance;
	}

	public TrainingCampManager()
	{
		
	}

	public void init()
	{
		if(!Config.TRAINING_CAMP_ENABLE)
			return;

		CharacterTrainingCampDAO.getInstance().restore(_trainingCamps);
		CharListenerList.addGlobal(new TrainingCampListeners());
		_log.info(getClass().getSimpleName() + ": Restored " + _trainingCamps.size() + " players training camps.");
	}

	public boolean addTrainingCamp(Player player, TrainingCamp trainingCamp)
	{
		if(CharacterTrainingCampDAO.getInstance().replace(player.getAccountName(), trainingCamp))
		{
			_trainingCamps.put(player.getAccountName(), trainingCamp);
			return true;
		}
		return false;
	}

	public TrainingCamp getTrainingCamp(Player player)
	{
		return _trainingCamps.get(player.getAccountName());
	}

	public void removeTrainingCamp(Player player)
	{
		if(_trainingCamps.remove(player.getAccountName()) != null)
			CharacterTrainingCampDAO.getInstance().delete(player.getAccountName());
	}

    public long getTrainingCampDuration(Player player)
    {
        return Long.parseLong(AccountVariablesDAO.getInstance().select(player.getAccountName(), TRAINING_CAMP_DURATION_VAR, "0"));
    }
    
    public void setTrainingCampDuration(Player player, long value)
    {
        if(value == 0L)
            AccountVariablesDAO.getInstance().delete(player.getAccountName(), TRAINING_CAMP_DURATION_VAR);
        else
            AccountVariablesDAO.getInstance().insert(player.getAccountName(), TRAINING_CAMP_DURATION_VAR, String.valueOf(value));
    }
    
	public void refreshTrainingCamp()
	{
		AccountVariablesDAO.getInstance().delete(TRAINING_CAMP_DURATION_VAR);
	}

	public void onEnterTrainingCamp(Player player)
	{
		player.setTarget(null);
		player.stopMove();
		player.removeAutoShots(true);
		player.setStablePoint(player.getLoc());
		player.sendPacket(new ExUserInfoEquipSlot(player));
		player.teleToLocation(TRAINING_LOCATION);
		player.decayMe();
	}

	public void onExitTrainingCamp(Player player)
	{
		player.stopTrainingCampTask();
		player.sendPacket(new ExUserInfoEquipSlot(player));
		player.teleToLocation(player.getStablePoint(), ReflectionManager.MAIN);
		player.setStablePoint(null);
		player.setTarget(null);
		player.stopMove();
		player.spawnMe();
		player.sendPacket(ExTrainingZone_Leaving.STATIC);
	}
}