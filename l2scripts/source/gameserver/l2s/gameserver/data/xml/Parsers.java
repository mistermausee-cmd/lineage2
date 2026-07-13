package l2s.gameserver.data.xml;

import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.data.string.SkillNameHolder;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.xml.parser.AirshipDockParser;
import l2s.gameserver.data.xml.parser.AlchemyDataParser;
import l2s.gameserver.data.xml.parser.AppearanceStoneParser;
import l2s.gameserver.data.xml.parser.ArmorSetsParser;
import l2s.gameserver.data.xml.parser.AttendanceRewardParser;
import l2s.gameserver.data.xml.parser.AttributeStoneParser;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.BeautyShopParser;
import l2s.gameserver.data.xml.parser.BotReportPropertiesParser;
import l2s.gameserver.data.xml.parser.BuyListParser;
import l2s.gameserver.data.xml.parser.ClassDataParser;
import l2s.gameserver.data.xml.parser.CrystallizationDataParser;
import l2s.gameserver.data.xml.parser.CubicParser;
import l2s.gameserver.data.xml.parser.DailyMissionsParser;
import l2s.gameserver.data.xml.parser.DomainParser;
import l2s.gameserver.data.xml.parser.DoorParser;
import l2s.gameserver.data.xml.parser.EnchantItemParser;
import l2s.gameserver.data.xml.parser.EnchantStoneParser;
import l2s.gameserver.data.xml.parser.EnsoulParser;
import l2s.gameserver.data.xml.parser.EventParser;
import l2s.gameserver.data.xml.parser.ExperienceDataParser;
import l2s.gameserver.data.xml.parser.FactionDataParser;
import l2s.gameserver.data.xml.parser.FakeItemParser;
import l2s.gameserver.data.xml.parser.FakePlayersParser;
import l2s.gameserver.data.xml.parser.FishDataParser;
import l2s.gameserver.data.xml.parser.HennaParser;
import l2s.gameserver.data.xml.parser.HitCondBonusParser;
import l2s.gameserver.data.xml.parser.InstantZoneParser;
import l2s.gameserver.data.xml.parser.ItemParser;
import l2s.gameserver.data.xml.parser.JumpTracksParser;
import l2s.gameserver.data.xml.parser.KarmaIncreaseDataParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.LevelUpRewardParser;
import l2s.gameserver.data.xml.parser.LuckyGameParser;
import l2s.gameserver.data.xml.parser.MultiSellParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.data.xml.parser.OptionDataParser;
import l2s.gameserver.data.xml.parser.PetDataParser;
import l2s.gameserver.data.xml.parser.PetitionGroupParser;
import l2s.gameserver.data.xml.parser.PlayerTemplateParser;
import l2s.gameserver.data.xml.parser.PremiumAccountParser;
import l2s.gameserver.data.xml.parser.ProductDataParser;
import l2s.gameserver.data.xml.parser.RecipeParser;
import l2s.gameserver.data.xml.parser.ResidenceFunctionsParser;
import l2s.gameserver.data.xml.parser.ResidenceParser;
import l2s.gameserver.data.xml.parser.RestartPointParser;
import l2s.gameserver.data.xml.parser.ShuttleTemplateParser;
import l2s.gameserver.data.xml.parser.SkillAcquireParser;
import l2s.gameserver.data.xml.parser.SkillEnchantInfoParser;
import l2s.gameserver.data.xml.parser.SkillParser;
import l2s.gameserver.data.xml.parser.SoulCrystalParser;
import l2s.gameserver.data.xml.parser.SpawnParser;
import l2s.gameserver.data.xml.parser.StaticObjectParser;
import l2s.gameserver.data.xml.parser.StatuesSpawnParser;
import l2s.gameserver.data.xml.parser.SynthesisDataParser;
import l2s.gameserver.data.xml.parser.TransformTemplateParser;
import l2s.gameserver.data.xml.parser.VariationDataParser;
import l2s.gameserver.data.xml.parser.ZoneParser;
import l2s.gameserver.instancemanager.ReflectionManager;


public abstract class Parsers
{
    public static void parseAll()
    {
    	HtmCache.getInstance().reload();
        StringsHolder.getInstance().load();
        ItemNameHolder.getInstance().load();
        SkillNameHolder.getInstance().load();
        
        SkillEnchantInfoParser.getInstance().load();
        SkillParser.getInstance().load();
        OptionDataParser.getInstance().load();
        VariationDataParser.getInstance().load();
        ItemParser.getInstance().load();
	    EnsoulParser.getInstance().load();
        RecipeParser.getInstance().load();
        AlchemyDataParser.getInstance().load();
        CrystallizationDataParser.getInstance().load();
        SynthesisDataParser.getInstance().load();
        
	    ExperienceDataParser.getInstance().load();
	    BaseStatsBonusParser.getInstance().load();
	    BeautyShopParser.getInstance().load();
        LevelBonusParser.getInstance().load();
        KarmaIncreaseDataParser.getInstance().load();
        HitCondBonusParser.getInstance().load();
        PlayerTemplateParser.getInstance().load();
        ClassDataParser.getInstance().load();
        TransformTemplateParser.getInstance().load();
        NpcParser.getInstance().load();
        PetDataParser.getInstance().load();
        FactionDataParser.getInstance().load();

        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();

        StaticObjectParser.getInstance().load();
        DoorParser.getInstance().load();
        ZoneParser.getInstance().load();
        SpawnParser.getInstance().load();
        StatuesSpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();

        ReflectionManager.getInstance().init();
        
        AirshipDockParser.getInstance().load();
        SkillAcquireParser.getInstance().load();
        
	    ResidenceFunctionsParser.getInstance().load();
        ResidenceParser.getInstance().load();
        ShuttleTemplateParser.getInstance().load();
        EventParser.getInstance().load();
        
        CubicParser.getInstance().load();
        
        BuyListParser.getInstance().load();
        MultiSellParser.getInstance().load();
        ProductDataParser.getInstance().load();
	    AttendanceRewardParser.getInstance().load();
        
        HennaParser.getInstance().load();
        JumpTracksParser.getInstance().load();
        EnchantItemParser.getInstance().load();
        EnchantStoneParser.getInstance().load();
        AttributeStoneParser.getInstance().load();
        AppearanceStoneParser.getInstance().load();
        SoulCrystalParser.getInstance().load();
        ArmorSetsParser.getInstance().load();
        FishDataParser.getInstance().load();

        LevelUpRewardParser.getInstance().load();
        LuckyGameParser.getInstance().load();
        
	    PremiumAccountParser.getInstance().load();

        
        PetitionGroupParser.getInstance().load();
	    BotReportPropertiesParser.getInstance().load();

	    DailyMissionsParser.getInstance().load();

		
	    FakeItemParser.getInstance().load();
	    FakePlayersParser.getInstance().load();
    }
}