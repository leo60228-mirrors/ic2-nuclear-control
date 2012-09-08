package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import shedar.mods.ic2.nuclearcontrol.panel.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.panel.PanelString;
import shedar.mods.ic2.nuclearcontrol.utils.ItemStackUtils;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.ic2.api.IEnergyStorage;

public class ItemCardEnergyArrayLocation extends ItemCardBase
{
    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 4;
    public static final int DISPLAY_EACH = 8;
    public static final int DISPLAY_TOTAL = 16;
    public static final int DISPLAY_PERCENTAGE = 32;    
    
    public static final int CARD_TYPE = 3;
    
    public ItemCardEnergyArrayLocation(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    protected static int getCardCount(ItemStack itemStack)
    {
        if(itemStack == null)
            return 0;
        if(!(itemStack.getItem() instanceof ItemCardEnergyArrayLocation))
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
        if(!(itemStack.getItem() instanceof ItemCardEnergyArrayLocation))
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
            int[] coordinates =  ItemCardEnergySensorLocation.getCoordinates(card);
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
            NuclearNetworkHelper.setSensorCardField(panel, updateSet);
    }

    @Override
    public int getCardType()
    {
        return CARD_TYPE;
    }

    @Override
    public void networkUpdate(String fieldName, int value, ItemStack itemStack)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        nbtTagCompound.setInteger(fieldName, value);
    }
    
    @Override
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        int state = nbtTagCompound.getInteger("state");
        if(state != STATE_OK)
            return StringUtils.getStateMessage(state);
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        int totalEnergy = 0;
        int totalStorage = 0;
        boolean showEach = (displaySettings & DISPLAY_EACH) > 0;
        boolean showSummary = (displaySettings & DISPLAY_TOTAL) > 0;
        boolean showEnergy = (displaySettings & DISPLAY_ENERGY) > 0;
        boolean showFree = (displaySettings & DISPLAY_FREE) > 0;
        boolean showStorage = (displaySettings & DISPLAY_STORAGE) > 0;
        boolean showPercentage = (displaySettings & DISPLAY_PERCENTAGE) > 0;
        int cardCount = getCardCount(itemStack);
        String title = nbtTagCompound.getString("title");
        if(title!=null && !title.isEmpty())
        {
            line = new PanelString();
            line.textCenter = title; 
            result.add(line);
        }
        for(int i=0; i<cardCount; i++)
        {
            int energy =  nbtTagCompound.getInteger(String.format("_%denergy",i));
            int storage =  nbtTagCompound.getInteger(String.format("_%dmaxStorage",i));
            if(showSummary)
            {
                totalEnergy += energy;
                totalStorage += storage;
            }
            
            if(showEach)
            {
                if(showEnergy)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = StringTranslate.getInstance().translateKeyFormat("msg.nc.InfoPanelEnergyN", i+1, StringUtils.getFormatted("", energy, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", energy, false);
                    result.add(line);
                }
                if(showFree)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = StringTranslate.getInstance().translateKeyFormat("msg.nc.InfoPanelEnergyFreeN", i+1, StringUtils.getFormatted("", storage - energy, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage - energy, false);

                    result.add(line);
                }
                if(showStorage)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = StringTranslate.getInstance().translateKeyFormat("msg.nc.InfoPanelEnergyStorageN", i+1, StringUtils.getFormatted("", storage, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage, false);
                    result.add(line);
                }
                if(showPercentage)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = StringTranslate.getInstance().translateKeyFormat("msg.nc.InfoPanelEnergyPercentageN", i+1, StringUtils.getFormatted("", storage==0? 100:(energy*100/storage), false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage==0? 100:(energy*100/storage), false);
                    result.add(line);
                }                
            }
        }
        if(showSummary)
        {
            if(showEnergy)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergy", totalEnergy, showLabels);
                result.add(line);
            }
            if(showFree)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyFree", totalStorage - totalEnergy, showLabels);
                result.add(line);
            }
            if(showStorage)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyStorage", totalStorage, showLabels);
                result.add(line);
            }
            if(showPercentage)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyPercentage", totalStorage==0? 100:(totalEnergy*100/totalStorage), showLabels);
                result.add(line);
            }                
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyCurrent"), DISPLAY_ENERGY, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyStorage"), DISPLAY_STORAGE, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyFree"), DISPLAY_FREE, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyPercentage"), DISPLAY_PERCENTAGE, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyEach"), DISPLAY_EACH, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyTotal"), DISPLAY_TOTAL, CARD_TYPE));
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, List info) 
    {
        int cardCount = getCardCount(itemStack);
        if(cardCount > 0)
        {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            String title = nbtTagCompound.getString("title");
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(StringTranslate.getInstance().translateKey("msg.nc.EnergyCardQuantity"), cardCount);
            info.add(hint);
        }
    }
}
