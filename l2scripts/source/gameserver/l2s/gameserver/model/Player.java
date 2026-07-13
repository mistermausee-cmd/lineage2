package l2s.gameserver.model;

import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_ALTERED_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_PEACE_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_PVP_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_SIEGE_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_SSQ_FLAG;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.napile.pair.primitive.IntObjectPair;
import org.napile.pair.primitive.impl.IntObjectPairImpl;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import l2s.commons.collections.LazyArrayList;
import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.commons.util.concurrent.atomic.AtomicState;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.ai.PlayableAI.AINextAction;
import l2s.gameserver.ai.PlayerAI;
import l2s.gameserver.dao.AccountVariablesDAO;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.dao.CharacterGroupReuseDAO;
import l2s.gameserver.dao.CharacterPostFriendDAO;
import l2s.gameserver.dao.CharacterSubclassDAO;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.dao.CustomHeroDAO;
import l2s.gameserver.dao.EffectsDAO;
import l2s.gameserver.dao.PremiumAccountDAO;
import l2s.gameserver.dao.SummonsDAO;
import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.LevelUpRewardHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.data.xml.holder.PlayerTemplateHolder;
import l2s.gameserver.data.xml.holder.PremiumAccountHolder;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.handler.onshiftaction.OnShiftActionHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.AwayManager;
import l2s.gameserver.instancemanager.BotCheckManager;
import l2s.gameserver.instancemanager.BotCheckManager.BotCheckQuestion;
import l2s.gameserver.instancemanager.ChaosFestivalManager;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.instancemanager.DimensionalRiftManager;
import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.instancemanager.OfflineBufferManager;
import l2s.gameserver.instancemanager.OfflineBufferManager.BufferData;
import l2s.gameserver.instancemanager.PartySubstituteManager;
import l2s.gameserver.instancemanager.PvPRewardManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.TrainingCampManager;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.OnPlayerChatMessageReceive;
import l2s.gameserver.listener.actor.player.impl.BotCheckAnswerListner;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.GameObjectTasks.EndSitDownTask;
import l2s.gameserver.model.GameObjectTasks.EndStandUpTask;
import l2s.gameserver.model.GameObjectTasks.HourlyTask;
import l2s.gameserver.model.GameObjectTasks.KickTask;
import l2s.gameserver.model.GameObjectTasks.PvPFlagTask;
import l2s.gameserver.model.GameObjectTasks.UnJailTask;
import l2s.gameserver.model.GameObjectTasks.WaterTask;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.basestats.PlayerBaseStats;
import l2s.gameserver.model.actor.flags.PlayerFlags;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.player.AntiFlood;
import l2s.gameserver.model.actor.instances.player.AttendanceRewards;
import l2s.gameserver.model.actor.instances.player.BlockList;
import l2s.gameserver.model.actor.instances.player.BookMarkList;
import l2s.gameserver.model.actor.instances.player.CharacterVariable;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.model.actor.instances.player.DailyMissionList;
import l2s.gameserver.model.actor.instances.player.DeathPenalty;
import l2s.gameserver.model.actor.instances.player.FactionList;
import l2s.gameserver.model.actor.instances.player.Fishing;
import l2s.gameserver.model.actor.instances.player.FriendList;
import l2s.gameserver.model.actor.instances.player.HennaList;
import l2s.gameserver.model.actor.instances.player.Macro;
import l2s.gameserver.model.actor.instances.player.MacroList;
import l2s.gameserver.model.actor.instances.player.MenteeList;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.actor.instances.player.PremiumItem;
import l2s.gameserver.model.actor.instances.player.PremiumItemList;
import l2s.gameserver.model.actor.instances.player.ProductHistoryList;
import l2s.gameserver.model.actor.instances.player.ShortCut;
import l2s.gameserver.model.actor.instances.player.ShortCutList;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.actor.instances.player.SubClassList;
import l2s.gameserver.model.actor.instances.player.TrainingCamp;
import l2s.gameserver.model.actor.instances.player.tasks.EnableUserRelationTask;
import l2s.gameserver.model.actor.listener.PlayerListenerList;
import l2s.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.base.NobleType;
import l2s.gameserver.model.base.PetType;
import l2s.gameserver.model.base.PlayerAccess;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.base.SoulShotType;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.model.entity.DimensionalRift;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.boat.ClanAirShip;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.entity.events.impl.PvPEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.impl.SingleMatchEvent;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.entity.olympiad.OlympiadParticipiantData;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.model.instances.ChairInstance;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetBabyInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.instances.SummonInstance.RestoredSummon;
import l2s.gameserver.model.instances.SymbolInstance;
import l2s.gameserver.model.instances.TamedBeastInstance;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemContainer;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.LockType;
import l2s.gameserver.model.items.ManufactureItem;
import l2s.gameserver.model.items.PcFreight;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.items.PcRefund;
import l2s.gameserver.model.items.PcWarehouse;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.model.items.Warehouse;
import l2s.gameserver.model.items.Warehouse.WarehouseType;
import l2s.gameserver.model.items.attachment.FlagItemAttachment;
import l2s.gameserver.model.items.attachment.PickableAttachment;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.model.petition.PetitionMainGroup;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.ClanWar;
import l2s.gameserver.model.pledge.Privilege;
import l2s.gameserver.model.pledge.RankPrivs;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.BonusRequest;
import l2s.gameserver.network.authcomm.gs2as.ReduceAccountPoints;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AbnormalStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.AcquireSkillListPacket;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.CIPacket;
import l2s.gameserver.network.l2.s2c.CameraModePacket;
import l2s.gameserver.network.l2.s2c.ChairSitPacket;
import l2s.gameserver.network.l2.s2c.ChangeWaitTypePacket;
import l2s.gameserver.network.l2.s2c.ConfirmDlgPacket;
import l2s.gameserver.network.l2.s2c.EtcStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ExAcquireAPSkillList;
import l2s.gameserver.network.l2.s2c.ExAlchemySkillList;
import l2s.gameserver.network.l2.s2c.ExAlterSkillRequest;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.ExBR_AgathionEnergyInfoPacket;
import l2s.gameserver.network.l2.s2c.ExBR_PremiumStatePacket;
import l2s.gameserver.network.l2.s2c.ExBasicActionList;
import l2s.gameserver.network.l2.s2c.ExMagicAttackInfo;
import l2s.gameserver.network.l2.s2c.ExNewSkillToLearnByLevelUp;
import l2s.gameserver.network.l2.s2c.ExNotifyPremiumItem;
import l2s.gameserver.network.l2.s2c.ExOlympiadSpelledInfoPacket;
import l2s.gameserver.network.l2.s2c.ExPCCafePointInfoPacket;
import l2s.gameserver.network.l2.s2c.ExPrivateStoreWholeMsg;
import l2s.gameserver.network.l2.s2c.ExQuestItemListPacket;
import l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExSpawnEmitterPacket;
import l2s.gameserver.network.l2.s2c.ExStartScenePlayer;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.ExTeleportToLocationActivate;
import l2s.gameserver.network.l2.s2c.ExUseSharedGroupItem;
import l2s.gameserver.network.l2.s2c.ExUserInfoCubic;
import l2s.gameserver.network.l2.s2c.ExVitalityEffectInfo;
import l2s.gameserver.network.l2.s2c.ExVitalityPointInfo;
import l2s.gameserver.network.l2.s2c.ExWaitWaitingSubStituteInfo;
import l2s.gameserver.network.l2.s2c.ExWorldChatCnt;
import l2s.gameserver.network.l2.s2c.GetItemPacket;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.ItemListPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.LogOutOkPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.NpcInfoPoly;
import l2s.gameserver.network.l2.s2c.ObserverEndPacket;
import l2s.gameserver.network.l2.s2c.ObserverStartPacket;
import l2s.gameserver.network.l2.s2c.PartySmallWindowUpdatePacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.PetDeletePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAllPacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListDeletePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListUpdatePacket;
import l2s.gameserver.network.l2.s2c.PrivateStoreBuyList;
import l2s.gameserver.network.l2.s2c.PrivateStoreBuyMsg;
import l2s.gameserver.network.l2.s2c.PrivateStoreList;
import l2s.gameserver.network.l2.s2c.PrivateStoreMsg;
import l2s.gameserver.network.l2.s2c.QuestListPacket;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.network.l2.s2c.RecipeShopMsgPacket;
import l2s.gameserver.network.l2.s2c.RecipeShopSellListPacket;
import l2s.gameserver.network.l2.s2c.RelationChangedPacket;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import l2s.gameserver.network.l2.s2c.SetupGaugePacket;
import l2s.gameserver.network.l2.s2c.ShortBuffStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ShortCutInitPacket;
import l2s.gameserver.network.l2.s2c.ShortCutRegisterPacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SnoopPacket;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SpecialCameraPacket;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.TargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.TargetUnselectedPacket;
import l2s.gameserver.network.l2.s2c.TeleportToLocationPacket;
import l2s.gameserver.network.l2.s2c.TradeDonePacket;
import l2s.gameserver.network.l2.s2c.UIPacket;
import l2s.gameserver.network.l2.s2c.ValidateLocationPacket;
import l2s.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.skills.skillclasses.Summon;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.taskmanager.AutoSaveManager;
import l2s.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.templates.OptionDataTemplate;
import l2s.gameserver.templates.fakeplayer.FakePlayerAITemplate;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.ItemType;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.templates.jump.JumpTrack;
import l2s.gameserver.templates.jump.JumpWay;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.pet.PetData;
import l2s.gameserver.templates.player.PlayerTemplate;
import l2s.gameserver.templates.player.transform.TransformTemplate;
import l2s.gameserver.templates.premiumaccount.PremiumAccountTemplate;
import l2s.gameserver.utils.AbnormalsComparator;
import l2s.gameserver.utils.AdminFunctions;
import l2s.gameserver.utils.BypassStorage;
import l2s.gameserver.utils.GameStats;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.Mentoring;
import l2s.gameserver.utils.SkillUtils;
import l2s.gameserver.utils.SqlBatch;
import l2s.gameserver.utils.Strings;
import l2s.gameserver.utils.TeleportUtils;
import l2s.gameserver.utils.TimeUtils;

public final class Player extends Playable implements PlayerGroup
{
	public static final int DEFAULT_NAME_COLOR = 0xFFFFFF;
	public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
	public static final int MAX_POST_FRIEND_SIZE = 100;

	private static final Logger _log = LoggerFactory.getLogger(Player.class);

	public static final String NO_TRADERS_VAR = "notraders";
	public static final String NO_ANIMATION_OF_CAST_VAR = "notShowBuffAnim";
	public static final String MY_BIRTHDAY_RECEIVE_YEAR = "MyBirthdayReceiveYear";
	private static final String NOT_CONNECTED = "<not connected>";
	private static final String RECENT_PRODUCT_LIST_VAR = "recentProductList";
	private static final String LVL_UP_REWARD_VAR = "@lvl_up_reward";
	private static final String ACADEMY_GRADUATED_VAR = "@academy_graduated";
	private static final String ENERGY_DESTRUCTION_VAR = "@energ_destr_count";
	private static final String MARK_ENDURITY_VAR = "@mark_endur_count";
	private static final String JAILED_VAR = "jailed";
	private static final String PA_ITEMS_RECIEVED = "pa_items_recieved";
	private static final String FREE_PA_RECIEVED = "free_pa_recieved";
	private static final String ACTIVE_SHOT_ID_VAR = "@active_shot_id";
	private static final String PC_BANG_POINTS_VAR = "pc_bang_poins";
	public static final int MAX_VITALITY_POINTS = 140000;
	private static final String PK_KILL_VAR = "@pk_kill";

	public final static int OBSERVER_NONE = 0;
	public final static int OBSERVER_STARTING = 1;
	public final static int OBSERVER_STARTED = 3;
	public final static int OBSERVER_LEAVING = 2;

	public static final int STORE_PRIVATE_NONE = 0;
	public static final int STORE_PRIVATE_SELL = 1;
	public static final int STORE_PRIVATE_BUY = 3;
	public static final int STORE_PRIVATE_MANUFACTURE = 5;
	public static final int STORE_OBSERVING_GAMES = 7;
	public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
	public static final int STORE_PRIVATE_BUFF = 20;

	public static final int[] EXPERTISE_LEVELS = { 0, 20, 40, 52, 61, 76, 80, 84, 85, 95, 99, Integer.MAX_VALUE };

	private PlayerTemplate _baseTemplate;

	private GameClient _connection;
	private String _login;

	private int _karma, _pkKills, _pvpKills;
	private int _face, _hairStyle, _hairColor;
	private int _beautyFace, _beautyHairStyle, _beautyHairColor;
	private int _recomHave, _recomLeftToday, _fame, _raidPoints;
	private int _recomLeft = 20;
	private int _deleteTimer;
	private boolean _isVoting = false;

	private long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;
	private long _uptime;
	
	private long _lastAccess;

	
	private int _nameColor = DEFAULT_NAME_COLOR, _titlecolor = DEFAULT_TITLE_COLOR;

	private boolean _overloaded;

	boolean sittingTaskLaunched;

	
	private int _waitTimeWhenSit;

	private boolean _autoLoot = Config.AUTO_LOOT, AutoLootHerbs = Config.AUTO_LOOT_HERBS, _autoLootOnlyAdena = Config.AUTO_LOOT_ONLY_ADENA;

	private final PcInventory _inventory = new PcInventory(this);
	private final Warehouse _warehouse = new PcWarehouse(this);
	private final ItemContainer _refund = new PcRefund(this);
	private final PcFreight _freight = new PcFreight(this);

	private final BookMarkList _bookmarks = new BookMarkList(this, 0);

	public Location bookmarkLocation = null;

	private final AntiFlood _antiFlood = new AntiFlood(this);

	private final Map<Integer, RecipeTemplate> _recipebook = new TreeMap<Integer, RecipeTemplate>();
	private final Map<Integer, RecipeTemplate> _commonrecipebook = new TreeMap<Integer, RecipeTemplate>();

	
	private final IntObjectMap<QuestState> _quests = new HashIntObjectMap<QuestState>();

	
	private final ShortCutList _shortCuts = new ShortCutList(this);

	
	private final MacroList _macroses = new MacroList(this);

	
	private final SubClassList _subClassList = new SubClassList(this);

	
	private int _privatestore;
	
	private String _manufactureName;
	private List<ManufactureItem> _createList = Collections.emptyList();
	
	private String _sellStoreName;
	private String _packageSellStoreName;
	private List<TradeItem> _sellList = Collections.emptyList();
	private List<TradeItem> _packageSellList = Collections.emptyList();
	
	private String _buyStoreName;
	private List<TradeItem> _buyList = Collections.emptyList();
	
	private List<TradeItem> _tradeList = Collections.emptyList();

	private Party _party;
	private Location _lastPartyPosition;
	private long _startingTimeInFullParty = 0;
	private long _startingTimeInParty = 0;

	private Clan _clan;
	private PledgeRank _pledgeRank = PledgeRank.VAGABOND;
	private int _pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;

	
	private int _accessLevel;
	private PlayerAccess _playerAccess = new PlayerAccess();

	private boolean _messageRefusal = false, _tradeRefusal = false, _blockAll = false;

	private boolean _inCtF = false;
	
	
	public static final int MAX_SUMMON_COUNT = 4;	
	private IntObjectMap<SummonInstance> _summons = new CHashIntObjectMap<SummonInstance>(MAX_SUMMON_COUNT); 
	private PetInstance _pet = null;
	private SymbolInstance _symbol = null;

	private boolean _riding;
	
	private int _botRating;

	private List<DecoyInstance> _decoys = new CopyOnWriteArrayList<DecoyInstance>();

	private IntObjectMap<Cubic> _cubics = null;
	private int _agathionId = 0;

	private Request _request;

	private ItemInstance _arrowItem;

	
	private WeaponTemplate _fistsWeaponItem;

	private Map<Integer, String> _chars = new HashMap<Integer, String>(8);

	private ItemInstance _enchantScroll = null;
	private ItemInstance _appearanceStone = null;
	private ItemInstance _appearanceExtractItem = null;

	private WarehouseType _usingWHType;

	private boolean _isOnline = false;

	private final AtomicBoolean _isLogout = new AtomicBoolean();

	
	private HardReference<NpcInstance> _lastNpc = HardReferences.emptyRef();
	
	private MultiSellListContainer _multisell = null;

	private IntObjectMap<SoulShotType> _activeAutoShots = new CHashIntObjectMap<SoulShotType>();

	private ObservePoint _observePoint;
	private AtomicInteger _observerMode = new AtomicInteger(0);

	public int _telemode = 0;

	private int _handysBlockCheckerEventArena = -1;
	
	public boolean entering = true;

	
	private Location _stablePoint = null;

	
	public int _loto[] = new int[5];
	
	public int _race[] = new int[2];

	private final BlockList _blockList = new BlockList(this);
	private final FriendList _friendList = new FriendList(this);
	private final MenteeList _menteeList = new MenteeList(this);
	private final PremiumItemList _premiumItemList = new PremiumItemList(this);
	private final ProductHistoryList _productHistoryList = new ProductHistoryList(this);
	private final HennaList _hennaList = new HennaList(this);

	private final AttendanceRewards _attendanceRewards = new AttendanceRewards(this);
	private final DailyMissionList _dailiyMissionList = new DailyMissionList(this);

	private final FactionList _factionList = new FactionList(this);

	private boolean _hero = false;

	private PremiumAccountTemplate _premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(0);
	private Future<?> _premiumAccountExpirationTask;

	private boolean _isSitting;

	private ChairInstance _chairObject;

	private NobleType _nobleType = NobleType.NONE;
	
	private boolean _inOlympiadMode;
	private OlympiadGame _olympiadGame;
	private ObservableArena _observableArena;

	private int _olympiadSide = -1;

	
	private int _varka = 0;
	private int _ketra = 0;
	private int _ram = 0;

	private byte[] _keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;

	private int _cursedWeaponEquippedId = 0;
	
	private final Fishing _fishing = new Fishing(this);

	private Future<?> _taskWater;
	private Future<?> _autoSaveTask;
	private Future<?> _kickTask;

	private Future<?> _pcCafePointsTask;
	private Future<?> _unjailTask;
	private Future<?> _trainingCampTask;

	private final Lock _storeLock = new ReentrantLock();

	private int _zoneMask;

	private boolean _offline = false;
	private boolean _awaying = false;

	private boolean _registeredInEvent = false;
	
	private int _destructionCount = 0;
	private int _markEndureCount = 0;
	private int _pcBangPoints;

	private int _expandInventory = 0;
	private int _expandWarehouse = 0;
	private int _battlefieldChatId;
	private int _lectureMark;

	private AtomicState _gmInvisible = new AtomicState();
	private AtomicState _gmUndying = new AtomicState();

	private IntObjectMap<String> _postFriends = Containers.emptyIntObjectMap();

	private List<String> _blockedActions = new ArrayList<String>();

	private BypassStorage _bypassStorage = new BypassStorage();

	private boolean _notShowBuffAnim = false;
	private boolean _notShowTraders = false;
	private boolean _canSeeAllShouts = false;
	private boolean _debug = false;

	private long _dropDisabled;
	private long _lastItemAuctionInfoRequest;

	private IntObjectPair<OnAnswerListener> _askDialog = null;

	private boolean _matchingRoomWindowOpened = false;
	private MatchingRoom _matchingRoom;
	private PetitionMainGroup _petitionGroup;
	private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<Integer, Long>();

	private Language _language = Config.DEFAULT_LANG;

	private JumpTrack _currentJumpTrack = null;
	private JumpWay _currentJumpWay = null;
	
	private TIntSet _disabledAnalogSkills = new TIntHashSet();
	
	private int _npcDialogEndTime = 0;

	private Mount _mount = null;

	private final Map<String, CharacterVariable> _variables = new ConcurrentHashMap<String, CharacterVariable>();

	private final DeathPenalty _deathPenalty = new DeathPenalty(this);

	private List<RestoredSummon> _restoredSummons = null;

	private IntObjectMap<SkillChain> _skillChainDetail = new HashIntObjectMap<SkillChain>();

	private boolean _autoSearchParty;
	private Future<?> _substituteTask;

	private JumpState _jumpState = JumpState.NONE;

	private TransformTemplate _transform = null;

	private final IntObjectMap<SkillEntry> _transformSkills = new CHashIntObjectMap<SkillEntry>();

	private long _lastMultisellBuyTime = 0L;
	private long _lastEnchantItemTime = 0L;
	private long _lastAttributeItemTime = 0L;

	private Future<?> _enableRelationTask;
	
	private boolean _isInReplaceTeleport = false;

	private int _armorSetEnchant = 0;

	private int _usedWorldChatPoints = 0;

	private boolean _hideHeadAccessories = false;

	private ItemInstance _synthesisItem1 = null;
	private ItemInstance _synthesisItem2 = null;

	private final IntObjectMap<SkillEntry> _alchemySkills = new CHashIntObjectMap<SkillEntry>();

	private List<TrapInstance> _traps = Collections.emptyList();
	private boolean _isInJail = false;
	private final IntObjectMap<OptionDataTemplate> _options = new CTreeIntObjectMap<OptionDataTemplate>();
	private long _receivedExp = 0L;
	private Reflection _activeReflection = null;
	private int _questZoneId = -1;
	private ClassId _selectedMultiClassId = null;

	public static enum JumpState
	{
		NONE,
		IN_PROGRESS,
		FINISHED;
	}

	
	public Player(final int objectId, final PlayerTemplate template, final String accountName)
	{
		super(objectId, template);

		_baseTemplate = template;
		_login = accountName;
	}

