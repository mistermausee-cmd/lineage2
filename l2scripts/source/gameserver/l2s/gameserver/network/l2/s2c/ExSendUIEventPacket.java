package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.NpcString;

public class ExSendUIEventPacket extends NpcStringContainer
{
	private int _objectId;
	private int _isHide;
	private int _isIncrease;
	private int _startTime;
	private int _endTime;

	public ExSendUIEventPacket(Player player, int isHide, int isIncrease, int startTime, int endTime, String... params)
	{
		this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
	}

	public ExSendUIEventPacket(Player player, int isHide, int isIncrease, int startTime, int endTime, NpcString npcString, String... params)
	{
		super(npcString, params);
		_objectId = player.getObjectId();
		_isHide = isHide;
		_isIncrease = isIncrease;
		_startTime = startTime;
		_endTime = endTime;
	}

	@Override
	protected void writeImpl()
	{
		if(_isHide == 5) 
		{
			writeD(_objectId);
			writeD(_isHide); 
			writeD(0x00); 
			writeD(0x00); 
			writeS(String.valueOf(_isIncrease)); 
			writeS(String.valueOf(_startTime)); 
			writeS(String.valueOf(_endTime)); 
			writeS(String.valueOf(0)); 
			writeS(String.valueOf(0)); 
			writeElements();
		}
		else if(_isHide == 2)
		{
			writeD(_objectId);
			writeD(_isHide); 
			writeD(1); 
			writeD(0x00); 
			writeS(String.valueOf(_isIncrease)); 
			writeS(""+_startTime+"%"); 
			writeS(String.valueOf(0)); 
			writeS(String.valueOf(_endTime)); 
			writeS(String.valueOf(0)); 
			writeElements();	
		}
		else
		{
			writeD(_objectId);
			writeD(_isHide); 
			writeD(0x00); 
			writeD(0x00); 
			writeS(String.valueOf(_isIncrease)); 
			writeS(String.valueOf(_startTime / 60)); 
			writeS(String.valueOf(_startTime % 60)); 
			writeS(String.valueOf(_endTime / 60)); 
			writeS(String.valueOf(_endTime % 60)); 
			writeElements();
		}
	}
}