package Util;

public enum MeterType
{
    GAS("Газ"),
    ELECTRICITY("Электричество"),
    COLD_WATER("Холодная вода"),
    HOT_WATER("Горячая вода");

    private String title;

    MeterType(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public static MeterType getTypeByTitle(String title) throws Exception
    {
        for(MeterType type : MeterType.values())
            if(type.getTitle().equals(title))
                return type;
            throw new Exception("No such meter " + title + "!");
    }

    @Override
    public String toString()
    {
        return title;
    }
}
