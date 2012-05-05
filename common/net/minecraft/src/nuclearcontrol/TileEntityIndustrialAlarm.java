package net.minecraft.src.nuclearcontrol;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm
{

    @Override
    protected void checkStatus()
    {
        int light = lightLevel;
        super.checkStatus();
        if(lightLevel!=light)
            worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
    }
    
}
