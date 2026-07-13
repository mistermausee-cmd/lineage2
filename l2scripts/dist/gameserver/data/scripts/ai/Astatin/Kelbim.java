package ai.Astatin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bosses.KelbimManager;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.*;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

//TODO Смену внешнего вида когда кельвин меняет стойку. Вероятно из за этого отсутствует анимация у скилов 23685 23686

public class Kelbim extends DefaultAI
{
	// debuffs
	final Skill s_fear = getSkill(23684, 1), s_wave = getSkill(23685, 1), s_shtorm = getSkill(23686, 1), s_shok = getSkill(23695, 1), s_shok2 = getSkill(23696, 1), s_storm = getSkill(23698, 1);

	// damage skills
	final Skill s_fatal = getSkill(23681, 1), s_black = getSkill(23682, 1), s_blade = getSkill(23683, 1);

	// buff skills
	final Skill s_phisdef = getSkill(23689, 1), s_mdef = getSkill(23690, 1), s_celestial = getSkill(23691, 1), s_fury = getSkill(23688, 1), s_reflect = getSkill(23687, 1);

	//StageChange skills
	final Skill s_stage2 = getSkill(23702, 1), s_stage3 = getSkill(23703, 1), s_stage4 = getSkill(23704, 1);

	// Vars
	private int _hpStage = 0;
	private static long _minionsSpawnDelay = 0;
	private List<NpcInstance> minions = new ArrayList<NpcInstance>();
	private int DAMAGE_COUNTER = 0;
	private int MINIONS_COUNT = 4;
	private int MASS_ATTACK_COUNT = 4;
	private boolean guard = false;
	private static long _massattackSpawnDelay = 0;
	private static long _guardSpawnDelay = 0;
	private static long _notOrdinalattackDelay = 0;
	private static long _movetome = 0;

	public Kelbim(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(DAMAGE_COUNTER == 0)
			actor.getAI().startAITask();
		KelbimManager.setLastAttackTime();
		for(Playable p : KelbimManager.getZone().getInsidePlayables())
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);
		DAMAGE_COUNTER++;	
		super.onEvtAttacked(attacker, skill, damage);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_minionsSpawnDelay = System.currentTimeMillis() + 6300L;
		_massattackSpawnDelay= System.currentTimeMillis() + 30000L;
		_guardSpawnDelay = System.currentTimeMillis() + 125000L;
		_notOrdinalattackDelay = System.currentTimeMillis() + 60000L;
		_movetome = System.currentTimeMillis() + 4000L;
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		double distance = actor.getDistance(target);

