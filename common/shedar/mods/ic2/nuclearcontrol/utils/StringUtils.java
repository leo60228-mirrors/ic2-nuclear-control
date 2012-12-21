package shedar.mods.ic2.nuclearcontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.StringTranslate;

import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;

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
            return StringTranslate.getInstance().translateKeyFormat(resourceName, value);
        else
            return value;
    }

    public static String getFormatted(String resourceName, long value, boolean showLabels)
    {
        return getFormatted(resourceName, getFormatter().format(value), showLabels);
    }
    
    public static List<PanelString> getStateMessage(CardState state)
    { 
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line= new PanelString();
        switch (state)
        {
            case OUT_OF_RANGE:
                line.textCenter = StringTranslate.getInstance().translateKey("msg.nc.InfoPanelOutOfRange"); 
                break;
            case INVALID_CARD:
                line.textCenter = StringTranslate.getInstance().translateKey("msg.nc.InfoPanelInvalidCard"); 
                break;
            case NO_TARGET:
                line.textCenter = StringTranslate.getInstance().translateKey("msg.nc.InfoPanelNoTarget"); 
                break;
        }
        result.add(line);
        return result;
    }    
    
}
