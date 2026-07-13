package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Mentee;


public class MentoringDAO
{
	private static final Logger _log = LoggerFactory.getLogger(MentoringDAO.class);

	private static final MentoringDAO _instance = new MentoringDAO();

	
	private static final String menteeList = "SELECT m.mentor AS charid, c.char_name, s.class_id, s.level FROM character_mentoring m LEFT JOIN characters c ON m.mentor = c.obj_Id LEFT JOIN character_subclasses s ON ( m.mentor = s.char_obj_id AND s.active =1 ) WHERE m.mentee = ?";
	
	private static final String mentorList = "SELECT m.mentee AS charid, c.char_name, s.class_id, s.level FROM character_mentoring m LEFT JOIN characters c ON m.mentee = c.obj_Id LEFT JOIN character_subclasses s ON ( m.mentee = s.char_obj_id AND s.active =1 ) WHERE m.mentor = ?";

	public static MentoringDAO getInstance()
	{
		return _instance;
	}

	public TIntObjectHashMap<Mentee> selectMenteeList(Player listOwner)
	{
		TIntObjectHashMap<Mentee> map = new TIntObjectHashMap<Mentee>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement(mentorList);
			statement.setInt(1, listOwner.getObjectId());
			rset = statement.executeQuery();
            boolean foundMentee = false;
			while(rset.next())
			{
                foundMentee = true;
				int objectId = rset.getInt("charid");
				String name = rset.getString("c.char_name");
				int classId = rset.getInt("s.class_id");
				int level = rset.getInt("s.level");

				map.put(objectId, new Mentee(objectId, name, classId, level, false));
			}
            if(!foundMentee)
            {
                statement = con.prepareStatement(menteeList);
                statement.setInt(1, listOwner.getObjectId());
                rset = statement.executeQuery();
                while(rset.next())
                {
                    int objectId = rset.getInt("charid");
                    String name = rset.getString("c.char_name");
                    int classId = rset.getInt("s.class_id");
                    int level = rset.getInt("s.level");
                    if(listOwner.getObjectId() != objectId) {
                        map.put(objectId, new Mentee(objectId, name, classId, level, true));
                        break;
                    }
                }
            }
		}
		catch(Exception e)
		{
			_log.error("MentoringDAO.load(L2Player): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return map;
	}

	public void insert(Player mentor, Player mentee)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO character_mentoring (mentor,mentee) VALUES(?,?)");
			statement.setInt(1, mentor.getObjectId());
			statement.setInt(2, mentee.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn(mentor.getMenteeList() + " could not add mentee objectid: " + mentee.getObjectId(), e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void delete(int mentor, int mentee)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_mentoring WHERE mentor=? AND mentee=?");
			statement.setInt(1, mentor);
			statement.setInt(2, mentee);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warn("MenteeList: could not delete mentee objectId: " + mentee + " mentorId: " + mentor, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}