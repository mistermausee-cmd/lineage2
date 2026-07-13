package l2s.gameserver.listener.actor.door;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.instances.DoorInstance;


public interface OnOpenCloseListener extends CharListener
{
	void onOpen(DoorInstance doorInstance);

	void onClose(DoorInstance doorInstance);
}