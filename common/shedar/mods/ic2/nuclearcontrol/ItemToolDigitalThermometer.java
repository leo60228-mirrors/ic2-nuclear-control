package shedar.mods.ic2.nuclearcontrol;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ic2.api.ElectricItem;
import net.minecraft.src.ic2.api.IElectricItem;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemToolDigitalThermometer extends ItemToolThermometer
    implements IElectricItem
{

    public int tier;
    public int ratio;
    public int transfer;

    public ItemToolDigitalThermometer(int i, int j, int k, int l, int i1)
    {
        super(i, j);
        setMaxDamage(101);
        tier = k;
        ratio = l;
        transfer = i1;
        
    }

    public boolean canTakeDamage(ItemStack itemstack, int i)
    {
        i *= 50;
        return ElectricItem.discharge(itemstack, i, 0x7fffffff, true, true) == i;
    }
    
    @Override
    protected void messagePlayer(EntityPlayer entityplayer, net.minecraft.src.ic2.api.IReactor reactor) {
        int heat = reactor.getHeat();
        int maxHeat = reactor.getMaxHeat();
        NuclearNetworkHelper.chatMessage(entityplayer, 
                "ThermoDigital:" + heat + ":" +((maxHeat * 50) / 100) + 
                ":"+ ((maxHeat * 85) / 100));
    }
    
    @Override
    protected void damage(ItemStack itemstack, int i, EntityPlayer entityplayer)
    {
        ElectricItem.use(itemstack, 50 * i, entityplayer);
    }
    
	@Override
	public boolean canProvideEnergy() {
		return false;
	}

	@Override
	public int getChargedItemId() {
		return shiftedIndex;
	}

	@Override
	public int getEmptyItemId() {
		return shiftedIndex;
	}

	@Override
	public int getMaxCharge() {
		return 12000;
	}

	@Override
	public int getTier() {
		return tier;
	}

	@Override
	public int getTransferLimit() {
		return 250;
	}

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List itemList)
    {
        ItemStack itemstack = new ItemStack(this, 1);
        ElectricItem.charge(itemstack, 0x7fffffff, 0x7fffffff, true, false);
        itemList.add(itemstack);
        itemList.add(new ItemStack(this, 1, getMaxDamage()));
    }
}
