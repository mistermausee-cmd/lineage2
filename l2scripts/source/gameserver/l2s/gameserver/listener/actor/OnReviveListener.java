package l2s.gameserver.listener.actor;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Creature;

public interface OnReviveListener extends CharListener
{
    public void onRevive(Creature actor);
}