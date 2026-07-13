package l2s.gameserver.network.l2.s2c;


public class NpcHtmlMessagePacket extends L2GameServerPacket
{
	private final int _npcObjId;
	private final int _itemId;
	private final CharSequence _html;
	private final boolean _playVoice;

	public NpcHtmlMessagePacket(int npcObjId, int itemId, boolean playVoice, CharSequence html)
	{
		_npcObjId = npcObjId;
		_itemId = itemId;
		_playVoice = playVoice;
		_html = html;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_npcObjId);
		writeS(_html);
		writeD(_itemId);
		writeD(!_playVoice);
	}
}
