package shedar.mods.ic2.nuclearcontrol.crossmod.buildcraft;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.ITankContainer;
import shedar.mods.ic2.nuclearcontrol.crossmod.data.EnergyStorageData;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;

public class CrossBuildcraft
{
    private boolean _isApiAvailable = false;
    
    public boolean isApiAvailable()
    {
        return _isApiAvailable;
    }
    
    @SuppressWarnings("unchecked")
    public void registerTileEntity()
    {
        try
        {
            GameRegistry.registerTileEntity((Class<? extends TileEntity>)Class.forName("shedar.mods.ic2.nuclearcontrol.crossmod.buildcraft.TileEntityAverageCounterBC"), "IC2NCAverageCounterBC");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    public CrossBuildcraft()
    {
        try
        {
            Class.forName("buildcraft.api.power.IPowerReceptor", false, this.getClass().getClassLoader());
            _isApiAvailable = true;
            registerTileEntity();
        } catch (ClassNotFoundException e)
        {
            _isApiAvailable = false;
        }
    }
    
    public boolean isTankContainer(Object obj)
    {
        return _isApiAvailable && obj instanceof ITankContainer;
    }
    
    public TileEntityAverageCounter getAverageCounter()
    {
        if(_isApiAvailable)
        {
            try
            {
                //return new TileEntityAverageCounterBC();
                return (TileEntityAverageCounter)Class.forName("shedar.mods.ic2.nuclearcontrol.crossmod.buildcraft.TileEntityAverageCounterBC").newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public EnergyStorageData getStorageData(TileEntity target)
    {
        if(!_isApiAvailable || target == null)
            return null;
        IPowerProvider provider = null;
        if(target instanceof IPowerProvider)
        {
            provider = (IPowerProvider)provider;
        }
        else if (target instanceof IPowerReceptor)
        {
            provider = ((IPowerReceptor)target).getPowerProvider();
        }
        if(provider == null)
            return null;
        EnergyStorageData result = new EnergyStorageData();
        result.capacity = provider.getMaxEnergyStored();
        result.stored = (int)provider.getEnergyStored();
        result.units = "MJ";
        result.type = EnergyStorageData.TARGET_TYPE_BC;
        return result;
    }

}
