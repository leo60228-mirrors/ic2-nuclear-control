package shedar.mods.ic2.nuclearcontrol;

import shedar.mods.ic2.nuclearcontrol.panel.IPanelDataSource;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
    public boolean isPlaying(String soundId)
    {
        return false;
    }
    
    public void stopAlarm(String soundId)
    {}
    
    public String playAlarm(double x, double y, double z, String name, float volume)
    {
        return null;
    }
    
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityIC2Thermo.class, "IC2Thermo");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityRemoteThermo.class, "IC2RemoteThermo");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanel.class, "IC2NCInfoPanel");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityEnergyCounter.class, "IC2NCEnergyCounter");
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        switch (ID)
        {
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                return new ContainerRemoteThermo(player, (TileEntityRemoteThermo)tileEntity);
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL:
                return new ContainerInfoPanel(player, (TileEntityInfoPanel)tileEntity);
            case BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER:
                return new ContainerEnergyCounter(player, (TileEntityEnergyCounter)tileEntity);
            case BlockNuclearControlMain.DAMAGE_HOWLER_ALARM:
            case BlockNuclearControlMain.DAMAGE_INDUSTRIAL_ALARM:
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return new ContainerEmpty(tileEntity);
            default:
                return null;
        }
    }
    
    public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (player instanceof EntityPlayerMP)//server
        {
            // used to set sound alarm from client's GUI
            ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
            int x = dat.readInt();
            int y = dat.readInt();
            int z = dat.readInt();
            String soundName = dat.readUTF();
            TileEntity tileEntity = ((EntityPlayerMP) player).worldObj.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityHowlerAlarm)
            {
                ((TileEntityHowlerAlarm) tileEntity).setSoundName(soundName);
            } 
            else if (tileEntity instanceof TileEntityInfoPanel)
            {
                ItemStack stack = ((TileEntityInfoPanel) tileEntity).getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
                if (stack == null || !(stack.getItem() instanceof IPanelDataSource))
                {
                    return;
                }
                IPanelDataSource card = (IPanelDataSource) stack.getItem();
                card.setTitle(stack, soundName);
                NuclearNetworkHelper.setSensorCardTitle((TileEntityInfoPanel)tileEntity, soundName);
            }
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        // null on server
        return null;
    }

}
