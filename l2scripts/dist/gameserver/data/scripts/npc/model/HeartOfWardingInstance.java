package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.CommandChannel;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

import bosses.AntharasManager;
import bosses.BossesConfig;

/**
 * @author Bonux
**/
public final class HeartOfWardingInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public HeartOfWardingInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onTeleportRequest(Player talker)
	{
		String htmltext = null;
		if(AntharasManager.isReborned())
		{
			if(!AntharasManager.isEntryLocked())
			{
				if(AntharasManager.getZone().getInsidePlayers().size() >= BossesConfig.ANTHARAS_MAX_MEMBERS_COUNT)
				{
					htmltext = "heart_of_warding005.htm";
				}
				else
				{
					final Party party = talker.getParty();
					final CommandChannel commandChannel = party != null ? party.getCommandChannel() : null;
					if(commandChannel != null)
					{
						if(!commandChannel.isLeaderCommandChannel(talker))
						{
							htmltext = "heart_of_warding006.htm";
						}
						else if(commandChannel.getMemberCount() < BossesConfig.ANTHARAS_MIN_MEMBERS_COUNT)
						{
							htmltext = "heart_of_warding007.htm";
						}
						else
						{
							if(AntharasManager.checkRequiredItems(talker))
							{
								if(commandChannel.getMemberCount() > (BossesConfig.ANTHARAS_MAX_MEMBERS_COUNT - AntharasManager.getZone().getInsidePlayers().size()))
								{
									htmltext = "heart_of_warding005.htm";
								}
								else
								{
									for(Player member : commandChannel)
									{
										if(member.isCursedWeaponEquipped()) // TODO: Check.
											continue;

										if(!member.isInRange(this, 1000))
										{
											showChatWindow(talker, "default/heart_of_warding009.htm", false);
											return;
										}

										if(!AntharasManager.checkRequiredItems(member))
										{
											showChatWindow(talker, "default/heart_of_warding004a.htm", false, "<?name?>", member.getName());
											return;
										}

										if(member.getLevel() < BossesConfig.ANTHARAS_MEMBER_MIN_LEVEL)
										{
											showChatWindow(talker, "default/heart_of_warding008.htm", false);
											return;
										}
									}

									for(Player member : commandChannel)
									{
										if(member.isCursedWeaponEquipped()) // TODO: Check.
											continue;

										if(AntharasManager.consumeRequiredItems(member))
											member.teleToLocation(AntharasManager.TELEPORT_POSITION.getX() + Rnd.get(700), AntharasManager.TELEPORT_POSITION.getY() + Rnd.get(2100), AntharasManager.TELEPORT_POSITION.getZ());
									}
									AntharasManager.setAntharasSpawnTask();
								}
							}
							else
							{
								htmltext = "heart_of_warding004.htm";
							}
						}
					}
					// TODO: Можно ли в Ertheia входить к Антарасу одному?
					else if(!AntharasManager.isSpawnTaskStarted())
						htmltext = "heart_of_warding007.htm";
					else if(AntharasManager.checkRequiredItems(talker))
					{
						if(talker.getLevel() < BossesConfig.ANTHARAS_MEMBER_MIN_LEVEL)
							htmltext = "heart_of_warding008.htm";
						else
							talker.teleToLocation(AntharasManager.TELEPORT_POSITION.getX() + Rnd.get(700), AntharasManager.TELEPORT_POSITION.getY() + Rnd.get(2100), AntharasManager.TELEPORT_POSITION.getZ());
					}
					else
						htmltext = "heart_of_warding004.htm";
				}
			}
			else
				htmltext = "heart_of_warding003.htm";
		}
		else
			htmltext = "heart_of_warding002.htm";

		if(htmltext != null)
			showChatWindow(talker, "default/" + htmltext, false);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
			showChatWindow(player, "default/heart_of_warding001.htm", firstTalk, replace);
		else
			super.showChatWindow(player, val, firstTalk, replace);
	}
}