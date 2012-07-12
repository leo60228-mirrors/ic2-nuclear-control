package net.minecraft.src.nuclearcontrol;

import java.util.Vector;

import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ic2.api.Items;

public class StorageArrayRecipe implements IRecipe
{

    @Override
    public boolean matches(InventoryCrafting inventory)
    {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        int inventoryLength = inventory.getSizeInventory();
        boolean fail = false;
        int cardCount = 0;
        int arrayCount = 0;
        ItemStack array = null;
        Vector<ItemStack> cards = new Vector<ItemStack>();
        for(int i=0; i<inventoryLength; i++)
        {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if(itemStack == null)
                continue;
            if(itemStack.getItem() instanceof ItemEnergySensorLocationCard)
            {
                cards.add(itemStack);
                cardCount++;
            }
            else if(itemStack.getItem() instanceof ItemEnergyArrayLocationCard)
            {
                array = itemStack;
                arrayCount++;
            }
            else
            {
                fail = true;
                break;
            }
        }
        if(fail)
        {
            return null;
        }
        if(cardCount >= 2 && cardCount <= 6 && arrayCount == 0)
        {
            ItemStack itemStack = new ItemStack(IC2NuclearControl.itemEnergyArrayLocationCard, 1, 0);
            ItemEnergyArrayLocationCard.initArray(itemStack, cards);
            return itemStack;
        }
        else if(cardCount == 0 && arrayCount == 1)
        {
            int cnt = ItemEnergyArrayLocationCard.getCardCount(array);
            if(cnt > 0)
            {
                return new ItemStack(Items.getItem("electronicCircuit").getItem(), 2*cnt, 0);
            }
        }
        else if(arrayCount == 1 && cardCount > 0)
        {
            int cnt = ItemEnergyArrayLocationCard.getCardCount(array);
            if(cnt + cardCount <= 6)
            {
                ItemStack itemStack = new ItemStack(IC2NuclearControl.itemEnergyArrayLocationCard, 1, 0);
                itemStack.setTagCompound((NBTTagCompound)array.getTagCompound().copy());
                ItemEnergyArrayLocationCard.initArray(itemStack, cards);
                return itemStack;
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

}
