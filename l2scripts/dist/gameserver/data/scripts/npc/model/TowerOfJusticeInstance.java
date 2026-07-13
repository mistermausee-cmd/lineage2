package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class TowerOfJusticeInstance extends NpcInstance
{
	private class TriggerDeactivate extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			ACTIVE = false;
			removeEventTrigger(TRIGGER_ID);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final int TRIGGER_ID = 17250700;

	private static boolean ACTIVE = false;

	public TowerOfJusticeInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("condolence"))
		{
			if(ACTIVE)
				return;

			ACTIVE = true;
			addEventTrigger(TRIGGER_ID);
			ThreadPoolManager.getInstance().schedule(new TriggerDeactivate(), 3000L);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
