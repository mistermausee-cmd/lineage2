package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2s.gameserver.model.Player;

public class UCMemberObject implements Serializable
{
    private final Player _player;
    private final String _name;
    private int _kills;
    private int _deaths;
    
    public UCMemberObject(final Player player)
    {
        _player = player;
        _name = player.getName();
    }
    
    public Player getPlayer()
    {
        return _player;
    }
    
    public int getKills()
    {
        return _kills;
    }
    
    public void incKills()
    {
        _kills++;
    }
    
    public int getDeaths()
    {
        return _deaths;
    }
    
    public void incDeaths()
    {
        _deaths++;
    }
    
    public String getName()
    {
        return _name;
    }
}