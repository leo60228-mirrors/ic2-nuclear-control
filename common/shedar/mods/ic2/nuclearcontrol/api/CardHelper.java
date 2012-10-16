package shedar.mods.ic2.nuclearcontrol.api;

import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.FMLLog;

public final class CardHelper
{
    private static final String className = "shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl";
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ICardWrapper getWrapper(ItemStack card)
    {
        try
        {
            Class c = Class.forName(className);
            return (ICardWrapper)c.getConstructor(ItemStack.class).newInstance(card);
        } catch (Exception e)
        {
            FMLLog.severe("Can't crate Nuclear Control Card Wrapper: %s", e.toString());
        } 
        return null;
        
    }

}
