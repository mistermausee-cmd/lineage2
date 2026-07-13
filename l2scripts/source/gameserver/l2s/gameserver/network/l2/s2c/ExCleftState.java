package l2s.gameserver.network.l2.s2c;

public class ExCleftState extends L2GameServerPacket
{
	public static final int CleftState_Total = 0;
	public static final int CleftState_TowerDestroy = 1;
	public static final int CleftState_CatUpdate = 2;
	public static final int CleftState_Result = 3;
	public static final int CleftState_PvPKill = 4;

	private int CleftState = 0; 

	@Override
	protected void writeImpl()
	{
		writeD(CleftState);
		switch(CleftState)
		{
			case CleftState_Total:
				
				
				
				break;
			case CleftState_TowerDestroy:
				
				break;
			case CleftState_CatUpdate:
				
				break;
			case CleftState_Result:
				
				break;
			case CleftState_PvPKill:
				
				
				
				break;
		}
	}
}