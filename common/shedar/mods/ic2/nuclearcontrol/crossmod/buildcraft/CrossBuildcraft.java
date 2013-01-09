package shedar.mods.ic2.nuclearcontrol.crossmod.buildcraft;

import shedar.mods.ic2.nuclearcontrol.crossmod.data.EnergyStorageData;
import net.minecraft.tileentity.TileEntity;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;

public class CrossBuildcraft
{
    private boolean _isApiAvailable = false;
    
    public boolean isApiAvailable()
    {
        return _isApiAvailable;
    }
    
    public CrossBuildcraft()
    {
        try
        {
            Class.forName("buildcraft.api.power.IPowerReceptor", false, this.getClass().getClassLoader());
            _isApiAvailable = true;
        } catch (ClassNotFoundException e)
        {
            _isApiAvailable = false;
        }
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
