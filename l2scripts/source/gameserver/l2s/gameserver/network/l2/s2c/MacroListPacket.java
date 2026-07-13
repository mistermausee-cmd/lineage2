package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.actor.instances.player.Macro;


public class MacroListPacket extends L2GameServerPacket
{
	public static enum Action
	{
		DELETE,
		ADD,
		UPDATE
	}

	private final int _macroId, _count;
	private final Action _action;
	private final Macro _macro;

	public MacroListPacket(int macroId, Action action, int count, Macro macro)
	{
		_macroId = macroId;
		_action = action;
		_count = count;
		_macro = macro;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_action.ordinal()); 
		writeD(_macroId); 
		writeC(_count); 

		if(_macro != null)
		{
			writeC(1); 
			writeD(_macro.id); 
			writeS(_macro.name); 
			writeS(_macro.descr); 
			writeS(_macro.acronym); 
			writeD(_macro.icon); 

			writeC(_macro.commands.length); 

			for(int i = 0; i < _macro.commands.length; i++)
			{
				Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i + 1); 
				writeC(cmd.type); 
				writeD(cmd.d1); 
				writeC(cmd.d2); 
				writeS(cmd.cmd); 
			}
		}
		else
			writeC(0); 
	}
}