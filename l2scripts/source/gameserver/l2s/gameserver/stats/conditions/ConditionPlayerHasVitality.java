package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerHasVitality extends Condition
{
  private final boolean _value;
  
  public ConditionPlayerHasVitality(boolean value)
  {
    _value = value;
  }
  
  protected boolean testImpl(Env env)
  {
    if (!env.character.isPlayer())
      return !_value;
    
    return env.character.getPlayer().getVitality() > 0 == _value;
  }
}