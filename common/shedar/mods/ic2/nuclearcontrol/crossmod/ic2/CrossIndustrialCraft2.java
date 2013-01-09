package shedar.mods.ic2.nuclearcontrol.crossmod.ic2;

import ic2.api.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import shedar.mods.ic2.nuclearcontrol.crossmod.data.EnergyStorageData;

public class CrossIndustrialCraft2
{
    private boolean _isApiAvailable = false;
    
    public boolean isApiAvailable()
    {
        return _isApiAvailable;
    }
    
    public CrossIndustrialCraft2()
    {
        try
        {
            Class.forName("ic2.api.IEnergyStorage", false, this.getClass().getClassLoader());
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
        if(target instanceof IEnergyStorage)
        {
            IEnergyStorage storage = (IEnergyStorage)target;
            EnergyStorageData result = new EnergyStorageData();
            result.capacity = storage.getCapacity();
            result.stored = storage.getStored();
            result.units = "EU";
            result.type = EnergyStorageData.TARGET_TYPE_IC2;
            return result;
        }
        return null;
    }    
}
