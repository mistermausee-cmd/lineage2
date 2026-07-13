package l2s.gameserver.network.l2.s2c;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.instancemanager.ChaosFestivalManager;
import l2s.gameserver.model.CharSelectInfoPackage;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.utils.AutoBan;

public class CharacterSelectionInfoPacket extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterSelectionInfoPacket.class);

	private final String _loginName;
	private final int _sessionId;
	private final CharSelectInfoPackage[] _characterPackages;
	private final boolean _hasPremiumAccount;

	public CharacterSelectionInfoPacket(GameClient client)
	{
		_loginName = client.getLogin();
		_sessionId = client.getSessionKey().playOkID1;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_hasPremiumAccount = client.getPremiumAccountType() > 0 && client.getPremiumAccountExpire() > System.currentTimeMillis() / 1000L;
	}

	public CharSelectInfoPackage[] getCharInfo()
	{
		return _characterPackages;
	}

	@Override
	protected final void writeImpl()
	{
		int size = _characterPackages != null ? _characterPackages.length : 0;		
		
		writeD(size);
		writeD(0x07); 
		writeC(0x00); 
		writeC(0x00);
		writeD(0x02); 
		writeC(0x00); 

		long lastAccess = -1L;
		int lastUsed = -1;
		for(int i = 0; i < size; i++)
			if(lastAccess < _characterPackages[i].getLastAccess())
			{
				lastAccess = _characterPackages[i].getLastAccess();
				lastUsed = i;
			}

		for(int i = 0; i < size; i++)
		{
			CharSelectInfoPackage charInfoPackage = _characterPackages[i];

			writeS(charInfoPackage.getName());
			writeD(charInfoPackage.getCharId()); 
			writeS(_loginName);
			writeD(_sessionId);
			writeD(charInfoPackage.getClanId());
			writeD(0x00); 

			writeD(charInfoPackage.getSex());
			writeD(charInfoPackage.getRace());
			writeD(charInfoPackage.getBaseClassId());

			writeD(Config.REQUEST_ID); 

			writeD(charInfoPackage.getX());
			writeD(charInfoPackage.getY());
			writeD(charInfoPackage.getZ());

			writeF(charInfoPackage.getCurrentHp());
			writeF(charInfoPackage.getCurrentMp());

			writeQ(charInfoPackage.getSp());
			writeQ(charInfoPackage.getExp());
			int lvl = Experience.getLevel(charInfoPackage.getExp());
			writeF(Experience.getExpPercent(lvl, charInfoPackage.getExp()));
			writeD(lvl);

			writeD(charInfoPackage.getKarma());
			writeD(charInfoPackage.getPk());
			writeD(charInfoPackage.getPvP());

			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);

			writeD(0x00); 
			writeD(0x00); 

			for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
				writeD(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));

			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_RHAND)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LHAND)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_GLOVES)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_CHEST)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LEGS)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_FEET)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LRHAND));
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_HAIR)); 
			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_DHAIR)); 

			writeH(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_CHEST)); 
			writeH(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_LEGS)); 
			writeH(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_HEAD)); 
			writeH(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_GLOVES)); 
			writeH(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_FEET)); 

			writeD(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_HAIR) > 0 ? charInfoPackage.getSex() : charInfoPackage.getHairStyle());
			writeD(charInfoPackage.getHairColor());
			writeD(charInfoPackage.getFace());

			writeF(charInfoPackage.getMaxHp()); 
			writeF(charInfoPackage.getMaxMp()); 

			writeD(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
			writeD(charInfoPackage.getClassId());
			writeD(i == lastUsed ? 1 : 0);

			writeC(Math.min(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_RHAND), 127));
			writeD(charInfoPackage.getPaperdollVariation1Id(Inventory.PAPERDOLL_RHAND));
			writeD(charInfoPackage.getPaperdollVariation2Id(Inventory.PAPERDOLL_RHAND));

			int weaponId = charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
			if(weaponId == 8190) 
				writeD(301);
			else if(weaponId == 8689)
				writeD(302);
			else
				writeD(0x00);

			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeF(0x00);
			writeF(0x00);

			writeD(charInfoPackage.getVitalityPoints());
			if(_hasPremiumAccount)
			{
				writeD(charInfoPackage.getVitalityPoints() > 0 ? (int)(100.0 * Config.ALT_VITALITY_PA_RATE) : 100);
		        writeD(Config.ALT_VITALITY_POTIONS_PA_LIMIT - charInfoPackage.getVitalityUsedPotions());
			}
			else
			{
		        writeD(charInfoPackage.getVitalityPoints() > 0 ? (int)(100.0 * Config.ALT_VITALITY_RATE) : 100);
		        writeD(Config.ALT_VITALITY_POTIONS_LIMIT - charInfoPackage.getVitalityUsedPotions());
			}

			writeD(charInfoPackage.isAvailable());
			writeC(ChaosFestivalManager.getInstance().isWinnerReceived(charInfoPackage.getCharId()) ? 100 : 0);
			writeC(charInfoPackage.isHero()); 
			writeC(charInfoPackage.isHairAccessoryEnabled() ? 0x01 : 0x00); 
		}
	}


	public static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName)
	{
		CharSelectInfoPackage charInfopackage;
		List<CharSelectInfoPackage> characterList = new ArrayList<CharSelectInfoPackage>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id AND cs.active=1) WHERE account_name=? LIMIT 7");
			statement.setString(1, loginName);
			rset = statement.executeQuery();
			while(rset.next()) 
			{
				charInfopackage = restoreChar(rset);
				if(charInfopackage != null)
					characterList.add(charInfopackage);
			}
		}
		catch(Exception e)
		{
			_log.error("could not restore charinfo:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
	}

	private static int restoreBaseClassId(int objId)
	{
		int classId = 0;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT default_class_id FROM character_subclasses WHERE char_obj_id=? AND type=" + SubClassType.BASE_CLASS.ordinal());
			statement.setInt(1, objId);
			rset = statement.executeQuery();
			while(rset.next())
			{
				classId = rset.getInt("default_class_id");
			}
		}
		catch(Exception e)
		{
			_log.error("could not restore base class id:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return classId;
	}

	private static CharSelectInfoPackage restoreChar(ResultSet chardata)
	{
		CharSelectInfoPackage charInfopackage = null;
		try
		{
			int objectId = chardata.getInt("obj_Id");
			int classid = chardata.getInt("class_id");
			int baseClassId = chardata.getInt("default_class_id");
			boolean useBaseClass = chardata.getInt("type") == SubClassType.BASE_CLASS.ordinal();
			if(!useBaseClass)
				baseClassId = restoreBaseClassId(objectId);

			Race race = ClassId.VALUES[baseClassId].getRace();
			if(race == null)
			{
				_log.warn(CharacterSelectionInfoPacket.class.getSimpleName() + ": Race was not found for the class id: " + baseClassId);
				return null;
			}

			String name = chardata.getString("char_name");
			charInfopackage = new CharSelectInfoPackage(objectId, name);
			charInfopackage.setMaxHp(chardata.getInt("maxHp"));
			charInfopackage.setCurrentHp(chardata.getDouble("curHp"));
			charInfopackage.setMaxMp(chardata.getInt("maxMp"));
			charInfopackage.setCurrentMp(chardata.getDouble("curMp"));

			charInfopackage.setX(chardata.getInt("x"));
			charInfopackage.setY(chardata.getInt("y"));
			charInfopackage.setZ(chardata.getInt("z"));
			charInfopackage.setPk(chardata.getInt("pkkills"));
			charInfopackage.setPvP(chardata.getInt("pvpkills"));

			int face = chardata.getInt("beautyFace");
			charInfopackage.setFace(face > 0 ? face : chardata.getInt("face"));

			int hairstyle = chardata.getInt("beautyHairstyle");
			charInfopackage.setHairStyle(hairstyle > 0 ? hairstyle : chardata.getInt("hairstyle"));

			int haircolor = chardata.getInt("beautyHaircolor");
			charInfopackage.setHairColor(haircolor > 0 ? haircolor : chardata.getInt("haircolor"));

			charInfopackage.setSex(chardata.getInt("sex"));

			charInfopackage.setExp(chardata.getLong("exp"));
			charInfopackage.setSp(chardata.getLong("sp"));
			charInfopackage.setClanId(chardata.getInt("clanid"));

			charInfopackage.setKarma(chardata.getInt("karma"));
			charInfopackage.setRace(race.ordinal());
			charInfopackage.setClassId(classid);
			charInfopackage.setBaseClassId(baseClassId);
			long deletetime = chardata.getLong("deletetime");
			int deletehours = 0;
			if(Config.CHARACTER_DELETE_AFTER_HOURS > 0)
				if(deletetime > 0)
				{
					deletetime = (int) (System.currentTimeMillis() / 1000 - deletetime);
					deletehours = (int) (deletetime / 3600);
					if(deletehours >= Config.CHARACTER_DELETE_AFTER_HOURS)
					{
						CharacterDAO.getInstance().deleteCharByObjId(objectId);
						return null;
					}
					deletetime = Config.CHARACTER_DELETE_AFTER_HOURS * 3600 - deletetime;
				}
				else
					deletetime = 0;
			charInfopackage.setDeleteTimer((int) deletetime);
			charInfopackage.setLastAccess(chardata.getLong("lastAccess") * 1000L);
			charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));
			charInfopackage.setVitalityPoints(chardata.getInt("vitality"));
		    charInfopackage.setVitalityUsedPotions(chardata.getInt("used_vitality_potions"));
			charInfopackage.setHairAccessoryEnabled(chardata.getInt("hide_head_accessories") == 0);

			if(charInfopackage.getAccessLevel() < 0 && !AutoBan.isBanned(objectId))
				charInfopackage.setAccessLevel(0);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		return charInfopackage;
	}
}