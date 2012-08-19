package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ic2.api.ElectricItem;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.IReactorChamber;

public class ItemToolThermometer extends Item implements ITextureProvider
{

    public ThermometerVersion thermometerVersion;

    public ItemToolThermometer(int i, int j, ThermometerVersion thermometerversion)
    {
        super(i);
        setIconIndex(j);
        setMaxDamage(102);
        setMaxStackSize(1);
        thermometerVersion = thermometerversion;
    }

    public boolean canTakeDamage(ItemStack itemstack, int i)
    {
        return true;
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l)
    {
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
        	if(!world.isRemote)
        	{
                messagePlayer(entityplayer, reactor);
                damage(itemstack, 1, entityplayer);
        	}
        	return true;
        }
        return false;

    }

    public void messagePlayer(EntityPlayer entityplayer, IReactor reactor)
    {
        int heat = reactor.getHeat();
        switch(thermometerVersion)
        {
        case ANALOG: 
        	mod_IC2NuclearControl.chatMessage(entityplayer, IC2NuclearControl.MSG_PREFIX+"Thermo:" + heat);
            break;

        case DIGITAL: 
            int maxHeat = reactor.getMaxHeat();
            mod_IC2NuclearControl.chatMessage(entityplayer, 
                    IC2NuclearControl.MSG_PREFIX+"ThermoDigital:" + heat + ":" +((maxHeat * 50) / 100) + 
            		":"+ ((maxHeat * 85) / 100));
            break;
        }
    }

    public void damage(ItemStack itemstack, int i, EntityPlayer entityplayer)
    {
        switch(thermometerVersion)
        {
        case ANALOG: 
            itemstack.damageItem(10, entityplayer);
            break;

        case DIGITAL: 
            ElectricItem.use(itemstack, 50*i, entityplayer);
            break;
        }
    }

}
