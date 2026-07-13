package l2s.gameserver.network.l2.c2s;

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		readD();
		readD();
	}

	@Override
	protected void runImpl()
	{
		
	}
}