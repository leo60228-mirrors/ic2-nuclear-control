package shedar.mods.ic2.nuclearcontrol.items;

import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IReactor;

import java.util.List;

import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    protected void messagePlayer(EntityPlayer entityplayer, IReactor reactor) {
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
		return itemID;
	}

	@Override
	public int getEmptyItemId() {
		return itemID;
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
