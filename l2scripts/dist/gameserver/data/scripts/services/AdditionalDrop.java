package services;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.utils.Functions;

/**
 * @author Bonux
**/
public class AdditionalDrop implements OnInitScriptListener
{
	private static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature cha, Creature killer)
		{
			if(!cha.isMonster())
				return;

			MonsterInstance monster = (MonsterInstance) cha;

			Creature topDamager = monster.getAggroList().getTopDamager(killer);
			if(topDamager == null || !topDamager.isPlayable())
				return;

			if(!Functions.SimpleCheckDrop(monster, topDamager))
				return;

			if(Formulas.tryLuck(topDamager.getPlayer()))
			{
				// TODO: [Bonux] Пересмотреть формулу шанса.
				if(Rnd.chance(FORTUNE_POCKET_DROP_CHANCE))
					monster.dropItem(topDamager.getPlayer(), ITEM_ID_FORTUNE_POCKET_STAGE_1, 1);
			}
		}
	}

	private static final int ITEM_ID_FORTUNE_POCKET_STAGE_1 = 39629; // Мешочек Кладоискателя - Ур. 1
	private static final double FORTUNE_POCKET_DROP_CHANCE = 2.5;

	@Override
	public void onInit()
	{
		CharListenerList.addGlobal(new DeathListener());
	}
}