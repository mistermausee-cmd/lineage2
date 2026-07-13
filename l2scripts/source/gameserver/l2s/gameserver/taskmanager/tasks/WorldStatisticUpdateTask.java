package l2s.gameserver.taskmanager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.instancemanager.WorldStatisticsManager;

public class WorldStatisticUpdateTask extends AutomaticTask
{
    private static final Logger _log = LoggerFactory.getLogger(WorldStatisticUpdateTask.class);
    private static final SchedulingPattern PATTERN = new SchedulingPattern("30 6 1 * *");
    
    @Override
    public void doTask() throws Exception
    {
        WorldStatisticUpdateTask._log.info("World statistic task: launched.");
        WorldStatisticsManager.getInstance().resetMonthlyStatistic();
        WorldStatisticUpdateTask._log.info("World statistic task: completed.");
    }
    
    @Override
    public long reCalcTime(final boolean start)
    {
        return WorldStatisticUpdateTask.PATTERN.next(System.currentTimeMillis());
    }
}