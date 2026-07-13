package services;

import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * Раз в 3 часа на всей территории базы альянса на Грации всем внутри зоны показывается мувик
 *
 * @author pchayka
 */

public class LindviorMovie implements OnInitScriptListener
{
	private static long movieDelay = 3 * 60 * 60 * 1000L; // показывать мувик раз в n часов

	@Override
	public void onInit()
	{
		Zone zone = ReflectionUtils.getZone("[keucereus_alliance_base_town_peace]");
		if(zone != null)
		{
			zone.setActive(true);
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShowLindviorMovie(zone), movieDelay, movieDelay);
		}
	}

	public class ShowLindviorMovie extends RunnableImpl
	{
		Zone _zone;

		public ShowLindviorMovie(Zone zone)
		{
			_zone = zone;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<Player> insideZoners = _zone.getInsidePlayers();

			if(insideZoners != null && !insideZoners.isEmpty())
				for(Player player : insideZoners)
					if(!player.isInBoat() && !player.isInFlyingTransform())
						player.startScenePlayer(SceneMovie.LINDVIOR_SPAWN);
		}
	}
}
