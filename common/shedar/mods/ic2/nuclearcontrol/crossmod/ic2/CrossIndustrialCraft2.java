package shedar.mods.ic2.nuclearcontrol.crossmod.ic2;

import ic2.api.item.Items;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import shedar.mods.ic2.nuclearcontrol.crossmod.data.EnergyStorageData;

public class CrossIndustrialCraft2
{

    private int _IC2WrenchId;
    private int _IC2ElectricWrenchId;
    
    
    private boolean _isApiAvailable = false;
    private boolean _isIdInitialized = false;
    
    private int _uraniumId1;
    private int _uraniumId2;
    private int _uraniumId4;
    
    private void initIds()
    {
        if(!_isApiAvailable || _isIdInitialized)
            return;
        _uraniumId1 = Items.getItem("reactorUraniumSimple").itemID;
        _uraniumId2 = Items.getItem("reactorUraniumDual").itemID;
        _uraniumId4 = Items.getItem("reactorUraniumQuad").itemID;
        _IC2WrenchId = Items.getItem("wrench").itemID;
        _IC2ElectricWrenchId = Items.getItem("electricWrench").itemID;
        _isIdInitialized = true;
    }
    
    public boolean isApiAvailable()
    {
        return _isApiAvailable;
    }
    
    public boolean isWrench(ItemStack itemStack)
    {
        initIds();
        return _isApiAvailable && (itemStack.itemID == _IC2WrenchId || itemStack.itemID==_IC2ElectricWrenchId);
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
    
    public int getNuclearCellTimeLeft(ItemStack stack)
    {
        if(!_isApiAvailable || stack == null)
            return -1;
        initIds();
        if(stack.itemID == _uraniumId1 || stack.itemID == _uraniumId2 || stack.itemID == _uraniumId4)
        {
            return stack.getMaxDamage() - stack.getItemDamage();
        }
        return -1;
    }
    
}
