package shedar.mods.ic2.nuclearcontrol.tileentities;

import java.util.ArrayList;
import java.util.List;

import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.items.ItemUpgrade;

import net.minecraft.item.ItemStack;

public class TileEntityAdvancedInfoPanel extends TileEntityInfoPanel
{
    public float angleHor;
    public float angleVert;
    public ItemStack card2;
    public ItemStack card3;

    private static final int SLOT_CARD1 = 0;
    private static final int SLOT_CARD2 = 1;
    private static final int SLOT_CARD3 = 2;
    private static final int SLOT_UPGRADE_RANGE = 3;

    
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
}
