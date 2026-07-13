package l2s.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.dao.CustomHeroDAO;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.HtmlUtils;

public class Hero
{
	private static final Logger _log = LoggerFactory.getLogger(Hero.class);

	private static Hero _instance;
	private static final String GET_HEROES = "SELECT h.char_id AS char_id, h.count AS count, h.active AS active, c.char_name AS char_name, cs.class_id AS class_id FROM heroes AS h LEFT JOIN characters AS c ON c.obj_Id = h.char_id LEFT JOIN character_subclasses AS cs ON cs.char_obj_id = h.char_id AND cs.type=? WHERE char_name IS NOT NULL AND class_id IS NOT NULL AND played = 1";
	private static final String GET_ALL_HEROES = "SELECT h.char_id AS char_id, h.count AS count, h.active AS active, h.played AS played, c.char_name AS char_name, cs.class_id AS class_id FROM heroes AS h LEFT JOIN characters AS c ON c.obj_Id = h.char_id LEFT JOIN character_subclasses AS cs ON cs.char_obj_id = h.char_id AND cs.type=? WHERE char_name IS NOT NULL AND class_id IS NOT NULL";

	private static IntObjectMap<StatsSet> _heroes;
	private static IntObjectMap<StatsSet> _completeHeroes;

	private static IntObjectMap<List<HeroDiary>> _herodiary;
	private static IntObjectMap<String> _heroMessage;

	public static final String CHAR_ID = "char_id";
	public static final String CLASS_ID = "class_id";
	public static final String CHAR_NAME = "char_name";
	public static final String COUNT = "count";
	public static final String PLAYED = "played";
	public static final String CLAN_NAME = "clan_name";
	public static final String CLAN_CREST = "clan_crest";
	public static final String ALLY_NAME = "ally_name";
	public static final String ALLY_CREST = "ally_crest";
	public static final String ACTIVE = "active";
	public static final String MESSAGE = "message";

	public static Hero getInstance()
	{
		if(_instance == null)
			_instance = new Hero();
		return _instance;
	}

	public Hero()
	{
		init();
	}

	private static void HeroSetClanAndAlly(int charId, StatsSet hero)
	{
		Entry<Clan, Alliance> e = ClanTable.getInstance().getClanAndAllianceByCharId(charId);
		hero.set(CLAN_CREST, e.getKey() == null ? 0 : e.getKey().getCrestId());
		hero.set(CLAN_NAME, e.getKey() == null ? "" : e.getKey().getName());
		hero.set(ALLY_CREST, e.getValue() == null ? 0 : e.getValue().getAllyCrestId());
		hero.set(ALLY_NAME, e.getValue() == null ? "" : e.getValue().getAllyName());
		e = null;
	}

	private void init()
	{
		_heroes = new CHashIntObjectMap<StatsSet>();
		_completeHeroes = new CHashIntObjectMap<StatsSet>();
		_herodiary = new CHashIntObjectMap<List<HeroDiary>>();
		_heroMessage = new CHashIntObjectMap<String>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(GET_HEROES);
			statement.setInt(1, SubClassType.BASE_CLASS.ordinal());
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatsSet hero = new StatsSet();
				int charId = rset.getInt(CHAR_ID);
				hero.set(CHAR_NAME, rset.getString(CHAR_NAME));
				hero.set(CLASS_ID, Olympiad.convertParticipantClassId(rset.getInt(CLASS_ID)));
				hero.set(COUNT, rset.getInt(COUNT));
				hero.set(PLAYED, 1);
				hero.set(ACTIVE, rset.getInt(ACTIVE));
				HeroSetClanAndAlly(charId, hero);
				loadDiary(charId);
				loadMessage(charId);
				_heroes.put(charId, hero);
			}
			DbUtils.close(statement, rset);

