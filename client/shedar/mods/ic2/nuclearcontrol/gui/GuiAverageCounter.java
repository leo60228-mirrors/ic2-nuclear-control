package shedar.mods.ic2.nuclearcontrol.gui;

import ic2.api.network.NetworkHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import shedar.mods.ic2.nuclearcontrol.containers.ContainerAverageCounter;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAverageCounter extends GuiContainer
{
    private String name;
    private ContainerAverageCounter container;

    public GuiAverageCounter(Container container)
    {
        super(container);
        this.container = (ContainerAverageCounter)container; 
        name = StatCollector.translateToLocal("tile.blockAverageCounter.name");
    }
    
    @SuppressWarnings("unchecked")
    private void initControls()
    {
        controlList.clear();
        //controlList.add(new GuiButton(0, guiLeft+35, guiTop+42, 127, 20, StatCollector.translateToLocal("msg.nc.Reset")));
        controlList.add(new GuiButton(1, guiLeft+35, guiTop+42, 30, 20, StatCollector.translateToLocal("1")));
        controlList.add(new GuiButton(2, guiLeft+35+30, guiTop+42, 30, 20, StatCollector.translateToLocal("3")));
        controlList.add(new GuiButton(3, guiLeft+35+60, guiTop+42, 30, 20, StatCollector.translateToLocal("5")));
        controlList.add(new GuiButton(4, guiLeft+35+90, guiTop+42, 30, 20, StatCollector.translateToLocal("10")));
    }
    
    @Override
    public void initGui() 
    {
        super.initGui();
        initControls();
    };

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        String value = StringUtils.getFormatted("msg.nc.InfoPanelOutput", container.averageCounter.getClientAverage(), true);
        fontRenderer.drawString(value, (xSize - fontRenderer.getStringWidth(value)) / 2, 22, 0x404040);
        value = StringUtils.getFormatted("msg.nc.AverageCounterPeriod", container.averageCounter.period, true);
        fontRenderer.drawString(value, (xSize - fontRenderer.getStringWidth(value)) / 2, 32, 0x404040);
        
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIEnergyCounter.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        initControls();
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        int event = 0;
        switch(guiButton.id)
        {
        case 1:
            event = 1;break;
        case 2:
            event = 3;break;
        case 3:
            event = 5;break;
        case 4:
            event = 10;break;
        }
        NetworkHelper.initiateClientTileEntityEvent(container.averageCounter, event);
    }
}
