package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.Item;

public class ItemRangeUpgrade extends Item
{

    public ItemRangeUpgrade(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

}
