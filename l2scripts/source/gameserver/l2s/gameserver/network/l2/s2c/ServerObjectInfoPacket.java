package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.WinnerStatueInstance;


public class ServerObjectInfoPacket extends L2GameServerPacket
{
	private final int idTemplate;
	private final String name;
	private final boolean isAttackable;
	private final int x;
	private final int y;
	private final int z;
	private final int heading;
	private final double collisionRadius;
	private final double collisionHeight;
	private final int categoryId;
	private final int classId;
	private final int raceId;
	private final int sex;
	private final int hairstyle;
	private final int haircolor;
	private final int face;
	private final int necklace;
	private final int head;
	private final int rhand;
	private final int objectId;
	private final int lhand;
	private final int gloves;
	private final int chest;
	private final int pants;
	private final int boots;
	private final int cloak;
	private final int hair1;
	private final int hair2;

	public ServerObjectInfoPacket(WinnerStatueInstance statue, Creature actor)
	{
		objectId = statue.getObjectId();
		idTemplate = 1000000;
		name = statue.getTemplate().getName();
		isAttackable = statue.isAttackable(actor);
		x = statue.getX();
		y = statue.getY();
		z = statue.getZ();
		heading = statue.getHeading();
		collisionRadius = statue.getCollisionRadius();
		collisionHeight = statue.getCollisionHeight();
		categoryId = statue.getTemplate().getCategoryType().getClientId();
		classId = statue.getTemplate().getClassId();
		raceId = statue.getTemplate().getRaceId();
		sex = statue.getTemplate().getSex();
		hairstyle = statue.getTemplate().getHairStyle();
		haircolor = statue.getTemplate().getHairColor();
		face = statue.getTemplate().getFace();
		necklace = statue.getTemplate().getNecklace();
		head = statue.getTemplate().getHead();
		rhand = statue.getTemplate().getRhand();
		lhand = statue.getTemplate().getLhand();
		gloves = statue.getTemplate().getGloves();
		chest = statue.getTemplate().getChest();
		pants = statue.getTemplate().getPants();
		boots = statue.getTemplate().getBoots();
		cloak = statue.getTemplate().getCloak();
		hair1 = statue.getTemplate().getHair1();
		hair2 = statue.getTemplate().getHair2();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x92);
		writeD(objectId);
		writeD(idTemplate + 1000000);
		writeS(name); 
		writeD(isAttackable ? 1 : 0);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
		writeF(1.0); 
		writeF(1.0); 
		writeF(collisionRadius);
		writeF(collisionHeight);
		writeD(0); 
		writeD(0); 
		writeD(7); 
		writeD(0x00);

		writeD(categoryId);
		writeD(0x00);
		writeD(0x00); 
		writeD(0x00); 

		writeD(classId);
		writeD(raceId);
		writeD(sex);

		writeD(hairstyle);
		writeD(haircolor);
		writeD(face);

		writeD(necklace);
		writeD(head);
		writeD(rhand);
		writeD(lhand);
		writeD(gloves);
		writeD(chest);
		writeD(pants);
		writeD(boots);
		writeD(cloak);
		writeD(hair1);
		writeD(hair2);
	}
}