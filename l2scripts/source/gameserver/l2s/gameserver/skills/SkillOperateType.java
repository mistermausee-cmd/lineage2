package l2s.gameserver.skills;


public enum SkillOperateType
{
    
    A1,

    
    A2,

    
    A3,

    
    A4,

    
    A5,

    
    A6,

    
    CA1,

    
    CA2,

    
    CA5,

    
    DA1,

    
    DA2,

    
    DA3,

    
    P,

    
    T,

    
    TG,

    
    AU;

    
    public boolean isActive()
    {
        switch (this)
        {
            case A1:
            case A2:
            case A3:
            case A4:
            case A5:
            case A6:
            case CA1:
            case CA5:
            case DA1:
            case DA2:
                return true;
            default:
                return false;
        }
    }

    
    public boolean isContinuous()
    {
        switch (this)
        {
            case A2:
            case A4:
            case A5:
            case A6:
            case DA2:
                return true;
            default:
                return false;
        }
    }

    
    public boolean isSelfContinuous()
    {
        return (this == A3);
    }

    
    public boolean isPassive()
    {
        return (this == P);
    }

    
    public boolean isToggle()
    {
        return (this == T) || (this == TG) || (this == AU);
    }

    public boolean isToggleGrouped()
    {
        return (this == TG) || (this == AU);
    }

    
    public boolean isAura()
    {
        return (this == A5) || (this == A6) || (this == AU);
    }

    
    public boolean isHidingMesseges()
    {
        return (this == A5) || (this == A6) || (this == TG);
    }

    
    public boolean isNotBroadcastable()
    {
        return (this == A5) || (this == A6) || (this == AU) || (this == TG);
    }

    
    public boolean isChanneling()
    {
        switch (this)
        {
            case CA1:
            case CA2:
            case CA5:
                return true;
            default:
                return false;
        }
    }

    
    public boolean isSynergy()
    {
        return (this == A6);
    }
}