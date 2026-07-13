package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestBypassToServer;
import l2s.gameserver.network.l2.s2c.ExHeroListPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

public class OlympiadMonumentInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public OlympiadMonumentInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -50)
		{	
			if(player.isNoble())
				showChatWindow(player, "olympiad/obelisk001.htm", false);
			else
				showChatWindow(player, "olympiad/obelisk001a.htm", false);
		}
		if(ask == -51)
		{
			if(reply == 1)
			{
				if(Hero.getInstance().isInactiveHero(player.getObjectId()))
				{
					if(Olympiad.isValidationPeriod())
						showChatWindow(player, "olympiad/obelisk010c.htm", false);
					else
						showChatWindow(player, "olympiad/obelisk010.htm", false);
				}
				else if(player.isHero())
					showChatWindow(player, "olympiad/obelisk010b.htm", false);
				else
					showChatWindow(player, "olympiad/obelisk010a.htm", false);
			}
			else if(reply == 2)
			{
				if(player.isHero())
				{
					if(!player.isQuestContinuationPossible(true))
						return;

					if (ItemFunctions.getItemCount(player, 30392) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30393) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30394) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30395) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30396) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30397) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30398) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30399) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30400) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30401) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30402) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30403) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30404) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else if (ItemFunctions.getItemCount(player, 30405) >= 1)
					  showChatWindow(player, "olympiad/obelisk020b.htm", false);
					else
					  showChatWindow(player, "olympiad/obelisk020.htm", false);

				}
				else
					showChatWindow(player, "olympiad/obelisk020a.htm", false);
			}
			else if(reply == 3)
			{
			}
			else if(reply == 4)
			{
				if(player.isHero())
				{
					if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_HERO_WING) >= 1)
						showChatWindow(player, "olympiad/obelisk020c.htm", false);
					else if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_HERO_WING, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020c.htm", false);
				}
				else
					showChatWindow(player, "olympiad/obelisk020d.htm", false);
			}
		}
		else if(ask == -52)
		{
			if(reply == 1)
			{
				if(Hero.getInstance().isInactiveHero(player.getObjectId()))
				{
					if(player.isBaseClassActive())
					{
						if(player.getLevel() >= 85)
							Hero.getInstance().activateHero(player);
						else
							showChatWindow(player, "olympiad/obelisk010d.htm", false);
					}
					else
						showChatWindow(player, "olympiad/obelisk010e.htm", false);
				}
				else
					showChatWindow(player, "olympiad/obelisk010a.htm", false);
			}
		}
		else if(ask != -53)
		{
			if(ask == -54)
			{
				if(reply == 1)
				{
					int rank = Olympiad.getRank(player);
					if(rank == 1)
					{
						if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_HERO_CLOAK) < 1 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_FAME_CLOAK) < 1)
						{
							if(!player.isQuestContinuationPossible(true))
								return;

							ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_HERO_CLOAK, 1);
						}
						else
							showChatWindow(player, "olympiad/obelisk040c.htm", false);
					}
					else if(rank == 2 || rank == 3)
					{
						if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_HERO_CLOAK) < 1 && ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_FAME_CLOAK) < 1)
						{
							if(!player.isQuestContinuationPossible(true))
								return;

							ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_FAME_CLOAK, 1);
						}
						else
							showChatWindow(player, "olympiad/obelisk040c.htm", false);
					}
					else if(rank == 0 || rank > 3)
						showChatWindow(player, "olympiad/obelisk040d.htm", false);
				}
			}
			else if(ask == -60)
			{

				if(ItemFunctions.getItemCount(player, 30392) >= 1L || ItemFunctions.getItemCount(player, 30393) >= 1L || ItemFunctions.getItemCount(player, 30394) >= 1L || ItemFunctions.getItemCount(player, 30395) >= 1L || ItemFunctions.getItemCount(player, 30396) >= 1L)
				{
					showChatWindow(player, "olympiad/obelisk020b.htm", false, new Object[0]);
					return;
				} 
				if (ItemFunctions.getItemCount(player, 30397) >= 1L || ItemFunctions.getItemCount(player, 30398) >= 1L || ItemFunctions.getItemCount(player, 30399) >= 1L || ItemFunctions.getItemCount(player, 30400) >= 1L || ItemFunctions.getItemCount(player, 30401) >= 1L || ItemFunctions.getItemCount(player, 30402) >= 1L)
				{
					showChatWindow(player, "olympiad/obelisk020b.htm", false, new Object[0]);
					return;
				} 
				if (ItemFunctions.getItemCount(player, 30403) >= 1L || ItemFunctions.getItemCount(player, 30404) >= 1L || ItemFunctions.getItemCount(player, 30405) >= 1L)
				{
					showChatWindow(player, "olympiad/obelisk020b.htm", false, new Object[0]);
					return;
				} 
				if(reply == 1)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30392, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 2)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30393, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 3)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30394, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 4)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30395, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 5)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30396, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 6)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30397, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 7)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30398, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 8)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30399, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 9)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30400, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				}
				if(reply == 10)
				{
					if(player.isHero())
					{
						if(!player.isQuestContinuationPossible(true))
							return;

						ItemFunctions.addItem(player, 30401, 1);
					}
					else
						showChatWindow(player, "olympiad/obelisk020a.htm", false);
				} 
		        if(reply == 11)
		        {
		        	if(player.isHero())
		            {
		        		if(!player.isQuestContinuationPossible(true))
		        			return;

		              ItemFunctions.addItem(player, 30402, 1);
		            }
		        	else
		        		showChatWindow(player, "olympiad/obelisk020a.htm", false, new Object[0]);
		        }  
		        if(reply == 12)
		        {
		        	if(player.isHero())
		        	{
		        		if(!player.isQuestContinuationPossible(true))
		        			return;

		              ItemFunctions.addItem(player, 30403, 1);
		            }
		        	else
		        		showChatWindow(player, "olympiad/obelisk020a.htm", false, new Object[0]);
		        }  
		        if(reply == 13)
		        {
		            if(player.isHero())
		            {
		            	if(!player.isQuestContinuationPossible(true))
		            		return;

		            	ItemFunctions.addItem(player, 30404, 1);
		            }
		            else
		            	showChatWindow(player, "olympiad/obelisk020a.htm", false, new Object[0]);
		        }  
		        if(reply == 14)
		        {
		            if(player.isHero())
		            {
		            	if(!player.isQuestContinuationPossible(true))
		            		return;

		            	ItemFunctions.addItem(player, 30405, 1);
		            }
		            else
		            	showChatWindow(player, "olympiad/obelisk020a.htm", false, new Object[0]);
		        }  
				if(reply == 0)
				{
					if(player.isNoble())
						showChatWindow(player, "olympiad/obelisk001.htm", false);
					else
						showChatWindow(player, "olympiad/obelisk001a.htm", false);
				}
			}
			else if(ask == -61)
			{
				if(reply == 0)
				{
					if(player.isNoble())
						showChatWindow(player, "olympiad/obelisk001.htm", false);
					else
						showChatWindow(player, "olympiad/obelisk001a.htm", false);
				}
			}
			else if(ask == -62)
			{
				if(reply == 0)
				{
					if(player.isNoble())
						showChatWindow(player, "olympiad/obelisk001.htm", false);
					else
						showChatWindow(player, "olympiad/obelisk001a.htm", false);
				}
			}
			else if(ask != -70)
				super.onMenuSelect(player, ask, reply);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("_heroes"))
		{
			player.sendPacket(new ExHeroListPacket());
			return;
		}

		super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "olympiad/";
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			String fileName = "olympiad/";
			if(player.isNoble())
				fileName = fileName + "obelisk001.htm";
			else
				fileName = fileName + "obelisk001a.htm";

			showChatWindow(player, fileName, firstTalk);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestBypassToServer.class && arg.length == 1 && arg[0].equals("_heroes");
	}
}