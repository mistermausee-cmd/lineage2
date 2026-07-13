package l2s.gameserver.handler.chat;

import l2s.gameserver.network.l2.components.ChatType;


public interface IChatHandler
{
	void say();

	ChatType getType();
}