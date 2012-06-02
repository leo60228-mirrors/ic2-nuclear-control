package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.StatCollector;
import net.minecraft.src.forge.IChatHandler;

public class MsgProcessor implements IChatHandler
{

    @Override
    public String onServerChat(EntityPlayer player, String message)
    {
        return message;
    }

    @Override
    public boolean onChatCommand(EntityPlayer player, boolean isOp, String command)
    {
        return false;
    }

    @Override
    public boolean onServerCommand(Object listener, String username, String command)
    {
        return false;
    }

    @Override
    public String onServerCommandSay(Object listener, String username, String message)
    {
        return message;
    }

    @Override
    public String onClientChatRecv(String message)
    {
        if(message.startsWith(IC2NuclearControl.MSG_PREFIX))
        {
            String[] chunks = message.substring(IC2NuclearControl.MSG_PREFIX.length()).split(":");
            message = StatCollector.translateToLocal("msg.nc."+chunks[0]);
            if(chunks.length > 1)
            {
                List<String> list = new ArrayList<String>(Arrays.asList(chunks));
                list.remove(0);
                chunks = list.toArray(chunks);
                message = String.format(message, (Object[])chunks);
            }
            
        }
        return message;
    }

}