			statement = con.prepareStatement(GET_ALL_HEROES);
			statement.setInt(1, SubClassType.BASE_CLASS.ordinal());
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatsSet hero = new StatsSet();
				int charId = rset.getInt(CHAR_ID);
				hero.set(CHAR_NAME, rset.getString(CHAR_NAME));
				hero.set(CLASS_ID, Olympiad.convertParticipantClassId(rset.getInt(CLASS_ID)));
				hero.set(COUNT, rset.getInt(COUNT));
				hero.set(PLAYED, rset.getInt(PLAYED));
				hero.set(ACTIVE, rset.getInt(ACTIVE));
				HeroSetClanAndAlly(charId, hero);
				_completeHeroes.put(charId, hero);
			}
		}
		catch(SQLException e)
		{
			_log.warn("Hero System: Couldnt load Heroes", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		_log.info("Hero System: Loaded " + _heroes.size() + " Heroes.");
		_log.info("Hero System: Loaded " + _completeHeroes.size() + " all time Heroes.");
	}

	public IntObjectMap<StatsSet> getHeroes()
	{
		return _heroes;
	}

	public synchronized void clearHeroes()
	{
		mysql.set("UPDATE heroes SET played = 0, active = 0");

		for(IntObjectPair<StatsSet> entry : _heroes.entrySet())
		{
			if(entry.getValue().getInteger(ACTIVE) == 0)
				continue;

			Player player = GameObjectsStorage.getPlayer(entry.getKey());

			if(player != null)
			{
				player.setHero(CustomHeroDAO.getInstance().isCustomHero(player.getObjectId()));
				player.checkAndDeleteOlympiadItems();
				player.updatePledgeRank();
				player.broadcastUserInfo(true);
			}
		}

		_heroes.clear();
		_herodiary.clear();
	}

	public synchronized boolean computeNewHeroes(List<StatsSet> newHeroes)
	{
		if(newHeroes.size() == 0)
			return true;

		IntObjectMap<StatsSet> heroes = new CHashIntObjectMap<StatsSet>();

		for(StatsSet hero : newHeroes)
		{
			int charId = hero.getInteger(CHAR_ID);

			if(_completeHeroes != null && _completeHeroes.containsKey(charId))
			{
				StatsSet oldHero = _completeHeroes.get(charId);
				int count = oldHero.getInteger(COUNT);
				oldHero.set(COUNT, count + 1);
				oldHero.set(PLAYED, 1);
				oldHero.set(ACTIVE, 0);

				heroes.put(charId, oldHero);
			}
			else
			{
				StatsSet newHero = new StatsSet();
				newHero.set(CHAR_NAME, hero.getString(CHAR_NAME));
				newHero.set(CLASS_ID, hero.getInteger(CLASS_ID));
				newHero.set(COUNT, 1);
				newHero.set(PLAYED, 1);
				newHero.set(ACTIVE, 0);

				heroes.put(charId, newHero);
			}

			addHeroDiary(charId, HeroDiary.ACTION_HERO_GAINED, 0);
			loadDiary(charId);
		}

		_heroes.putAll(heroes);
		heroes.clear();

		updateHeroes(0);

		return false;
	}

	public void updateHeroes(int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO heroes (char_id, count, played, active) VALUES (?,?,?,?)");

			for(int heroId : _heroes.keySet().toArray())
			{
				if(id > 0 && heroId != id)
					continue;
				StatsSet hero = _heroes.get(heroId);
				statement.setInt(1, heroId);
				statement.setInt(2, hero.getInteger(COUNT));
				statement.setInt(3, hero.getInteger(PLAYED));
				statement.setInt(4, hero.getInteger(ACTIVE));
				statement.execute();
				if(_completeHeroes != null && !_completeHeroes.containsKey(heroId))
				{
					HeroSetClanAndAlly(heroId, hero);
					_completeHeroes.put(heroId, hero);
				}
			}
		}
		catch(SQLException e)
		{
			_log.warn("Hero System: Couldnt update Heroes");
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public boolean isHero(int id)
	{
		if(_heroes == null || _heroes.isEmpty())
			return false;
		if(_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 1)
			return true;
		return false;
	}

	public boolean isInactiveHero(int id)
	{
		if(_heroes == null || _heroes.isEmpty())
			return false;
		if(_heroes.containsKey(id) && _heroes.get(id).getInteger(ACTIVE) == 0)
			return true;
		return false;
	}

	public void activateHero(Player player)
	{
		StatsSet hero = _heroes.get(player.getObjectId());
		if(hero == null)
			return;

		hero.set(ACTIVE, 1);

		player.checkHeroSkills();
		player.setHero(true);
		player.updatePledgeRank();
		player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.GIVE_HERO));	

		switch (ClassId.VALUES[player.getBaseClassId()])
		{
        	case SIGEL_PHOENIX_KNIGHT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_PHOENIX_KNIGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case SIGEL_HELL_KNIGHT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_HELL_KNIGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case SIGEL_EVAS_TEMPLAR:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_EVAS_TEMPLAR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case SIGEL_SHILLIEN_TEMPLAR:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_SHILLIEN_TEMPLAR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_DUELIST:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DUELIST_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_DREADNOUGHT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DREADNOUGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_TITAN:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_TITAN_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_GRAND_KHAVATARI:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_GRAND_KHAVATARI_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_MAESTRO:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_MAESTRO_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case TYR_DOOMBRINGER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DOOMBRINGER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case OTHELL_ADVENTURER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_ADVENTURER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case OTHELL_WIND_RIDER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_WIND_RIDER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case OTHELL_GHOST_HUNTER: {
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_GHOST_HUNTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case OTHELL_FORTUNE_SEEKER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_FORTUNE_SEEKER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case YR_SAGITTARIUS:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_SAGITTARIUS_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case YR_MOONLIGHT_SENTINEL:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_MOONLIGHT_SENTINEL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case YR_GHOST_SENTINEL:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_GHOST_SENTINEL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case YR_TRICKSTER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_TRICKSTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case FEOH_ARCHMAGE:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_ARCHMAGE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case FEOH_SOULTAKER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_SOULTAKER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case FEOH_MYSTIC_MUSE:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_MYSTIC_MUSE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case FEOH_STORM_SCREAMER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_STORM_SCREAMER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case FEOH_SOUL_HOUND:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_SOUL_HOUND_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case ISS_HIEROPHANT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_HIEROPHANT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case ISS_SWORD_MUSE:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_SWORD_MUSE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case ISS_SPECTRAL_DANCER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_SPECTRAL_DANCER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case ISS_DOMINATOR:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_DOMINATOR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case ISS_DOOMCRYER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_DOOMCRYER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case WYNN_ARCANA_LORD:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_ARCANA_LORD_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case WYNN_ELEMENTAL_MASTER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_ELEMENTAL_MASTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case WYNN_SPECTRAL_MASTER:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_SPECTRAL_MASTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case AEORE_CARDINAL:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_CARDINAL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case AEORE_EVAS_SAINT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_EVAS_SAINT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        		break;
        	}
        	case AEORE_SHILLIEN_SAINT:
        	{
        		Announcements.announceToAll(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_SHILLIEN_SAINT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, true, player.getName()));
        	}
		}

		if(player.getClan() != null && player.getClan().getLevel() >= 5)
		{
			player.getClan().incReputation(5000, true, "Hero:activateHero:" + player);
			player.getClan().broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_WAS_NAMED_A_HERO_2S_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addString(player.getName()).addNumber(Math.round(5000 * Config.RATE_CLAN_REP_SCORE)), player);
		}
		player.broadcastUserInfo(true);
		updateHeroes(player.getObjectId());
	}

	public void loadDiary(int charId)
	{
		List<HeroDiary> diary = new ArrayList<HeroDiary>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM  heroes_diary WHERE charId=? ORDER BY time ASC");
			statement.setInt(1, charId);
			rset = statement.executeQuery();

			while(rset.next())
			{
				long time = rset.getLong("time");
				int action = rset.getInt("action");
				int param = rset.getInt("param");

				HeroDiary d = new HeroDiary(action, time, param);
				diary.add(d);
			}

			_herodiary.put(charId, diary);

			if(Config.DEBUG)
				_log.info("Hero System: Loaded " + diary.size() + " diary entries for Hero(object id: #" + charId + ")");
		}
		catch(SQLException e)
		{
			_log.warn("Hero System: Couldnt load Hero Diary for CharId: " + charId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void showHeroDiary(Player activeChar, int heroclass, int charid, int page)
	{
		StatsSet hero = _heroes.get(charid);
		if(hero == null)
			return;

		final int perpage = 10;

		List<HeroDiary> mainlist = _herodiary.get(charid);

		if(mainlist != null)
		{
			HtmlMessage html = new HtmlMessage(5);
			html.setFile("olympiad/monument_hero_info.htm");
			html.replace("%title%", StringsHolder.getInstance().getString(activeChar, "hero.diary"));
			html.replace("%heroname%", hero.getString(CHAR_NAME));
			String message = _heroMessage.get(charid);
			html.replace("%message%", message == null ? "" : message);

			List<HeroDiary> list = new ArrayList<HeroDiary>(mainlist);

			Collections.reverse(list);

			boolean color = true;
			final StringBuilder fList = new StringBuilder(500);
			int counter = 0;
			int breakat = 0;
			for(int i = (page - 1) * perpage; i < list.size(); i++)
			{
				breakat = i;
				HeroDiary diary = list.get(i);
				Map.Entry<String, String> entry = diary.toString(activeChar);

				fList.append("<tr><td>");
				if(color)
					fList.append("<table width=270 bgcolor=\"131210\">");
				else
					fList.append("<table width=270>");
				fList.append("<tr><td width=270><font color=\"LEVEL\">" + entry.getKey() + "</font></td></tr>");
				fList.append("<tr><td width=270>" + entry.getValue() + "</td></tr>");
				fList.append("<tr><td>&nbsp;</td></tr></table>");
				fList.append("</td></tr>");
				color = !color;
				counter++;
				if(counter >= perpage)
					break;
			}

			if(breakat < list.size() - 1)
			{
				html.replace("%buttprev%", HtmlUtils.PREV_BUTTON);
				html.replace("%prev_bypass%", "_diary?class=" + heroclass + "&page=" + (page + 1));
			}
			else
				html.replace("%buttprev%", StringUtils.EMPTY);

			if(page > 1)
			{
				html.replace("%buttnext%", HtmlUtils.NEXT_BUTTON);
				html.replace("%next_bypass%", "_diary?class=" + heroclass + "&page=" + (page - 1));
			}
			else
				html.replace("%buttnext%", StringUtils.EMPTY);

			html.replace("%list%", fList.toString());

			activeChar.sendPacket(html);
		}
	}

	public void addHeroDiary(int playerId, int id, int param)
	{
		insertHeroDiary(playerId, id, param);

		List<HeroDiary> list = _herodiary.get(playerId);
		if(list != null)
			list.add(new HeroDiary(id, System.currentTimeMillis(), param));
	}

	private void insertHeroDiary(int charId, int action, int param)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO heroes_diary (charId, time, action, param) values(?,?,?,?)");
			statement.setInt(1, charId);
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, action);
			statement.setInt(4, param);
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.error("SQL exception while saving DiaryData.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void loadMessage(int charId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			String message = null;
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT message FROM heroes WHERE char_id=?");
			statement.setInt(1, charId);
			rset = statement.executeQuery();
			rset.next();
			message = rset.getString("message");
			_heroMessage.put(charId, message);
		}
		catch(SQLException e)
		{
			_log.error("Hero System: Couldnt load Hero Message for CharId: " + charId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void setHeroMessage(int charId, String message)
	{
		_heroMessage.put(charId, message);
	}

	public void saveHeroMessage(int charId)
	{
		if(_heroMessage.get(charId) == null)
			return;

		Connection con = null;
		PreparedStatement statement = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE heroes SET message=? WHERE char_id=?;");
			statement.setString(1, _heroMessage.get(charId));
			statement.setInt(2, charId);
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.error("SQL exception while saving HeroMessage.", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void shutdown()
	{
		for(int charId : _heroMessage.keySet().toArray())
			saveHeroMessage(charId);
	}

	public int getHeroByClass(int classid)
	{
		if(!_heroes.isEmpty())
			for(int heroId : _heroes.keySet().toArray())
			{
				StatsSet hero = _heroes.get(heroId);
				if(hero.getInteger(CLASS_ID) == classid)
					return heroId;
			}
		return 0;
	}

	public IntObjectPair<StatsSet> getHeroStats(int classId)
	{
		for(IntObjectPair<StatsSet> entry : _heroes.entrySet())
		{
			if(entry.getValue().getInteger(CLASS_ID) == classId)
				return entry;
		}
		return null;
	}
}