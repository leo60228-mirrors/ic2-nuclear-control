package shedar.mods.ic2.nuclearcontrol.panel;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.utils.ItemStackUtils;
import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;
import cpw.mods.fml.common.FMLLog;

public class CardWrapperImpl implements ICardWrapper
{
    private ItemStack card;
    private Map<String, Object> updateSet;
    
    public CardWrapperImpl(ItemStack card)
    {
        if(!(card.getItem() instanceof IPanelDataSource))
        {
            FMLLog.severe("CardHelper sould be used for IPanelDataSource items.");
        }
        this.card = card;
        updateSet = new HashMap<String, Object>();
    }
    
    @Override
    public void setTarget(int x, int y, int z)
    {
        if(!(card.getItem() instanceof IRemoteSensor))
        {
            FMLLog.warning("Trying to set coordinates [%d, %d, %d] for item which is not RemoteSensor.", x, y, z);
            return;
        }
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(card);
        nbtTagCompound.setInteger("x", x);
        nbtTagCompound.setInteger("y", y);
        nbtTagCompound.setInteger("z", z);    
    }
    
    @Override
    public ChunkCoordinates getTarget()
    {
        NBTTagCompound nbtTagCompound = card.getTagCompound();
        if (nbtTagCompound == null)
        {
            return null;
        }
        ChunkCoordinates coordinates  = new ChunkCoordinates();
        coordinates.posX = nbtTagCompound.getInteger("x");
        coordinates.posY = nbtTagCompound.getInteger("y");
        coordinates.posZ = nbtTagCompound.getInteger("z");  
        return coordinates;
    }
    
    @Override
    public void setInt(String name, Integer value)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(card);
        if(nbtTagCompound.hasKey(name))
        {
            Integer prevValue = nbtTagCompound.getInteger(name);
            if(prevValue==null || !prevValue.equals(value))
                updateSet.put(name, value);
        }
        else
        {
            updateSet.put(name, value);
        }
        nbtTagCompound.setInteger(name, value);
    }

    @Override
    public Integer getInt(String name)
    {
        NBTTagCompound nbtTagCompound = card.getTagCompound();
        if(nbtTagCompound == null)
            return 0;
        return nbtTagCompound.getInteger(name);
    }
    
    @Override
    public void setLong(String name, Long value)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(card);
        if(nbtTagCompound.hasKey(name))
        {
            Long prevValue = nbtTagCompound.getLong(name);
            if(prevValue==null || !prevValue.equals(value))
                updateSet.put(name, value);
        }
        else
        {
            updateSet.put(name, value);
        }
        nbtTagCompound.setLong(name, value);
    }
    
    @Override
    public Long getLong(String name)
    {
        NBTTagCompound nbtTagCompound = card.getTagCompound();
        if(nbtTagCompound == null)
            return 0L;
        return nbtTagCompound.getLong(name);
    }
    
    @Override
    public void setString(String name, String value)
    {
        if(name == null)
            return;
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(card);
        if(nbtTagCompound.hasKey(name))
        {
            String prevValue = nbtTagCompound.getString(name);
            if(prevValue==null || !prevValue.equals(value))
                updateSet.put(name, value);
        }
        else
        {
            updateSet.put(name, value);
        }
        nbtTagCompound.setString(name, value);
    }
    
    @Override
    public String getString(String name)
    {
        NBTTagCompound nbtTagCompound = card.getTagCompound();
        if(nbtTagCompound == null)
            return "";
        return nbtTagCompound.getString(name);
    }
    
    @Override
    public void setBoolean(String name, Boolean value)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(card);
        if(nbtTagCompound.hasKey(name))
        {
            Boolean prevValue = nbtTagCompound.getBoolean(name);
            if(prevValue==null || !prevValue.equals(value))
                updateSet.put(name, value);
        }
        else
        {
            updateSet.put(name, value);
        }
        nbtTagCompound.setBoolean(name, value);
    }
    
    @Override
    public Boolean getBoolean(String name)
    {
        NBTTagCompound nbtTagCompound = card.getTagCompound();
        if(nbtTagCompound == null)
            return false;
        return nbtTagCompound.getBoolean(name);
    }
    
    @Override
    public void setTitle(String title)
    {
        setString("title", title);
    }
    
    @Override
    public String getTitle()
    {
        return getString("title");
    }
    
    @Override
    public CardState getState()
    {
        return CardState.fromInteger(getInt("state"));
    }
    
    @Override
    public void setState(CardState state)
    {
        setInt("state", state.getIndex());
    }
    
    @Override
    public ItemStack getItemStack()
    {
        return card;
    }
    
    @Override
    public boolean hasField(String field)
    {
        return ItemStackUtils.getTagCompound(card).hasKey(field);
    }
    
    @Override
    public void commit(TileEntity panel)
    {
        if(!updateSet.isEmpty())
            NuclearNetworkHelper.setSensorCardField(panel, updateSet);
    }

}
