package l2s.gameserver.instancemanager;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Zone;
import l2s.gameserver.utils.ReflectionUtils;


public class ParnassusManager
{
	private static final String TELEPORT_ZONE_NAME = "[parnassus]";
	private static final Logger _log = LoggerFactory.getLogger(ParnassusManager.class);
	private static ParnassusManager _instance;
	private static final long _taskDelay = 24 * 60 * 60 * 1000L; 
	private static int _Stage = 0;
	private static int Prison1 = 24230010;
	private static int Prison2 = 24230012;
	private static int Prison3 = 24230014;
	private static int Vault1 = 24230016;
	private static int Vault2 = 24230018;
	private static final int Mon = 1;
	private static final int Wed = 2;
	private static final int Thi = 3;
	private static final int Tue = 4;
	private static final int Fri = 5;
	private static final int Sun = 6;
	private static final int Sat = 7;

	private static ZoneListener _zoneListener;

	public static ParnassusManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new ParnassusManager();
		}
		return _instance;
	}

	public ParnassusManager()
	{
		_zoneListener = new ZoneListener();
		
		Zone zone = ReflectionUtils.getZone(TELEPORT_ZONE_NAME);
		zone.addListener(_zoneListener);
		_log.info("Parnasus Manager: Loaded");
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(zone == null)
			{
				return;
			}

			if(cha == null)
			{
				return;
			}

			if(!cha.isPlayer())
			{
				return;
			}

			if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= 15)
			{
				eventTriggerManage(24230010, true);
				eventTriggerManage(24230012, false);
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
				{
					eventTriggerManage(24230014, true);
					eventTriggerManage(24230016, false);
					eventTriggerManage(24230018, false);
				}
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 3 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 5)
				{
					eventTriggerManage(24230014, false);
					eventTriggerManage(24230016, true);
					eventTriggerManage(24230018, false);
				}
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 6 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 7)
				{
					eventTriggerManage(24230014, false);
					eventTriggerManage(24230016, false);
					eventTriggerManage(24230018, true);
				}
				
			}
			if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= 15)
			{
				eventTriggerManage(24230010, false);
				eventTriggerManage(24230012, true);
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
				{
					eventTriggerManage(24230014, true);
					eventTriggerManage(24230016, false);
					eventTriggerManage(24230018, false);
				}
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 3 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 5)
				{
					eventTriggerManage(24230014, false);
					eventTriggerManage(24230016, true);
					eventTriggerManage(24230018, false);
				}
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 6 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 7)
				{
					eventTriggerManage(24230014, false);
					eventTriggerManage(24230016, false);
					eventTriggerManage(24230018, true);
				}
				
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}
	}

	public void eventTriggerManage(int value, boolean activate)
	{
		if(activate)
			EventTriggersManager.getInstance().addTrigger(24, 23, value);
		else
			EventTriggersManager.getInstance().removeTrigger(24, 23, value);
	}
}