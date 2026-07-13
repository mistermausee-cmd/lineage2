package zones;

import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.listener.zone.impl.PresentSceneMovieZoneListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
 */
public class TalkingIslandMovie implements OnInitScriptListener
{
	private static final String ZONE_NAME = "[ti_presentation_video]";

	private static ZoneListener _zoneListener;

	@Override
	public void onInit()
	{
		init();
	}

	private void init()
	{
		_zoneListener = new ZoneListener();
		Zone zone = ReflectionUtils.getZone(ZONE_NAME);
		if(zone != null)
			zone.addListener(_zoneListener);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(!cha.isPlayer())
				return;

			Player player = cha.getPlayer();
			if(player == null)
				return;

			if(!player.getVarBoolean("@ti_present_video"))
			{
				QuestState qs = player.getQuestState(10320);
				if(qs != null && qs.getCond() == 1)
					PresentSceneMovieZoneListener.scheduleShowMovie(SceneMovie.SINEMA_ILLUSION_02_QUE, player);
				else
					PresentSceneMovieZoneListener.scheduleShowMovie(SceneMovie.SINEMA_ILLUSION_01_QUE, player);
				player.setVar("@ti_present_video", "true", -1);
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{}
	}
}