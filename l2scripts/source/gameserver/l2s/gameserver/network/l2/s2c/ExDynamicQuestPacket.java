package l2s.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Collections;

import l2s.gameserver.model.quest.dynamic.DynamicQuestParticipant;
import l2s.gameserver.model.quest.dynamic.DynamicQuestTask;


public class ExDynamicQuestPacket extends L2GameServerPacket
{

	
	private final DynamicQuestInfo questInfo;

    

	public ExDynamicQuestPacket(DynamicQuestInfo questInfo)
	{
		this.questInfo = questInfo;
	}

	@Override
	protected void writeImpl()
	{
		writeC(questInfo.questType); 
		writeC(questInfo.subType); 
		writeD(questInfo.questId); 
		writeD(questInfo.step); 
		questInfo.write(this);

        
	}

	public static class DynamicQuestInfo
	{ 
		public int questType;
		public int questId;
		public int step;
		private int subType;

		public DynamicQuestInfo(int subType)
		{
			this.subType = subType;
		}

		public void write(ExDynamicQuestPacket packet)
		{
			
		}
	}

	public static class StartedQuest extends DynamicQuestInfo
	{
		private int state;
		private int remainingTime;
		private int participantsCount;
		private Collection<DynamicQuestTask> tasks = Collections.emptyList();

		public StartedQuest(int state, int remainingTime, int participantsCount, Collection<DynamicQuestTask> tasks)
		{
			super(2);
			this.state = state;
			this.remainingTime = remainingTime;
			this.participantsCount = participantsCount;
			this.tasks = tasks;
		}

		@Override
		public void write(ExDynamicQuestPacket packet)
		{
			packet.writeC(state);
			packet.writeD(remainingTime);
			if(questType == 1)  
			{
				packet.writeD(participantsCount);
			}
			packet.writeD(tasks.size());
			for(DynamicQuestTask task : tasks)
			{
				packet.writeD(task.taskId);
				packet.writeD(task.getCurrentPoints());
				packet.writeD(task.getMaxPoints());
			}
		}
	}

	public static class ScoreBoardInfo extends DynamicQuestInfo
	{
		private final int remainingTime;
		private final int friendsCount;
		private final Collection<DynamicQuestParticipant> participants;

		public ScoreBoardInfo(int remainingTime, int friendsCount, Collection<DynamicQuestParticipant> participants)
		{
			super(3);
			this.remainingTime = remainingTime;
			this.friendsCount = friendsCount;
			this.participants = participants;
		}

		@Override
		public void write(ExDynamicQuestPacket packet)
		{
			if(questType == 1)
			{
				packet.writeD(remainingTime); 
				packet.writeD(friendsCount); 
				packet.writeD(participants.size()); 
				for(DynamicQuestParticipant participant : participants)
				{
					packet.writeS(participant.getName());
					packet.writeD(participant.getCurrentPoints());
					packet.writeD(participant.getAdditionalPoints()); 
					packet.writeD(participant.getCurrentPoints() + participant.getAdditionalPoints()); 
				}
			}
			else
			{
				packet.writeD(participants.size()); 
				for(DynamicQuestParticipant participant : participants)
				{
					packet.writeS(participant.getName()); 
				}
			}
		}
	}
}