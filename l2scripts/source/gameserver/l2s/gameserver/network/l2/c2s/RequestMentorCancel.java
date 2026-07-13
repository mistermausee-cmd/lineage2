package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.dao.MentoringDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.s2c.ExMentorList;
import l2s.gameserver.utils.Mentoring;


public class RequestMentorCancel extends L2GameClientPacket
{
	private int _mtype;
	private String _charName;

	@Override
	protected void readImpl()
	{
		_mtype = readD(); 
		_charName = readS(); 
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.getMenteeList().remove(_charName, _mtype == 1, true);
		activeChar.sendPacket(new ExMentorList(activeChar));

		final Player menteeChar = World.getPlayer(_charName);
		if(menteeChar != null && menteeChar.isOnline())
		{
			menteeChar.getMenteeList().remove(activeChar.getName(), _mtype != 1, false);
			menteeChar.sendPacket(new ExMentorList(menteeChar));
		}
		else
		{
			final int targetObjectId = CharacterDAO.getInstance().getObjectIdByName(_charName);
			if(targetObjectId > 0)
				MentoringDAO.getInstance().delete(targetObjectId, activeChar.getObjectId());
		}

		Mentoring.applyMentoringCond(activeChar, false);
		Mentoring.setTimePenalty(_mtype == 1 ? activeChar.getObjectId() : activeChar.getMenteeList().getMentor(), System.currentTimeMillis() + 2 * 24 * 3600 * 1000L, -1);
	}
}