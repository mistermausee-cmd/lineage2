package l2s.gameserver.model.actor.instances.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.dao.CharacterFactionDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.FactionType;
import l2s.gameserver.network.l2.s2c.ExFactionInfo;

public class FactionList
{
    private final Player _owner;
    private final Map<FactionType, Faction> _factions = new HashMap<FactionType, Faction>(FactionType.VALUES.length);

    public FactionList(Player owner)
    {
        _owner = owner;
    }

    public void restore()
    {
        Collection<Faction> factions = CharacterFactionDAO.getInstance().restore(_owner.getObjectId());
        factions.forEach(faction -> _factions.put(faction.getType(), faction));
    }

    public Faction get(FactionType type)
    {
        Faction faction = _factions.get(type);
        if (faction == null)
        {
            faction = new Faction(type, 0);
            _factions.put(type, faction);
        }
        return faction;
    }

    public Faction[] values()
    {
        return _factions.values().toArray(new Faction[_factions.size()]);
    }

    public Collection<Faction> valueCollection()
    {
        return _factions.values();
    }

    public int getProgress(FactionType type)
    {
        return get(type).getProgress();
    }

    public boolean setProgress(FactionType type, int value)
    {
        Faction faction = get(type);
        faction.setProgress(value);
        if (CharacterFactionDAO.getInstance().update(_owner.getObjectId(), faction))
        {
            _owner.sendPacket(new ExFactionInfo(_owner.getObjectId(), 0));
            return true;
        }
        return false;
    }

    public boolean addProgress(FactionType type, int value)
    {
        Faction faction = get(type);
        faction.addProgress(value);
        if (CharacterFactionDAO.getInstance().update(_owner.getObjectId(), faction))
        {
            _owner.sendPacket(new ExFactionInfo(_owner.getObjectId(), 0));
            return true;
        }
        return false;
    }

    public int getLevel(FactionType type)
    {
        return this.get(type).getLevel();
    }

    public String toString()
    {
        return "FactionList[owner=" + _owner.getName() + "]";
    }
}