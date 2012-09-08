package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIC2Thermo extends GuiContainer
{
    private TileEntityIC2Thermo thermo;
    private GuiRemoteThermoSlider slider;
    private String name;
    
    public GuiIC2Thermo(TileEntityIC2Thermo thermo)
    {
        super(new ContainerEmpty(thermo));
        xSize = 191;
        ySize = 64;
        this.thermo = thermo;
        name = StatCollector.translateToLocal("tile.blockThermalMonitor.name");
    }

    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        controlList.clear();
        guiLeft = (this.width - xSize) / 2;
        guiTop = (this.height - ySize) / 2;
        slider = new GuiRemoteThermoSlider(3, guiLeft+5, guiTop + 33, 
                StatCollector.translateToLocal("msg.nc.ThermalMonitorSignalAt"), 
                thermo);
        controlList.add(slider);
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if((which == 0 || which == 1) && slider.dragging )
        {
            slider.mouseReleased(mouseX, mouseY);
        }
        else
        {
            slider.checkMouseWheel(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIThermalMonitor.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
}
