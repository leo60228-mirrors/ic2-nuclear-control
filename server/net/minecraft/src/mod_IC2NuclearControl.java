package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.nuclearcontrol.BlockNuclearControlMain;
import net.minecraft.src.nuclearcontrol.ContainerInfoPanel;
import net.minecraft.src.nuclearcontrol.ContainerRemoteThermo;
import net.minecraft.src.nuclearcontrol.IC2NuclearControl;
import net.minecraft.src.nuclearcontrol.TileEntityHowlerAlarm;
import net.minecraft.src.nuclearcontrol.TileEntityInfoPanel;
import net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo;

public class mod_IC2NuclearControl extends IC2NuclearControl
{
    private static String allowedAlarms;
    

    public static boolean isClient()
    {
        return false;
    }

    @Override
    protected File getConfigFile(String name)
    {
    	return new File(new File("config"), name);
    }
    
    @Override
    public void load()
    {
        instance = this;
    	Configuration configuration;
        try
        {
        	File file = getConfigFile(CONFIG_NUCLEAR_CONTROL);
        	if(!file.exists()){
        		configuration = importConfig();
        	}
        	else
        	{
        		configuration = new Configuration(file);
                configuration.load();
        	}
        	
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
            configuration = null;
        }
        ModLoader.setInGameHook(this, true, false);
        initBlocks(configuration);
        registerBlocks();
        alarmRange = new Integer(configuration.getOrCreateIntProperty("alarmRange", Configuration.CATEGORY_GENERAL, 64).value).intValue();
        maxAlarmRange = new Integer(configuration.getOrCreateIntProperty("maxAlarmRange", Configuration.CATEGORY_GENERAL, 128).value).intValue();
        allowedAlarms = configuration.getOrCreateProperty("allowedAlarms", Configuration.CATEGORY_GENERAL, "default,sci-fi").value.replaceAll(" ", "");
        SMPMaxAlarmRange = 256;
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIC2Thermo.class, "IC2Thermo");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo.class, "IC2RemoteThermo");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityInfoPanel.class, "IC2NCInfoPanel");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender");

        MinecraftForge.setGuiHandler(this, this);
        if(configuration!=null)
        {
        	configuration.save();
        }
    }

    public static void chatMessage(EntityPlayer entityplayer, String message)
    {
        ((EntityPlayerMP) entityplayer).playerNetServerHandler.sendPacket(new Packet3Chat(message));
    }

    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity;
        switch (ID)
        {
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return null;
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                tileEntity = world.getBlockTileEntity(x, y, z);
                return new ContainerRemoteThermo(player, (TileEntityRemoteThermo)tileEntity);
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL:
                tileEntity = world.getBlockTileEntity(x, y, z);
                return new ContainerInfoPanel(player, (TileEntityInfoPanel)tileEntity);
            default:
                return null;
        }
    }
    
    @Override
    public void onLogin(NetworkManager network, Packet1Login login) 
    {
        EntityPlayerMP player = ((NetServerHandler)network.getNetHandler()).getPlayerEntity();
        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(arrayOutput);
        try
        {
            output.writeInt(maxAlarmRange);
            output.writeUTF(allowedAlarms);
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = NETWORK_CHANNEL_NAME;
            packet.isChunkDataPacket = false;
            packet.data = arrayOutput.toByteArray();
            packet.length = arrayOutput.size();
            player.playerNetServerHandler.sendPacket(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    };

    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] data)
    {
        //used to set sound alarm from client's GUI
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));
        try
        {
            int x = dataStream.readInt();
            int y = dataStream.readInt();
            int z = dataStream.readInt();
            String soundName = dataStream.readUTF();
            EntityPlayerMP player = ((NetServerHandler)network.getNetHandler()).getPlayerEntity(); 
            TileEntity alarm = player.worldObj.getBlockTileEntity(x, y, z);
            if(alarm instanceof TileEntityHowlerAlarm)
            {
                ((TileEntityHowlerAlarm)alarm).setSoundName(soundName);
            }
        }
        catch(IOException e)
        {
            ModLoader.getLogger().log(Level.WARNING, LOG_PREFIX + "Invalid packet: " + e.getMessage());
        }
        
    }
    
}
