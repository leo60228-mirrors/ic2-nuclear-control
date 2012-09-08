package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.IReactorChamber;

public class ItemToolThermometer extends Item
{

    public ItemToolThermometer(int i, int j)
    {
        super(i);
        setIconIndex(j);
        setMaxDamage(102);
        setMaxStackSize(1);
        setTabToDisplayOn(CreativeTabs.tabMisc);
    }

    public boolean canTakeDamage(ItemStack itemstack, int i)
    {
        return true;
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,  float hitX, float hitY, float hitZ)
    {
        boolean isServer = player instanceof EntityPlayerMP;
        if(!isServer)
            return false;
        if (!canTakeDamage(itemstack, 2))
        {
            return false;
        }
        IReactor reactor = NuclearHelper.getReactorAt(world, x, y, z);
        if (reactor == null)
        {
        	IReactorChamber chamber = NuclearHelper.getReactorChamberAt(world, x, y, z);
        	if(chamber!=null)
        	{
        		reactor = chamber.getReactor();
        	}
        }
        if(reactor != null)
        {
            messagePlayer(player, reactor);
            damage(itemstack, 1, player);
        	return true;
        }
        return false;

    }

    protected void messagePlayer(EntityPlayer entityplayer, IReactor reactor)
    {
        int heat = reactor.getHeat();
    	NuclearNetworkHelper.chatMessage(entityplayer, "Thermo:" + heat);
    }

    protected void damage(ItemStack itemstack, int i, EntityPlayer entityplayer)
    {
        itemstack.damageItem(10, entityplayer);
    }

}
