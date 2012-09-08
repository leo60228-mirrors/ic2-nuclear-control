package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

public class ItemCardEnergySensorLocation extends ItemCardBase
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";
    
    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 4;
    public static final int DISPLAY_PERCENTAGE = 8;
    
    public static final int CARD_TYPE = 2;

    public ItemCardEnergySensorLocation(int i, int iconIndex)
    {
        super(i, iconIndex);
    }
    
    @Override
    public void update(TileEntityInfoPanel panel, ItemStack stack, int range)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(stack);
        int[] coordinates = getCoordinates(stack);
        Map<String, Integer> updateSet = new HashMap<String, Integer>();
        if(coordinates == null)
        {
            setField("state", STATE_INVALID_CARD, nbtTagCompound, panel, updateSet);
        }
        else
        {
            int dx = coordinates[0] - panel.xCoord;
            int dy = coordinates[1] - panel.yCoord;
            int dz = coordinates[2] - panel.zCoord;
            if(Math.abs(dx) > range || 
                Math.abs(dy) > range || 
                Math.abs(dz) > range)
            {
                setField("state", STATE_OUT_OF_RANGE, nbtTagCompound, panel, updateSet);
            }
            else
            {
                IEnergyStorage storage = EnergyStorageHelper.getStorageAt(panel.worldObj, 
                        coordinates[0], coordinates[1], coordinates[2]);
                if(storage != null){
                    setField("state", STATE_OK, nbtTagCompound, panel, updateSet);
                    setField("energy", storage.getStored(), nbtTagCompound, panel, updateSet);
                    setField("maxStorage", storage.getCapacity(), nbtTagCompound, panel, updateSet);
                }
                else
                {
                    setField("state", STATE_NO_TARGET, nbtTagCompound, panel, updateSet);
                }
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
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, List info) 
    {
        int[] coordinates = getCoordinates(itemStack);
        if(coordinates!=null)
        {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            String title = nbtTagCompound.getString("title");
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(HINT_TEMPLATE, coordinates[0], coordinates[1], coordinates[2]);
            info.add(hint);
        }
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
        int energy =  nbtTagCompound.getInteger("energy");
        int storage =  nbtTagCompound.getInteger("maxStorage");
        String title = nbtTagCompound.getString("title");
        if(title!=null && !title.isEmpty())
        {
            line = new PanelString();
            line.textCenter = title; 
            result.add(line);
        }
        if((displaySettings & DISPLAY_ENERGY) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergy", energy, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_FREE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyFree", storage - energy, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_STORAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyStorage", storage, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_PERCENTAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyPercentage", storage==0? 100:(energy*100/storage), showLabels); 
            result.add(line);
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
        return result;
    }
}
