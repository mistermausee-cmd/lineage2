package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class AlchemyMasterInstance extends FreightSenderInstance
{
	private static final long serialVersionUID = 1L;

	private static final String TUTORIAL_PATH = "..\\L2text\\QT_026_alchemy_01.htm";

	public AlchemyMasterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("about"))
		{
			player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, TUTORIAL_PATH));
			showChatWindow(player, 1, false);
		}
		else if(cmd.equalsIgnoreCase("learn"))
		{
			if(player.getRace() != Race.ERTHEIA)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_ertheia.htm", false);
				return;
			}
			showAlchemySkillList(player);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
