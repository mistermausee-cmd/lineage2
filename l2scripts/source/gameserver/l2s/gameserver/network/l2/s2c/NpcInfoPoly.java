package l2s.gameserver.network.l2.s2c;	

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.templates.npc.NpcTemplate;

public class NpcInfoPoly extends L2GameServerPacket
{
	
	private Creature _obj;
	private int _x, _y, _z, _heading;
	private int _npcId;
	private boolean _isSummoned, _isRunning, _isInCombat, _isAlikeDead;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand;
	private String _name, _title;
	private int _abnormalEffect, _abnormalEffect2;
	private double colRadius, colHeight;
	private TeamType _team;

	public NpcInfoPoly(Player cha)
	{
		_obj = cha;
		_npcId = cha.getPolyId();
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = false;
		colRadius = template.getCollisionRadius();
		colHeight = template.getCollisionHeight();
		_x = _obj.getX();
		_y = _obj.getY();
		_z = _obj.getZ();
		_rhand = template.rhand;
		_lhand = template.lhand;
		_heading = cha.getHeading();
		_mAtkSpd = cha.getMAtkSpd();
		_pAtkSpd = cha.getPAtkSpd();
		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
		_isRunning = cha.isRunning();
		_isInCombat = cha.isInCombat();
		_isAlikeDead = cha.isAlikeDead();
		_name = cha.getName();
		_title = cha.getTitle();
		_abnormalEffect = 0;
		_abnormalEffect2 = 0;
		_team = cha.getTeam();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_obj.getObjectId());
		writeD(_npcId + 1000000); 
		writeD(0x00);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0x00);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd); 
		writeD(_swimWalkSpd); 
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(1);
		writeF(1);
		writeF(colRadius);
		writeF(colHeight);
		writeD(_rhand); 
		writeD(0);
		writeD(_lhand); 
		writeC(1); 
		writeC(_isRunning ? 1 : 0);
		writeC(_isInCombat ? 1 : 0);
		writeC(_isAlikeDead ? 1 : 0);
		writeC(_isSummoned ? 2 : 0); 
		writeS(_name);
		writeS(_title);
		writeD(0);
		writeD(0);
		writeD(0000); 

		writeD(_abnormalEffect);

		writeD(0000); 
		writeD(0000); 
		writeD(0000); 
		writeD(0000); 
		writeC(0000); 
		writeC(_team.ordinal());
		writeF(colRadius); 
		writeF(colHeight); 
		writeD(0x00); 
		writeD(0x00); 
		writeD(0x00);
		writeD(0x00); 

		writeC(0x00); 
		writeC(0x00); 
		writeD(_abnormalEffect2);
	}
}