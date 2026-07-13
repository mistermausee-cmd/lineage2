package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.CommandChannel;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

import bosses.BossesConfig;
import bosses.ValakasManager;

/**
 * @author Bonux
**/
public final class ValakasGatekeeperInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ValakasGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onTeleportRequest(Player talker)
	{
		String htmltext = null;
		if(ValakasManager.isReborned())
		{
			if(!ValakasManager.isEntryLocked())
			{
				if(ValakasManager.getZone().getInsidePlayers().size() >= BossesConfig.VALAKAS_MAX_MEMBERS_COUNT)
				{
					htmltext = "heart_of_volcano004.htm";
				}
				else
				{
					final Party party = talker.getParty();
					final CommandChannel commandChannel = party != null ? party.getCommandChannel() : null;
					if(commandChannel != null)
					{
						if(!commandChannel.isLeaderCommandChannel(talker))
						{
							htmltext = "heart_of_volcano006.htm";
						}
						else if(commandChannel.getMemberCount() < BossesConfig.VALAKAS_MIN_MEMBERS_COUNT)
						{
							htmltext = "heart_of_volcano005.htm";
						}
						else
						{
							if(commandChannel.getMemberCount() > (BossesConfig.VALAKAS_MAX_MEMBERS_COUNT - ValakasManager.getZone().getInsidePlayers().size()))
							{
								htmltext = "heart_of_volcano004.htm";
							}
							else
							{
								for(Player member : commandChannel)
								{
									if(member.isCursedWeaponEquipped()) // TODO: Check.
										continue;

									if(!member.isInRange(this, 1000))
									{
										showChatWindow(talker, "default/heart_of_volcano007.htm", false);
										return;
									}

									if(member.getLevel() < BossesConfig.VALAKAS_MEMBER_MIN_LEVEL)
									{
										showChatWindow(talker, "default/heart_of_volcano008.htm", false);
										return;
									}

									member.teleToLocation(ValakasManager.TELEPORT_POSITION);
								}
								ValakasManager.setValakasSpawnTask();
							}
						}
					}
					// TODO: Можно ли в Ertheia входить к Валакасу одному?
					else if(!ValakasManager.isSpawnTaskStarted())
						htmltext = "heart_of_volcano005.htm";
					else if(talker.getLevel() < BossesConfig.VALAKAS_MEMBER_MIN_LEVEL)
						htmltext = "heart_of_volcano008.htm";
					else
						talker.teleToLocation(ValakasManager.TELEPORT_POSITION);
				}
			}
			else
				htmltext = "heart_of_volcano003.htm";
		}
		else
			htmltext = "heart_of_volcano002.htm";

		if(htmltext != null)
			showChatWindow(talker, "default/" + htmltext, false);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		if(val == 0)
			showChatWindow(player, "default/heart_of_volcano001.htm", firstTalk, replace);
		else
			super.showChatWindow(player, val, firstTalk, replace);
	}
}