package shedar.mods.ic2.nuclearcontrol;

import java.util.Map;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class NuclearNetworkHelper
{
    //server
    public static void setSensorCardField(TileEntityInfoPanel panel, Map<String, Integer> fields)
    {
        if(fields==null || fields.isEmpty() || panel==null)
            return;
            
        if(panel.worldObj.isRemote)
            return;

        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(PacketHandler.PACKET_SENSOR);
        output.writeInt(panel.xCoord);
        output.writeInt(panel.yCoord);
        output.writeInt(panel.zCoord);
        output.writeShort(fields.size());
        for (Map.Entry<String, Integer> entry : fields.entrySet())
        {
            output.writeUTF(entry.getKey());
            output.writeInt(entry.getValue());
        }
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = output.toByteArray();
        packet.length = packet.data.length;
        
        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(
                panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.worldObj.getWorldInfo().getDimension(), packet);
    }
    
    //server
    public static void setSensorCardTitle(TileEntityInfoPanel panel, String title)
    {
        if(title==null || panel==null)
            return;
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(PacketHandler.PACKET_SENSOR_TITLE);
        output.writeInt(panel.xCoord);
        output.writeInt(panel.yCoord);
        output.writeInt(panel.zCoord);
        output.writeUTF(title);
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = output.toByteArray();
        packet.length = packet.data.length;

        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(
                panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.worldObj.getWorldInfo().getDimension(), packet);
    }
    
    public static void chatMessage(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            Packet250CustomPayload packet = new Packet250CustomPayload();
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeShort(PacketHandler.PACKET_CHAT);
            output.writeUTF(message);
            packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
            packet.isChunkDataPacket = false;
            packet.data = output.toByteArray();
            packet.length = packet.data.length;
            ((EntityPlayerMP)player).serverForThisPlayer.sendPacketToPlayer(packet);
        }
    }
    
    //client
    public static void setNewAlarmSound(int x, int y, int z, String soundName)
    {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
        output.writeUTF(soundName);
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = output.toByteArray();
        packet.length = packet.data.length;
        FMLClientHandler.instance().getClient().getSendQueue().addToSendQueue(packet);
    }    
}