		// Buffs and stats
		double chp = actor.getCurrentHpPercents();
		if(_hpStage == 0)
		{
			_hpStage = 1;
			MASS_ATTACK_COUNT = 4;
			MINIONS_COUNT = 4;
			KelbimManager.setKelbimStage(1);
		}
		else if(chp < 75 && _hpStage == 1)
		{
			actor.altOnMagicUse(actor, s_stage2);
			_hpStage = 2;
			KelbimManager.setKelbimStage(2);
			MINIONS_COUNT = 5;
			MASS_ATTACK_COUNT = 8;
			actor.altOnMagicUse(actor, s_fury);

			for(Player player : KelbimManager.getPlayersInside())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.KELBIM_HAS_TRANSFORMED_TO_BECOME_EVEN_STRONGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			}
		}
		else if(chp < 50 && _hpStage == 2)
		{
			actor.altOnMagicUse(actor, s_stage3);
			_hpStage = 3;
			MINIONS_COUNT = 6;
			MASS_ATTACK_COUNT = 5;
			KelbimManager.setKelbimStage(2);
			for(Player player : KelbimManager.getPlayersInside())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.KELBIM_HAS_TRANSFORMED_TO_BECOME_EVEN_STRONGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			}
			if(actor.getAggroList().getTopDamager(null).isPlayer() && actor.getAggroList().getTopDamager(null).isMageClass())
				actor.altOnMagicUse(actor, s_mdef);
			else if (actor.getAggroList().getTopDamager(null).isPlayer())
				actor.altOnMagicUse(actor, s_phisdef);
			else
				actor.altOnMagicUse(actor, s_celestial);
		}
		else if(chp < 45 && _hpStage == 3)
		{
			_hpStage = 4;
			MINIONS_COUNT = 6;
			MASS_ATTACK_COUNT = 6;
			for(Player player : KelbimManager.getPlayersInside())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.KELBIM_IS_CALLING_HIS_UNDERLINGS_TO_PROTECT_HIM, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			}
		}
		else if(chp < 15 && _hpStage == 4)
		{
			actor.altOnMagicUse(actor, s_stage4);
			_hpStage = 5;
			MINIONS_COUNT = 6;
			MASS_ATTACK_COUNT = 7;
			KelbimManager.setKelbimStage(4);
			for(Player player : KelbimManager.getPlayersInside())
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.KELBIM_IS_MAKING_HIS_LASTDITCH_EFFORT, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			}
			KelbimManager.setZoneGazz(true);
		}

		// Minions spawn
		if(_minionsSpawnDelay < System.currentTimeMillis() && getAliveMinionsCount() < 8 && Rnd.chance(20))
		{
			for (int i = 0; i < MINIONS_COUNT; i++)
			{
				NpcInstance minion = NpcUtils.spawnSingle(26134, Location.findPointToStay(actor.getLoc(), 200, 400, actor.getGeoIndex()));  // Темный Маг Астатина
				minions.add(minion);

				for (Player player : KelbimManager.getPlayersInside())
					minion.getAggroList().addDamageHate(player, 0, 1);

				KelbimManager.addSpawnedMinion(minion);
			}
			_minionsSpawnDelay = System.currentTimeMillis() + 30000L;
		}
		if(_hpStage == 3 && !guard)
		{
			NpcInstance astatinguard = NpcUtils.spawnSingle(Rnd.chance(50) ? 26127 : 26126, Location.findPointToStay(actor.getLoc(), 200, 400, actor.getGeoIndex()));  // Страж Дестра\Синистра
			minions.add(astatinguard);
			guard = true;
			for(Player player : KelbimManager.getPlayersInside())
				astatinguard.getAggroList().addDamageHate(player,0, 1);
			KelbimManager.addSpawnedMinion(astatinguard);
		}
        //Массовые атаки
		if(_massattackSpawnDelay < System.currentTimeMillis())
		{
			for (int i = 0; i < MASS_ATTACK_COUNT; i++)
			{
				NpcUtils.spawnSingle(19599, Location.findPointToStay(actor.getLoc(), 200, 450, actor.getGeoIndex()));
				NpcUtils.spawnSingle(19599, Location.findPointToStay(actor.getLoc(), 500, 900, actor.getGeoIndex()));
			}
			_massattackSpawnDelay = System.currentTimeMillis() + (Rnd.get(20000, 40000)-_hpStage*1500);
		}

		//Устройства-стражи
		if(_guardSpawnDelay < System.currentTimeMillis())
		{
			for (int i = 0; i < 7; i++)
			{
				NpcInstance guardsdevice = NpcUtils.spawnSingle(26130, Location.findPointToStay(actor.getLoc(), 500, 800, actor.getGeoIndex()));
				minions.add(guardsdevice);
				KelbimManager.addSpawnedMinion(guardsdevice);
			}
			_guardSpawnDelay = System.currentTimeMillis() + (Rnd.get(200000, 400000)-_hpStage*6000);;
		}

		//Различные дебафы
		if(_notOrdinalattackDelay < System.currentTimeMillis() && _hpStage >= 2)
		{
			if(_hpStage == 2)
			{
				addTaskCast(target, s_wave);
			}
			else if(_hpStage == 3 || _hpStage == 4)
			{
				if(Rnd.chance(60))
					addTaskCast(target, s_storm);
				else if(Rnd.chance(50))
					addTaskCast(target, s_shtorm);
				else if(Rnd.chance(30))
					addTaskCast(target, s_wave);
				else
					addTaskCast(target, s_fear);
			}
			else if(_hpStage == 5)
			{
				if(Rnd.chance(40))
					addTaskCast(target, s_storm);
				else if(Rnd.chance(30))
					addTaskCast(target, s_shtorm);
				else if(Rnd.chance(20))
					addTaskCast(target, s_wave);
				else
					addTaskCast(target, s_fear);
			}
			_notOrdinalattackDelay = System.currentTimeMillis() + (Rnd.get(20000, 40000)-_hpStage*3000);
		}

		if(_movetome < System.currentTimeMillis() && distance > 150)
		{
			addTaskCast(target, s_shok2);
			_movetome = System.currentTimeMillis() + 8000L;
		}

		// Stage based skill attacks
		Map<Skill, Integer> d_skill = new HashMap<Skill, Integer>();
		switch(_hpStage)
		{
			case 1:
				addDesiredSkill(d_skill, target, distance, s_fatal);
				break;
			case 2:
				addDesiredSkill(d_skill, target, distance, s_black);
				break;
			case 3:
				addDesiredSkill(d_skill, target, distance, s_blade);
				addDesiredSkill(d_skill, target, distance, s_black);
				break;
			case 4:
				addDesiredSkill(d_skill, target, distance, s_blade);
				addDesiredSkill(d_skill, target, distance, s_black);
				addDesiredSkill(d_skill, target, distance, s_shok);
				break;
			case 5:
				addDesiredSkill(d_skill, target, distance, s_blade);
				addDesiredSkill(d_skill, target, distance, s_black);
				addDesiredSkill(d_skill, target, distance, s_shok);
				break;
			default:
				break;
		}

		Skill r_skill = selectTopSkill(d_skill);
		if(r_skill != null && !r_skill.isOffensive())
			target = actor;

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private int getAliveMinionsCount()
	{
		int i = 0;
		for(NpcInstance n : minions)
			if(n != null && !n.isDead())
				i++;
		return i;
	}

	private Skill getSkill(int id, int level)
	{
		return SkillHolder.getInstance().getSkill(id, level);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(minions != null && !minions.isEmpty())
			for(NpcInstance n : minions)
				n.deleteMe();
		super.onEvtDead(killer);
	}
}