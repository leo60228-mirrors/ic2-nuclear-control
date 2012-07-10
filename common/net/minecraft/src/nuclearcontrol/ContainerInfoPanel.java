package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerInfoPanel extends Container
{
    public TileEntityInfoPanel panel;
    public EntityPlayer player;

    public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel)
    {
        super();
        
        this.panel = panel;
        this.player = player; 
        
        //card
        addSlot(new SlotFilter(panel, 0, 8, 18));
        
        //upgrade
        addSlot(new SlotFilter(panel, 1, 8, 36));

        //inventory
        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
                addSlot(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++)
        {
            addSlot(new Slot(player.inventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return panel.isUseableByPlayer(player);
    }
    
    @Override
    public ItemStack transferStackInSlot(int slotId)
    {
        Slot slot = (Slot)this.inventorySlots.get(slotId);
        if(slot!=null)
        {
            ItemStack items = slot.getStack();
            if(items!=null)
            {
                int initialCount = items.stackSize;
                if(slotId < panel.getSizeInventory())//moving from panel to inventory
                {
                    mergeItemStack(items, panel.getSizeInventory(), inventorySlots.size(), false);
                    if (items.stackSize == 0)
                    {
                        slot.putStack((ItemStack)null);
                    }
                    else
                    {
                        slot.onSlotChanged();
                        if(initialCount!=items.stackSize)
                            return items;
                    }
                }
                else//moving from inventory to panel
                {
                    for(int i=0;i<panel.getSizeInventory();i++)
                    {
                        if(!panel.isItemValid(i, items))
                        {
                            continue;
                        }
                        ItemStack targetStack = panel.getStackInSlot(i);
                        if(targetStack == null)
                        {
                            Slot targetSlot = (Slot)this.inventorySlots.get(i);
                            targetSlot.putStack(items);
                            slot.putStack((ItemStack)null);
                            break;
                        }
                        else if(items.isStackable() && items.isItemEqual(targetStack))
                        {
                            mergeItemStack(items, i, i+1, false);
                            if (items.stackSize == 0)
                            {
                                slot.putStack((ItemStack)null);
                            }
                            else
                            {
                                slot.onSlotChanged();
                                if(initialCount!=items.stackSize)
                                    return items;
                            }
                            break;
                        }
                        
                    }
                }
            }
        }
        return null;
    }
    
}