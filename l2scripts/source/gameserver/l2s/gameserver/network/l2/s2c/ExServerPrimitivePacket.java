package l2s.gameserver.network.l2.s2c;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class ExServerPrimitivePacket extends L2GameServerPacket
{
	private final String _name;
	private final int _x;
	private final int _y;
	private final int _z;
	private final List<Point> _points = new ArrayList<>();
	private final List<Line> _lines = new ArrayList<>();
	
	
	public ExServerPrimitivePacket(String name, int x, int y, int z)
	{
		_name = name;
		_x = x;
		_y = y;
		_z = z;
	}
	
	
	public void addPoint(String name, int color, boolean isNameColored, int x, int y, int z)
	{
		_points.add(new Point(name, color, isNameColored, x, y, z));
	}
	
	
	public void addPoint(int color, int x, int y, int z)
	{
		addPoint("", color, false, x, y, z);
	}
	
	
	public void addPoint(String name, Color color, boolean isNameColored, int x, int y, int z)
	{
		addPoint(name, color.getRGB(), isNameColored, x, y, z);
	}
	
	
	public void addPoint(Color color, int x, int y, int z)
	{
		addPoint("", color, false, x, y, z);
	}
	
	
	public void addLine(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
	{
		_lines.add(new Line(name, color, isNameColored, x, y, z, x2, y2, z2));
	}
	
	
	public void addLine(int color, int x, int y, int z, int x2, int y2, int z2)
	{
		addLine("", color, false, x, y, z, x2, y2, z2);
	}
	
	
	public void addLine(String name, Color color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
	{
		addLine(name, color.getRGB(), isNameColored, x, y, z, x2, y2, z2);
	}
	
	
	public void addLine(Color color, int x, int y, int z, int x2, int y2, int z2)
	{
		addLine("", color, false, x, y, z, x2, y2, z2);
	}

	@Override
	protected void writeImpl()
	{
		
		writeS(_name);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(65535); 
		writeD(65535); 

		writeD(_points.size() + _lines.size());

		for (Point point : _points)
		{
			writeC(1); 
			writeS(point.getName());
			int color = point.getColor();
			writeD((color >> 16) & 0xFF); 
			writeD((color >> 8) & 0xFF); 
			writeD(color & 0xFF); 
			writeD(point.isNameColored() ? 1 : 0);
			writeD(point.getX());
			writeD(point.getY());
			writeD(point.getZ());
		}
		
		for (Line line : _lines)
		{
			writeC(2); 
			writeS(line.getName());
			int color = line.getColor();
			writeD((color >> 16) & 0xFF); 
			writeD((color >> 8) & 0xFF); 
			writeD(color & 0xFF); 
			writeD(line.isNameColored() ? 1 : 0);
			writeD(line.getX());
			writeD(line.getY());
			writeD(line.getZ());
			writeD(line.getX2());
			writeD(line.getY2());
			writeD(line.getZ2());
		}
	}
	
	private static class Point
	{
		private final String _name;
		private final int _color;
		private final boolean _isNameColored;
		private final int _x;
		private final int _y;
		private final int _z;
		
		public Point(String name, int color, boolean isNameColored, int x, int y, int z)
		{
			_name = name;
			_color = color;
			_isNameColored = isNameColored;
			_x = x;
			_y = y;
			_z = z;
		}
		
		
		public String getName()
		{
			return _name;
		}
		
		
		public int getColor()
		{
			return _color;
		}
		
		
		public boolean isNameColored()
		{
			return _isNameColored;
		}
		
		
		public int getX()
		{
			return _x;
		}
		
		
		public int getY()
		{
			return _y;
		}
		
		
		public int getZ()
		{
			return _z;
		}
	}
	
	private static class Line extends Point
	{
		private final int _x2;
		private final int _y2;
		private final int _z2;
		
		public Line(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2)
		{
			super(name, color, isNameColored, x, y, z);
			_x2 = x2;
			_y2 = y2;
			_z2 = z2;
		}
		
		
		public int getX2()
		{
			return _x2;
		}
		
		
		public int getY2()
		{
			return _y2;
		}
		
		
		public int getZ2()
		{
			return _z2;
		}
	}
}