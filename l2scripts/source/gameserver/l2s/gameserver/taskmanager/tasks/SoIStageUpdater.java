package l2s.gameserver.taskmanager.tasks;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.instancemanager.SoIManager;

public class SoIStageUpdater extends AutomaticTask
{
    private static final SchedulingPattern PATTERN = new SchedulingPattern("0 12 * * mon");
    
    @Override
    public void doTask() throws Exception
    {
        SoIManager.setCurrentStage(1);
        SoIStageUpdater._log.info("Seed of Infinity update Task: Seed updated successfuly.");
    }
    
    @Override
    public long reCalcTime(final boolean start)
    {
        return SoIStageUpdater.PATTERN.next(System.currentTimeMillis());
    }
}