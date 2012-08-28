package net.minecraft.src.nuclearcontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.minecraft.src.StatCollector;

public class StringUtils
{
    private static DecimalFormat formatter = null;
    
    private static  DecimalFormat getFormatter()
    {
        if(formatter == null)
        {
            DecimalFormat lFormatter = new DecimalFormat("#,###.###");
            DecimalFormatSymbols smb = new DecimalFormatSymbols();
            smb.setGroupingSeparator(' ');
            lFormatter.setDecimalFormatSymbols(smb);
            formatter = lFormatter;
        }
        return formatter;
    }
    
    public static String getFormatted(String resourceName, String value, boolean showLabels)
    {
        if(showLabels)
            return String.format(StatCollector.translateToLocal(resourceName), value);
        else
            return value;
    }

    public static String getFormatted(String resourceName, long value, boolean showLabels)
    {
        return getFormatted(resourceName, getFormatter().format(value), showLabels);
    }
    
}
