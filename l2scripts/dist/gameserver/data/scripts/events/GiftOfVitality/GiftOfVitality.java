package events.GiftOfVitality;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author n0nam3, pchayka
 * @date 12/10/2010 20:06
 * 
 * http://www.lineage2.com/archive/2010/06/gift_of_vitalit.html
 *
 */
public class GiftOfVitality implements OnInitScriptListener
{
	private static final String EVENT_NAME = "GiftOfVitality";
	private static final int REUSE_HOURS = 24; // reuse
	private static final int EVENT_MANAGER_ID = 109; // npc id
	private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
	private static final Logger _log = LoggerFactory.getLogger(GiftOfVitality.class);

	private final static int[][] _mageBuff = new int[][] { { 5627, 1 }, // windwalk
			{ 5628, 1 }, // shield
			{ 5637, 1 }, // Magic Barrier 1
			{ 5633, 1 }, // blessthesoul
			{ 5634, 1 }, // acumen
			{ 5635, 1 }, // concentration
			{ 5636, 1 }, // empower
	};

	private final static int[][] _warrBuff = new int[][] { { 5627, 1 }, // windwalk
			{ 5628, 1 }, // shield
			{ 5637, 1 }, // Magic Barrier 1
			{ 5629, 1 }, // btb
			{ 5630, 1 }, // vampirerage
			{ 5631, 1 }, // regeneration
			{ 5632, 1 }, // haste 2
	};

	private final static int[][] _summonBuff = new int[][] { { 5627, 1 }, // windwalk
			{ 5628, 1 }, // shield
			{ 5637, 1 }, // Magic Barrier 1
			{ 5629, 1 }, // btb
			{ 5633, 1 }, // vampirerage
			{ 5630, 1 }, // regeneration
			{ 5634, 1 }, // blessthesoul
			{ 5631, 1 }, // acumen
			{ 5635, 1 }, // concentration
			{ 5632, 1 }, // empower
			{ 5636, 1 }, // haste 2
	};

	public enum BuffType
	{
		PLAYER,
		SUMMON,
		VITALITY,
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -119494, 44882, 360, 24576)); //Kamael Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -84023, 243051, -3728, 4096)); //Talking Island Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 45538, 48357, -3056, 18000)); //Elven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 9929, 16324, -4568, 62999)); //Dark Elven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 115096, -178370, -880, 0)); //Dwarven Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -45372, -114104, -240, 16384)); //Orc Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -83156, 150994, -3120, 0)); //Gludin
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, -13727, 122117, -2984, 16384)); //Gludio
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 16111, 142850, -2696, 16000)); //Dion
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 111004, 218928, -3536, 16384)); //Heine
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 82145, 148609, -3464, 0)); //Giran
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 81083, 56118, -1552, 32768)); //Oren
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 117356, 76708, -2688, 49151)); //Hunters Village
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147200, 25614, -2008, 16384)); //Aden
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 43966, -47709, -792, 49999)); //Rune
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 147421, -55435, -2728, 49151)); //Goddart
		_spawns.add(NpcUtils.spawnSingle(EVENT_MANAGER_ID, 85584, -142490, -1336, 0)); //Schutgard
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(NpcInstance npc : _spawns)
			npc.deleteMe();
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return Functions.IsActive(EVENT_NAME);
	}

	/**
	 * Запускает эвент
	 */
	@Bypass("events.GiftOfVitality.GiftOfVitality:startEvent")
	public void startEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event: 'Gift Of Vitality' started.");
			Announcements.announceToAllFromStringHolder("scripts.events.GiftOfVitality.AnnounceEventStarted");
		}
		else
			player.sendMessage("Event 'Gift Of Vitality' already started.");
	}

	/**
	 * Останавливает эвент
	 */
	@Bypass("events.GiftOfVitality.GiftOfVitality:stopEvent")
	public void stopEvent(Player player, NpcInstance npc, String[] param)
	{
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(Functions.SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			System.out.println("Event: 'Gift Of Vitality' stopped.");
			Announcements.announceToAllFromStringHolder("scripts.events.GiftOfVitality.AnnounceEventStoped");
		}
		else
			player.sendMessage("Event: 'Gift Of Vitality' not started.");
	}

	@Override
	public void onInit()
	{
		if(isActive())
		{
			spawnEventManagers();
			_log.info("Loaded Event: Gift Of Vitality [state: activated]");
		}
		else
			_log.info("Loaded Event: Gift Of Vitality [state: deactivated]");
	}

	private void buffMe(Player player, NpcInstance npc, BuffType type)
	{
		if(!player.checkInteractionDistance(npc))
			return;

		String htmltext = null;
		String var = player.getVar("govEventTime");

		switch(type)
		{
			case VITALITY:
				if(var != null && Long.parseLong(var) > System.currentTimeMillis() || !player.isBaseClassActive())
					htmltext = "jack-notime.htm";
				else
				{
					npc.broadcastPacket(new MagicSkillUse(npc, player, 23179, 1, 0, 0));
					player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(23179, 1));
					player.setVar("govEventTime", String.valueOf(System.currentTimeMillis() + REUSE_HOURS * 60 * 60 * 1000L), -1);
					player.setVitality(Player.MAX_VITALITY_POINTS);
					htmltext = "jack-okvitality.htm";
				}
				break;
			case SUMMON:
				if(player.getLevel() < 76)
					htmltext = "jack-nolevel.htm";
				else if(!player.hasSummon())
					htmltext = "jack-nosummon.htm";
				else
				{
					for(Servitor s : player.getServitors())
					{
						for(int[] buff : _summonBuff)
						{
							npc.broadcastPacket(new MagicSkillUse(npc, s, buff[0], buff[1], 0, 0));
							player.altOnMagicUse(s, SkillHolder.getInstance().getSkill(buff[0], buff[1]));
						}
					}
					htmltext = "jack-okbuff.htm";
				}
				break;
			case PLAYER:
				if(player.getLevel() < 76)
					htmltext = "jack-nolevel.htm";
				else
				{
					if(!player.isMageClass() || player.getTemplate().getRace() == Race.ORC)
						for(int[] buff : _warrBuff)
						{
							npc.broadcastPacket(new MagicSkillUse(npc, player, buff[0], buff[1], 0, 0));
							player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(buff[0], buff[1]));
						}
					else
						for(int[] buff : _mageBuff)
						{
							npc.broadcastPacket(new MagicSkillUse(npc, player, buff[0], buff[1], 0, 0));
							player.altOnMagicUse(player, SkillHolder.getInstance().getSkill(buff[0], buff[1]));
						}
					htmltext = "jack-okbuff.htm";
				}
				break;
		}
		Functions.show("scripts/events/GiftOfVitality/" + htmltext, player);
	}

	@Bypass("events.GiftOfVitality.GiftOfVitality:buffVitality")
	public void buffVitality(Player player, NpcInstance npc, String[] param)
	{
		buffMe(player, npc, BuffType.VITALITY);
	}

	@Bypass("events.GiftOfVitality.GiftOfVitality:buffSummon")
	public void buffSummon(Player player, NpcInstance npc, String[] param)
	{
		buffMe(player, npc, BuffType.SUMMON);
	}

	@Bypass("events.GiftOfVitality.GiftOfVitality:buffPlayer")
	public void buffPlayer(Player player, NpcInstance npc, String[] param)
	{
		buffMe(player, npc, BuffType.PLAYER);
	}
}