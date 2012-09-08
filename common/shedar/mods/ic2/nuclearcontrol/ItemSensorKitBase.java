package shedar.mods.ic2.nuclearcontrol;

import shedar.mods.ic2.nuclearcontrol.utils.ItemStackUtils;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public abstract class ItemSensorKitBase extends Item 
{
    private Item item;

    public ItemSensorKitBase(int i, int iconIndex, Item item)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
        setTabToDisplayOn(CreativeTabs.tabMisc);
        this.item = item;
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }
    
    abstract protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z);

    private void setCoordinates(ItemStack itemStack, int x, int y, int z)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        nbtTagCompound.setInteger("x", x);
        nbtTagCompound.setInteger("y", y);
        nbtTagCompound.setInteger("z", z);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(player == null)
            return false;
        boolean isServer = player instanceof EntityPlayerMP;
        if(!isServer)
            return false;
        ChunkCoordinates position = getTargetCoordinates(world, x, y, z);
        
        if(position != null)
        {
            ItemStack sensorLocationCard = new ItemStack(item, 1, 0);
            setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
            player.inventory.mainInventory[player.inventory.currentItem] = sensorLocationCard;
        	if(!world.isRemote)
        	{
        	    NuclearNetworkHelper.chatMessage(player, "SensorKit");
        	}
        	return true;
        }
        return false;
    }

}
