package l2s.gameserver.model.instances;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class TamedBeastInstance extends FeedableBeastInstance
{
	private static final long serialVersionUID = 1L;

	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DISTANCE_FOR_BUFF = 200;
	private static final int MAX_DURATION = 1200000; 
	private static final int DURATION_CHECK_INTERVAL = 60000; 
	private static final int DURATION_INCREASE_INTERVAL = 20000; 

	private HardReference<Player> _playerRef = HardReferences.emptyRef();
	private int _foodSkillId, _remainingTime = MAX_DURATION;
	private Future<?> _durationCheckTask = null;

	private List<SkillEntry> _skills = new ArrayList<SkillEntry>();

	private static final List<Map.Entry<NpcString, int[]>> TAMED_DATA = new ArrayList<Map.Entry<NpcString, int[]>>(6);
	static
	{
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.RECKLESS_S1, new int[] { 6671 }));
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.S1_OF_BALANCE, new int[] { 6431, 6666 }));
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.SHARP_S1, new int[] { 6432, 6668 }));
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.USEFUL_S1, new int[] { 6433, 6670 }));
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.S1_OF_BLESSING, new int[] { 6669, 6672 }));
		TAMED_DATA.add(new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.SWIFT_S1, new int[] { 6434, 6667 }));
	}

	public TamedBeastInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		_hasRandomWalk = false;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public void onAction(final Player player, final boolean dontMove)
	{
		player.setNpcTarget(this);
		
	}

	private void onReceiveFood()
	{
		
		_remainingTime = _remainingTime + DURATION_INCREASE_INTERVAL;
		if(_remainingTime > MAX_DURATION)
			_remainingTime = MAX_DURATION;
	}

	public int getRemainingTime()
	{
		return _remainingTime;
	}

	public void setRemainingTime(int duration)
	{
		_remainingTime = duration;
	}

	public int getFoodType()
	{
		return _foodSkillId;
	}

	public void setTameType()
	{
		Map.Entry<NpcString, int[]> type = TAMED_DATA.get(Rnd.get(TAMED_DATA.size()));

		setNameNpcString(type.getKey());
		setName("#" + getNameNpcStringByNpcId().getId());

		for(int skillId : type.getValue())
		{
			SkillEntry sk = SkillHolder.getInstance().getSkillEntry(skillId, 1);
			if(sk != null)
				_skills.add(sk);
		}
	}

	public NpcString getNameNpcStringByNpcId()
	{
		switch(getNpcId())
		{
			case 18869:
				return NpcString.ALPEN_KOOKABURRA;
			case 18870:
				return NpcString.ALPEN_COUGAR;
			case 18871:
				return NpcString.ALPEN_BUFFALO;
			case 18872:
				return NpcString.ALPEN_GRENDEL;
		}
		return NpcString.NONE;
	}

	public void buffOwner()
	{
		if(!isInRange(getPlayer(), MAX_DISTANCE_FOR_BUFF))
		{
			setFollowTarget(getPlayer());
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getPlayer(), Config.FOLLOW_RANGE);
			return;
		}

		int delay = 0;
		for(SkillEntry skillEntry : _skills)
		{
			ThreadPoolManager.getInstance().schedule(new Buff(this, getPlayer(), skillEntry), delay);
			delay = delay + skillEntry.getTemplate().getHitTime() + 500;
		}
	}

	public static class Buff extends RunnableImpl
	{
		private NpcInstance _actor;
		private Player _owner;
		private SkillEntry _skillEntry;

		public Buff(NpcInstance actor, Player owner, SkillEntry skillEntry)
		{
			_actor = actor;
			_owner = owner;
			_skillEntry = skillEntry;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_actor != null)
				_actor.doCast(_skillEntry, _owner, true);
		}
	}

	public void setFoodType(int foodItemId)
	{
		if(foodItemId > 0)
		{
			_foodSkillId = foodItemId;

			
			if(_durationCheckTask != null)
				_durationCheckTask.cancel(false);
			_durationCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		if(_durationCheckTask != null)
		{
			_durationCheckTask.cancel(false);
			_durationCheckTask = null;
		}

		Player owner = getPlayer();
		if(owner != null)
			owner.removeTrainedBeast(getObjectId());

		_foodSkillId = 0;
		_remainingTime = 0;
	}

	@Override
	public Player getPlayer()
	{
		return _playerRef.get();
	}

	public void setOwner(Player owner)
	{
		_playerRef = owner == null ? HardReferences.<Player> emptyRef() : owner.getRef();
		if(owner != null)
		{
			setTitle(owner.getName());
			owner.addTrainedBeast(this);

			broadcastCharInfoImpl(new IUpdateTypeComponent[] { NpcInfoType.TITLE });

			
			setFollowTarget(getPlayer());
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
		}
		else
			doDespawn(); 
	}

	public void despawnWithDelay(int delay)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			doDespawn();
		}, delay);
	}

	public void doDespawn()
	{
		
		stopMove();

		if(_durationCheckTask != null)
		{
			_durationCheckTask.cancel(false);
			_durationCheckTask = null;
		}

		
		Player owner = getPlayer();
		if(owner != null)
			owner.removeTrainedBeast(getObjectId());

		setTarget(null);
		_foodSkillId = 0;
		_remainingTime = 0;

		
		onDecay();
	}

	private static class CheckDuration extends RunnableImpl
	{
		private TamedBeastInstance _tamedBeast;

		CheckDuration(TamedBeastInstance tamedBeast)
		{
			_tamedBeast = tamedBeast;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player owner = _tamedBeast.getPlayer();

			if(owner == null || !owner.isOnline())
			{
				_tamedBeast.doDespawn();
				return;
			}

			if(_tamedBeast.getDistance(owner) > MAX_DISTANCE_FROM_OWNER)
			{
				_tamedBeast.doDespawn();
				return;
			}

			int foodTypeSkillId = _tamedBeast.getFoodType();
			_tamedBeast.setRemainingTime(_tamedBeast.getRemainingTime() - DURATION_CHECK_INTERVAL);

			
			
			
			ItemInstance item = null;
			int foodItemId = _tamedBeast.getItemIdBySkillId(foodTypeSkillId);
			if(foodItemId > 0)
				item = owner.getInventory().getItemByItemId(foodItemId);

			
			if(item != null && item.getCount() >= 1)
			{
				_tamedBeast.onReceiveFood();
				owner.getInventory().destroyItem(item, 1);
			}
			else 
			
			if(_tamedBeast.getRemainingTime() < MAX_DURATION - 300000)
				_tamedBeast.setRemainingTime(-1);

			if(_tamedBeast.getRemainingTime() <= 0)
				_tamedBeast.doDespawn();
		}
	}
}