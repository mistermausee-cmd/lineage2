package npc.model.events;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class RoyalLoveManagerInstance extends NpcInstance
{
	// Item ID's
	private static final int NOBLE_COMPASSION_CHOCOLATE = 28501;	// Шоколад Королевской Симпатии
	private static final int NOBLE_DEVOTION_CANDY = 28502;	// Конфета Королевской Преданности
	private static final int NOBLE_LONGING_COOKIE = 28503;	// Печенье Королевской Страсти
	private static final int FRINTEZZAS_TRANSFORMATION_CHOCOLATE = 28504;	// Шоколад Фринтезы для Трансформации
	private static final int FRINTEZZAS_TRANSFORMATION_CANDY = 28505;	// Конфета Фринтезы для Трансформации
	private static final int FRINTEZZAS_TRANSFORMATION_COOKIE = 28506;	// Печенье Фринтезы для Трансформации
	private static final int FRINTEZZAS_FRIEND_SUMMON_CHOCOLATE = 28507;	// Шоколад Фринтезы для Призыва Друга
	private static final int FRINTEZZAS_FRIEND_SUMMON_CANDY = 28508;	// Конфета Фринтезы для Призыва Друга
	private static final int FRINTEZZAS_FRIEND_SUMMON_COOKIE = 28509;	// Печенье Фринтезы для Призыва Друга

	// Skill ID's
	private static final int FRINTEZZA_MAGIC_SKILL_ID = 27845;	// Frintezza's Magic
	private static final int COMPASSION_SKILL_ID = 27846;	// Frintezza's Magic
	private static final int DEVOTION_SKILL_ID = 27847;	// Frintezza's Magic
	private static final int LONGING_SKILL_ID = 27848;	// Frintezza's Magic

	public RoyalLoveManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -1000000)
		{
			if(reply == 1)
			{
				if(ItemFunctions.haveItem(player, NOBLE_COMPASSION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CHOCOLATE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_DEVOTION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CANDY, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_LONGING_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_COOKIE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				player.getAbnormalList().stop(COMPASSION_SKILL_ID);
				player.getAbnormalList().stop(DEVOTION_SKILL_ID);
				player.getAbnormalList().stop(LONGING_SKILL_ID);

				ItemFunctions.addItem(player, NOBLE_COMPASSION_CHOCOLATE, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_TRANSFORMATION_CHOCOLATE, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_FRIEND_SUMMON_CHOCOLATE, 1, true);

				showChatWindow(player, "events/royal_love/ev_g_2017_valentine004.htm", false);
			}
			else if(reply == 2)
			{
				if(ItemFunctions.haveItem(player, NOBLE_COMPASSION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CHOCOLATE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_DEVOTION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CANDY, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_LONGING_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_COOKIE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				player.getAbnormalList().stop(COMPASSION_SKILL_ID);
				player.getAbnormalList().stop(DEVOTION_SKILL_ID);
				player.getAbnormalList().stop(LONGING_SKILL_ID);

				ItemFunctions.addItem(player, NOBLE_DEVOTION_CANDY, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_TRANSFORMATION_CANDY, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_FRIEND_SUMMON_CANDY, 1, true);

				showChatWindow(player, "events/royal_love/ev_g_2017_valentine005.htm", false);
			}
			else if(reply == 3)
			{
				if(ItemFunctions.haveItem(player, NOBLE_COMPASSION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CHOCOLATE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CHOCOLATE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_DEVOTION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_CANDY, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_CANDY, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				if(ItemFunctions.haveItem(player, NOBLE_LONGING_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_TRANSFORMATION_COOKIE, 1) || ItemFunctions.haveItem(player, FRINTEZZAS_FRIEND_SUMMON_COOKIE, 1))
				{
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine007.htm", false);
					return;
				}

				player.getAbnormalList().stop(COMPASSION_SKILL_ID);
				player.getAbnormalList().stop(DEVOTION_SKILL_ID);
				player.getAbnormalList().stop(LONGING_SKILL_ID);

				ItemFunctions.addItem(player, NOBLE_LONGING_COOKIE, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_TRANSFORMATION_COOKIE, 1, true);
				ItemFunctions.addItem(player, FRINTEZZAS_FRIEND_SUMMON_COOKIE, 1, true);

				showChatWindow(player, "events/royal_love/ev_g_2017_valentine006.htm", false);
			}
		}
		if(ask == -1000009)
		{
			if(reply == 1)
			{
				Skill skill = SkillHolder.getInstance().getSkill(FRINTEZZA_MAGIC_SKILL_ID, 1);
				if(skill != null)
				{
					skill.getEffects(this, player);
					showChatWindow(player, "events/royal_love/ev_g_2017_valentine008.htm", false);
				}
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public String getHtmlFilename(int val, Player player)
	{
		if(val == 0)
			return "ev_g_2017_valentine001.htm";
		return super.getHtmlFilename(val, player);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "events/royal_love/";
	}
}