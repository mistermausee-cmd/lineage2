package npc.model;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import instances.KartiaGroup;
import instances.KartiaSolo;

/**
 * @author Iqman
 * @reworked by Bonux
**/
public final class KartiaHelperInstance extends NpcInstance
{
	private static final int KARTIA_SOLO85 = 205;
	private static final int KARTIA_SOLO90 = 206;
	private static final int KARTIA_SOLO95 = 207;
	private static final int KARTIA_PARTY85 = 208;
	private static final int KARTIA_PARTY90 = 209;
	private static final int KARTIA_PARTY95 = 210;

	public KartiaHelperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("kartia"))
		{
			if(!st.hasMoreTokens())
				return;

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("enter"))
			{
				if(!st.hasMoreTokens())
					return;

				String cmd3 = st.nextToken();
				if(cmd3.equalsIgnoreCase("p"))
				{
					if(!st.hasMoreTokens())
						return;

					int kartiaLevel = Integer.parseInt(st.nextToken());

					int instanceId;
					if(kartiaLevel == 85)
						instanceId = KARTIA_PARTY85;
					else if(kartiaLevel == 90)
						instanceId = KARTIA_PARTY90;
					else if(kartiaLevel == 95)
						instanceId = KARTIA_PARTY95;
					else
						return;

					Reflection r = player.getActiveReflection();
					if(r != null)
					{
						if(player.canReenterInstance(instanceId))
						{
							if(r instanceof KartiaGroup)
							{
								KartiaGroup kInst = (KartiaGroup) r;
								if(kInst.getStatus() >= 2)
									player.teleToLocation(-120856, -14344, -11452, r);
								else
									player.teleToLocation(-119830, -10547, -11925, r);
							}
						}
					}
					else if(player.canEnterInstance(instanceId))
						ReflectionUtils.enterReflection(player, new KartiaGroup(), instanceId);
				}
				else if(cmd3.equalsIgnoreCase("s"))
				{
					if(!st.hasMoreTokens())
						return;

					int kartiaLevel = Integer.parseInt(st.nextToken());

					int instanceId;
					if(kartiaLevel == 85)
						instanceId = KARTIA_SOLO85;
					else if(kartiaLevel == 90)
						instanceId = KARTIA_SOLO90;
					else if(kartiaLevel == 95)
						instanceId = KARTIA_SOLO95;
					else
						return;

					Reflection r = player.getActiveReflection();
					if(r != null)
					{
						if(player.canReenterInstance(instanceId))
						{
							if(r instanceof KartiaSolo)
							{
								KartiaSolo kInst = (KartiaSolo) r;
								if(kInst.getStatus() >= 2)
									player.teleToLocation(-111281, -14239, -11428, r);
								else
									player.teleToLocation(-110262, -10547, -11925, r);
							}
						}
					}
					else if(player.canEnterInstance(instanceId))
						ReflectionUtils.enterReflection(player, new KartiaSolo(), instanceId);
				}
			}
			else if(cmd2.equalsIgnoreCase("deselect"))
			{
				if(!st.hasMoreTokens())
					return;

				String cmd3 = st.nextToken();

				Reflection r = player.getActiveReflection();
				if(r != null)
				{
					if(r instanceof KartiaSolo)
					{
						KartiaSolo kInst = (KartiaSolo) r;
						if(cmd3.equalsIgnoreCase("warrior"))
							kInst.deselectSupport(1);
						else if(cmd3.equalsIgnoreCase("archer"))
							kInst.deselectSupport(2);
						else if(cmd3.equalsIgnoreCase("summoner"))
							kInst.deselectSupport(3);
						else if(cmd3.equalsIgnoreCase("healer"))
							kInst.deselectSupport(4);
					}
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
