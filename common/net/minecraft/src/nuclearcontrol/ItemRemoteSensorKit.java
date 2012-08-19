package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.IReactorChamber;

public class ItemRemoteSensorKit extends Item implements ITextureProvider
{

    public ItemRemoteSensorKit(int i, int iconIndex)
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
            ChunkCoordinates position = reactor.getPosition();
            ItemStack sensorLocationCard = new ItemStack(mod_IC2NuclearControl.itemSensorLocationCard, 1, 0);
            ItemSensorLocationCardBase.setCoordinates(sensorLocationCard, position.posX, position.posY, position.posZ);
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
