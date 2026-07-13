package l2s.gameserver.handler.items.impl;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.SoulShotType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.WeaponTemplate;

public class SoulShotItemHandler extends DefaultItemHandler
{
	private static final TIntIntMap SHOT_SKILLS = new TIntIntHashMap();
	static
	{
		SHOT_SKILLS.put(ItemGrade.NONE.ordinal(), 2039); 
		SHOT_SKILLS.put(ItemGrade.D.ordinal(), 2150); 
		SHOT_SKILLS.put(ItemGrade.C.ordinal(), 2151); 
		SHOT_SKILLS.put(ItemGrade.B.ordinal(), 2152); 
		SHOT_SKILLS.put(ItemGrade.A.ordinal(), 2153); 
		SHOT_SKILLS.put(ItemGrade.S.ordinal(), 2154); 
		SHOT_SKILLS.put(ItemGrade.R.ordinal(), 9193); 
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;
        if(player.isCursedWeaponEquipped())
        {
            player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
            return false;
        }

		
		if(player.getChargedSoulshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isAutoShot(shotId))
			isAutoSoulShot = true;

		if(player.getActiveWeaponInstance() == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int ssConsumption = weaponItem.getSoulShotCount();
		if(ssConsumption <= 0)
		{
			
			if(isAutoSoulShot)
			{
				player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		int[] reducedSoulshot = weaponItem.getReducedSoulshot();
		if(reducedSoulshot[0] > 0 && Rnd.chance(reducedSoulshot[0]))
			ssConsumption = reducedSoulshot[1];

		if(ssConsumption <= 0)
			return false;

		ItemGrade grade = weaponItem.getGrade().extGrade();
		if(grade != item.getGrade())
		{
			
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(!player.getInventory().destroyItem(item, ssConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
			return false;
		}

		if(grade != ItemGrade.NONE)
            WorldStatisticsManager.getInstance().updateStat(player, CategoryType.SS_CONSUMED, weaponItem.getGrade().extOrdinal(), ssConsumption);

        SkillEntry skillEntry = player.getAdditionalSSEffect(false, false);
        if(skillEntry == null)
            skillEntry = item.getTemplate().getFirstSkill();

		if(skillEntry == null)
			skillEntry = SkillHolder.getInstance().getSkillEntry(SHOT_SKILLS.get(grade.ordinal()), 1);

		player.forceUseSkill(skillEntry.getTemplate(), player);
		return true;
	}

	@Override
	public boolean isAutoUse()
	{
		return true;
	}
}