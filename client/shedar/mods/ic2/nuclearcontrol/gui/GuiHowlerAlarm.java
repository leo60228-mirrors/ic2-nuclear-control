package shedar.mods.ic2.nuclearcontrol.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import shedar.mods.ic2.nuclearcontrol.IC2NuclearControl;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerEmpty;
import shedar.mods.ic2.nuclearcontrol.gui.controls.GuiHowlerAlarmListBox;
import shedar.mods.ic2.nuclearcontrol.gui.controls.GuiHowlerAlarmSlider;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityHowlerAlarm;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHowlerAlarm extends GuiContainer
{
    private TileEntityHowlerAlarm alarm;
    private GuiHowlerAlarmSlider slider;
    private GuiHowlerAlarmListBox listBox;
    private String name;

    public GuiHowlerAlarm(TileEntityHowlerAlarm alarm)
    {
        super(new ContainerEmpty(alarm));
        this.alarm = alarm;
        xSize = 131;
        ySize = 136;
        name = StatCollector.translateToLocal("tile.blockHowlerAlarm.name");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        buttonList.clear();
        slider = new GuiHowlerAlarmSlider(3, guiLeft+12, guiTop + 33, 
                StatCollector.translateToLocal("msg.nc.HowlerAlarmSoundRange"), alarm);
        List<String> items;
        if(alarm.worldObj.isRemote)
        {
            items =  new ArrayList<String>(IC2NuclearControl.instance.availableAlarms);
            items.retainAll(IC2NuclearControl.instance.serverAllowedAlarms);
        }
        else
        {
            items = IC2NuclearControl.instance.availableAlarms;
        }
        listBox = new GuiHowlerAlarmListBox(4, guiLeft+13, guiTop+63, 105, 65, items, alarm);
        buttonList.add(slider);
        buttonList.add(listBox);
        
    };
    
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("msg.nc.HowlerAlarmSound"), 12, 53, 0x404040);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if((which == 0 || which == 1) && (slider.dragging || listBox.dragging))
        {
            slider.mouseReleased(mouseX, mouseY);
            listBox.mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.func_98187_b("/mods/nuclearControl/textures/gui/GUIHowlerAlarm.png");
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }

}
