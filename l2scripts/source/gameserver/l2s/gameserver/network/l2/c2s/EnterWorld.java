package l2s.gameserver.network.l2.c2s;

import java.util.Calendar;
import java.util.List;

import org.napile.pair.primitive.IntObjectPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.CoupleManager;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.instancemanager.OfflineBufferManager;
import l2s.gameserver.instancemanager.PetitionManager;
import l2s.gameserver.instancemanager.PlayerMessageStack;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.ChangeAllowedHwid;
import l2s.gameserver.network.authcomm.gs2as.ChangeAllowedIp;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ChangeWaitTypePacket;
import l2s.gameserver.network.l2.s2c.ConfirmDlgPacket;
import l2s.gameserver.network.l2.s2c.DiePacket;
import l2s.gameserver.network.l2.s2c.EtcStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ExAdenaInvenCount;
import l2s.gameserver.network.l2.s2c.ExBR_PremiumStatePacket;
import l2s.gameserver.network.l2.s2c.ExBasicActionList;
import l2s.gameserver.network.l2.s2c.ExBeautyItemList;
import l2s.gameserver.network.l2.s2c.ExCastleState;
import l2s.gameserver.network.l2.s2c.ExChangeMPCost;
import l2s.gameserver.network.l2.s2c.ExConnectedTimeAndGettableReward;
import l2s.gameserver.network.l2.s2c.ExEnterWorldPacket;
import l2s.gameserver.network.l2.s2c.ExFactionInfo;
import l2s.gameserver.network.l2.s2c.ExGetBookMarkInfoPacket;
import l2s.gameserver.network.l2.s2c.ExLightingCandleEvent;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExNotifyPremiumItem;
import l2s.gameserver.network.l2.s2c.ExOneDayReceiveRewardList;
import l2s.gameserver.network.l2.s2c.ExOpenMPCCPacket;
import l2s.gameserver.network.l2.s2c.ExPCCafePointInfoPacket;
import l2s.gameserver.network.l2.s2c.ExPeriodicHenna;
import l2s.gameserver.network.l2.s2c.ExPledgeCount;
import l2s.gameserver.network.l2.s2c.ExReceiveShowPostFriend;
import l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode;
import l2s.gameserver.network.l2.s2c.ExStorageMaxCountPacket;
import l2s.gameserver.network.l2.s2c.ExUnReadMailCount;
import l2s.gameserver.network.l2.s2c.ExUserInfoAbnormalVisualEffect;
import l2s.gameserver.network.l2.s2c.ExUserInfoCubic;
import l2s.gameserver.network.l2.s2c.ExUserInfoEquipSlot;
import l2s.gameserver.network.l2.s2c.ExUserInfoInvenWeight;
import l2s.gameserver.network.l2.s2c.ExVitalityEffectInfo;
import l2s.gameserver.network.l2.s2c.ExVoteSystemInfoPacket;
import l2s.gameserver.network.l2.s2c.ExWorldChatCnt;
import l2s.gameserver.network.l2.s2c.HennaInfoPacket;
import l2s.gameserver.network.l2.s2c.MagicAndSkillList;
import l2s.gameserver.network.l2.s2c.MagicSkillLaunchedPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.MyPetSummonInfoPacket;
import l2s.gameserver.network.l2.s2c.PartySmallWindowAllPacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.PledgeSkillListPacket;
import l2s.gameserver.network.l2.s2c.QuestListPacket;
import l2s.gameserver.network.l2.s2c.RelationChangedPacket;
import l2s.gameserver.network.l2.s2c.RidePacket;
import l2s.gameserver.network.l2.s2c.ShortCutInitPacket;
import l2s.gameserver.network.l2.s2c.UIPacket;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.stats.triggers.TriggerType;
import l2s.gameserver.utils.GameStats;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.TradeHelper;

public class EnterWorld extends L2GameClientPacket
{
	private static final Object _lock = new Object();

	private static final Logger _log = LoggerFactory.getLogger(EnterWorld.class);

