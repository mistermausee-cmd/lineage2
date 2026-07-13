package l2s.gameserver.model.instances;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.StaticObjectTemplate;

public class TavernChairInstance extends ChairInstance
{
  private static final long serialVersionUID = 1L;
  private Party _owner;
  
  public TavernChairInstance(int objectId, StaticObjectTemplate template)
  {
    super(objectId, template);
  }
  
  @Override
  public boolean canSit(Player player)
  {
	  if(!super.canSit(player))
	      return false; 
	  return (_owner != null && player.getParty() == _owner);
  }

  public void setOwner(Party owner)
  {
    _owner = owner;
  }
}