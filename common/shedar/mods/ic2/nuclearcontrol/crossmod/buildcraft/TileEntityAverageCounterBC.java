package shedar.mods.ic2.nuclearcontrol.crossmod.buildcraft;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.Direction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;

public class TileEntityAverageCounterBC extends TileEntityAverageCounter implements IPowerReceptor
{
    private static final int MAX_SEND = 100;

    private IPowerProvider powerProvider;

    public TileEntityAverageCounterBC()
    {
        super();
        powerProvider = PowerFramework.currentFramework.createPowerProvider();
        powerProvider.configure(50, 2, 1000, 1, 1000);
        powerProvider.configurePowerPerdition(1, 100);
    }
    
    @Override
    public void setPowerProvider(IPowerProvider provider)
    {
        powerProvider = provider;
    }

    @Override
    public IPowerProvider getPowerProvider()
    {
        return powerProvider;
    }

    @Override
    public void doWork()
    {
        // do nothing
    }
    
    @Override
    public void updateEntity() 
    {
        super.updateEntity();
        if (worldObj.isRemote)
            return;
        Direction[] directions = Direction.values();
        for (Direction apiDirection : directions) 
        {
            ForgeDirection direction = apiDirection.toForgeDirection();
            if(direction.ordinal() == getFacing())
            {
                continue;
            }
            int x = direction.offsetX + xCoord;
            int y = direction.offsetY + yCoord;
            int z = direction.offsetZ + zCoord;
            TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
            if (tile!=null && tile instanceof IPowerReceptor) {
                IPowerProvider receptor = ((IPowerReceptor) tile).getPowerProvider();
                if(receptor!=null)
                {
                    float powerRequested = (float)((IPowerReceptor)tile).powerRequest();
                    
                    if(powerRequested > 0.0F) 
                    {
                       if(tile instanceof IEnergySink)
                       {
                           if(!((IEnergySink)tile).acceptsEnergyFrom(this, apiDirection.getInverse()))
                               continue;
                       }
                       float adjustedEnergyRequest = Math.min(powerRequested, (float)receptor.getMaxEnergyStored() - receptor.getEnergyStored());
                       float energyMax = Math.min(adjustedEnergyRequest, MAX_SEND);
                       float energy = powerProvider.useEnergy(0, energyMax, true);
                       receptor.receiveEnergy(energy, direction.getOpposite());
                       data[index] += energy;
                       if(energy>0)
                           setPowerType(POWER_TYPE_MJ);
                    }
                }
            }
        }
    }

    @Override
    public int powerRequest()
    {
        return getPowerProvider().getMaxEnergyReceived();
    }
    
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        powerProvider.writeToNBT(nbttagcompound);
    }    

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        powerProvider.readFromNBT(nbttagcompound);
    }
}
