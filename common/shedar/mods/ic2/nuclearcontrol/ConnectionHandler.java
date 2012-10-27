package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler
{

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(PacketHandler.PACKET_ALARM);
        out.writeInt(IC2NuclearControl.instance.maxAlarmRange);
        out.writeUTF(IC2NuclearControl.instance.allowedAlarms);
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = IC2NuclearControl.NETWORK_CHANNEL_NAME;
        packet.isChunkDataPacket = false;
        packet.data = out.toByteArray();
        packet.length = packet.data.length;
        manager.addToSendQueue(packet);
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
    {
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
    {
    }

    @Override
    public void connectionClosed(INetworkManager manager)
    {
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
    }

}
