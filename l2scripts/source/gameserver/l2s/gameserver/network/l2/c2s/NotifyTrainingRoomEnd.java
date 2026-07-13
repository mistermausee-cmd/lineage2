package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.CharacterTrainingCampDAO;
import l2s.gameserver.instancemanager.TrainingCampManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.TrainingCamp;

public class NotifyTrainingRoomEnd extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

        if(!activeChar.isInTrainingCamp())
        {
            activeChar.sendActionFailed();
            return;
        }
        
		TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(activeChar);
		if(trainingCamp == null || activeChar.getObjectId() != trainingCamp.getObjectId() || !trainingCamp.isTraining())
		{
			activeChar.sendActionFailed();
			return;
		}

		trainingCamp.setEndTime(System.currentTimeMillis());
		CharacterTrainingCampDAO.getInstance().replace(activeChar.getAccountName(), trainingCamp);
		TrainingCampManager.getInstance().onExitTrainingCamp(activeChar);
	}
}