	@Override
	protected void readImpl()
	{
		
	}

	@Override
	protected void runImpl()
	{
		GameClient client = getClient();
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
		{
			client.closeNow(false);
			return;
		}

		GameStats.incrementPlayerEnterGame();

		onEnterWorld(activeChar);
	}

	public static void onEnterWorld(Player activeChar)
	{
		boolean first = activeChar.entering;

		activeChar.sendPacket(ExLightingCandleEvent.DISABLED);
		
		
		activeChar.sendPacket(new ExEnterWorldPacket());
		activeChar.sendPacket(ExConnectedTimeAndGettableReward.STATIC);
		activeChar.sendPacket(new ExOneDayReceiveRewardList(activeChar));
		activeChar.sendPacket(ExConnectedTimeAndGettableReward.STATIC);
		activeChar.sendPacket(new ExPeriodicHenna(activeChar));
		activeChar.sendPacket(new HennaInfoPacket(activeChar));
		activeChar.sendAbilitiesInfo();

		List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		for(Castle c : castleList)
			activeChar.sendPacket(new ExCastleState(c));

		activeChar.sendSkillList();
		activeChar.sendPacket(new EtcStatusUpdatePacket(activeChar));

		activeChar.sendPacket(new UIPacket(activeChar));
		activeChar.sendPacket(new ExUserInfoInvenWeight(activeChar));
		activeChar.sendPacket(new ExUserInfoEquipSlot(activeChar));
		activeChar.sendPacket(new ExUserInfoCubic(activeChar));
		activeChar.sendPacket(new ExUserInfoAbnormalVisualEffect(activeChar));

		activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);

		double mpCostDiff = activeChar.getMPCostDiff(Skill.SkillMagicType.PHYSIC);
		if(mpCostDiff != 0)
			activeChar.sendPacket(new ExChangeMPCost(Skill.SkillMagicType.PHYSIC, mpCostDiff));

		mpCostDiff = activeChar.getMPCostDiff(Skill.SkillMagicType.MAGIC);
		if(mpCostDiff != 0)
			activeChar.sendPacket(new ExChangeMPCost(Skill.SkillMagicType.MAGIC, mpCostDiff));

		mpCostDiff = activeChar.getMPCostDiff(Skill.SkillMagicType.MUSIC);
		if(mpCostDiff != 0)
			activeChar.sendPacket(new ExChangeMPCost(Skill.SkillMagicType.MUSIC, mpCostDiff));

