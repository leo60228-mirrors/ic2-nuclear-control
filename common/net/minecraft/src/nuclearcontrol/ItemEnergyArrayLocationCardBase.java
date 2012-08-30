package net.minecraft.src.nuclearcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.ic2.api.IEnergyStorage;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemEnergyArrayLocationCardBase extends ItemCardBase
{
    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 4;
    public static final int DISPLAY_EACH = 8;
    public static final int DISPLAY_TOTAL = 16;
    public static final int DISPLAY_PERCENTAGE = 32;    
    
    public static final int CARD_TYPE = 3;
    
    public ItemEnergyArrayLocationCardBase(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    protected static int getCardCount(ItemStack itemStack)
    {
        if(itemStack == null)
            return 0;
        if(!(itemStack.getItem() instanceof ItemEnergyArrayLocationCardBase))
            return 0;
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        if (nbtTagCompound == null)
        {
            return 0;
        }
        return nbtTagCompound.getInteger("cardCount");
    }
    
    private int[] getCoordinates(ItemStack itemStack, int cardNumber)
    {
        if(!(itemStack.getItem() instanceof ItemEnergyArrayLocationCardBase))
            return null;
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        if (nbtTagCompound == null)
        {
            return null;
        }
        int cardCount = nbtTagCompound.getInteger("cardCount");
        if(cardNumber >= cardCount)
            return null;
        int[] coordinates = new int[]{
            nbtTagCompound.getInteger(String.format("_%dx", cardNumber)),
            nbtTagCompound.getInteger(String.format("_%dy", cardNumber)),  
            nbtTagCompound.getInteger(String.format("_%dz", cardNumber))  
        };
        return coordinates;
    }


    public static void initArray(ItemStack itemStack, Vector<ItemStack> cards)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        int cardCount = getCardCount(itemStack); 
        for (ItemStack card : cards)
        {
            int[] coordinates =  ItemEnergySensorLocationCard.getCoordinates(card);
            if(coordinates == null)
                continue;
            nbtTagCompound.setInteger(String.format("_%dx", cardCount), coordinates[0]);
            nbtTagCompound.setInteger(String.format("_%dy", cardCount), coordinates[1]);
            nbtTagCompound.setInteger(String.format("_%dz", cardCount), coordinates[2]);
            cardCount++;
        }
        nbtTagCompound.setInteger("cardCount", cardCount);
    }

    @Override
    public void update(TileEntityInfoPanel panel, ItemStack stack, int range)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(stack);
        int cardCount = getCardCount(stack);
        Map<String, Integer> updateSet = new HashMap<String, Integer>();
        if(cardCount == 0)
        {
            setField("state", STATE_INVALID_CARD, nbtTagCompound, panel, updateSet);
        }
        else
        {
            boolean foundAny = false;
            boolean outOfRange = false;
            for(int i=0; i<cardCount; i++)
            {
                int[] coordinates = getCoordinates(stack, i);
                int dx = coordinates[0] - panel.xCoord;
                int dy = coordinates[1] - panel.yCoord;
                int dz = coordinates[2] - panel.zCoord;
                if(Math.abs(dx) <= range && 
                        Math.abs(dy) <= range && 
                        Math.abs(dz) <= range)
                {
                    IEnergyStorage storage = EnergyStorageHelper.getStorageAt(panel.worldObj, 
                            coordinates[0], coordinates[1], coordinates[2]);
                    if(storage != null)
                    {
                        setField("state", STATE_OK, nbtTagCompound, panel, updateSet);
                        setField(String.format("_%denergy", i), storage.getStored(), nbtTagCompound, panel, updateSet);
                        setField(String.format("_%dmaxStorage", i), storage.getCapacity(), nbtTagCompound, panel, updateSet);
                        foundAny = true;
                    }
                }
                else
                {
                    outOfRange = true;
                }
            }
            if(!foundAny)
            {
                if(outOfRange)
                    setField("state", STATE_OUT_OF_RANGE, nbtTagCompound, panel, updateSet);
                else
                    setField("state", STATE_NO_TARGET, nbtTagCompound, panel, updateSet);
            }
        }
        if(!updateSet.isEmpty())
            mod_IC2NuclearControl.setSensorCardField(panel, updateSet);
    }

    @Override
    public int getCardType()
    {
        return CARD_TYPE;
    }

    @Override
    abstract public void networkUpdate(String fieldName, int value, ItemStack stack);

    @Override
    abstract public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels);

    @Override
    abstract public List<PanelSetting> getSettingsList();
}
