package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.actor.instances.player.Faction;
import l2s.gameserver.model.base.FactionType;

public class CharacterFactionDAO
{
  private static final Logger _log = LoggerFactory.getLogger(CharacterFactionDAO.class);
  private static final CharacterFactionDAO _instance = new CharacterFactionDAO();
  private static final String SELECT_QUERY = "SELECT type, progress FROM character_factions WHERE char_id = ?";
  private static final String REPLACE_QUERY = "REPLACE INTO character_factions (char_id,type,progress) VALUES(?,?,?)";
  
  public CharacterFactionDAO() {}
  
  public static CharacterFactionDAO getInstance()
  {
    return _instance;
  }
  
  public Collection<Faction> restore(int objectId)
  {
    Collection<Faction> result = new ArrayList<Faction>();
    
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;
    try
    {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SELECT_QUERY);
      statement.setInt(1, objectId);
      rset = statement.executeQuery();
      while (rset.next())
      {
        FactionType type = FactionType.VALUES[rset.getInt("type")];
        int progress = rset.getInt("progress");
        result.add(new Faction(type, progress));
      }
    }
    catch (Exception e)
    {
      _log.error("CharacterFactionDAO.restore(int): " + e, e);
    }
    finally
    {
      DbUtils.closeQuietly(con, statement, rset);
    }
    return result;
  }
  
  public boolean update(int objectId, Faction faction)
  {
    Connection con = null;
    PreparedStatement statement = null;
    try
    {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(REPLACE_QUERY);
      statement.setInt(1, objectId);
      statement.setInt(2, faction.getType().ordinal());
      statement.setInt(3, faction.getProgress());
      statement.execute();
    }
    catch (Exception e)
    {
      _log.error("CharacterFactionDAO.update(int,Faction): " + e, e);
      return false;
    }
    finally
    {
      DbUtils.closeQuietly(con, statement);
    }
    return true;
  }
}