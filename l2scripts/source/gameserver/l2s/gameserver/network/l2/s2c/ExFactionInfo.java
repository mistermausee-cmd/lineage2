package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.dao.CharacterFactionDAO;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Faction;

public class ExFactionInfo extends L2GameServerPacket
{
    private final int _playerId;
    private final int _action;
    private final Collection<Faction> _factions;

    public ExFactionInfo(int playerId, int action)
    {
        _playerId = playerId;
        _action = action;
        Player player = GameObjectsStorage.getPlayer(playerId);
        if (player != null)
            _factions = player.getFactionList().valueCollection();
        else
            _factions = CharacterFactionDAO.getInstance().restore(playerId);
    }

    @Override
    protected void writeImpl()
    {
        writeD(_playerId);
        writeC(_action);
        writeD(_factions.size());
        _factions.forEach(faction -> {
            writeC(faction.getType().ordinal());
            writeH(faction.getLevel());
            writeCutF(faction.getPercentForNextLevel());
        });
    }
}