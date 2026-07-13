package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.AdventurerInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class AdventurerManagerInstance extends AdventurerInstance
{
	private static final long serialVersionUID = 1L;

	public AdventurerManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "adventurer_guildsman/";
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		return "adventure_manager00" + val + ".htm";
	}
}