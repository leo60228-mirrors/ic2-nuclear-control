package shedar.mods.ic2.nuclearcontrol.items;

import java.util.List;

import shedar.mods.ic2.nuclearcontrol.utils.TextureResolver;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;


public class ItemUpgrade extends Item
{
    public static final int DAMAGE_RANGE = 0;
    public static final int DAMAGE_COLOR = 1;
    
    private static final String TEXTURE_RANGE = "upgradeRange"; 
    private static final String TEXTURE_COLOR = "upgradeColor"; 
    
    private Icon iconRange;
    private Icon iconColor;

    public ItemUpgrade(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        iconRange = iconRegister.registerIcon(TextureResolver.getItemTexture(TEXTURE_RANGE));
        iconColor = iconRegister.registerIcon(TextureResolver.getItemTexture(TEXTURE_COLOR));
    }    
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) 
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
    public Icon getIconFromDamage(int damage)
    {
        switch (damage)
        {
        case DAMAGE_RANGE:
            return iconRange;
        case DAMAGE_COLOR:
            return iconColor;
        default:
            return iconRange;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList)
    {
        itemList.add(new ItemStack(par1, 1, DAMAGE_RANGE));
        itemList.add(new ItemStack(par1, 1, DAMAGE_COLOR));
    }
}
