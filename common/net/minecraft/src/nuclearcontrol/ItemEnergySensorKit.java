package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ic2.api.IEnergyStorage;

public class ItemEnergySensorKit extends Item implements ITextureProvider
{

    public ItemEnergySensorKit(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

    public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int l)
    {
        if(entityPlayer==null)
            return false;
        IEnergyStorage storage = EnergyStorageHelper.getStorageAt(world, x, y, z);
        if (storage == null)
        {
            return false;
        }
        ItemStack sensorLocationCard = new ItemStack(mod_IC2NuclearControl.itemEnergySensorLocationCard, 1, 0);
        ItemSensorLocationCardBase.setCoordinates(sensorLocationCard, x, y, z);
        entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = sensorLocationCard;
    	if(!world.isRemote)
    	{
    	    mod_IC2NuclearControl.chatMessage(entityPlayer, IC2NuclearControl.MSG_PREFIX+"SensorKit");
    	}
    	return true;
    }

}
