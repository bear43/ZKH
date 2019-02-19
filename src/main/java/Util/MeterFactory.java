package Util;

import Model.Meter;

public class MeterFactory
{
    public static Meter createCopiedInstance(Meter meter)
    {
        return new Meter(meter);
    }

    public static Meter createFullCopiedInstance(Meter meter)
    {
        Meter m = createCopiedInstance(meter);
        m.setId(meter.getId());
        return m;
    }
}
