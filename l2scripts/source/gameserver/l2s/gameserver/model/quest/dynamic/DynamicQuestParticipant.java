package l2s.gameserver.model.quest.dynamic;


public class DynamicQuestParticipant implements Comparable<DynamicQuestParticipant>
{
	private String name;
	private int currentPoints;
	private int additionalPoints;

	public DynamicQuestParticipant(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public int getCurrentPoints()
	{
		return currentPoints;
	}

	public int getAdditionalPoints()
	{
		return additionalPoints;
	}

	public void setAdditionalPoints(int additionalPoints)
	{
		this.additionalPoints = additionalPoints;
	}

	public void increaseCurrentPoints(int points)
	{
		this.currentPoints += points;
	}

	@Override
	public int compareTo(DynamicQuestParticipant participant)
	{
		if(getCurrentPoints() + getAdditionalPoints() > participant.getCurrentPoints() + participant.getAdditionalPoints())
		{
			return 1;
		}
		else if(getCurrentPoints() + getAdditionalPoints() > participant.getCurrentPoints() + participant.getAdditionalPoints())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
}
