package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Kolobrodik
 * @date 16:21/16.06.13
 */
public class BergamoChestInstance extends NpcInstance
{
    private static final int[] _B_Grade_Drop = { 947, 948, 6571, 6572 };
    private static final int[] _A_Grade_Drop = { 6569, 6570, 729, 730 };
    private static final int[] _S_Grade_Drop = { 6577, 6578, 959, 960 };
    private static final int[] _R_Grade_Drop = { 19447, 19448, 17526, 17527 };
    private static final int[] _Stones_Drop = { 9546, 9547, 9548, 9549, 9550, 9551, 9552, 9553, 9554, 9555, 9556, 9557 };

    public BergamoChestInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
    {
        super(objectId, template, set);
    }

    @Override
    public void onBypassFeedback(Player player, String command)
    {
        if ((command.equalsIgnoreCase("start")) && (player.getVar("BerOpen") == null))
        {
            player.setVar("BerOpen", "true", System.currentTimeMillis() + 180000L);
            int rewardId = rollDrop(getReflection().getInstancedZoneId());
            if (rewardId > 0)
				ItemFunctions.addItem(player, rewardId, 1);
                //dropItem(player, rewardId, 1);
			doDie(null);	
        }
        else
        {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
    {
        if (player.getVar("BerOpen") != null)
        {
			showChatWindow(player, "openBergamoChest.htm", firstTalk);
			return;
        }
        super.showChatWindow(player, val, firstTalk, replace);
    }

    private int rollDrop(int id)
    {
        switch (id)
        {
            case 212:
                return Rnd.get(_Stones_Drop);
            case 213:
                return Rnd.nextBoolean() ? Rnd.get(_B_Grade_Drop) : Rnd.get(_Stones_Drop);
            case 214:
                return Rnd.nextBoolean() ? Rnd.get(_A_Grade_Drop) : Rnd.get(_Stones_Drop);
            case 215:
                return Rnd.nextBoolean() ? Rnd.get(_S_Grade_Drop) : Rnd.get(_Stones_Drop);
            case 216:
                return Rnd.nextBoolean() ? Rnd.get(_R_Grade_Drop) : Rnd.get(_Stones_Drop);
            default:
                return 0;
        }
    }
}
