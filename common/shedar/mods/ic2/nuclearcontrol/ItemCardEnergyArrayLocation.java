package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.IEnergyStorage;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemCardEnergyArrayLocation extends ItemCardBase
{
    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 4;
    public static final int DISPLAY_EACH = 8;
    public static final int DISPLAY_TOTAL = 16;
    public static final int DISPLAY_PERCENTAGE = 32;    
    
    public static final UUID CARD_TYPE = new UUID(0, 3);;
    
    public ItemCardEnergyArrayLocation(int i, int iconIndex)
    {
        super(i, iconIndex);
    }
    
    private int[] getCoordinates(ICardWrapper card, int cardNumber)
    {
        int cardCount = card.getInt("cardCount");
        if(cardNumber >= cardCount)
            return null;
        int[] coordinates = new int[]{
            card.getInt(String.format("_%dx", cardNumber)),
            card.getInt(String.format("_%dy", cardNumber)),  
            card.getInt(String.format("_%dz", cardNumber))  
        };
        return coordinates;
    }

    public static int getCardCount(ICardWrapper card)
    {
        return card.getInt("cardCount");
    }
    

    public static void initArray(CardWrapperImpl card, Vector<ItemStack> cards)
    {
        int cardCount = getCardCount(card); 
        for (ItemStack subCard : cards)
        {
            ChunkCoordinates target = new CardWrapperImpl(subCard).getTarget();
            if(target == null)
                continue;
            card.setInt(String.format("_%dx", cardCount), target.posX);
            card.setInt(String.format("_%dy", cardCount), target.posY);
            card.setInt(String.format("_%dz", cardCount), target.posZ);
            cardCount++;
        }
        card.setInt("cardCount", cardCount);
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range)
    {
        int cardCount = getCardCount(card);
        if(cardCount == 0)
        {
            return CardState.INVALID_CARD;
        }
        else
        {
            boolean foundAny = false;
            boolean outOfRange = false;
            for(int i=0; i<cardCount; i++)
            {
                int[] coordinates = getCoordinates(card, i);
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
                        card.setInt(String.format("_%denergy", i), storage.getStored());
                        card.setInt(String.format("_%dmaxStorage", i), storage.getCapacity());
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
                    return CardState.OUT_OF_RANGE;
                else
                    return CardState.NO_TARGET;
            }
            return CardState.OK;
        }
    }

    @Override
    public UUID getCardType()
    {
        return CARD_TYPE;
    }
    
    @Override
    public List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        long totalEnergy = 0;
        long totalStorage = 0;
        boolean showEach = (displaySettings & DISPLAY_EACH) > 0;
        boolean showSummary = (displaySettings & DISPLAY_TOTAL) > 0;
        boolean showEnergy = (displaySettings & DISPLAY_ENERGY) > 0;
        boolean showFree = (displaySettings & DISPLAY_FREE) > 0;
        boolean showStorage = (displaySettings & DISPLAY_STORAGE) > 0;
        boolean showPercentage = (displaySettings & DISPLAY_PERCENTAGE) > 0;
        int cardCount = getCardCount(card);
        for(int i=0; i<cardCount; i++)
        {
            int energy =  card.getInt(String.format("_%denergy",i));
            int storage =  card.getInt(String.format("_%dmaxStorage",i));
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
                        line.textLeft = StringTranslate.getInstance().translateKeyFormat("msg.nc.InfoPanelEnergyPercentageN", i+1, StringUtils.getFormatted("", storage==0? 100:(((long)energy)*100L/storage), false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage==0? 100:(((long)energy)*100L/storage), false);
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
        CardWrapperImpl card = new CardWrapperImpl(itemStack);
        int cardCount = getCardCount(card);
        if(cardCount > 0)
        {
            String title = card.getTitle();
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(StringTranslate.getInstance().translateKey("msg.nc.EnergyCardQuantity"), cardCount);
            info.add(hint);
        }
    }
}
