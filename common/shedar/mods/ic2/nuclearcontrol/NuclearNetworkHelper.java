package shedar.mods.ic2.nuclearcontrol;

import java.util.List;
import java.util.Map;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICrafting;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.client.FMLClientHandler;

public class NuclearNetworkHelper
{
    
    //server
    public static void sendEnergyCounterValue(TileEntityEnergyCounter counter, ICrafting crafter)
    {
        if(counter==null || !(crafter instanceof EntityPlayerMP))
            return;
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(PacketHandler.PACKET_ECOUNTER);
        output.writeInt(counter.xCoord);
        output.writeInt(counter.yCoord);
        output.writeInt(counter.zCoord);
        output.writeLong(counter.counter);
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = output.toByteArray();
        packet.length = packet.data.length;
        
        EntityPlayerMP player = (EntityPlayerMP)crafter;
        player.serverForThisPlayer.sendPacketToPlayer(packet);
    }

    //server
    public static void sendAverageCounterValue(TileEntityAverageCounter counter, ICrafting crafter, int average)
    {
        if(counter==null || !(crafter instanceof EntityPlayerMP))
            return;
        Packet250CustomPayload packet = new Packet250CustomPayload();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(PacketHandler.PACKET_ACOUNTER);
        output.writeInt(counter.xCoord);
        output.writeInt(counter.yCoord);
        output.writeInt(counter.zCoord);
        output.writeInt(average);
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = output.toByteArray();
        packet.length = packet.data.length;
        
        EntityPlayerMP player = (EntityPlayerMP)crafter;
        player.serverForThisPlayer.sendPacketToPlayer(packet);
    }
    
    private static void sendPacketToAllAround(int x, int y, int z, int dist, World world, Packet packet)
    {
        @SuppressWarnings("unchecked")
        List<EntityPlayerMP> players = world.playerEntities;
        for (EntityPlayerMP player : players)
        {
            double dx = x - player.posX;
            double dy = y - player.posY;
            double dz = z - player.posZ;

            if (dx*dx + dy*dy + dz*dz < dist * dist)
            {
                player.serverForThisPlayer.sendPacketToPlayer(packet);
            }        
        }
        
    }
    
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
        sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.worldObj, packet);
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

        sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.worldObj, packet);
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
