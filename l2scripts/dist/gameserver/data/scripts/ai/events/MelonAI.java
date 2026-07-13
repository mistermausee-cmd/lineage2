package ai.events;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.utils.ChatUtils;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class MelonAI extends DefaultAI
{
	private static final int ev_unripe_watermelon = 13271;	// Молодой Арбуз
	private static final int ev_bad_watermelon = 13272;	// Завядший Арбуз
	private static final int ev_great_watermelon = 13273;	// Превосходный Арбуз
	private static final int ev_kgreat_watermelon = 13274;	// Огромный Превосходный Арбуз
	private static final int ev_unripe_h_watermelon = 13275;	// Молодой Сладкий Арбуз
	private static final int ev_bad_h_watermelon = 13276;	// Завядший Сладкий Арбуз
	private static final int ev_great_h_watermelon = 13277;	// Превосходный Сладкий Арбуз
	private static final int ev_kgreat_h_watermelon = 13278;	// Огромный Превосходный Сладкий Арбуз

	private static final int s_gourd_nectar = 2005;	// Нектар
	private static final int s_gourd_nectar_good = 4513;	// Тыква - Повышение Уровня
	private static final int s_gourd_nectar_bad = 4514;	// Тыква - Отравление

	private static final NpcString[] ON_SPAWN_TEXTS_1 = new NpcString[]{
		NpcString.OH_WHERE_I_BE_WHO_CALL_ME,
		NpcString.TADA_ITS_A_WATERMELON,
		NpcString.__DID_YA_CALL_ME,
		NpcString.ENTER_THE_WATERMELON_ITS_GONNA_GROW_AND_GROW_FROM_NOW_ON,
		NpcString.OH_OUCH_DID_I_SEE_YOU_BEFORE,
		NpcString.A_NEW_SEASON_SUMMER_IS_ALL_ABOUT_THE_WATERMELON,
		NpcString.DID_YA_CALL_HO_THOUGHT_YOUD_GET_SOMETHING,
		NpcString.DO_YOU_WANT_TO_SEE_MY_BEAUTIFUL_SELF,
		NpcString.HOHOHO_LETS_DO_IT_TOGETHER,
		NpcString.ITS_A_GIANT_WATERMELON_IF_YOU_RAISE_IT_RIGHT_AND_A_WATERMELON_SLICE_IF_YOU_MESS_UP
	};

	private static final NpcString[] ON_SPAWN_TEXTS_2 = new NpcString[]{
		NpcString.I_NEED_NECTAR_GOURD_NECTAR,
		NpcString.I_CAN_ONLY_GROW_BY_DRINKING_NECTAR,
		NpcString.GROW_ME_QUICK_IF_YOURE_GOOD_ITS_A_LARGE_WATERMELON_IF_YOURE_BAD_IT_A_WATERMELON_SLICE,
		NpcString.GIMME_NECTAR_IM_HUNGRY,
		NpcString.HURRY_AND_BRING_ME_NECTAR_NOT_A_NECKTIE_SORRY,
		NpcString.BRING_ME_NECTAR_THEN_ILL_DRINK_AND_GROW,
		NpcString.YOU_WANNA_EAT_A_TINY_WATERMELON_LIKE_ME_TRY_GIVING_ME_SOME_NECTAR_ILL_GET_HUGE,
		NpcString.HEHEHE_GROW_ME_WELL_AND_YOULL_GET_A_REWARD_GROW_ME_BAD_AND_WHO_KNOWS_WHATLL_HAPPEN,
		NpcString.YOU_WANT_A_LARGE_WATERMELON_ID_LIKE_TO_BE_A_WATERMELON_SLICE,
		NpcString.TRUST_ME_AND_BRING_ME_SOME_NECTAR_ILL_BECOME_A_LARGE_WATERMELON_FOR_YOU
	};

	private static final NpcString[] WAIT_TEXTS = new NpcString[]{
		NpcString.IM_LEAVING_NOW_THEN_GOODBYE,
		NpcString.SORRY_BUT_THIS_LARGE_WATERMELON_IS_DISAPPEARING_HERE,
		NpcString.TOO_LATE_HAVE_A_GOOD_TIME,
		NpcString.DING_DING_THATS_THE_BELL_PUT_AWAY_YOUR_WEAPONS_AND_TRY_FOR_NEXT_TIME,
		NpcString.TOO_BAD_YOU_RAISED_IT_UP_TOO__
	};

	private static final NpcString[] ON_ATTACK_TEXTS_1 = new NpcString[]{
		NpcString.LOOK_HERE_DO_IT_RIGHT_YOU_SPILLED_THIS_PRECIOUS,
		NpcString.AH_REFRESHING_SPRAY_A_LITTLE_MORE,
		NpcString.GULP_GULP_GREAT_BUT_ISNT_THERE_MORE,
		NpcString.CANT_YOU_EVEN_AIM_RIGHT_HAVE_YOU_EVEN_BEEN_TO_THE_ARMY,
		NpcString.DID_YOU_MIX_THIS_WITH_WATER_WHYS_IT_TASTE_LIKE_THIS,
		NpcString.OH_GOOD_DO_A_LITTLE_MORE_YEAH,
		NpcString.HOHO_ITS_NOT_THERE_OVER_HERE_AM_I_SO_SMALL_THAT_YOU_CAN_EVEN_SPRAY_ME_RIGHT,
		NpcString.YUCK_WHAT_IS_THIS_ARE_YOU_SURE_THIS_IS_NECTAR,
		NpcString.DO_YOUR_BEST_I_BECOME_A_BIG_WATERMELON_AFTER_JUST_FIVE_BOTTLES,
		NpcString.OF_COURSE_WATERMELON_IS_THE_BEST_NECTAR_HAHAHA
	};

	private static final NpcString[] ON_ATTACK_TEXTS_2 = new NpcString[]{
		NpcString.TADA_TRANSFORMATION_COMPLETE,
		NpcString.AM_I_A_RAIN_WATERMELON_OR_A_DEFECTIVE_WATERMELON,
		NpcString.NOW_IVE_GOTTEN_BIG_EVERYONE_COME_AT_ME,
		NpcString.GET_BIGGER_GET_STRONGER_TELL_ME_YOUR_WISH,
		NpcString.A_WATERMELON_SLICES_WISH_BUT_IM_BIGGER_ALREADY,
		NpcString.A_LARGE_WATERMELONS_WISH_WELL_TRY_TO_BREAK_ME,
		NpcString.IM_DONE_GROWING_IM_RUNNING_AWAY_NOW,
		NpcString.IF_YOU_LET_ME_GO_ILL_GIVE_YOU_TEN_MILLION_ADENA,
		NpcString.FREEDOM_WHAT_DO_YOU_THINK_I_HAVE_INSIDE,
		NpcString.OK_OK_GOOD_JOB_YOU_KNOW_WHAT_TO_DO_NEXT_RIGHT
	};

	private static final NpcString[] ON_ATTACK_TEXTS_3 = new NpcString[]{
		NpcString.AHH_GOOD___SLAP_SLAP_ME,
		NpcString.OWW_YOURE_JUST_BEATING_ME_NOW_GIVE_ME_NECTAR,
		NpcString.LOOK_ITS_GONNA_BREAK,
		NpcString.NOW_ARE_YOU_TRYING_TO_EAT_WITHOUT_DOING_THE_WORK_FINE_DO_WHAT_YOU_WANT_ILL_HATE_YOU_IF_YOU_DONT_GIVE_ME_ANY_NECTAR,
		NpcString.HIT_ME_MORE_HIT_ME_MORE,
		NpcString.IM_GONNA_WITHER_LIKE_THIS_DAMN_IT,
		NpcString.HEY_YOU_IF_I_DIE_LIKE_THIS_THERELL_BE_NO_ITEM_EITHER_ARE_YOU_REALLY_SO_STINGY_WITH_THE_NECTAR,
		NpcString.ITS_JUST_A_LITTLE_MORE_GOOD_LUCK,
		NpcString.SAVE_ME_IM_ABOUT_TO_DIE_WITHOUT_TASTING_NECTAR_EVEN_ONCE,
		NpcString.IF_I_DIE_LIKE_THIS_ILL_JUST_BE_A_WATERMELON_SLICE
	};

	private static final NpcString[] ON_ATTACK_TEXTS_4 = new NpcString[]{
		NpcString.HOHOHO_PLAY_BETTER,
		NpcString.OH_YOURE_VERY_TALENTED_HUH,
		NpcString.PLAY_SOME_MORE_MORE__MORE__MORE,
		NpcString.I_EAT_HITS_AND_GROW,
		NpcString.BUCK_UP_THERE_ISNT_MUCH_TIME,
		NpcString.DO_YOU_THINK_ILL_BURST_WITH_JUST_THAT,
		NpcString.WHAT_A_NICE_ATTACK_YOU_MIGHT_BE_ABLE_TO_KILL_A_PASSING_FLY,
		NpcString.RIGHT_THERE_A_LITTLE_TO_THE_RIGHT_AH_REFRESHING,
		NpcString.YOU_CALL_THAT_HITTING_BRING_SOME_MORE_TALENTED_FRIENDS,
		NpcString.DONT_THINK_JUST_HIT_WERE_HITTING
	};

	private static final NpcString[] ON_ATTACK_TEXTS_5 = new NpcString[]{
		NpcString.OH_WHAT_A_NICE_SOUND,
		NpcString.THE_INSTRUMENT_IS_NICE_BUT_THERES_NO_SONG_SHALL_I_SING_FOR_YOU,
		NpcString.WHAT_BEAUTIFUL_MUSIC,
		NpcString.I_FEEL_GOOD_PLAY_SOME_MORE,
		NpcString.MY_HEART_IS_BEING_CAPTURED_BY_THE_SOUND_OF_CRONO,
		NpcString.GET_THE_NOTES_RIGHT_HEY_OLD_MAN_THAT_WAS_WRONG,
		NpcString.I_LIKE_IT,
		NpcString.OOH_MY_BODY_WANTS_TO_OPEN,
		NpcString.OH_THIS_CHORD_MY_HEART_IS_BEING_TORN_PLAY_A_LITTLE_MORE,
		NpcString.ITS_THIS_THIS_I_WANTED_THIS_SOUND_WHY_DONT_YOU_TRY_BECOMING_A_SINGER
	};

	private static final NpcString[] ON_ATTACK_TEXTS_6 = new NpcString[]{
		NpcString.YOU_CAN_TRY_A_HUNDRED_TIMES_ON_THIS_YOU_WONT_GET_ANYTHING_GOOD,
		NpcString.IT_HURTS_PLAY_JUST_THE_INSTRUMENT,
		NpcString.ONLY_GOOD_MUSIC_CAN_OPEN_MY_BODY,
		NpcString.NOT_THIS_BUT_YOU_KNOW_THAT_WHAT_YOU_GOT_AS_A_CHRONICLE_SOUVENIR_PLAY_WITH_THAT,
		NpcString.WHY_YOU_HAVE_NO_MUSIC_BORING_IM_LEAVING_NOW,
		NpcString.NOT_THOSE_SHARP_THINGS_USE_THE_ONES_THAT_MAKE_NICE_SOUNDS,
		NpcString.LARGE_WATERMELONS_ONLY_OPEN_WITH_MUSIC_JUST_STRIKING_WITH_A_WEAPON_WONT_WORK,
		NpcString.STRIKE_WITH_MUSIC_NOT_WITH_SOMETHING_LIKE_THIS_YOU_NEED_MUSIC,
		NpcString.YOURE_PRETTY_AMAZING_BUT_ITS_ALL_FOR_NOTHING,
		NpcString.USE_THAT_ON_MONSTERS_OK_I_WANT_CRONO
	};

	private static final NpcString[] ON_DEAD_TEXTS_1 = new NpcString[]{
		NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO,
		NpcString.ITS_GOODBYE_AFTER_20_SECONDS,
		NpcString.YEAH_10_SECONDS_LEFT_9_8_7,
		NpcString.IM_LEAVING_IN_2_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR,
		NpcString.IM_LEAVING_IN_1_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR
	};

	private static final NpcString[] ON_DEAD_TEXTS_2 = new NpcString[]{
		NpcString.EVERYONE_THE_WATERMELON_IS_BREAKING,
		NpcString.ITS_LIKE_A_WATERMELON_SLICE,
		NpcString.A_GOBLIN_IS_COMING_OUT,
		NpcString.LARGE_WATERMELON_MAKE_A_WISH,
		NpcString.DONT_TELL_ANYONE_ABOUT_MY_DEATH,
		NpcString.UGH_THE_RED_JUICE_IS_FLOWING_OUT,
		NpcString.THIS_IS_ALL,
		NpcString.KYAAHH_IM_MAD,
		NpcString.EVERYONE_THIS_WATERMELON_BROKE_OPEN_THE_ITEM_IS_FALLING_OUT,
		NpcString.OH_IT_BURST_THE_CONTENTS_ARE_SPILLING_OUT
	};

	private boolean _attacked = false;

	private int _tryCount = 0;
	private int _successCount = 0;
	private int _failCount = 0;

	public MelonAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		int npcId = actor.getNpcId();
		if(npcId == ev_unripe_watermelon)
		{
			ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_1));
			addTimer(1800905, 1000 * 2);
			addTimer(1800926, 1000 * 60);
		}
		else if(npcId == ev_great_watermelon)
			addTimer(1800914, 1000 * 60);
		else if(npcId == ev_bad_watermelon)
			addTimer(1800917, 1000 * 60);
		else if(npcId == ev_unripe_h_watermelon)
		{
			ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_1));
			addTimer(1800907, 1000 * 3);
			addTimer(1800928, 1000 * 60);
		}
		else if(npcId == ev_great_h_watermelon)
			addTimer(1800920, 1000 * 60);
		else if(npcId == ev_bad_h_watermelon)
			addTimer(1800923, 1000 * 60);
		else if(npcId == ev_kgreat_watermelon)
			addTimer(1800914, 1000 * 60);
		else if(npcId == ev_kgreat_h_watermelon)
			addTimer(1800920, 1000 * 60);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		int npcId = actor.getNpcId();
		if(npcId == ev_unripe_watermelon)
		{
			if(timerId == 1800905)
				ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_2));
			else if(timerId == 1800926)
			{
				ChatUtils.say(actor, NpcString.IM_LEAVING_IN_2_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR);
				addTimer(1800927, 1000 * 60);
			}
			else if(timerId == 1800927)
			{
				ChatUtils.say(actor, NpcString.IM_LEAVING_IN_1_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR);
				addTimer(1800912, 1000 * 60);
			}
			else if(timerId == 1800912)
				actor.deleteMe();
		}
		else if(npcId == ev_great_watermelon)
		{
			if(timerId == 1800904)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800914)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800915, 1000 * 10);
			}
			else if(timerId == 1800915)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800916, 1000 * 10);
			}
			else if(timerId == 1800916)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800904, 1000 * 10);
			}
		}
		else if(npcId == ev_bad_watermelon)
		{
			if(timerId == 1800906)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800917)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800918, 1000 * 10);
			}
			else if(timerId == 1800918)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800919, 1000 * 10);
			}
			else if(timerId == 1800919)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800906, 1000 * 10);
			}
		}
		else if(npcId == ev_unripe_h_watermelon)
		{
			if(timerId == 1800907)
				ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_2));
			else if(timerId == 1800928)
			{
				ChatUtils.say(actor, NpcString.IM_LEAVING_IN_2_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR);
				addTimer(1800929, 1000 * 60);
			}
			else if(timerId == 1800929)
			{
				ChatUtils.say(actor, NpcString.IM_LEAVING_IN_1_MINUTES_IF_YOU_DONT_GIVE_ME_ANY_NECTAR);
				addTimer(1800913, 1000 * 60);
			}
			else if(timerId == 1800913)
				actor.deleteMe();
		}
		else if(npcId == ev_great_h_watermelon)
		{
			if(timerId == 1800908)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800920)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800921, 1000 * 10);
			}
			else if(timerId == 1800921)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800922, 1000 * 10);
			}
			else if(timerId == 1800922)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800908, 1000 * 10);
			}
		}
		else if(npcId == ev_bad_h_watermelon)
		{
			if(timerId == 1800909)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800923)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800924, 1000 * 10);
			}
			else if(timerId == 1800924)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800925, 1000 * 10);
			}
			else if(timerId == 1800925)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800909, 1000 * 10);
			}
		}
		else if(npcId == ev_kgreat_watermelon)
		{
			if(timerId == 1800904)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800914)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800915, 1000 * 10);
			}
			else if(timerId == 1800915)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800916, 1000 * 10);
			}
			else if(timerId == 1800916)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800904, 1000 * 10);
			}
		}
		else if(npcId == ev_kgreat_h_watermelon)
		{
			if(timerId == 1800908)
			{
				ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
				actor.deleteMe();
			}
			else if(timerId == 1800920)
			{
				ChatUtils.say(actor, NpcString.IM_GETTING_STRONGER_I_THINK_ILL_BE_ABLE_TO_RUN_AWAY_IN_30_SECONDS_HOHO);
				addTimer(1800921, 1000 * 10);
			}
			else if(timerId == 1800921)
			{
				ChatUtils.say(actor, NpcString.ITS_GOODBYE_AFTER_20_SECONDS);
				addTimer(1800922, 1000 * 10);
			}
			else if(timerId == 1800922)
			{
				ChatUtils.say(actor, NpcString.YEAH_10_SECONDS_LEFT_9_8_7);
				addTimer(1800908, 1000 * 10);
			}
		}
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster, Creature target)
	{
		NpcInstance actor = getActor();
		if(actor == null || actor != target)
			return;

		onEvtAttacked(caster, skill, 0);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return;

		int npcId = actor.getNpcId();
		int skillId = skill == null ? 0 : skill.getId();
		if(npcId == ev_unripe_watermelon)
		{
			if(!_attacked)
			{
				_tryCount = 0;
				_successCount = 0;
				_failCount = 0;

				if(skillId == s_gourd_nectar)
				{
					_tryCount++;

					if(Rnd.get(1000) < 631)
					{
						_successCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
					}
					else
					{
						_failCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
					}
				}
				_attacked = true;
			}
			else
			{
				if(skillId == s_gourd_nectar && _tryCount >= 4)
				{
					_tryCount++;

					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_2));

					Player owner = actor.getPlayer();
					Location loc = actor.getLoc();
					Reflection reflection = actor.getReflection();

					actor.deleteMe();

					if(Rnd.get(1000) < 631)
						_successCount++;
					else
						_failCount++;

					if(_successCount == 5)
					{
						spawnNextNpc(ev_kgreat_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					else if(_successCount == 4)
					{
						spawnNextNpc(ev_great_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					else
					{
						spawnNextNpc(ev_bad_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					attacker.sendPacket(new PlaySoundPacket("ItemSound3.sys_sow_success"));
				}
				else if(skillId == s_gourd_nectar)
				{
					_tryCount++;

					if(Rnd.get(1000) < 631)
					{
						_successCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
					}
					else
					{
						_failCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
					}
				}
				else if(Rnd.get(2) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_3));
			}
		}
		else if(npcId == ev_great_watermelon)
		{
			if(!_attacked)
			{
				ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
				_attacked = true;
			}
			else
			{
				if(Rnd.get(2) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
			}
		}
		else if(npcId == ev_bad_watermelon)
		{
			if(!_attacked)
			{
				ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
				_attacked = true;
			}
			else
			{
				if(Rnd.get(2) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
			}
		}
		else if(npcId == ev_unripe_h_watermelon)
		{
			if(!_attacked)
			{
				_tryCount = 0;
				_successCount = 0;
				_failCount = 0;

				if(skillId == s_gourd_nectar)
				{
					_tryCount++;

					if(Rnd.get(1000) < 631)
					{
						_successCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
					}
					else
					{
						_failCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
					}
				}
				_attacked = true;
			}
			else
			{
				if(skillId == s_gourd_nectar && _tryCount >= 4)
				{
					_tryCount++;

					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_2));

					Player owner = actor.getPlayer();
					Location loc = actor.getLoc();
					Reflection reflection = actor.getReflection();

					actor.deleteMe();

					if(Rnd.get(1000) < 631)
						_successCount++;
					else
						_failCount++;

					if(_successCount == 5)
					{
						spawnNextNpc(ev_kgreat_h_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					else if(_successCount == 4)
					{
						spawnNextNpc(ev_great_h_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					else
					{
						spawnNextNpc(ev_bad_h_watermelon, loc, reflection, owner);

						_successCount = 0;
						_failCount = 0;
					}
					attacker.sendPacket(new PlaySoundPacket("ItemSound3.sys_sow_success"));
				}
				else if(skillId == s_gourd_nectar)
				{
					_tryCount++;

					if(Rnd.get(1000) < 631)
					{
						_successCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
					}
					else
					{
						_failCount++;

						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
						actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
					}
				}
				else if(Rnd.get(10) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_3));
			}
		}
		else if(npcId == ev_great_h_watermelon)
		{
			if(!_attacked)
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

				_attacked = true;
			}
			else
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
				{
					if(Rnd.get(20) == 0)
						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
				}
				else if(Rnd.get(10) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
			}
		}
		else if(npcId == ev_bad_h_watermelon)
		{
			if(!_attacked)
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

				_attacked = true;
			}
			else
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
				{
					if(Rnd.get(20) == 0)
						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
				}
				else if(Rnd.get(10) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
			}
		}
		else if(npcId == ev_kgreat_watermelon)
		{
			if(!_attacked)
			{
				ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
				_attacked = true;
			}
			else
			{
				if(Rnd.get(2) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
			}
		}
		else if(npcId == ev_kgreat_h_watermelon)
		{
			if(!_attacked)
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

				_attacked = true;
			}
			else
			{
				int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
				if(weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
				{
					if(Rnd.get(20) == 0)
						ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
				}
				else if(Rnd.get(10) == 0)
					ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
			}
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(killer != null && actor.getDistance(killer) <= 1500)
		{
			int npcId = actor.getNpcId();
			if(npcId == ev_unripe_watermelon)
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_1));
			else if(npcId == ev_great_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));
				actor.dropItem(actor.getPlayer(), 6391, 10);
			}
			else if(npcId == ev_bad_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));
				actor.dropItem(actor.getPlayer(), 6391, 1);
			}
			else if(npcId == ev_unripe_h_watermelon)
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_1));
			else if(npcId == ev_great_h_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

				int dropedCount = 0;
				if(Rnd.get(100000) < 18)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 6659, 1);
				}
				if(Rnd.get(1000) < 3)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 13071, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 13073, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 13072, 1);
				}
				if(Rnd.get(1000) < 12)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 10480, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 10482, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 10481, 1);
				}
				if(Rnd.get(100) < 11)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 8761, 1);
				}
				if(Rnd.get(100) < 44)
				{
					dropedCount++;

					if(Rnd.get(1000) < 364)
						actor.dropItem(actor.getPlayer(), 9575, 1);
					else if(Rnd.get(1000) < 341)
						actor.dropItem(actor.getPlayer(), 10485, 1);
					else if(Rnd.get(1000) < 295)
						actor.dropItem(actor.getPlayer(), 8760, 1);
				}
				if(Rnd.get(100) < 35)
				{
					dropedCount++;

					if(Rnd.get(1000) < 429)
						actor.dropItem(actor.getPlayer(), 729, 1);
					else if(Rnd.get(1000) < 286)
						actor.dropItem(actor.getPlayer(), 14687, 2);
					else if(Rnd.get(1000) < 286)
						actor.dropItem(actor.getPlayer(), 14693, 2);
				}
				if(Rnd.get(100) < 51)
				{
					dropedCount++;

					if(Rnd.get(100) < 49)
						actor.dropItem(actor.getPlayer(), 14701, 3);
					else if(Rnd.get(1000) < 353)
						actor.dropItem(actor.getPlayer(), 730, 1);
					else if(Rnd.get(1000) < 157)
						actor.dropItem(actor.getPlayer(), 9157, 1);
				}
				if(Rnd.get(10) < 7)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 2134, 1);
				}
			}
			else if(npcId == ev_bad_h_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

				int dropedCount = 0;
				if(Rnd.get(100) < 2)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 10480, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 10482, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 10481, 1);
				}
				if(Rnd.get(100) < 2)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 9570, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 9572, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 9571, 1);
				}
				if(Rnd.get(100) < 51)
				{
					dropedCount++;

					if(Rnd.get(1000) < 353)
						actor.dropItem(actor.getPlayer(), 8750, 1);
					else if(Rnd.get(1000) < 333)
						actor.dropItem(actor.getPlayer(), 8751, 1);
					else if(Rnd.get(1000) < 314)
						actor.dropItem(actor.getPlayer(), 8752, 1);
				}
				if(Rnd.get(10) < 3)
				{
					dropedCount++;

					int i1 = Rnd.get(2);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 14686, 2);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 14692, 2);
				}
				if(Rnd.get(10) < 4)
				{
					dropedCount++;

					if(Rnd.get(1000) < 375)
						actor.dropItem(actor.getPlayer(), 730, 1);
					else if(Rnd.get(1000) < 375)
						actor.dropItem(actor.getPlayer(), 947, 1);
					else if(Rnd.get(1000) < 250)
						actor.dropItem(actor.getPlayer(), 729, 1);
				}
				if(Rnd.get(100) < 55)
				{
					dropedCount++;

					if(Rnd.get(1000) < 364)
						actor.dropItem(actor.getPlayer(), 14700, 5);
					else if(Rnd.get(1000) < 364)
						actor.dropItem(actor.getPlayer(), 9156, 1);
					else if(Rnd.get(1000) < 273)
						actor.dropItem(actor.getPlayer(), 948, 1);
				}
				if(Rnd.get(10) < 8)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 1462, 4);
				}
			}
			else if(npcId == ev_kgreat_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));
				actor.dropItem(actor.getPlayer(), 6391, 20);
			}
			else if(npcId == ev_kgreat_h_watermelon)
			{
				ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

				int dropedCount = 0;
				if(Rnd.get(10000) < 5)
				{
					dropedCount++;

					int i1 = Rnd.get(2);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 6658, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 6660, 1);
				}
				if(Rnd.get(1000) < 5)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 15541, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 15543, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 15542, 1);
				}
				if(Rnd.get(1000) < 5)
				{
					dropedCount++;

					int i1 = Rnd.get(3);
					if(i1 == 0)
						actor.dropItem(actor.getPlayer(), 13071, 1);
					else if(i1 == 1)
						actor.dropItem(actor.getPlayer(), 13073, 1);
					else if(i1 == 2)
						actor.dropItem(actor.getPlayer(), 13072, 1);
				}
				if(Rnd.get(10000) < 1362)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 9627, 1);
				}
				if(Rnd.get(1000) < 225)
				{
					dropedCount++;

					if(Rnd.get(1000) < 353)
						actor.dropItem(actor.getPlayer(), 9576, 1);
					else if(Rnd.get(1000) < 333)
						actor.dropItem(actor.getPlayer(), 10486, 1);
					else if(Rnd.get(1000) < 314)
						actor.dropItem(actor.getPlayer(), 14169, 1);
				}
				if(Rnd.get(10) < 3)
				{
					dropedCount++;

					if(Rnd.get(1000) < 467)
						actor.dropItem(actor.getPlayer(), 14168, 1);
					else if(Rnd.get(1000) < 333)
						actor.dropItem(actor.getPlayer(), 8762, 1);
					else if(Rnd.get(1000) < 200)
						actor.dropItem(actor.getPlayer(), 959, 1);
				}
				if(Rnd.get(100) < 55)
				{
					dropedCount++;

					if(Rnd.get(1000) < 455)
						actor.dropItem(actor.getPlayer(), 14701, 5);
					else if(Rnd.get(1000) < 273)
						actor.dropItem(actor.getPlayer(), 960, 1);
					else if(Rnd.get(1000) < 273)
						actor.dropItem(actor.getPlayer(), 9157, 1);
				}
				if(Rnd.get(10) < 6)
				{
					dropedCount++;
					actor.dropItem(actor.getPlayer(), 2134, 2);
				}
			}
			killer.sendPacket(new PlaySoundPacket("ItemSound.quest_middle"));
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	private static void spawnNextNpc(int npcId, Location loc, Reflection reflection, Player owner)
	{
		if(owner == null)
			return;

		NpcInstance npc = NpcUtils.spawnSingle(npcId, loc, reflection);
		npc.setOwner(owner);
		npc.onAction(owner, false);
	}
}