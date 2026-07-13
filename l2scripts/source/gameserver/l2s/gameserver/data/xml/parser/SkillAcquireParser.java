package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Element;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.stats.conditions.Condition;


public final class SkillAcquireParser extends StatParser<SkillAcquireHolder>
{
	private static final SkillAcquireParser _instance = new SkillAcquireParser();

	public static SkillAcquireParser getInstance()
	{
		return _instance;
	}

	protected SkillAcquireParser()
	{
		super(SkillAcquireHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/skill_tree/");
	}

	@Override
	public String getDTDFileName()
	{
		return "tree.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{

	    for(Iterator<Element> iterator = rootElement.elementIterator("certification_skill_tree"); iterator.hasNext();)
	    	getHolder().addAllCertificationLearns(parseSkillLearn(iterator.next()));
	      
	    for(Iterator<Element> iterator = rootElement.elementIterator("dual_certification_skill_tree"); iterator.hasNext();)
	    	getHolder().addAllDualCertificationLearns(parseSkillLearn(iterator.next()));
	      
		for(Iterator<Element> iterator = rootElement.elementIterator("sub_unit_skill_tree"); iterator.hasNext();)
			getHolder().addAllSubUnitLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("pledge_skill_tree"); iterator.hasNext();)
			getHolder().addAllPledgeLearns(parseSkillLearn(iterator.next()));

	    for(Iterator<Element> iterator = rootElement.elementIterator("collection_skill_tree"); iterator.hasNext();)
	    	getHolder().addAllCollectionLearns(parseSkillLearn(iterator.next()));
	    
	    for(Iterator<Element> iterator = rootElement.elementIterator("transformation_skill_tree"); iterator.hasNext();)
	    	getHolder().addAllTransformationLearns(parseSkillLearn(iterator.next()));
	    
		for(Iterator<Element> iterator = rootElement.elementIterator("fishing_skill_tree"); iterator.hasNext();)
			getHolder().addAllFishingLearns(parseSkillLearn(iterator.next()));

	    for(Iterator<Element> iterator = rootElement.elementIterator("noblesse_skill_tree"); iterator.hasNext();)
	    	getHolder().addAllNoblesseLearns(parseSkillLearn(iterator.next()));
	    
		for(Iterator<Element> iterator = rootElement.elementIterator("hero_skill_tree"); iterator.hasNext();)
			getHolder().addAllHeroLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("gm_skill_tree"); iterator.hasNext();)
			getHolder().addAllGMLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("custom_skill_tree"); iterator.hasNext();)
			getHolder().addAllCustomLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("transfer_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				if (classElement.attributeValue("id") != null)
				{
					int classId = Integer.parseInt(classElement.attributeValue("id"));
					Set<SkillLearn> learns = parseSkillLearn(classElement, ClassId.VALUES[classId].getClassLevel());
					getHolder().addAllTransferLearns(classId, learns);
				}
				if(classElement.attributeValue("level") != null)
				{
					ClassLevel classLevel = ClassLevel.valueOf(classElement.attributeValue("level").toUpperCase());
					Set<SkillLearn> learns = parseSkillLearn(classElement, classLevel);
					for(ClassId classId : ClassId.VALUES)
					{
						if(classId.isOfLevel(classLevel))
							getHolder().addAllTransferLearns(classId.getId(), learns);
					}
				}
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("normal_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				if (classElement.attributeValue("id") != null)
				{
					int classId = Integer.parseInt(classElement.attributeValue("id"));
					Set<SkillLearn> learns = parseSkillLearn(classElement, ClassId.VALUES[classId].getClassLevel());
					getHolder().addAllNormalSkillLearns(classId, learns);
				}
				if(classElement.attributeValue("level") != null)
				{
					ClassLevel classLevel = ClassLevel.valueOf(classElement.attributeValue("level").toUpperCase());
					Set<SkillLearn> learns = parseSkillLearn(classElement, classLevel);
					for(ClassId classId : ClassId.VALUES)
					{
						if(classId.isOfLevel(classLevel))
							getHolder().addAllNormalSkillLearns(classId.getId(), learns);
					}
				}
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("general_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			getHolder().addAllGeneralSkillLearns(-1, parseSkillLearn(nxt));
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				if(classElement.attributeValue("id") != null)
				{
					int classId = Integer.parseInt(classElement.attributeValue("id"));
					Set<SkillLearn> learns = parseSkillLearn(classElement, ClassId.VALUES[classId].getClassLevel());

					getHolder().addAllGeneralSkillLearns(classId, learns);
				}
				if(classElement.attributeValue("level") != null)
				{
					ClassLevel classLevel = ClassLevel.valueOf(classElement.attributeValue("level").toUpperCase());
					Set<SkillLearn> learns = parseSkillLearn(classElement, classLevel);
					for(ClassId classId : ClassId.VALUES)
					{
						if(classId.isOfLevel(classLevel))
							getHolder().addAllGeneralSkillLearns(classId.getId(), learns);
					}
				}
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("dual_class_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				if (classElement.attributeValue("id") != null)
				{
					int classId = Integer.parseInt(classElement.attributeValue("id"));
					Set<SkillLearn> learns = parseSkillLearn(classElement, ClassId.VALUES[classId].getClassLevel());
					getHolder().addAllDualClassSkillLearns(classId, learns);
				}
				if(classElement.attributeValue("level") != null)
				{
					ClassLevel classLevel = ClassLevel.valueOf(classElement.attributeValue("level").toUpperCase());
					Set<SkillLearn> learns = parseSkillLearn(classElement, classLevel);
					for(ClassId classId : ClassId.VALUES)
					{
						if(classId.isOfLevel(classLevel))
							getHolder().addAllDualClassSkillLearns(classId.getId(), learns);
					}
				}
			}
		}
		
		for(Iterator<Element> iterator = rootElement.elementIterator("awake_parent_skill_tree"); iterator.hasNext();)
	    {
			Element nxt = iterator.next();
			for(Iterator<Element> awakeClassIterator = nxt.elementIterator("awake_class"); awakeClassIterator.hasNext();)
			{
				Element awakeClassElement = awakeClassIterator.next();
				int awakeClassId = Integer.parseInt(awakeClassElement.attributeValue("id"));
				for(Iterator<Element> parentClassIterator = awakeClassElement.elementIterator("parent_class"); parentClassIterator.hasNext();)
				{
					Element parentClassElement = parentClassIterator.next();
					int parentClassId = Integer.parseInt(parentClassElement.attributeValue("id"));
					Set<SkillLearn> learns = parseSkillLearn(parentClassElement, ClassId.VALUES[parentClassId].getClassLevel());
					getHolder().addAllAwakeParentSkillLearns(awakeClassId, parentClassId, learns);
				}
			}
	    }

		for(Iterator<Element> iterator = rootElement.elementIterator("chaos_skill_tree"); iterator.hasNext();)
			getHolder().addAllChaosSkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("dual_chaos_skill_tree"); iterator.hasNext();)
			getHolder().addAllDualChaosSkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("ability_skill_tree"); iterator.hasNext();)
			getHolder().addAllAbilitySkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("abilities_settings"); iterator.hasNext();)
		{
			Element element = iterator.next();
		      
			getHolder().setAbilitiesMinLevel(Integer.parseInt(element.attributeValue("min_level")));
			getHolder().setMaxAbilitiesPoints(Integer.parseInt(element.attributeValue("maximun_points")));
			getHolder().setAbilitiesRefreshPrice(Long.parseLong(element.attributeValue("refresh_price")));
		}
		
		for(Iterator<Element> iterator = rootElement.elementIterator("alchemy_skill_tree"); iterator.hasNext();)
			getHolder().addAllAlchemySkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("honor_noblesse_skill_tree"); iterator.hasNext();)
			getHolder().addAllHonorNobleSkillLearns(parseSkillLearn(iterator.next()));
	}

	@Override
	protected void afterParseActions()
	{
		
		getHolder().initNormalSkillLearns();
		getHolder().initGeneralSkillLearns();
	}

	private Set<SkillLearn> parseSkillLearn(Element tree, ClassLevel classLevel)
	{
		Set<SkillLearn> skillLearns = new HashSet<SkillLearn>();
		for(Iterator<Element> iterator = tree.elementIterator("skill"); iterator.hasNext();)
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			int level = element.attributeValue("level") == null ? 1 : Integer.parseInt(element.attributeValue("level"));
			int cost = element.attributeValue("cost") == null ? 0 : Integer.parseInt(element.attributeValue("cost"));
			int min_level = element.attributeValue("min_level") == null ? 1 : Integer.parseInt(element.attributeValue("min_level"));
			int item_id = element.attributeValue("item_id") == null ? 0 : Integer.parseInt(element.attributeValue("item_id"));
			long item_count = element.attributeValue("item_count") == null ? 1 : Long.parseLong(element.attributeValue("item_count"));
			boolean auto_get = element.attributeValue("auto_get") == null ? true : Boolean.parseBoolean(element.attributeValue("auto_get"));
			Race race = element.attributeValue("race") == null ? null : Race.valueOf(element.attributeValue("race"));
			int dual_class_min_level = element.attributeValue("dual_class_min_level") == null ? 0 : Integer.parseInt(element.attributeValue("dual_class_min_level"));

			SkillLearn skillLearn = new SkillLearn(id, level, min_level, cost, item_id, item_count, auto_get, race, dual_class_min_level, classLevel);

			Condition condition = parseFirstCond(element);
			if(condition != null)
				skillLearn.addCondition(condition);

			skillLearns.add(skillLearn);
		}

		return skillLearns;
	}

	private Set<SkillLearn> parseSkillLearn(Element tree)
	{
		return parseSkillLearn(tree, ClassLevel.NONE);
	}

	@Override
	protected Object getTableValue(String name, int... arg)
	{
		return null;
	}
}