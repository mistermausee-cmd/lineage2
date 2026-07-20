/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.EquipSkillRelogFix;

import java.util.List;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.Containers;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jmobius.gameserver.model.item.enums.ItemSkillType;
import org.l2jmobius.gameserver.model.item.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Re-applies ACTIVE item skills (toggles / usable abilities) and ON_EQUIP self-buffs of the
 * player's equipped items on login.
 * <p>
 * Fixes a core issue where equipped items lose their active skills after relog:
 * <ul>
 * <li>{@code Inventory.restore()} runs before the character's expertise level is set, so
 * {@code ItemSkillsListener.notifyEquiped} skips grade-R items
 * ({@code getCrystalType().isGreater(getExpertiseLevel())} is {@code true} because expertise is
 * still NONE at that point).</li>
 * <li>The later re-application ({@code Player.onPlayerEnter} -&gt;
 * {@code Inventory.applyItemSkills} -&gt; {@code Item.giveSkillsToOwner}) only re-adds PASSIVE item
 * skills, never ACTIVE ones.</li>
 * </ul>
 * As a result, god's jewelry toggles (e.g. Sayha's / Maphr's Ring toggle, skill 46799) and similar
 * active item skills become unusable after relog until the item is manually re-equipped. This
 * listener restores them on login, mirroring what a fresh equip would do.
 * @author Kiro
 */
public class EquipSkillRelogFix extends Script
{
	private EquipSkillRelogFix()
	{
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
	}
	
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		boolean updateSkillList = false;
		for (Item item : player.getInventory().getPaperdollItems())
		{
			if ((item == null) || !item.getTemplate().hasSkills())
			{
				continue;
			}
			
			// (1) Re-add ACTIVE normal item skills. The login re-application (giveSkillsToOwner)
			// only re-adds PASSIVE skills, so active toggles/abilities are otherwise missing.
			final List<ItemSkillHolder> normalSkills = item.getTemplate().getSkills(ItemSkillType.NORMAL);
			if (normalSkills != null)
			{
				for (ItemSkillHolder holder : normalSkills)
				{
					final Skill skill = holder.getSkill();
					if ((skill == null) || skill.isPassive())
					{
						continue;
					}
					if (player.getSkillLevel(skill.getId()) >= skill.getLevel())
					{
						continue;
					}
					player.addSkill(skill, false);
					updateSkillList = true;
				}
			}
			
			// (2) Re-fire ON_EQUIP self-buffs (e.g. god's jewelry glow / marker abnormals) if the
			// player is not already affected by them.
			final List<ItemSkillHolder> onEquipSkills = item.getTemplate().getSkills(ItemSkillType.ON_EQUIP);
			if (onEquipSkills != null)
			{
				for (ItemSkillHolder holder : onEquipSkills)
				{
					final Skill skill = holder.getSkill();
					if ((skill == null) || player.isAffectedBySkill(skill.getId()))
					{
						continue;
					}
					skill.activateSkill(player, player, item);
				}
			}
		}
		
		if (updateSkillList)
		{
			player.sendSkillList();
		}
	}
	
	public static void main(String[] args)
	{
		new EquipSkillRelogFix();
	}
}
