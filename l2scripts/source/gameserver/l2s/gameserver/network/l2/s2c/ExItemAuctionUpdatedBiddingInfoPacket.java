package l2s.gameserver.network.l2.s2c;

public class ExItemAuctionUpdatedBiddingInfoPacket extends L2GameServerPacket
{
    private final long _newBid;
    
    public ExItemAuctionUpdatedBiddingInfoPacket(long newBid)
    {
        _newBid = newBid;
    }
    
    @Override
    protected final void writeImpl()
    {
        writeQ(_newBid);
    }
}