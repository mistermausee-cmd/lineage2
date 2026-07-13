package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.napile.primitive.maps.IntLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

public class ChaosFestivalDAO
{
    private static final Logger _log = LoggerFactory.getLogger(ChaosFestivalDAO.class);
    private static final ChaosFestivalDAO _instance = new ChaosFestivalDAO();
    public static final String SELECT_SQL_QUERY = "SELECT obj_id, points FROM chaos_festival_statistic";
    public static final String DELETE_SQL_QUERY = "DELETE FROM chaos_festival_statistic";
    public static final String INSERT_SQL_QUERY = "REPLACE INTO chaos_festival_statistic(obj_id, points) VALUES (?,?)";
    
    public static ChaosFestivalDAO getInstance()
    {
        return _instance;
    }
    
    public void restore(IntLongMap map)
    {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            rset = statement.executeQuery();
            while(rset.next())
                map.put(rset.getInt("obj_id"), rset.getLong("points"));
        }
        catch (Exception e)
        {
            _log.info("ChaosFestivalDAO.restore(IntLongMap): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }
    
    public void clear()
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.execute();
        }
        catch (Exception e)
        {
            _log.info("ChaosFestivalDAO.clear(): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }
    
    public void insert(int objectId, long points)
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, objectId);
            statement.setLong(2, points);
            statement.execute();
        }
        catch (Exception e)
        {
            _log.info("ChaosFestivalDAO.insert(int,long): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }
}