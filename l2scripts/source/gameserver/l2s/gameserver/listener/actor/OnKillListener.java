package l2s.gameserver.listener.actor;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Creature;

public interface OnKillListener extends CharListener
{
	public void onKill(Creature actor, Creature victim);

	
	public boolean ignorePetOrSummon();
}