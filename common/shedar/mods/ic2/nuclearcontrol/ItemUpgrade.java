package shedar.mods.ic2.nuclearcontrol;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class ItemUpgrade extends Item
{
    public static final int DAMAGE_RANGE = 0;
    public static final int DAMAGE_COLOR = 1;

    public ItemUpgrade(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    public String getItemNameIS(ItemStack itemStack) 
    {
        int damage = itemStack.getItemDamage();
        switch (damage)
        {
        case DAMAGE_RANGE:
            return "item.ItemRangeUpgrade";
        case DAMAGE_COLOR:
            return "item.ItemColorUpgrade";
        default:
            return "";
        }
    }
    
    @Override
    public int getIconFromDamage(int damage)
    {
        switch (damage)
        {
        case DAMAGE_RANGE:
            return 66;
        case DAMAGE_COLOR:
            return 67;
        default:
            return 66;
        }
    }


    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList)
    {
        itemList.add(new ItemStack(par1, 1, DAMAGE_RANGE));
        itemList.add(new ItemStack(par1, 1, DAMAGE_COLOR));
    }
}
