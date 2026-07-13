package npc.model;

import instances.AltarShilen;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Awakeninger
 */

public final class AltarGatekeeperInstance extends NpcInstance
{
	private static final int DoorEnter1 = 25180001;
	private static final int DoorEnter2 = 25180002;
	private static final int DoorEnter3 = 25180003;
	private static final int DoorEnter7 = 25180007;

	public AltarGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Reflection reflection = player.getReflection();
		if (reflection != null)
		{
			if (reflection instanceof AltarShilen)
			{
				final AltarShilen altarinstance = (AltarShilen) reflection;

				if (command.startsWith("start1"))
				{
					if(altarinstance.getStage() == 0)
					{
						DoorInstance a_door1 = getReflection().getDoor(DoorEnter1);
						altarinstance.stageStart(1);
						a_door1.openMe();
					}
				}
				else if (command.startsWith("start2"))
				{
					if(altarinstance.getStage() == 3)
					{
						DoorInstance a_door2 = getReflection().getDoor(DoorEnter2);
						altarinstance.stageStart(4);
						a_door2.openMe();
					}
				}
				else if (command.startsWith("start3"))
				{
					if(altarinstance.getStage() == 6)
					{
						DoorInstance a_door1 = getReflection().getDoor(DoorEnter3);
						altarinstance.stageStart(7);
						a_door1.openMe();
					}
				}
				else if (command.startsWith("start4"))
				{
					DoorInstance a_door1 = getReflection().getDoor(DoorEnter7);
					altarinstance.stageStart(8);
					a_door1.openMe();
					deleteMe();
				}
				else if (command.startsWith("portonsecondstage"))
				{
					player.teleToLocation(179304, 13704, -9852, player.getReflection());
				}
				else if (command.startsWith("portonthirdstage"))
				{
					player.teleToLocation(179272, 12920, -12796, player.getReflection());
				}
				else
				{
					super.onBypassFeedback(player, command);
				}
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		HtmlMessage htmlMessage = new HtmlMessage(getObjectId()).setPlayVoice(firstTalk);
		Reflection reflection = player.getReflection();
		if (reflection != null)
		{
			if (reflection instanceof AltarShilen)
			{
				final AltarShilen altarinstance = (AltarShilen) reflection;
				if (getNpcId() == 32798)
				{
					if (altarinstance.getStage() == 0)
					{
						htmlMessage.setFile("default/32798-1.htm");
					}
					else if (altarinstance.getStage() == 3)
					{
						htmlMessage.setFile("default/32798-2.htm");
					}
					else if (altarinstance.getStage() == 6)
					{
						htmlMessage.setFile("default/32798-3.htm");
					}
					else
						htmlMessage.setFile("default/32798-4.htm");
				}
				else if(getNpcId() == 19121)
				{
					if (altarinstance.getStage() >= 3)
						htmlMessage.setFile("default/19121.htm");
					else
						htmlMessage.setFile("no-quest.htm");
				}
				else if(getNpcId() == 19122)
				{
					if (altarinstance.getStage() >= 6)
						htmlMessage.setFile("default/19122.htm");
					else
						htmlMessage.setFile("no-quest.htm");
				}
				else if(getNpcId() == 33300)
				{
					if (altarinstance.getStage() == 7)
						htmlMessage.setFile("default/33300.htm");
					else
						htmlMessage.setFile("no-quest.htm");
				}
			}
			player.sendPacket(htmlMessage);
		}
	}
}