package net.minecraft.src.nuclearcontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.ItemCardBase;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

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
    
    public static List<PanelString> getStateMessage(int state)
    { 
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line= new PanelString();
        switch (state)
        {
            case ItemCardBase.STATE_OUT_OF_RANGE:
                line.textCenter = StatCollector.translateToLocal("msg.nc.InfoPanelOutOfRange"); 
                break;
            case ItemCardBase.STATE_INVALID_CARD:
                line.textCenter = StatCollector.translateToLocal("msg.nc.InfoPanelInvalidCard"); 
                break;
            case ItemCardBase.STATE_NO_TARGET:
                line.textCenter = StatCollector.translateToLocal("msg.nc.InfoPanelNoTarget"); 
                break;
        }
        result.add(line);
        return result;
    }    
    
}
