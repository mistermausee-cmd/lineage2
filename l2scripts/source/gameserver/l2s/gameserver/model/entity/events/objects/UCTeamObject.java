package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Iterator;

import l2s.commons.listener.Listener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;

public class UCTeamObject implements Serializable, Iterable<UCMemberObject>
{
    private final Party _party;
    private final Player _leader;
    private final long _registerTime;
    private int _kills;
    private int _deaths;
    private UCMemberObject[] _members;
    
    public UCTeamObject(final Player leader, final Listener<Creature> listener)
    {
        _members = new UCMemberObject[9];
        _leader = leader;
        _party = leader.getParty();
        _registerTime = System.currentTimeMillis();
        int i = 0;
        for(final Player player : _party)
        {
            player.addListener(listener);
            _members[i++] = new UCMemberObject(player);
        }
    }
    
    public Party getParty()
    {
        return _party;
    }
    
    public Player getLeader()
    {
        return _leader;
    }
    
    public long getRegisterTime()
    {
        return _registerTime;
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
    
    public UCMemberObject[] getMembers()
    {
        return _members;
    }
    
    @Override
    public Iterator<UCMemberObject> iterator()
    {
        return new ArrayIterator<UCMemberObject>(_members);
    }
    
    private class ArrayIterator<E> implements Iterator<E>
    {
        final E[] objects;
        int cursor;
        
        public ArrayIterator(final E[] objects)
        {
            this.cursor = 0;
            this.objects = objects;
        }
        
        @Override
        public boolean hasNext()
        {
            return this.cursor < this.objects.length;
        }
        
        @Override
        public E next()
        {
            return this.objects[this.cursor++];
        }
        
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}