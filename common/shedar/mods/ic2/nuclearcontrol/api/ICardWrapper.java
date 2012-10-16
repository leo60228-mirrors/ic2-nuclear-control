package shedar.mods.ic2.nuclearcontrol.api;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public interface ICardWrapper
{
    
    void setTarget(int x, int y, int z);
    ChunkCoordinates getTarget();
    void setInt(String name, Integer value);
    Integer getInt(String name);
    void setLong(String name, Long value);
    Long getLong(String name);
    void setString(String name, String value);
    String getString(String name);
    void setBoolean(String name, Boolean value);
    Boolean getBoolean(String name);
    void setTitle(String title);
    String getTitle();
    CardState getState();
    void setState(CardState state);
    ItemStack getItemStack();
    boolean hasField(String field);
    void commit(TileEntity panel);

}