		activeChar.sendPacket(new QuestListPacket(activeChar));
		activeChar.initActiveAutoShots();
		activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));

		activeChar.sendItemList(false);
		activeChar.sendPacket(new ExAdenaInvenCount(activeChar));
		activeChar.sendPacket(new ShortCutInitPacket(activeChar));
		activeChar.sendPacket(new ExBasicActionList(activeChar));
		
		activeChar.getMacroses().sendMacroses();

		Announcements.getInstance().showAnnouncements(activeChar);

		if(first)
		{
			activeChar.setOnlineStatus(true);
			if(activeChar.getPlayerAccess().GodMode && !Config.SHOW_GM_LOGIN)
			{
				activeChar.setGMInvisible(true);
				activeChar.startAbnormalEffect(AbnormalEffect.STEALTH);
			}

			activeChar.setNonAggroTime(Long.MAX_VALUE);
			activeChar.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);

			if(activeChar.isInBuffStore())
				activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			else if(activeChar.isInStoreMode())
			{
				if(!TradeHelper.validateStore(activeChar))
				{
					activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
					activeChar.storePrivateStore();
				}
			}

			activeChar.setRunning();
			activeChar.standUp();
			activeChar.spawnMe();
			activeChar.startTimers();
			DailyQuestsManager.checkAndRemoveDisabledQuests(activeChar);
		}

		activeChar.sendPacket(new ExVitalityEffectInfo(activeChar));
		activeChar.sendPacket(new ExBR_PremiumStatePacket(activeChar, activeChar.hasPremiumAccount()));
		activeChar.sendPacket(new ExFactionInfo(activeChar.getObjectId(), 0));
		activeChar.sendPacket(new ExSetCompassZoneCode(activeChar));

		activeChar.sendPacket(new MagicAndSkillList(activeChar, 3503292, 730502));
		activeChar.sendPacket(new ExStorageMaxCountPacket(activeChar));
		activeChar.sendPacket(new ExVoteSystemInfoPacket(activeChar));
		activeChar.sendPacket(new ExBeautyItemList(activeChar));
		activeChar.getAttendanceRewards().onEnterWorld();
		activeChar.sendPacket(new ExReceiveShowPostFriend(activeChar));

		if(Config.ALLOW_WORLD_CHAT)
			activeChar.sendPacket(new ExWorldChatCnt(activeChar));

		checkNewMail(activeChar);

		if (first)
			activeChar.getListeners().onEnter();

		if(first && activeChar.getCreateTime() > 0)
		{
			Calendar create = Calendar.getInstance();
			create.setTimeInMillis(activeChar.getCreateTime());
			Calendar now = Calendar.getInstance();

			int day = create.get(Calendar.DAY_OF_MONTH);
			if(create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29)
				day = 28;

			int myBirthdayReceiveYear = activeChar.getVarInt(Player.MY_BIRTHDAY_RECEIVE_YEAR, 0);
			if(create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == day)
			{
				if((myBirthdayReceiveYear == 0 && create.get(Calendar.YEAR) != now.get(Calendar.YEAR)) || myBirthdayReceiveYear > 0 && myBirthdayReceiveYear != now.get(Calendar.YEAR))
				{
					Mail mail = new Mail();
					mail.setSenderId(1);
					mail.setSenderName(StringsHolder.getInstance().getString(activeChar, "birthday.npc"));
					mail.setReceiverId(activeChar.getObjectId());
					mail.setReceiverName(activeChar.getName());
					mail.setTopic(StringsHolder.getInstance().getString(activeChar, "birthday.title"));
					mail.setBody(StringsHolder.getInstance().getString(activeChar, "birthday.text"));

					ItemInstance item = ItemFunctions.createItem(21169);
					item.setLocation(ItemInstance.ItemLocation.MAIL);
					item.setCount(1L);
					item.save();

					mail.addAttachment(item);
					mail.setUnread(true);
					mail.setType(Mail.SenderType.BIRTHDAY);
					mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
					mail.save();

					activeChar.setVar(Player.MY_BIRTHDAY_RECEIVE_YEAR, String.valueOf(now.get(Calendar.YEAR)), -1);
				}
			}
		}

		activeChar.checkAndDeleteOlympiadItems();

		if(activeChar.getClan() != null)
		{
			activeChar.getClan().loginClanCond(activeChar, true);

			activeChar.sendPacket(activeChar.getClan().listAll());
			activeChar.sendPacket(new PledgeSkillListPacket(activeChar.getClan()));
		}
		else
			activeChar.sendPacket(new ExPledgeCount(0));

		
		if(first && Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance().engage(activeChar);
			CoupleManager.getInstance().notifyPartner(activeChar);
		}

		if(first)
		{
			activeChar.getFriendList().notifyFriends(true);
			activeChar.mentoringLoginConditions();
			
		}

		activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
		activeChar.checkDayNightMessages();

		if(Config.SHOW_HTML_WELCOME)
		{
			String html = HtmCache.getInstance().getHtml("welcome.htm", activeChar);
			HtmlMessage msg = new HtmlMessage(5);
			msg.setHtml(HtmlUtils.bbParse(html));
			activeChar.sendPacket(msg);
		}

		if(Config.PETITIONING_ALLOWED)
			PetitionManager.getInstance().checkPetitionMessages(activeChar);

		if(!first)
		{
			boolean dualCast = activeChar.isDualCastingNow();
			if(activeChar.isCastingNow())
			{
				Creature castingTarget = activeChar.getCastingTarget();
				Skill castingSkill = activeChar.getCastingSkill();
				long animationEndTime = activeChar.getAnimationEndTime();
				if(castingSkill != null && !castingSkill.isNotBroadcastable() && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
					activeChar.sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
			}

			if(dualCast)
			{
				Creature castingTarget = activeChar.getDualCastingTarget();
				Skill castingSkill = activeChar.getDualCastingSkill();
				long animationEndTime = activeChar.getDualAnimationEndTime();
				if(castingSkill != null && !castingSkill.isNotBroadcastable() && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
					activeChar.sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
			}

			if(activeChar.isInBoat())
				activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));

			if(activeChar.isMoving || activeChar.isFollow)
				activeChar.sendPacket(activeChar.movePacket());

			if(activeChar.getMountNpcId() != 0)
				activeChar.sendPacket(new RidePacket(activeChar));

			if(activeChar.isFishing())
				activeChar.getFishing().stop();
		}

		activeChar.entering = false;

		if(activeChar.isSitting())
			activeChar.sendPacket(new ChangeWaitTypePacket(activeChar, ChangeWaitTypePacket.WT_SITTING));

		if(activeChar.isInStoreMode())
			activeChar.sendPacket(activeChar.getPrivateStoreMsgPacket(activeChar));

		activeChar.unsetVar("offline");
		activeChar.unsetVar("offlinebuff");
		activeChar.unsetVar("offlinebuff_price");
		activeChar.unsetVar("offlinebuff_skills");
		activeChar.unsetVar("offlinebuff_title");

		OfflineBufferManager.getInstance().getBuffStores().remove(activeChar.getObjectId());

		
		activeChar.sendActionFailed();

		if(first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand)
		{
			
			if(activeChar.getVarBoolean("gm_silence"))
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
			}
			
			if(activeChar.getVarBoolean("gm_invul"))
			{
				activeChar.getFlags().getInvulnerable().start();
				activeChar.startAbnormalEffect(AbnormalEffect.INVINCIBILITY);
				activeChar.sendMessage(activeChar.getName() + " is now immortal.");
			}
			
			if(activeChar.getVarBoolean("gm_undying"))
			{
				activeChar.setGMUndying(true);
				activeChar.sendMessage("Undying state has been enabled.");
			}
			
			try
			{
				int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
				if(var_gmspeed >= 1 && var_gmspeed <= 4)
					activeChar.doCast(SkillHolder.getInstance().getSkillEntry(7029, var_gmspeed), activeChar, true);
			}
			catch(Exception E)
			{}
		}

		PlayerMessageStack.getInstance().CheckMessages(activeChar);

		IntObjectPair<OnAnswerListener> entry = activeChar.getAskListener(false);
		if(entry != null && entry.getValue() instanceof ReviveAnswerListener)
			activeChar.sendPacket(new ConfirmDlgPacket(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0).addString("Other player").addString("some"));

		if(activeChar.isCursedWeaponEquipped())
			CursedWeaponsManager.getInstance().showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());

		if(!first)
		{
			
			if(activeChar.isInObserverMode())
			{
				if(activeChar.getObserverMode() == Player.OBSERVER_LEAVING)
					activeChar.returnFromObserverMode();
				else
					activeChar.leaveObserverMode();
			}
			else if(activeChar.isVisible())
				World.showObjectsToPlayer(activeChar);

			List<Servitor> servitors = activeChar.getServitors();
			for(Servitor servitor : servitors)
				activeChar.sendPacket(new MyPetSummonInfoPacket(servitor));

			if(activeChar.isInParty())
			{
				
				
				activeChar.sendPacket(new PartySmallWindowAllPacket(activeChar.getParty(), activeChar));

				RelationChangedPacket rcp = new RelationChangedPacket();
				for(Player member : activeChar.getParty().getPartyMembers())
				{
					if(member != activeChar)
					{
						activeChar.sendPacket(new PartySpelledPacket(member, true));

						for(Servitor servitor : servitors)
							activeChar.sendPacket(new PartySpelledPacket(servitor, true));

						rcp.add(member, activeChar);
						for(Servitor servitor : member.getServitors())
							rcp.add(servitor, activeChar);

						for(Servitor servitor : servitors)
							servitor.broadcastCharInfoImpl(activeChar, NpcInfoType.VALUES);
					}
				}
				activeChar.sendPacket(rcp);

				
				if(activeChar.getParty().isInCommandChannel())
					activeChar.sendPacket(ExOpenMPCCPacket.STATIC);
			}

			activeChar.sendActiveAutoShots();

			for(Abnormal e : activeChar.getAbnormalList())
			{
				if(e.getSkill().isToggle() && !e.getSkill().isNotBroadcastable())
					activeChar.sendPacket(new MagicSkillLaunchedPacket(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar, false));
			}

			activeChar.broadcastCharInfo();
		}

		if(activeChar.isDead())
			activeChar.sendPacket(new DiePacket(activeChar));

		activeChar.updateAbnormalIcons();
		activeChar.updateStats();

		if(Config.ALT_PCBANG_POINTS_ENABLED)
		{
			if(!Config.ALT_PCBANG_POINTS_ONLY_PREMIUM || activeChar.hasPremiumAccount())
				activeChar.sendPacket(new ExPCCafePointInfoPacket(activeChar, 0, 1, 2, 12));
		}

	    if(!activeChar.getPremiumItemList().isEmpty())
	    	activeChar.sendPacket(ExNotifyPremiumItem.STATIC);

		activeChar.checkLevelUpReward(true);

		if(first)
		{
			activeChar.useTriggers(activeChar, TriggerType.ON_ENTER_WORLD, null, null, 0);

			for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_ENTER_GAME))
				hook.onPlayerEnterGame(activeChar);

			if(Config.ALLOW_IP_LOCK && Config.AUTO_LOCK_IP_ON_LOGIN)
				AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(activeChar.getAccountName(), activeChar.getIP()));

			if(Config.ALLOW_HWID_LOCK && Config.AUTO_LOCK_HWID_ON_LOGIN)
			{
				GameClient client = activeChar.getNetConnection();
				if(client != null)
					AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedHwid(activeChar.getAccountName(), client.getHWID()));
			}
		}
	}

	private static void checkNewMail(Player activeChar)
	{
		activeChar.sendPacket(new ExUnReadMailCount(activeChar));
		for(Mail mail : MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId()))
		{
			if(mail.isUnread())
			{
				activeChar.sendPacket(ExNoticePostArrived.STATIC_FALSE);
				break;
			}
		}
	}

	public static void printInfo()
	{
        _log.info("============= The 13 Dragonds Team ==============");
        _log.info("=================== Present =====================");
        _log.info("================= Open Service ==================");
        _log.info("-------------------------------------------------");
        _log.info("==> decompiing any Lineage II java server =======");
        _log.info("==> not only crack we will provide a source =====");
        _log.info("==> price start from $ 30, ======================");
        _log.info("==> the process is around 2 weeks to 1 month ====");
        _log.info("-------------------------------------------------");
        _log.info("===== for more information please contact =======");
        _log.info("-------------------------------------------------");
        _log.info("========= the.13.dragons.dev@gmail.com ==========");
        _log.info("-------------------------------------------------");
        _log.info("============== Skype : 13 Dragons ===============");
        _log.info("======== the.13.dragons.dev@outlook.com =========");
        _log.info("-------------------------------------------------");
        _log.info("=========== Discord : 13Dragons#7746 ============");
        _log.info("-------------------------------------------------");
        _log.info("-------------------------------------------------");
        _log.info("==> this is just example result decompiling from this file");
        _log.info("==> https://maxcheaters.com/forums/topic/227663-share-server-files-grand-crusade-real-files-clean/");
        _log.info("=================================================");
	}
}