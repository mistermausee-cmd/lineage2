package l2s.gameserver.config.xml;

import l2s.gameserver.config.xml.parser.HostsConfigParser;


public abstract class ConfigParsers
{
	public static void parseAll()
	{
		HostsConfigParser.getInstance().load();
	}
}