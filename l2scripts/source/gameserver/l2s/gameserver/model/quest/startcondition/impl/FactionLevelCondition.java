package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

public final class FactionLevelCondition implements ICheckStartCondition
{
  private final FactionType _type;
  private final int _level;
  
  public FactionLevelCondition(FactionType type, int level)
  {
    _type = type;
    _level = level;
  }
  
  public final boolean checkCondition(Player player)
  {
    return player.getFactionList().getLevel(_type) >= _level;
  }
}