package shedar.mods.ic2.nuclearcontrol.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.items.ItemUpgrade;

public class TileEntityAdvancedInfoPanel extends TileEntityInfoPanel
{
    public float angleHor;
    public float angleVert;
    private byte prevPowerMode;
    public byte powerMode;
    
    public ItemStack card2;
    public ItemStack card3;

    private static final int SLOT_CARD1 = 0;
    private static final int SLOT_CARD2 = 1;
    private static final int SLOT_CARD3 = 2;
    private static final int SLOT_UPGRADE_RANGE = 3;
    
    private static final int POWER_REDSTONE = 0;
    private static final int POWER_INVERTED = 1;
    private static final int POWER_ON = 2;
    private static final int POWER_OFF = 3;
    

    
    public TileEntityAdvancedInfoPanel()
    {
        super(4);//3 cards + range upgrade
        colored = true;
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = super.getNetworkedFields();
        list.add("angleHor");
        list.add("angleVert");
        list.add("card2");
        list.add("card3");
        list.add("powerMode");
        return list;
    }

    @Override
    public void onNetworkUpdate(String field)
    {
        super.onNetworkUpdate(field);
        if (field.equals("card2"))
        {
            inventory[SLOT_CARD2] = card2;
        }
        else if (field.equals("card3"))
        {
            inventory[SLOT_CARD3] = card3;
        } 
        else if (field.equals("powerMode") && prevPowerMode != powerMode)
        {
            if(screen!=null)
            {
                screen.turnPower(getPowered(), worldObj);
            }
            else
            {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
            }
            prevPowerMode = powerMode;
        }

    }
    
    @Override
    public boolean isItemValid(int slotIndex, ItemStack itemstack)
    {
        switch (slotIndex)
        {
            case SLOT_CARD1:
            case SLOT_CARD2:
            case SLOT_CARD3:
                return itemstack.getItem() instanceof IPanelDataSource;
            case SLOT_UPGRADE_RANGE:
                return itemstack.getItem() instanceof ItemUpgrade && itemstack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE; 
            default:
                return false;
        }
    }
    
    @Override
    protected boolean isColoredEval()
    {
        return true;
    }
    
    @Override
    protected ItemStack getRangeUpgrade()
    {
        return inventory[SLOT_UPGRADE_RANGE];
    }

    @Override
    public List<ItemStack> getCards()
    {
        List<ItemStack> data = new ArrayList<ItemStack>(3);
        data.add(inventory[SLOT_CARD1]);
        data.add(inventory[SLOT_CARD2]);
        data.add(inventory[SLOT_CARD3]);
        return data;
    }
    
    @Override
    protected boolean isCardSlot(int slot)
    {
        return slot == SLOT_CARD1 || slot == SLOT_CARD2 || slot == SLOT_CARD3; 
    }

    @Override
    protected void saveDisplaySettings(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setTag("dSettings1", serializeSlotSettings(SLOT_CARD1));
        nbttagcompound.setTag("dSettings2", serializeSlotSettings(SLOT_CARD2));
        nbttagcompound.setTag("dSettings3", serializeSlotSettings(SLOT_CARD3));
    }
    
    @Override
    protected void readDisplaySettings(NBTTagCompound nbttagcompound)
    {
        deserializeDisplaySettings(nbttagcompound, "dSettings1", SLOT_CARD1);
        deserializeDisplaySettings(nbttagcompound, "dSettings2", SLOT_CARD2);
        deserializeDisplaySettings(nbttagcompound, "dSettings3", SLOT_CARD3);
    }
    
    @Override
    protected void postReadFromNBT()
    {
        if(inventory[SLOT_CARD1]!=null)
        {
            card = inventory[SLOT_CARD1];
        }
        if(inventory[SLOT_CARD2]!=null)
        {
            card2 = inventory[SLOT_CARD2];
        }
        if(inventory[SLOT_CARD3]!=null)
        {
            card3 = inventory[SLOT_CARD3];
        }
    }
    
    @Override
    public boolean getPowered()
    {
        switch (powerMode)
        {   
        case POWER_ON:
            return true;
        case POWER_OFF:
            return false;
        case POWER_REDSTONE:
            return powered;
        case POWER_INVERTED:
            return !powered;
        }
        return false;
    }

}
