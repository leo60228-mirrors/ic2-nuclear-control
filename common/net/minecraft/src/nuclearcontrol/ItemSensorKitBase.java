package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemSensorKitBase extends Item implements ITextureProvider
{
    private Item item;

    public ItemSensorKitBase(int i, int iconIndex, Item item)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
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

    
    public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int l)
    {
        if(entityPlayer==null)
            return false;
        ChunkCoordinates position = getTargetCoordinates(world, x, y, z);
        
        if(position != null)
        {
            ItemStack sensorLocationCard = new ItemStack(item, 1, 0);
            setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
            entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = sensorLocationCard;
        	if(!world.isRemote)
        	{
        	    mod_IC2NuclearControl.chatMessage(entityPlayer, IC2NuclearControl.MSG_PREFIX+"SensorKit");
        	}
        	return true;
        }
        return false;
    }

}