	private Player(final FakePlayerAITemplate fakeAiTemplate, final int objectId, final PlayerTemplate template)
	{
		this(objectId, template, null);
		_ai = new FakeAI(this, fakeAiTemplate);
	}

	
	private Player(final int objectId, final PlayerTemplate template)
	{
		this(objectId, template, null);

		if(GameObjectsStorage.getPlayers().size() >= GameServer.getInstance().getOnlineLimit())
		{
			kick();
			return;
		}

		_baseTemplate = template;

		_ai = new PlayerAI(this);

		if(!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
			setPlayerAccess(Config.gmlist.get(objectId));
		else
			setPlayerAccess(Config.gmlist.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<Player> getRef()
	{
		return (HardReference<Player>) super.getRef();
	}

	public String getAccountName()
	{
		if(_connection == null)
			return _login;
		return _connection.getLogin();
	}

	public String getIP()
	{
		if(_connection == null)
			return NOT_CONNECTED;
		return _connection.getIpAddr();
	}

	public String getLogin()
	{
		return _login;
	}

	public void setLogin(String val)
	{
		_login = val;
	}

	
	public Map<Integer, String> getAccountChars()
	{
		return _chars;
	}

	@Override
	public final PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) super.getTemplate();
	}

	@Override
	public final void setTemplate(CreatureTemplate template)
	{
		if(isBaseClassActive())
			_baseTemplate = (PlayerTemplate) template;

		super.setTemplate(template);
	}

	public final PlayerTemplate getBaseTemplate()
	{
		return _baseTemplate;
	}

	@Override
	public final boolean isTransformed()
	{
		return _transform != null;
	}

	@Override
	public final TransformTemplate getTransform()
	{
		return _transform;
	}

	@Override
	public final void setTransform(int id)
	{
		TransformTemplate template = id > 0 ? TransformTemplateHolder.getInstance().getTemplate(getSex(), id) : null;
		setTransform(template);
	}

	@Override
	public final void setTransform(TransformTemplate transform)
	{
		if(transform == _transform || transform != null && _transform != null)
			return;

		boolean isFlying = false;
		final boolean isVisible = isVisible();

		
		if(transform == null) 
		{
			isFlying = _transform.getType() == TransformType.FLYING;

			if(isFlying)
			{
				decayMe();
				setFlying(false);
				setLoc(getLoc().correctGeoZ());
			}

			if(!_transformSkills.isEmpty())
			{
				
				for(SkillEntry skillEntry : _transformSkills.values())
				{
					if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skillEntry.getTemplate()))
						super.removeSkill(skillEntry);
				}
				_transformSkills.clear();
			}

			if(_transform.getItemCheckType() != LockType.NONE)
				getInventory().unlock();

			_transform = transform;

			checkActiveToggleEffects();

			
			getAbnormalList().stop(AbnormalType.transform);
		}
		else
		{
			isFlying = transform.getType() == TransformType.FLYING;

			if(isFlying || isCursedWeaponEquipped())
			{
				for(Servitor servitor : getServitors())
					servitor.unSummon(false);
			}
			
            if(isFlying)
            {
				decayMe();
				setFlying(true);
				setLoc(getLoc().changeZ(transform.getSpawnHeight())); 

		        for(SkillEntry skillEntry : getAllSkillsArray())
		        {
		        	if(SkillAcquireHolder.getInstance().isSkillPossible(this, skillEntry.getTemplate(), AcquireType.COLLECTION))
		        		_transformSkills.put(skillEntry.getId(), skillEntry); 
		        } 
			}

			
			for(SkillLearn sl : transform.getSkills())
			{
				SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
				if(skillEntry == null)
					continue;

				_transformSkills.put(skillEntry.getId(), skillEntry);
			}

			
			for(SkillLearn sl : transform.getAddtionalSkills())
			{
				if(sl.getMinLevel() > getLevel())
					continue;

				SkillEntry skillEntry = _transformSkills.get(sl.getId());
				if(skillEntry != null && skillEntry.getLevel() >= sl.getLevel())
					continue;

				skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
				if(skillEntry == null)
					continue;

				_transformSkills.put(skillEntry.getId(), skillEntry);
			}

			if(!isInOlympiadMode() && isCursedWeaponEquipped() && isHero() && isBaseClassActive())
			{
				
				for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.HERO))
				{
					SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
					if(skillEntry == null)
						continue;

					SkillEntry transformSkillEntry = _transformSkills.get(sl.getId());
					if(transformSkillEntry != null && transformSkillEntry.getLevel() >= skillEntry.getLevel())
						continue;

					_transformSkills.put(skillEntry.getId(), skillEntry);
		        }
			}

			for(SkillEntry skillEntry : _transformSkills.values())
				addSkill(skillEntry, false);

			if(transform.getItemCheckType() != LockType.NONE)
			{
				getInventory().unlock();
				getInventory().lockItems(transform.getItemCheckType(), transform.getItemCheckIDs());
			}

			checkActiveToggleEffects();

			_transform = transform;
		}

		sendPacket(new ExBasicActionList(this));
		sendSkillList();
		sendPacket(new ShortCutInitPacket(this));

		sendActiveAutoShots();

		if(isFlying && isVisible)
			spawnMe();

		sendChanges();
	}

	public void changeSex()
	{
		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), getClassId(), getSex().revert());
		if(template == null)
			return;

		setTemplate(template);
		if(isTransformed())
		{
			int transformId = getTransform().getId();
			setTransform(null);
			setTransform(transformId);
		}
	}

	@Override
	public PlayerAI getAI()
	{
		return (PlayerAI) _ai;
	}

	@Override
	public void doCast(final SkillEntry skillEntry, final Creature target, boolean forceUse)
	{
		if(skillEntry == null)
			return;

		super.doCast(skillEntry, target, forceUse);
	}

	@Override
	public void sendReuseMessage(Skill skill)
	{
		if(isCastingNow() && !isDualCastEnable() || isCastingNow() && isDualCastEnable() && isDualCastingNow())
			return;

		TimeStamp sts = getSkillReuse(skill);
		if(sts == null || !sts.hasNotPassed())
			return;
		long timeleft = sts.getReuseCurrent();
		if(!Config.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500)
			return;
		long hours = timeleft / 3600000;
		long minutes = (timeleft - hours * 3600000) / 60000;
		long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
		if(hours > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
		else if(minutes > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
		else
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
	}

	@Override
	public final int getLevel()
	{
		return getActiveSubClass() == null ? 1 : getActiveSubClass().getLevel();
	}

	@Override
	public final Sex getSex()
	{
		return getTemplate().getSex();
	}

	public int getFace()
	{
		return _face;
	}

	public void setFace(int face)
	{
		_face = face;
	}

	public int getBeautyFace()
	{
		return _beautyFace;
	}

	public void setBeautyFace(int face)
	{
		_beautyFace = face;
	}

	public int getHairColor()
	{
		return _hairColor;
	}

	public void setHairColor(int hairColor)
	{
		_hairColor = hairColor;
	}

	public int getBeautyHairColor()
	{
		return _beautyHairColor;
	}

	public void setBeautyHairColor(int hairColor)
	{
		_beautyHairColor = hairColor;
	}

	public int getHairStyle()
	{
		return _hairStyle;
	}

	public void setHairStyle(int hairStyle)
	{
		_hairStyle = hairStyle;
	}

	public int getBeautyHairStyle()
	{
		return _beautyHairStyle;
	}

	public void setBeautyHairStyle(int hairStyle)
	{
		_beautyHairStyle = hairStyle;
	}

	public void offline()
	{
		offline(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);
	}

	public void offline(int delay)
	{
		if(_connection != null)
		{
			_connection.setActiveChar(null);
			_connection.close(ServerCloseSocketPacket.STATIC);
			setNetConnection(null);
		}

		startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL_EFFECT);
		setOfflineMode(true);

		if(isInBuffStore())
			OfflineBufferManager.getInstance().storeBufferData(this);
		else
			storePrivateStore();

		if(delay > 0)
		{
			setVar(isInBuffStore() ? "offlinebuff" : "offline", delay + System.currentTimeMillis() / 1000L);
			startKickTask(delay * 1000L);
		}
		else
			setVar(isInBuffStore() ? "offlinebuff" : "offline", Integer.MAX_VALUE);

		Party party = getParty();
		if(party != null)
			leaveParty();

		if(isAutoSearchParty())
			PartySubstituteManager.getInstance().removeWaitingPlayer(this);


		for(Servitor servitor : getServitors())
			servitor.unSummon(false);

		CursedWeaponsManager.getInstance().doLogout(this);
		
		Olympiad.logoutPlayer(this);

		if(isFishing())
			getFishing().stop();

		MatchingRoomManager.getInstance().removeFromWaitingList(this);

		broadcastCharInfo();
		stopWaterTask();
		stopPremiumAccountTask();
		stopHourlyTask();
		stopPcBangPointsTask();
		stopTrainingCampTask();
		stopAutoSaveTask();
		stopQuestTimers();
		stopEnableUserRelationTask();
		broadcastUserInfo(true);

		try
		{
			getInventory().store();
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}

		try
		{
			store(false);
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}
	}

	
	public void kick()
	{
		if(isCursedWeaponEquipped() && Config.DROP_CURSED_WEAPONS_ON_KICK)
		{
			CursedWeaponsManager.getInstance().dropPlayer(this);
			setPvpFlag(0);
		}
		
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.close(LogOutOkPacket.STATIC);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	
	public void restart()
	{
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.setActiveChar(null);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	
	public void logout()
	{
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.close(ServerCloseSocketPacket.STATIC);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	private void prepareToLogout1()
	{
		if(isCursedWeaponEquipped() && Config.DROP_CURSED_WEAPONS_ON_LOGOUT)
		{
			CursedWeaponsManager.getInstance().dropPlayer(this);
			setPvpFlag(0);
		}
		
		for(Servitor servitor : getServitors())
			sendPacket(new PetDeletePacket(servitor.getObjectId(), servitor.getServitorType()));

		if(isProcessingRequest())
		{
			Request request = getRequest();
			if(isInTrade())
			{
				Player parthner = request.getOtherPlayer(this);
				parthner.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
				parthner.sendPacket(TradeDonePacket.FAIL);
			}
			request.cancel();
		}
		World.removeObjectsFromPlayer(this);
	}

	private void prepareToLogout2()
	{
		if(_isLogout.getAndSet(true))
			return;

		for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_QUIT_GAME))
			hook.onPlayerQuitGame(this);

		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_QUIT_GAME))
			hook.onPlayerQuitGame(this);

		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if(attachment != null)
			attachment.onLogout(this);

		setNetConnection(null);
		setIsOnline(false);

		getListeners().onExit();

		if(isFlying() && !checkLandingState())
			_stablePoint = TeleportUtils.getRestartPoint(this, RestartType.TO_VILLAGE).getLoc();

		if(isCastingNow() || isDualCastingNow())
			abortCast(true, true);

		Party party = getParty();
		if(party != null)
			leaveParty();
		
		CursedWeaponsManager.getInstance().doLogout(this);
		
		if(_observableArena != null)
			_observableArena.removeObserver(_observePoint);

		Olympiad.logoutPlayer(this);

		if(isFishing())
			getFishing().stop();

		if(_stablePoint != null)
			teleToLocation(_stablePoint);

		for(Servitor servitor : getServitors())
			servitor.unSummon(true);

		if(isMounted())
			_mount.onLogout();

		_friendList.notifyFriends(false);

		mentoringLogoutConditions();
		
		if(getClan() != null)
			getClan().loginClanCond(this, false);

		if(isProcessingRequest())
			getRequest().cancel();

		stopAllTimers();

		if(isInBoat())
			getBoat().removePlayer(this);

		SubUnit unit = getSubUnit();
		UnitMember member = unit == null ? null : unit.getUnitMember(getObjectId());
		if(member != null)
		{
			int sponsor = member.getSponsor();
			int apprentice = getApprentice();
			PledgeShowMemberListUpdatePacket memberUpdate = new PledgeShowMemberListUpdatePacket(this);
			for(Player clanMember : _clan.getOnlineMembers(getObjectId()))
			{
				clanMember.sendPacket(memberUpdate);
				if(clanMember.getObjectId() == sponsor)
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(_name));
				else if(clanMember.getObjectId() == apprentice)
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(_name));
			}
			member.setPlayerInstance(this, true);
		}
		
		if(CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null)
			CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
		
		MatchingRoom room = getMatchingRoom();
		if(room != null)
		{
			if(room.getLeader() == this)
				room.disband();
			else
				room.removeMember(this, false);
		}
		setMatchingRoom(null);

		MatchingRoomManager.getInstance().removeFromWaitingList(this);

		destroyAllTraps();

		if(!_decoys.isEmpty())
		{
			for(DecoyInstance decoy : getDecoys())
			{
				decoy.unSummon();
				removeDecoy(decoy);
			}
		}

		stopPvPFlag();

		Reflection ref = getReflection();

		if(!ref.isMain())
		{
			if(ref.getReturnLoc() != null)
				_stablePoint = ref.getReturnLoc();

			ref.removeObject(this);
		}

		try
		{
			getInventory().store();
			getRefund().clear();
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}

		try
		{
			store(false);
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}
	}

	
	public Collection<RecipeTemplate> getDwarvenRecipeBook()
	{
		return _recipebook.values();
	}

	public Collection<RecipeTemplate> getCommonRecipeBook()
	{
		return _commonrecipebook.values();
	}

	public int recipesCount()
	{
		return _commonrecipebook.size() + _recipebook.size();
	}

	public boolean hasRecipe(final RecipeTemplate id)
	{
		return _recipebook.containsValue(id) || _commonrecipebook.containsValue(id);
	}

	public boolean findRecipe(final int id)
	{
		return _recipebook.containsKey(id) || _commonrecipebook.containsKey(id);
	}

	
	public void registerRecipe(final RecipeTemplate recipe, boolean saveDB)
	{
		if(recipe == null)
			return;

		if(recipe.isCommon())
			_commonrecipebook.put(recipe.getId(), recipe);
		else
			_recipebook.put(recipe.getId(), recipe);

		if(saveDB)
			mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", getObjectId(), recipe.getId());
	}

	
	public void unregisterRecipe(final int RecipeID)
	{
		if(_recipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_recipebook.remove(RecipeID);
		}
		else if(_commonrecipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_commonrecipebook.remove(RecipeID);
		}
		else
			_log.warn("Attempted to remove unknown RecipeList" + RecipeID);
	}

	public QuestState getQuestState(int id)
	{
		questRead.lock();
		try
		{
			return _quests.get(id);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public QuestState getQuestState(Quest quest)
	{
		return getQuestState(quest.getId());
	}

	public boolean isQuestCompleted(int id)
	{
		QuestState qs = getQuestState(id);
		return qs != null && qs.isCompleted();
	}

	public boolean isQuestCompleted(Quest quest)
	{
		return isQuestCompleted(quest.getId());
	}

	public void setQuestState(QuestState qs)
	{
		questWrite.lock();
		try
		{
			_quests.put(qs.getQuest().getId(), qs);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public void removeQuestState(int id)
	{
		questWrite.lock();
		try
		{
			_quests.remove(id);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public void removeQuestState(Quest quest)
	{
		removeQuestState(quest.getId());
	}

	public Quest[] getAllActiveQuests()
	{
		List<Quest> quests = new ArrayList<Quest>(_quests.size());
		questRead.lock();
		try
		{
			for(final QuestState qs : _quests.values())
				if(qs.isStarted())
					quests.add(qs.getQuest());
		}
		finally
		{
			questRead.unlock();
		}
		return quests.toArray(new Quest[quests.size()]);
	}

	public QuestState[] getAllQuestsStates()
	{
		questRead.lock();
		try
		{
			return _quests.values().toArray(new QuestState[_quests.size()]);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public List<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event)
	{
		List<QuestState> states = new ArrayList<QuestState>();
		Set<Quest> quests = npc.getTemplate().getEventQuests(event);
		if(quests != null)
		{
			QuestState qs;
			for(Quest quest : quests)
			{
				qs = getQuestState(quest);
				if(qs != null && !qs.isCompleted())
					states.add(getQuestState(quest));
			}
		}
		return states;
	}

	public void processQuestEvent(int questId, String event, NpcInstance npc)
	{
		if(event == null)
			event = "";
		QuestState qs = getQuestState(questId);
		if(qs == null)
		{
			Quest q = QuestHolder.getInstance().getQuest(questId);
			if(q == null)
			{
				_log.warn("Quest ID[" + questId + "] not found!");
				return;
			}
			qs = q.newQuestState(this);
		}
		if(qs == null || qs.isCompleted())
			return;
		qs.getQuest().notifyEvent(event, qs, npc);
		sendPacket(new QuestListPacket(this));
	}

	public boolean isInventoryFull()
	{
		if(getWeightPenalty() >= 3 || getInventoryLimit() * 0.8 < getInventory().getSize())
			return true;

		return false;
	}

	
	public boolean isQuestContinuationPossible(boolean msg)
	{
		if(isInventoryFull() || Config.QUEST_INVENTORY_MAXIMUM * 0.8 < getInventory().getQuestSize())
		{
			if(msg)
				sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
			return false;
		}
		return true;
	}

	
	public void stopQuestTimers()
	{
		for(QuestState qs : getAllQuestsStates())
			if(qs.isStarted())
				qs.pauseQuestTimers();
			else
				qs.stopQuestTimers();
	}

	
	public void resumeQuestTimers()
	{
		for(QuestState qs : getAllQuestsStates())
			qs.resumeQuestTimers();
	}

	

	public Collection<ShortCut> getAllShortCuts()
	{
		return _shortCuts.getAllShortCuts();
	}

	public ShortCut getShortCut(int slot, int page)
	{
		return _shortCuts.getShortCut(slot, page);
	}

	public void registerShortCut(ShortCut shortcut)
	{
		_shortCuts.registerShortCut(shortcut);
	}

	public void deleteShortCut(int slot, int page)
	{
		_shortCuts.deleteShortCut(slot, page);
	}

	public void registerMacro(Macro macro)
	{
		_macroses.registerMacro(macro);
	}

	public void deleteMacro(int id)
	{
		_macroses.deleteMacro(id);
	}

	public MacroList getMacroses()
	{
		return _macroses;
	}

	public boolean isCastleLord(int castleId)
	{
		return _clan != null && isClanLeader() && _clan.getCastle() == castleId;
	}
	
	public boolean isFortressLord(int fortressId)
	{
		return _clan != null && isClanLeader() && _clan.getHasFortress() == fortressId;
	}
	
	public int getPkKills()
	{
		return _pkKills;
	}

	public void setPkKills(final int pkKills)
	{
		_pkKills = pkKills;
	}

	public long getCreateTime()
	{
		return _createTime;
	}

	public void setCreateTime(final long createTime)
	{
		_createTime = createTime;
	}

	public int getDeleteTimer()
	{
		return _deleteTimer;
	}

	public void setDeleteTimer(final int deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}

	@Override
	public int getCurrentLoad()
	{
		return getInventory().getTotalWeight();
	}

	public long getLastAccess()
	{
		return _lastAccess;
	}

	public void setLastAccess(long value)
	{
		_lastAccess = value;
	}

	public int getRecomHave()
	{
		return _recomHave;
	}

	public void setRecomHave(int value)
	{
		if(value > 255)
			_recomHave = 255;
		else if(value < 0)
			_recomHave = 0;
		else
			_recomHave = value;
	}

	public int getRecomLeft()
	{
		return _recomLeft;
	}

	public void setRecomLeft(final int value)
	{
		_recomLeft = value;
	}

	public int addRecomLeft()
	{
		int recoms = 0;
		if(getRecomLeftToday() < 20)
			recoms = 10;
		else
			recoms = 1;
		setRecomLeft(getRecomLeft() + recoms);
		setRecomLeftToday(getRecomLeftToday() + recoms);
		sendUserInfo(true);
		return recoms;
	}

	public int getRecomLeftToday()
	{
		return _recomLeftToday;
	}

	public void setRecomLeftToday(final int value)
	{
		_recomLeftToday = value;
		setVar("recLeftToday", _recomLeftToday);
	}

	public void giveRecom(final Player target)
	{
		int targetRecom = target.getRecomHave();
		if(targetRecom < 255)
			target.addRecomHave(1);
		if(getRecomLeft() > 0)
			setRecomLeft(getRecomLeft() - 1);

		sendUserInfo(true);
	}

	public void addRecomHave(final int val)
	{
		setRecomHave(getRecomHave() + val);
		broadcastUserInfo(true);
	}

	@Override
	public int getKarma()
	{
		return _karma;
	}

	public void setKarma(int karma)
	{
		if(_karma == karma)
			return;

		_karma = karma;

		sendChanges();

		for(Servitor servitor : getServitors())
			servitor.broadcastCharInfo();
	}

	@Override
	public int getMaxLoad()
	{
		return (int) calcStat(Stats.MAX_LOAD, 69000, this, null);
	}

	@Override
	public void updateAbnormalIcons()
	{
		if(entering || isLogoutStarted())
			return;

		super.updateAbnormalIcons();
	}

	@Override
	public void updateAbnormalIconsImpl()
	{
		Abnormal[] effects = getAbnormalList().toArray();
		Arrays.sort(effects, AbnormalsComparator.getInstance());

		PartySpelledPacket ps = new PartySpelledPacket(this, false);
		AbnormalStatusUpdatePacket abnormalStatus = new AbnormalStatusUpdatePacket();

		for(Abnormal effect : effects)
		{
			if(effect == null)
				continue;
			if(effect.checkAbnormalType(AbnormalType.hp_recover))
				sendPacket(new ShortBuffStatusUpdatePacket(effect));
			else
				effect.addIcon(abnormalStatus);
			if(_party != null)
				effect.addPartySpelledIcon(ps);
		}

		sendPacket(abnormalStatus);
		if(_party != null)
			_party.broadCast(ps);

		if(isInOlympiadMode() && isOlympiadCompStart())
		{
			OlympiadGame olymp_game = _olympiadGame;
			if(olymp_game != null)
			{
				ExOlympiadSpelledInfoPacket olympiadSpelledInfo = new ExOlympiadSpelledInfoPacket();

				for(Abnormal effect : effects)
					if(effect != null)
						effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);

				sendPacket(olympiadSpelledInfo);

				for(ObservePoint observer : olymp_game.getObservers())
					observer.sendPacket(olympiadSpelledInfo);
			}
		}

		final List<SingleMatchEvent> events = getEvents(SingleMatchEvent.class);
		for(SingleMatchEvent event : events)
			event.onEffectIconsUpdate(this, effects);

		super.updateAbnormalIconsImpl();
	}

	@Override
	public int getWeightPenalty()
	{
		return getSkillLevel(4270, 0);
	}

	public void refreshOverloaded()
	{
		if(isLogoutStarted() || getMaxLoad() <= 0)
			return;

		setOverloaded(getCurrentLoad() > getMaxLoad());
		double weightproc = 100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null)) / getMaxLoad();
		int newWeightPenalty = 0;

		if(weightproc < 50)
			newWeightPenalty = 0;
		else if(weightproc < 66.6)
			newWeightPenalty = 1;
		else if(weightproc < 80)
			newWeightPenalty = 2;
		else if(weightproc < 100)
			newWeightPenalty = 3;
		else
			newWeightPenalty = 4;

		int current = getWeightPenalty();
		if(current == newWeightPenalty)
			return;

		if(newWeightPenalty > 0)
			addSkill(SkillHolder.getInstance().getSkillEntry(4270, newWeightPenalty));
		else
			super.removeSkill(getKnownSkill(4270));

		sendSkillList();
		sendEtcStatusUpdate();
		updateStats();
	}

	public int getArmorsExpertisePenalty()
	{
		return getSkillLevel(6213, 0);
	}

	public int getWeaponsExpertisePenalty()
	{
		return getSkillLevel(6209, 0);
	}

	public int getExpertisePenalty(ItemInstance item)
	{
		if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
			return getWeaponsExpertisePenalty();
		else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
			return getArmorsExpertisePenalty();
		return 0;
	}

	public void refreshExpertisePenalty()
	{
		if(isLogoutStarted())
			return;

		
		int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
		int skillLvl = 0;
		for(skillLvl = 0; skillLvl < EXPERTISE_LEVELS.length; skillLvl++)
			if(level < EXPERTISE_LEVELS[skillLvl + 1])
				break;

		skillLvl = Math.max(skillLvl, (int) calcStat(Stats.ADDITIONAL_EXPERTISE_INDEX));
		if(skillLvl == 7)
			skillLvl--;

		boolean skillUpdate = false; 

		if(skillLvl > 0)
		{
			while(skillLvl >= 1)
			{
				SkillEntry skill = SkillHolder.getInstance().getSkillEntry(239, skillLvl);
				if(skill != null)
				{
					if(addSkill(skill, false) != skill)
						skillUpdate = true;
					break;
				}
				else
					skillLvl--;
			}
		}

		if(Config.EXPERTISE_PENALTY_ENABLED)
		{
			int expertiseIndex = getExpertiseIndex();
			int newWeaponPenalty = 0;
			int newArmorPenalty = 0;
			ItemInstance[] items = getInventory().getPaperdollItems();
			for(ItemInstance item : items)
				if(item != null)
				{
					int crystaltype = item.getTemplate().getGrade().ordinal();
					if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
					{
						if(crystaltype > newWeaponPenalty)
							newWeaponPenalty = crystaltype;
					}
					else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
					{
						if(crystaltype > expertiseIndex)
						{
							if(item.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
								newArmorPenalty++;
							newArmorPenalty++;
						}
					}
				}

			
			newWeaponPenalty = newWeaponPenalty - expertiseIndex;
			newWeaponPenalty = Math.max(0, Math.min(4, newWeaponPenalty));

			
			newArmorPenalty = Math.max(0, Math.min(4, newArmorPenalty));

			int weaponExpertise = getWeaponsExpertisePenalty();
			int armorExpertise = getArmorsExpertisePenalty();

			if(weaponExpertise != newWeaponPenalty)
			{
				weaponExpertise = newWeaponPenalty;
				if(newWeaponPenalty > 0)
					addSkill(SkillHolder.getInstance().getSkillEntry(6209, weaponExpertise));
				else
					super.removeSkill(getKnownSkill(6209));
				skillUpdate = true;
			}
			if(armorExpertise != newArmorPenalty)
			{
				armorExpertise = newArmorPenalty;
				if(newArmorPenalty > 0)
					addSkill(SkillHolder.getInstance().getSkillEntry(6213, armorExpertise));
				else
					super.removeSkill(getKnownSkill(6213));
				skillUpdate = true;
			}
		}

		if(skillUpdate)
		{
			getInventory().validateItemsSkills();

			sendSkillList();
			sendEtcStatusUpdate();
			updateStats();
		}
	}

	public int getPvpKills()
	{
		return _pvpKills;
	}

	public void setPvpKills(int pvpKills)
	{
		_pvpKills = pvpKills;
	}

	public ClassLevel getClassLevel()
	{
		return getClassId().getClassLevel();
	}

	public boolean isAcademyGraduated()
	{
		return getVarBoolean(ACADEMY_GRADUATED_VAR, false);
	}

	public void addClanPointsOnProfession(final int id)
	{
		ClassId classId = ClassId.VALUES[id];
		if(getPledgeType() == Clan.SUBUNIT_ACADEMY && getLvlJoinedAcademy() > 0 && getClan() != null && getClan().getLevel() >= 5 && (!classId.isOfRace(Race.ERTHEIA) && classId.isOfLevel(ClassLevel.AWAKED) || classId.isOfRace(Race.ERTHEIA) && classId.isOfLevel(ClassLevel.THIRD)))
		{
			int earnedPoints = Math.min(1000, Math.min((85 - getLvlJoinedAcademy()), 40) * 20 + 200);

			getClan().setAcademyGraduatesCount(getClan().getAcademyGraduatesCount() + 1);
			getClan().updateClanInDB();

			getClan().removeClanMember(getObjectId());

			SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_AWAKENED_OBTAINING_S2_CLAN_REPUTATION);
			sm.addName(this);
			sm.addInteger(getClan().incReputation(earnedPoints, true, "Academy"));
			getClan().broadcastToOnlineMembers(sm);

			if((getClan().getAcademyGraduatesCount() % 10) == 0)
			{
				sm = new SystemMessagePacket(SystemMsg.THE_NUMBER_OF_GRADUATES_OF_THE_CLAN_ACADEMY_IS_S1_S2_BONUS_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION);
				sm.addInteger(getClan().getAcademyGraduatesCount());
				sm.addInteger(getClan().incReputation(1000, true, "Academy"));
				getClan().broadcastToOnlineMembers(sm);
			}

			getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListDeletePacket(getName()), this);

			setClan(null);
			setTitle("");
			sendPacket(SystemMsg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
			setLeaveClanTime(0);
			setVar(ACADEMY_GRADUATED_VAR, true);

			broadcastCharInfo();

			sendPacket(PledgeShowMemberListDeleteAllPacket.STATIC);
		}
	}

	
	public synchronized void setClassId(final int id, boolean noban)
	{
		ClassId classId = ClassId.VALUES[id];
		if(classId.isDummy())
			return;
		if(!noban && !(classId.equalsOrChildOf(getClassId()) || getPlayerAccess().CanChangeClass || Config.EVERYBODY_HAS_ADMIN_RIGHTS))
		{
			Thread.dumpStack();
			return;
		}

		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), classId, getSex());
		if(template == null)
		{
			_log.error("Missing template for classId: " + id);
			return;
		}
		setTemplate(template);

		
		if(!_subClassList.containsClassId(id))
		{
			final SubClass cclass = getActiveSubClass();
			ClassId oldClass = ClassId.VALUES[cclass.getClassId()];

			_subClassList.changeSubClassId(oldClass.getId(), id);
			changeClassInDb(oldClass.getId(), id, cclass.getDefaultClassId());

			if(cclass.isBase())
				addClanPointsOnProfession(classId.getId());

			onReceiveNewClassId(oldClass, classId);

			storeCharSubClasses();

			getListeners().onClassChange(oldClass, classId);

			for(QuestState qs : getAllQuestsStates())
				qs.getQuest().notifyTutorialEvent("CE", false, "100", qs);
		}
		else
			getListeners().onClassChange(null, classId);

		broadcastUserInfo(true);

		
		if(isInParty())
			getParty().broadCast(new PartySmallWindowUpdatePacket(this));
		if(getClan() != null)
			getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));
		if(_matchingRoom != null)
			_matchingRoom.broadcastPlayerUpdate(this);
	}

	private void onReceiveNewClassId(ClassId oldClass, ClassId newClass)
	{
		if(oldClass != null)
		{
			if(isBaseClassActive())
			{
				OlympiadParticipiantData participant = Olympiad.getParticipantInfo(getObjectId());
				if(participant != null)
					participant.setClassId(newClass.getId());
			}

			if(oldClass.isOfLevel(ClassLevel.AWAKED))
			{
				getAbnormalList().stopAll(); 

				deleteCubics();
				
				
				for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.CHAOS))
				{
					SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
					if(skillEntry == null)
						continue;

					removeSkill(skillEntry, true);
				}

				
				for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.DUAL_CHAOS))
				{
					SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
					if(skillEntry == null)
						continue;

					removeSkill(skillEntry, true);
				}
			}

			if(!newClass.equalsOrChildOf(oldClass) || newClass.isOfLevel(ClassLevel.AWAKED))
			{
				removeAllSkills();
				restoreSkills();
				rewardSkills(false);

				checkSkills();

				refreshExpertisePenalty();

				getInventory().refreshEquip();
				getInventory().validateItems();

				getHennaList().refreshStats(true);

				sendSkillList();

				updateStats();
			}
			else
				rewardSkills(true);

			
			switch(oldClass)
			{
				case CARDINAL:
					ItemFunctions.deleteItemsEverywhere(this, 15307);
					break;
				case EVAS_SAINT:
					ItemFunctions.deleteItemsEverywhere(this, 15308);
					break;
				case SHILLIEN_SAINT:
					ItemFunctions.deleteItemsEverywhere(this, 15309);
					break;
			}
		}

		
		switch(newClass)
		{
			case CARDINAL:
				ItemFunctions.addItem(this, 15307, 7, true);
				break;
			case EVAS_SAINT:
				ItemFunctions.addItem(this, 15308, 7, true);
				break;
			case SHILLIEN_SAINT:
				ItemFunctions.addItem(this, 15309, 7, true);
				break;
		}

		if(oldClass == null || newClass.getClassLevel().ordinal() > oldClass.getClassLevel().ordinal())
		{
			if(newClass.isOfLevel(ClassLevel.AWAKED))
			{
				ClassId baseAwakedClassId = newClass.getBaseAwakedClassId();
				if(baseAwakedClassId != null)
				{
					switch(baseAwakedClassId)
					{
						case SIGEL_KNIGHT:
							ItemFunctions.addItem(this, 32264, 1, true);
							break;
						case TYR_WARRIOR:
							ItemFunctions.addItem(this, 32265, 1, true);
							break;
						case OTHELL_ROGUE:
							ItemFunctions.addItem(this, 32266, 1, true);
							break;
						case YR_ARCHER:
							ItemFunctions.addItem(this, 32267, 1, true);
							break;
						case FEOH_WIZARD:
							ItemFunctions.addItem(this, 32268, 1, true);
							break;
						case WYNN_SUMMONER:
							ItemFunctions.addItem(this, 32269, 1, true);
							break;
						case ISS_ENCHANTER:
							ItemFunctions.addItem(this, 32270, 1, true);
							break;
						case EOLH_HEALER:
							ItemFunctions.addItem(this, 32271, 1, true);
							break;
					}
				}
			}
			else if(newClass.isOfRace(Race.ERTHEIA) && newClass.isOfLevel(ClassLevel.THIRD))
			{
				switch(newClass)
				{
					case RANGER_GRAVITY:
						ItemFunctions.addItem(this, 40268, 1, true);
						break;
					case SAIHA_RULER:
						ItemFunctions.addItem(this, 40269, 1, true);
						break;
				}
			}
		}

		if(!newClass.isOfRace(Race.ERTHEIA) && newClass.isOfLevel(ClassLevel.AWAKED) || newClass.isOfRace(Race.ERTHEIA) && newClass.isOfLevel(ClassLevel.THIRD))
		{
			int pomanderItemId = 0;
			if(isBaseClassActive())
				pomanderItemId = ItemTemplate.ITEM_ID_CHAOS_POMANDER;
			else if(isDualClassActive())
				pomanderItemId = ItemTemplate.ITEM_ID_CHAOS_POMANDER_DUAL_CLASS;

			if(pomanderItemId > 0)
			{
				ItemFunctions.deleteItemsEverywhere(this, pomanderItemId);
				ItemFunctions.addItem(this, pomanderItemId, 2, true);
			}
		}
	}

	public long getExp()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getExp();
	}

	public long getMaxExp()
	{
		return getActiveSubClass() == null ? Experience.getExpForLevel(Experience.getMaxLevel() + 1) : getActiveSubClass().getMaxExp();
	}

	public void setEnchantScroll(final ItemInstance scroll)
	{
		_enchantScroll = scroll;
	}

	public ItemInstance getEnchantScroll()
	{
		return _enchantScroll;
	}

	public void setAppearanceStone(final ItemInstance stone)
	{
		_appearanceStone = stone;
	}

	public ItemInstance getAppearanceStone()
	{
		return _appearanceStone;
	}

	public void setAppearanceExtractItem(final ItemInstance item)
	{
		_appearanceExtractItem = item;
	}

	public ItemInstance getAppearanceExtractItem()
	{
		return _appearanceExtractItem;
	}

	public void addExpAndCheckBonus(MonsterInstance mob, final double noRateExp, double noRateSp)
	{
		if(getActiveSubClass() == null)
			return;

		
		double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0, mob, null);
		if(neededExp > 0 && noRateExp > neededExp)
		{
			mob.broadcastPacket(new ExSpawnEmitterPacket(mob, this));
			ThreadPoolManager.getInstance().schedule(new GameObjectTasks.SoulConsumeTask(this), 1000);
		}

		if(noRateExp > 0)
		{
			if(!(getVarBoolean("NoExp") && getExp() == Experience.getExpForLevel(getLevel() + 1) - 1))
			{
				int points;
				if(getLevel() < 85)
					points = Math.max((int)(noRateExp / 1000.0D * Math.max(getLevel() - mob.getLevel(), 1)), 1);
				else
					points = Math.max((int)(noRateExp / (mob.isRaid() ? 1125 : 2250) * Math.max(getLevel() - mob.getLevel(), 1)), 1);

				points *= Config.ALT_VITALITY_CONSUME_RATE;
				if(getAbnormalList().contains(AbnormalType.vp_keep) || getAbnormalList().contains(AbnormalType.vp_up))
					points /= -4;
			    else if(getAbnormalList().contains(AbnormalType.vp_keep))
			    	points = 0;
			    else
			    	points = (int)calcStat(Stats.VITALITY_CONSUME, points);

				setVitality(getVitality() - points);
				
				Clan clan = getClan();
				if(clan != null)
				{
					int huntingPoints = Math.max((int)(noRateExp * (getRateExp() / Config.RATE_XP_BY_LVL[getLevel()]) / Math.pow(getLevel(), 2.0) * Config.CLAN_HUNTING_PROGRESS_RATE), 1);
					clan.addHuntingProgress(huntingPoints);
				}
			}	
		}

		long normalExp = (long) (noRateExp * getRateExp() * (mob.isRaid() ? Config.RATE_XP_RAIDBOSS_MODIFIER : 1.0));
		long normalSp = (long) (noRateSp * getRateSp());

		long expWithoutBonus = (long) (noRateExp * Config.RATE_XP_BY_LVL[getLevel()]);
		long spWithoutBonus = (long) (noRateSp * Config.RATE_SP_BY_LVL[getLevel()]);

		addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true, false, true, true);
	}

	@Override
	public void addExpAndSp(long exp, long sp)
	{
		addExpAndSp(exp, sp, -1, -1, false, false, Config.ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL > -1 && getLevel() >= Config.ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL, true, true);
	}

	public void addExpAndSp(long exp, long sp, boolean delevel)
	{
		addExpAndSp(exp, sp, -1, -1, false, false, delevel, true, true);
	}

	public void addExpAndSp(long addToExp, long addToSp, long bonusAddExp, long bonusAddSp, boolean applyRate, boolean applyToPet, boolean delevel, boolean clearKarma, boolean sendMsg)
	{
		if(getActiveSubClass() == null)
			return;

		if(addToExp < 0 && isFakePlayer())
			return;

		if(applyRate)
		{
			addToExp *= getRateExp();
			addToSp *= getRateSp();
		}

		PetInstance pet = getPet();
		if(addToExp > 0)
		{
			if(applyToPet)
			{
				if(pet != null && !pet.isDead() && !pet.getData().isOfType(PetType.SPECIAL))
				{
					
					if(pet.getData().isOfType(PetType.KARMA))
					{
						pet.addExpAndSp(addToExp, 0);
						addToExp = 0;
					}
					else if(pet.getExpPenalty() > 0f)
					{
						if(pet.getLevel() > getLevel() - 20 && pet.getLevel() < getLevel() + 5)
						{
							pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
							addToExp *= 1. - pet.getExpPenalty();
						}
						else
						{
							pet.addExpAndSp((long) (addToExp * pet.getExpPenalty() / 5.), 0);
							addToExp *= 1. - pet.getExpPenalty() / 5.;
						}
					}
					else if(pet.isSummon())
						addToExp *= 1. - pet.getExpPenalty();
				}
			}

			
			
			if(clearKarma && isPK() && !isCursedWeaponEquipped() && !isInZoneBattle())
			{
				int karmaLost = Formulas.calculateKarmaLost(this, addToExp);
				if(karmaLost > 0)
				{
					_karma += karmaLost;
					if(_karma > 0)
						_karma = 0;

					if(sendMsg)
						sendPacket(new SystemMessagePacket(SystemMsg.YOUR_FAME_HAS_BEEN_CHANGED_TO_S1).addInteger(_karma));
				}
			}

			long max_xp = getVarBoolean("NoExp") || isInDuel() ? Experience.getExpForLevel(getLevel() + 1) - 1 : getMaxExp();
			addToExp = Math.min(addToExp, max_xp - getExp());
		}

		int oldLvl = getActiveSubClass().getLevel();
		boolean oldIsAllowAbilities = isAllowAbilities();
		getActiveSubClass().addExp(addToExp, delevel);
		getActiveSubClass().addSp(addToSp);

		if(addToExp > 0)
			_receivedExp += addToExp;

		if(sendMsg)
		{
			if((addToExp > 0 || addToSp > 0) && bonusAddExp >= 0 && bonusAddSp >= 0)
				sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(bonusAddExp).addInteger(addToSp).addInteger((int) bonusAddSp));
			else if(addToSp > 0 && addToExp == 0)
				sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
			else if(addToSp > 0 && addToExp > 0)
				sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
			else if(addToSp == 0 && addToExp > 0)
				sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));
		}

		int level = getActiveSubClass().getLevel();
		if(level != oldLvl)
		{
			levelSet(level - oldLvl);
			getListeners().onLevelChange(oldLvl, level);

			for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_GLOBAL_LEVEL_UP))
				hook.onPlayerGlobalLevelUp(this, oldLvl, level);
		}

		if(pet != null && pet.getData().isOfType(PetType.SPECIAL))
		{
			pet.setLevel(getLevel());
			pet.setExp(pet.getExpForNextLevel());
			pet.broadcastStatusUpdate();
		}

		updateStats();
	}

	private boolean _dontRewardSkills = false; 

	public void rewardSkills(boolean send)
	{
		if(getClassId().isOfLevel(ClassLevel.AWAKED))
			rewardSkills(send, true, Config.AUTO_LEARN_AWAKED_SKILLS, true);
		else
			rewardSkills(send, true, Config.AUTO_LEARN_SKILLS, true);
	}

	public int rewardSkills(boolean send, boolean checkShortCuts, boolean learnAllSkills, boolean checkRequiredItems)
	{
		if(_dontRewardSkills)
			return 0;

		List<SkillLearn> skillLearns = new ArrayList<SkillLearn>(SkillAcquireHolder.getInstance().getAvailableNextLevelsSkills(this, AcquireType.NORMAL));
		Collections.sort(skillLearns);
		Collections.reverse(skillLearns);

		IntObjectMap<SkillLearn> skillsToLearnMap = new HashIntObjectMap<SkillLearn>();
		for(SkillLearn sl : skillLearns)
		{
			if(!(sl.isAutoGet() && ((learnAllSkills && (!checkRequiredItems || !sl.haveRequiredItemsForLearn(AcquireType.NORMAL))) || sl.isFreeAutoGet(AcquireType.NORMAL))))
			{
				
				skillsToLearnMap.remove(sl.getId());
				continue;
			}

			if(!skillsToLearnMap.containsKey(sl.getId()))
				skillsToLearnMap.put(sl.getId(), sl);
		}

		boolean update = false;
		int addedSkillsCount = 0;

		for(SkillLearn sl : skillsToLearnMap.values())
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
			if(skillEntry == null)
				continue;

			if(addSkill(skillEntry, true) == null)
				addedSkillsCount++;

			if(checkShortCuts && getAllShortCuts().size() > 0 && skillEntry.getLevel() > 1)
				updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());

			update = true;
		}

		if(isTransformed())
		{
			boolean added = false;
			
			for(SkillLearn sl : _transform.getAddtionalSkills())
			{
				if(sl.getMinLevel() > getLevel())
					continue;

				SkillEntry skillEntry = _transformSkills.get(sl.getId());
				if(skillEntry != null && skillEntry.getLevel() >= sl.getLevel())
					continue;

				skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
				if(skillEntry == null)
					continue;

				_transformSkills.remove(skillEntry.getId());
				_transformSkills.put(skillEntry.getId(), skillEntry);

				update = true;
				added = true;
			}

			if(added)
			{
				for(SkillEntry skillEntry : _transformSkills.values())
				{
					if(addSkill(skillEntry, false) == null)
						addedSkillsCount++;
				}
			}
		}

		updateStats();

		if(send && update)
			sendSkillList();

		return addedSkillsCount;
	}

	public Race getRace()
	{
		return ClassId.VALUES[getBaseDefaultClassId()].getRace();
	}

	public ClassType getBaseClassType()
	{
		return ClassId.VALUES[getBaseDefaultClassId()].getType();
	}

	public long getSp()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getSp();
	}

	public void setSp(long sp)
	{
		if(getActiveSubClass() != null)
			getActiveSubClass().setSp(sp);
	}

	public int getClanId()
	{
		return _clan == null ? 0 : _clan.getClanId();
	}

	public long getLeaveClanTime()
	{
		return _leaveClanTime;
	}

	public long getDeleteClanTime()
	{
		return _deleteClanTime;
	}

	public void setLeaveClanTime(final long time)
	{
		_leaveClanTime = time;
	}

	public void setDeleteClanTime(final long time)
	{
		_deleteClanTime = time;
	}

	public void setOnlineTime(final long time)
	{
		_onlineTime = time;
		_onlineBeginTime = System.currentTimeMillis();
	}

	public int getOnlineTime()
	{
		return (int) (_onlineBeginTime > 0 ? (_onlineTime + System.currentTimeMillis() - _onlineBeginTime) / 1000L : _onlineTime / 1000L);
	}

	public long getOnlineBeginTime()
	{
		return _onlineBeginTime;
	}

	public void setNoChannel(final long time)
	{
		_NoChannel = time;
		if(_NoChannel > 2145909600000L || _NoChannel < 0)
			_NoChannel = -1;

		if(_NoChannel > 0)
			_NoChannelBegin = System.currentTimeMillis();
		else
			_NoChannelBegin = 0;
	}

	public long getNoChannel()
	{
		return _NoChannel;
	}

	public long getNoChannelRemained()
	{
		if(_NoChannel == 0)
			return 0;
		else if(_NoChannel < 0)
			return -1;
		else
		{
			long remained = _NoChannel - System.currentTimeMillis() + _NoChannelBegin;
			if(remained < 0)
				return 0;

			return remained;
		}
	}

	public boolean isChatBlocked()
	{
		return getFlags().getChatBlocked().get();
	}

	public boolean isEscapeBlocked()
	{
		return getFlags().getEscapeBlocked().get();
	}

	public boolean isPartyBlocked()
	{
		return getFlags().getPartyBlocked().get();
	}

	public boolean isVioletBoy()
	{
		return getFlags().getVioletBoy().get();
	}

	public void setLeaveClanCurTime()
	{
		_leaveClanTime = System.currentTimeMillis();
	}

	public void setDeleteClanCurTime()
	{
		_deleteClanTime = System.currentTimeMillis();
	}

	public boolean canJoinClan()
	{
		if(_leaveClanTime == 0)
			return true;
		if(System.currentTimeMillis() - _leaveClanTime >= Config.ALT_CLAN_LEAVE_PENALTY_TIME * 60 * 60 * 1000L)
		{
			_leaveClanTime = 0;
			return true;
		}
		return false;
	}

	public boolean canCreateClan()
	{
		if(_deleteClanTime == 0)
			return true;
		if(System.currentTimeMillis() - _deleteClanTime >= Config.ALT_CLAN_CREATE_PENALTY_TIME * 60 * 60 * 1000L)
		{
			_deleteClanTime = 0;
			return true;
		}
		return false;
	}

	public IBroadcastPacket canJoinParty(Player inviter)
	{
		Request request = getRequest();
		if(request != null && request.isInProgress() && request.getOtherPlayer(this) != inviter)
			return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter); 
		if(isBlockAll() || getMessageRefusal()) 
			return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
		if(isInParty()) 
			return new SystemMessagePacket(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
		if(isPartyBlocked())
			return new SystemMessagePacket(SystemMsg.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY).addName(this);
		if(inviter.getReflection() != getReflection()) 
			if(!inviter.getReflection().isMain() && !getReflection().isMain())
				return SystemMsg.INVALID_TARGET.packet(inviter);
		if(isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped()) 
		    return SystemMsg.INVALID_TARGET.packet(inviter);
		if(inviter.isInOlympiadMode() || isInOlympiadMode()) 
			return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
		if(!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty) 
			return SystemMsg.INVALID_TARGET.packet(inviter);
		if(getTeam() != TeamType.NONE && Config.DISABLE_PARTY_ON_EVENT && inviter.isInPvPEvent()) 
		    return SystemMsg.INVALID_TARGET.packet(inviter);
		return null;
	}

	@Override
	public PcInventory getInventory()
	{
		return _inventory;
	}

	@Override
	public long getWearedMask()
	{
		return _inventory.getWearedMask();
	}

	public PcFreight getFreight()
	{
		return _freight;
	}

	public void removeItemFromShortCut(final int objectId)
	{
		_shortCuts.deleteShortCutByObjectId(objectId);
	}

	public void removeSkillFromShortCut(final int skillId)
	{
		_shortCuts.deleteShortCutBySkillId(skillId);
	}

	@Override
	public boolean isSitting()
	{
		return _isSitting;
	}

	public void setSitting(boolean val)
	{
		_isSitting = val;
	}

	public boolean getSittingTask()
	{
		return sittingTaskLaunched;
	}

	public ChairInstance getChairObject()
	{
		return _chairObject;
	}

	@Override
	public void sitDown(ChairInstance chair)
	{
		if(isSitting() || sittingTaskLaunched || isAlikeDead())
			return;

		if(isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || isCastingNow() || isDualCastingNow() || isMoving)
		{
			getAI().setNextAction(AINextAction.REST, null, null, false, false);
			return;
		}

		resetWaitSitTime();
		getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

		if(chair == null)
			broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_SITTING));
		else
		{
			chair.setSeatedPlayer(this);
			broadcastPacket(new ChairSitPacket(this, chair));
		}

		_chairObject = chair;
		setSitting(true);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500);
	}

	@Override
	public void standUp()
	{
		if(!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead())
			return;

		
		getAbnormalList().stop(EffectType.Relax);

		getAI().clearNextAction();
		broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_STANDING));

		if(_chairObject != null)
			_chairObject.setSeatedPlayer(this);

		_chairObject = null;
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500);
	}

	public void updateWaitSitTime()
	{
		if(_waitTimeWhenSit < 200)
			_waitTimeWhenSit += 2;
	}

	public int getWaitSitTime()
	{
		return _waitTimeWhenSit;
	}

	public void resetWaitSitTime()
	{
		_waitTimeWhenSit = 0;
	}

	public Warehouse getWarehouse()
	{
		return _warehouse;
	}

	public ItemContainer getRefund()
	{
		return _refund;
	}

	public long getAdena()
	{
		return getInventory().getAdena();
	}

	public boolean reduceAdena(long adena)
	{
		return reduceAdena(adena, false);
	}

	
	public boolean reduceAdena(long adena, boolean notify)
	{
		if(adena < 0)
			return false;
		if(adena == 0)
			return true;
		boolean result = getInventory().reduceAdena(adena);
		if(notify && result)
			sendPacket(SystemMessagePacket.removeItems(ItemTemplate.ITEM_ID_ADENA, adena));
		return result;
	}

	public ItemInstance addAdena(long adena)
	{
		return addAdena(adena, false);
	}

	
	public ItemInstance addAdena(long adena, boolean notify)
	{
		if(adena < 1)
			return null;
		ItemInstance item = getInventory().addAdena(adena);
		if(item != null && notify)
			sendPacket(SystemMessagePacket.obtainItems(ItemTemplate.ITEM_ID_ADENA, adena, 0));
		return item;
	}

	public GameClient getNetConnection()
	{
		return _connection;
	}

	public int getRevision()
	{
		return _connection == null ? 0 : _connection.getRevision();
	}

	public void setNetConnection(final GameClient connection)
	{
		_connection = connection;
	}

	public boolean isConnected()
	{
		return _connection != null && _connection.isConnected();
	}

	@Override
	public void onAction(final Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(isFrozen())
		{
			player.sendPacket(ActionFailPacket.STATIC);
			return;
		}

		if(shift && OnShiftActionHolder.getInstance().callShiftAction(player, Player.class, this, true))
			return;

		
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() != this)
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(!player.checkInteractionDistance(this) && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
				else
					player.sendPacket(ActionFailPacket.STATIC);
			}
			else
				player.doInteract(this);
		}
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else if(player != this)
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
				else
					player.sendPacket(ActionFailPacket.STATIC);
			}
			else
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else
			player.sendPacket(ActionFailPacket.STATIC);
	}

	@Override
	public void broadcastStatusUpdate()
	{
		
			

		sendPacket(makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP));
		broadcastPacketToOthers(makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP));

		
		if(isInParty())
			
			getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdatePacket(this));

		final List<SingleMatchEvent> events = getEvents(SingleMatchEvent.class);
		for(SingleMatchEvent event : events)
			event.onStatusUpdate(this);

		if(isInOlympiadMode() && isOlympiadCompStart())
		{
			if(_olympiadGame != null)
				_olympiadGame.broadcastInfo(this, null, false);
		}
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public class BroadcastCharInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadcastCharInfoImpl();
			_broadcastCharInfoTask = null;
		}
	}

	@Override
	public void broadcastCharInfo()
	{
		broadcastUserInfo(false);
	}

	
	public void broadcastUserInfo(boolean force)
	{
		sendUserInfo(force);

		if(!isVisible())
			return;

		if(Config.BROADCAST_CHAR_INFO_INTERVAL == 0)
			force = true;

		if(force)
		{
			if(_broadcastCharInfoTask != null)
			{
				_broadcastCharInfoTask.cancel(false);
				_broadcastCharInfoTask = null;
			}
			broadcastCharInfoImpl();
			return;
		}

		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	private int _polyNpcId;

	public void setPolyId(int polyid)
	{
		_polyNpcId = polyid;

		teleToLocation(getLoc());
		broadcastUserInfo(true);
	}

	public boolean isPolymorphed()
	{
		return _polyNpcId != 0;
	}

	public int getPolyId()
	{
		return _polyNpcId;
	}

	@Override
	public void broadcastCharInfoImpl(IUpdateTypeComponent... components)
	{
		if(!isVisible())
			return;

		for(Player target : World.getAroundObservers(this))
		{
			if(isInvisible(target))
				continue;
	
			target.sendPacket(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, target));
			target.sendPacket(new RelationChangedPacket(this, target));
		}
	}

	public void sendEtcStatusUpdate()
	{
		if(!isVisible())
			return;

		sendPacket(new EtcStatusUpdatePacket(this));
	}

	private Future<?> _userInfoTask;

	private class UserInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			sendUserInfoImpl();
			_userInfoTask = null;
		}
	}

	private void sendUserInfoImpl()
	{
		sendPacket(new UIPacket(this));
	}

	public void sendUserInfo()
	{
		sendUserInfo(false);
	}

	public void sendUserInfo(boolean force)
	{
		if(!isVisible() || entering || isLogoutStarted() || isFakePlayer())
			return;

		if(Config.USER_INFO_INTERVAL == 0 || force)
		{
			if(_userInfoTask != null)
			{
				_userInfoTask.cancel(false);
				_userInfoTask = null;
			}
			sendUserInfoImpl();
			return;
		}

		if(_userInfoTask != null)
			return;

		_userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(), Config.USER_INFO_INTERVAL);
	}

	public void sendSkillList(int learnedSkillId)
	{
		sendPacket(new SkillListPacket(this, learnedSkillId));
		sendPacket(new AcquireSkillListPacket(this));
	}

	public void sendSkillList()
	{
		sendSkillList(0);
	}

	public void updateSkillShortcuts(int skillId, int skillLevel)
	{
		for(ShortCut sc : getAllShortCuts())
		{
			if(sc.getId() == skillId && sc.getType() == ShortCut.TYPE_SKILL)
			{
				ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
				sendPacket(new ShortCutRegisterPacket(this, newsc));
				registerShortCut(newsc);
			}
		}
	}

	@Override
	public StatusUpdatePacket makeStatusUpdate(Creature caster, int... fields)
	{
		StatusUpdatePacket su = new StatusUpdatePacket(this, caster);
		for(int field : fields)
			switch(field)
			{
				case StatusUpdatePacket.CUR_HP:
					su.addAttribute(field, (int) getCurrentHp());
					break;
				case StatusUpdatePacket.MAX_HP:
					su.addAttribute(field, getMaxHp());
					break;
				case StatusUpdatePacket.CUR_MP:
					su.addAttribute(field, (int) getCurrentMp());
					break;
				case StatusUpdatePacket.MAX_MP:
					su.addAttribute(field, getMaxMp());
					break;
				case StatusUpdatePacket.CUR_LOAD:
					su.addAttribute(field, getCurrentLoad());
					break;
				case StatusUpdatePacket.MAX_LOAD:
					su.addAttribute(field, getMaxLoad());
					break;
				case StatusUpdatePacket.PVP_FLAG:
					su.addAttribute(field, getPvpFlag());
					break;
				case StatusUpdatePacket.KARMA:
					su.addAttribute(field, getKarma());
					break;
				case StatusUpdatePacket.CUR_CP:
					su.addAttribute(field, (int) getCurrentCp());
					break;
				case StatusUpdatePacket.MAX_CP:
					su.addAttribute(field, getMaxCp());
					break;
			}
		return su;
	}

	public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields)
	{
		if(fields.length == 0 || entering && !broadCast)
			return;

		StatusUpdatePacket su = makeStatusUpdate(null, fields);
		if(!su.hasAttributes())
			return;

		List<L2GameServerPacket> packets = new ArrayList<L2GameServerPacket>(withPet ? 2 : 1);
		if(withPet)
		{
			for(Servitor servitor : getServitors())
				packets.add(servitor.makeStatusUpdate(null, fields));
		}

		packets.add(su);

		if(!broadCast)
			sendPacket(packets);
		else if(entering)
			broadcastPacketToOthers(packets);
		else
			broadcastPacket(packets);
	}

	
	public int getAllyId()
	{
		return _clan == null ? 0 : _clan.getAllyId();
	}

	@Override
	public void sendPacket(IBroadcastPacket p)
	{
		if(p == null)
			return;

		if(isPacketIgnored(p))
			return;

		GameClient connection = getNetConnection();
		if(connection != null && connection.isConnected())
			_connection.sendPacket(p.packet(this));
	}

	@Override
	public void sendPacket(IBroadcastPacket... packets)
	{
		for(IBroadcastPacket p : packets)
			sendPacket(p);
	}

	@Override
	public void sendPacket(List<? extends IBroadcastPacket> packets)
	{
		for(IBroadcastPacket p : packets)
			sendPacket(p);
	}

	private boolean isPacketIgnored(IBroadcastPacket p)
	{
		if(p == null)
			return true;

		
		

		return false;
	}

	public void doInteract(GameObject target)
	{
		if(target == null || isActionsDisabled())
		{
			sendActionFailed();
			return;
		}
		if(target.isPlayer())
		{
			if(checkInteractionDistance(target))
			{
				Player temp = (Player) target;

				if(temp.getPrivateStoreType() == STORE_PRIVATE_SELL || temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
					sendPacket(new PrivateStoreList(this, temp));
				else if(temp.getPrivateStoreType() == STORE_PRIVATE_BUY)
					sendPacket(new PrivateStoreBuyList(this, temp));
				else if(temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
					sendPacket(new RecipeShopSellListPacket(this, temp));
				else if(temp.getPrivateStoreType() == STORE_PRIVATE_BUFF)
					OfflineBufferManager.getInstance().processBypass(this, "bufflist_" + temp.getObjectId());

				sendActionFailed();
			}
			else if(getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
		}
		else
			target.onAction(this, false);
	}

	public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc)
	{
		boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

		if((fromNpc.isRaid() || fromNpc instanceof ReflectionBossInstance) && !Config.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot)
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		
		if(item.isHerb())
		{
			if(!AutoLootHerbs && !forceAutoloot)
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
			for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
			{
				altUseSkill(skillEntry.getTemplate(), this);

				for(Servitor servitor : getServitors())
				{
					if(servitor.isSummon() && !servitor.isDead())
						servitor.altUseSkill(skillEntry.getTemplate(), servitor);
				}
			}
			item.deleteMe();
			return;
		}

		if(!forceAutoloot && !(_autoLoot && (Config.AUTO_LOOT_ITEM_ID_LIST.isEmpty() || Config.AUTO_LOOT_ITEM_ID_LIST.contains(item.getItemId()))) && !(_autoLootOnlyAdena && item.getTemplate().isAdena()))
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		
		if(!isInParty())
		{
			if(!pickupItem(item, Log.Pickup))
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
		}
		else
			getParty().distributeItem(this, item, fromNpc);

		broadcastPickUpMsg(item);
	}

	@Override
	public void doPickupItem(final GameObject object)
	{
		
		if(!object.isItem())
		{
			_log.warn("trying to pickup wrong target." + getTarget());
			return;
		}

		sendActionFailed();
		stopMove();

		ItemInstance item = (ItemInstance) object;

		synchronized (item)
		{
			if(!item.isVisible())
				return;

			
			if(!ItemFunctions.checkIfCanPickup(this, item))
			{
				SystemMessage sm;
				if(item.getItemId() == 57)
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
					sm.addNumber(item.getCount());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
					sm.addItemName(item.getItemId());
				}
				sendPacket(sm);
				return;
			}

			
			if(item.isHerb())
			{
				for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
					altUseSkill(skillEntry.getTemplate(), this);

				broadcastPacket(new GetItemPacket(item, getObjectId()));
				item.deleteMe();
				return;
			}

			FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

			if(!isInParty() || attachment != null)
			{
				if(pickupItem(item, Log.Pickup))
				{
					broadcastPacket(new GetItemPacket(item, getObjectId()));
					broadcastPickUpMsg(item);
					item.pickupMe();
				}
			}
			else
				getParty().distributeItem(this, item, null);
		}
	}

	public boolean pickupItem(ItemInstance item, String log)
	{
		PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

		if(!ItemFunctions.canAddItem(this, item))
			return false;

		Log.LogItem(this, log, item);
		sendPacket(SystemMessagePacket.obtainItems(item));
		getInventory().addItem(item);

		if(attachment != null)
			attachment.pickUp(this);

		getListeners().onPickupItem(item);

		sendChanges();
		return true;
	}

	public void setNpcTarget(GameObject target)
	{
		setTarget(target);
		if(target == null)
			return;

		if(target == getTarget())
		{
			if(target.isNpc())
			{
				NpcInstance npc = (NpcInstance) target;
				sendPacket(npc.makeStatusUpdate(null, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP));
				sendPacket(new ValidateLocationPacket(npc), ActionFailPacket.STATIC);
			}
		}
	}

	@Override
	public void setTarget(GameObject newTarget)
	{
		
		if(newTarget != null && !newTarget.isVisible())
			newTarget = null;

		Party party = getParty();

		
		if(party != null && party.isInDimensionalRift())
		{
			int riftType = party.getDimensionalRift().getType();
			int riftRoom = party.getDimensionalRift().getCurrentRoom();
			if(newTarget != null && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ()))
				newTarget = null;
		}

		GameObject oldTarget = getTarget();

		if(oldTarget != null)
		{
			if(oldTarget.equals(newTarget))
				return;

			broadcastPacket(new TargetUnselectedPacket(this));
		}

		if(newTarget != null)
		{
			broadcastTargetSelected(newTarget);

			if(newTarget.isCreature())
				sendPacket(((Creature) newTarget).getAbnormalStatusUpdate());
		}

		if(newTarget != null && newTarget != this && getDecoys() != null && !getDecoys().isEmpty() && newTarget.isCreature())
		{
			for(DecoyInstance dec : getDecoys())
			{
				if(dec == null)
					continue;
				if(dec.getAI() == null)
				{
					_log.info("This decoy has NULL AI");
					continue;
				}
				if(newTarget.isCreature())
				{
					Creature _nt = (Creature) newTarget;
					if(_nt.isInPeaceZone()) 
						continue;
				}
				dec.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, newTarget, 1000);
				
				
			}
		}

		super.setTarget(newTarget);
	}

	public void broadcastTargetSelected(GameObject newTarget)
	{
		sendPacket(new MyTargetSelectedPacket(this, newTarget));
		broadcastPacket(new TargetSelectedPacket(getObjectId(), newTarget.getObjectId(), getLoc()));
	}

	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
	}

	
	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		final ItemInstance weapon = getActiveWeaponInstance();

		if(weapon == null)
			return null;

		ItemTemplate template = weapon.getTemplate();
		if(template == null)
			return null;

		if(!(template instanceof WeaponTemplate))
		{
			_log.warn("Template in active weapon not WeaponTemplate! (Item ID[" + weapon.getItemId() + "])");
			return null;
		}

		return (WeaponTemplate) template;
	}

	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
	}

	
	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		final ItemInstance weapon = getSecondaryWeaponInstance();

		if(weapon == null)
			return null;

		final ItemTemplate item = weapon.getTemplate();

		if(item instanceof WeaponTemplate)
			return (WeaponTemplate) item;

		return null;
	}

	public ArmorType getWearingArmorType()
	{
		final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(chest == null)
			return ArmorType.NONE;

		final ItemType chestItemType = chest.getItemType();
		if(!(chestItemType instanceof ArmorType))
			return ArmorType.NONE;

		final ArmorType chestArmorType = (ArmorType) chestItemType;
		if(chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
			return chestArmorType;

		final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		if(legs == null)
			return ArmorType.NONE;

		if(legs.getItemType() != chestArmorType)
			return ArmorType.NONE;

		return chestArmorType;
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(attacker == null || isDead() || (attacker.isDead() && !isDot))
			return;

		
		if(attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			
			if(attacker.isPK() && getAbnormalList().contains(5182) && !isInSiegeZone())
				return;
			
			if(isPK() && attacker.getAbnormalList().contains(5182) && !attacker.isInSiegeZone())
				return;
		}

		
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		if(damage <= 0)
			return;

		if(standUp)
		{
			standUp();
			if(isFakeDeath())
				breakFakeDeath();
		}

		final double originDamage = damage;

		if(attacker.isPlayable())
		{
			if(!directHp && getCurrentCp() > 0)
			{
				double cp = getCurrentCp();
				if(cp >= damage)
				{
					cp -= damage;
					damage = 0;
				}
				else
				{
					damage -= cp;
					cp = 0;
				}

				setCurrentCp(cp);
			}
		}

		double hp = getCurrentHp();

		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if(duelEvent != null)
		{
			if(hp - damage <= 1 && !isDeathImmune()) 
			{
				setCurrentHp(1, false);
				duelEvent.onDie(this);
				return;
			}
		}

		if(isInOlympiadMode())
		{
			OlympiadGame game = _olympiadGame;
			if(this != attacker && (skill == null || skill.isOffensive())) 
				game.addDamage(this, Math.min(hp, originDamage));

			if(hp - damage <= 1 && !isDeathImmune()) 
			{
				game.setWinner(getOlympiadSide() == 1 ? 2 : 1);
				game.endGame(20000, false);
				setCurrentHp(1, false);
				attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				attacker.sendActionFailed();
				return;
			}
		}

		if(calcStat(Stats.RestoreHPGiveDamage) == 1 && Rnd.chance(1))
			setCurrentHp(getCurrentHp() + getMaxHp() / 10, false);

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);
	}

	private void altDeathPenalty(final Creature killer)
	{
		
		if(!Config.ALT_GAME_DELEVEL)
			return;
		if(isInZoneBattle())
			return;
		deathPenalty(killer);
	}

	public final boolean atWarWith(final Player player)
	{
		return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId());
	}

	public boolean atMutualWarWith(Player player)
	{
		return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
	}

	public final void doPurePk(final Player killer)
	{
		
		final int pkCountMulti = (int) Math.max(killer.getPkKills() * Config.KARMA_PENALTY_DURATION_INCREASE, 1);

		
		

		
		
		killer.decreaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti); 
		killer.setPkKills(killer.getPkKills() + 1);
	}

	public final void doKillInPeace(final Player killer) 
	{
		if(!isPK())
			doPurePk(killer);
		else
		{
			String var = PK_KILL_VAR + "_" + getObjectId();
			if(!killer.getVarBoolean(var))
			{
				killer.increaseKarma(360); 
				
				long expirationTime = System.currentTimeMillis() + (30 * 60 * 1000);
				killer.setVar(var, true, expirationTime);
			}
		}
	}

	public void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount)
	{
		for(int i = 0; i < maxCount && !items.isEmpty(); i++)
			array.add(items.remove(Rnd.get(items.size())));
	}

	public FlagItemAttachment getActiveWeaponFlagAttachment()
	{
		ItemInstance item = getActiveWeaponInstance();
		if(item == null || !(item.getAttachment() instanceof FlagItemAttachment))
			return null;
		return (FlagItemAttachment) item.getAttachment();
	}

	protected void doPKPVPManage(Creature killer)
	{
		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if(attachment != null)
			attachment.onDeath(this, killer);

		if(killer == null || isMyServitor(killer.getObjectId()) || killer == this)
			return;

		if(killer.isServitor() && (killer = killer.getPlayer()) == null)
			return;

		if(killer.isPlayer())
			PvPRewardManager.tryGiveReward(this, killer.getPlayer());

		if(isInZoneBattle() || killer.isInZoneBattle())
			return;

		if(killer.getTeam() != TeamType.NONE && getTeam() != TeamType.NONE) 
			return;

		
		if(killer.isPlayer() || killer instanceof FakePlayer) 
		{
			final Player pk = killer.getPlayer();
			boolean war = atMutualWarWith(pk);

			
			if(war )
			{
				ClanWar clanWar = _clan.getClanWar(pk.getClan());
				if(clanWar != null)
					clanWar.onKill(pk, this);
			}

			if(isInSiegeZone())
				return;

			Castle castle = getCastle();
			if(getPvpFlag() > 0 || war || castle != null && castle.getResidenceSide() == ResidenceSide.DARK)
				pk.setPvpKills(pk.getPvpKills() + 1);
			else
				doKillInPeace(pk);

			pk.sendChanges();
		}

		int karma = _karma;
		if(isPK())
		{
			increaseKarma(Config.KARMA_LOST_BASE);
			if(_karma > 0)
				_karma = 0;
		}

		
		
		boolean isPvP = killer.isPlayable() || killer instanceof GuardInstance;

		if(isFakePlayer() 
				|| killer.isMonster() && !Config.DROP_ITEMS_ON_DIE 
				|| isPvP 
				&& (_pkKills < Config.MIN_PK_TO_ITEMS_DROP 
				|| karma >= 0 && Config.KARMA_NEEDED_TO_DROP) 
				|| !killer.isMonster() && !isPvP) 
			return;

		
		if(!Config.KARMA_DROP_GM && isGM())
			return;

		final int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;

		double dropRate; 
		if(isPvP)
			dropRate = _pkKills * Config.KARMA_DROPCHANCE_MOD + Config.KARMA_DROPCHANCE_BASE;
		else
			dropRate = Config.NORMAL_DROPCHANCE_BASE;

		int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

		for(int i = 0; i < Math.ceil(dropRate / 100) && i < max_drop_count; i++)
			if(Rnd.chance(dropRate))
			{
				int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
				if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT)
					dropItemCount++;
				else if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON)
					dropEquipCount++;
				else
					dropWeaponCount++;
			}

		List<ItemInstance> drop = new LazyArrayList<ItemInstance>(), 
			dropItem = new LazyArrayList<ItemInstance>(), dropEquip = new LazyArrayList<ItemInstance>(), dropWeapon = new LazyArrayList<ItemInstance>(); 

		getInventory().writeLock();
		try
		{
			for(ItemInstance item : getInventory().getItems())
			{
				if(!item.canBeDropped(this, true) || Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId()))
					continue;

				if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
					dropWeapon.add(item);
				else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
					dropEquip.add(item);
				else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER)
					dropItem.add(item);
			}

			checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
			checkAddItemToDrop(drop, dropEquip, dropEquipCount);
			checkAddItemToDrop(drop, dropItem, dropItemCount);

			
			if(drop.isEmpty())
				return;

			for(ItemInstance item : drop)
			{
				if(item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
				{
					item.setVariationStoneId(0);
					item.setVariation1Id(0);
					item.setVariation2Id(0);
				}

				item = getInventory().removeItem(item);
				Log.LogItem(this, Log.PvPDrop, item);

				if(item.getEnchantLevel() > 0)
					sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
				else
					sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));

				if(killer.isPlayable() && ((Config.AUTO_LOOT && Config.AUTO_LOOT_PK) || isInFlyingTransform()))
				{
					killer.getPlayer().getInventory().addItem(item);
					Log.LogItem(this, Log.Pickup, item);

					killer.getPlayer().sendPacket(SystemMessagePacket.obtainItems(item));
				}
				else
					item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
			}
		}
		finally
		{
			getInventory().writeUnlock();
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		
		getDeathPenalty().checkCharmOfLuck();
		
		if(isInStoreMode())
		{
			setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			storePrivateStore();
		}
		if(isProcessingRequest())
		{
			Request request = getRequest();
			if(isInTrade())
			{
				Player parthner = request.getOtherPlayer(this);
				sendPacket(TradeDonePacket.FAIL);
				parthner.sendPacket(TradeDonePacket.FAIL);
			}
			request.cancel();
		}

		setAgathion(0);

		boolean checkPvp = true;
		if(Config.ALLOW_CURSED_WEAPONS)
		{
			if(isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().dropPlayer(this);
				checkPvp = false;
			}
			else if(killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().increaseKills(((Player) killer).getCursedWeaponEquippedId());
				checkPvp = false;
			}
		}

		final Player killerPlayer = killer.getPlayer();
		if(killerPlayer != null)
		{
			for(SingleMatchEvent event : getEvents(SingleMatchEvent.class))
			{
				if(!event.canIncreasePvPPKCounter(killerPlayer, this))
				{
					checkPvp = false;
					break;
				}
			}
		}

		if(checkPvp)
		{
			doPKPVPManage(killer);
			altDeathPenalty(killer);
		}
		
		
		getDeathPenalty().notifyDead(killer);
		
		setIncreasedForce(0);

		if(isInParty() && getParty().isInReflection() && getParty().getReflection() instanceof DimensionalRift)
			((DimensionalRift) getParty().getReflection()).memberDead(this);

		stopWaterTask();

		if(!isSalvation() && isInSiegeZone() && isCharmOfCourage())
		{
			ask(new ConfirmDlgPacket(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false));
			setCharmOfCourage(false);
		}

		for(QuestState qs : getAllQuestsStates())
			qs.getQuest().notifyTutorialEvent("CE", false, "200", qs);

		if(isMounted())
			_mount.onDeath();

		for(Servitor servitor : getServitors())
			servitor.notifyMasterDeath();

		for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_DIE))
			hook.onPlayerDie(this, killer);

		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_DIE))
			hook.onPlayerDie(this, killer);
			
		super.onDeath(killer);
	}

	public void restoreExp()
	{
		restoreExp(100.);
	}

	public void restoreExp(double percent)
	{
		if(percent == 0)
			return;

		long lostexp = 0;

		String lostexps = getVar("lostexp");
		if(lostexps != null)
		{
			lostexp = Long.parseLong(lostexps);
			unsetVar("lostexp");
		}

		if(lostexp != 0)
			addExpAndSp((long) (lostexp * percent / 100), 0);
	}

	public void deathPenalty(Creature killer)
	{
		if(killer == null)
			return;

		final boolean atwar = killer.getPlayer() != null && atWarWith(killer.getPlayer());
		double deathPenaltyBonus = getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_EXPERIENCE_PENALTY;
	    if(deathPenaltyBonus < 2.0D)
	    	deathPenaltyBonus = 1.0D;
	    else
	    	deathPenaltyBonus /= 2.0D;
	      
		final int level = getLevel();

		
		double percentLost = Config.PERCENT_LOST_ON_DEATH[getLevel()];
		if(isPK())
			percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_FOR_PK;
		else if(isInPeaceZone())
			percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_PEACE_ZONE;
		else
		{
			if(atwar) 
				percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_WAR;
			else if(killer.getPlayer() != null && killer.getPlayer() != this)
				percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_PVP;
		}

		if(percentLost <= 0)
			return;

		
		long lostexp = (long) ((Experience.getExpForLevel(level + 1) - Experience.getExpForLevel(level)) * percentLost / 100);
		lostexp *= deathPenaltyBonus;
		lostexp = (long) calcStat(Stats.EXP_LOST, lostexp, killer, null);

		
		if(isInSiegeZone())
		{
			SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
			if(siegeEvent != null)
				lostexp = 0;

			if(siegeEvent != null)
			{
				int syndromeLvl = 0;
				for(Abnormal e : getAbnormalList())
				{
					if(e.getSkill().getId() == Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME)
					{
						syndromeLvl = e.getSkill().getLevel();
						break;
					}
				}
	
				if(syndromeLvl == 0)
				{
					Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
					if(skill != null)
						skill.getEffects(this, this);
				}
				else if(syndromeLvl < 5)
				{
					getAbnormalList().stop(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
					Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
					skill.getEffects(this, this);
				}
				else if(syndromeLvl == 5)
				{
					getAbnormalList().stop(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
					Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
					skill.getEffects(this, this);
				}
			}
		}

		long before = getExp();
		addExpAndSp(-lostexp, 0);
		long lost = before - getExp();

		if(lost > 0)
			setVar("lostexp", lost);
	}

	public void setRequest(Request transaction)
	{
		_request = transaction;
	}

	public Request getRequest()
	{
		return _request;
	}

	
	public boolean isBusy()
	{
		if(!Config.DISABLE_PARTY_ON_EVENT && isInPvPEvent())
			return false;
		return isProcessingRequest() || isOutOfControl() || isInOlympiadMode() || getTeam() != TeamType.NONE || isInStoreMode() || isInDuel() || getMessageRefusal() || isBlockAll() || isInvisible(null);
	}

	public boolean isProcessingRequest()
	{
		if(_request == null)
			return false;
		if(!_request.isInProgress())
			return false;
		return true;
	}

	public boolean isInTrade()
	{
		return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
	}

	public List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper)
	{
		if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || !object.isVisible() || object.isObservePoint())
			return Collections.emptyList();

		return object.addPacketList(this, dropper);
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if(isInvisible(forPlayer) && forPlayer.getObjectId() != getObjectId())
			return Collections.emptyList();

		if(isInStoreMode() && forPlayer.getVarBoolean(NO_TRADERS_VAR))
			return Collections.emptyList();

		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		if(forPlayer.getObjectId() != getObjectId())
			list.add(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, forPlayer));

		if(isSitting() && _chairObject != null)
			list.add(new ChairSitPacket(this, _chairObject));

		if(isInStoreMode())
			list.add(getPrivateStoreMsgPacket(forPlayer));
		
		boolean dualCast = isDualCastingNow();
		if(isCastingNow())
		{
			Creature castingTarget = getCastingTarget();
			Skill castingSkill = getCastingSkill();
			long animationEndTime = getAnimationEndTime();
			if(castingSkill != null && !castingSkill.isNotBroadcastable() && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(dualCast)
		{
			Creature castingTarget = getDualCastingTarget();
			Skill castingSkill = getDualCastingSkill();
			long animationEndTime = getDualAnimationEndTime();
			if(castingSkill != null && !castingSkill.isNotBroadcastable() && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		list.add(new RelationChangedPacket(this, forPlayer));

		if(isInBoat())
			list.add(getBoat().getOnPacket(this, getInBoatPosition()));
		else
		{
			if(isMoving || isFollow)
				list.add(movePacket());
		}

		
		
		if((isInStoreMode() && entering))
		{
			list.add(new CIPacket(this, forPlayer));
			
		}

		return list;
	}

	public List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list)
	{
		if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || object.isObservePoint()) 
			return Collections.emptyList();

		List<L2GameServerPacket> result = list == null ? object.deletePacketList(this) : list;

		if(getParty() != null && object instanceof Creature)
			getParty().removeTacticalSign((Creature) object);

		if(!isInObserverMode())
			getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
		return result;
	}

	private void levelSet(int levels)
	{
		if(levels > 0)
		{
			final int level = getLevel();

			checkLevelUpReward(false);

			sendPacket(SystemMsg.YOUR_LEVEL_HAS_INCREASED);
			broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

			setCurrentHpMp(getMaxHp(), getMaxMp());
			setCurrentCp(getMaxCp());

			for(QuestState qs : getAllQuestsStates())
				qs.getQuest().notifyTutorialEvent("CE", false, "300", qs);

			
			rewardSkills(false);
			notifyNewSkills();
			
			int mentorId = getMenteeList().getMentor();
			if(mentorId != 0 && isBaseClassActive())
			{
				if (level > 84)
				{
					String mentorName = getMenteeList().get(mentorId).getName();
					sendPacket((new SystemMessagePacket(SystemMsg.YOU_REACHED_LEVEL_86_RELATIONSHIP_WITH_S1_CAME_TO_AN_END)).addString(mentorName));
					Mentoring.removeEffFromGraduatedMentee(this);
					ItemFunctions.addItem(this, 33800, 1, true);
					getMenteeList().remove(mentorName, false, true);
					Player mentorPlayer = World.getPlayer(mentorId);
					if (mentorPlayer != null)
					{
						mentorPlayer.sendPacket((new SystemMessagePacket(SystemMsg.THE_MENTEE_S1_HAS_REACHED_LEVEL_86)).addName(this));
						Mentoring.removeMentoring(mentorPlayer, this, false);
						if(Mentoring.getGraduatedMenteesCount(mentorId) == -1)
							Mentoring.setNewMenteesCount(mentorId, 1);
						else if(Mentoring.getGraduatedMenteesCount(mentorId) == 2)
						{
							Mentoring.unsetMenteesCount(mentorId);
							Mentoring.setTimePenalty(mentorId, System.currentTimeMillis() + 432000000L, -1);
						}
						else
							Mentoring.setNewMenteesCount(mentorId, Mentoring.getGraduatedMenteesCount(mentorId));
					} 
		        }
			}
		}
		else if(levels < 0)
			checkSkills();
		
		checkAbilitiesSkills();
		sendUserInfo(true);
		sendSkillList();

		
		if(isInParty())
			getParty().recalculatePartyData();

		if(_clan != null)
			_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));

		if(_matchingRoom != null)
			_matchingRoom.broadcastPlayerUpdate(this);
	}

	public boolean notifyNewSkills()
	{
		final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
		for(SkillLearn s : skills)
		{
			if(s.isFreeAutoGet(AcquireType.NORMAL))
				continue;

			Skill sk = SkillHolder.getInstance().getSkill(s.getId(), s.getLevel());
			if(sk == null)
				continue;

			sendPacket(ExNewSkillToLearnByLevelUp.STATIC);
			return true;
		}
		return false;
	}

	
	public boolean checkSkills()
	{
		boolean update = false;
		for(SkillEntry sk : getAllSkillsArray())
		{
			if(SkillUtils.checkSkill(this, sk))
				update = true;
		}
		return update;
	}

	public void startTimers()
	{
		startAutoSaveTask();
		startPcBangPointsTask();
		startPremiumAccountTask();
		getInventory().startTimers();
		resumeQuestTimers();
		getAttendanceRewards().startTasks();
	}

	public void stopAllTimers()
	{
		setAgathion(0);
		stopWaterTask();
		stopPremiumAccountTask();
		stopHourlyTask();
		stopKickTask();
		stopPcBangPointsTask();
		stopTrainingCampTask();
		stopAutoSaveTask();
		getInventory().stopAllTimers();
		stopQuestTimers();
		stopEnableUserRelationTask();
		getHennaList().stopHennaRemoveTask();
		getAttendanceRewards().stopTasks();
	}

    @Override
	public boolean isMyServitor(int objId)
	{
		if(_pet != null && _pet.getObjectId() == objId)
			return true;

		return _summons.containsKey(objId);
	}

	public int getServitorsCount()
	{
		int count = _summons.size();
		if(_pet != null)
			count++;
		return count;
	}

	public boolean hasServitor()
	{
		return getServitorsCount() > 0;
	}

	@Override
	public List<Servitor> getServitors()
	{
		List<Servitor> servitors = new ArrayList<Servitor>(_summons.values());
		if(_pet != null)
			servitors.add(_pet);

		Collections.sort(servitors, Servitor.ServitorComparator.getInstance());
		return servitors;
	}

	public Servitor getAnyServitor()
	{
		return getServitors().stream().findAny().orElse(null);
	}

	public Servitor getFirstServitor()
	{
		return getServitors().stream().findFirst().orElse(null);
	}

	public Servitor getServitor(int objId)
	{
		if(_pet != null && _pet.getObjectId() == objId)
			return _pet;

		return getSummon(objId);
	}

    public int getSummonsCount()
    {
        return _summons.size();
    }
    
	public boolean hasSummon()
	{
		return getSummonsCount() > 0;
	}

	public List<SummonInstance> getSummons()
	{
		List<SummonInstance> summons = new ArrayList<SummonInstance>(_summons.values());
        Collections.sort(summons, Servitor.ServitorComparator.getInstance());
        return summons;
	}
	 
    public SummonInstance getAnySummon()
    {
        return getSummons().stream().findAny().orElse(null);
    }
    
    public SummonInstance getFirstSummon()
    {
        return getSummons().stream().findFirst().orElse(null);
    }
    
    public SummonInstance getSummon(int objId)
    {
        return _summons.get(objId);
    }
    
    public void addSummon(SummonInstance summon)
    {
        _summons.put(summon.getObjectId(), summon);
        






		autoShot();


    }

	public void deleteServitor(int objId)
	{
		if(_summons.containsKey(objId))
			deleteSummon(objId);
		else if(_pet != null && _pet.getObjectId() == objId)
			setPet(null);
	}

	public void deleteSummon(int objId)
	{
		_summons.remove(objId);
		
		if (_summons.isEmpty() && _pet == null)
		{
            removeAutoShot(SoulShotType.BEAST_SOULSHOT);
            removeAutoShot(SoulShotType.BEAST_SPIRITSHOT);
        }
		autoShot(); 
		getAbnormalList().stop(4140); 
	}

	public PetInstance getPet()
	{
		return _pet;
	}

	public void setPet(PetInstance pet)
	{
		boolean petDeleted = _pet != null;
		_pet = pet;
		unsetVar("pet");

		if(pet == null)
		{
			if(petDeleted)
			{
				if(isLogoutStarted())
				{
					if(getPetControlItem() != null)
						setVar("pet", getPetControlItem().getObjectId());
				}
				setPetControlItem(null);
				if(_summons.isEmpty() && _pet == null)
				{
					removeAutoShot(SoulShotType.BEAST_SOULSHOT);
					removeAutoShot(SoulShotType.BEAST_SPIRITSHOT);
				}
			}
			getAbnormalList().stop(4140);
		}
		autoShot();
	}

	public void scheduleDelete()
	{
		long time = 0L;

		if(Config.SERVICES_ENABLE_NO_CARRIER)
			time = NumberUtils.toInt(getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);

		scheduleDelete(time * 1000L);
	}

	
	public void scheduleDelete(long time)
	{
		if(isLogoutStarted() || isInOfflineMode())
			return;

		broadcastCharInfo();

		ThreadPoolManager.getInstance().schedule(() ->
		{
			if(!isConnected())
			{
				prepareToLogout1();
				prepareToLogout2();
				deleteMe();
			}
		}, time);
	}

	@Override
	protected void onDelete()
	{
		deleteCubics();
		super.onDelete();

		
		if(_observePoint != null)
			_observePoint.deleteMe();

		
		_friendList.notifyFriends(false);

		getBookMarkList().clear();

		_inventory.clear();
		_warehouse.clear();
		_summons.clear();
		_pet = null;
		_arrowItem = null;
		_fistsWeaponItem = null;
		_chars = null;
		_enchantScroll = null;
		_lastNpc = HardReferences.emptyRef();
		_observePoint = null;
	}

	public void setTradeList(List<TradeItem> list)
	{
		_tradeList = list;
	}

	public List<TradeItem> getTradeList()
	{
		return _tradeList;
	}

	public String getSellStoreName()
	{
		return _sellStoreName;
	}

	public void setSellStoreName(String name)
	{
		_sellStoreName = Strings.stripToSingleLine(name);
	}

	public String getPackageSellStoreName()
	{
		return _packageSellStoreName;
	}

	public void setPackageSellStoreName(String name)
	{
		_packageSellStoreName = Strings.stripToSingleLine(name);
	}

	public void setSellList(boolean packageSell, List<TradeItem> list)
	{
		if(packageSell)
			_packageSellList = list;
		else
			_sellList = list;
	}

	public List<TradeItem> getSellList()
	{
		return getSellList(_privatestore == STORE_PRIVATE_SELL_PACKAGE);
	}

	public List<TradeItem> getSellList(boolean packageSell)
	{
		return packageSell ? _packageSellList : _sellList;
	}

	public String getBuyStoreName()
	{
		return _buyStoreName;
	}

	public void setBuyStoreName(String name)
	{
		_buyStoreName = Strings.stripToSingleLine(name);
	}

	public void setBuyList(List<TradeItem> list)
	{
		_buyList = list;
	}

	public List<TradeItem> getBuyList()
	{
		return _buyList;
	}

	public void setManufactureName(String name)
	{
		_manufactureName = Strings.stripToSingleLine(name);
	}

	public String getManufactureName()
	{
		return _manufactureName;
	}

	public List<ManufactureItem> getCreateList()
	{
		return _createList;
	}

	public void setCreateList(List<ManufactureItem> list)
	{
		_createList = list;
	}

	public void setPrivateStoreType(final int type)
	{
		_privatestore = type;
	}

	public boolean isInStoreMode()
	{
		return _privatestore != STORE_PRIVATE_NONE;
	}

	public boolean isInBuffStore()
	{
		return _privatestore == STORE_PRIVATE_BUFF;
	}

	public int getPrivateStoreType()
	{
		return _privatestore;
	}

	public L2GameServerPacket getPrivateStoreMsgPacket(Player forPlayer)
	{
		switch(getPrivateStoreType())
		{
			case STORE_PRIVATE_BUY:
				return new PrivateStoreBuyMsg(this, canTalkWith(forPlayer));
			case STORE_PRIVATE_SELL:
				return new PrivateStoreMsg(this, canTalkWith(forPlayer));
			case STORE_PRIVATE_SELL_PACKAGE:
				return new ExPrivateStoreWholeMsg(this, canTalkWith(forPlayer));
			case STORE_PRIVATE_MANUFACTURE:
				return new RecipeShopMsgPacket(this, canTalkWith(forPlayer));
		}

		return null;
	}

	public void broadcastPrivateStoreInfo()
	{
		if(!isVisible() || _privatestore == STORE_PRIVATE_NONE)
			return;

		sendPacket(getPrivateStoreMsgPacket(this));
		for(Player target : World.getAroundObservers(this))
			target.sendPacket(getPrivateStoreMsgPacket(target));
	}

	
	public void setClan(Clan clan)
	{
		if(_clan != clan && _clan != null)
			unsetVar("canWhWithdraw");

		Clan oldClan = _clan;
		if(oldClan != null && clan == null)
			for(SkillEntry skillEntry : oldClan.getAllSkills())
				removeSkill(skillEntry, false);

		_clan = clan;

		if(clan == null)
		{
			_pledgeType = Clan.SUBUNIT_NONE;
			_pledgeRank = PledgeRank.VAGABOND;
			_powerGrade = 0;
			_apprentice = 0;
			_lvlJoinedAcademy = 0;
			getInventory().validateItems();
			return;
		}

		if(!clan.isAnyMember(getObjectId()))
		{
			setClan(null);
			if(!isNoble())
				setTitle("");
		}
	}

	@Override
	public Clan getClan()
	{
		return _clan;
	}

	public SubUnit getSubUnit()
	{
		return _clan == null ? null : _clan.getSubUnit(_pledgeType);
	}

	public ClanHall getClanHall()
	{
		int id = _clan != null ? _clan.getHasHideout() : 0;
		return ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
	}

	public Castle getCastle()
	{
		int id = _clan != null ? _clan.getCastle() : 0;
		return ResidenceHolder.getInstance().getResidence(Castle.class, id);
	}

	public Fortress getFortress()
	{
		int id = _clan != null ? _clan.getHasFortress() : 0;
		return ResidenceHolder.getInstance().getResidence(Fortress.class, id);
	}

	public Alliance getAlliance()
	{
		return _clan == null ? null : _clan.getAlliance();
	}

	public boolean isClanLeader()
	{
		return _clan != null && getObjectId() == _clan.getLeaderId();
	}

	public boolean isAllyLeader()
	{
		return getAlliance() != null && getAlliance().getLeader().getLeaderId() == getObjectId();
	}

	@Override
	public void reduceArrowCount()
	{
		if(_arrowItem != null && _arrowItem.getTemplate().isQuiver())
			return;

		sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
		if(!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L))
		{
			getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
			_arrowItem = null;
		}
	}

	
	public boolean checkAndEquipArrows()
	{
		
		if(getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
		{
			ItemInstance activeWeapon = getActiveWeaponInstance();
			if(activeWeapon != null)
			{
				if(activeWeapon.getItemType() == WeaponType.BOW)
					_arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
				else if(activeWeapon.getItemType() == WeaponType.CROSSBOW || activeWeapon.getItemType() == WeaponType.TWOHANDCROSSBOW)
					_arrowItem = getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
			}

			
			if(_arrowItem != null)
				getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
		}
		else
			
			_arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

		return _arrowItem != null;
	}

	public void setUptime(final long time)
	{
		_uptime = time;
	}

	public long getUptime()
	{
		return System.currentTimeMillis() - _uptime;
	}

	public boolean isInParty()
	{
		return _party != null;
	}

	public void setParty(final Party party)
	{
		_party = party;
	}

	public void joinParty(final Party party)
	{
		if(party != null)
			party.addPartyMember(this);
	}

	public void leaveParty()
	{
		if(isInParty())
			_party.removePartyMember(this, false);
	}

	public Party getParty()
	{
		return _party;
	}

	public void setStartingTimeInFullParty(long time)
	{
		_startingTimeInFullParty = time;
	}

	public long getStartingTimeInFullParty()
	{
		return _startingTimeInFullParty;
	}

	public void setStartingTimeInParty(long time)
	{
		_startingTimeInParty = time;
	}

	public long getStartingTimeInParty()
	{
		return _startingTimeInParty;
	}

	public void setLastPartyPosition(Location loc)
	{
		_lastPartyPosition = loc;
	}

	public Location getLastPartyPosition()
	{
		return _lastPartyPosition;
	}

	public boolean isGM()
	{
		return _playerAccess == null ? false : _playerAccess.IsGM;
	}

	
	public void setAccessLevel(final int level)
	{
		_accessLevel = level;
	}

	
	@Override
	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setPlayerAccess(final PlayerAccess pa)
	{
		if(pa != null)
			_playerAccess = pa;
		else
			_playerAccess = new PlayerAccess();

		setAccessLevel(isGM() || _playerAccess.Menu ? 100 : 0);
	}

	public PlayerAccess getPlayerAccess()
	{
		return _playerAccess;
	}

	
	@Override
	public void updateStats()
	{
		if(entering || isLogoutStarted())
			return;

		refreshOverloaded();
		refreshExpertisePenalty();
		super.updateStats();
		for(Servitor servitor : getServitors())
			servitor.updateStats();
	}

	@Override
	public void sendChanges()
	{
		if(entering || isLogoutStarted())
			return;
		super.sendChanges();
	}

	
	public void updateKarma(boolean flagChanged)
	{
		sendStatusUpdate(true, true, StatusUpdatePacket.KARMA);
		if(flagChanged)
			broadcastRelation();
	}

	public boolean isOnline()
	{
		return _isOnline;
	}

	public void setIsOnline(boolean isOnline)
	{
		_isOnline = isOnline;
	}

	public void setOnlineStatus(boolean isOnline)
	{
		_isOnline = isOnline;
		updateOnlineStatus();
	}

	private void updateOnlineStatus()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
			statement.setInt(1, isOnline() && !isInOfflineMode() ? 1 : 0);
			statement.setLong(2, System.currentTimeMillis() / 1000L);
			statement.setInt(3, getObjectId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void decreaseKarma(final long val)
	{
		boolean flagChanged = _karma >= 0;
		long new_karma = _karma - val;

		if(new_karma < Integer.MIN_VALUE)
			new_karma = Integer.MIN_VALUE;

		if(_karma >= 0 && new_karma < 0)
		{
			if(_pvpFlag > 0)
			{
				_pvpFlag = 0;
				if(_PvPRegTask != null)
				{
					_PvPRegTask.cancel(true);
					_PvPRegTask = null;
				}
				sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);
			}
			_karma = (int)new_karma;
		}
		else
			_karma = (int)new_karma;

		updateKarma(flagChanged);
	}

	public void increaseKarma(final int val)
	{
		boolean flagChanged = _karma < 0;
		long new_karma = _karma + val;
		if(new_karma > Integer.MAX_VALUE)
			new_karma = Integer.MAX_VALUE;

		_karma = (int)new_karma;
		if(_karma > 0)
			updateKarma(flagChanged);
		else
			updateKarma(false);
	}

	public static Player create(int classId, int sex, String accountName, final String name, final int hairStyle, final int hairColor, final int face)
	{
		if(classId < 0 || classId >= ClassId.VALUES.length)
			return null;

		ClassId classID = ClassId.VALUES[classId];
		if(classID.isDummy() || !classID.isOfLevel(ClassLevel.NONE))
			return null;

		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(classID.getRace(), classID, Sex.VALUES[sex]);

		
		Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);

		player.setName(name);
		player.setTitle("");
		player.setHairStyle(hairStyle);
		player.setHairColor(hairColor);
		player.setFace(face);
		player.setCreateTime(System.currentTimeMillis());

		if(Config.PC_BANG_POINTS_BY_ACCOUNT)
			player.setPcBangPoints(Integer.parseInt(AccountVariablesDAO.getInstance().select(player.getAccountName(), PC_BANG_POINTS_VAR, "0")));

		
		if(!CharacterDAO.getInstance().insert(player))
			return null;

		int level = Config.STARTING_LVL;
		double hp = classID.getBaseHp(level);
		double mp = classID.getBaseMp(level);
		double cp = classID.getBaseCp(level);
		long exp = Experience.getExpForLevel(level);
		long sp = Config.STARTING_SP;
		boolean active = true;
		SubClassType type = SubClassType.BASE_CLASS;

		
		if(!CharacterSubclassDAO.getInstance().insert(player.getObjectId(), classId, classId, exp, sp, hp, mp, cp, hp, mp, cp, level, active, type, 0, 0, MAX_VITALITY_POINTS, 0))
			return null;

		return player;
	}

	public static Player restore(final int objectId, boolean fake)
	{
		Player player = null;
		Connection con = null;
		Statement statement = null;
		Statement statement2 = null;
		PreparedStatement statement3 = null;
		ResultSet rset = null;
		ResultSet rset2 = null;
		ResultSet rset3 = null;
		try
		{
			
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement2 = con.createStatement();
			rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
			rset2 = statement2.executeQuery("SELECT `class_id`, `default_class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `type`=" + SubClassType.BASE_CLASS.ordinal() + " LIMIT 1");
		      
			if(rset.next() && rset2.next())
			{
				final ClassId classId = ClassId.VALUES[rset2.getInt("class_id")];
				final ClassId defaultClassId = ClassId.VALUES[rset2.getInt("default_class_id")];
				final PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(defaultClassId.getRace(), classId, Sex.VALUES[rset.getInt("sex")]);;

				if(fake)
				{
					FakePlayerAITemplate fakeAiTemplate = FakePlayersHolder.getInstance().getAITemplate(classId.getRace(), classId.getType());
					if(fakeAiTemplate == null)
						return null;

					player = new Player(fakeAiTemplate, objectId, template);
				}
				else
					player = new Player(objectId, template);

				player.getSubClassList().restore();

				player.restoreVariables();
				player.loadInstanceReuses();
				player.getBookMarkList().setCapacity(rset.getInt("bookmarks"));
				player.getBookMarkList().restore();
				player.setBotRating(rset.getInt("bot_rating"));
				player.getFriendList().restore();
				player.getBlockList().restore();
				player.getPremiumItemList().restore();
				player.getProductHistoryList().restore();
				player.getFactionList().restore();
				player.setPostFriends(CharacterPostFriendDAO.getInstance().select(player));
				CharacterGroupReuseDAO.getInstance().select(player);

				player.setLogin(rset.getString("account_name"));
				player.setName(rset.getString("char_name"));

				player.setFace(rset.getInt("face"));
				player.setBeautyFace(rset.getInt("beautyFace"));
				player.setHairStyle(rset.getInt("hairStyle"));
				player.setBeautyHairStyle(rset.getInt("beautyHairStyle"));
				player.setHairColor(rset.getInt("hairColor"));
				player.setBeautyHairColor(rset.getInt("beautyHairColor"));
				player.setHeading(0);

				player.setKarma(rset.getInt("karma"));
				player.setPvpKills(rset.getInt("pvpkills"));
				player.setPkKills(rset.getInt("pkkills"));
				player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
				if(player.getLeaveClanTime() > 0 && player.canJoinClan())
					player.setLeaveClanTime(0);
				player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
				if(player.getDeleteClanTime() > 0 && player.canCreateClan())
					player.setDeleteClanTime(0);

				player.setNoChannel(rset.getLong("nochannel") * 1000L);
				if(player.getNoChannel() > 0 && player.getNoChannelRemained() < 0)
					player.setNoChannel(0);

				player.setOnlineTime(rset.getLong("onlinetime") * 1000L);

				final int clanId = rset.getInt("clanid");
				if(clanId > 0)
				{
					player.setClan(ClanTable.getInstance().getClan(clanId));
					player.setPledgeType(rset.getInt("pledge_type"));
					player.setPowerGrade(rset.getInt("pledge_rank"));
					player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
					player.setApprentice(rset.getInt("apprentice"));
				}

				player.setCreateTime(rset.getLong("createtime") * 1000L);
				player.setDeleteTimer(rset.getInt("deletetime"));

				player.setTitle(rset.getString("title"));

				if(player.getVar("titlecolor") != null)
					player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")));

				if(player.getVar("namecolor") == null)
					if(player.isGM())
						player.setNameColor(Config.GM_NAME_COLOUR);
					else if(player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId())
						player.setNameColor(Config.CLANLEADER_NAME_COLOUR);
					else
						player.setNameColor(Config.NORMAL_NAME_COLOUR);
				else
					player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));

				if(Config.AUTO_LOOT_INDIVIDUAL)
				{
					player._autoLoot = player.getVarBoolean("AutoLoot", Config.AUTO_LOOT);
					player._autoLootOnlyAdena = player.getVarBoolean("AutoLootOnlyAdena", Config.AUTO_LOOT);
					player.AutoLootHerbs = player.getVarBoolean("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
				}

				player.setUptime(System.currentTimeMillis());
				player.setLastAccess(rset.getLong("lastAccess"));

				player.setRecomHave(rset.getInt("rec_have"));
				player.setRecomLeft(rset.getInt("rec_left"));

				if(player.getVar("recLeftToday") != null)
					player.setRecomLeftToday(Integer.parseInt(player.getVar("recLeftToday")));
				else
					player.setRecomLeftToday(0);

				if(!Config.USE_CLIENT_LANG)
					player.setLanguage(player.getVar(Language.LANG_VAR));

				player.setKeyBindings(rset.getBytes("key_bindings"));
				if(Config.PC_BANG_POINTS_BY_ACCOUNT)
					player.setPcBangPoints(Integer.parseInt(AccountVariablesDAO.getInstance().select(player.getAccountName(), PC_BANG_POINTS_VAR, "0")));
				else
					player.setPcBangPoints(rset.getInt("pcBangPoints"));

				player.setFame(rset.getInt("fame"), null, false);
				
				player.setRaidPoints(rset.getInt("raid_points"));
				
				player.setUsedWorldChatPoints(rset.getInt("used_world_chat_points"));

				player.setHideHeadAccessories(rset.getInt("hide_head_accessories") > 0);

				player.restoreRecipeBook();

				player.setNobleType(NobleType.VALUES[rset.getInt("is_noble")], true);
				
				if(Config.ENABLE_OLYMPIAD)
					player.setHero(Hero.getInstance().isHero(player.getObjectId()));

				if(!player.isHero())
					player.setHero(CustomHeroDAO.getInstance().isCustomHero(player.getObjectId()));

				player.updatePledgeRank();

				player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

				int reflection = 0;

				long jailExpireTime = player.getVarExpireTime(JAILED_VAR);
				if(jailExpireTime > System.currentTimeMillis())
				{
					reflection = ReflectionManager.JAIL.getId();
					if(!player.isInZone("[gm_prison]"))
						player.setLoc(Location.findPointToStay(player, AdminFunctions.JAIL_SPAWN, 50, 200));
					player.setIsInJail(true);
					player.startUnjailTask(player, (int) (jailExpireTime - System.currentTimeMillis() / 60000));
				}
				else
				{
					
					String jumpSafeLoc = player.getVar("@safe_jump_loc");
					if(jumpSafeLoc != null)
					{
						player.setLoc(Location.parseLoc(jumpSafeLoc));
						player.unsetVar("@safe_jump_loc");
					}

					String ref = player.getVar("reflection");
					if(ref != null)
					{
						reflection = Integer.parseInt(ref);
						if(reflection != ReflectionManager.PARNASSUS.getId() && reflection != ReflectionManager.GIRAN_HARBOR.getId()) 
						{
							String back = player.getVar("backCoords");
							if(back != null)
							{
								player.setLoc(Location.parseLoc(back));
								player.unsetVar("backCoords");
							}
							reflection = 0;
						}
					}
				}

				player.setReflection(reflection);

				EventHolder.getInstance().findEvent(player);

				
				Quest.restoreQuestStates(player);

				player.getInventory().restore();

				player.setActiveSubClass(player.getActiveClassId(), false, true);

				player.getMenteeList().restore();
				
				player.getAttendanceRewards().restore();

				player.restoreSummons();

				try
				{
					String var = player.getVar("ExpandInventory");
					if(var != null)
						player.setExpandInventory(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar("ExpandWarehouse");
					if(var != null)
						player.setExpandWarehouse(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar(NO_ANIMATION_OF_CAST_VAR);
					if(var != null)
						player.setNotShowBuffAnim(Boolean.parseBoolean(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar(NO_TRADERS_VAR);
					if(var != null)
						player.setNotShowTraders(Boolean.parseBoolean(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar("pet");
					if(var != null)
						player.setPetControlItem(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

		        try
		        {
		        	String var = player.getVar("@energ_destr_count");
		        	if(var != null)
		        		player.setDestructionCount(Integer.parseInt(var)); 
		        }
		        catch (Exception e)
		        {
		        	_log.error("", e);
		        }
		        
		        try
		        {
		        	String var = player.getVar("@mark_endur_count");
		        	if(var != null)
		        		player.setMarkEndureCount(Integer.parseInt(var)); 
		        }
		        catch (Exception e)
		        {
		        	_log.error("", e);
		        }
		        
				statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?");
				statement3.setString(1, player._login);
				statement3.setInt(2, objectId);
				rset3 = statement3.executeQuery();
				while(rset3.next())
				{
					final Integer charId = rset3.getInt("obj_Id");
					final String charName = rset3.getString("char_name");
					player._chars.put(charId, charName);
				}

				DbUtils.close(statement3, rset3);

				
				{
					LazyArrayList<Zone> zones = LazyArrayList.newInstance();

					World.getZones(zones, player.getLoc(), player.getReflection());

					if(!zones.isEmpty())
						for(Zone zone : zones)
							if(zone.getType() == ZoneType.no_restart)
							{
								if(System.currentTimeMillis() / 1000L - player.getLastAccess() > zone.getRestartTime())
								{
									player.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.EnterWorld.TeleportedReasonNoRestart"));
									player.setLoc(TeleportUtils.getRestartPoint(player, RestartType.TO_VILLAGE).getLoc());
								}
							}
							else if(zone.getType() == ZoneType.SIEGE)
							{
								SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
								if(siegeEvent != null)
									player.setLoc(siegeEvent.getEnterLoc(player, zone));
								else
								{
									Residence r = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
									player.setLoc(r.getNotOwnerRestartPoint(player));
								}
							}

					LazyArrayList.recycle(zones);

					if(DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false))
						player.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
				}

				player.getMacroses().restore();

				
				player.refreshExpertisePenalty();
				player.refreshOverloaded();

				player.getWarehouse().restore();
				player.getFreight().restore();

				player.restorePrivateStore();

				player.updateKetraVarka();
				player.updateRam();
				player.checkDailyCounters();
				player.checkWeeklyCounters();
			}
		}
		catch(final Exception e)
		{
			_log.error("Could not restore char data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(statement2, rset2);
			DbUtils.closeQuietly(statement3, rset3);
			DbUtils.closeQuietly(con, statement, rset);
		}
		return player;
	}

	
	public void store(boolean fast)
	{
		if(!_storeLock.tryLock())
			return;

		try
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(
				"UPDATE characters SET face=?,beautyFace=?,hairStyle=?,beautyHairStyle=?,hairColor=?,beautyHairColor=?,sex=?,x=?,y=?,z=?" + 
				",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,clanid=?,deletetime=?," + 
				"title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + 
				"onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,fame=?,raid_points=?,bookmarks=?,bot_rating=?,is_noble=?,used_world_chat_points=?,hide_head_accessories=? WHERE obj_Id=? LIMIT 1");
				statement.setInt(1, getFace());
				statement.setInt(2, getBeautyFace());
				statement.setInt(3, getHairStyle());
				statement.setInt(4, getBeautyHairStyle());
				statement.setInt(5, getHairColor());
				statement.setInt(6, getBeautyHairColor());
				statement.setInt(7, getSex().ordinal());
				if(_stablePoint == null) 
				{
					statement.setInt(8, getX());
					statement.setInt(9, getY());
					statement.setInt(10, getZ());
				}
				else
				{
					statement.setInt(8, _stablePoint.x);
					statement.setInt(9, _stablePoint.y);
					statement.setInt(10, _stablePoint.z);
				}
				statement.setInt(11, getKarma());
				statement.setInt(12, getPvpKills());
				statement.setInt(13, getPkKills());
				statement.setInt(14, getRecomHave());
				if(getRecomLeft() > 255)
					setRecomLeft(255);
				statement.setInt(15, getRecomLeft());
				statement.setInt(16, getClanId());
				statement.setInt(17, getDeleteTimer());
				statement.setString(18, _title);
				statement.setInt(19, _accessLevel);
				statement.setInt(20, isOnline() && !isInOfflineMode() ? 1 : 0);
				statement.setLong(21, getLeaveClanTime() / 1000L);
				statement.setLong(22, getDeleteClanTime() / 1000L);
				statement.setLong(23, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
				statement.setInt(24, getOnlineTime());
				statement.setInt(25, getPledgeType());
				statement.setInt(26, getPowerGrade());
				statement.setInt(27, getLvlJoinedAcademy());
				statement.setInt(28, getApprentice());
				statement.setBytes(29, getKeyBindings());
				statement.setInt(30, Config.PC_BANG_POINTS_BY_ACCOUNT ? 0 : getPcBangPoints());
				statement.setString(31, getName());
				statement.setInt(32, getFame());
				statement.setInt(33, getRaidPoints());
				statement.setInt(34, getBookMarkList().getCapacity());
				statement.setInt(35, getBotRating());
				statement.setInt(36, getNobleType().ordinal());
				statement.setInt(37, getUsedWorldChatPoints());
				statement.setInt(38, hideHeadAccessories() ? 1 : 0);
				statement.setInt(39, getObjectId());

				statement.executeUpdate();
				GameStats.increaseUpdatePlayerBase();

				if(!fast)
				{
					EffectsDAO.getInstance().insert(this);
					CharacterGroupReuseDAO.getInstance().insert(this);
					storeDisableSkills();
				}

				storeCharSubClasses();
				getBookMarkList().store();

				getDailyMissionList().store();
				if(Config.PC_BANG_POINTS_BY_ACCOUNT)
					AccountVariablesDAO.getInstance().insert(getAccountName(), PC_BANG_POINTS_VAR, String.valueOf(getPcBangPoints()));
			}
			catch(Exception e)
			{
				_log.error("Could not store char data: " + this + "!", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
		finally
		{
			_storeLock.unlock();
		}
	}

	
	public SkillEntry addSkill(SkillEntry newSkillEntry, final boolean store)
	{
		if(newSkillEntry == null)
			return null;

		
		SkillEntry oldSkillEntry = addSkill(newSkillEntry);
		SkillEntry knownSkillEntry = getKnownSkill(newSkillEntry.getId());

		if(knownSkillEntry != null)
		    newSkillEntry = knownSkillEntry;
		
		if(newSkillEntry.equals(oldSkillEntry))
			return oldSkillEntry;

		
		if(store)
			storeSkill(newSkillEntry);

		return oldSkillEntry;
	}

	public SkillEntry removeSkill(SkillEntry skillEntry, boolean fromDB)
	{
		if(skillEntry == null)
			return null;
		return removeSkill(skillEntry.getId(), fromDB);
	}

	
	public SkillEntry removeSkill(int id, boolean fromDB)
	{
		
		SkillEntry oldSkillEntry = removeSkillById(id);

		if(!fromDB)
			return oldSkillEntry;

		if(oldSkillEntry != null)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
				statement.setInt(1, oldSkillEntry.getId());
				statement.setInt(2, getObjectId());
				statement.setInt(3, getActiveClassId());
				statement.execute();
			}
			catch(final Exception e)
			{
				_log.error("Could not delete skill!", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}

		return oldSkillEntry;
	}

	
	private void storeSkill(final SkillEntry newSkillEntry)
	{
		if(newSkillEntry == null) 
		{
			_log.warn("could not store new skill. its NULL");
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newSkillEntry.getId());
			statement.setInt(3, newSkillEntry.getLevel());

			
			if(SkillAcquireHolder.getInstance().containsInTree(newSkillEntry.getTemplate(), AcquireType.CERTIFICATION))
				statement.setInt(4, -1);
			else if(SkillAcquireHolder.getInstance().containsInTree(newSkillEntry.getTemplate(), AcquireType.DUAL_CERTIFICATION) || SkillAcquireHolder.getInstance().containsInTree(newSkillEntry.getTemplate(), AcquireType.HONORABLE_NOBLESSE))
				statement.setInt(4, -2);
			else
				statement.setInt(4, getActiveClassId());

			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("Error could not store skills!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private void restoreSkills()
	{
		restoreSkills(false);
	}

	public void restoreSkills(boolean dualClassSkillsOnly)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id,skill_level,class_index FROM character_skills WHERE char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
			statement.setInt(1, getObjectId());
			statement.setInt(2, getActiveClassId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				int classIndex = rset.getInt("class_index");
				if(dualClassSkillsOnly && classIndex != -2)
					continue;

				final SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(rset.getInt("skill_id"), rset.getInt("skill_level"));
				if(skillEntry == null)
					continue;

				
				if(!isSkillPossible(skillEntry))
				{
					removeSkill(skillEntry, true);
					
					
					continue;
				}

				if(classIndex != -2 || isBaseClassActive() || isDualClassActive())
					addSkill(skillEntry);
			}

			if(dualClassSkillsOnly)
				return;

			
			checkNobleSkills();

			
			checkHeroSkills();

			
			if(_clan != null)
				_clan.addSkillsQuietly(this);

			if(Config.UNSTUCK_SKILL && getSkillLevel(1050) < 0)
				addSkill(SkillHolder.getInstance().getSkillEntry(2099, 1));

			if(isGM())
				giveGMSkills();
		}
		catch(final Exception e)
		{
			_log.warn("Could not restore skills for player objId: " + getObjectId());
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	private boolean isSkillPossible(SkillEntry skillEntry)
	{
		if(isGM())
			return true;

		if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skillEntry.getTemplate()))
			return false;

		if(SkillUtils.isEnchantedSkill(skillEntry.getLevel()) && !skillEntry.getTemplate().isEnchantable())
		    return false;
		
		return true;
	}

	public void storeDisableSkills()
	{
		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

			if(_skillReuses.isEmpty())
				return;

			SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
			synchronized (_skillReuses)
			{
				StringBuilder sb;
				for(TimeStamp timeStamp : _skillReuses.values())
				{
					if(timeStamp.hasNotPassed())
					{
						sb = new StringBuilder("(");
						sb.append(getObjectId()).append(",");
						sb.append(timeStamp.getId()).append(",");
						sb.append(timeStamp.getLevel()).append(",");
						sb.append(getActiveClassId()).append(",");
						sb.append(timeStamp.getEndTime()).append(",");
						sb.append(timeStamp.getReuseBasic()).append(")");
						b.write(sb.toString());
					}
				}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.warn("Could not store disable skills data: " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void restoreDisableSkills()
	{
		_skillReuses.clear();

		Connection con = null;
		Statement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + getObjectId() + " AND class_index=" + getActiveClassId());
			while(rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLevel = rset.getInt("skill_level");
				long endTime = rset.getLong("end_time");
				long rDelayOrg = rset.getLong("reuse_delay_org");
				long curTime = System.currentTimeMillis();

				Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLevel);

				if(skill != null && endTime - curTime > 500)
					_skillReuses.put(skill.getReuseHash(), new TimeStamp(skill, endTime, rDelayOrg));
			}
			DbUtils.close(statement);

			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
		}
		catch(Exception e)
		{
			_log.error("Could not restore active skills data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	@Override
	public boolean consumeItem(int itemConsumeId, long itemCount, boolean sendMessage)
	{
		return ItemFunctions.deleteItem(this, itemConsumeId, itemCount, sendMessage);
	}

	@Override
	public boolean consumeItemMp(int itemId, int mp)
	{
		for(ItemInstance item : getInventory().getPaperdollItems())
			if(item != null && item.getItemId() == itemId)
			{
				final int newMp = item.getLifeTime() - mp;
				if(newMp >= 0)
				{
					item.setLifeTime(newMp);
					sendPacket(new InventoryUpdatePacket().addModifiedItem(this, item));
					return true;
				}
				break;
			}
		return false;
	}

	
	@Override
	public boolean isMageClass()
	{
		return getClassId().isMage();
	}

	
	public boolean checkLandingState()
	{
		if(isInZone(ZoneType.no_landing))
			return false;

		SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
		if(siege != null)
		{
			Residence unit = siege.getResidence();
			if(unit != null && getClan() != null && isClanLeader() && (getClan().getCastle() == unit.getId() || getClan().getHasFortress() == unit.getId()))
				return true;
			return false;
		}

		return true;
	}

	public void setMount(int controlItemObjId, int npcId, int level, int currentFeed)
	{
		Mount mount = Mount.create(this, controlItemObjId, npcId, level, currentFeed);
		if(mount != null)
			setMount(mount);
	}

	public void setMount(Mount mount)
	{
		if(_mount == mount)
			return;

		if(isCursedWeaponEquipped())
			return;

		Mount oldMount = _mount;
		_mount = null;
		if(oldMount != null) 
			oldMount.onUnride();

		if(mount != null)
		{
			_mount = mount;
			_mount.onRide();
		}
	}

	public boolean isMounted()
	{
		return _mount != null;
	}

	public Mount getMount()
	{
		return _mount;
	}

	public int getMountControlItemObjId()
	{
		return isMounted() ? _mount.getControlItemObjId() : 0;
	}

	public int getMountNpcId()
	{
		return isMounted() ? _mount.getNpcId() : 0;
	}

	public int getMountLevel()
	{
		return isMounted() ? _mount.getLevel() : 0;
	}

	public int getMountCurrentFeed()
	{
		return isMounted() ? _mount.getCurrentFeed() : 0;
	}

	public void unEquipWeapon()
	{
		ItemInstance wpn = getSecondaryWeaponInstance();
		if(wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		wpn = getActiveWeaponInstance();
		if(wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		abortAttack(true, true);
		abortCast(true, true);
	}

	public void sendDisarmMessage(ItemInstance wpn)
	{
		if(wpn.getEnchantLevel() > 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
			sm.addNumber(wpn.getEnchantLevel());
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
	}

	
	public void setUsingWarehouseType(final WarehouseType type)
	{
		_usingWHType = type;
	}

	
	public WarehouseType getUsingWarehouseType()
	{
		return _usingWHType;
	}

	public Collection<Cubic> getCubics()
	{
		return _cubics == null ? Collections.<Cubic>emptyList() : _cubics.values();
	}

	@Override
	public void deleteCubics()
	{
		for(Cubic cubic : getCubics())
			cubic.delete();
	}

	public void addCubic(Cubic cubic)
	{
		if(_cubics == null)
			_cubics = new CHashIntObjectMap<Cubic>(3);
		Cubic oldCubic = _cubics.get(cubic.getSlot());
		if(oldCubic != null)
			oldCubic.delete();

		_cubics.put(cubic.getSlot(), cubic);

		sendPacket(new ExUserInfoCubic(this));
	}

	public void removeCubic(int slot)
	{
		if(_cubics != null)
			_cubics.remove(slot);

		sendPacket(new ExUserInfoCubic(this));
	}

	public Cubic getCubic(int slot)
	{
		return _cubics == null ? null : _cubics.get(slot);
	}

	@Override
	public String toString()
	{
		return getName() + "[" + getObjectId() + "]";
	}

	
	@Override
	public int getEnchantEffect()
	{
		final ItemInstance wpn = getActiveWeaponInstance();

		if(wpn == null)
			return 0;

		return Math.min(127, wpn.getFixedEnchantLevel(this));
	}

	
	public void setLastNpc(final NpcInstance npc)
	{
		if(npc == null)
			_lastNpc = HardReferences.emptyRef();
		else
			_lastNpc = npc.getRef();
	}

	
	public NpcInstance getLastNpc()
	{
		return _lastNpc.get();
	}

	public void setMultisell(MultiSellListContainer multisell)
	{
		_multisell = multisell;
	}

	public MultiSellListContainer getMultisell()
	{
		return _multisell;
	}

	@Override
	public boolean unChargeShots(boolean spirit)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon == null)
			return false;

		if(spirit)
			weapon.setChargedSpiritshotPower(0);
		else
			weapon.setChargedSoulshotPower(0);

		autoShot();
		return true;
	}

	public boolean unChargeFishShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon == null)
			return false;

		weapon.setChargedFishshotPower(0);

		autoShot();
		return true;
	}

	public void autoShot()
	{
		for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
		{
			int shotId = entry.getKey();

			ItemInstance item = getInventory().getItemByItemId(shotId);
			if(item == null)
			{
				removeAutoShot(shotId, false, entry.getValue());
				continue;
			}
			useItem(item, false, false);
		}
	}

	@Override
	public double getChargedSoulshotPower()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null && weapon.getChargedSoulshotPower() > 0)
			return calcStat(Stats.SOULSHOT_POWER, weapon.getChargedSoulshotPower());
		return 0;
	}

	@Override
	public void setChargedSoulshotPower(double val)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null)
			weapon.setChargedSoulshotPower(val);
	}

	@Override
	public double getChargedSpiritshotPower()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null && weapon.getChargedSpiritshotPower() > 0)
			return calcStat(Stats.SPIRITSHOT_POWER, weapon.getChargedSpiritshotPower());
		return 0;
	}

	@Override
	public void setChargedSpiritshotPower(double val)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null)
			weapon.setChargedSpiritshotPower(val);
	}

	public double getChargedFishshotPower()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null)
			return weapon.getChargedFishshotPower();
		return 0;
	}

	public void setChargedFishshotPower(double val)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon != null)
			weapon.setChargedFishshotPower(val);
	}

	public boolean addAutoShot(int itemId, boolean sendMessage, SoulShotType type)
	{
		if(Config.EX_USE_AUTO_SOUL_SHOT)
		{
			for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
			{
				if(entry.getValue() == type)
					_activeAutoShots.remove(entry.getKey());
			}
			if(type == SoulShotType.SOULSHOT || type == SoulShotType.SPIRITSHOT)
			{
				WeaponTemplate weaponTemplate = getActiveWeaponTemplate();
				if(weaponTemplate == null)
					return false;

				ItemTemplate shotTemplate = ItemHolder.getInstance().getTemplate(itemId);
				if(shotTemplate == null)
					return false;

				if(shotTemplate.getGrade().extGrade() != weaponTemplate.getGrade().extGrade())
					return false;
			}
			else if((type == SoulShotType.BEAST_SOULSHOT || type == SoulShotType.BEAST_SPIRITSHOT) && getServitorsCount() == 0)
				return false;
		}

		if(_activeAutoShots.put(itemId, type) != type)
		{
			if(!Config.EX_USE_AUTO_SOUL_SHOT)
				sendPacket(new ExAutoSoulShot(itemId, 1, type));

			if(sendMessage)
				sendPacket(new SystemMessagePacket(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(itemId));

			return true;
		}
		return false;
	}

	public boolean manuallyAddAutoShot(int itemId, SoulShotType type, boolean save)
	{
		if(addAutoShot(itemId, true, type))
		{
			if(Config.EX_USE_AUTO_SOUL_SHOT)
			{
				if(save)
					setVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), itemId);
				else
					unsetVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal());
			}
			return true;
		}
		return false;
	}

	public void sendActiveAutoShots()
	{
		if(Config.EX_USE_AUTO_SOUL_SHOT)
			return;

		for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
			sendPacket(new ExAutoSoulShot(entry.getKey(), 1, entry.getValue()));
	}

	public void initActiveAutoShots()
	{
		if(!Config.EX_USE_AUTO_SOUL_SHOT)
			return;

		for(SoulShotType type : SoulShotType.VALUES)
		{
			if(!initSavedActiveShot(type))
				sendPacket(new ExAutoSoulShot(0, 1, type));
		}
	}

	public boolean initSavedActiveShot(SoulShotType type)
	{
		if(!Config.EX_USE_AUTO_SOUL_SHOT)
			return false;

		int shotId = getVarInt(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), 0);
		if(shotId > 0)
		{
			ItemInstance item = getInventory().getItemByItemId(shotId);
			if(item != null)
			{
				IItemHandler handler = item.getTemplate().getHandler();
				if(handler != null && handler.isAutoUse() && addAutoShot(shotId, true, type))
				{
					sendPacket(new ExAutoSoulShot(shotId, 3, type));
					ItemFunctions.useItem(this, item, false, false);
					return true;
				}
			}
		}
		else if(shotId == -1)
		{
			sendPacket(new ExAutoSoulShot(0, 2, type));
			return true;
		}
		return false;
	}

	public void removeAutoShots(boolean uncharge)
	{
		if(Config.EX_USE_AUTO_SOUL_SHOT)
			return;

		for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
			removeAutoShot(entry.getKey(), false, entry.getValue());

		if(uncharge)
		{
			ItemInstance weapon = getActiveWeaponInstance();
			if(weapon != null)
			{
				weapon.setChargedSoulshotPower(0);
				weapon.setChargedSpiritshotPower(0);
				weapon.setChargedFishshotPower(0);
			}
		}
	}

	public boolean removeAutoShot(int itemId, boolean sendMessage, SoulShotType type)
	{
		if(_activeAutoShots.remove(itemId) != null)
		{
			if(!Config.EX_USE_AUTO_SOUL_SHOT)
				sendPacket(new ExAutoSoulShot(itemId, 0, type));

			if(sendMessage)
				sendPacket(new SystemMessagePacket(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(itemId));

			return true;
		}
		return false;
	}

	public boolean manuallyRemoveAutoShot(int itemId, SoulShotType type, boolean save)
	{
		if(removeAutoShot(itemId, true, type))
		{
			if(Config.EX_USE_AUTO_SOUL_SHOT)
			{
				if(save)
					setVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), -1);
				else
					unsetVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal());
			}
			return true;
		}
		return false;
	}

	public void removeAutoShot(SoulShotType type)
	{
		if(!Config.EX_USE_AUTO_SOUL_SHOT)
			return;

		for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
		{
			if(entry.getValue() == type)
			{
				removeAutoShot(entry.getKey(), false, entry.getValue());
				sendPacket(new ExAutoSoulShot(entry.getKey(), 1, entry.getValue()));
			}
		}
	}

	public boolean isAutoShot(int itemId)
	{
		return _activeAutoShots.containsKey(itemId);
	}

	public boolean isAutoShot(SoulShotType type)
	{
		return _activeAutoShots.containsValue( type);
	}

	@Override
	public boolean isInvisible(GameObject observer)
	{
		if(observer != null)
		{
			if(isMyServitor(observer.getObjectId()))
				return false;

			if(observer.isPlayer())
			{
				Player observPlayer = (Player) observer;
				if(isInSameParty(observPlayer))
					return false;
			}
		}
		return super.isInvisible(observer) || isGMInvisible();
	}

	@Override
	public boolean startInvisible(Object owner, boolean withServitors)
	{
		if(super.startInvisible(owner, withServitors))
		{
			sendUserInfo(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean stopInvisible(Object owner, boolean withServitors)
	{
		if(super.stopInvisible(owner, withServitors))
		{
			sendUserInfo(true);
			return true;
		}
		return false;
	}

	public boolean isGMInvisible()
	{
		return getPlayerAccess().GodMode && _gmInvisible.get();
	}

	public boolean setGMInvisible(boolean value)
	{
		if(value)
			return _gmInvisible.getAndSet(true);
		return _gmInvisible.setAndGet(false);
	}

	@Override
	public boolean isUndying()
	{
		return super.isUndying() || isGMUndying();
	}

	public boolean isGMUndying()
	{
		return getPlayerAccess().GodMode && _gmUndying.get();
	}

	public boolean setGMUndying(boolean value)
	{
		if(value)
			return _gmUndying.getAndSet(true);
		return _gmUndying.setAndGet(false);
	}

	public int getClanPrivileges()
	{
		if(_clan == null)
			return 0;
		if(isClanLeader())
			return Clan.CP_ALL;
		if(_powerGrade < 1 || _powerGrade > 9)
			return 0;
		RankPrivs privs = _clan.getRankPrivs(_powerGrade);
		if(privs != null)
			return privs.getPrivs();
		return 0;
	}

	public void teleToClosestTown()
	{
		TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_VILLAGE);
		teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
	}

	public void teleToCastle()
	{
		TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_CASTLE);
		teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
	}

	public void teleToFortress()
	{
		TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_FORTRESS);
	    teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
	}

	public void teleToClanhall()
	{
		TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_CLANHALL);
		teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
	}

	@Override
	public void sendMessage(CustomMessage message)
	{
		sendPacket(message);
	}

	public void teleToLocation(Location loc, boolean replace)
	{
		_isInReplaceTeleport = replace;

		teleToLocation(loc);

		_isInReplaceTeleport = false;
	}

	@Override
	public boolean onTeleported()
	{
		if(!super.onTeleported())
			return false;

		if(isFakeDeath())
			breakFakeDeath();

		if(isInBoat())
			setLoc(getBoat().getLoc());

		
		setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);

		spawnMe();

		setLastClientPosition(getLoc());
		setLastServerPosition(getLoc());

		if(isPendingRevive())
			doRevive();

		sendActionFailed();

		getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

		if(isLockedTarget() && getTarget() != null)
			sendPacket(new MyTargetSelectedPacket(this, getTarget()));

		sendUserInfo(true);

		if(!_isInReplaceTeleport)
		{
			for(Servitor servitor : getServitors())
				servitor.teleportToOwner();
		}

		getListeners().onTeleported();

		for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_TELEPORT))
			hook.onPlayerTeleport(this, getReflectionId());

		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_TELEPORT))
			hook.onPlayerTeleport(this, getReflectionId());

		return true;
	}

	public boolean enterObserverMode(Location loc)
	{
		WorldRegion observerRegion = World.getRegion(loc);
		if(observerRegion == null)
			return false;
		if(!_observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING))
			return false;

		setTarget(null);
		stopMove();
		sitDown(null);
		setFlying(true);

		
		World.removeObjectsFromPlayer(this);

		_observePoint = new ObservePoint(this);
		_observePoint.setLoc(loc);
		_observePoint.getFlags().getImmobilized().start();

		
		broadcastCharInfoImpl();

		
		sendPacket(new ObserverStartPacket(loc));

		return true;
	}

	public boolean enterArenaObserverMode(ObservableArena arena)
	{
		Location enterPoint = arena.getObserverEnterPoint(this);
		WorldRegion observerRegion = World.getRegion(enterPoint);
		if(observerRegion == null)
			return false;

		if(!_observerMode.compareAndSet(isInArenaObserverMode() ? 3 : 0, 1))
			return false;

		sendPacket(new TeleportToLocationPacket(this, enterPoint));
		setTarget(null);
		stopMove();

		World.removeObjectsFromPlayer(this);

		if(_observableArena != null)
		{
			_observableArena.removeObserver(_observePoint);
			_observableArena.onChangeObserverArena(this);
			_observePoint.decayMe();
		}
		else
		{
			broadcastCharInfoImpl();
			arena.onEnterObserverArena(this);
			_observePoint = new ObservePoint(this);
		}

		_observePoint.setLoc(enterPoint);
		_observePoint.setReflection(arena.getReflection());

		_observableArena = arena;

		sendPacket(new ExTeleportToLocationActivate(this, enterPoint));

		return true;
	}

	public void appearObserverMode()
	{
		if(!_observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED))
			return;

		_observePoint.spawnMe();
		sendUserInfo(true);
		if(_observableArena != null)
		{
			_observableArena.addObserver(_observePoint);
			_observableArena.onAppearObserver(_observePoint);
		}
	}

	public void leaveObserverMode()
	{
		if(!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
			return;

		ObservableArena arena = _observableArena;
		if(arena != null)
		{
			sendPacket(new TeleportToLocationPacket(this, getLoc()));
			_observableArena.removeObserver(_observePoint);
			_observableArena = null;
		}

		_observePoint.deleteMe();
		_observePoint = null;

		setTarget(null);
		stopMove();

		if(arena != null)
		{
			arena.onExitObserverArena(this);
			sendPacket(new ExTeleportToLocationActivate(this, getLoc()));
		}
		else 
			sendPacket(new ObserverEndPacket(getLoc()));
	}

	public void returnFromObserverMode()
	{
		if(!_observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE))
			return;

		
		setLastClientPosition(null);
		setLastServerPosition(null);

		standUp();
		setFlying(false);

		broadcastUserInfo(true);

		World.showObjectsToPlayer(this);
	}

	public void setOlympiadSide(final int i)
	{
		_olympiadSide = i;
	}

	public int getOlympiadSide()
	{
		return _olympiadSide;
	}

	public boolean isInObserverMode()
	{
		return getObserverMode() > 0;
	}

	public boolean isInArenaObserverMode()
	{
		return _observableArena != null;
	}

	public ObservableArena getObservableArena()
	{
		return _observableArena;
	}

	public int getObserverMode()
	{
		return _observerMode.get();
	}

	public ObservePoint getObservePoint()
	{
		return _observePoint;
	}

	public int getTeleMode()
	{
		return _telemode;
	}

	public void setTeleMode(final int mode)
	{
		_telemode = mode;
	}

	public void setLoto(final int i, final int val)
	{
		_loto[i] = val;
	}

	public int getLoto(final int i)
	{
		return _loto[i];
	}

	public void setRace(final int i, final int val)
	{
		_race[i] = val;
	}

	public int getRace(final int i)
	{
		return _race[i];
	}

	public boolean getMessageRefusal()
	{
		return _messageRefusal;
	}

	public void setMessageRefusal(final boolean mode)
	{
		_messageRefusal = mode;
	}

	public void setTradeRefusal(final boolean mode)
	{
		_tradeRefusal = mode;
	}

	public boolean getTradeRefusal()
	{
		return _tradeRefusal;
	}

	public boolean isBlockAll()
	{
		return _blockAll;
	}

	public void setBlockAll(final boolean state)
	{
		_blockAll = state;
	}

	public void setHero(final boolean hero)
	{
		_hero = hero;
	}

	@Override
	public boolean isHero()
	{
		return _hero;
	}

    public boolean isChaosFestivalWinner()
    {
        return ChaosFestivalManager.getInstance().isWinnerReceived(this);
    }
    
	public void setIsInOlympiadMode(final boolean b)
	{
		_inOlympiadMode = b;
	}

	public boolean isInOlympiadMode()
	{
		return _inOlympiadMode;
	}

	public boolean isOlympiadGameStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 1;
	}

	public boolean isOlympiadCompStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 2;
	}

	public final void setNobleType(NobleType type)
	{
		setNobleType(type, false);
	}

	public final void setNobleType(NobleType type, boolean onRestore)
	{
		if(_nobleType == type)
			return;

		_nobleType = type;

		if(!onRestore)
		{
			if(isNoble())
			{
				broadcastPacket(new MagicSkillUse(this, this, 6673, 1, 1000, 0));

			}

			updatePledgeRank();
			checkNobleSkills();
			sendSkillList();
			broadcastUserInfo(true);
		}

		if(Config.ENABLE_OLYMPIAD)
		{
			if(isNoble())
				Olympiad.addParticipant(this);
			else
				Olympiad.removeParticipant(this);
		}
	}

	public NobleType getNobleType()
	{
		return _nobleType;
	}

	public boolean isNoble()
	{
		return _nobleType != NobleType.NONE;
	}

	public boolean isHonorableNoble()
	{
		return _nobleType == NobleType.HONORABLE;
	}

	public int getSubLevel()
	{
		return isBaseClassActive() ? 0 : getLevel();
	}

	
	public void updateKetraVarka()
	{
		if(ItemFunctions.getItemCount(this, 7215) > 0)
			_ketra = 5;
		else if(ItemFunctions.getItemCount(this, 7214) > 0)
			_ketra = 4;
		else if(ItemFunctions.getItemCount(this, 7213) > 0)
			_ketra = 3;
		else if(ItemFunctions.getItemCount(this, 7212) > 0)
			_ketra = 2;
		else if(ItemFunctions.getItemCount(this, 7211) > 0)
			_ketra = 1;
		else if(ItemFunctions.getItemCount(this, 7225) > 0)
			_varka = 5;
		else if(ItemFunctions.getItemCount(this, 7224) > 0)
			_varka = 4;
		else if(ItemFunctions.getItemCount(this, 7223) > 0)
			_varka = 3;
		else if(ItemFunctions.getItemCount(this, 7222) > 0)
			_varka = 2;
		else if(ItemFunctions.getItemCount(this, 7221) > 0)
			_varka = 1;
		else
		{
			_varka = 0;
			_ketra = 0;
		}
	}

	public int getVarka()
	{
		return _varka;
	}

	public int getKetra()
	{
		return _ketra;
	}

	public void updateRam()
	{
		if(ItemFunctions.getItemCount(this, 7247) > 0)
			_ram = 2;
		else if(ItemFunctions.getItemCount(this, 7246) > 0)
			_ram = 1;
		else
			_ram = 0;
	}

	public int getRam()
	{
		return _ram;
	}

	public void setPledgeType(final int typeId)
	{
		_pledgeType = typeId;
	}

	public int getPledgeType()
	{
		return _pledgeType;
	}

	public void setLvlJoinedAcademy(int lvl)
	{
		_lvlJoinedAcademy = lvl;
	}

	public int getLvlJoinedAcademy()
	{
		return _lvlJoinedAcademy;
	}

	public PledgeRank getPledgeRank()
	{
		return _pledgeRank;
	}

	public void updatePledgeRank()
	{
		if(isGM()) 
		{
			_pledgeRank = PledgeRank.EMPEROR;
			return;
		}

		int CLAN_LEVEL = _clan == null ? -1 : _clan.getLevel();
		boolean IN_ACADEMY = _clan != null && Clan.isAcademy(_pledgeType);
		boolean IS_GUARD = _clan != null && Clan.isRoyalGuard(_pledgeType);
		boolean IS_KNIGHT = _clan != null && Clan.isOrderOfKnights(_pledgeType);

		boolean IS_GUARD_CAPTAIN = false, IS_KNIGHT_COMMANDER = false, IS_LEADER = false;

		SubUnit unit = getSubUnit();
		if(unit != null)
		{
			UnitMember unitMember = unit.getUnitMember(getObjectId());
			if(unitMember == null)
			{
				_log.warn("Player: unitMember null, clan: " + _clan.getClanId() + "; pledgeType: " + unit.getType());
				return;
			}
			IS_GUARD_CAPTAIN = Clan.isRoyalGuard(unitMember.isLeaderOf());
			IS_KNIGHT_COMMANDER = Clan.isOrderOfKnights(unitMember.isLeaderOf());
			IS_LEADER = unitMember.isLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
		}

		switch(CLAN_LEVEL)
		{
			case -1:
				_pledgeRank = PledgeRank.VAGABOND;
				break;
			case 0:
			case 1:
			case 2:
			case 3:
				_pledgeRank = PledgeRank.VASSAL;
				break;
			case 4:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.KNIGHT;
				else
					_pledgeRank = PledgeRank.VASSAL;
				break;
			case 5:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else
					_pledgeRank = PledgeRank.HEIR;
				break;
			case 6:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.BARON;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.HEIR;
				else
					_pledgeRank = PledgeRank.KNIGHT;
				break;
			case 7:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.COUNT;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.KNIGHT;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.HEIR;
				else
					_pledgeRank = PledgeRank.WISEMAN;
				break;
			case 8:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.MARQUIS;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.KNIGHT;
				else
					_pledgeRank = PledgeRank.BARON;
				break;
			case 9:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.DUKE;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.MARQUIS;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.WISEMAN;
				else
					_pledgeRank = PledgeRank.VISCOUNT;
				break;
			case 10:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.GRAND_DUKE;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.DUKE;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.MARQUIS;
				else
					_pledgeRank = PledgeRank.COUNT;
				break;
			case 11:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.DISTINGUISHED_KING;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.GRAND_DUKE;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.DUKE;
				else
					_pledgeRank = PledgeRank.MARQUIS;
				break;
		}

		if(isHero() && _pledgeRank.ordinal() < PledgeRank.MARQUIS.ordinal())
			_pledgeRank = PledgeRank.MARQUIS;
		else if(isNoble() && _pledgeRank.ordinal() < PledgeRank.BARON.ordinal())
			_pledgeRank = PledgeRank.BARON;
	}

	public void setPowerGrade(final int grade)
	{
		_powerGrade = grade;
	}

	public int getPowerGrade()
	{
		return _powerGrade;
	}

	public void setApprentice(final int apprentice)
	{
		_apprentice = apprentice;
	}

	public int getApprentice()
	{
		return _apprentice;
	}

	public int getSponsor()
	{
		return _clan == null ? 0 : _clan.getAnyMember(getObjectId()).getSponsor();
	}
	
	@Override
	public int getNameColor()
	{
		if(isInObserverMode())
			return Color.black.getRGB();

		return _nameColor;
	}

	public void setNameColor(final int nameColor)
	{
		if(nameColor != Config.NORMAL_NAME_COLOUR && nameColor != Config.CLANLEADER_NAME_COLOUR && nameColor != Config.GM_NAME_COLOUR && nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR)
			setVar("namecolor", Integer.toHexString(nameColor));
		else if(nameColor == Config.NORMAL_NAME_COLOUR)
			unsetVar("namecolor");
		_nameColor = nameColor;
	}

	public void setNameColor(final int red, final int green, final int blue)
	{
		_nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
		if(_nameColor != Config.NORMAL_NAME_COLOUR && _nameColor != Config.CLANLEADER_NAME_COLOUR && _nameColor != Config.GM_NAME_COLOUR && _nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR)
			setVar("namecolor", Integer.toHexString(_nameColor));
		else
			unsetVar("namecolor");
	}

	private void restoreVariables()
	{
		List<CharacterVariable> variables = CharacterVariablesDAO.getInstance().restore(getObjectId());
		for(CharacterVariable var : variables)
			_variables.put(var.getName(), var);
	}

	public Collection<CharacterVariable> getVariables()
	{
		return _variables.values();
	}

	public boolean setVar(String name, String value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, String value, long expirationTime)
	{
		CharacterVariable var = new CharacterVariable(name, value, expirationTime);
		if(CharacterVariablesDAO.getInstance().insert(getObjectId(), var))
		{
			_variables.put(name, var);
			return true;
		}
		return false;
	}

	public boolean setVar(String name, int value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, int value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, long value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, long value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, double value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, double value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, boolean value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, boolean value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean unsetVar(String name)
	{
		if(name == null || name.isEmpty())
			return false;

		if(_variables.containsKey(name) && CharacterVariablesDAO.getInstance().delete(getObjectId(), name))
			return _variables.remove(name) != null;

		return false;
	}

	public String getVar(String name)
	{
		return getVar(name, null);
	}

	public String getVar(String name, String defaultValue)
	{
		CharacterVariable var = _variables.get(name);
		if(var != null && !var.isExpired())
			return var.getValue();

		return defaultValue;
	}

	public long getVarExpireTime(String name)
	{
		CharacterVariable var = _variables.get(name);
		if(var != null)
			return var.getExpireTime();

		return 0;
	}

	public int getVarInt(String name)
	{
		return getVarInt(name, 0);
	}

	public int getVarInt(String name, int defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Integer.parseInt(var);

		return defaultValue;
	}

	public long getVarLong(String name)
	{
		return getVarLong(name, 0L);
	}

	public long getVarLong(String name, long defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Long.parseLong(var);

		return defaultValue;
	}

	public double getVarDouble(String name)
	{
		return getVarDouble(name, 0.);
	}

	public double getVarDouble(String name, double defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Double.parseDouble(var);

		return defaultValue;
	}

	public boolean getVarBoolean(String name)
	{
		return getVarBoolean(name, false);
	}

	public boolean getVarBoolean(String name, boolean defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return !(var.equals("0") || var.equalsIgnoreCase("false"));

		return defaultValue;
	}

	public void setLanguage(String val)
	{
		_language = Language.getLanguage(val);
		setVar(Language.LANG_VAR, _language.getShortName(), -1);
	}

	public Language getLanguage()
	{
		if(Config.USE_CLIENT_LANG && getNetConnection() != null)
			return getNetConnection().getLanguage();
		return _language;
	}

	public int getLocationId()
	{
		if(getNetConnection() != null)
			return getNetConnection().getLanguage().getId();
		return -1;
	}

	public boolean isLangRus()
	{
		return getLanguage() == Language.RUSSIAN;
	}

	public int isAtWarWith(int id)
	{
		return _clan == null || !_clan.isAtWarWith(id) ? 0 : 1;
	}

	public void stopWaterTask()
	{
		if(_taskWater != null)
		{
			_taskWater.cancel(false);
			_taskWater = null;
			sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, 0));
			sendChanges();
		}
	}

	public void startWaterTask()
	{
		if(isDead())
			stopWaterTask();
		else if(Config.ALLOW_WATER && _taskWater == null)
		{
			int timeinwater = (int) (calcStat(Stats.BREATH, getBaseStats().getBreathBonus(), null, null) * 1000L);
			sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, timeinwater));
			if(isTransformed() && !getTransform().isCanSwim())
				setTransform(null);

			_taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
			sendChanges();
		}
	}

	public void doRevive(double percent)
	{
		restoreExp(percent);
		doRevive();
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		setAgathionRes(false);
		unsetVar("lostexp");
		updateAbnormalIcons();
		autoShot();
		if(isMounted())
			_mount.onRevive();
	}

	public void reviveRequest(Player reviver, double percent, boolean pet)
	{
		ReviveAnswerListener reviveAsk = _askDialog != null && _askDialog.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) _askDialog.getValue() : null;
		if(reviveAsk != null)
		{
			if(reviveAsk.isForPet() == pet && reviveAsk.getPower() >= percent)
			{
				reviver.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
				return;
			}
			if(pet && !reviveAsk.isForPet())
			{
				reviver.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
				return;
			}
			if(pet && isDead())
			{
				reviver.sendPacket(SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
				return;
			}
		}

		if(pet && getPet() != null && getPet().isDead() || !pet && isDead())
		{

			ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0);
			pkt.addName(reviver).addInteger(Math.round(percent));

			ask(pkt, new ReviveAnswerListener(this, percent, pet));
		}
	}

	public void requestCheckBot()
	{
		BotCheckQuestion question = BotCheckManager.generateRandomQuestion();
		int qId = question.getId();
		String qDescr = question.getDescr(isLangRus());
		
		ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.S1, 60000).addString(qDescr);
		
		ask(pkt, new BotCheckAnswerListner(this, qId));
	}
	
	public void increaseBotRating()
	{
		int bot_points = getBotRating();
		if(bot_points + 1 >= Config.MAX_BOT_POINTS)
			return;
		setBotRating(bot_points + 1);	
	}
	
	public void decreaseBotRating()
	{
		int bot_points = getBotRating();
		if(bot_points - 1 <= Config.MINIMAL_BOT_RATING_TO_BAN)
		{
			if(toJail(Config.AUTO_BOT_BAN_JAIL_TIME))
			{
				sendMessage("You moved to jail, time to escape - " + Config.AUTO_BOT_BAN_JAIL_TIME + " minutes, reason - botting .");
				if(Config.ANNOUNCE_AUTO_BOT_BAN)
					Announcements.announceToAll("Player " + getName() + " jailed for botting!");
			}
		}
		else
		{
			setBotRating(bot_points - 1);
			if(Config.ON_WRONG_QUESTION_KICK)
				kick();
		}	
	}
	
	public void setBotRating(int rating)
	{
		_botRating = rating;
	}
	
	public int getBotRating()
	{
		return _botRating;
	}

	public boolean isInJail()
	{
		return _isInJail;
	}

	public void setIsInJail(boolean value)
	{
		_isInJail = value;
	}

	public boolean toJail(int time)
	{
		if(isInJail())
			return false;

		setIsInJail(true);
		setVar(JAILED_VAR, true, System.currentTimeMillis() + (time * 60000));
		startUnjailTask(this, time);

		if(getReflection().isMain())
			setVar("backCoords", getLoc().toXYZString(), -1);

		if(isInStoreMode())
		{
			setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			storePrivateStore();
		}

		teleToLocation(Location.findPointToStay(this, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);

		return true;
	}

	public boolean fromJail()
	{
		if(!isInJail())
			return false;

		setIsInJail(false);
		unsetVar(JAILED_VAR);
		stopUnjailTask();

		String back = getVar("backCoords");
		if(back != null)
		{
			teleToLocation(Location.parseLoc(back), ReflectionManager.MAIN);
			unsetVar("backCoords");
		}
		return true;
	}

	public void summonCharacterRequest(final Creature summoner, final Location loc, final int summonConsumeCrystal)
	{
		ConfirmDlgPacket cd = new ConfirmDlgPacket(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
		cd.addName(summoner).addZoneName(loc);

		ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal));
	}

	public void updateNoChannel(final long time)
	{
		setNoChannel(time);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
			statement = con.prepareStatement(stmt);
			statement.setLong(1, _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
			statement.setInt(2, getObjectId());
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.warn("Could not activate nochannel:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		sendPacket(new EtcStatusUpdatePacket(this));
	}

	public boolean canTalkWith(Player player)
	{
		return _NoChannel >= 0 || player == this;
	}

	private void checkDailyCounters()
	{
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		temp.set(Calendar.MILLISECOND, 0);
		long daysPassed = Math.round((System.currentTimeMillis() / 1000 - _lastAccess) / 86400);
		if(daysPassed == 0 && _lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
			daysPassed++;

		for(int i = 1; i < daysPassed; i++)
			setRecomHave(getRecomHave() - 20);

		if(daysPassed > 0)
			restartDailyCounters(true);
	}

	public void restartDailyCounters(boolean onRestore)
	{
	    setRecomLeftToday(0);
	    setRecomLeft(20);
	    setRecomHave(getRecomHave() - 20);
	    if(!onRestore)
	      sendUserInfo(true); 
	    setDestructionCount(0);
	    setMarkEndureCount(0);
		if(Config.ALLOW_WORLD_CHAT)
		{
			setUsedWorldChatPoints(0);
			if(!onRestore)
				sendPacket(new ExWorldChatCnt(this));
		}
	}

	private void checkWeeklyCounters()
	{
		Calendar temp = Calendar.getInstance();
		if(temp.get(Calendar.DAY_OF_WEEK) > Calendar.WEDNESDAY)
			temp.add(Calendar.DAY_OF_MONTH, 7);

		temp.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		temp.set(Calendar.MILLISECOND, 0);
		if(_lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
			restartWeeklyCounters(true);
	}

	public void restartWeeklyCounters(boolean onRestore)
	{
	    for(SubClass sub : getSubClassList().values())
	    {
	        sub.setVitality(140000);
	        sub.setUsedVitalityPotions(0);
	    } 
	    if(!onRestore)
	        sendPacket(new ExVitalityEffectInfo(this));
	}

	public SubClassList getSubClassList()
	{
		return _subClassList;
	}

	public SubClass getBaseSubClass()
	{
		return _subClassList.getBaseSubClass();
	}

	public int getBaseClassId()
	{
		if(getBaseSubClass() != null)
			return getBaseSubClass().getClassId();

		return -1;
	}

    public int getBaseDefaultClassId()
    {
        if(getBaseSubClass() != null)
            return getBaseSubClass().getDefaultClassId();
        
        return -1;
    }

	public SubClass getActiveSubClass()
	{
		return _subClassList.getActiveSubClass();
	}

	public int getActiveClassId()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getClassId();

		return -1;
	}

	public int getActiveDefaultClassId()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getDefaultClassId();

		return -1;
	}

	public SubClass getDualClass()
	{
		return _subClassList.getDualClass();
	}

	public int getDualClassId()
	{
		if(getDualClass() != null)
			return getDualClass().getClassId();

		return -1;
	}

	public int getDualClassLevel()
	{
		if(getDualClass() != null)
			return getDualClass().getLevel();

		return 0;
	}

	public boolean isBaseClassActive()
	{
		return getActiveSubClass().isBase();
	}

	public boolean isDualClassActive()
	{
		return getActiveSubClass().isDual();
	}

	public ClassId getClassId()
	{
		return ClassId.VALUES[getActiveClassId()];
	}

	public int getMaxLevel()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getMaxLevel();

		return Experience.getMaxLevel();
	}

	
	private synchronized void changeClassInDb(final int oldclass, final int newclass, final int defaultClass)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_subclasses SET class_id=?, default_class_id=? WHERE char_obj_id=? AND class_id=?");
			statement.setInt(1, newclass);
			statement.setInt(2, defaultClass);
			statement.setInt(3, getObjectId());
			statement.setInt(4, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);
		}
		catch(final SQLException e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	
	public void storeCharSubClasses()
	{
		SubClass main = getActiveSubClass();
		if(main != null)
		{
			main.setCp(getCurrentCp());
			main.setHp(getCurrentHp());
			main.setMp(getCurrentMp());
		}
		else
			_log.warn("Could not store char sub data, main class " + getActiveClassId() + " not found for " + this);

		CharacterSubclassDAO.getInstance().store(this);
	}

	
	public boolean addSubClass(final int classId, boolean storeOld, int certification, int dualCertification, long exp, long sp)
	{
		return addSubClass(classId, storeOld, certification, dualCertification, SubClassType.SUBCLASS, exp, sp);
	}

	public boolean addSubClass(final int classId, boolean storeOld, int certification, int dualCertification, SubClassType type, long exp, long sp)
	{
		return addSubClass(-1, classId, storeOld, certification, dualCertification, type, exp, sp);
	}

	private boolean addSubClass(final int oldClassId, final int classId, boolean storeOld, int certification, int dualCertification, SubClassType type, long exp, long sp)
	{
		final ClassId newId = ClassId.VALUES[classId];
		if(newId.isDummy() || newId.isOfLevel(ClassLevel.NONE) || newId.isOfLevel(ClassLevel.FIRST))
			return false;

		final SubClass newClass = new SubClass(this);
		newClass.setType(type);
		newClass.setClassId(classId);
	    ClassId oldId = (oldClassId >= 0) ? ClassId.VALUES[oldClassId] : null;
	    newClass.setDefaultClassId(newId.getBaseAwakeParent(oldId).getId());
	    
		if(exp > 0L)
			newClass.setExp(exp, true);
		if(sp > 0)
			newClass.setSp(sp);
		
	    newClass.setCertification(certification);
	    newClass.setDualCertification(dualCertification);
	    
		if(!getSubClassList().add(newClass))
			return false;

		final int level = newClass.getLevel();
		final double hp = newId.getBaseHp(level);
		final double mp = newId.getBaseMp(level);
		final double cp = newId.getBaseCp(level);
		if(!CharacterSubclassDAO.getInstance().insert(getObjectId(), newClass.getClassId(), newClass.getDefaultClassId(), newClass.getExp(), newClass.getSp(), hp, mp, cp, hp, mp, cp, level, false, type, certification, dualCertification, MAX_VITALITY_POINTS, 0))
			return false;

		setActiveSubClass(classId, storeOld, false);

		rewardSkills(true, false, !newId.isOfLevel(ClassLevel.AWAKED), false);

		sendSkillList();
		
		rewardAlchemySkills(true);
		
		sendSkillList();
		setCurrentHpMp(getMaxHp(), getMaxMp(), true);
		setCurrentCp(getMaxCp());

		onReceiveNewClassId(oldId, newId);

		return true;
	}

	
	public boolean modifySubClass(final int oldClassId, final int newClassId, final boolean safeExpSp)
	{
		final SubClass originalClass = getSubClassList().getByClassId(oldClassId);
		if(originalClass == null || originalClass.isBase())
			return false;
		
		final int certification = originalClass.getCertification();
		final int dualCertification = originalClass.getDualCertification();
		final SubClassType type = originalClass.getType();
		long exp = 0L;
		long sp = 0;
		if(safeExpSp)
		{
			exp = originalClass.getExp();
			sp = originalClass.getSp();
		}

		TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(this);
		if(trainingCamp != null && trainingCamp.getClassIndex() == originalClass.getIndex())
			TrainingCampManager.getInstance().removeTrainingCamp(this);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			
			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND type != " + SubClassType.BASE_CLASS.ordinal());
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			
			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			
			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			
			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			
			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(final Exception e)
		{
			_log.warn("Could not delete char sub-class: " + e);
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		getSubClassList().removeByClassId(oldClassId);

		return newClassId <= 0 || addSubClass(oldClassId, newClassId, false, certification, dualCertification, type, exp, sp);
	}

	public void setActiveSubClass(final int subId, final boolean store, final boolean onRestore)
	{
		if(!onRestore)
		{
			SubClass oldActiveSub = getActiveSubClass();
			if(oldActiveSub != null)
			{
				storeDisableSkills();

				QuestState qs = getQuestState(422);
	            if(qs != null)
	                qs.abortQuest();

				if(store)
				{
					oldActiveSub.setCp(getCurrentCp());
					oldActiveSub.setHp(getCurrentHp());
					oldActiveSub.setMp(getCurrentMp());
				}
			}
		}

		SubClass newActiveSub = _subClassList.changeActiveSubClass(subId);

		setClassId(subId, false);

		sendPacket(new ExSubjobInfo(this, false));
		
		removeAllSkills();

		getAbnormalList().stopAll();
		deleteCubics();

		for(Servitor servitor : getServitors())
		{
			if(servitor != null && (servitor.isSummon() || (Config.ALT_IMPROVED_PETS_LIMITED_USE && ((servitor.getNpcId() == PetDataHolder.IMPROVED_BABY_KOOKABURRA_ID && !isMageClass() || servitor.getNpcId() == PetDataHolder.IMPROVED_BABY_BUFFALO_ID && isMageClass())))))
				servitor.unSummon(false);
		}

		restoreSkills();
		restoreAlchemySkills();
		rewardSkills(false);
		rewardAlchemySkills(true);

		checkSkills();

		refreshExpertisePenalty();

		getInventory().refreshEquip();
		getInventory().validateItems();

		getHennaList().restore();

		getDailyMissionList().restore();

		EffectsDAO.getInstance().restoreEffects(this);
		restoreDisableSkills();

		setCurrentHpMp(newActiveSub.getHp(), newActiveSub.getMp());
		setCurrentCp(newActiveSub.getCp());

		_shortCuts.restore();
		sendPacket(new ShortCutInitPacket(this));
		sendActiveAutoShots();

		broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

		setIncreasedForce(0);

		startHourlyTask();

		sendSkillList();

		broadcastCharInfo();
		updateAbnormalIcons();
		updateStats();
	}

	
	public void startKickTask(long delayMillis)
	{
		stopKickTask();
		_kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delayMillis);
	}

	public void stopKickTask()
	{
		if(_kickTask != null)
		{
			_kickTask.cancel(false);
			_kickTask = null;
		}
	}

	public boolean givePremiumAccount(PremiumAccountTemplate premiumAccount, int delay)
	{
		if(getNetConnection() == null)
			return false;

		int type = premiumAccount.getType();
		if(type == 0)
			return false;

		int expireTime = (delay > 0) ? (int) ((delay * 60 * 60) + (System.currentTimeMillis() / 1000)) : Integer.MAX_VALUE;
		boolean extended = false;
		int oldAccountType = getNetConnection().getPremiumAccountType();
		int oldAccountExpire = getNetConnection().getPremiumAccountExpire();
		if(oldAccountType == type && oldAccountExpire > (System.currentTimeMillis() / 1000))
		{
			expireTime += (int) (oldAccountExpire - (System.currentTimeMillis() / 1000));
			extended = true;
		}

		if(Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER)
			PremiumAccountDAO.getInstance().insert(getAccountName(), type, expireTime);
		else
		{
			if(AuthServerCommunication.getInstance().isShutdown())
				return false;

			AuthServerCommunication.getInstance().sendPacket(new BonusRequest(getAccountName(), type, expireTime));
		}

		getNetConnection().setPremiumAccountType(type);
		getNetConnection().setPremiumAccountExpire(expireTime);

		if(startPremiumAccountTask())
		{
			if(!extended)
			{
				if(getParty() != null)
					getParty().recalculatePartyData();

				getAttendanceRewards().onReceivePremiumAccount();
				sendPacket(new ExBR_PremiumStatePacket(this, hasPremiumAccount()));
			}
			return true;
		}
		return false;
	}

	public boolean removePremiumAccount()
	{
		PremiumAccountTemplate oldPremiumAccount = getPremiumAccount();
		if(oldPremiumAccount.getType() == 0)
			return false;

		double currentHpRatio = getCurrentHpRatio();
		double currentMpRatio = getCurrentMpRatio();
		double currentCpRatio = getCurrentCpRatio();

		removeStatsOwner(oldPremiumAccount);
		removeTriggers(oldPremiumAccount);

		SkillEntry[] skills = _premiumAccount.getAttachedSkills();
		for(SkillEntry skill : skills)
			removeSkill(skill);

		if(skills.length > 0)
			sendSkillList();

		setCurrentHp(getMaxHp() * currentHpRatio, false);
		setCurrentMp(getMaxMp() * currentMpRatio);
		setCurrentCp(getMaxCp() * currentCpRatio);

		updateStats();

		_premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(0);

		if(getParty() != null)
			getParty().recalculatePartyData();

		if(Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER)
			PremiumAccountDAO.getInstance().delete(getAccountName());
		else
			AuthServerCommunication.getInstance().sendPacket(new BonusRequest(getAccountName(), 0, 0));

		if(getNetConnection() != null)
		{
			getNetConnection().setPremiumAccountType(0);
			getNetConnection().setPremiumAccountExpire(0);
		}

		stopPremiumAccountTask();
		removePremiumAccountItems(true);
		sendPacket(new ExBR_PremiumStatePacket(this, hasPremiumAccount()));
		getAttendanceRewards().onRemovePremiumAccount();
		return true;
	}

	private boolean tryGiveFreePremiumAccount()
	{
		if(Config.FREE_PA_TYPE == 0 || Config.FREE_PA_DELAY <= 0)
			return false;

		PremiumAccountTemplate premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(Config.FREE_PA_TYPE);
		if(premiumAccount == null)
			return false;

		boolean recieved = Boolean.parseBoolean(AccountVariablesDAO.getInstance().select(getAccountName(), FREE_PA_RECIEVED, "false"));
		if(recieved)
			return false;

		if(givePremiumAccount(premiumAccount, Config.FREE_PA_DELAY))
		{
			AccountVariablesDAO.getInstance().insert(getAccountName(), FREE_PA_RECIEVED, "true");
			if(Config.ENABLE_FREE_PA_NOTIFICATION)
			{
				CustomMessage message = null;
				int accountExpire = getNetConnection().getPremiumAccountExpire();
				if(accountExpire != Integer.MAX_VALUE)
				{
					message = new CustomMessage("l2s.gameserver.model.Player.GiveFreePA");
					message.addString(TimeUtils.toSimpleFormat(accountExpire * 1000L));
				}
				else
					message = new CustomMessage("l2s.gameserver.model.Player.GiveUnlimFreePA");

				sendPacket(new ExShowScreenMessage(message.toString(this), 15000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
			return true;
		}
		return false;
	}

	private boolean startPremiumAccountTask()
	{
		if(!Config.PREMIUM_ACCOUNT_ENABLED)
			return false;

		stopPremiumAccountTask();

		if(getNetConnection() == null)
			return false;

		int accountType = getNetConnection().getPremiumAccountType();

		PremiumAccountTemplate premiumAccount = accountType == 0 ? null : PremiumAccountHolder.getInstance().getPremiumAccount(accountType);
		if(premiumAccount != null)
		{
			int accountExpire = getNetConnection().getPremiumAccountExpire();
			if(accountExpire > System.currentTimeMillis() / 1000L)
			{
				_premiumAccount = premiumAccount;

				double currentHpRatio = getCurrentHpRatio();
				double currentMpRatio = getCurrentMpRatio();
				double currentCpRatio = getCurrentCpRatio();

				addTriggers(_premiumAccount);
				addStatFuncs(_premiumAccount.getStatFuncs());

				SkillEntry[] skills = _premiumAccount.getAttachedSkills();
				for(SkillEntry skill : skills)
					addSkill(skill);

				if(skills.length > 0)
					sendSkillList();

				setCurrentHp(getMaxHp() * currentHpRatio, false);
				setCurrentMp(getMaxMp() * currentMpRatio);
				setCurrentCp(getMaxCp() * currentCpRatio);

				updateStats();

				int itemsReceivedType = getVarInt(PA_ITEMS_RECIEVED);
				if(itemsReceivedType != premiumAccount.getType())
				{
					removePremiumAccountItems(false);
					ItemData[] items = premiumAccount.getGiveItemsOnStart();
					if(items.length > 0)
					{
						if(!isInventoryFull())
						{
							sendPacket(SystemMsg.THE_PREMIUM_ITEM_FOR_THIS_ACCOUNT_WAS_PROVIDED_IF_THE_PREMIUM_ACCOUNT_IS_TERMINATED_THIS_ITEM_WILL_BE_DELETED);
							for(ItemData item : items)
								ItemFunctions.addItem(this, item.getId(), item.getCount(), true);

							setVar(PA_ITEMS_RECIEVED, accountType);
						}
						else
							sendPacket(SystemMsg.THE_PREMIUM_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
					}
				}
				if(accountExpire != Integer.MAX_VALUE)
					_premiumAccountExpirationTask = LazyPrecisionTaskManager.getInstance().startPremiumAccountExpirationTask(this, accountExpire);

				return true;
			}
			if(!Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER)
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(getAccountName(), 0, 0));
		}

		removePremiumAccountItems(true);
		if(tryGiveFreePremiumAccount())
			return false;

		if(Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER)
			PremiumAccountDAO.getInstance().delete(getAccountName());

		if(getNetConnection() != null)
		{
			getNetConnection().setPremiumAccountType(0);
			getNetConnection().setPremiumAccountExpire(0);
		}

		return false;
	}

	private void stopPremiumAccountTask()
	{
		if(_premiumAccountExpirationTask != null)
		{
			_premiumAccountExpirationTask.cancel(false);
			_premiumAccountExpirationTask = null;
		}
	}

	private void removePremiumAccountItems(boolean notify)
	{
		PremiumAccountTemplate premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(getVarInt(PA_ITEMS_RECIEVED));
		if(premiumAccount != null)
		{
			ItemData[] items = premiumAccount.getTakeItemsOnEnd();
			if(items.length > 0)
			{
				if(notify)
					sendPacket(SystemMsg.THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED_THE_PROVIDED_PREMIUM_ITEM_WAS_DELETED);

				for(ItemData item : items)
					ItemFunctions.deleteItem(this, item.getId(), item.getCount(), notify);

				for(ItemData item : items)
					ItemFunctions.deleteItemsEverywhere(this, item.getId());
			}
		}

		unsetVar(PA_ITEMS_RECIEVED);
	}

	@Override
	public int getInventoryLimit()
	{
		return (int) calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
	}

	public int getWarehouseLimit()
	{
		return (int) calcStat(Stats.STORAGE_LIMIT, 0, null, null);
	}

	public int getTradeLimit()
	{
		return (int) calcStat(Stats.TRADE_LIMIT, 0, null, null);
	}

	public int getDwarvenRecipeLimit()
	{
		return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
	}

	public int getCommonRecipeLimit()
	{
		return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
	}

	public boolean getAndSetLastItemAuctionRequest()
	{
		if(_lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis())
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return true;
		}
		else
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return false;
		}
	}

	@Override
	public int getNpcId()
	{
		return -2;
	}

	public GameObject getVisibleObject(int id)
	{
		if(getObjectId() == id)
			return this;

		GameObject target = null;

		if(getTargetId() == id)
			target = getTarget();

		if(target == null && isInParty())
			for(Player p : _party.getPartyMembers())
				if(p != null && p.getObjectId() == id)
				{
					target = p;
					break;
				}

		if(target == null)
			target = World.getAroundObjectById(this, id);

		return target == null || target.isInvisible(this) ? null : target;
	}

	@Override
	public String getTitle()
	{
		return super.getTitle();
	}

	public int getTitleColor()
	{
		return _titlecolor;
	}

	public void setTitleColor(final int titlecolor)
	{
		if(titlecolor != DEFAULT_TITLE_COLOR)
			setVar("titlecolor", Integer.toHexString(titlecolor), -1);
		else
			unsetVar("titlecolor");
		_titlecolor = titlecolor;
	}

	@Override
	public final boolean isCursedWeaponEquipped()
	{
		return _cursedWeaponEquippedId != 0;
	}

	public final void setCursedWeaponEquippedId(int value)
	{
		_cursedWeaponEquippedId = value;
	}

	public final int getCursedWeaponEquippedId()
	{
		return _cursedWeaponEquippedId;
	}

	public final String getCursedWeaponName(Player activeChar)
	{
		if(isCursedWeaponEquipped())
			return new CustomMessage("cursed_weapon_name." + _cursedWeaponEquippedId).toString(activeChar);
		return null;
	}

	@Override
	public boolean isImmobilized()
	{
		return super.isImmobilized() || isOverloaded() || isSitting() || isFishing() || isInTrainingCamp();
	}

	@Override
	public boolean isBlocked()
	{
		return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted() || isInTrainingCamp();
	}

	@Override
	public boolean isInvulnerable()
	{
		return super.isInvulnerable() || isInMovie() || isInTrainingCamp();
	}

	
	public void setOverloaded(boolean overloaded)
	{
		_overloaded = overloaded;
	}

	public boolean isOverloaded()
	{
		return _overloaded;
	}

	public boolean isFishing()
	{
		return _fishing.inStarted();
	}

	public Fishing getFishing()
	{
		return _fishing;
	}

	public PremiumAccountTemplate getPremiumAccount()
	{
		return _premiumAccount;
	}

	public boolean hasPremiumAccount()
	{
		return _premiumAccount.getType() > 0;
	}

	public int getPremiumAccountLeftTime()
	{
		if(hasPremiumAccount())
		{
			GameClient client = this.getNetConnection();
			if(client != null)
				return (int) Math.max(0, client.getPremiumAccountExpire() - System.currentTimeMillis() / 1000L);
		}
		return 0;
	}

	public double getRateAdena()
	{
		double rate = Config.RATE_DROP_ADENA_BY_LVL[getLevel()];
		rate *= isInParty() ? _party._rateAdena : getPremiumAccount().getRates().getAdena();
		rate *= 1. + calcStat(Stats.ADENA_RATE_MULTIPLIER, 0, null, null);
		return rate;
	}

	public double getRateItems()
	{
		double rate = Config.RATE_DROP_ITEMS_BY_LVL[getLevel()];
		rate *= isInParty() ? _party._rateDrop : getPremiumAccount().getRates().getDrop();
		rate *= 1. + calcStat(Stats.DROP_RATE_MULTIPLIER, 0, null, null);
		return rate;
	}

	public double getRateExp()
	{
		final double baseRate = Config.RATE_XP_BY_LVL[getLevel()] * (isInParty() ? _party._rateExp : getPremiumAccount().getRates().getExp());
		double rate = baseRate;
		rate += baseRate * (getVitalityBonus() - 1);
		rate += baseRate * calcStat(Stats.EXP_RATE_MULTIPLIER, 0, null, null);
		return rate;
	}

	public double getRateSp()
	{
		final double baseRate = Config.RATE_SP_BY_LVL[getLevel()] * (isInParty() ? _party._rateSp : getPremiumAccount().getRates().getSp());
		double rate = baseRate;
		rate += baseRate * (getVitalityBonus() - 1);
		rate += baseRate * calcStat(Stats.SP_RATE_MULTIPLIER, 0, null, null);
		return rate;
	}

	public double getRateSpoil()
	{
		double rate = Config.RATE_DROP_SPOIL_BY_LVL[getLevel()];
		rate *= isInParty() ? _party._rateSpoil : getPremiumAccount().getRates().getSpoil();
		rate *= 1. + calcStat(Stats.SPOIL_RATE_MULTIPLIER, 0, null, null);
		return rate;
	}

	public double getRateQuestsDrop()
	{
		double rate = Config.RATE_QUESTS_DROP;
		rate *= getPremiumAccount().getRates().getQuestDrop();
		return rate;
	}

	public double getRateQuestsReward()
	{
		double rate = Config.RATE_QUESTS_REWARD;
		rate *= getPremiumAccount().getRates().getQuestReward();
		return rate;
	}

	public double getDropChanceMod()
	{
		double mod = Config.DROP_CHANCE_MODIFIER;
		mod *= isInParty() ? _party._dropChanceMod : getPremiumAccount().getModifiers().getDropChance();
		mod *= 1. + calcStat(Stats.DROP_CHANCE_MODIFIER, 0, null, null);
		return mod;
	}

	public double getSpoilChanceMod()
	{
		double mod = Config.SPOIL_CHANCE_MODIFIER;
		mod *= isInParty() ? _party._spoilChanceMod : getPremiumAccount().getModifiers().getSpoilChance();
		mod *= 1. + calcStat(Stats.SPOIL_CHANCE_MODIFIER, 0, null, null);
		return mod;
	}

	private boolean _maried = false;
	private int _partnerId = 0;
	private int _coupleId = 0;
	private boolean _maryrequest = false;
	private boolean _maryaccepted = false;

	public boolean isMaried()
	{
		return _maried;
	}

	public void setMaried(boolean state)
	{
		_maried = state;
	}

	public void setMaryRequest(boolean state)
	{
		_maryrequest = state;
	}

	public boolean isMaryRequest()
	{
		return _maryrequest;
	}

	public void setMaryAccepted(boolean state)
	{
		_maryaccepted = state;
	}

	public boolean isMaryAccepted()
	{
		return _maryaccepted;
	}

	public int getPartnerId()
	{
		return _partnerId;
	}

	public void setPartnerId(int partnerid)
	{
		_partnerId = partnerid;
	}

	public int getCoupleId()
	{
		return _coupleId;
	}

	public void setCoupleId(int coupleId)
	{
		_coupleId = coupleId;
	}

	private OnPlayerChatMessageReceive _snoopListener = null;
	private List<Player> _snoopListenerPlayers = new ArrayList<Player>();

	private class SnoopListener implements OnPlayerChatMessageReceive
	{
		@Override
		public void onChatMessageReceive(Player player, ChatType type, String charName, String text)
		{
			if(_snoopListenerPlayers.size() > 0)
			{
				SnoopPacket sn = new SnoopPacket(getObjectId(), getName(), type.ordinal(), charName, text);
				for(Player pci : _snoopListenerPlayers)
				{
					if(pci != null)
						pci.sendPacket(sn);
				}
			}
		}
	}

	public void addSnooper(Player pci)
	{
		if(!_snoopListenerPlayers.contains(pci))
			_snoopListenerPlayers.add(pci);

		if(!_snoopListenerPlayers.isEmpty() && _snoopListener == null)
			addListener(_snoopListener = new SnoopListener());
	}

	public void removeSnooper(Player pci)
	{
		_snoopListenerPlayers.remove(pci);
		if(_snoopListenerPlayers.isEmpty() && _snoopListener != null)
		{
			removeListener(_snoopListener);
			_snoopListener = null;
		}
	}

	
	public void resetReuse()
	{
		_skillReuses.clear();
		_sharedGroupReuses.clear();
	}

	public DeathPenalty getDeathPenalty()
	{
		return _deathPenalty;
	}

	private boolean _charmOfCourage = false;

	public boolean isCharmOfCourage()
	{
		return _charmOfCourage;
	}

	public void setCharmOfCourage(boolean val)
	{
		_charmOfCourage = val;

		sendEtcStatusUpdate();
	}

	private int _increasedForce = 0;
	private int _consumedSouls = 0;

	@Override
	public int getIncreasedForce()
	{
		return _increasedForce;
	}

	@Override
	public int getConsumedSouls()
	{
		return _consumedSouls;
	}

	@Override
	public void setConsumedSouls(int i, NpcInstance monster)
	{
		if(i == _consumedSouls)
			return;

		int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

		if(i > max)
			i = max;

		if(i <= 0)
		{
			_consumedSouls = 0;
			sendEtcStatusUpdate();
			return;
		}

		if(_consumedSouls != i)
		{
			int diff = i - _consumedSouls;
			if(diff > 0)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
				sm.addNumber(diff);
				sm.addNumber(i);
				sendPacket(sm);
			}
		}
		else if(max == i)
		{
			sendPacket(SystemMsg.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
			return;
		}

		_consumedSouls = i;
		sendPacket(new EtcStatusUpdatePacket(this));
	}

	@Override
	public void setIncreasedForce(int i)
	{
		i = Math.min(i, getMaxIncreasedForce());
		i = Math.max(i, 0);

		if(i != 0 && i > _increasedForce)
			sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));

		_increasedForce = i;
		sendEtcStatusUpdate();
	}

	
	private long _lastFalling;

    public boolean isFalling()
    {
        return System.currentTimeMillis() - _lastFalling < 5000L;
    }
    
    public void falling(int height)
    {
        if(!Config.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat())
            return;

        switch(getJumpState()) 
        {
            case IN_PROGRESS:
            case FINISHED:
                setJumpState(JumpState.NONE);
        }
        
        _lastFalling = System.currentTimeMillis();
        int damage = (int) calcStat(Stats.FALL, getMaxHp() / 2000.0 * height, null, null);
        if(damage > 0)
        {
            int curHp = (int) getCurrentHp();
            if(curHp - damage < 1)
                setCurrentHp(1.0, false);
            else
                setCurrentHp(curHp - damage, false);

            sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
        }
    }
    
	
	@Override
	public void checkHpMessages(double curHp, double newHp)
	{
		
		int[] _hp = { 30, 30 };
		int[] skills = { 290, 291 };

		
		int[] _effects_skills_id = { 139, 176, 292, 292, 420 };
		int[] _effects_hp = { 30, 30, 30, 60, 30 };

		double percent = getMaxHp() / 100;
		double _curHpPercent = curHp / percent;
		double _newHpPercent = newHp / percent;
		boolean needsUpdate = false;

		
		for(int i = 0; i < skills.length; i++)
		{
			int level = getSkillLevel(skills[i]);
			if(level > 0)
				if(_curHpPercent > _hp[i] && _newHpPercent <= _hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
					needsUpdate = true;
				}
				else if(_curHpPercent <= _hp[i] && _newHpPercent > _hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
					needsUpdate = true;
				}
		}

		
		for(Integer i = 0; i < _effects_skills_id.length; i++)
			if(getAbnormalList().contains(_effects_skills_id[i]))
				if(_curHpPercent > _effects_hp[i] && _newHpPercent <= _effects_hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}
				else if(_curHpPercent <= _effects_hp[i] && _newHpPercent > _effects_hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}

		if(needsUpdate)
			sendChanges();
	}

	
	public void checkDayNightMessages()
	{
		int level = getSkillLevel(294);
		if(level > 0)
			if(GameTimeController.getInstance().isNowNight())
				sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
			else
				sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
		sendChanges();
	}

	public int getZoneMask()
	{
		return _zoneMask;
	}

	
	@Override
	protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
	{
		super.onUpdateZones(leaving, entering);

		if((leaving == null || leaving.isEmpty()) && (entering == null || entering.isEmpty()))
			return;

		boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
		boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
		boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
		boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
		

		boolean isInCombatZone = isInZoneBattle();
		boolean isInDangerArea = isInDangerArea() || isInZone(ZoneType.CHANGED_ZONE);
		boolean isOnSiegeField = isInSiegeZone();
		boolean isInPeaceZone = isInPeaceZone();
		boolean isInSSQZone = isInSSQZone();

		
		int lastZoneMask = _zoneMask;
		_zoneMask = 0;

		if(isInCombatZone)
			_zoneMask |= ZONE_PVP_FLAG;
		if(isInDangerArea)
			_zoneMask |= ZONE_ALTERED_FLAG;
		if(isOnSiegeField)
			_zoneMask |= ZONE_SIEGE_FLAG;
		if(isInPeaceZone)
			_zoneMask |= ZONE_PEACE_FLAG;
		if(isInSSQZone)
			_zoneMask |= ZONE_SSQ_FLAG;

		if(lastZoneMask != _zoneMask)
			sendPacket(new ExSetCompassZoneCode(this));
		boolean broadcastRelation = false;
		if(lastInCombatZone != isInCombatZone)
			broadcastRelation = true;

		if(lastInDangerArea != isInDangerArea)
			sendPacket(new EtcStatusUpdatePacket(this));

		if(lastOnSiegeField != isOnSiegeField)
		{
			broadcastRelation = true;
			if(isOnSiegeField)
				sendPacket(SystemMsg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
			else
			{
				
				
				FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
				if(attachment != null)
					attachment.onLeaveSiegeZone(this);

				sendPacket(SystemMsg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
				if(!isTeleporting() && getPvpFlag() == 0)
					startPvPFlag(null);
			}
		}

		if(broadcastRelation)
			broadcastRelation();

		if(isInWater())
			startWaterTask();
		else
			stopWaterTask();
	}

	public void startAutoSaveTask()
	{
		if(!Config.AUTOSAVE)
			return;
		if(_autoSaveTask == null)
			_autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
	}

	public void stopAutoSaveTask()
	{
		if(_autoSaveTask != null)
			_autoSaveTask.cancel(false);
		_autoSaveTask = null;
	}

	public void startPcBangPointsTask()
	{
		if(!Config.ALT_PCBANG_POINTS_ENABLED || Config.ALT_PCBANG_POINTS_DELAY <= 0)
			return;
		if(_pcCafePointsTask == null)
			_pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
	}

	public void stopPcBangPointsTask()
	{
		if(_pcCafePointsTask != null)
			_pcCafePointsTask.cancel(false);
		_pcCafePointsTask = null;
	}

	public void startUnjailTask(Player player, int time)
	{
		if(_unjailTask != null)
			_unjailTask.cancel(false);
		_unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), time * 60000);
	}

	public void stopUnjailTask()
	{
		if(_unjailTask != null)
			_unjailTask.cancel(false);
		_unjailTask = null;
	}

	public void startTrainingCampTask(long timeRemaining)
	{
		if(_trainingCampTask == null)
			_trainingCampTask = ThreadPoolManager.getInstance().schedule(() -> TrainingCampManager.getInstance().onExitTrainingCamp(this), timeRemaining);
	}

	public void stopTrainingCampTask()
	{
		if(_trainingCampTask != null)
		{
			_trainingCampTask.cancel(false);
			_trainingCampTask = null;
		}
	}

	public boolean isInTrainingCamp()
	{
		return _trainingCampTask != null;
	}

	@Override
	public void sendMessage(String message)
	{
		sendPacket(new SystemMessage(message));
	}

	private Location _lastClientPosition;
	private Location _lastServerPosition;

	public void setLastClientPosition(Location position)
	{
		_lastClientPosition = position;
	}

	public Location getLastClientPosition()
	{
		return _lastClientPosition;
	}

	public void setLastServerPosition(Location position)
	{
		_lastServerPosition = position;
	}

	public Location getLastServerPosition()
	{
		return _lastServerPosition;
	}

	private int _useSeed = 0;

	public void setUseSeed(int id)
	{
		_useSeed = id;
	}

	public int getUseSeed()
	{
		return _useSeed;
	}

	@Override
	public int getRelation(Player target)
	{
		int result = 0;

		if(getClan() != null)
		{
			result |= RelationChangedPacket.RELATION_CLAN_MEMBER;
			if(getClan() == target.getClan())
				result |= RelationChangedPacket.RELATION_CLAN_MATE;
			if(getClan().getAllyId() != 0)
				result |= RelationChangedPacket.RELATION_ALLY_MEMBER;
		}

		if(isClanLeader())
			result |= RelationChangedPacket.RELATION_LEADER;

		Party party = getParty();
		if(party != null && party == target.getParty())
		{
			result |= RelationChangedPacket.RELATION_HAS_PARTY;

			switch(party.getPartyMembers().indexOf(this))
			{
				case 0:
					result |= RelationChangedPacket.RELATION_PARTYLEADER; 
					break;
				case 1:
					result |= RelationChangedPacket.RELATION_PARTY4; 
					break;
				case 2:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 3:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2; 
					break;
				case 4:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 5:
					result |= RelationChangedPacket.RELATION_PARTY3; 
					break;
				case 6:
					result |= RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; 
					break;
				case 7:
					result |= RelationChangedPacket.RELATION_PARTY2; 
					break;
				case 8:
					result |= RelationChangedPacket.RELATION_PARTY1; 
					break;
			}
		}

		Clan clan1 = getClan();
		Clan clan2 = target.getClan();
		if(clan1 != null && clan2 != null)
		{
			if((target.getPledgeType() != Clan.SUBUNIT_ACADEMY || target.getLevel() >= 70) && (getPledgeType() != Clan.SUBUNIT_ACADEMY || getLevel() >= 70))
				if(clan2.isAtWarWith(clan1.getClanId()))
				{
					result |= RelationChangedPacket.RELATION_1SIDED_WAR;
					if(clan1.isAtWarWith(clan2.getClanId()))
						result |= RelationChangedPacket.RELATION_MUTUAL_WAR;
				}
			if(getBlockCheckerArena() != -1)
			{
				result |= RelationChangedPacket.RELATION_IN_SIEGE;
				ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
				if(holder.getPlayerTeam(this) == 0)
					result |= RelationChangedPacket.RELATION_ENEMY;
				else
					result |= RelationChangedPacket.RELATION_ALLY;
				result |= RelationChangedPacket.RELATION_ATTACKER;
			}
		}

		for(Event e : getEvents())
			result = e.getRelation(this, target, result);

		return result;
	}

	
	protected int _pvpFlag;

	private Future<?> _PvPRegTask;
	private long _lastPvPAttack;

	public long getLastPvPAttack()
	{
		return isVioletBoy() ? System.currentTimeMillis() : _lastPvPAttack;
	}

	public void setLastPvPAttack(long time)
	{
		_lastPvPAttack = time;
	}

	@Override
	public void startPvPFlag(Creature target)
	{
		if(isPK() || isVioletBoy())
			return;

		long startTime = System.currentTimeMillis();
		if(target != null && target.getPvpFlag() != 0)
			startTime -= Config.PVP_TIME / 2;
		if(getPvpFlag() != 0 && getLastPvPAttack() >= startTime)
			return;

		_lastPvPAttack = startTime;

		updatePvPFlag(1);

		if(_PvPRegTask == null)
			_PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
	}

	public void stopPvPFlag()
	{
		if(_PvPRegTask != null)
		{
			_PvPRegTask.cancel(false);
			_PvPRegTask = null;
		}
		updatePvPFlag(0);
	}

	public void updatePvPFlag(int value)
	{
		if(_handysBlockCheckerEventArena != -1)
			return;
		if(getPvpFlag() == value)
			return;

		setPvpFlag(value);

		sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);

		broadcastRelation();
	}

	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = pvpFlag;
	}

	@Override
	public int getPvpFlag()
	{
		return isVioletBoy() ? 1 : _pvpFlag;
	}

	public boolean isInDuel()
	{
		return getEvent(DuelEvent.class) != null;
	}

	private Map<Integer, TamedBeastInstance> _tamedBeasts = new ConcurrentHashMap<Integer, TamedBeastInstance>();
	
    public Map<Integer, TamedBeastInstance> getTrainedBeasts()
    {
        return _tamedBeasts;
    }
    
    public void addTrainedBeast(TamedBeastInstance tamedBeast)
    {
        _tamedBeasts.put(tamedBeast.getObjectId(), tamedBeast);
    }
    
    public void removeTrainedBeast(int npcId)
    {
        _tamedBeasts.remove(npcId);
    }
    
	private long _lastAttackPacket = 0;

	public long getLastAttackPacket()
	{
		return _lastAttackPacket;
	}

	public void setLastAttackPacket()
	{
		_lastAttackPacket = System.currentTimeMillis();
	}

	private long _lastMovePacket = 0;

	public long getLastMovePacket()
	{
		return _lastMovePacket;
	}

	public void setLastMovePacket()
	{
		_lastMovePacket = System.currentTimeMillis();
	}

	public byte[] getKeyBindings()
	{
		return _keyBindings;
	}

	public void setKeyBindings(byte[] keyBindings)
	{
		if(keyBindings == null)
			keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
		_keyBindings = keyBindings;
	}

	
	@Override
	public final Collection<SkillEntry> getAllSkills()
	{
		
		if(!isTransformed())
			return super.getAllSkills();

		
		IntObjectMap<SkillEntry> temp = new HashIntObjectMap<SkillEntry>();
		for(SkillEntry skillEntry : super.getAllSkills())
		{
			Skill skill = skillEntry.getTemplate();
			if(!skill.isActive() && !skill.isToggle())
				temp.put(skillEntry.getId(), skillEntry);
		}

		temp.putAll(_transformSkills); 
		return temp.values();
	}

	public final void addTransformSkill(SkillEntry skillEntry)
	{
		_transformSkills.put(skillEntry.getId(), skillEntry);
	}

	public final void removeTransformSkill(SkillEntry skillEntry)
	{
		_transformSkills.remove(skillEntry.getId());
	}

	public void setAgathion(int id)
	{
		if(_agathionId == id)
			return;

		_agathionId = id;

		sendPacket(new ExUserInfoCubic(this));
		broadcastCharInfo();
	}

	public int getAgathionId()
	{
		return _agathionId;
	}

	
	public int getPcBangPoints()
	{
		return _pcBangPoints;
	}

	
	public void setPcBangPoints(int val)
	{
		_pcBangPoints = val;
	}

	public void addPcBangPoints(int count, boolean doublePoints, boolean notify)
	{
		if(doublePoints)
			count *= 2;

		_pcBangPoints += count;

		if(count > 0 && notify)
			sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfoPacket(this, count, 1, 2, 12));
	}

	public boolean reducePcBangPoints(int count)
	{
		if(_pcBangPoints < count)
			return false;

		_pcBangPoints -= count;
		sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfoPacket(this, 0, 1, 2, 12));
		return true;
	}

	private Location _groundSkillLoc;

	public void setGroundSkillLoc(Location location)
	{
		_groundSkillLoc = location;
	}

	public Location getGroundSkillLoc()
	{
		return _groundSkillLoc;
	}

	
	public boolean isLogoutStarted()
	{
		if(_isLogout == null)
			return false;

		return _isLogout.get();
	}

	public void setOfflineMode(boolean val)
	{
		if(!val)
			unsetVar("offline");
		_offline = val;
	}

	public boolean isInOfflineMode()
	{
		return _offline;
	}

	public void storePrivateStore()
	{
		int storeType = getPrivateStoreType();
		if(storeType == 0)
			unsetVar("storemode");
		else if(Config.ALT_SAVE_PRIVATE_STORE || isInOfflineMode())
			setVar("storemode", storeType);

		if(_sellList != null && !_sellList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE || (isInOfflineMode() && storeType == STORE_PRIVATE_SELL)))
		{
			StringBuilder items = new StringBuilder();

			for(TradeItem i : _sellList)
			{
				items.append(i.getObjectId());
				items.append(";");
				items.append(i.getCount());
				items.append(";");
				items.append(i.getOwnersPrice());
				items.append(":");
			}
			setVar("selllist", items.toString(), -1);
			String title = getSellStoreName();
			if(title != null && !title.isEmpty())
				setVar("sellstorename", title, -1);
			else
				unsetVar("sellstorename");
		}
		else
		{
			unsetVar("selllist");
			unsetVar("sellstorename");
		}

		if(_packageSellList != null && !_packageSellList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE || (isInOfflineMode() && storeType == STORE_PRIVATE_SELL_PACKAGE)))
		{
			StringBuilder items = new StringBuilder();
			for(TradeItem i : _packageSellList)
			{
				items.append(i.getObjectId());
				items.append(";");
				items.append(i.getCount());
				items.append(";");
				items.append(i.getOwnersPrice());
				items.append(":");
			}
			setVar("packageselllist", items.toString(), -1);
			String title = getPackageSellStoreName();
			if(title != null && !title.isEmpty())
				setVar("packagesellstorename", title, -1);
			else
				unsetVar("packagesellstorename");
		}
		else
		{
			unsetVar("packageselllist");
			unsetVar("packagesellstorename");
		}

		if(_buyList != null && !_buyList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE || (isInOfflineMode() && storeType == STORE_PRIVATE_BUY)))
		{
			StringBuilder items = new StringBuilder();
			for(TradeItem i : _buyList)
			{
				items.append(i.getItemId());
				items.append(";");
				items.append(i.getCount());
				items.append(";");
				items.append(i.getOwnersPrice());
				items.append(";");
				items.append(i.getEnchantLevel());
				items.append(":");
			}
			setVar("buylist", items.toString(), -1);
			String title = getBuyStoreName();
			if(title != null && !title.isEmpty())
				setVar("buystorename", title, -1);
			else
				unsetVar("buystorename");
		}
		else
		{
			unsetVar("buylist");
			unsetVar("buystorename");
		}

		if(_createList != null && !_createList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE || (isInOfflineMode() && storeType == STORE_PRIVATE_MANUFACTURE)))
		{
			StringBuilder items = new StringBuilder();
			for(ManufactureItem i : _createList)
			{
				items.append(i.getRecipeId());
				items.append(";");
				items.append(i.getCost());
				items.append(":");
			}
			setVar("createlist", items.toString(), -1);
			String title = getManufactureName();
			if(title != null && !title.isEmpty())
				setVar("manufacturename", title, -1);
			else
				unsetVar("manufacturename");
		}
		else
		{
			unsetVar("createlist");
			unsetVar("manufacturename");
		}
	}

	public void restorePrivateStore()
	{
		String var;
		var = getVar("selllist");
		if(var != null)
		{
			_sellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if(count < 1 || itemToSell == null)
					continue;

				if(count > itemToSell.getCount())
					count = itemToSell.getCount();

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_sellList.add(i);
			}
			var = getVar("sellstorename");
			if(var != null)
				setSellStoreName(var);
		}
		var = getVar("packageselllist");
		if(var != null)
		{
			_packageSellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if(count < 1 || itemToSell == null)
					continue;

				if(count > itemToSell.getCount())
					count = itemToSell.getCount();

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_packageSellList.add(i);
			}
			var = getVar("packagesellstorename");
			if(var != null)
				setPackageSellStoreName(var);
		}
		var = getVar("buylist");
		if(var != null)
		{
			_buyList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;
				TradeItem i = new TradeItem();
				i.setItemId(Integer.parseInt(values[0]));
				i.setCount(Long.parseLong(values[1]));
				i.setOwnersPrice(Long.parseLong(values[2]));
				if(values.length > 3)
					i.setEnchantLevel(Integer.parseInt(values[3]));

				_buyList.add(i);
			}
			var = getVar("buystorename");
			if(var != null)
				setBuyStoreName(var);
		}
		var = getVar("createlist");
		if(var != null)
		{
			_createList = new CopyOnWriteArrayList<ManufactureItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 2)
					continue;
				int recId = Integer.parseInt(values[0]);
				long price = Long.parseLong(values[1]);
				if(findRecipe(recId))
					_createList.add(new ManufactureItem(recId, price));
			}
			var = getVar("manufacturename");
			if(var != null)
				setManufactureName(var);
		}

		int storeType = getVarInt("storemode", 0);
		if(storeType != 0)
		{
			setPrivateStoreType(storeType);
			setSitting(true);
		}
	}

	public void restoreRecipeBook()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, getObjectId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				int id = rset.getInt("id");
				RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(id);
				registerRecipe(recipe, false);
			}
		}
		catch(Exception e)
		{
			_log.warn("count not recipe skills:" + e);
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public List<DecoyInstance> getDecoys()
	{
		return _decoys;
	}

	public void addDecoy(DecoyInstance decoy)
	{
		_decoys.add(decoy);
	}

	public void removeDecoy(DecoyInstance decoy)
	{
		_decoys.remove(decoy);
	}

	public MountType getMountType()
	{
		return _mount == null ? MountType.NONE : _mount.getType();
	}

	@Override
	public boolean setReflection(Reflection reflection)
	{
		if(getReflection() == reflection)
			return true;

		if(!super.setReflection(reflection))
			return false;

		for(Servitor servitor : getServitors())
		{
			if(!servitor.isDead())
				servitor.setReflection(reflection);
		}

		if(!reflection.isMain())
		{
			String var = getVar("reflection");
			if(var == null || !var.equals(String.valueOf(reflection.getId())))
				setVar("reflection", String.valueOf(reflection.getId()), -1);
		}
		else
			unsetVar("reflection");

		return true;
	}

	public boolean isTerritoryFlagEquipped()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return weapon != null && weapon.getTemplate().isTerritoryFlag();
	}

	private int _buyListId;

	public void setBuyListId(int listId)
	{
		_buyListId = listId;
	}

	public int getBuyListId()
	{
		return _buyListId;
	}

	public int getFame()
	{
		return _fame;
	}

	public void setFame(int fame, String log, boolean notify)
	{
		fame = Math.min(Config.LIM_FAME, fame);
		if(log != null && !log.isEmpty())
			Log.add(_name + "|" + (fame - _fame) + "|" + fame + "|" + log, "fame");
		if(fame > _fame && notify)
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - _fame));
		_fame = fame;
		sendChanges();
	}

    public int getRaidPoints()
    {
        return _raidPoints;
    }
    
    public void setRaidPoints(int value)
    {
        _raidPoints = value;
        _raidPoints = Math.min(Config.LIM_RAID_POINTS, _raidPoints);
        sendChanges();
    }
    
    public void addRaidPoints(int value, boolean notify)
    {
        if(value <= 0)
            return;

        if(_raidPoints >= Config.LIM_RAID_POINTS)
        {
            if(notify)
                this.sendPacket(SystemMsg.YOU_HAVE_REACHED_THE_MAXIMUM_AMOUNT_OF_RAID_POINTS_AND_CAN_ACQUIRE_NO_MORE);
            return;
        }
        
        if(notify)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_RAID_POINTS).addNumber(value));
        
        _raidPoints += value;
        _raidPoints = Math.min(Config.LIM_RAID_POINTS, _raidPoints);
        sendChanges();
    }
    
    public boolean reduceRaidPoints(int value, boolean notify)
    {
        if(value > _raidPoints)
        {
            if(notify)
                sendPacket(SystemMsg.NOT_ENOUGH_RAID_POINTS);
            
            return false;
        }
        
        if(notify)
            sendPacket(new SystemMessagePacket(SystemMsg.YOU_CONSUMED_S1_RAID_POINTS).addInteger(value));
        
        _raidPoints -= value;
        _raidPoints = Math.max(0, _raidPoints);
        sendChanges();
        return true;
    }
    
    public int getVitality()
    {
        return getActiveSubClass() == null ? 0 : getActiveSubClass().getVitality();
    }
    
    public void setVitality(int val)
    {
        setVitality(val, true);
    }
    
    public void setVitality(int val, boolean send)
    {
        if(getActiveSubClass() != null)
            getActiveSubClass().setVitality(val);
        
        if(send)
            sendPacket(new ExVitalityPointInfo(getVitality()));
    }
    
    public double getVitalityBonus()
    {
        return getVitality() > 0 ? (hasPremiumAccount() ? Config.ALT_VITALITY_PA_RATE : Config.ALT_VITALITY_RATE) : 1.;
    }
    
    public int getVitalityPotionsLimit()
    {
        return hasPremiumAccount() ? Config.ALT_VITALITY_POTIONS_PA_LIMIT : Config.ALT_VITALITY_POTIONS_LIMIT;
    }
    
    public void setUsedVitalityPotions(int val, boolean send)
    {
        if(getActiveSubClass() != null)
            getActiveSubClass().setUsedVitalityPotions(val);
        
        if(send)
            sendPacket(new ExVitalityEffectInfo(this));
    }
    
    public int getUsedVitalityPotions()
    {
        return getActiveSubClass() == null ? 0 : getActiveSubClass().getUsedVitalityPotions();
    }
    
    public int getVitalityPotionsLeft()
    {
        return Math.max(0, getVitalityPotionsLimit() - getUsedVitalityPotions());
    }

	private final int _incorrectValidateCount = 0;

	public int getIncorrectValidateCount()
	{
		return _incorrectValidateCount;
	}

	public int setIncorrectValidateCount(int count)
	{
		return _incorrectValidateCount;
	}

	public int getExpandInventory()
	{
		return _expandInventory;
	}

	public void setExpandInventory(int inventory)
	{
		_expandInventory = inventory;
	}

	public int getExpandWarehouse()
	{
		return _expandWarehouse;
	}

	public void setExpandWarehouse(int warehouse)
	{
		_expandWarehouse = warehouse;
	}

	public boolean isNotShowBuffAnim()
	{
		return _notShowBuffAnim;
	}

	public void setNotShowBuffAnim(boolean value)
	{
		_notShowBuffAnim = value;
	}

	public boolean canSeeAllShouts()
	{
		return _canSeeAllShouts;
	}

	public void setCanSeeAllShouts(boolean b)
	{
		_canSeeAllShouts = b;
	}

	public void enterMovieMode()
	{
		if(isInMovie()) 
			return;

		setTarget(null);
		stopMove();
		setMovieId(-1);
		sendPacket(new CameraModePacket(1));
	}

	public void leaveMovieMode()
	{
		setMovieId(0);
		sendPacket(new CameraModePacket(0));
		broadcastCharInfo();
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration)
	{
		sendPacket(new SpecialCameraPacket(target.getObjectId(), dist, yaw, pitch, time, duration));
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		sendPacket(new SpecialCameraPacket(target.getObjectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
	}

	private int _movieId = 0;

	public void setMovieId(int id)
	{
		_movieId = id;
	}

	public int getMovieId()
	{
		return _movieId;
	}

	public boolean isInMovie()
	{
		return _movieId != 0 && !isFakePlayer();
	}

	public void startScenePlayer(SceneMovie movie)
	{
		if(isInMovie()) 
			return;

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movie.getId());
		sendPacket(movie.packet(this));
	}

	public void startScenePlayer(int movieId)
	{
		if(isInMovie()) 
			return;

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movieId);
		sendPacket(new ExStartScenePlayer(movieId));
	}

	public void endScenePlayer()
	{
		if(!isInMovie())
			return;

		setMovieId(0);
		decayMe();
		spawnMe();
	}

	public void setAutoLoot(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL)
		{
			_autoLoot = enable;
			setVar("AutoLoot", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootOnlyAdena(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL && Config.AUTO_LOOT_ONLY_ADENA)
		{
			_autoLootOnlyAdena = enable;
			setVar("AutoLootOnlyAdena", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootHerbs(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL)
		{
			AutoLootHerbs = enable;
			setVar("AutoLootHerbs", String.valueOf(enable), -1);
		}
	}

	public boolean isAutoLootEnabled()
	{
		return _autoLoot;
	}

	public boolean isAutoLootOnlyAdenaEnabled()
	{
		return _autoLootOnlyAdena;
	}

	public boolean isAutoLootHerbsEnabled()
	{
		return AutoLootHerbs;
	}

	public final void reName(String name, boolean saveToDB)
	{
		setName(name);
		if(saveToDB)
		{
			saveNameToDB();
			OlympiadParticipiantData participant = Olympiad.getParticipantInfo(getObjectId());
			if(participant != null)
				participant.setName(name);
		}
		broadcastUserInfo(true);
	}

	public final void reName(String name)
	{
		reName(name, false);
	}

	public final void saveNameToDB()
	{
		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
			st.setString(1, getName());
			st.setInt(2, getObjectId());
			st.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, st);
		}
	}

	@Override
	public Player getPlayer()
	{
		return this;
	}

	public BypassStorage getBypassStorage()
	{
		return _bypassStorage;
	}

	public int getTalismanCount()
	{
		return (int) calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
	}

	public int getJewelsLimit()
	{
		return (int) calcStat(Stats.JEWELS_LIMIT, 0, null, null);
	}

	public final void disableDrop(int time)
	{
		_dropDisabled = System.currentTimeMillis() + time;
	}

	public final boolean isDropDisabled()
	{
		return _dropDisabled > System.currentTimeMillis();
	}

	private ItemInstance _petControlItem = null;

	public void setPetControlItem(int itemObjId)
	{
		setPetControlItem(getInventory().getItemByObjectId(itemObjId));
	}

	public void setPetControlItem(ItemInstance item)
	{
		_petControlItem = item;
	}

	public ItemInstance getPetControlItem()
	{
		return _petControlItem;
	}

	private AtomicBoolean isActive = new AtomicBoolean();

	public boolean isActive()
	{
		return isActive.get();
	}

	public void setActive()
	{
		setNonAggroTime(0);
		setNonPvpTime(0);

		if(isActive.getAndSet(true))
			return;

		onActive();
	}

	private void onActive()
	{
		sendPacket(SystemMsg.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);

		if(getPetControlItem() != null || _restoredSummons != null && !_restoredSummons.isEmpty())
		{
			ThreadPoolManager.getInstance().execute(() ->
			{
				if(getPetControlItem() != null)
					summonPet();

				if(_restoredSummons != null && !_restoredSummons.isEmpty())
					spawnRestoredSummons();
			});
		}
	}

	public void summonPet()
	{
		if(getPet() != null)
			return;

		ItemInstance controlItem = getInventory().getItemByObjectId(getPetControlItem().getObjectId());
		if(controlItem == null)
		{
			setPetControlItem(null);
			return;
		}

		PetData petTemplate = PetDataHolder.getInstance().getTemplateByItemId(controlItem.getItemId());
		if(petTemplate == null)
		{
			setPetControlItem(null);
			return;
		}

		NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(petTemplate.getNpcId());
		if(npcTemplate == null)
		{
			setPetControlItem(null);
			return;
		}

		PetInstance pet = PetInstance.restore(controlItem, npcTemplate, this);
		if(pet == null)
		{
			setPetControlItem(null);
			return;
		}

		setPet(pet);
		pet.setTitle(Servitor.TITLE_BY_OWNER_NAME);

		if(!pet.isRespawned())
		{
			pet.setCurrentHp(pet.getMaxHp(), false);
			pet.setCurrentMp(pet.getMaxMp());
			pet.setCurrentFed(pet.getMaxFed(), false);
			pet.updateControlItem();
			pet.store();
		}

		pet.getInventory().restore();

		pet.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		pet.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
		pet.setReflection(getReflection());
		pet.spawnMe(Location.findPointToStay(this, 50, 70));
		pet.setRunning();
		pet.setFollowMode(true);
		pet.getInventory().validateItems();

		if(pet instanceof PetBabyInstance)
			((PetBabyInstance) pet).startBuffTask();

		getListeners().onSummonServitor(pet);
	}

	public void restoreSummons()
	{
		_restoredSummons = SummonsDAO.getInstance().restore(this);
	}

	private void spawnRestoredSummons()
	{
		if(_restoredSummons == null || _restoredSummons.isEmpty())
			return;

		for(RestoredSummon summon : _restoredSummons)
		{
			Skill skill = SkillHolder.getInstance().getSkill(summon.skillId, summon.skillLvl);
			if(skill == null)
				continue;

			if(skill instanceof Summon)
				((Summon) skill).summon(this, null, summon);
		}
		_restoredSummons.clear();
		_restoredSummons = null;
	}

	public List<TrapInstance> getTraps()
	{
		return _traps;
	}

	public void addTrap(TrapInstance trap)
	{
		if(_traps == Collections.<TrapInstance>emptyList())
			_traps = new CopyOnWriteArrayList<TrapInstance>();
		_traps.add(trap);
	}

	public void removeTrap(TrapInstance trap)
	{
		_traps.remove(trap);
	}
	
	public void destroyAllTraps()
	{
		for(TrapInstance t : _traps)
			t.deleteMe();
	}

	public void setBlockCheckerArena(byte arena)
	{
		_handysBlockCheckerEventArena = arena;
	}

	public int getBlockCheckerArena()
	{
		return _handysBlockCheckerEventArena;
	}

	@Override
	public PlayerListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new PlayerListenerList(this);
			}
		return (PlayerListenerList) listeners;
	}

	@Override
	public PlayerStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new PlayerStatsChangeRecorder(this);
			}
		return (PlayerStatsChangeRecorder) _statsRecorder;
	}

	private Future<?> _hourlyTask;
	private int _hoursInGame = 0;

	public int getHoursInGame()
	{
		_hoursInGame++;
		return _hoursInGame;
	}

	public void startHourlyTask()
	{
		_hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
	}

	public void stopHourlyTask()
	{
		if(_hourlyTask != null)
		{
			_hourlyTask.cancel(false);
			_hourlyTask = null;
		}
	}

	public long getPremiumPoints()
	{
		if(Config.IM_PAYMENT_ITEM_ID > 0)
			return ItemFunctions.getItemCount(this, Config.IM_PAYMENT_ITEM_ID);

		if(getNetConnection() != null)
			return getNetConnection().getPoints();

		return 0;
	}

	public boolean reducePremiumPoints(final int val)
	{
		if(Config.IM_PAYMENT_ITEM_ID > 0)
		{
			if(ItemFunctions.deleteItem(this, Config.IM_PAYMENT_ITEM_ID, val, true))
				return true;
			return false;
		}

		if(getNetConnection() != null)
		{
			getNetConnection().setPoints((int) (getPremiumPoints() - val));
			AuthServerCommunication.getInstance().sendPacket(new ReduceAccountPoints(getAccountName(), val));
			return true;
		}
		return false;
	}

	private boolean _agathionResAvailable = false;

	public boolean isAgathionResAvailable()
	{
		return _agathionResAvailable;
	}

	public void setAgathionRes(boolean val)
	{
		_agathionResAvailable = val;
	}

	public boolean isClanAirShipDriver()
	{
		return isInBoat() && getBoat().isClanAirShip() && ((ClanAirShip) getBoat()).getDriver() == this;
	}

	
	private Map<String, String> _userSession;

	public String getSessionVar(String key)
	{
		if(_userSession == null)
			return null;
		return _userSession.get(key);
	}

	public void setSessionVar(String key, String val)
	{
		if(_userSession == null)
			_userSession = new ConcurrentHashMap<String, String>();

		if(val == null || val.isEmpty())
			_userSession.remove(key);
		else
			_userSession.put(key, val);
	}

	public BlockList getBlockList()
	{
		return _blockList;
	}

	public FriendList getFriendList()
	{
		return _friendList;
	}

	public MenteeList getMenteeList()
	{
		return _menteeList;
	}

	public PremiumItemList getPremiumItemList()
	{
		return _premiumItemList;
	}

	public ProductHistoryList getProductHistoryList()
	{
		return _productHistoryList;
	}

	public HennaList getHennaList()
	{
		return _hennaList;
	}

	public AttendanceRewards getAttendanceRewards()
	{
		return _attendanceRewards;
	}

	public DailyMissionList getDailyMissionList()
	{
		return _dailiyMissionList;
	}

    public FactionList getFactionList()
    {
        return _factionList;
    }

	public void mentoringLoginConditions()
	{
		if(getMenteeList().someOneOnline(true))
		{
			getMenteeList().notify(true);
			Mentoring.applyMentoringCond(this, true);
            Mentoring.addMentoringSkills(this);
		}
	}

	public void mentoringLogoutConditions()
	{
		if(getMenteeList().someOneOnline(false))
		{
			getMenteeList().notify(false);
			Mentoring.applyMentoringCond(this, false);
            Mentoring.addMentoringSkills(this);
		}
	}

	public boolean isNotShowTraders()
	{
		return _notShowTraders;
	}

	public void setNotShowTraders(boolean notShowTraders)
	{
		_notShowTraders = notShowTraders;
	}

	public boolean isDebug()
	{
		return _debug;
	}

	public void setDebug(boolean b)
	{
		_debug = b;
	}

	public void sendItemList(boolean show)
	{
		final ItemInstance[] items = getInventory().getItems();
		final LockType lockType = getInventory().getLockType();
		final int[] lockItems = getInventory().getLockItems();

		int allSize = items.length;
		int questItemsSize = 0;
		int agathionItemsSize = 0;
		for(ItemInstance item : items)
		{
			if(item.getTemplate().isQuest())
				questItemsSize++;
			if(item.getTemplate().getAgathionEnergy() > 0)
		        agathionItemsSize++;
		}

		sendPacket(new ItemListPacket(this, allSize - questItemsSize, items, show, lockType, lockItems));
		sendPacket(new ExQuestItemListPacket(questItemsSize, items, lockType, lockItems));
	    if(agathionItemsSize > 0)
	        sendPacket(new ExBR_AgathionEnergyInfoPacket(agathionItemsSize, items));
	}

	public int getBeltInventoryIncrease()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
		if(item != null && item.getTemplate().getAttachedSkills() != null)
		{
			for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
			{
				for(FuncTemplate func : skillEntry.getTemplate().getAttachedFuncs())
				{
					if(func._stat == Stats.INVENTORY_LIMIT)
						return (int) func._value;
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	public boolean checkCoupleAction(Player target)
	{
		if(target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
			return false;
		}
		if(target.isFishing())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
			return false;
		}
		if(target.isInTrainingCamp())
		{
			sendPacket(SystemMsg.YOU_CANNOT_REQUEST_TO_A_CHARACTER_WHO_IS_ENTERING_THE_TRAINING_CAMP);
			return false;
		} 
	    if(target.isChaosFestivalParticipant())
	    {
	        sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
	        return false;
	    }
		if(target.isTransformed())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
			return false;
		}
		if(target.isInCombat() || target.isVisualTransformed())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
			return false;
		} 
	    if(target.isCursedWeaponEquipped())
	    {
	        sendPacket(new SystemMessage(SystemMsg.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
	        return false;
	    }
		if(target.isInOlympiadMode())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
			return false;
		}
		if(target.isInSiegeZone())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
			return false;
		}
		if(target.isInBoat() || target.getMountNpcId() != 0)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
			return false;
		}
		if(target.isTeleporting())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
			return false;
		}
		if(target.isDead())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
			return false;
		}
		return true;
	}

	@Override
	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
		for(Servitor servitor : getServitors())
			servitor.startAttackStanceTask0();
	}

	@Override
	public void displayGiveDamageMessage(Creature target, Skill skill, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean blocked)
	{
		super.displayGiveDamageMessage(target, skill, damage, servitorTransferedDamage, transferedDamage, crit, miss, shld, blocked);

		if(miss)
		{
			if(skill == null)
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
			else
				sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.EVADED));
			return;
		}

		if(crit)
			if(skill != null)
			{
				if(skill.isMagic())
					sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);

				sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.CRITICAL));
			}
			else
				sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this));

		if(blocked)
		{
			sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
			sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), target.isInvulnerable() ? ExMagicAttackInfo.IMMUNE : ExMagicAttackInfo.BLOCKED));
		}
		else if(target.isDoor() || (target instanceof SiegeToggleNpcInstance))
			sendPacket(new SystemMessagePacket(SystemMsg.YOU_HIT_FOR_S1_DAMAGE).addInteger(damage));
		else
		{
			if(servitorTransferedDamage != null && transferedDamage > 0)
			{
				SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_INFLICTED_S3_DAMAGE_ON_C2_AND_S4_DAMAGE_ON_THE_DAMAGE_TRANSFER_TARGET);
				sm.addName(this);
				sm.addInteger(damage);
				sm.addName(target);
				sm.addInteger(transferedDamage);
				sm.addHpChange(target.getObjectId(), getObjectId(), -damage);
				sm.addHpChange(servitorTransferedDamage.getObjectId(), getObjectId(), -transferedDamage);
				sendPacket(sm);
			}
			else
				sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(target).addInteger(damage).addHpChange(target.getObjectId(), getObjectId(), -damage));

			if(shld)
			{
				if(damage == Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE)
				{
					if(skill != null && skill.isMagic())
					{
						sendPacket(new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(this));
						sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
					}
				}
				else if(damage > 0 && skill != null && skill.isMagic())
					sendPacket(new SystemMessagePacket(SystemMsg.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED));
			}
		}
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		if(attacker != this)
			sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addInteger(damage).addHpChange(getObjectId(), attacker.getObjectId(), -damage));
	}

	public IntObjectMap<String> getPostFriends()
	{
		return _postFriends;
	}

	public void setPostFriends(IntObjectMap<String> val)
	{
		_postFriends = val;
	}

	public void sendReuseMessage(ItemInstance item)
	{
		TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
		if(sts == null || !sts.hasNotPassed())
			return;

		long timeleft = sts.getReuseCurrent();
		long hours = timeleft / 3600000;
		long minutes = (timeleft - hours * 3600000) / 60000;
		long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);

		if(hours > 0)
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().getItemId()).addInteger(hours).addInteger(minutes).addInteger(seconds));
		else if(minutes > 0)
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().getItemId()).addInteger(minutes).addInteger(seconds));
		else
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().getItemId()).addInteger(seconds));
	}

	public void ask(ConfirmDlgPacket dlg, OnAnswerListener listener)
	{
		if(_askDialog != null)
			return;
		int rnd = Rnd.nextInt();
		_askDialog = new IntObjectPairImpl<OnAnswerListener>(rnd, listener);
		dlg.setRequestId(rnd);
		sendPacket(dlg);
	}

	public IntObjectPair<OnAnswerListener> getAskListener(boolean clear)
	{
		if(!clear)
			return _askDialog;
		else
		{
			IntObjectPair<OnAnswerListener> ask = _askDialog;
			_askDialog = null;
			return ask;
		}
	}

	@Override
	public boolean isDead()
	{
		return (isInOlympiadMode() || isInDuel()) ? getCurrentHp() <= 1. : super.isDead();
	}

	@Override
	public int getAgathionEnergy()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		return item == null ? 0 : item.getAgathionEnergy();
	}

	@Override
	public void setAgathionEnergy(int val)
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if(item == null)
			return;
		item.setAgathionEnergy(val);
		item.setJdbcState(JdbcEntityState.UPDATED);

		sendPacket(new ExBR_AgathionEnergyInfoPacket(1, item));
	}

	public boolean hasPrivilege(Privilege privilege)
	{
		return _clan != null && (getClanPrivileges() & privilege.mask()) == privilege.mask();
	}

	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}

	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
		if(matchingRoom == null)
			_matchingRoomWindowOpened = false;
	}

	public boolean isMatchingRoomWindowOpened()
	{
		return _matchingRoomWindowOpened;
	}

	public void setMatchingRoomWindowOpened(boolean b)
	{
		_matchingRoomWindowOpened = b;
	}

	public void dispelBuffs()
	{
		for(Abnormal e : getAbnormalList())
			if(e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !isSpecialAbnormal(e.getSkill()))
			{
				sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
				e.exit();
			}


		for(Servitor servitor : getServitors())
		{
			for(Abnormal e : servitor.getAbnormalList())
			{
				if(!e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !servitor.isSpecialAbnormal(e.getSkill()))
					e.exit();
			}
		}
	}

	public void setInstanceReuse(int id, long time)
	{
		final SystemMessage msg = new SystemMessage(SystemMessage.INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE).addString(getName());
		sendPacket(msg);
		_instancesReuses.put(id, time);
		mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", getObjectId(), id, time);
	}

	public void removeInstanceReuse(int id)
	{
		if(_instancesReuses.remove(id) != null)
			mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", getObjectId(), id);
	}

	public void removeAllInstanceReuses()
	{
		_instancesReuses.clear();
		mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", getObjectId());
	}

	public void removeInstanceReusesByGroupId(int groupId)
	{
		for(int i : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId))
			if(getInstanceReuse(i) != null)
				removeInstanceReuse(i);
	}

	public Long getInstanceReuse(int id)
	{
		return _instancesReuses.get(id);
	}

	public Map<Integer, Long> getInstanceReuses()
	{
		return _instancesReuses;
	}

	private void loadInstanceReuses()
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?");
			offline.setInt(1, getObjectId());
			rs = offline.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("id");
				long reuse = rs.getLong("reuse");
				_instancesReuses.put(id, reuse);
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, offline, rs);
		}
	}

	public void setActiveReflection(Reflection reflection)
	{
		_activeReflection = reflection;
	}

	public Reflection getActiveReflection()
	{
		return _activeReflection;
	}

	public boolean canEnterInstance(int instancedZoneId)
	{
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

		if(isDead())
			return false;

		if(ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT)
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		if(iz == null)
		{
			sendPacket(SystemMsg.SYSTEM_ERROR);
			return false;
		}

		if(ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels())
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		return iz.getEntryType(this).canEnter(this, iz);
	}

	public boolean canReenterInstance(int instancedZoneId)
	{
		if((getActiveReflection() != null && getActiveReflection().getInstancedZoneId() != instancedZoneId) || !getReflection().isMain())
		{
			sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
			return false;
		}
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
		if(iz.isDispelBuffs())
			dispelBuffs();
		return iz.getEntryType(this).canReEnter(this, iz);
	}

	public int getBattlefieldChatId()
	{
		return _battlefieldChatId;
	}

	public void setBattlefieldChatId(int battlefieldChatId)
	{
		_battlefieldChatId = battlefieldChatId;
	}

	@Override
	public void broadCast(IBroadcastPacket... packet)
	{
		sendPacket(packet);
	}

	@Override
	public int getMemberCount()
	{
		return 1;
	}

	@Override
	public Player getGroupLeader()
	{
		return this;
	}

	@Override
	public Iterator<Player> iterator()
	{
		return Collections.singleton(this).iterator();
	}

	public PlayerGroup getPlayerGroup()
	{
		if(getParty() != null)
		{
			if(getParty().getCommandChannel() != null)
				return getParty().getCommandChannel();
			else
				return getParty();
		}
		else
			return this;
	}

	public boolean isActionBlocked(String action)
	{
		return _blockedActions.contains(action);
	}

	public void blockActions(String... actions)
	{
		Collections.addAll(_blockedActions, actions);
	}

	public void unblockActions(String... actions)
	{
		for(String action : actions)
			_blockedActions.remove(action);
	}

	public OlympiadGame getOlympiadGame()
	{
		return _olympiadGame;
	}

	public void setOlympiadGame(OlympiadGame olympiadGame)
	{
		_olympiadGame = olympiadGame;
	}

	public void addRadar(int x, int y, int z)
	{
		sendPacket(new RadarControlPacket(0, 1, x, y, z));
	}

	public void addRadarWithMap(int x, int y, int z)
	{
		sendPacket(new RadarControlPacket(0, 2, x, y, z));
	}

	public PetitionMainGroup getPetitionGroup()
	{
		return _petitionGroup;
	}

	public void setPetitionGroup(PetitionMainGroup petitionGroup)
	{
		_petitionGroup = petitionGroup;
	}

	public int getLectureMark()
	{
		return _lectureMark;
	}

	public void setLectureMark(int lectureMark)
	{
		_lectureMark = lectureMark;
	}

	public boolean isUserRelationActive()
	{
		return _enableRelationTask == null;
	}

	public void startEnableUserRelationTask(long time, SiegeEvent<?, ?> siegeEvent)
	{
		if(_enableRelationTask != null)
			return;

		_enableRelationTask = ThreadPoolManager.getInstance().schedule(new EnableUserRelationTask(this, siegeEvent), time);
	}

	public void stopEnableUserRelationTask()
	{
		if(_enableRelationTask != null)
		{
			_enableRelationTask.cancel(false);
			_enableRelationTask = null;
		}
	}

	public void broadcastRelation()
	{
		if(!isVisible())
			return;

		for(Player target : World.getAroundObservers(this))
		{
			if(isInvisible(target))
				continue;

			RelationChangedPacket relationChanged = new RelationChangedPacket(this, target);
			for(Servitor servitor : getServitors())
				relationChanged.add(servitor, target);

			target.sendPacket(relationChanged);
		}
	}

	private int[] _recentProductList = null;

	public int[] getRecentProductList()
	{
		if(_recentProductList == null)
		{
			String value = getVar(RECENT_PRODUCT_LIST_VAR);
			if(value == null)
				return null;

			String[] products_str = value.split(";");
			int[] result = new int[0];
			for(int i = 0; i < products_str.length; i++)
			{
				int productId = Integer.parseInt(products_str[i]);
				if(ProductDataHolder.getInstance().getProduct(productId) == null)
					continue;

				result = ArrayUtils.add(result, productId);
			}
			_recentProductList = result;
		}
		return _recentProductList;
	}

	public void updateRecentProductList(final int productId)
	{
		if(_recentProductList == null)
		{
			_recentProductList = new int[1];
			_recentProductList[0] = productId;
		}
		else
		{
			int[] newProductList = new int[1];
			newProductList[0] = productId;
			for(int i = 0; i < _recentProductList.length; i++)
			{
				if(newProductList.length >= Config.IM_MAX_ITEMS_IN_RECENT_LIST)
					break;

				int itemId = _recentProductList[i];
				if(ArrayUtils.contains(newProductList, itemId))
					continue;

				newProductList = ArrayUtils.add(newProductList, itemId);
			}

			_recentProductList = newProductList;
		}

		String valueToUpdate = "";
		for(int itemId : _recentProductList)
		{
			valueToUpdate += itemId + ";";
		}
		setVar(RECENT_PRODUCT_LIST_VAR, valueToUpdate, -1);
	}

	@Override
	public int getINT()
	{
		return Math.max(getTemplate().getMinINT(), Math.min(getTemplate().getMaxINT(), super.getINT()));
	}

	@Override
	public int getSTR()
	{
		return Math.max(getTemplate().getMinSTR(), Math.min(getTemplate().getMaxSTR(), super.getSTR()));
	}

	@Override
	public int getCON()
	{
		return Math.max(getTemplate().getMinCON(), Math.min(getTemplate().getMaxCON(), super.getCON()));
	}

	@Override
	public int getMEN()
	{
		return Math.max(getTemplate().getMinMEN(), Math.min(getTemplate().getMaxMEN(), super.getMEN()));
	}

	@Override
	public int getDEX()
	{
		return Math.max(getTemplate().getMinDEX(), Math.min(getTemplate().getMaxDEX(), super.getDEX()));
	}

	@Override
	public int getWIT()
	{
		return Math.max(getTemplate().getMinWIT(), Math.min(getTemplate().getMaxWIT(), super.getWIT()));
	}

    @Override
    public int getLUC()
    {
        final int luc = (int) calcStat(Stats.STAT_LUC, getBaseStats().getLUC(), null, null);
        return Math.max(getTemplate().getMinLUC(), Math.min(getTemplate().getMaxLUC(), luc));
    }
    
    @Override
    public int getCHA()
    {
        final int cha = (int) calcStat(Stats.STAT_CHA, getBaseStats().getCHA(), null, null);
        return Math.max(getTemplate().getMinCHA(), Math.min(getTemplate().getMaxCHA(), cha));
    }

	public void changeClass(final int index)
	{
		if(isInDuel()) 
			return;

		SystemMsg msg = checkChangeClassCondition();
		if(msg != null)
		{
			sendPacket(msg);
			return;
		}

		if(isInZone(ZoneType.epic))
		{
			sendMessage("you cannot change class while in epic zone"); 
			return;
		}

		SubClass sub = _subClassList.getByIndex(index);
		if(sub == null)
			return;

		
		int classId = sub.getClassId();
		int oldClassId = getActiveClassId();
		setActiveSubClass(classId, true, false);
		Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_CONFUSION, 1);
		skill.getEffects(this, this);
        
        Mentoring.applyMentoringCond(this,true);
		sendPacket(new SystemMessage(SystemMessage.THE_TRANSFER_OF_SUB_CLASS_HAS_BEEN_COMPLETED).addClassName(oldClassId).addClassName(classId));
	}

	private SystemMsg checkChangeClassCondition()
	{
		if(getWeightPenalty() >= 3 || getInventoryLimit() * 0.8 < getInventory().getSize())
			return SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT;
		
		if(isInOlympiadMode() || isChaosFestivalParticipant()) 
			return SystemMsg.THIS_TERRITORY_CAN_NOT_CHANGE_CLASS;
		
		if(isRegisteredInChaosFestival())
            return SystemMsg.YOU_CANNOT_CHANGE_YOUR_SUBCLASS_WHILE_REGISTERED_IN_THE_CEREMONY_OF_CHAOS;
		
		if(hasServitor())
			return SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED;
		
		if(isTransformed())
			return SystemMsg.YOU_CAN_NOT_CHANGE_CLASS_IN_TRANSFORMATION;

		return null;
	}

	public JumpTrack getCurrentJumpTrack()
	{
		return _currentJumpTrack;
	}

	public void setCurrentJumpTrack(JumpTrack val)
	{
		_currentJumpTrack = val;
	}

	public JumpWay getCurrentJumpWay()
	{
		return _currentJumpWay;
	}

	public void setCurrentJumpWay(JumpWay val)
	{
		_currentJumpWay = val;
	}

	public boolean isInJumping()
	{
		return _currentJumpTrack != null;
	}

	public void onJumpingBreak()
	{
		sendActionFailed();
		unsetVar("@safe_jump_loc");
		setCurrentJumpTrack(null);
		setCurrentJumpWay(null);
		unblock();
		_jumpState = JumpState.FINISHED;
	}

	public BookMarkList getBookMarkList()
	{
		return _bookmarks;
	}

	public AntiFlood getAntiFlood()
	{
		return _antiFlood;
	}

	@Override
	public boolean isDisabledAnalogSkill(int skillId)
	{
		return _disabledAnalogSkills.contains(skillId);
	}

	@Override
	public void disableAnalogSkills(Skill skill)
	{
		if(!skill.haveAnalogSkills())
			return;

		for(int removeSkillId : skill.getAnalogSkillIDs())
		{
			removeSkillById(removeSkillId);
			_disabledAnalogSkills.add(removeSkillId);
		}
	}

	@Override
	public void removeDisabledAnalogSkills(Skill skill)
	{
		if(!skill.haveAnalogSkills())
			return;

		
		for(int analogSkillId : skill.getAnalogSkillIDs())
		{
			_disabledAnalogSkills.remove(analogSkillId);
			
		}

		
			
	}

	public int getNpcDialogEndTime()
	{
		return _npcDialogEndTime;
	}

	public void setNpcDialogEndTime(int val)
	{
		_npcDialogEndTime = val;
	}

	@Override
	public boolean useItem(ItemInstance item, boolean ctrlPressed, boolean force)
	{
		if(item == null)
			return false;

		ItemTemplate template = item.getTemplate();
		IItemHandler handler = template.getHandler();
		if(handler == null)
		{
			
			return false;
		}

		boolean success = force ? handler.forceUseItem(this, item, ctrlPressed) : handler.useItem(this, item, ctrlPressed);
		if(success)
		{
			long nextTimeUse = template.getReuseType().next(item);
			if(nextTimeUse > System.currentTimeMillis())
			{
				TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, template.getReuseDelay());
				addSharedGroupReuse(template.getReuseGroup(), timeStamp);

				if(template.getReuseDelay() > 0)
					sendPacket(new ExUseSharedGroupItem(template.getDisplayReuseGroup(), timeStamp));
			}
		}
		return success;
	}

	public int getSkillsElementID()
	{
		return (int) calcStat(Stats.SKILLS_ELEMENT_ID, -1, null, null);
	}

	public int getAvailableSummonPoints()
	{
		int usedSummonPoints = 0;
		for(SummonInstance summon : getSummons())
			usedSummonPoints += summon.getSummonPoints();
		return getMaxSummonPoints() - usedSummonPoints;
	}

	public int getMaxSummonPoints()
	{
		return (int) calcStat(Stats.SUMMON_POINTS, 0, null, null);
	}

	public int getUsedSummonPoints()
	{
		return getMaxSummonPoints() - getAvailableSummonPoints();
	}

	public Location getStablePoint()
	{
		return _stablePoint;
	}

	public void setStablePoint(Location point)
	{
		_stablePoint = point;
	}

	public boolean isInSameParty(Player target)
	{
		return getParty() != null && target.getParty() != null && getParty() == target.getParty();
	}

	public boolean isInSameChannel(Player target)
	{
		Party activeCharP = getParty();
		Party targetP = target.getParty();
		if(activeCharP != null && targetP != null)
		{
			CommandChannel chan = activeCharP.getCommandChannel();
			if(chan != null && chan == targetP.getCommandChannel())
				return true;
		}
		return false;
	}

	public boolean isInSameClan(Player target)
	{
		return getClanId() != 0 && getClanId() == target.getClanId();
	}

	public final boolean isInSameAlly(Player target)
	{
		return getAllyId() != 0 && getAllyId() == target.getAllyId();
	}

	@Override
	public boolean isInCtF()
	{
		return _inCtF;
	}

	public boolean isInPvPEvent()
	{
		PvPEvent event = getEvent(PvPEvent.class);
		if(event != null && event.isBattleActive() || _inCtF)
			return true;

		return false;
	}

	public void setIsInCtF(boolean param) 
	{
		_inCtF = param;
	}

	public IntObjectMap<SkillChain> getSkillChainDetails()
	{
		return _skillChainDetail;
	}

	public void removeChainDetail(int i)
	{
		if(!_skillChainDetail.isEmpty() && _skillChainDetail.containsKey(i))
		{
			SkillChain sco = _skillChainDetail.remove(i);
			if(sco != null)
			{                
				SkillEntry skillEntry = getKnownSkill(sco.getCastingSkill());
				if (skillEntry != null)
				{
                    removeUnActiveSkill(skillEntry.getTemplate());
                    removeUnActiveSkill(this.getKnownSkill(skillEntry.getTemplate().getChainSkillId()).getTemplate());
                }
			}
		}
	}

	public void addChainDetail(Creature target, Skill skill, int duration)
	{
		_skillChainDetail.put(skill.getChainIndex(), new SkillChain(this, target,skill.getId(),skill.getChainSkillId()));

		addUnActiveSkill(skill);
		addUnActiveSkill(getKnownSkill(skill.getChainSkillId()).getTemplate());
		sendPacket(new ExAlterSkillRequest(14612 + skill.getChainIndex(), skill.getChainSkillId(), duration));
	}

	public boolean isRelatedTo(Creature character)
	{
		if(character == this)
			return true;

		if(character.isServitor()) 
		{
			if(isMyServitor(character.getObjectId()))
				return true;
			else if(character.getPlayer() != null)
			{
				Player Spc = character.getPlayer();
				if(isInSameParty(Spc) || isInSameChannel(Spc) || isInSameClan(Spc) || isInSameAlly(Spc))
					return true;
			}
		}
		else if(character.isPlayer())
		{
			Player pc = character.getPlayer();
			if(isInSameParty(pc) || isInSameChannel(pc) || isInSameClan(pc) || isInSameAlly(pc))
				return true;
		}
		return false;
	}

	public boolean isAutoSearchParty()
	{
		return _autoSearchParty;
	}

	public void enableAutoSearchParty()
	{
		_autoSearchParty = true;
		PartySubstituteManager.getInstance().addWaitingPlayer(this);
		sendPacket(ExWaitWaitingSubStituteInfo.OPEN);
	}

	public void disablePartySearch(boolean disableFlag)
	{
		if(_autoSearchParty)
		{
			PartySubstituteManager.getInstance().removeWaitingPlayer(this);
			sendPacket(ExWaitWaitingSubStituteInfo.CLOSE);
			_autoSearchParty = !disableFlag;
		}
	}

	public boolean refreshPartySearchStatus(boolean sendMsg)
	{
		if(!mayPartySearch(false,sendMsg))
		{
			disablePartySearch(false);
			return false;
		}

		if(isAutoSearchParty())
		{
			enableAutoSearchParty();
			return true;
		}
		return false;
	}

	public boolean mayPartySearch(boolean first, boolean msg)
	{
		if(getParty() != null)
			return false;

		if(isPK())
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
			}
			return false; 
		}

		if(isInDuel() && getTeam() != TeamType.NONE)
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_IN_A_DUEL);
			}
			return false;
		}

		if(isInOlympiadMode())
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_CURRENTLY_PARTICIPATING_IN_OLYMPIAD);
			}
			return false;
		}

		if(isInSiegeZone())
		{
			if(msg && first)
				sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGEFORTRESS_SIEGETERRITORY_WAR);

			return false;
		}

		if(isInZoneBattle() || getReflectionId() != 0)
		{
			if(msg && first)
				sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_BLOCK_CHECKERCOLISEUMKRATEIS_CUBE);

			return false;
		}

		if(isInZone(ZoneType.no_escape) || isInZone(ZoneType.epic))
			return false;
			
		if(!Config.ENABLE_PARTY_SEARCH)
			return false;
		return true;
	}

	public void startSubstituteTask()
	{
		if(!isPartySubstituteStarted())
		{
			_substituteTask = PartySubstituteManager.getInstance().SubstituteSearchTask(this);
			sendUserInfo();
			if(isInParty())
				getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
		}
	}

	public void stopSubstituteTask()
	{
		if(isPartySubstituteStarted())
		{
			PartySubstituteManager.getInstance().removePartyMember(this);
			_substituteTask.cancel(true);
			sendUserInfo();
			if(isInParty())
				getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
		}
	}

	public boolean isPartySubstituteStarted()
	{
		return getParty() != null && _substituteTask != null && !_substituteTask.isDone() && !_substituteTask.isCancelled();
	}

	@Override
	public int getSkillLevel(int skillId)
	{
		switch(skillId)
		{
			case 1566:	
			case 1567:	
			case 1568:	
			case 1569:	
			case 17192:	
				return 1;
			case 14612:	
			case 14613:	
			case 14614:	
				return !getSkillChainDetails().isEmpty() && getSkillChainDetails().containsKey(Math.abs(14612 - skillId)) ? 1 : -1;
		}
		return super.getSkillLevel(skillId);
	}

	public SymbolInstance getSymbol()
	{
		return _symbol;
	}

	public void setSymbol(SymbolInstance symbol)
	{
		_symbol = symbol;
	}

	public void setRegisteredInEvent(boolean inEvent)
	{
		_registeredInEvent = inEvent;
	}

	public boolean isRegisteredInEvent()
	{
		return _registeredInEvent;
	}

	private boolean checkActiveToggleEffects()
	{
		boolean dispelled = false;
		for(Abnormal effect : getAbnormalList())
		{
			Skill skill = effect.getSkill();
			if(skill == null)
				continue;

			if(!skill.isToggle())
				continue;

			if(getAllSkills().contains(skill))
				continue;

			effect.exit();
		}
		return dispelled;
	}

	public JumpState getJumpState()
	{
		return _jumpState;
	}

	public void setJumpState(JumpState value)
	{
		_jumpState = value;
	}
	
	public int getDestructionCount()
	{
		return _destructionCount;
	}

    public void setDestructionCount(int count)
    {
        if(_destructionCount == count)
            return;
        
        _destructionCount = count;
        if(_destructionCount > 0)
            setVar("@energ_destr_count", count);
        else
            unsetVar("@energ_destr_count");
    }
    
	public int getMarkEndureCount()
	{
		return _markEndureCount;
	}
	  
    public void setMarkEndureCount(int count)
    {
        if(_markEndureCount == count)
            return;

        _markEndureCount = count;
        if(_markEndureCount > 0)
            setVar("@mark_endur_count", count);
        else
            unsetVar("@mark_endur_count");
    }
 
	public void updateStat(CategoryType categoryType, int subCategory, long valueAdd)
	{
		WorldStatisticsManager.getInstance().updateStat(this, categoryType, subCategory, valueAdd);
	}

	public void updateStat(CategoryType categoryType, long valueAdd)
	{
		WorldStatisticsManager.getInstance().updateStat(this, categoryType, valueAdd);
	}

	@Override
	public Servitor getServitorForTransfereDamage(double transferDamage)
	{
		for (Servitor servitor : getSummons())
		{
			if(servitor == null || servitor.isDead() || servitor.getCurrentHp() < transferDamage)
				continue;

			if(servitor.isInRangeZ(this, 1200))
				return servitor;
		}

		return null;
	}

	@Override
	public double getDamageForTransferToServitor(double damage)
	{
		final double transferToSummonDam = calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.);
		if(transferToSummonDam > 0)
			return (damage * transferToSummonDam) * .01;
		return 0.;
	}

	public boolean canFixedRessurect()
	{
		if(getPlayerAccess().ResurectFixed)
			return true;

		if(!isInSiegeZone())
		{
			if(getInventory().getCountOf(10649) > 0 && !isInPvPEvent())
				return true;
			if(getInventory().getCountOf(13300) > 0 && !isInPvPEvent())
				return true;
		}
		else
		{
			int level = getLevel();
			if(level <= 19 && getInventory().getCountOf(8515) > 0)
				return true;

			if(level <= 39 && getInventory().getCountOf(8516) > 0)
				return true;

			if(level <= 51 && getInventory().getCountOf(8517) > 0)
				return true;

			if(level <= 60 && getInventory().getCountOf(8518) > 0)
				return true;

			if(level <= 75 && getInventory().getCountOf(8519) > 0)
				return true;

			if(level <= 84 && getInventory().getCountOf(8520) > 0)
				return true;
		}

		return false;
	}

	@Override
	public double getLevelBonus()
	{
		if(getTransform() != null && getTransform().getLevelBonus(getLevel()) > 0)
			return getTransform().getLevelBonus(getLevel());

		return super.getLevelBonus();
	}

	@Override
	public PlayerBaseStats getBaseStats()
	{
		if(_baseStats == null)
			_baseStats = new PlayerBaseStats(this);
		return (PlayerBaseStats) _baseStats;
	}

	@Override
	public PlayerFlags getFlags()
	{
		if(_statuses == null)
			_statuses = new PlayerFlags(this);

		return (PlayerFlags) _statuses;
	}

	public final boolean isChaosFestivalParticipant()
	{
		ChaosFestivalEvent event = getEvent(ChaosFestivalEvent.class);
		return event != null && event.isInProgress() && event.isParticle(this);
	}

	public final boolean isRegisteredInChaosFestival()
	{
		ChaosFestivalEvent event = getEvent(ChaosFestivalEvent.class);
		return event != null && !event.isInProgress() && event.isRegistered(this);
	}

	public final String getVisibleName(Player receiver)
	{
		if(isCursedWeaponEquipped())
		{
			String cursedName = getCursedWeaponName(receiver);
			if(cursedName == null || cursedName.isEmpty())
				return getName();

			return cursedName;
		}

		for(Event event : getEvents())
		{
			String name = event.getVisibleName(this, receiver);
			if(name != null)
				return name;
		}

		return getName();
	}

	public final String getVisibleTitle(Player receiver)
	{
		if(isCursedWeaponEquipped())
		    return "";
		
		if(isInBuffStore())
		{
			BufferData bufferData = OfflineBufferManager.getInstance().getBuffStore(getObjectId());
			if(bufferData != null)
				return bufferData.getSaleTitle();
		}

		if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(getReflection() == ReflectionManager.GIRAN_HARBOR)
				return "";

			if(getReflection() == ReflectionManager.PARNASSUS)
				return "";
		}

		if(isInAwayingMode())
		{
			String awayText = AwayManager.getInstance().getAwayText(this);
			
			if(awayText == null || awayText.length() <= 1)
				return isLangRus() ? "<Отошел>" : "<Away>";
			else
				return (isLangRus() ? "<Отошел>" : "<Away>") + " - " + awayText + "*";
		}

		String title;
		for(Event event : getEvents())
		{
			title = event.getVisibleTitle(this, receiver);
			if(title != null)
				return title;
		}

		return getTitle();
	}

	public final int getVisibleNameColor(Player receiver)
	{
		if(isInBuffStore())
		{
			OfflineBufferManager.BufferData bufferData = OfflineBufferManager.getInstance().getBuffStore(getObjectId());
			if(bufferData != null)
			{
				if(isInOfflineMode())
					return Config.BUFF_STORE_OFFLINE_NAME_COLOR;
				else
					return Config.BUFF_STORE_NAME_COLOR;
			}
		}

		if(isInStoreMode() && isInOfflineMode())
		{
			return Config.SERVICES_OFFLINE_TRADE_NAME_COLOR;
		}

		Integer color;
		for(Event event : getEvents())
		{
			color = event.getVisibleNameColor(this, receiver);
			if(color != null)
				return color.intValue();
		}

		int premiumNameColor = getPremiumAccount().getProperties().getNameColor();
		if(premiumNameColor != -1)
			return premiumNameColor;

		return getNameColor();
	}

	public final int getVisibleTitleColor(Player receiver)
	{
		if(isInBuffStore())
		{
			OfflineBufferManager.BufferData bufferData = OfflineBufferManager.getInstance().getBuffStore(getObjectId());
			if(bufferData != null && !isInOfflineMode())
				return Config.BUFF_STORE_TITLE_COLOR;
		}

		if(isInAwayingMode())
			return Config.AWAY_TITLE_COLOR;

		Integer color;
		for(Event event : getEvents())
		{
			color = event.getVisibleTitleColor(this, receiver);
			if(color != null)
				return color.intValue();
		}

		int premiumTitleColor = getPremiumAccount().getProperties().getTitleColor();
		if(premiumTitleColor != -1)
			return premiumTitleColor;

		return getTitleColor();
	}

	public final boolean isPledgeVisible(Player receiver)
	{
		if(isCursedWeaponEquipped())
		    return false;
		
		if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(getReflection() == ReflectionManager.GIRAN_HARBOR)
				return false;

			if(getReflection() == ReflectionManager.PARNASSUS)
				return false;
		}

		for(Event event : getEvents())
		{
			if(!event.isPledgeVisible(this, receiver))
				return false;
		}

		return true;
	}

	public void checkAndDeleteOlympiadItems()
	{
		int rank = Olympiad.getRank(this);
		if(rank != 2 && rank != 3)
			ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_FAME_CLOAK);

		if(!isHero())
		{
			ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_HERO_WING);
			ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_HERO_CLOAK);
			for(int itemId : ItemTemplate.HERO_WEAPON_IDS)
				ItemFunctions.deleteItemsEverywhere(this, itemId);
		}
	}

	public double getEnchantChanceModifier()
	{
		return calcStat(Stats.ENCHANT_CHANCE_MODIFIER);
	}

	@Override
	public boolean isSpecialAbnormal(Skill skill)
	{
		if(getClan() != null && getClan().isSpecialAbnormal(skill))
			return true;

		if(skill.isNecessaryToggle())
			return true;

		int skillId = skill.getId();

		if(skillId == 7008 || skillId == 7115 || skillId == 16419 || skillId == 16420 || skillId == 16421 || skillId == 6038 || skillId == 6039 || skillId == 6040 || skillId == 6055 || skillId == 6056 || skillId == 6057 || skillId == 6058)
			return true;
	    if(Mentoring.isSpecialAbnormal(skill))
	        return true;

		return false;
	}

	@Override
	public void removeAllSkills()
	{
		_dontRewardSkills = true;

		super.removeAllSkills();

		_dontRewardSkills = false;
	}

	public void setLastMultisellBuyTime(long val)
	{
		_lastMultisellBuyTime = val;
	}

	public long getLastMultisellBuyTime()
	{
		return _lastMultisellBuyTime;
	}

	public void setLastEnchantItemTime(long val)
	{
		_lastEnchantItemTime = val;
	}

	public long getLastEnchantItemTime()
	{
		return _lastEnchantItemTime;
	}

	public void setLastAttributeItemTime(long val)
	{
		_lastAttributeItemTime = val;
	}

	public long getLastAttributeItemTime()
	{
		return _lastAttributeItemTime;
	}

	public void checkLevelUpReward(boolean onRestore)
	{
		int lastRewarded = getVarInt(LVL_UP_REWARD_VAR);
		int lastRewardedByClass = getVarInt(LVL_UP_REWARD_VAR + "_" + getActiveSubClass().getIndex());
		int playerLvl = getLevel();
		boolean rewarded = false;
		int clanPoints = 0;
	    long clanRewardCount = 0;
	    long mentorRewardCount = 0;
		if(playerLvl > lastRewarded)
		{
			for(int i = playerLvl; i > lastRewarded; i--)
			{
				TIntLongMap items = LevelUpRewardHolder.getInstance().getRewardData(i);
				if(items != null)
				{
					for(TIntLongIterator iterator = items.iterator(); iterator.hasNext();)
					{
						iterator.advance();
						getPremiumItemList().add(new PremiumItem(iterator.key(), iterator.value(), ""));
						rewarded = true;
					}
				}
				if(Mentoring.SIGN_OF_TUTOR.containsKey(i))
					mentorRewardCount += Mentoring.SIGN_OF_TUTOR.get(i);
			}
			setVar(LVL_UP_REWARD_VAR, playerLvl);
		}

		if(playerLvl > lastRewardedByClass)
		{
			for(int i = playerLvl; i > lastRewardedByClass; i--)
			{
				if(getClan() != null && getClan().getLevel() >= 5)
				{
					int earnedPoints = 0;
					switch(i)
					{
						case 40:
                            earnedPoints = 5;
                            break;
                        case 41:
                            earnedPoints = 5;
                            break;
                        case 42:
                            earnedPoints = 5;
                            break;
                        case 43:
                            earnedPoints = 6;
                            break;
                        case 44:
                            earnedPoints = 6;
                            break;
                        case 45:
                            earnedPoints = 6;
                            break;
                        case 46:
                            earnedPoints = 7;
                            break;
                        case 47:
                            earnedPoints = 7;
                            break;
                        case 48:
                            earnedPoints = 7;
                            break;
                        case 49:
                            earnedPoints = 8;
                            break;
                        case 50:
                            earnedPoints = 8;
                            break;
                        case 51:
                            earnedPoints = 8;
                            break;
                        case 52:
                            earnedPoints = 9;
                            break;
                        case 53:
                            earnedPoints = 9;
                            break;
                        case 54:
                            earnedPoints = 9;
                            break;
                        case 55:
                            earnedPoints = 10;
                            break;
                        case 56:
                            earnedPoints = 10;
                            break;
                        case 57:
                            earnedPoints = 10;
                            break;
                        case 58:
                            earnedPoints = 11;
                            break;
                        case 59:
                            earnedPoints = 11;
                            break;
                        case 60:
                            earnedPoints = 12;
                            break;
                        case 61:
                            earnedPoints = 12;
                            break;
                        case 62:
                            earnedPoints = 12;
                            break;
                        case 63:
                            earnedPoints = 13;
                            break;
                        case 64:
                            earnedPoints = 13;
                            break;
                        case 65:
                            earnedPoints = 14;
                            break;
                        case 66:
                            earnedPoints = 14;
                            break;
                        case 67:
                            earnedPoints = 14;
                            break;
                        case 68:
                            earnedPoints = 15;
                            break;
                        case 69:
                            earnedPoints = 15;
                            break;
                        case 70:
                            earnedPoints = 16;
                            break;
                        case 71:
                            earnedPoints = 16;
                            break;
                        case 72:
                            earnedPoints = 17;
                            break;
                        case 73:
                            earnedPoints = 17;
                            break;
                        case 74:
                            earnedPoints = 18;
                            break;
                        case 75:
                            earnedPoints = 18;
                            break;
                        case 76:
                            earnedPoints = 19;
                            break;
                        case 77:
                            earnedPoints = 19;
                            break;
                        case 78:
                            earnedPoints = 20;
                            break;
                        case 79:
                            earnedPoints = 20;
                            break;
                        case 80:
                            earnedPoints = 21;
                            break;
                        case 81:
                            earnedPoints = 21;
                            break;
                        case 82:
                            earnedPoints = 22;
                            break;
                        case 83:
                            earnedPoints = 22;
                            break;
                        case 84:
                            earnedPoints = 23;
                            break;
			            case 85:
			                earnedPoints = 90;
			                break;
			            case 86:
			                earnedPoints = 92;
			                break;
			            case 87:
			                earnedPoints = 94;
			                break;
			            case 88:
			                earnedPoints = 96;
			                break;
			            case 89:
			                earnedPoints = 99;
			                break;
			            case 90:
			                earnedPoints = 115;
			                break;
			            case 91:
			                earnedPoints = 118;
			                break;
			            case 92:
			                earnedPoints = 120;
			                break;
			            case 93:
			                earnedPoints = 123;
			                break;
			            case 94:
			                earnedPoints = 126;
			                break;
			            case 95:
			                earnedPoints = 180;
			                break;
			            case 96:
			                earnedPoints = 184;
			                break;
			            case 97:
			                earnedPoints = 188;
			                break;
			            case 98:
			                earnedPoints = 192;
			                break;
			            case 99:
			                earnedPoints = 745;
			                break;
					}
					 
					if(earnedPoints > 0 && (getPledgeType() != -1 || i <= 85))
						clanPoints += earnedPoints; 
					if(getPledgeType() != -1)
			            if(i >= 85)
			            	clanRewardCount++;
				}
			}

			setVar(LVL_UP_REWARD_VAR + "_" + getActiveSubClass().getIndex(), playerLvl);
		}

		if(rewarded)
			sendPacket(ExNotifyPremiumItem.STATIC);

		if(clanPoints > 0)
			getClan().incReputation(clanPoints, true, "ClanMemberLvlUp");
		
	    if(clanRewardCount > 0)
	        ItemFunctions.addItem(this, 37361, clanRewardCount, !onRestore); 
	    if(mentorRewardCount > 0)
	    {
	        int mentorId = getMenteeList().getMentor();
	        if(mentorId != 0 && isBaseClassActive())
	        {
	        	Player mentorPlayer = World.getPlayer(mentorId);
	        	if(mentorPlayer != null)
	        		Mentoring.sendMentorMail(mentorPlayer, 33804, mentorRewardCount); 
	        } 
	    } 
	}

	public void checkNobleSkills()
	{
		final boolean noble = isNoble();
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(noble ? this : null, AcquireType.NOBLESSE))
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
			if(skillEntry == null)
				continue;

			if(noble)
			{
				if(getSkillLevel(skillEntry.getId()) < skillEntry.getLevel())
					addSkill(skillEntry, true);
			}
			else
				removeSkill(skillEntry, true);
		}
	}

	public void checkHeroSkills()
	{
		final boolean hero = isHero() && isBaseClassActive();
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(hero ? this : null, AcquireType.HERO))
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
			if(skillEntry == null)
				continue;

			if(hero)
			{
				if(getSkillLevel(skillEntry.getId()) < skillEntry.getLevel())
					addSkill(skillEntry, true);
			}
			else
				removeSkill(skillEntry, true);
		}
	}

	public void activateHeroSkills(boolean activate)
	{
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.HERO))
		{
			Skill skill = SkillHolder.getInstance().getSkill(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(!activate)
				addUnActiveSkill(skill);
			else
				removeUnActiveSkill(skill);
		}
	}

	public void giveGMSkills()
	{
		if(!isGM())
			return;

		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(this, AcquireType.GM))
		{
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
			if(skillEntry == null)
				continue;

			if(getSkillLevel(skillEntry.getId()) < skillEntry.getLevel())
				addSkill(skillEntry, true);
		}
	}

	public static int _arenaIdForLogout = 0;

    public void setArenaIdForLogout(int arenaId)
    {
        _arenaIdForLogout = arenaId;
    }
    
    public int getArenaIdForLogout()
    {
        return _arenaIdForLogout;
    }
    
	private long _blockUntilTime = 0;

	public void setblockUntilTime(long time)
	{
		_blockUntilTime = time;
	}

	public long getblockUntilTime()
	{
		return _blockUntilTime;
	}

	
	public boolean isAllowAbilities()
	{
		return getLevel() >= SkillAcquireHolder.getInstance().getAbilitiesMinLevel();
	}

	public void sendAbilitiesInfo()
	{
		sendPacket(new ExAcquireAPSkillList(this));
	}

	public int getAllowAbilitiesPoints()
	{
		return Math.min(getMaxAbilitiesPoints(), Math.max(0, getLevel() - SkillAcquireHolder.getInstance().getAbilitiesMinLevel() + 1));
	}

	public static long getAbilitiesRefreshPrice()
	{
		return SkillAcquireHolder.getInstance().getAbilitiesRefreshPrice();
	}

	public static int getMaxAbilitiesPoints()
	{
		return SkillAcquireHolder.getInstance().getMaxAbilitiesPoints();
	}

	public int getUsedAbilitiesPoints() 
	{
		int result = 0;
		for(Skill skill : getLearnedAbilitiesSkills())
			result += skill.getLevel();
		return result;
	}

	public Collection<Skill> getLearnedAbilitiesSkills()
	{
		return SkillAcquireHolder.getInstance().getLearnedSkills(this, AcquireType.ABILITY);
	}

    public void checkAbilitiesSkills()
    {
        if(isAllowAbilities())
        {
            for(Skill skill : getLearnedAbilitiesSkills())
                removeUnActiveSkill(skill);
        }
        else
        {
            for(Skill skill : getLearnedAbilitiesSkills())
                addUnActiveSkill(skill);
        }
    }

	

	public int getWorldChatPoints()
	{
		if(hasPremiumAccount())
			return Math.max(0, Config.WORLD_CHAT_POINTS_PER_DAY_PA - _usedWorldChatPoints);

		return Math.max(0, Config.WORLD_CHAT_POINTS_PER_DAY - _usedWorldChatPoints);
	}

	public int getUsedWorldChatPoints()
	{
		return _usedWorldChatPoints;
	}

	public void setUsedWorldChatPoints(int value)
	{
		_usedWorldChatPoints = value;
	}

	public int getArmorSetEnchant()
	{
		return _armorSetEnchant;
	}

	public void setArmorSetEnchant(int value)
	{
		_armorSetEnchant = value;
	}

	public boolean hideHeadAccessories()
	{
		return _hideHeadAccessories;
	}

	public void setHideHeadAccessories(boolean value)
	{
		_hideHeadAccessories = value;
	}

	public ItemInstance getSynthesisItem1()
	{
		return _synthesisItem1;
	}

	public void setSynthesisItem1(ItemInstance value)
	{
		_synthesisItem1 = value;
	}

	public ItemInstance getSynthesisItem2()
	{
		return _synthesisItem2;
	}

	public void setSynthesisItem2(ItemInstance value)
	{
		_synthesisItem2 = value;
	}

	private static final int[] ADDITIONAL_SS_EFFECTS = new int[]
	{
		38859, 
		38931, 
		38858, 
		38930, 
		38857, 
		38929, 
		38856, 
		38928, 
		38855, 
		38927 
	};

	@Override
	public int getAdditionalVisualSSEffect()
	{
		for(int id : ADDITIONAL_SS_EFFECTS)
		{
			if(ItemFunctions.checkIsEquipped(this, -1, id, 0))
				return id;
		}
		return 0;
	}

	@Override
	public SkillEntry getAdditionalSSEffect(boolean spiritshot, boolean blessed)
	{
		if(!spiritshot)
		{
			
			if(ItemFunctions.checkIsEquipped(this, -1, 47688, 0))
		        return SkillHolder.getInstance().getSkillEntry(18715, 1);
			if(ItemFunctions.checkIsEquipped(this, -1, 38859, 0)) 
				return SkillHolder.getInstance().getSkillEntry(17817, 1);
			if(ItemFunctions.checkIsEquipped(this, -1, 38858, 0)) 
				return SkillHolder.getInstance().getSkillEntry(17816, 1);
			if(ItemFunctions.checkIsEquipped(this, -1, 38857, 0)) 
				return SkillHolder.getInstance().getSkillEntry(17815, 1);
			if(ItemFunctions.checkIsEquipped(this, -1, 38856, 0)) 
				return SkillHolder.getInstance().getSkillEntry(17814, 2);
			if(ItemFunctions.checkIsEquipped(this, -1, 38855, 0)) 
				return SkillHolder.getInstance().getSkillEntry(17814, 1);
		}
		else
		{
			if(!blessed)
			{
				
				if(ItemFunctions.checkIsEquipped(this, -1, 47689, 0))
			        return SkillHolder.getInstance().getSkillEntry(18718, 1);
				if(ItemFunctions.checkIsEquipped(this, -1, 38931, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17821, 1);
				if(ItemFunctions.checkIsEquipped(this, -1, 38930, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17820, 1);
				if(ItemFunctions.checkIsEquipped(this, -1, 38929, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17819, 1);
				if(ItemFunctions.checkIsEquipped(this, -1, 38928, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17818, 2);
				if(ItemFunctions.checkIsEquipped(this, -1, 38927, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17818, 1);
			}
			else
			{
				
				if(ItemFunctions.checkIsEquipped(this, -1, 47689, 0))
			        return SkillHolder.getInstance().getSkillEntry(18718, 2);
				if(ItemFunctions.checkIsEquipped(this, -1, 38931, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17821, 2);
				if(ItemFunctions.checkIsEquipped(this, -1, 38930, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17820, 2);
				if(ItemFunctions.checkIsEquipped(this, -1, 38929, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17819, 2);
				if(ItemFunctions.checkIsEquipped(this, -1, 38928, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17818, 4);
				if(ItemFunctions.checkIsEquipped(this, -1, 38927, 0)) 
					return SkillHolder.getInstance().getSkillEntry(17818, 3);
			}
		}

		return null;
	}

	
	public SkillEntry getKnownAlchemySkill(int id)
	{
		return _alchemySkills.get(id);
	}

	public int getAlchemySkillLevel(int id)
	{
		return getAlchemySkillLevel(id, -1);
	}

	public int getAlchemySkillLevel(int id, int def)
	{
		SkillEntry skillEntry = _alchemySkills.get(id);
		if(skillEntry == null)
			return def;
		return skillEntry.getLevel();
	}

	public Collection<SkillEntry> getAllAlchemySkills()
	{
		return _alchemySkills.values();
	}

	public SkillEntry[] getAllAlchemySkillsArray()
	{
		Collection<SkillEntry> skills = getAllAlchemySkills();
		return skills.toArray(new SkillEntry[skills.size()]);
	}

	private void restoreAlchemySkills()
	{
		_alchemySkills.clear();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=-100");
			statement.setInt(1, getObjectId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				final SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(rset.getInt("skill_id"), rset.getInt("skill_level"));
				if(skillEntry == null)
					continue;

				if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skillEntry.getTemplate(), AcquireType.ALCHEMY))
				{
					removeAlchemySkill(skillEntry, true);
					continue;
				}

				addAlchemySkill(skillEntry);
			}
		}
		catch(final Exception e)
		{
			_log.warn("Could not restore alchemy skills for player objId: " + getObjectId());
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public SkillEntry addAlchemySkill(SkillEntry skillEntry)
	{
		return addAlchemySkill(skillEntry, false);
	}

	public SkillEntry addAlchemySkill(SkillEntry skillEntry, boolean store)
	{
		if(skillEntry == null)
			return null;

		SkillEntry oldSkillEntry = _alchemySkills.get(skillEntry.getId());
		if(skillEntry.equals(oldSkillEntry))
			return oldSkillEntry;

		_alchemySkills.put(skillEntry.getId(), skillEntry);

		if(store)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,-100)");
				statement.setInt(1, getObjectId());
				statement.setInt(2, skillEntry.getId());
				statement.setInt(3, skillEntry.getLevel());
				statement.execute();
			}
			catch(final Exception e)
			{
				_log.error("Error could not store alchemy skill!", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
		return oldSkillEntry;
	}

	public SkillEntry removeAlchemySkill(SkillEntry skillEntry)
	{
		return removeAlchemySkill(skillEntry.getId(), false);
	}

	public SkillEntry removeAlchemySkill(SkillEntry skillEntry, boolean store)
	{
		return removeAlchemySkill(skillEntry.getId(), store);
	}

	public SkillEntry removeAlchemySkill(int id)
	{
		return removeAlchemySkill(id, false);
	}

	public SkillEntry removeAlchemySkill(int id, boolean store)
	{
		SkillEntry skillEntry = _alchemySkills.remove(id);

		if(skillEntry != null)
		{
			if(store)
			{
				Connection con = null;
				PreparedStatement statement = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? class_index=-100");
					statement.setInt(1, skillEntry.getId());
					statement.setInt(2, getObjectId());
					statement.execute();
				}
				catch(final Exception e)
				{
					_log.error("Could not delete alchemy skill!", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
			}
		}

		return skillEntry;
	}

	public int rewardAlchemySkills(boolean send)
	{
		int addedSkillsCount = 0;
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.ALCHEMY))
		{
			if(sl.isAutoGet() && sl.isFreeAutoGet(AcquireType.ALCHEMY))
			{
				SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
				if(skillEntry == null)
					continue;

				if(addAlchemySkill(skillEntry, true) == null)
					addedSkillsCount++;
			}
		}

		if(send && addedSkillsCount > 0)
			sendAlchemySkillList();

		return addedSkillsCount;
	}

	public void sendAlchemySkillList()
	{
		if(getRace() == Race.ERTHEIA && isBaseClassActive() && !getClassId().isOfLevel(ClassLevel.NONE))
			sendPacket(new ExAlchemySkillList(this));
	}
	

	public String getHWID()
	{
		return getNetConnection().getHWID();
	}

	public boolean isInAwayingMode()
	{
		return _awaying;
	}

	public void setAwayingMode(boolean awaying)
	{
		_awaying = awaying;
	}

	public double getMPCostDiff(Skill.SkillMagicType type)
	{
		double value = 0;
		switch(type)
		{
			case PHYSIC:
			{
				value = (calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
				break;
			}
			case MAGIC:
			{
				value = (calcStat(Stats.MP_MAGIC_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
				break;
			}
			case MUSIC:
			{
				value = (calcStat(Stats.MP_DANCE_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
				break;
			}
		}

		return value;
	}

	public int getExpertiseIndex()
	{
		return getSkillLevel(239, 0);
	}

	private final ConcurrentHashMap<ListenerHookType, CopyOnWriteArraySet<ListenerHook>> scriptHookTypeList = new ConcurrentHashMap<ListenerHookType, CopyOnWriteArraySet<ListenerHook>>();

	public void addListenerHook(ListenerHookType type, ListenerHook hook)
	{
		if(!scriptHookTypeList.containsKey(type))
		{
			CopyOnWriteArraySet<ListenerHook> hooks = new CopyOnWriteArraySet<ListenerHook>();
			hooks.add(hook);
			scriptHookTypeList.put(type, hooks);
		}
		else
		{
			CopyOnWriteArraySet<ListenerHook> hooks = scriptHookTypeList.get(type);
			hooks.add(hook);
		}
	}

	public void removeListenerHookType(ListenerHookType type, ListenerHook hook)
	{
		if(scriptHookTypeList.containsKey(type))
		{
			Set<ListenerHook> hooks = scriptHookTypeList.get(type);
			hooks.remove(hook);
		}
	}

	public Set<ListenerHook> getListenerHooks(ListenerHookType type)
	{
		Set<ListenerHook> hooks = scriptHookTypeList.get(type);
		if(hooks == null)
			hooks = Collections.emptySet();
		return hooks;
	}

	@Override
	public boolean isFakePlayer()
	{
		return getAI() != null && getAI().isFake();
	}

	public OptionDataTemplate addOptionData(OptionDataTemplate optionData)
	{
		if(optionData == null)
			return null;

		OptionDataTemplate oldOptionData = _options.get(optionData.getId());
		if(optionData == oldOptionData)
			return oldOptionData;

		_options.put(optionData.getId(), optionData);
		addTriggers(optionData);
		addStatFuncs(optionData.getStatFuncs(optionData));

		for(SkillEntry skillEntry : optionData.getSkills())
			addSkill(skillEntry);

		return oldOptionData;
	}

	public OptionDataTemplate removeOptionData(int id)
	{
		OptionDataTemplate oldOptionData = _options.remove(id);
		if(oldOptionData != null)
		{
			removeTriggers(oldOptionData);
			removeStatsOwner(oldOptionData);
			for(SkillEntry skillEntry : oldOptionData.getSkills())
				removeSkill(skillEntry);
		}
		return oldOptionData;
	}

	public long getReceivedExp()
	{
		return _receivedExp;
	}
	 
    public void onSuccessLucky()
    {
        broadcastPacket(new L2GameServerPacket[] { new MagicSkillUse(this, this, 18103, 1, 0, 0) });
        sendPacket(SystemMsg.LADY_LUCK_SMILES_UPON_YOU);
    }
    
	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		getAI().notifyEvent(CtrlEvent.EVT_SPAWN);
	}

	@Override
	protected void onDespawn()
	{
		getAI().notifyEvent(CtrlEvent.EVT_DESPAWN);
		super.onDespawn();
	}

	public void setQuestZoneId(int id)
	{
		_questZoneId = id;
	}

	public int getQuestZoneId()
	{
		return _questZoneId;
	}

	@Override
	protected void onAddSkill(SkillEntry skillEntry)
	{
		Skill skill = skillEntry.getTemplate();
		if(skill.isNecessaryToggle())
		{
			if(skill.isToggleGrouped() && skill.getToggleGroupId() > 0)
			{
				for(Abnormal abnormal : getAbnormalList())
				{
					if(abnormal.getSkill().isToggleGrouped() && abnormal.getSkill().getToggleGroupId() == skill.getToggleGroupId())
						return;
				}
			}
			forceUseSkill(skill, this);
		}
	}

	public void setCustomHero(int hours)
	{
		setHero(true);
		updatePledgeRank();
		broadcastPacket(new SocialActionPacket(getObjectId(), 20016));
		checkHeroSkills();
		int time = hours == -1 ? -1 : (int) (System.currentTimeMillis() / 1000) + hours * 60 * 60;
		CustomHeroDAO.getInstance().addCustomHero(getObjectId(), time);
	}

	public void setSelectedMultiClassId(ClassId classId)
	{
		_selectedMultiClassId = classId;
	}

	public ClassId getSelectedMultiClassId()
	{
		return _selectedMultiClassId;
	}

	@Override
	public int getPAtk(Creature target)
	{
		return (int) (super.getPAtk(target) * Config.PLAYER_P_ATK_MODIFIER);
	}

	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		return (int) (super.getMAtk(target, skill) * Config.PLAYER_M_ATK_MODIFIER);
	}

	@Override
	public void onZoneEnter(Zone zone)
	{
		if(zone.getType() == ZoneType.SIEGE)
		{
			for(CastleSiegeEvent siegeEvent : zone.getEvents(CastleSiegeEvent.class))
			{
				if(containsEvent(siegeEvent))
					siegeEvent.addVisitedParticipant(this);
			}
		}

		if(zone.getType() == ZoneType.buff_store && Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(getClassId().getId()))
			sendPacket(new SayPacket2(0, ChatType.BATTLEFIELD, getName(), new CustomMessage("l2s.gameserver.model.Player.EnterOfflineBufferZone").toString(this)));

		if(zone.getEnteringMessageId() != 0)
			sendPacket(new SystemMessage(zone.getEnteringMessageId()));

		if(zone.getTemplate().getBlockedActions() != null)
			blockActions(zone.getTemplate().getBlockedActions());

		if(zone.getType() == ZoneType.peace_zone)
		{
			DuelEvent duel = getEvent(DuelEvent.class);
			if(duel != null)
				duel.abortDuel(this);
		}
	}

	@Override
	public void onZoneLeave(Zone zone)
	{
		if(zone.getType() == ZoneType.buff_store && Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(getClassId().getId()))
			sendPacket(new SayPacket2(0, ChatType.BATTLEFIELD, getName(), new CustomMessage("l2s.gameserver.model.Player.ExitOfflineBufferZone").toString(this)));

		if(zone.getLeavingMessageId() != 0 && isPlayer())
			sendPacket(new SystemMessage(zone.getLeavingMessageId()));

		if(zone.getTemplate().getBlockedActions() != null)
			unblockActions(zone.getTemplate().getBlockedActions());
	}

	@Override
	public boolean hasBasicPropertyResist()
	{
		return getClassId().isAwaked();
	}
}