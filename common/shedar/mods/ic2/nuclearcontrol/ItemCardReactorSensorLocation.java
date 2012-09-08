package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.Items;
import shedar.mods.ic2.nuclearcontrol.panel.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.panel.PanelString;
import shedar.mods.ic2.nuclearcontrol.utils.ItemStackUtils;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemCardReactorSensorLocation extends ItemCardBase
{
    protected static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public static final int DISPLAY_ONOFF = 1;
    public static final int DISPLAY_HEAT = 2;
    public static final int DISPLAY_MAXHEAT = 4;
    public static final int DISPLAY_OUTPUT = 8;
    public static final int DISPLAY_TIME = 16;
    public static final int DISPLAY_MELTING = 32;
    
    public static final int CARD_TYPE = 0;
    
    public ItemCardReactorSensorLocation(int i, int iconIndex)
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
            return;
        }
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
            IReactor reactor = NuclearHelper.getReactorAt(panel.worldObj, 
                    coordinates[0], coordinates[1], coordinates[2]);
            if(reactor != null)
            {
                setField("state", STATE_OK, nbtTagCompound, panel, updateSet);
                setField("heat", reactor.getHeat(), nbtTagCompound, panel, updateSet);
                setField("maxHeat", reactor.getMaxHeat(), nbtTagCompound, panel, updateSet);
                setField("reactorPowered", NuclearHelper.isProducing(reactor), nbtTagCompound, panel, updateSet);
                setField("output", reactor.getOutput(), nbtTagCompound, panel, updateSet);

                IInventory inventory = (IInventory)reactor; 
                int slotCount = inventory.getSizeInventory();
                int timeLeft = 0;
                int uraniumId = Items.getItem("uraniumCell").itemID;
                for(int i = 0; i < slotCount; i++)
                {
                    ItemStack rStack = inventory.getStackInSlot(i);
                    if(rStack!=null && rStack.itemID == uraniumId)
                    {
                        timeLeft = Math.max(timeLeft, rStack.getMaxDamage() - rStack.getItemDamage());
                    }
                }
                setField("timeLeft", timeLeft, nbtTagCompound, panel, updateSet);
            }
            else
            {
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
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        int state = nbtTagCompound.getInteger("state");
        if(state != STATE_OK)
            return StringUtils.getStateMessage(state);
        List<PanelString> result = new LinkedList<PanelString>();
        String text;
        PanelString line;
        String title = nbtTagCompound.getString("title");
        if(title!=null && !title.isEmpty())
        {
            line = new PanelString();
            line.textCenter = title; 
            result.add(line);
        }
        if((displaySettings & DISPLAY_HEAT) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelHeat", nbtTagCompound.getInteger("heat"), showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_MAXHEAT) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelMaxHeat", nbtTagCompound.getInteger("maxHeat"), showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_MELTING) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelMelting", nbtTagCompound.getInteger("maxHeat")*85/100, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_OUTPUT) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelOutput", nbtTagCompound.getInteger("output"), showLabels); 
            result.add(line);
        }
        int timeLeft = nbtTagCompound.getInteger("timeLeft");
        if((displaySettings & DISPLAY_TIME) > 0)
        {
            int hours = timeLeft / 3600;
            int minutes = (timeLeft % 3600) / 60;
            int seconds = timeLeft % 60;
            line = new PanelString();

            String time = String.format("%d:%02d:%02d", hours, minutes, seconds);                
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelTimeRemaining", time, showLabels); 
            result.add(line);
        }

        int txtColor = 0;
        if((displaySettings & DISPLAY_ONOFF) > 0)
        {
            int shift = title!=null && !title.isEmpty()?1:0;
            boolean reactorPowered = nbtTagCompound.getInteger("reactorPowered")==1;
            if(reactorPowered)
            {
                txtColor = 0x00ff00;
                text = StringTranslate.getInstance().translateKey("msg.nc.InfoPanelOn");
            }
            else
            {
                txtColor = 0xff0000;
                text = StringTranslate.getInstance().translateKey("msg.nc.InfoPanelOff");
            }
            if(result.size()>shift)
            {
                PanelString firstLine = result.get(shift);
                firstLine.textRight = text;
                firstLine.colorRight = txtColor;
            }
            else
            {
                line = new PanelString();
                line.textLeft = text;
                line.colorLeft = txtColor;
                result.add(line);
            }
        }        
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(6);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelOnOff"), DISPLAY_ONOFF, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelHeat"), DISPLAY_HEAT, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelMaxHeat"), DISPLAY_MAXHEAT, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelMelting"), DISPLAY_MELTING, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelOutput"), DISPLAY_OUTPUT, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelTimeRemaining"), DISPLAY_TIME, CARD_TYPE));
        return result;
    }

}
